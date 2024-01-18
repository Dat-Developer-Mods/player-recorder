A fabric mod to track simple information about players on a server for the purpose of use as data.

This mod takes player actions and stores them as a set of events that describe what they did and when they did it.
These events can then be processed later to recreate a simple representation of the player's actions.

The intention for this project was to produce some data that, with some processing, can be used in combination with a
map like dynmap to produce a timelapse of a speed run.

# The data format
More information about the data output can be found in [The Wiki](https://github.com/Dat-Developer-Mods/player-recorder/wiki/Event-Reference)

# Future work
To continue the project, the following features should be implemented:
- More event store types
  - Mysql
  - sqlite
  - JSON/YAML
  - CSV
- Config
  - Set the store type
  - Control events that are tracked
  - Control the interval for position tracking
- Track more events
  - Chat?
  - Kills?
  - Block Changes?

# Building
To build this project, run
```shell
./gradlew jar
```
and grab the built jar from
```
/build/libs
```