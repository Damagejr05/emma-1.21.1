package net.damagejr05.emma.item.tracker;

import net.damagejr05.emma.entity.GrapplingBladeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class GrappleTracker {

    private static final Map<UUID, GrapplingBladeEntity> ACTIVE = new HashMap<>();
    private static final Map<UUID, GrappleState> STATES = new HashMap<>();
    private static final Map<UUID, Integer> REEL_TIME = new HashMap<>();

    public static void set(PlayerEntity player, GrapplingBladeEntity entity) {
        UUID id = player.getUuid();
        ACTIVE.put(id, entity);
        STATES.put(id, GrappleState.THROWN);
        REEL_TIME.put(id, 0);
    }

    public static boolean has(PlayerEntity player) {
        return ACTIVE.containsKey(player.getUuid());
    }

    public static GrapplingBladeEntity get(PlayerEntity player) {
        return ACTIVE.get(player.getUuid());
    }

    public static GrappleState getState(PlayerEntity player) {
        return STATES.get(player.getUuid());
    }

    public static void startReeling(PlayerEntity player) {
        STATES.put(player.getUuid(), GrappleState.REELING);
    }

    public static void startReturning(PlayerEntity player) {
        STATES.put(player.getUuid(), GrappleState.RETURNING);
    }


    public static void tickReeling(PlayerEntity player) {
        UUID id = player.getUuid();
        REEL_TIME.computeIfPresent(id, (k, t) -> t + 1);
    }

    public static int getReelTime(PlayerEntity player) {
        return REEL_TIME.getOrDefault(player.getUuid(), 0);
    }

    public static void endWithCooldown(PlayerEntity player, Item item) {
        int ticks = getReelTime(player);

        if (ticks > 0) {
            player.getItemCooldownManager().set(item, (ticks * 2) + 20);
        }

        REEL_TIME.remove(player);
        clear(player);
    }

    public static void clear(PlayerEntity player) {
        UUID id = player.getUuid();
        ACTIVE.remove(id);
        STATES.remove(id);
    }

    public enum GrappleState {
        THROWN,
        REELING,
        RETURNING;
    }
}
