package dev.benj.timelinebuilder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import dev.benj.timelinebuilder.tlevent.TLEvent
import dev.benj.timelinebuilder.tlevent.TLEntity
import dev.benj.timelinebuilder.tlevent.TLTag

/**
 * A sidebar UI component to display detailed information about a selected timeline event.
 * State:
 * - TLEvent data
 * - Editable event fields (title, description, start/end dates, related events/entities, tags)
 * - Edit mode (viewing vs editing)
 * - Validation errors for required fields
 * - Show/Hide
 *
 * Actions:
 * - Edit event details
 * - Save changes to the event
 * - Cancel edits and revert to original event data
 * - Close the sidebar
 * - Link/unlink related events and entities
 * - Add/remove tags
 * - Show linked events on TimelineDisplay
 * - Delete event
 * - Create new event
 *
 * Controls:
 * IF EDITING OR CREATING:
 * - Text fields for title, description
 * - Date pickers for start/end dates
 * ALL THE TIME:
 * - Buttons to save, cancel, close, delete
 * - Text field to enter tags
 *   - Autocomplete suggestions for existing tags
 *   - Saves new tags if they do not exist
 * - Text field to link events by title
 *  - Autocomplete suggestions for existing events
 *  - If the event to link does not exist, show a button to create it
 *
 * - Text field to link entities by name
 *  - Autocomplete suggestions for existing entities
 *  - If the entity to link does not exist, show a button to create it
 *
 * - List of related events
 *  - If a related event is clicked, the detail view loads that event.
 *  - If the related event is not present in the timeline display's date range, show a button that will allow the user to expand the timeline's date range window to include that event.
 *
 */
@Composable
internal fun EventDetailView(
    event: TLEvent?,
    isVisible: Boolean = false,
    isEditing: Boolean = false,
    isCreating: Boolean = false,
    availableTags: List<TLTag> = emptyList(),
    availableEvents: List<TLEvent> = emptyList(),
    availableEntities: List<TLEntity> = emptyList(),
    onClose: () -> Unit = {},
    onSave: (TLEvent) -> Unit = {},
    onDelete: (TLEvent) -> Unit = {},
    onCancel: () -> Unit = {},
    onEdit: () -> Unit = {},
    onEventClick: (TLEvent) -> Unit = {},
    onExpandTimelineRange: (LocalDateTime, LocalDateTime) -> Unit = { _, _ -> },
    onCreateRelatedEvent: (String) -> Unit = {},
    onCreateRelatedEntity: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // State for editing
    var editableTitle by remember { mutableStateOf("") }
    var editableDescription by remember { mutableStateOf("") }
    var editableStartDateTime by remember { mutableStateOf("") }
    var editableEndDateTime by remember { mutableStateOf("") }
    var editableTags by remember { mutableStateOf(listOf<TLTag>()) }
    var editableRelatedEvents by remember { mutableStateOf(listOf<TLEvent>()) }
    var editableRelatedEntities by remember { mutableStateOf(listOf<TLEntity>()) }

    // Input fields state
    var tagInput by remember { mutableStateOf("") }
    var eventInput by remember { mutableStateOf("") }
    var entityInput by remember { mutableStateOf("") }

    // Validation errors
    var titleError by remember { mutableStateOf("") }

    // Initialize editable fields when event changes
    LaunchedEffect(event) {
        event?.let {
            editableTitle = it.title
            editableDescription = it.description
            editableStartDateTime = it.startDateTime.toString()
            editableEndDateTime = it.endDateTime.toString()
            editableTags = it.tags
            editableRelatedEvents = it.relatedEvents
            editableRelatedEntities = it.relatedEntities
        } ?: run {
            // Reset for new event creation
            editableTitle = ""
            editableDescription = ""
            editableStartDateTime = ""
            editableEndDateTime = ""
            editableTags = emptyList()
            editableRelatedEvents = emptyList()
            editableRelatedEntities = emptyList()
        }
        titleError = ""
    }

    if (isVisible) {
        Card(
            modifier = modifier
                .fillMaxHeight()
                .width(400.dp)
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with title and controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when {
                            isCreating -> "Create Event"
                            isEditing -> "Edit Event"
                            else -> "Event Details"
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onClose) {
                        Text("✕") // Using text symbol instead of icon for now
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title field
                OutlinedTextField(
                    value = if (isEditing || isCreating) editableTitle else (event?.title ?: ""),
                    onValueChange = {
                        editableTitle = it
                        titleError = if (it.isBlank()) "Title is required" else ""
                    },
                    label = { Text("Title") },
                    readOnly = !isEditing && !isCreating,
                    isError = titleError.isNotEmpty(),
                    supportingText = if (titleError.isNotEmpty()) { { Text(titleError) } } else null,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description field
                OutlinedTextField(
                    value = if (isEditing || isCreating) editableDescription else (event?.description ?: ""),
                    onValueChange = { editableDescription = it },
                    label = { Text("Description") },
                    readOnly = !isEditing && !isCreating,
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Date fields (simplified for now - would need proper date pickers)
                if (isEditing || isCreating) {
                    OutlinedTextField(
                        value = editableStartDateTime,
                        onValueChange = { editableStartDateTime = it },
                        label = { Text("Start Date/Time") },
                        placeholder = { Text("YYYY-MM-DDTHH:MM:SS") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editableEndDateTime,
                        onValueChange = { editableEndDateTime = it },
                        label = { Text("End Date/Time") },
                        placeholder = { Text("YYYY-MM-DDTHH:MM:SS") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                } else {
                    // Display dates when not editing
                    Text(
                        text = "Start: ${event?.startDateTime ?: ""}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "End: ${event?.endDateTime ?: ""}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tags section
                Text(
                    text = "Tags",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tag input field with autocomplete
                OutlinedTextField(
                    value = tagInput,
                    onValueChange = { tagInput = it },
                    label = { Text("Add tags") },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (tagInput.isNotBlank()) {
                                    val existingTag = availableTags.find { it.name.equals(tagInput, ignoreCase = true) }
                                    val newTag = existingTag ?: TLTag(0, tagInput, "")
                                    if (!editableTags.contains(newTag)) {
                                        editableTags = editableTags + newTag
                                    }
                                    tagInput = ""
                                }
                            }
                        ) {
                            Text("+")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (tagInput.isNotBlank()) {
                                val existingTag = availableTags.find { it.name.equals(tagInput, ignoreCase = true) }
                                val newTag = existingTag ?: TLTag(0, tagInput, "")
                                if (!editableTags.contains(newTag)) {
                                    editableTags = editableTags + newTag
                                }
                                tagInput = ""
                            }
                        }
                    )
                )

                // Current tags display
                if (editableTags.isNotEmpty() || (!isEditing && !isCreating && event?.tags?.isNotEmpty() == true)) {
                    Spacer(modifier = Modifier.height(8.dp))

                    val tagsToShow = if (isEditing || isCreating) editableTags else (event?.tags ?: emptyList())
                    tagsToShow.forEach { tag ->
                        AssistChip(
                            onClick = {
                                if (isEditing || isCreating) {
                                    editableTags = editableTags - tag
                                }
                            },
                            label = { Text(tag.name) },
                            trailingIcon = if (isEditing || isCreating) {
                                { Text("×") }
                            } else null,
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Related Events section
                Text(
                    text = "Related Events",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Event linking field
                OutlinedTextField(
                    value = eventInput,
                    onValueChange = { eventInput = it },
                    label = { Text("Link event by title") },
                    trailingIcon = {
                        Row {
                            val matchingEvent = availableEvents.find { it.title.equals(eventInput, ignoreCase = true) }
                            if (matchingEvent != null && !editableRelatedEvents.contains(matchingEvent)) {
                                IconButton(
                                    onClick = {
                                        editableRelatedEvents = editableRelatedEvents + matchingEvent
                                        eventInput = ""
                                    }
                                ) {
                                    Text("+")
                                }
                            } else if (eventInput.isNotBlank() && matchingEvent == null) {
                                IconButton(
                                    onClick = {
                                        onCreateRelatedEvent(eventInput)
                                        eventInput = ""
                                    }
                                ) {
                                    Text("⊕")
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Related events list
                val relatedEventsToShow = if (isEditing || isCreating) editableRelatedEvents else (event?.relatedEvents ?: emptyList())
                relatedEventsToShow.forEach { relatedEvent ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        onClick = { onEventClick(relatedEvent) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = relatedEvent.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${relatedEvent.startDateTime}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row {
                                // Button to expand timeline range if needed
                                IconButton(
                                    onClick = {
                                        onExpandTimelineRange(relatedEvent.startDateTime, relatedEvent.endDateTime)
                                    }
                                ) {
                                    Text("👁")
                                }

                                if (isEditing || isCreating) {
                                    IconButton(
                                        onClick = {
                                            editableRelatedEvents = editableRelatedEvents - relatedEvent
                                        }
                                    ) {
                                        Text("×")
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Related Entities section
                Text(
                    text = "Related Entities",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Entity linking field
                OutlinedTextField(
                    value = entityInput,
                    onValueChange = { entityInput = it },
                    label = { Text("Link entity by name") },
                    trailingIcon = {
                        Row {
                            val matchingEntity = availableEntities.find { it.name.equals(entityInput, ignoreCase = true) }
                            if (matchingEntity != null && !editableRelatedEntities.contains(matchingEntity)) {
                                IconButton(
                                    onClick = {
                                        editableRelatedEntities = editableRelatedEntities + matchingEntity
                                        entityInput = ""
                                    }
                                ) {
                                    Text("+")
                                }
                            } else if (entityInput.isNotBlank() && matchingEntity == null) {
                                IconButton(
                                    onClick = {
                                        onCreateRelatedEntity(entityInput)
                                        entityInput = ""
                                    }
                                ) {
                                    Text("⊕")
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Related entities list
                val relatedEntitiesToShow = if (isEditing || isCreating) editableRelatedEntities else (event?.relatedEntities ?: emptyList())
                relatedEntitiesToShow.forEach { entity ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = entity.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                if (entity.description.isNotBlank()) {
                                    Text(
                                        text = entity.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (isEditing || isCreating) {
                                IconButton(
                                    onClick = {
                                        editableRelatedEntities = editableRelatedEntities - entity
                                    }
                                ) {
                                    Text("×")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when {
                        isEditing || isCreating -> {
                            Button(
                                onClick = {
                                    // Validate and save
                                    var hasErrors = false
                                    if (editableTitle.isBlank()) {
                                        titleError = "Title is required"
                                        hasErrors = true
                                    }

                                    if (!hasErrors) {
                                        // Create updated event (simplified date parsing)
                                        val updatedEvent = event?.copy(
                                            title = editableTitle,
                                            description = editableDescription,
                                            tags = editableTags,
                                            relatedEvents = editableRelatedEvents,
                                            relatedEntities = editableRelatedEntities
                                        ) ?: TLEvent(
                                            id = 0,
                                            title = editableTitle,
                                            description = editableDescription,
                                            startDateTime = LocalDateTime.parse(editableStartDateTime.ifBlank { "2024-01-01T00:00:00" }),
                                            endDateTime = LocalDateTime.parse(editableEndDateTime.ifBlank { editableStartDateTime.ifBlank { "2024-01-01T00:00:00" } }),
                                            tags = editableTags,
                                            relatedEvents = editableRelatedEvents,
                                            relatedEntities = editableRelatedEntities
                                        )
                                        onSave(updatedEvent)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Save")
                            }

                            OutlinedButton(
                                onClick = onCancel,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }
                        }
                        else -> {
                            Button(
                                onClick = onEdit,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Edit")
                            }

                            if (event != null) {
                                OutlinedButton(
                                    onClick = { onDelete(event) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}