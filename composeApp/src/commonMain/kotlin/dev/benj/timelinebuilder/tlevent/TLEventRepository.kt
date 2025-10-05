package dev.benj.timelinebuilder.tlevent

import dev.benj.timelinebuilder.db.DatabaseDriverFactory
import kotlinx.datetime.LocalDateTime
import timelinebuilder.tlevent.TLEventQueries
import timelinebuilder.tlevent.Tl_event

internal class TLEventRepository(driverFactory: DatabaseDriverFactory) {
    private val tlEventDb = TimelineEventDatabase(
        driverFactory.createDriver(),
    )
    private val eventQueries: TLEventQueries = tlEventDb.tLEventQueries

    internal fun getAllEvents(): List<TLEvent> {
        val unparsedEvents = eventQueries.selectAllEvents().executeAsList()
        return unparsedEvents.map { mapSingleEvent(it) }
    }

    internal fun deleteEvent(id: Long) {
        eventQueries.deleteEvent(id)
    }

    internal fun addEvent(event: TLEvent) {
        eventQueries.insertEvent(
            title = event.title,
            description = event.description,
            start_date_time = event.startDateTime.toString(),
            end_date_time = event.endDateTime.toString()
        )
    }

    internal fun linkEvents(id1: Long, id2: Long) {
        eventQueries.insertEventRelation(id1, id2)
    }

    internal fun unlinkEvents(id1: Long, id2: Long) {
        eventQueries.deleteEventRelation(id1, id2)
    }

    internal fun linkEntityToEvent(eventId: Long, entityId: Long) {
        eventQueries.insertEventEntity(eventId, entityId)
    }

    internal fun unlinkEntityFromEvent(eventId: Long, entityId: Long) {
        eventQueries.deleteEventEntity(eventId, entityId)
    }

    internal fun tagEvent(eventId: Long, tagId: Long) {
        eventQueries.insertEventTag(eventId, tagId)
    }

    internal fun untagEvent(eventId: Long, tagId: Long) {
        eventQueries.deleteEventTag(eventId, tagId)
    }



    private fun mapSingleEvent(data: Tl_event): TLEvent {
        return TLEvent(
            id = data.id,
            title = data.title,
            description = data.description,
            startDateTime = LocalDateTime.Companion.parse(data.start_date_time, LocalDateTime.Formats.ISO),
            endDateTime = LocalDateTime.Companion.parse(data.end_date_time, LocalDateTime.Formats.ISO)
        )
    }
}