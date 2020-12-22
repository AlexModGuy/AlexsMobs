package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.MimiCubeAIRangedAttack;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityMimicube extends MonsterEntity implements IRangedAttackMob {

    private static final DataParameter<Integer> ATTACK_TICK = EntityDataManager.createKey(EntityMimicube.class, DataSerializers.VARINT);
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

    protected EntityMimicube(EntityType type, World world) {
        super(type, world);
        this.moveController = new MimicubeMoveHelper(this);
        this.navigator = new DirectPathNavigator(this, world);
        this.setCombatTask();
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 30.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.45F);
    }


    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.mimicubeSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(ATTACK_TICK, 0);

    }

    public boolean attackEntityAsMob(Entity entityIn) {
        this.dataManager.set(ATTACK_TICK, 5);
        return true;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new AnimalAIWanderRanged(this, 60, 1.0D, 10, 7));
        this.goalSelector.addGoal(2, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(2, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, true));
    }

    public void setCombatTask() {
        if (this.world != null && !this.world.isRemote) {
            this.goalSelector.removeGoal(this.aiAttackOnCollide);
            this.goalSelector.removeGoal(this.aiArrowAttack);
            ItemStack itemstack = this.getHeldItemMainhand();
            if (itemstack.getItem() instanceof ShootableItem || itemstack.getItem() instanceof TridentItem) {
                int i = 10;
                if (this.world.getDifficulty() != Difficulty.HARD) {
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
        TridentEntity tridententity = new TridentEntity(this.world, this, new ItemStack(Items.TRIDENT));
        double d0 = target.getPosX() - this.getPosX();
        double d1 = target.getPosYHeight(0.3333333333333333D) - tridententity.getPosY();
        double d2 = target.getPosZ() - this.getPosZ();
        double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        tridententity.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - this.world.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ENTITY_DROWNED_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.world.addEntity(tridententity);
    }

    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        if (this.getHeldItemMainhand().getItem() instanceof TridentItem) {
            attackEntityWithRangedAttackTrident(target, distanceFactor);
            return;
        }
        ItemStack itemstack = this.findAmmo(this.getHeldItem(ProjectileHelper.getHandWith(this, Items.BOW)));
        AbstractArrowEntity abstractarrowentity = this.fireArrow(itemstack, distanceFactor);
        if (this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.BowItem)
            abstractarrowentity = ((net.minecraft.item.BowItem) this.getHeldItemMainhand().getItem()).customArrow(abstractarrowentity);
        double d0 = target.getPosX() - this.getPosX();
        double d1 = target.getPosYHeight(0.3333333333333333D) - abstractarrowentity.getPosY();
        double d2 = target.getPosZ() - this.getPosZ();
        double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        abstractarrowentity.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - this.world.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.world.addEntity(abstractarrowentity);
    }

    protected AbstractArrowEntity fireArrow(ItemStack arrowStack, float distanceFactor) {
        return ProjectileHelper.fireArrow(this, arrowStack, distanceFactor);
    }

    public boolean func_230280_a_(ShootableItem p_230280_1_) {
        return p_230280_1_ == Items.BOW;
    }

    public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {
        if (slotIn == EquipmentSlotType.HEAD && !stack.isItemEqual(this.getItemStackFromSlot(EquipmentSlotType.HEAD))) {
            helmetSwapProgress = 5;
            this.world.setEntityState(this, (byte) 45);
        }
        if (slotIn == EquipmentSlotType.MAINHAND && !stack.isItemEqual(this.getItemStackFromSlot(EquipmentSlotType.MAINHAND))) {
            rightSwapProgress = 5;
            this.world.setEntityState(this, (byte) 46);
        }
        if (slotIn == EquipmentSlotType.OFFHAND && !stack.isItemEqual(this.getItemStackFromSlot(EquipmentSlotType.OFFHAND))) {
            leftSwapProgress = 5;
            this.world.setEntityState(this, (byte) 47);
        }
        super.setItemStackToSlot(slotIn, stack);
        if (!this.world.isRemote) {
            this.setCombatTask();
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        super.handleStatusUpdate(id);
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

    public boolean isActiveItemStackBlocking() {
        return this.getHeldItemMainhand().getItem() instanceof ShieldItem || this.getHeldItemOffhand().getItem() instanceof ShieldItem;
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        Entity trueSource = source.getTrueSource();
        if (trueSource != null && trueSource instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity) trueSource;
            if (!attacker.getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty()) {
                this.setItemStackToSlot(EquipmentSlotType.HEAD, attacker.getItemStackFromSlot(EquipmentSlotType.HEAD).copy());
            }
            if (!attacker.getItemStackFromSlot(EquipmentSlotType.OFFHAND).isEmpty()) {
                this.setItemStackToSlot(EquipmentSlotType.OFFHAND, attacker.getItemStackFromSlot(EquipmentSlotType.OFFHAND).copy());
            }
            if (!attacker.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty()) {
                this.setItemStackToSlot(EquipmentSlotType.MAINHAND, attacker.getItemStackFromSlot(EquipmentSlotType.MAINHAND).copy());
            }
        }
        return super.attackEntityFrom(source, amount);
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
        if (this.onGround && !this.wasOnGround) {

            for (int j = 0; j < 8; ++j) {
                float f = this.rand.nextFloat() * ((float) Math.PI * 2F);
                float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
                float f2 = MathHelper.sin(f) * 0.5F * f1;
                float f3 = MathHelper.cos(f) * 0.5F * f1;
                this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, new ItemStack(AMItemRegistry.MIMICREAM)), this.getPosX() + (double)f2, this.getPosY(), this.getPosZ() + (double)f3, 0.0D, 0.0D, 0.0D);
            }

            this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            this.squishAmount = -0.35F;
        } else if (!this.onGround && this.wasOnGround) {
            this.squishAmount = 2F;
        }
        if (this.getHeldItemOffhand().getItem().isFood() && this.getHealth() < this.getMaxHealth()) {
            if (eatingTicks < 100) {
                for (int i = 0; i < 3; i++) {
                    double d2 = this.rand.nextGaussian() * 0.02D;
                    double d0 = this.rand.nextGaussian() * 0.02D;
                    double d1 = this.rand.nextGaussian() * 0.02D;
                    this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, this.getHeldItem(Hand.OFF_HAND)), this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, this.getPosY() + this.getHeight() * 0.5F + (double) (this.rand.nextFloat() * this.getHeight() * 0.5F), this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, d0, d1, d2);
                }
                if (eatingTicks % 6 == 0) {
                    this.playSound(SoundEvents.ENTITY_GENERIC_EAT, this.getSoundVolume(), this.getSoundPitch());
                }
                eatingTicks++;
            }
            if (eatingTicks == 100) {
                this.playSound(SoundEvents.ENTITY_PLAYER_BURP, this.getSoundVolume(), this.getSoundPitch());
                this.getHeldItemOffhand().shrink(1);
                this.heal(5);
                eatingTicks = 0;
            }
        } else if (this.getHeldItemMainhand().getItem().isFood() && this.getHealth() < this.getMaxHealth()) {
            if (eatingTicks < 100) {
                for (int i = 0; i < 3; i++) {
                    double d2 = this.rand.nextGaussian() * 0.02D;
                    double d0 = this.rand.nextGaussian() * 0.02D;
                    double d1 = this.rand.nextGaussian() * 0.02D;
                    this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, this.getHeldItem(Hand.MAIN_HAND)), this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, this.getPosY() + this.getHeight() * 0.5F + (double) (this.rand.nextFloat() * this.getHeight() * 0.5F), this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, d0, d1, d2);
                }
                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, this.getSoundVolume(), this.getSoundPitch());
                if (eatingTicks % 6 == 0) {
                    this.playSound(SoundEvents.ENTITY_GENERIC_EAT, this.getSoundVolume(), this.getSoundPitch());
                }
                eatingTicks++;
            }
            if (eatingTicks == 100) {
                this.playSound(SoundEvents.ENTITY_PLAYER_BURP, this.getSoundVolume(), this.getSoundPitch());
                this.getHeldItemMainhand().shrink(1);
                this.heal(5);
            }
        } else {
            eatingTicks = 0;
        }
        this.wasOnGround = this.onGround;
        this.alterSquishAmount();
        LivingEntity livingentity = this.getAttackTarget();
        if (livingentity != null && this.getDistanceSq(livingentity) < 144D) {
            this.moveController.setMoveTo(livingentity.getPosX(), livingentity.getPosY(), livingentity.getPosZ(), this.moveController.getSpeed());
            this.wasOnGround = true;
        }
        if (this.dataManager.get(ATTACK_TICK) > 0) {
            if (this.dataManager.get(ATTACK_TICK) == 2 && this.getAttackTarget() != null && this.getDistance(this.getAttackTarget()) < 2.3D) {
                super.attackEntityAsMob(this.getAttackTarget());
            }
            this.dataManager.set(ATTACK_TICK, this.dataManager.get(ATTACK_TICK) - 1);
            if (attackProgress < 3F) {
                attackProgress++;
            }
        } else {
            if (attackProgress > 0F) {
                attackProgress--;
            }
        }

    }

    protected float getDropChance(EquipmentSlotType slotIn) {
        return 0;
    }

    private SoundEvent getSquishSound() {
        return SoundEvents.ENTITY_SLIME_SQUISH;
    }

    private SoundEvent getJumpSound() {
        return SoundEvents.ENTITY_SLIME_SQUISH;
    }

    protected void jump() {
        Vector3d vector3d = this.getMotion();
        this.setMotion(vector3d.x, this.getJumpUpwardsMotion(), vector3d.z);
        this.isAirBorne = true;
    }

    protected int getJumpDelay() {
        return this.rand.nextInt(20) + 10;
    }

    protected void alterSquishAmount() {
        this.squishAmount *= 0.6F;
    }

    public boolean shouldShoot() {
        return this.getHeldItemMainhand().getItem() instanceof ShootableItem || this.getHeldItemMainhand().getItem() instanceof TridentItem;
    }

    private class MimicubeMoveHelper extends MovementController {
        private final EntityMimicube slime;
        private float yRot;
        private int jumpDelay;
        private boolean isAggressive;

        public MimicubeMoveHelper(EntityMimicube slimeIn) {
            super(slimeIn);
            this.slime = slimeIn;
            this.yRot = 180.0F * slimeIn.rotationYaw / (float) Math.PI;
        }

        public void setDirection(float yRotIn, boolean aggressive) {
            this.yRot = yRotIn;
            this.isAggressive = aggressive;
        }

        public void setSpeed(double speedIn) {
            this.speed = speedIn;
            this.action = MovementController.Action.MOVE_TO;
        }

        public void tick() {
            if (this.mob.isOnGround()) {
                this.mob.setAIMoveSpeed((float) (this.speed * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                if (this.jumpDelay-- <= 0 && this.action != Action.WAIT) {
                    this.jumpDelay = this.slime.getJumpDelay();
                    if (this.mob.getAttackTarget() != null) {
                        this.jumpDelay /= 3;
                    }

                    this.slime.getJumpController().setJumping();
                    this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getSoundPitch());
                } else {
                    this.slime.moveStrafing = 0.0F;
                    this.slime.moveForward = 0.0F;
                    this.mob.setAIMoveSpeed(0.0F);
                }
            }
            super.tick();
        }
    }
}

