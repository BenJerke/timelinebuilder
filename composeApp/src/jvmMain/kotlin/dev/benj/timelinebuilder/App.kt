package dev.benj.timelinebuilder

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import dev.benj.timelinebuilder.tlevent.TLEventRepository
import dev.benj.timelinebuilder.db.JvmFileDatabaseDriverFactory
import dev.benj.timelinebuilder.ui.theme.TimelineBuilderTheme
import dev.benj.timelinebuilder.ui.TimelineDisplay
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val db = TLEventRepository(JvmFileDatabaseDriverFactory())
    var events by remember { mutableStateOf(db.getAllEvents().toMutableList()) }

    TimelineBuilderTheme {
        TimelineDisplay(
            events = events,
            availableTags = emptyList(), // TODO: Load from database
            availableEntities = emptyList(), // TODO: Load from database
            initialStartDate = LocalDate.parse("2024-01-01"),
            initialEndDate = LocalDate.parse("2026-12-31"),
            onEventSave = { event ->
                if (event.id == 0L) {
                    // New event
                    db.addEvent(event)
                    events.add(event)
                } else {
                    // Update existing event (TODO: implement update in repository)
                    println("Update event: ${event.title}")
                }
            },
            onEventDelete = { event ->
                db.deleteEvent(event.id)
            },
            onCreateRelatedEvent = { title ->
                // TODO: Implement related event creation
                println("Create related event: $title")
            },
            onCreateRelatedEntity = { name ->
                // TODO: Implement related entity creation
                println("Create related entity: $name")
            },
            modifier = Modifier
        )
    }
}