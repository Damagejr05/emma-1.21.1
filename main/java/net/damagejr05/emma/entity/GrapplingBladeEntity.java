package net.damagejr05.emma.entity;

import net.damagejr05.emma.item.ModItems;
import net.damagejr05.emma.item.tracker.GrappleTracker;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GrapplingBladeEntity extends PersistentProjectileEntity {
    public PickupPermission pickupType;
    private State state;

    public GrapplingBladeEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
        this.state = State.FLYING;
    }

    public GrapplingBladeEntity(World world, LivingEntity owner, ItemStack stack) {
        super(ModEntities.GRAPPLING_BLADE, owner, world, stack, null);
        this.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
    }

    @Override
    public void setOwner(@Nullable Entity entity) {
        super.setOwner(entity);
    }

    @Nullable
    public PlayerEntity getPlayerOwner() {
        Entity entity = this.getOwner();
        return entity instanceof PlayerEntity ? (PlayerEntity) entity : null;
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return ItemStack.EMPTY;
    }

    public boolean isEmbedded() {
        return this.state == State.EMBEDDED;
    }
    public boolean isReturning() {
        return this.state == State.RETURNING;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        this.state=State.EMBEDDED;
        PlayerEntity player = this.getPlayerOwner();
        player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                1.0F, 1.0F);
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        PlayerEntity player = this.getPlayerOwner();
        GrappleTracker.startReturning(player);
    }

    @Override
    public void tick() {
        super.tick();
        PlayerEntity player = this.getPlayerOwner();

        if (player == null) {
            this.discard();
            return;
        }

        if (!this.getWorld().isClient) {
            if (removeIfInvalid(player)) {
                GrappleTracker.clear(player);
                return;
            }
            if (GrappleTracker.getState(player) == GrappleTracker.GrappleState.REELING) {
                this.setVelocity(Vec3d.ZERO);
                this.velocityDirty = true;
                this.setNoGravity(true);
            }
        }

        if(GrappleTracker.getState(player) == GrappleTracker.GrappleState.RETURNING){
            this.setNoClip(true);
            Vec3d vec3d = player.getEyePos().subtract(this.getPos());
            this.setPos(this.getX(), this.getY() + vec3d.y * 0.05, this.getZ());
            if (this.getWorld().isClient) {
                this.lastRenderY = this.getY();
            }

            this.setVelocity(this.getVelocity().multiply(0.5).add(vec3d.normalize()));
        }

        if (player != null) {
            double distance = this.getPos().distanceTo(player.getEyePos());

            if (distance < 1.5 && (GrappleTracker.getState(player) == GrappleTracker.GrappleState.REELING || GrappleTracker.getState(player) == GrappleTracker.GrappleState.RETURNING)) {
                this.discard();
                GrappleTracker.endWithCooldown(player, ModItems.EMMA_BLADE);

                player.velocityModified = true;
                this.getWorld().playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
                        SoundCategory.PLAYERS,
                        1F, 1.0F
                );
            }
        }

    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (!this.getWorld().isClient && (this.inGround || this.isNoClip()) && this.shake <= 0) {
            GrappleTracker.startReturning(player);
        }
    }

    private boolean removeIfInvalid(PlayerEntity player) {
        ItemStack itemStack = player.getMainHandStack();
        ItemStack itemStack2 = player.getOffHandStack();
        boolean bl = itemStack.isOf(ModItems.EMMA_BLADE);
        boolean bl2 = itemStack2.isOf(ModItems.EMMA_BLADE);
        if (!player.isRemoved() && player.isAlive() && (bl || bl2)) {
            return false;
        } else {
            this.discard();
            GrappleTracker.clear(player);
            player.getItemCooldownManager().set(ModItems.EMMA_BLADE, 400);
            return true;
        }
    }

    @Override
    public void onRemoved() {
        super.onRemoved();

        if (!this.getWorld().isClient) {
            PlayerEntity player = this.getPlayerOwner();
            if (player != null && GrappleTracker.get(player) == this) {
                GrappleTracker.clear(player);
            }
        }
    }

    static enum State {
        FLYING,
        EMBEDDED,
        RETURNING;

        State() {
        }
    }
}
