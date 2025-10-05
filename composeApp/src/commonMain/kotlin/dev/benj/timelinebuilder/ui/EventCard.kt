package dev.benj.timelinebuilder.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.benj.timelinebuilder.tlevent.TLEvent



/**
 * A card UI component to display a summary of a timeline event on the TimelineDisplay.
 *
 *
 *   State:
 *   - TLEvent data
 *   - Summarized event data for display (title, date, brief description)
 *   - Tooltip visibility
 *   - Hovered
 *   - Selected
 *   - Show Linked Event Connections
 *   - Highlighted
 *   - Show/Hide
 *
 *   Actions:
 *   - Click to select the event and open the EventDetailView sidebar
 *   - Hover to show tooltip with more event details
 *   - Highlight when entities/tags are selected on TimelineDisplay
 *   - Show connections to related events when toggled on TimelineDisplay
 *
 *
 */
@Composable
internal fun EventCard(
    event: TLEvent,
    isSelected: Boolean = false,
    isHighlighted: Boolean = false,
    showConnections: Boolean = false,
    isVisible: Boolean = true,
    onEventClick: (TLEvent) -> Unit = {},
    onEventHover: (TLEvent, Boolean) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var isHovered by remember { mutableStateOf(false) }

    // Animation states
    val elevation by animateFloatAsState(
        targetValue = when {
            isSelected -> 8f
            isHovered -> 4f
            else -> 2f
        }
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primary
            isHighlighted -> MaterialTheme.colorScheme.secondary
            showConnections -> MaterialTheme.colorScheme.tertiary
            else -> Color.Transparent
        }
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primaryContainer
            isHighlighted -> MaterialTheme.colorScheme.secondaryContainer
            isHovered -> MaterialTheme.colorScheme.surfaceVariant
            else -> MaterialTheme.colorScheme.surface
        }
    )

    if (isVisible) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(4.dp)
                .shadow(elevation.dp, RoundedCornerShape(8.dp))
                .border(2.dp, borderColor, RoundedCornerShape(8.dp))
                .clickable {
                    onEventClick(event)
                },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                // Title
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Date range
                Text(
                    // TODO: Format these
                    text = "${event.startDateTime}${event.endDateTime.let { " - $it" }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Brief description
                if (event.description.isNotBlank()) {
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Tags and entities indicators
                if (event.tags.isNotEmpty() || event.relatedEntities.isNotEmpty() || event.relatedEvents.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Tags
                        event.tags.take(3).forEach { tag ->
                            AssistChip(
                                onClick = { },
                                label = {
                                    Text(
                                        text = tag.name,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                modifier = Modifier.height(24.dp)
                            )
                        }

                        // Show connection indicator if there are related items
                        if (showConnections && (event.relatedEvents.isNotEmpty() || event.relatedEntities.isNotEmpty())) {
                            Badge(
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Text("${event.relatedEvents.size + event.relatedEntities.size}")
                            }
                        }
                    }
                }
            }
        }

        // Handle hover events
        LaunchedEffect(isHovered) {
            onEventHover(event, isHovered)
        }
    }
}