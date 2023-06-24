package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.MimiCubeAIRangedAttack;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolActions;

public class EntityMimicube extends Monster implements RangedAttackMob {

    private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(EntityMimicube.class, EntityDataSerializers.INT);
    private final MimiCubeAIRangedAttack aiArrowAttack = new MimiCubeAIRangedAttack(this, 1.0D, 10, 15.0F);
    private final MeleeAttackGoal aiAttackOnCollide = new MeleeAttackGoal(this, 1.2D, false);
    public float squishAmount;
    public float squishFactor;
    public float prevSquishFactor;
    public float leftSwapProgress = 0;
    public float prevLeftSwapProgress = 0;
    public float rightSwapProgress = 0;
    public float prevRightSwapProgress = 0;
    public float helmetSwapProgress = 0;
    public float prevHelmetSwapProgress = 0;
    public float prevAttackProgress;
    public float attackProgress;
    private boolean wasOnGround;
    private int eatingTicks;

    protected EntityMimicube(EntityType type, Level world) {
        super(type, world);
        this.moveControl = new MimicubeMoveHelper(this);
        this.navigation = new DirectPathNavigator(this, world);
        this.setCombatTask();
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.MOVEMENT_SPEED, 0.45F);
    }


    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.mimicubeSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_TICK, 0);

    }

    public boolean doHurtTarget(Entity entityIn) {
        this.entityData.set(ATTACK_TICK, 5);
        return true;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new AnimalAIWanderRanged(this, 60, 1.0D, 10, 7));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
    }

    public void setCombatTask() {
        if (this.level() != null && !this.level().isClientSide) {
            this.goalSelector.removeGoal(this.aiAttackOnCollide);
            this.goalSelector.removeGoal(this.aiArrowAttack);
            ItemStack itemstack = this.getMainHandItem();
            if (itemstack.getItem() instanceof ProjectileWeaponItem || itemstack.getItem() instanceof TridentItem) {
                int i = 10;
                if (this.level().getDifficulty() != Difficulty.HARD) {
                    i = 30;
                }

                this.aiArrowAttack.setAttackCooldown(i);
                this.goalSelector.addGoal(4, this.aiArrowAttack);
            } else {
                this.goalSelector.addGoal(4, this.aiAttackOnCollide);
            }

        }
    }

    public void attackEntityWithRangedAttackTrident(LivingEntity target, float distanceFactor) {
        ThrownTrident tridententity = new ThrownTrident(this.level(), this, new ItemStack(Items.TRIDENT));
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - tridententity.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Mth.sqrt((float)(d0 * d0 + d2 * d2));
        tridententity.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - this.level().getDifficulty().getId() * 4));
        this.gameEvent(GameEvent.PROJECTILE_SHOOT);
        this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(tridententity);
    }

    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (this.getMainHandItem().getItem() instanceof TridentItem) {
            attackEntityWithRangedAttackTrident(target, distanceFactor);
            return;
        }
        ItemStack itemstack = this.getProjectile(this.getMainHandItem());
        AbstractArrow abstractarrowentity = this.fireArrow(itemstack, distanceFactor);
        if (this.getMainHandItem().getItem() instanceof net.minecraft.world.item.BowItem)
            abstractarrowentity = ((net.minecraft.world.item.BowItem) this.getMainHandItem().getItem()).customArrow(abstractarrowentity);
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - abstractarrowentity.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Mth.sqrt((float)(d0 * d0 + d2 * d2));
        abstractarrowentity.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - this.level().getDifficulty().getId() * 4));
        this.gameEvent(GameEvent.PROJECTILE_SHOOT);
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(abstractarrowentity);
    }

    protected AbstractArrow fireArrow(ItemStack arrowStack, float distanceFactor) {
        return ProjectileUtil.getMobArrow(this, arrowStack, distanceFactor);
    }

    public boolean canFireProjectileWeapon(ProjectileWeaponItem p_230280_1_) {
        return p_230280_1_ == Items.BOW;
    }

    public void setItemSlot(EquipmentSlot slotIn, ItemStack stack) {
        if (slotIn == EquipmentSlot.HEAD && !ItemStack.isSameItem(stack, this.getItemBySlot(EquipmentSlot.HEAD))) {
            helmetSwapProgress = 5;
            this.level().broadcastEntityEvent(this, (byte) 45);
        }
        if (slotIn == EquipmentSlot.MAINHAND && !ItemStack.isSameItem(stack, this.getItemBySlot(EquipmentSlot.MAINHAND))) {
            rightSwapProgress = 5;
            this.level().broadcastEntityEvent(this, (byte) 46);
        }
        if (slotIn == EquipmentSlot.OFFHAND && !ItemStack.isSameItem(stack, this.getItemBySlot(EquipmentSlot.OFFHAND))) {
            leftSwapProgress = 5;
            this.level().broadcastEntityEvent(this, (byte) 47);
        }
        super.setItemSlot(slotIn, stack);
        if (!this.level().isClientSide) {
            this.setCombatTask();
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == 45) {
            helmetSwapProgress = 5;
        }
        if (id == 46) {
            rightSwapProgress = 5;
        }
        if (id == 47) {
            leftSwapProgress = 5;

        }
    }

    public boolean isBlocking() {
        return this.getMainHandItem().canPerformAction(ToolActions.SHIELD_BLOCK) || this.getOffhandItem().canPerformAction(ToolActions.SHIELD_BLOCK);
    }

    public boolean hurt(DamageSource source, float amount) {
        Entity trueSource = source.getEntity();
        if (trueSource != null && trueSource instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity) trueSource;
            if (!attacker.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                this.setItemSlot(EquipmentSlot.HEAD, mimicStack(attacker.getItemBySlot(EquipmentSlot.HEAD)));
            }
            if (!attacker.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty()) {
                this.setItemSlot(EquipmentSlot.OFFHAND, mimicStack(attacker.getItemBySlot(EquipmentSlot.OFFHAND)));
            }
            if (!attacker.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
                this.setItemSlot(EquipmentSlot.MAINHAND, mimicStack(attacker.getItemBySlot(EquipmentSlot.MAINHAND)));
            }
        }
        return super.hurt(source, amount);
    }

    private ItemStack mimicStack(ItemStack stack){
        ItemStack copy = stack.copy();
        if(copy.isDamageableItem()){
            copy.setDamageValue(copy.getMaxDamage());
        }
        return copy;
    }

    public void tick() {
        super.tick();
        this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;
        this.prevSquishFactor = this.squishFactor;
        this.prevHelmetSwapProgress = this.helmetSwapProgress;
        this.prevRightSwapProgress = this.rightSwapProgress;
        this.prevLeftSwapProgress = this.leftSwapProgress;
        this.prevAttackProgress = attackProgress;
        if (rightSwapProgress > 0F) {
            rightSwapProgress -= 0.5F;
        }
        if (leftSwapProgress > 0F) {
            leftSwapProgress -= 0.5F;
        }
        if (helmetSwapProgress > 0F) {
            helmetSwapProgress -= 0.5F;
        }
        if (this.onGround() && !this.wasOnGround) {

            for (int j = 0; j < 8; ++j) {
                float f = this.random.nextFloat() * Mth.TWO_PI;
                float f1 = this.random.nextFloat() * 0.5F + 0.5F;
                float f2 = Mth.sin(f) * 0.5F * f1;
                float f3 = Mth.cos(f) * 0.5F * f1;
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(AMItemRegistry.MIMICREAM.get())), this.getX() + (double)f2, this.getY(), this.getZ() + (double)f3, 0.0D, 0.0D, 0.0D);
            }

            this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            this.squishAmount = -0.35F;
        } else if (!this.onGround() && this.wasOnGround) {
            this.squishAmount = 2F;
        }
        if(this.isInWater()){
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.05D, 0));
        }
        if (this.getOffhandItem().getItem().isEdible() && this.getHealth() < this.getMaxHealth()) {
            if (eatingTicks < 100) {
                for (int i = 0; i < 3; i++) {
                    double d2 = this.random.nextGaussian() * 0.02D;
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItemInHand(InteractionHand.OFF_HAND)), this.getX() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, this.getY() + this.getBbHeight() * 0.5F + (double) (this.random.nextFloat() * this.getBbHeight() * 0.5F), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, d0, d1, d2);
                }
                if (eatingTicks % 6 == 0) {
                    this.gameEvent(GameEvent.EAT);
                    this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
                }
                eatingTicks++;
            }
            if (eatingTicks == 100) {
                this.gameEvent(GameEvent.EAT);
                this.playSound(SoundEvents.PLAYER_BURP, this.getSoundVolume(), this.getVoicePitch());
                this.getOffhandItem().shrink(1);
                this.heal(5);
                eatingTicks = 0;
            }
        } else if (this.getMainHandItem().getItem().isEdible() && this.getHealth() < this.getMaxHealth()) {
            if (eatingTicks < 100) {
                for (int i = 0; i < 3; i++) {
                    double d2 = this.random.nextGaussian() * 0.02D;
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItemInHand(InteractionHand.MAIN_HAND)), this.getX() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, this.getY() + this.getBbHeight() * 0.5F + (double) (this.random.nextFloat() * this.getBbHeight() * 0.5F), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, d0, d1, d2);
                }
                this.gameEvent(GameEvent.EAT);
                this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
                if (eatingTicks % 6 == 0) {
                    this.gameEvent(GameEvent.EAT);
                    this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
                }
                eatingTicks++;
            }
            if (eatingTicks == 100) {
                this.gameEvent(GameEvent.EAT);
                this.playSound(SoundEvents.PLAYER_BURP, this.getSoundVolume(), this.getVoicePitch());
                this.getMainHandItem().shrink(1);
                this.heal(5);
            }
        } else {
            eatingTicks = 0;
        }
        this.wasOnGround = this.onGround();
        this.alterSquishAmount();
        LivingEntity livingentity = this.getTarget();
        if (livingentity != null && this.distanceToSqr(livingentity) < 144D) {
            this.moveControl.setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), this.moveControl.getSpeedModifier());
            this.wasOnGround = true;
        }
        if (this.entityData.get(ATTACK_TICK) > 0) {
            if (this.entityData.get(ATTACK_TICK) == 2 && this.getTarget() != null && this.distanceTo(this.getTarget()) < 2.3D) {
                super.doHurtTarget(this.getTarget());
            }
            this.entityData.set(ATTACK_TICK, this.entityData.get(ATTACK_TICK) - 1);
            if (attackProgress < 3F) {
                attackProgress++;
            }
        } else {
            if (attackProgress > 0F) {
                attackProgress--;
            }
        }

    }

    protected float getEquipmentDropChance(EquipmentSlot slotIn) {
        return 0;
    }

    private SoundEvent getSquishSound() {
        return AMSoundRegistry.MIMICUBE_JUMP.get();
    }

    private SoundEvent getJumpSound() {
        return AMSoundRegistry.MIMICUBE_JUMP.get();
    }

    protected void jumpFromGround() {
        Vec3 vector3d = this.getDeltaMovement();
        this.setDeltaMovement(vector3d.x, this.getJumpPower(), vector3d.z);
        this.hasImpulse = true;
    }

    protected int getJumpDelay() {
        return this.random.nextInt(20) + 10;
    }

    protected void alterSquishAmount() {
        this.squishAmount *= 0.6F;
    }

    public boolean shouldShoot() {
        return this.getMainHandItem().getItem() instanceof ProjectileWeaponItem || this.getMainHandItem().getItem() instanceof TridentItem;
    }

    private class MimicubeMoveHelper extends MoveControl {
        private final EntityMimicube slime;
        private float yRot;
        private int jumpDelay;
        private boolean isAggressive;

        public MimicubeMoveHelper(EntityMimicube slimeIn) {
            super(slimeIn);
            this.slime = slimeIn;
            this.yRot = 180.0F * slimeIn.getYRot() / Mth.PI;
        }

        public void setDirection(float yRotIn, boolean aggressive) {
            this.yRot = yRotIn;
            this.isAggressive = aggressive;
        }

        public void setSpeed(double speedIn) {
            this.speedModifier = speedIn;
            this.operation = MoveControl.Operation.MOVE_TO;
        }

        public void tick() {
            if (this.mob.onGround()) {
                this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                if (this.jumpDelay-- <= 0 && this.operation != Operation.WAIT) {
                    this.jumpDelay = this.slime.getJumpDelay();
                    if (this.mob.getTarget() != null) {
                        this.jumpDelay /= 3;
                    }

                    this.slime.getJumpControl().jump();
                    this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getVoicePitch());
                } else {
                    this.slime.xxa = 0.0F;
                    this.slime.zza = 0.0F;
                    this.mob.setSpeed(0.0F);
                }
            }
            super.tick();
        }
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.MIMICUBE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.MIMICUBE_HURT.get();
    }

}

