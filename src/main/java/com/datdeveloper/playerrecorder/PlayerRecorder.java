package com.datdeveloper.playerrecorder;

import com.datdeveloper.playerrecorder.store.FileEventStore;
import com.datdeveloper.playerrecorder.store.IEventStore;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

public class PlayerRecorder implements ModInitializer {
    public static final String MOD_ID = "player-recorder";
    public static final Logger LOGGER = LogManager.getLogger("Player Recorder");
    IEventStore eventStore = null;

    /**
     * Initialise the mod
     */
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            final Path savePath = server.getSavePath(WorldSavePath.ROOT).resolve(MOD_ID);

            try {
                if (!Files.exists(savePath)) Files.createDirectories(savePath);
                eventStore = new FileEventStore(savePath);
            } catch (IOException e) {
                LOGGER.error("Failed to setup file eventStore: {}", savePath);
            }
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> eventStore.cleanup());

        // Join
        ServerPlayConnectionEvents.JOIN.register((this::onPlayerJoin));
        // Leave
        ServerPlayConnectionEvents.DISCONNECT.register(this::onPlayerDisconnect);
        // Respawn
        ServerLivingEntityEvents.AFTER_DEATH.register((this::afterDeath));
        // Death
        ServerPlayerEvents.AFTER_RESPAWN.register(this::afterRespawn);
        // Position
        ServerTickEvents.START_SERVER_TICK.register((this::onTick));
        // Change World
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((this::afterChangeWorld));
    }

    /**
     * Event fired when a player joins
     * @param handler The network handler for the player
     * @param sender The packet sender
     * @param server An instance of the server
     */
    private void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ServerPlayerEntity player = handler.getPlayer();
        eventStore.appendEventWithPosDim(player.getUuid(),
                TrackedEvents.JOIN,
                "",
                packPositionString(player.getBlockPos()),
                player.getServerWorld().getDimensionKey().getValue().toString());
    }

    /**
     * Event fired when a player leaves
     * @param handler The network handler for the player
     * @param server An instance of the server
     */
    private void onPlayerDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        ServerPlayerEntity player = handler.getPlayer();
        eventStore.appendEventWithPosition(player.getUuid(),
                TrackedEvents.LEAVE,
                "",
                packPositionString(player.getBlockPos()));
    }

    /**
     * Event fired when an entity dies
     * @param entity The entity that died
     * @param damageSource The source that killed the entity
     */
    private void afterDeath(LivingEntity entity, DamageSource damageSource) {
        if (entity instanceof ServerPlayerEntity player) {
            eventStore.appendEventWithPosition(player.getUuid(),
                    TrackedEvents.DEATH,
                    "",
                    packPositionString(player.getBlockPos()));
        }
    }

    /**
     * Event fired when a player respawns
     * @param oldPlayer The old instance of the player
     * @param newPlayer The new instance of the player
     * @param alive Whether the player is alive
     */
    private void afterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        eventStore.appendEventWithPosDim(newPlayer.getUuid(),
                TrackedEvents.RESPAWN,
                "",
                packPositionString(newPlayer.getBlockPos()),
                newPlayer.getServerWorld().getDimensionKey().getValue().toString());
    }

    /**
     * Event fired every tick
     * @param server An instance of the server
     */
    private void onTick(MinecraftServer server) {
        // Every Minute store players
        if (server.getTicks() % 1200 == 0) {
            Instant now = Instant.now();
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                eventStore.appendEvent(now,
                        player.getUuid(),
                        TrackedEvents.POSITION,
                        packPositionString(player.getBlockPos()));
            }
        }

        // Flush every 10 minutes
        if (server.getTicks() % 12000 == 0) eventStore.flushStore();
    }

    /**
     * Event fired after the player changes world
     * @param player The player that changes world
     * @param origin The world the player started in
     * @param destination The world the player goes to
     */
    private void afterChangeWorld(ServerPlayerEntity player, ServerWorld origin, ServerWorld destination) {
        eventStore.appendEventWithPosition(player.getUuid(),
                TrackedEvents.DIMENSION,
                destination.getDimensionKey().getValue().toString(),
                packPositionString(player.getBlockPos()));

        LOGGER.info("{}", packPositionString(player.getBlockPos()));
    }

    /**
     * Utility method to pack a position into a single string for the event store
     * @param x The x coord
     * @param y The y coord
     * @param z The z coord
     * @return A packed string representing the given coordinates
     */
    private String packPositionString(int x, int y, int z) {
        return "x" + x + "y" + y + "z" + z;
    }

    /**
     * Utility method to pack a position into a single string for the event store
     * @param pos The position
     * @return A packed string representing the given coordinates
     */
    private String packPositionString(BlockPos pos) {
        return packPositionString(pos.getX(), pos.getY(), pos.getZ());
    }
}
