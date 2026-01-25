package net.damagejr05.emma.item;

import com.mojang.serialization.Codec;
import net.damagejr05.emma.Emma;
import net.damagejr05.emma.entity.*;
import net.damagejr05.emma.item.tracker.ExplosiveExitTracker;
import net.damagejr05.emma.item.tracker.GrappleTracker;
import net.damagejr05.emma.item.tracker.ShadowstepTracker;
import net.damagejr05.emma.util.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

public class EmmaBlade extends ToolItem {
    public static final RegistryKey<DamageType> EMMA_STRIKE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Emma.MOD_ID, "emma_strike"));
    public static final int MIN_DRAW_DURATION = 10;
    public float particleLifetime;
    private final float attackDamage;
    private static final int MAX_TRAIL_TICKS = 20;
    private boolean isShootingALotOfSmokeParticlesAndObscuringTheUserPrettyWell;
    private int cooldownBeforeSoundAgain;
    private int timeShooting;
    public static final int MAX_TIME_SHOOTING = 20 * 5;

    //basic stuff (Because it's cool to be basic :D)
    public EmmaBlade(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Item.Settings settings) {
        super(toolMaterial, settings);
        this.attackDamage = attackDamage + toolMaterial.getAttackDamage();
    }

    public static final ComponentType<Integer> EXPLOSION_TRAIL_TICKS =
            Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(Emma.MOD_ID, "explosion_trail_ticks"),
                    ComponentType.<Integer>builder().codec(Codec.INT).packetCodec(PacketCodecs.VAR_INT).build());


    public static AttributeModifiersComponent createAttributeModifiers(ToolMaterial material, int baseAttackDamage, float attackSpeed) {
        return AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, ((float)baseAttackDamage + material.getAttackDamage()), EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND).add(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, attackSpeed, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND).build();
    }

    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = attacker.getWorld();
        world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).get(EMMA_STRIKE);

        RegistryEntry<DamageType> entry = world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .getEntry(EMMA_STRIKE)
                .orElseThrow();

        DamageSource src = new DamageSource(entry);
        target.damage(src, this.attackDamage);
        return true;
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = attacker.getWorld();

        RegistryEntry<DamageType> entry = world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .getEntry(EMMA_STRIKE)
                .orElseThrow();

        DamageSource src = new DamageSource(entry);
        target.damage(src, this.attackDamage);
    }

    //emma spear use logic
    @Override
    public UseAction getUseAction(ItemStack stack) {
        if (GetawayPlanHelper.hasGetawayPlan(stack, ModItems.WARDENS_INSPIRATION) ||
                GetawayPlanHelper.hasGetawayPlan(stack, ModItems.OBSCURING_HAZE) ||
                GetawayPlanHelper.hasGetawayPlan(stack, ModItems.SHADOWSTEP)) {
            return UseAction.BOW;
        }

        if (GetawayPlanHelper.hasGetawayPlan(stack, ModItems.EXPLOSIVE_EXIT) ||
                GetawayPlanHelper.hasGetawayPlan(stack, ModItems.GRAPPLING_BLADE)) {
            return UseAction.SPEAR;
        }

        return UseAction.NONE;
    }

    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (!(world instanceof ServerWorld)) {
            return TypedActionResult.consume(stack);
        }

        if (GetawayPlanHelper.hasGetawayPlan(stack, ModItems.GRAPPLING_BLADE)) {

            if (!GrappleTracker.has(player)) {
                player.setCurrentHand(hand);
                return TypedActionResult.consume(stack);
            }

            GrapplingBladeEntity hook = GrappleTracker.get(player);
            GrappleTracker.GrappleState state = GrappleTracker.getState(player);

            if (hook == null || hook.isRemoved()) {
                GrappleTracker.clear(player);
                return TypedActionResult.consume(stack);
            }

            if (state == GrappleTracker.GrappleState.RETURNING) {
                return TypedActionResult.fail(stack);
            }

            if (state == GrappleTracker.GrappleState.THROWN && hook.isEmbedded()) {
                GrappleTracker.startReeling(player);
                return TypedActionResult.consume(stack);
            }

            if (state == GrappleTracker.GrappleState.THROWN ||
                    state == GrappleTracker.GrappleState.REELING) {
                GrappleTracker.startReturning(player);
                return TypedActionResult.consume(stack);
            }

            return TypedActionResult.consume(stack);
        }

        if (GetawayPlanHelper.hasGetawayPlan(stack, ModItems.SHADOWSTEP)) {

            if (!ShadowstepTracker.has(player)) {
                player.setCurrentHand(hand);
                return TypedActionResult.consume(stack);
            }
            ShadowstepTracker.teleport(player);
            spawnShadowstepParticles(world, player);
            player.getItemCooldownManager().set(this, 200);
            return TypedActionResult.consume(stack);
        }

        if (GetawayPlanHelper.hasGetawayPlan(stack, ModItems.OBSCURING_HAZE)
                && player.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.fail(stack);
        }

        player.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        if (i >= MIN_DRAW_DURATION) {
            BlockPos spawn = player.getBlockPos();

            if (GetawayPlanHelper.hasGetawayPlan(stack, ModItems.EXPLOSIVE_EXIT)) {
                ExplosiveExitTracker.set(player);
                triggerExplosion(world, player);
                player.damage(
                        world.getDamageSources().explosion(null),
                        15.0f
                );
                stack.set(EXPLOSION_TRAIL_TICKS, MAX_TRAIL_TICKS);
                player.getItemCooldownManager().set(this, 100);
                player.getWorld().getOtherEntities(player, new Box(player.getEyePos().add(player.getRotationVec(0f).multiply(4)).add(-4, -4, -4),
                        player.getEyePos().add(player.getRotationVec(0f).multiply(4)).add(4, 4, 4))).forEach((e) ->
                        e.setVelocity(e.getPos().add(player.getPos().multiply(-1)).add(0, 1, 0).multiply(1)));
                player.setVelocity(player.getRotationVec(0f).multiply(-2f));
            }
            else if (GetawayPlanHelper.hasGetawayPlan(stack, ModItems.WARDENS_INSPIRATION)) {
                if (world instanceof ServerWorld currentWorld) {
                    spawnWardenParticles(world, player);

                    Box box = new Box(spawn).expand(5.0);
                    currentWorld.getEntitiesByClass(
                            LivingEntity.class,
                            box,
                            entity -> entity != player && entity.isAlive()
                    ).forEach(entity -> entity.addStatusEffect(
                            new StatusEffectInstance(StatusEffects.BLINDNESS, 140, 0, false, true)
                    ));
                    player.removeStatusEffect(StatusEffects.GLOWING);
                    player.getItemCooldownManager().set(this, 200);
                }
            }
            else if (GetawayPlanHelper.hasGetawayPlan(stack, ModItems.SHADOWSTEP)) {
                if (world instanceof ServerWorld currentWorld) {
                    TeleportPositionEntity anchor =
                            ModEntities.SHADOWSTEP_TELEPORT_POINT.create(currentWorld);
                    if (anchor != null) {
                        anchor.init(player);
                        currentWorld.spawnEntity(anchor);
                        BlockPos pos = anchor.getBlockPos();
                        ChunkPos chunkPos = new ChunkPos(pos);
                        currentWorld.setChunkForced(chunkPos.x, chunkPos.z, true);
                        ShadowstepTracker.set(player, anchor);
                        player.getItemCooldownManager().set(this, 20);

                    }
                    currentWorld.playSound(null, spawn,
                            SoundEvents.EVENT_MOB_EFFECT_BAD_OMEN,
                            SoundCategory.PLAYERS,
                            1.0F, 1.0F);
                    currentWorld.spawnParticles(
                            ParticleTypes.SOUL,
                            spawn.getX() + 0.5, spawn.getY() + 1, spawn.getZ() + 0.5,
                            50, 0.5, 1, 0.5, 0.2);
                }
            }
            else if (GetawayPlanHelper.hasGetawayPlan(stack, ModItems.GRAPPLING_BLADE)) {
                if (world instanceof ServerWorld currentWorld) {
                    if (!GrappleTracker.has(player)) {
                        stack.damage(1, player, LivingEntity.getSlotForHand(user.getActiveHand()));
                        GrapplingBladeEntity hook = new GrapplingBladeEntity(world, player, stack);
                        hook.setVelocity(player.getRotationVec(0f).multiply(2f));
                        world.spawnEntity(hook);
                        GrappleTracker.set(player, hook);
                        player.incrementStat(Stats.USED.getOrCreateStat(this));

                        currentWorld.playSound(null, spawn,
                                SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE,
                                SoundCategory.PLAYERS,
                                3.0F, 1.0F);
                        currentWorld.spawnParticles(
                                ParticleTypes.SMOKE,
                                spawn.getX() + 0.5, spawn.getY() + 1, spawn.getZ() + 0.5,
                                100, 0.25, 0.25, 0.25, 0.1);
                        currentWorld.spawnParticles(
                                ParticleTypes.WHITE_SMOKE,
                                spawn.getX() + 0.5, spawn.getY() + 1, spawn.getZ() + 0.5,
                                50, 0.25, 0.25, 0.25, 0.1);

                        player.getItemCooldownManager().set(this, 20);

                    }
                }
                player.setVelocity(player.getRotationVec(0f).multiply(-0.2f));
            }
        }
        if (GetawayPlanHelper.hasGetawayPlan(stack, ModItems.OBSCURING_HAZE)) {
            int usedTicks = this.getMaxUseTime(stack, user) - remainingUseTicks;
            usedTicks = Math.min(usedTicks, MAX_TIME_SHOOTING);

            int cooldown = usedTicks * 2;

            player.getItemCooldownManager().set(this, cooldown);
            player.removeStatusEffect(StatusEffects.GLOWING);

            timeShooting = 0;
            isShootingALotOfSmokeParticlesAndObscuringTheUserPrettyWell = false;
            if (world instanceof ServerWorld currentWorld) {
                currentWorld.playSound(
                        null,
                        player.getBlockPos(),
                        SoundEvents.BLOCK_FIRE_EXTINGUISH,
                        SoundCategory.PLAYERS,
                        1.0F,
                        0.8F
                );
            }
        }
    }

    private void triggerExplosion(World world, PlayerEntity player) {
        if (world.isClient) return;
        Vec3d pos = player.getPos();

        world.createExplosion(
                player,
                pos.getX(),
                pos.getY() + 1,
                pos.getZ(),
                2f,
                World.ExplosionSourceType.NONE
        );

        if (world instanceof ServerWorld currentWorld) {

            BlockPos spawn = player.getBlockPos();
            currentWorld.playSound(null, spawn,
                    SoundEvents.ENTITY_BREEZE_HURT,
                    SoundCategory.PLAYERS,
                    1.0F, 1.0F);

            currentWorld.spawnParticles(
                    ParticleTypes.CLOUD,
                    pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                    100, 2, 2, 2, 0.75);
            currentWorld.spawnParticles(
                    ParticleTypes.FLAME,
                    pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                    100, 1, 1, 1, 0.25);
            currentWorld.spawnParticles(
                    ParticleTypes.SMALL_GUST,
                    pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                    50, 2, 2, 2, 0.25);
            DustParticleEffect gold = new DustParticleEffect(new Vector3f(1.0F, 0.8F, 0.2F), 1.0F);
            DustParticleEffect red = new DustParticleEffect(new Vector3f(1.0F, 0.0F, 0.0F), 1.0F);

            currentWorld.spawnParticles(
                    gold,
                    spawn.getX() + 0.5,
                    spawn.getY() + 1,
                    spawn.getZ() + 0.5,
                    30, 0.5, 1, 0.5, 0.5
            );

            currentWorld.spawnParticles(
                    red,
                    spawn.getX() + 0.5,
                    spawn.getY() + 1,
                    spawn.getZ() + 0.5,
                    30, 0.5, 1, 0.5, 0.5
            );
        }
    }

    private void spawnExplosionTrailParticles(World world, PlayerEntity player){
        if (!player.isOnGround()) {
            ((ServerWorld) world).spawnParticles(
                    ParticleTypes.LARGE_SMOKE,
                    player.getX(),
                    player.getY() + 0.25,
                    player.getZ(),
                    2,
                    0.1, 0.1, 0.1,
                    0.0
            );

            ((ServerWorld) world).spawnParticles(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    player.getX(),
                    player.getY() + 0.25,
                    player.getZ(),
                    1,
                    0.1, 0.1, 0.1,
                    0.0
            );
            if (particleLifetime <= 10) {
                ((ServerWorld) world).spawnParticles(
                        ParticleTypes.FLAME,
                        player.getX(),
                        player.getY() + 0.25,
                        player.getZ(),
                        1,
                        0.1, 0.1, 0.1,
                        0.0
                );
            }
        }
    }

    private void spawnWardenParticles(World world, PlayerEntity player){
        if (world instanceof ServerWorld currentWorld) {
            BlockPos spawn = player.getBlockPos();
            currentWorld.playSound(null, spawn,
                    SoundEvents.ENTITY_WARDEN_HEARTBEAT,
                    SoundCategory.PLAYERS,
                    3.0F, 1.0F);

            currentWorld.playSound(null, spawn,
                    SoundEvents.ENTITY_WARDEN_ROAR,
                    SoundCategory.PLAYERS,
                    3.0F, 1.0F);

            currentWorld.playSound(null, spawn,
                    SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE,
                    SoundCategory.PLAYERS,
                    3.0F, 1.0F);

            currentWorld.spawnParticles(
                    ParticleTypes.SQUID_INK,
                    spawn.getX() + 0.5, spawn.getY() + 1, spawn.getZ() + 0.5,
                    1000, 5, 5, 5, 0.2);

            currentWorld.spawnParticles(
                    ParticleTypes.GLOW_SQUID_INK,
                    spawn.getX() + 0.5, spawn.getY() + 1, spawn.getZ() + 0.5,
                    200, 5, 5, 5, 0.2);

            currentWorld.spawnParticles(
                    ParticleTypes.SCULK_CHARGE_POP,
                    spawn.getX() + 0.5, spawn.getY() + 1, spawn.getZ() + 0.5,
                    100, 5, 5, 5, 0.2);

            currentWorld.spawnParticles(
                    ParticleTypes.SQUID_INK,
                    spawn.getX() + 0.5, spawn.getY() + 1, spawn.getZ() + 0.5,
                    100, 0.5, 0.5, 0.5, 0.2);

            DustParticleEffect gold = new DustParticleEffect(new Vector3f(1.0F, 0.8F, 0.2F), 1.0F);
            DustParticleEffect red = new DustParticleEffect(new Vector3f(1.0F, 0.0F, 0.0F), 1.0F);

            currentWorld.spawnParticles(
                    gold,
                    spawn.getX() + 0.5,
                    spawn.getY() + 1,
                    spawn.getZ() + 0.5,
                    30, 5, 5, 5, 0.5
            );

            currentWorld.spawnParticles(
                    red,
                    spawn.getX() + 0.5,
                    spawn.getY() + 1,
                    spawn.getZ() + 0.5,
                    30, 5, 5, 5, 0.5
            );
        }
    }

    private void spawnShadowstepParticles(World world, PlayerEntity player){
        if (world instanceof ServerWorld currentWorld) {

            BlockPos spawn = player.getBlockPos();
            BlockPos dest = player.getBlockPos();
            DustParticleEffect gold = new DustParticleEffect(new Vector3f(1.0F, 0.8F, 0.2F), 1.0F);
            DustParticleEffect red = new DustParticleEffect(new Vector3f(1.0F, 0.0F, 0.0F), 1.0F);

            currentWorld.spawnParticles(
                    gold,
                    spawn.getX() + 0.5,
                    spawn.getY() + 1,
                    spawn.getZ() + 0.5,
                    30, 0.5, 1, 0.5, 0.0
            );

            currentWorld.spawnParticles(
                    red,
                    spawn.getX() + 0.5,
                    spawn.getY() + 1,
                    spawn.getZ() + 0.5,
                    30, 0.5, 1, 0.5, 0.0
            );

            world.playSound(null, spawn,
                    SoundEvents.ENTITY_PLAYER_TELEPORT,
                    SoundCategory.PLAYERS,
                    1.5F, 1.0F);

            currentWorld.spawnParticles(
                    ParticleTypes.PORTAL,
                    spawn.getX() + 0.5, spawn.getY() + 1, spawn.getZ() + 0.5,
                    100, 0.5, 1, 0.5, 0.1);

            currentWorld.spawnParticles(
                    ParticleTypes.LARGE_SMOKE,
                    spawn.getX() + 0.5, spawn.getY() + 1, spawn.getZ() + 0.5,
                    50, 0.5, 1, 0.5, 0.0);

            currentWorld.spawnParticles(
                    gold,
                    dest.getX() + 0.5,
                    dest.getY() + 1,
                    dest.getZ() + 0.5,
                    30, 0.5, 1, 0.5, 0.0
            );

            currentWorld.spawnParticles(
                    red,
                    dest.getX() + 0.5,
                    dest.getY() + 1,
                    dest.getZ() + 0.5,
                    30, 0.5, 1, 0.5, 0.0
            );

            world.playSound(null, dest,
                    SoundEvents.ENTITY_PLAYER_TELEPORT,
                    SoundCategory.PLAYERS,
                    1.5F, 1.0F);

            currentWorld.spawnParticles(
                    ParticleTypes.PORTAL,
                    dest.getX() + 0.5, dest.getY() + 1, dest.getZ() + 0.5,
                    100, 0.5, 1, 0.5, 0.1);

            currentWorld.spawnParticles(
                    ParticleTypes.LARGE_SMOKE,
                    dest.getX() + 0.5, dest.getY() + 1, dest.getZ() + 0.5,
                    50, 0.5, 1, 0.5, 0.0);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (!(entity instanceof PlayerEntity player)) return;
        //grapple logic
        if (GetawayPlanHelper.hasGetawayPlan(stack, ModItems.GRAPPLING_BLADE)) {
            if (world.isClient()) return;
            if (!GrappleTracker.has(player)) return;
            if (GrappleTracker.getState(player) == GrappleTracker.GrappleState.REELING) {
                GrappleTracker.tickReeling(player);

                GrapplingBladeEntity hook = GrappleTracker.get(player);
                if (hook == null || hook.isRemoved()) {
                    GrappleTracker.endWithCooldown(player, this);
                    return;
                }

                Vec3d toHook = hook.getPos().subtract(player.getPos());
                double distance = toHook.length();

                if (distance < 1.5) {
                    hook.discard();
                    GrappleTracker.endWithCooldown(player, this);
                    player.velocityModified = true;
                    world.playSound(null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
                            SoundCategory.NEUTRAL,
                            1F, 1.0F);
                    return;
                }

                Vec3d pullDirection = toHook.normalize();
                double speed = 1;
                player.setVelocity(pullDirection.multiply(speed));
                player.velocityModified = true;

                world.playSound(null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.BLOCK_CHAIN_HIT,
                        SoundCategory.NEUTRAL,
                        1.0F,
                        1.0F
                );

                player.fallDistance = 0.0F;
            }
        }

        //recall stuff
        if (GetawayPlanHelper.hasGetawayPlan(stack, ModItems.GRAPPLING_BLADE) &&
                GrappleTracker.getState(player) == GrappleTracker.GrappleState.RETURNING) {
            GrapplingBladeEntity hook = GrappleTracker.get(player);

            if (hook == null || hook.isRemoved()) return;

            Vec3d toHook = hook.getPos().subtract(player.getPos());
            double distance = toHook.length();

            if (distance < 1.5 && (GrappleTracker.getState(player) == GrappleTracker.GrappleState.REELING || GrappleTracker.getState(player) == GrappleTracker.GrappleState.RETURNING)) return;

            world.playSound(null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.BLOCK_CHAIN_HIT,
                    SoundCategory.NEUTRAL,
                    0.8F,
                    1.5F
            );
        }

        //explosive exit trail particles
        if (GetawayPlanHelper.hasGetawayPlan(stack, ModItems.EXPLOSIVE_EXIT)) {
            if (!world.isClient) {
                if(player.isOnGround()){
                    ExplosiveExitTracker.clear(player);
                }
                Integer ticks = stack.get(EXPLOSION_TRAIL_TICKS);
                if (ticks != null && ticks > 0) {
                    spawnExplosionTrailParticles(world, player);

                    if (ticks <= 1) {
                        stack.remove(EXPLOSION_TRAIL_TICKS);
                    } else {
                        stack.set(EXPLOSION_TRAIL_TICKS, ticks - 1);
                    }
                }
            }
        }
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;
        if (!player.isUsingItem()) {
            isShootingALotOfSmokeParticlesAndObscuringTheUserPrettyWell = false;
            return;
        }

        if(GetawayPlanHelper.hasGetawayPlan(stack, ModItems.OBSCURING_HAZE)) {
            timeShooting++;
        }
        if(timeShooting + 1 >= MAX_TIME_SHOOTING) {
            isShootingALotOfSmokeParticlesAndObscuringTheUserPrettyWell = false;
            player.stopUsingItem();
            return;
        }
        if (player.getItemCooldownManager().isCoolingDown(this)) {
            player.stopUsingItem();
            return;
        }

        if(timeShooting < MAX_TIME_SHOOTING && GetawayPlanHelper.hasGetawayPlan(stack, ModItems.OBSCURING_HAZE)) {
            isShootingALotOfSmokeParticlesAndObscuringTheUserPrettyWell = player.isUsingItem();
            if(isShootingALotOfSmokeParticlesAndObscuringTheUserPrettyWell){
                cooldownBeforeSoundAgain++;
            }
            if (world instanceof ServerWorld currentWorld) {
                Vec3d look = player.getRotationVec(1.0F).normalize();
                Vec3d center = player.getPos().add(look.multiply(1.1)).add(0, 1.4, 0);
                Vec3d right = new Vec3d(-look.z, 0, look.x).normalize();
                Vec3d up = new Vec3d(0, 1, 0);
                int particleCount = 10;
                double radius = 0.8;
                double coneLength = 2;
                double coneAngle = Math.toRadians(60);
                double startOffset = 0.25;
                Vec3d forward = player.getRotationVec(1.0F).normalize();
                Vec3d coneStart = center.add(forward.multiply(startOffset));

                for (int i = 0; i < particleCount; i++) {

                    Vec3d tip = center.add(forward.multiply(0.25));

                    double t = currentWorld.random.nextDouble();
                    double maxRadius = Math.tan(coneAngle) * coneLength;
                    double r = Math.sqrt(currentWorld.random.nextDouble()) * maxRadius;

                    double angle = currentWorld.random.nextDouble() * Math.PI * 2;

                    Vec3d direction =
                            forward.multiply(coneLength)
                                    .add(right.multiply(Math.cos(angle) * r))
                                    .add(up.multiply(Math.sin(angle) * r))
                                    .normalize();

                    double speed = MathHelper.lerp(t, 0.25, 0.05);
                    double value = Math.random() * 0.02;

                    currentWorld.spawnParticles(
                            ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                            tip.x, tip.y, tip.z,
                            15,
                            direction.x * speed,
                            direction.y * speed,
                            direction.z * speed,
                            value
                    );

                    currentWorld.spawnParticles(
                            ParticleTypes.SMOKE,
                            tip.x, tip.y, tip.z,
                            1,
                            direction.x * speed,
                            direction.y * speed,
                            direction.z * speed,
                            value
                    );
                    currentWorld.spawnParticles(
                            ParticleTypes.LARGE_SMOKE,
                            tip.x, tip.y, tip.z,
                            1,
                            direction.x * speed,
                            direction.y * speed,
                            direction.z * speed,
                            value
                    );
                    currentWorld.spawnParticles(
                            ParticleTypes.WHITE_SMOKE,
                            tip.x, tip.y, tip.z,
                            1,
                            direction.x * speed,
                            direction.y * speed,
                            direction.z * speed,
                            value
                    );

                    BlockPos spawn = player.getBlockPos();
                    if (cooldownBeforeSoundAgain >= 4) {
                        currentWorld.playSound(null, spawn,
                                SoundEvents.ENTITY_BLAZE_SHOOT,
                                SoundCategory.PLAYERS,
                                1.0F, 1.0F);
                        cooldownBeforeSoundAgain = 0;
                    }
                }
            }
        }
    }

    //all the bundle stuff
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType != ClickType.RIGHT) {
            return false;
        } else {
            BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
            if (bundleContentsComponent == null) {
                return false;
            } else {
                ItemStack itemStack = slot.getStack();

                BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContentsComponent);
                if (itemStack.isEmpty()) {
                    this.playRemoveOneSound(player);
                    ItemStack itemStack2 = builder.removeFirst();
                    if (itemStack2 != null) {
                        ItemStack itemStack3 = slot.insertStack(itemStack2);
                        builder.add(itemStack3);
                    }
                } else if (itemStack.getItem().canBeNested()) {
                    int i = builder.add(slot, player);
                    if (i > 0) {
                        this.playInsertSound(player);
                    }
                }

                stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());

            }

            return true;
        }
    }

    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType == ClickType.RIGHT && slot.canTakePartial(player)) {
            BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
            if (bundleContentsComponent == null) {
                return false;
            } else {
                BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContentsComponent);
                if (otherStack.isEmpty()) {
                    ItemStack itemStack = builder.removeFirst();
                    if (itemStack != null) {
                        this.playRemoveOneSound(player);
                        cursorStackReference.set(itemStack);
                    }
                } else if (otherStack.isIn(ModTags.Items.GETAWAY_PLANS)){
                    int i = builder.add(otherStack);
                    if (i > 0) {
                        this.playInsertSound(player);
                    }
                }

                stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        BundleContentsComponent contents =
                stack.get(DataComponentTypes.BUNDLE_CONTENTS);

        if (contents == null || contents.isEmpty()) {
            tooltip.add(
                    Text.translatable("tooltip.emma.requires_getaway_plan.tooltip").formatted(Formatting.RED));
            tooltip.add(
                    Text.translatable("tooltip.emma.requires_getaway_plan.tooltip_1").formatted(Formatting.RED));
            return;
        }

        tooltip.add(
                Text.translatable("tooltip.emma.getaway_plan.tooltip")
                        .formatted(Formatting.YELLOW).formatted(Formatting.ITALIC)
        );

        ItemStack planStack = contents.iterateCopy().iterator().next();
        Item planItem = planStack.getItem();

        tooltip.add(
                Text.literal("  ")
                        .append(planStack.getName())
                        .formatted(GetawayPlanTooltipHelper.getColor(planItem)).formatted(Formatting.BOLD)
        );

        List<Text> planTooltip = new java.util.ArrayList<>();
        planItem.appendTooltip(planStack, context, planTooltip, type);

        for (Text line : planTooltip) {
            String key = line.getContent().toString();

            if (key.contains("tooltip.emma.getaway_plan.tooltip") ||
                    key.contains("tooltip.emma.blank_spot.tooltip")) {
                continue;
            }

            tooltip.add(
                    Text.literal("  ")
                            .append(line)
                            .formatted(Formatting.GRAY)
            );
        }
    }

    public void onItemEntityDestroyed(ItemEntity entity) {
        BundleContentsComponent bundleContentsComponent = entity.getStack().get(DataComponentTypes.BUNDLE_CONTENTS);
        if (bundleContentsComponent != null) {
            entity.getStack().set(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
            ItemUsage.spawnItemContents(entity, bundleContentsComponent.iterateCopy());
        }
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }
}
