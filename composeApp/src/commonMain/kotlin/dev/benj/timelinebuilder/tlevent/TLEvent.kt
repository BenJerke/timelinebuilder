package dev.benj.timelinebuilder.tlevent
import kotlinx.datetime.LocalDateTime


/**
 * Something that took place on a specific day, or developed over a span of time.
 * Can be linked to other events and entities to show relationships.
 */
data class TLEvent(val id: Long, val title: String, val description: String, val startDateTime: LocalDateTime, val endDateTime: LocalDateTime = startDateTime, val relatedEvents: List<TLEvent> = emptyList(), val relatedEntities: List<TLEntity> = emptyList(), val tags: List<TLTag> = emptyList())


/**
 * A person, organization, location, or object involved in events.
 */
data class TLEntity(val id: Long, val name: String, val description: String)

/**
 * A label or category that can be assigned to events and entities for organization and filtering.
 */
data class TLTag(val id: Long, val name: String, val description: String)

internal fun TLEvent.withRelatedEvents(relatedEvents: List<TLEvent>): TLEvent {
    return this.copy(relatedEvents = relatedEvents)
}

internal fun TLEvent.withRelatedEntities(relatedEntities: List<TLEntity>): TLEvent {
    return this.copy(relatedEntities = relatedEntities)
}


