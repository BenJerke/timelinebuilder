package dev.benj.timelinebuilder.tlevent

import dev.benj.timelinebuilder.db.DatabaseDriverFactory
import kotlinx.datetime.LocalDateTime
import timelinebuilder.tlevent.TLEventQueries
import timelinebuilder.tlevent.Tl_event

interface TLEventRepository {
    fun getAllEvents(): List<TLEvent>
    fun addEvent(event: TLEvent): Unit
    fun editEvent(event: TLEvent): Unit
    fun deleteEvent(id: Long): Unit

    fun loadTestData(): Unit
}

internal class SqliteTLEventRepository(driverFactory: DatabaseDriverFactory): TLEventRepository {
    private val tlEventDb = TimelineEventDatabase(
        driverFactory.createDriver(),
    )
    private val eventQueries: TLEventQueries = tlEventDb.tLEventQueries

    override fun getAllEvents(): List<TLEvent> {
        val unparsedEvents = eventQueries.selectAllEvents().executeAsList()
        return unparsedEvents.map { mapSingleEvent(it) }
    }


    override fun deleteEvent(id: Long) {
        eventQueries.deleteEvent(id)
    }

    override fun addEvent(event: TLEvent) {

        eventQueries.insertEvent(
            title = event.title,
            description = event.description,
            start_date_time = event.startDateTime.toString(),
            end_date_time = event.endDateTime.toString()
        )
        // Get the ID of the newly inserted event
        val newEventId = eventQueries.selectMostRecentRowId().executeAsOne()

        // link related events if present
        if (event.relatedEvents.isNotEmpty()){
            event.relatedEvents.forEach { relatedEvent ->
                eventQueries.insertEventRelation(newEventId, relatedEvent.id)
            }
        }
        // link related entities if present
        if(event.relatedEntities.isNotEmpty()){
            event.relatedEntities.forEach { relatedEntity ->
                eventQueries.insertEventEntity(newEventId, relatedEntity.id)
            }
        }
        // link tags if present
        if(event.tags.isNotEmpty()){
            event.tags.forEach { tag ->
                eventQueries.insertEventTag(newEventId, tag.id)
            }
        }
    }

    override fun editEvent(event: TLEvent) {
        val eventPreUpdate = eventQueries.selectEventById(event.id).executeAsOneOrNull()

        if(eventPreUpdate != null){
            // Remove all existing relations and re-add them
            eventQueries.deleteAllEventRelations(event.id)
            event.relatedEvents.forEach { relatedEvent ->
                eventQueries.insertEventRelation(event.id, relatedEvent.id)
            }
            // Remove all existing entity links and re-add them
            eventQueries.deleteAllEventEntities(event.id)
            event.relatedEntities.forEach { relatedEntity ->
                eventQueries.insertEventEntity(event.id, relatedEntity.id)
            }
            // Remove all existing tags and re-add them
            eventQueries.deleteAllEventTags(event.id)
            event.tags.forEach { tag ->
                eventQueries.insertEventTag(event.id, tag.id)
            }
        }

        eventQueries.updateEvent(
            id = event.id,
            title = event.title,
            description = event.description,
            start_date_time = event.startDateTime.toString(),
            end_date_time = event.endDateTime.toString()
        )
    }

    override fun loadTestData(){
        eventQueries.insertTestEvents()
        eventQueries.insertTestTags()
        eventQueries.insertTestEntities()
        eventQueries.insertTestEventTags()
        eventQueries.insertTestEventEntities()
        eventQueries.insertTestEventRelations()
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
            startDateTime = LocalDateTime.parse(data.start_date_time, LocalDateTime.Formats.ISO),
            endDateTime = LocalDateTime.parse(data.end_date_time, LocalDateTime.Formats.ISO)
        )
    }

    // TODO: Implement functions to get related events, entities, and tags for a given event.

    // TODO: Implement functions to create, update, and delete tags and entities.

    // TODO: Implement incremental loading for objects with lots of links to other objects.

    // TODO: Implement incremental loading for timelines with lots of individual events, tags, and entities.

}

