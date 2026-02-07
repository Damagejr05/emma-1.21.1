package net.damagejr05.emma.item.tracker;

import net.damagejr05.emma.entity.TeleportPositionEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShadowstepTracker {

    private static final Map<UUID, TeleportPositionEntity> ACTIVE = new HashMap<>();
    private static final Map<UUID, Boolean> TELEPORTED = new HashMap<>();

    public static boolean has(PlayerEntity player) {
        return ACTIVE.containsKey(player.getUuid());
    }

    public static TeleportPositionEntity get(PlayerEntity player) {
        return ACTIVE.get(player.getUuid());
    }

    public static void set(PlayerEntity player, TeleportPositionEntity entity) {
        ACTIVE.put(player.getUuid(), entity);
    }

    public static void clear(UUID uuid) {
        ACTIVE.remove(uuid);
    }

    public static void teleport(PlayerEntity player) {
        TeleportPositionEntity anchor = get(player);
        if (anchor == null || anchor.isRemoved()) {
            clear(player.getUuid());
            clearTeleported(player);
            return;
        }

        player.requestTeleport(anchor.getX(), anchor.getY(), anchor.getZ());
        anchor.discard();
        clear(player.getUuid());

        markTeleported(player);
    }


    public static boolean hasTeleported(PlayerEntity player) {
        return TELEPORTED.getOrDefault(player.getUuid(), false);
    }

    public static void markTeleported(PlayerEntity player) {
        TELEPORTED.put(player.getUuid(), true);
    }

    public static void clearTeleported(PlayerEntity player) {
        TELEPORTED.remove(player.getUuid());
    }
}
