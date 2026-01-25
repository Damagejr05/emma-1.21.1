package net.damagejr05.emma.entity;

import net.damagejr05.emma.item.ModItems;
import net.damagejr05.emma.item.tracker.ShadowstepTracker;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TeleportPositionEntity extends Entity implements Attackable {

    public static final int MAX_LIFETIME = 20 * 120;
    private int remainingTicks = MAX_LIFETIME;
    private static final float MAX_HEALTH = 20f;
    private float health = MAX_HEALTH;
    private int hurtTime = 0;
    private ChunkPos forcedChunk;

    @Nullable
    private UUID owner;

    public TeleportPositionEntity(EntityType<? extends TeleportPositionEntity> type, World world) {
        super(type, world);

        this.setNoGravity(true);
        this.noClip = true;
        this.dataTracker.set(REMAINING_TICKS, remainingTicks);
    }

    public void init(PlayerEntity player) {
        this.owner = player.getUuid();
        this.setPosition(player.getPos());
    }

    private static final TrackedData<Integer> REMAINING_TICKS =
            DataTracker.registerData(TeleportPositionEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public int getRemainingTicksClient() {
        return this.dataTracker.get(REMAINING_TICKS);
    }

    @Override public void tick() {
        super.tick();

        if (this.getWorld().isClient) return;

        if (forcedChunk == null && this.getWorld() instanceof ServerWorld serverWorld) {
            forcedChunk = new ChunkPos(this.getBlockPos()); serverWorld.setChunkForced(forcedChunk.x, forcedChunk.z, true);
        }

        remainingTicks--;

        this.dataTracker.set(REMAINING_TICKS, remainingTicks);

        if (hurtTime > 0) hurtTime--;

        spawnParticles(); if (remainingTicks <= 0) { discardAndClear();
        }
    }

    private void spawnParticles() {
        ((ServerWorld) this.getWorld()).spawnParticles(
                ParticleTypes.PORTAL,
                getX(),
                getY(),
                getZ(),
                12,
                0.5, 1.0, 0.5,
                0.0
        );
    }

    @Override
    public boolean damage(DamageSource source, float amount) {

        if (this.getWorld().isClient) return false;

        if (isInvulnerableTo(source)) return false;

        if (hurtTime > 0) return false;

        this.health -= amount;
        this.hurtTime = 5;

        reduceLifetimeByDamage(amount);
        spawnHitParticles();

        if (health <= 0) {
            discardAndClear();
            this.playSound(SoundEvents.ENTITY_GUARDIAN_DEATH, 2, 1);
            if (this.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(
                        ParticleTypes.SQUID_INK,
                        getX(), getY() + 1.0, getZ(),
                        50,
                        0.2, 0.5, 0.2,
                        0.3
                );
            }
        }

        return true;
    }

    private void spawnHitParticles() {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                    ParticleTypes.TRIAL_OMEN,
                    getX(), getY() + 1.0, getZ(),
                    10,
                    0.2, 0.5, 0.2,
                    0.3
            );
        }
    }


    public void reduceLifetimeByDamage(float damage) {
        int ticksLost = (int) (damage * 40);
        remainingTicks -= ticksLost;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(REMAINING_TICKS, MAX_LIFETIME);
    }

    @Override
    protected Box calculateBoundingBox() {
        return new Box(
                getX() - 0.5, getY(), getZ() - 0.5,
                getX() + 0.5, getY() + 2.0, getZ() + 0.5
        );
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    private void discardAndClear() {
        if (this.getWorld() instanceof ServerWorld serverWorld && forcedChunk != null) {
            serverWorld.setChunkForced(forcedChunk.x, forcedChunk.z, false);
        }

        this.remove(RemovalReason.DISCARDED);

        if (owner == null) return;

        PlayerEntity player = this.getWorld().getPlayerByUuid(owner);
        if (player == null) return;

        ShadowstepTracker.clear(owner);
        player.getItemCooldownManager().set(ModItems.EMMA_BLADE, 20);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("RemainingTicks", remainingTicks);
        if (owner != null) {
            nbt.putUuid("Owner", owner);
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        remainingTicks = nbt.getInt("RemainingTicks");
        if (nbt.containsUuid("Owner")) {
            owner = nbt.getUuid("Owner");
        }
    }

    @Override
    public @Nullable LivingEntity getLastAttacker() {
        return null;
    }
}
