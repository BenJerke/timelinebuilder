package dev.benj.timelinebuilder.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.LocalDate
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlinx.datetime.minus
import dev.benj.timelinebuilder.tlevent.TLEvent
import dev.benj.timelinebuilder.tlevent.TLEntity
import dev.benj.timelinebuilder.tlevent.TLEventViewModel
import dev.benj.timelinebuilder.tlevent.TLTag
import co.touchlab.kermit.Logger

/**
 * A composable that displays a timeline of events within a given date range - the main view of the app.
 * State:
 * - List of events that started within the given range
 * - Current zoom level (e.g., days, weeks, months)
 * - Current scroll position (if applicable)
 * - Selected event (if any)
 * - Display range (start and end dates)
 *   - this will change based on zoom level, scroll position, and manual date input.
 *   - when the display range changes, the list of events should be updated accordingly.
 *
 * Actions:
 * - Zoom in/out
 *   - Changes the display range by an interval of seconds, minutes, hours, days, weeks, months
 *
 * - Scroll left/right
 *   - Changes the display range by a fixed interval, depending on the current zoom level.
 *
 * - Select event
 *   - Opens the event in a detail view sidebar.
 *   - Events can be viewed and edited here
 *
 * - Deselect event
 *   - Closes the event detail view sidebar.
 *
 * - Add event
 *  - Opens a form for entering a new event in the detail view sidebar.
 *  - User will not be able to save the event until all required fields are filled out.
 *
 *
 * Controls:
 *  - Buttons to zoom in/out
 *  - Mousewheel or click/drag to scroll left/right
 *  - Click on event card to select it and open detail view
 *  - Date range pickers to manually set the display range
 *  - Button to add new event
 *  - Button to display links between events as lines
 *  - Button to toggle display of related entities and tags by highlighting event cards
 *  - Button to filter events by tag or entity
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TimelineDisplay(
    viewModel: TLEventViewModel,
    availableTags: List<TLTag> = emptyList(),
    availableEntities: List<TLEntity> = emptyList(),
    initialStartDate: LocalDate = LocalDate.parse("2020-01-01"),
    initialEndDate: LocalDate = LocalDate.parse("2025-12-31"),
    modifier: Modifier = Modifier
) {

    // Timeline state
    val events = viewModel.eventList.collectAsStateWithLifecycle()

    var displayRangeStart by remember { mutableStateOf(initialStartDate) }
    var displayRangeEnd by remember { mutableStateOf(initialEndDate) }
    var selectedEvent by remember { mutableStateOf<TLEvent?>(null) }
    var isDetailViewVisible by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var isCreating by remember { mutableStateOf(false) }

    // Display options
    var showConnections by remember { mutableStateOf(false) }
    var showRelatedHighlights by remember { mutableStateOf(false) }
    var selectedTagFilter by remember { mutableStateOf<TLTag?>(null) }
    var selectedEntityFilter by remember { mutableStateOf<TLEntity?>(null) }

    // Zoom levels (in days)
    val zoomLevels = listOf(1, 7, 30, 90, 365, 1825) // 1 day, 1 week, 1 month, 3 months, 1 year, 5 years
    var currentZoomIndex by remember { mutableStateOf(4) } // Start at 1 year

    val onEventSave = viewModel::addEvent
    val onEventDelete = viewModel::deleteEvent


    // Filter events based on date range and filters
    val filteredEvents = remember(events, displayRangeStart, displayRangeEnd, selectedTagFilter, selectedEntityFilter) {
        events.value.filter { event ->
            val eventDate = event.startDateTime.date
            val inDateRange = eventDate in displayRangeStart..displayRangeEnd
            val matchesTagFilter = selectedTagFilter == null || event.tags.contains(selectedTagFilter)
            val matchesEntityFilter = selectedEntityFilter == null || event.relatedEntities.contains(selectedEntityFilter)
            inDateRange && matchesTagFilter && matchesEntityFilter
        }.sortedBy { it.startDateTime }
    }

    // Functions for timeline navigation
    fun changeZoom(upOrDown: Int) {
        if ((upOrDown == -1 && currentZoomIndex > 0) || (upOrDown == 1 && currentZoomIndex < zoomLevels.size - 1)) {
            currentZoomIndex += upOrDown
            val newRangeDays = zoomLevels[currentZoomIndex]
            val currentRangeDays = (displayRangeEnd.toEpochDays() - displayRangeStart.toEpochDays()).toInt()
            val centerPoint = displayRangeStart.plus(currentRangeDays / 2, DateTimeUnit.DAY)
            displayRangeStart = centerPoint.minus(newRangeDays / 2, DateTimeUnit.DAY)
            displayRangeEnd = centerPoint.plus(newRangeDays / 2, DateTimeUnit.DAY)
        }
    }


    fun scrollLeft() {
        val scrollAmount = (zoomLevels[currentZoomIndex] / 4).coerceAtLeast(1)
        displayRangeStart = displayRangeStart.minus(scrollAmount, DateTimeUnit.DAY)
        displayRangeEnd = displayRangeEnd.minus(scrollAmount, DateTimeUnit.DAY)
    }

    fun scrollRight() {
        val scrollAmount = (zoomLevels[currentZoomIndex] / 4).coerceAtLeast(1)
        displayRangeStart = displayRangeStart.plus(scrollAmount, DateTimeUnit.DAY)
        displayRangeEnd = displayRangeEnd.plus(scrollAmount, DateTimeUnit.DAY)
    }

    fun expandRangeToIncludeEvent(eventStartDate: LocalDate, eventEndDate: LocalDate) {
        if (eventStartDate < displayRangeStart) {
            displayRangeStart = eventStartDate.minus(30, DateTimeUnit.DAY)
        }
        if (eventEndDate > displayRangeEnd) {
            displayRangeEnd = eventEndDate.plus(30, DateTimeUnit.DAY)
        }
    }

    Row(modifier = modifier.fillMaxSize()) {
        // Main timeline view
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Top toolbar
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Navigation and zoom controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { scrollLeft() }) {
                                Text("◀")
                            }
                            Button(onClick = { scrollRight() }) {
                                Text("▶")
                            }
                            Button(onClick = { changeZoom(-1) }, enabled = currentZoomIndex > 0) {
                                Text("⊕")
                            }
                            Button(onClick = { changeZoom(1) }, enabled = currentZoomIndex < zoomLevels.size - 1) {
                                Text("⊖")
                            }
                        }

                        Button(
                            onClick = {
                                selectedEvent = null
                                isEditing = false
                                isCreating = true
                                isDetailViewVisible = true
                            }
                        ) {
                            Text("+ Add Event")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Display options and filters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            onClick = { showConnections = !showConnections },
                            label = { Text("Connections") },
                            selected = showConnections
                        )

                        FilterChip(
                            onClick = { showRelatedHighlights = !showRelatedHighlights },
                            label = { Text("Highlights") },
                            selected = showRelatedHighlights
                        )

                        // Tag filter dropdown (simplified)
                        if (availableTags.isNotEmpty()) {
                            FilterChip(
                                onClick = {
                                    selectedTagFilter = if (selectedTagFilter == null) availableTags.firstOrNull() else null
                                },
                                label = { Text(selectedTagFilter?.name ?: "Tags") },
                                selected = selectedTagFilter != null
                            )
                        }

                        // Entity filter dropdown (simplified)
                        if (availableEntities.isNotEmpty()) {
                            FilterChip(
                                onClick = {
                                    selectedEntityFilter = if (selectedEntityFilter == null) availableEntities.firstOrNull() else null
                                },
                                label = { Text(selectedEntityFilter?.name ?: "Entities") },
                                selected = selectedEntityFilter != null
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Date range display
                    Text(
                        text = "Showing: $displayRangeStart to $displayRangeEnd (${filteredEvents.size} events)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Timeline content area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            // Simple horizontal scrolling with drag
                            if (dragAmount.x > 10) {
                                scrollLeft()
                            } else if (dragAmount.x < -10) {
                                scrollRight()
                            }
                        }
                    }
            ) {
                if (filteredEvents.isEmpty()) {
                    // Empty state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No events in this time range",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                selectedEvent = null
                                isEditing = false
                                isCreating = true
                                isDetailViewVisible = true
                            }
                        ) {
                            Text("Create Your First Event")
                        }
                    }
                } else {
                    // Events list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredEvents) { event ->
                            val isHighlighted = showRelatedHighlights && selectedEvent?.let { selected ->
                                selected.relatedEvents.contains(event) ||
                                event.relatedEvents.contains(selected) ||
                                selected.relatedEntities.any { entity -> event.relatedEntities.contains(entity) } ||
                                selected.tags.any { tag -> event.tags.contains(tag) }
                            } ?: false

                            EventCard(
                                event = event,
                                isSelected = selectedEvent == event,
                                isHighlighted = isHighlighted,
                                showConnections = showConnections,
                                onEventClick = { clickedEvent ->
                                    selectedEvent = clickedEvent
                                    isEditing = false
                                    isCreating = false
                                    isDetailViewVisible = true
                                },
                                onEventHover = { _, _ ->
                                    // Could implement hover effects here
                                }
                            )
                        }
                    }
                }
            }
        }

        // Event detail sidebar
        if (isDetailViewVisible) {
            EventDetailView(
                event = selectedEvent,
                isVisible = isDetailViewVisible,
                isEditing = isEditing,
                isCreating = isCreating,
                availableTags = availableTags,
                availableEvents = events.value,
                availableEntities = availableEntities,
                onClose = {
                    isDetailViewVisible = false
                    selectedEvent = null
                    isEditing = false
                    isCreating = false
                },
                onSave = { savedEvent ->
                    onEventSave(savedEvent)
                    isDetailViewVisible = false
                    selectedEvent = null
                    isEditing = false
                    isCreating = false
                    Logger.d("TimelineDisplay - saving event - $savedEvent" )
                },
                onDelete = { eventToDelete ->
                    onEventDelete(eventToDelete.id)
                    isDetailViewVisible = false
                    selectedEvent = null
                    isEditing = false
                    isCreating = false
                    Logger.d("TimelineDisplay - deleting event - $eventToDelete" )
                },
                onCancel = {
                    Logger.d("TimelineDisplay - cancelling edit/create" )
                    isEditing = false
                    if (isCreating) {
                        isDetailViewVisible = false
                        selectedEvent = null
                        isCreating = false
                    }
                },
                onEdit = {
                    isEditing = true
                    isCreating = false
                },
                onEventClick = { clickedRelatedEvent ->
                    selectedEvent = clickedRelatedEvent
                    isEditing = false
                    isCreating = false
                },
                onExpandTimelineRange = { eventStart, eventEnd ->
                    expandRangeToIncludeEvent(eventStart.date, eventEnd.date)
                },
                // TODO: Implement these callbacks (or get rid of them... tbd)
                onCreateRelatedEvent = {},
                onCreateRelatedEntity = {}
            )
        }
    }
}

//internal class TLDisplayState {
//    var displayRangeStart by mutableStateOf(LocalDate.parse("2020-01-01"))
//    var displayRangeEnd by mutableStateOf(LocalDate.parse("2025-12-31"))
//    var selectedEvent by mutableStateOf<TLEvent?>(null)
//    var isDetailViewVisible by mutableStateOf(false)
//    var isEditing by mutableStateOf(false)
//    var isCreating by mutableStateOf(false)
//
//    // Display options
//    var showConnections by mutableStateOf(false)
//    var showRelatedHighlights by mutableStateOf(false)
//    var selectedTagFilter by mutableStateOf<TLTag?>(null)
//    var selectedEntityFilter by mutableStateOf<TLEntity?>(null)
//
//    // Zoom levels (in days)
//    val zoomLevels = listOf(1, 7, 30, 90, 365, 1825) // 1 day, 1 week, 1 month, 3 months, 1 year, 5 years
//    var currentZoomIndex by mutableStateOf(4) // Start at 1 year
//}