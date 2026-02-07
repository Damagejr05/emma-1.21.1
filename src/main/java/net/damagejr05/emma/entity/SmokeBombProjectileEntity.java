package net.damagejr05.emma.entity;

import net.damagejr05.emma.item.ModItems;
import net.damagejr05.emma.util.ModSounds;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.List;

public class SmokeBombProjectileEntity extends ThrownItemEntity {
    private float rotationX = 0.0f;
    private float rotationY = 0.0f;

    private boolean hasLanded = false;
    private boolean hasBounced = false;
    private int ticksSinceBounce = 0;

    private boolean serverPuffed = false;


    public SmokeBombProjectileEntity(EntityType<? extends SmokeBombProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public SmokeBombProjectileEntity(World world, LivingEntity owner) {
        super(ModEntities.SMOKE_BOMB, owner, world);
        this.setItem(new ItemStack(ModItems.SMOKE_BOMB));
    }


    public SmokeBombProjectileEntity(World world, double x, double y, double z, LivingEntity owner) {
        super(ModEntities.SMOKE_BOMB, x, y, z, world);
        this.setOwner(owner);
    }


    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            for (int i = 0; i < 8; i++) {
                this.getWorld()
                        .addParticle(
                                new ItemStackParticleEffect(ParticleTypes.ITEM, this.getStack()),
                                this.getX(),
                                this.getY(),
                                this.getZ(),
                                0.0, 0.0, 0.0
                        );
            }

            createSmokePuff_Client();
        } else {
            super.handleStatus(status);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        entityHitResult.getEntity().damage(this.getDamageSources().thrown(this, this.getOwner()), 0.0F);
    }

    @Override
    public void tick() {
        super.tick();

        if (hasBounced) {
            ticksSinceBounce++;

            if (ticksSinceBounce == 15) {
                if (!this.getWorld().isClient && !serverPuffed) {
                    serverPuffed = true;

                    boolean affectedAny = applyInvisibilityAoE();

                    this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);

                    this.discard();
                }

                return;
            }
        }

        if (!hasLanded) {
            this.rotationY += 20.0f;
            if (this.rotationY > 360.0f) {
                this.rotationY -= 360.0f;
            }
        }
    }

    private void createSmokePuff_Client() {

        double currentRadius = 3;

        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        spawnParticleSphere(ParticleTypes.LARGE_SMOKE, 200, x, y, z, 2.5, 2.0, 2.5, 0.0);
        spawnParticleSphere(ParticleTypes.CAMPFIRE_COSY_SMOKE, 150, x, y, z, 4, 2.5, 4, 0.0);
        spawnParticleSphere(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, 800, x, y, z, 2.25, 2.5, 2.25, 0.0);
        spawnParticleSphere(ParticleTypes.SQUID_INK, 500, x, y, z, 2.5, 1.5, 2.5, 0.0);
    }

    private boolean applyInvisibilityAoE() {
        double radius = 2.0;
        int duration = 400;

        if (this.getWorld().isClient) return false;

        LivingEntity owner = (LivingEntity) this.getOwner();
        if (owner == null) return false;

        List<LivingEntity> targets = this.getWorld().getNonSpectatingEntities(
                LivingEntity.class,
                this.getBoundingBox().expand(radius)
        );

        boolean affectedAny = false;

        for (LivingEntity entity : targets) {

            if (!(entity instanceof PlayerEntity player)) continue;
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, duration));
            player.removeStatusEffect(StatusEffects.GLOWING);
            player.playSound(ModSounds.INVISIBILITY_TRIGGER, 1.0F, 1.0F);
            affectedAny = true;
        }

        return affectedAny;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (!this.hasBounced) {
            this.hasBounced = true;

            this.playSound(ModSounds.SMOKE_BOMB_EXPLODE, 1.0F, 1.0F);
            this.setVelocity(0, 0.3, 0);
            this.setNoGravity(false);

            return;
        }

        super.onCollision(hitResult);
    }

    private void spawnParticleSphere(
            ParticleEffect type,
            int count,
            double x, double y, double z,
            double offsetX, double offsetY, double offsetZ,
            double speed
    ) {
        for (int i = 0; i < count; i++) {
            double px = x + (this.random.nextDouble() * 2 - 1) * offsetX;
            double py = y + (this.random.nextDouble() * 2 - 1) * offsetY;
            double pz = z + (this.random.nextDouble() * 2 - 1) * offsetZ;

            double dx = (speed == 0.0) ? 0.0 : (this.random.nextDouble() - 0.5) * speed;
            double dy = (speed == 0.0) ? 0.0 : (this.random.nextDouble() - 0.5) * speed;
            double dz = (speed == 0.0) ? 0.0 : (this.random.nextDouble() - 0.5) * speed;

            this.getWorld().addParticle(type, px, py, pz, dx, dy, dz);
        }
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SMOKE_BOMB;
    }

    public float getRotationX() {
        return this.rotationX;
    }

    public float getRotationY() {
        return this.rotationY;
    }
}
