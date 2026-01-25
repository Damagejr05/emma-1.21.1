package net.damagejr05.emma.item.tracker;

import net.minecraft.entity.player.PlayerEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class ExplosiveExitTracker {
    private static final Set<UUID> ACTIVE = new HashSet<>();

    public static void set(PlayerEntity player) {
        ACTIVE.add(player.getUuid());
    }

    public static void clear(PlayerEntity player) {
        ACTIVE.remove(player.getUuid());
    }

    public static boolean has(PlayerEntity player) {
        return ACTIVE.contains(player.getUuid());
    }
}
