package com.datdeveloper.playerrecorder.store;

import com.datdeveloper.playerrecorder.TrackedEvents;

import java.time.Instant;
import java.util.UUID;

/**
 * An interface providing methods to store the events
 */
public interface IEventStore {
    /**
     * Append an event to the store.
     * <p>
     * The event will have a timestamp of Instant.now()
     * @param player The uuid of the player the event is for
     * @param event The event that is being stored
     * @param data The data for the event
     */
    default void appendEvent(final UUID player, final TrackedEvents event, final String data) {
        appendEvent(Instant.now(), player, event, data);
    }

    /**
     * Append an event to the store.
     *
     * @param timestamp The timestamp of the event
     * @param player The uuid of the player the event is for
     * @param event The event that is being stored
     * @param data The data for the event
     */
    void appendEvent(final Instant timestamp, final UUID player, final TrackedEvents event, final String data);

    /**
     * A convenience event to quickly add an event paired with a position event
     * @param player The uuid of the player the event is for
     * @param event The event that is being stored
     * @param data The data for the event
     * @param position The position data
     */
    default void appendEventWithPosition(final UUID player, final TrackedEvents event, final String data, String position) {
        Instant now = Instant.now();
        appendEvent(now, player, event, data);
        appendEvent(now, player, TrackedEvents.POSITION, position);
    }

    /**
     * A convenience event to quickly add an event paired with a position event and a dimension event
     * @param player The uuid of the player the event is for
     * @param event The event that is being stored
     * @param data The data for the event
     * @param position The position data
     * @param dimension The dimension data
     */
    default void appendEventWithPosDim(final UUID player, final TrackedEvents event, final String data, String position, String dimension) {
        Instant now = Instant.now();
        appendEvent(now, player, event, data);
        appendEvent(now, player, TrackedEvents.DIMENSION, dimension);
        appendEvent(now, player, TrackedEvents.POSITION, position);
    }

    /**
     * An opportunity to perform an infrequent sync operation
     */
    void flushStore();

    /**
     * Cleanup the event store.
     */
    void cleanup();
}
