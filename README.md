A fabric mod to track simple information about players on a server for the purpose of use as data.

# Event Reference
Every event consists of 4 components:
```
<timestamp> <player> <event> [command data]
```
Where 
- `timestamp` is a UTC time stamp in ISO 8601 format (For example: 2024-01-17T22:06:47Z)
- `player` the uuid of the player that performed the event
- `event` is the name of the event that occurred, and is one of the event listed below in [Events](#events)
- `command data` is an optional data component dependent on the command

## Events
The event defines what happened at that timestamp, it is often paired with some data that describes the event. 
Events names are always lowercase and never have spaces.
The tracked events are defined in the table below.

| Event                   | Description                             | Data        |
|-------------------------|-----------------------------------------|-------------| 
| [join](#join)           | The player joins the game               | N/A         |
| [leave](#leave)         | The player leaves the game              | N/A         |
| [dimension](#dimension) | The player changes world                | [New World] |
| [position](#position)   | The player's position at that timestamp | [Position]  |
| [death](#death)         | The player dies                         | N/A         |
| [respawn](#respawn)     | The player respawns                     | N/A         |

### join
The event that fires when the player joins the game.

This event does not have any data.

This event should be immediately followed by a dimension event, denoting the dimension they spawn into, then a position 
event, denoting the position they spawn in at.

### leave
The event that files when the player leaves the game.

This event does not have any data.

This event should be immediately followed by a position event, denoting their last position before they disconnected.

### dimension
The event representing the dimension the player is in. For example the nether or the end. This event is usually fired
to indicate the player is changing dimension, for example after having travelled through a nether portal. However, this
event can also be fired alongside other events to indicate which dimension is in for that event.

This event takes the resource name of the world they join as a string, for example, when a player goes to the 
overworld, the data is: "minecraft:overworld".

This event should be immediately followed by a position event, denoting their position in the new world. Note when this 
event is fired for any reason other than a change dimension event, that event will likely already call for a position
event following the change dimension event.

### position
The event representing the player's position at a given time. As this is not a specific event, this event will usually
occur at set intervals. Or will be paired with another event to denote the player's position during that event.

This event takes the position as a 3 component integer vector, in the format: `x<x coord>y<ycoord>z<z coord>`. For
example, for a player at 232, 64, 100, their position will be stored as `x232y64z100`.

In the case where the position event is paired with another, both events will have the same timestamp.

### death
The event that fires when a player dies.

This event does not have any data.

This event should be immediately followed by a position event, denoting the player's position when they died.

### respawn
The event that fires when a player respawns.

This event does not have any data.

This event should be immediately followed by a dimension event, denoting the dimension they spawn into, then a position 
event, denoting the player's respawn position.