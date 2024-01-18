package com.datdeveloper.playerrecorder.store;

import com.datdeveloper.playerrecorder.PlayerRecorder;
import com.datdeveloper.playerrecorder.TrackedEvents;
import net.minecraft.entity.player.PlayerEntity;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;

public class FileEventStore implements IEventStore {
    final Path filePath;
    final BufferedWriter writer;

    public FileEventStore(Path fileDirectory) throws IOException {
        this.filePath = fileDirectory.resolve(Instant.now().toString() + ".datTrack");
        writer = new BufferedWriter(new FileWriter(filePath.toFile(), StandardCharsets.UTF_8));
    }

    @Override
    public void appendEvent(Instant timestamp, UUID player, TrackedEvents event, String data) {
        try {
            writer.write(timestamp.toString() + " " + player + " " + event.getEventName() + " " + data + "\n");
        } catch (IOException e) {
            PlayerRecorder.LOGGER.error("Failed to write to eventStore: {}", filePath.toString());
        }
    }

    @Override
    public void flushStore() {
        try {
            writer.flush();
        } catch (IOException e) {
            PlayerRecorder.LOGGER.error("Failed to flush file eventStore: {}", filePath.toString());
        }
    }

    @Override
    public void cleanup() {
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            PlayerRecorder.LOGGER.error("Failed to close file eventStore: {}", filePath.toString());
        }
    }
}
