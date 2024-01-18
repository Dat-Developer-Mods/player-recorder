package com.datdeveloper.playerrecorder;

/**
 * The events that are tracked
 */
public enum TrackedEvents {
    /** Player joins the game event */
    JOIN,
    /** Player leaves the game event */
    LEAVE,
    /** Player dimension event */
    DIMENSION,
    /** Player position event */
    POSITION,
    /** Player death event */
    DEATH,
    /** Player respawn event */
    RESPAWN;

    /**
     * Get the event name that is stored.
     * @return The event name
     */
    public String getEventName() {
        return this.name().toLowerCase();
    }
}
