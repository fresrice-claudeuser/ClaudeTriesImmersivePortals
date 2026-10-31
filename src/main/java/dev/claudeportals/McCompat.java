package dev.claudeportals;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.UUID;

/**
 * Small stand-ins for Minecraft methods that disappeared between 1.21.1 and 1.21.11, so the ported upstream
 * code keeps its old call shape. Referenced by the edits that tools/port_sites.py applies.
 */
public final class McCompat {
    private McCompat() {
    }

    /** Entity.getServer() no longer exists; only ServerLevel knows its server. Null on the client. */
    public static MinecraftServer getServer(Entity entity) {
        return entity.level() instanceof ServerLevel serverLevel ? serverLevel.getServer() : null;
    }

    /** Level.getServer() no longer exists on the base class. Null on the client. */
    public static MinecraftServer getServer(Level level) {
        return level instanceof ServerLevel serverLevel ? serverLevel.getServer() : null;
    }

    /** Direction.fromDelta(int, int, int) was removed; same contract: the direction whose unit step matches, or null. */
    public static Direction fromDelta(int x, int y, int z) {
        for (Direction direction : Direction.values()) {
            if (direction.getStepX() == x && direction.getStepY() == y && direction.getStepZ() == z) {
                return direction;
            }
        }
        return null;
    }

    // CompoundTag.putUUID/getUUID/hasUUID are gone in 1.21.11 (entities moved to ValueInput/ValueOutput). Until the
    // entity serialization is ported properly, a UUID is stored as two longs. Uses only putLong/getLongOr/contains.
    public static void putUUID(CompoundTag tag, String key, UUID uuid) {
        tag.putLong(key + "_most", uuid.getMostSignificantBits());
        tag.putLong(key + "_least", uuid.getLeastSignificantBits());
    }

    public static boolean hasUUID(CompoundTag tag, String key) {
        return tag.contains(key + "_most") && tag.contains(key + "_least");
    }

    public static UUID getUUID(CompoundTag tag, String key) {
        return new UUID(tag.getLongOr(key + "_most", 0L), tag.getLongOr(key + "_least", 0L));
    }
}
