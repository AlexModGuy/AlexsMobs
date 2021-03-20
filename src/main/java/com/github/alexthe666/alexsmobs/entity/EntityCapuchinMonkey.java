package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.CapuchinAIMelee;
import com.github.alexthe666.alexsmobs.entity.ai.CapuchinAIRangedAttack;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAIFollowOwner;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class EntityCapuchinMonkey extends TameableEntity implements IAnimatedEntity, IFollower, ITargetsDroppedItems {

    private int animationTick;
    private Animation currentAnimation;
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityCapuchinMonkey.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COMMAND = EntityDataManager.createKey(EntityCapuchinMonkey.class, DataSerializers.VARINT);
    public static final Animation ANIMATION_THROW = Animation.create(12);
    public static final Animation ANIMATION_HEADTILT = Animation.create(15);
    public static final Animation ANIMATION_SCRATCH = Animation.create(20);
    public float prevSitProgress;
    public float sitProgress;
    public boolean forcedSit = false;
    private int sittingTime = 0;
    private int maxSitTime = 75;
    private static Ingredient temptationItems = Ingredient.fromItems(Items.EGG, AMItemRegistry.MAGGOT);
    public boolean attackDecision = false;//true for ranged, false for melee
    private boolean hasSlowed = false;
    private int rideCooldown = 0;

    protected EntityCapuchinMonkey(EntityType type, World worldIn) {
        super(type, worldIn);
        this.setPathPriority(PathNodeType.LEAVES, 0.0F);
    }

    public int getMaxSpawnedInChunk() {
        return 8;
    }

    public boolean isMaxGroupSize(int sizeIn) {
        return false;
    }


    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.capuchinMonkeySpawnRolls, this.getRNG(), spawnReasonIn);
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getTrueSource();
            if (entity != null && this.isTamed() && !(entity instanceof PlayerEntity) && !(entity instanceof AbstractArrowEntity)) {
                amount = (amount + 1.0F) / 4.0F;
            }
            return super.attackEntityFrom(source, amount);
        }
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new SitGoal(this));
        this.goalSelector.addGoal(3, new CapuchinAIMelee(this, 1, true));
        this.goalSelector.addGoal(3, new CapuchinAIRangedAttack(this, 1, 20, 15));
        this.goalSelector.addGoal(6, new TameableAIFollowOwner(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.1D, temptationItems.merge(ImmutableList.of(Ingredient.fromTag(ItemTags.getCollection().get(AMTagRegistry.BANANAS)))), true){
            public void tick() {
                super.tick();
                if (this.creature.getDistanceSq(this.closestPlayer) < 6.25D && this.creature.getRNG().nextInt(14) == 0) {
                    ((EntityCapuchinMonkey)this.creature).setAnimation(ANIMATION_HEADTILT);
                }
            }
        });
        this.goalSelector.addGoal(7, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 1.0D, 60));
        this.goalSelector.addGoal(10, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(4, (new HurtByTargetGoal(this, EntityCapuchinMonkey.class, EntityTossedItem.class)).setCallsForHelp());
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.CAPUCHIN_MONKEY_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.CAPUCHIN_MONKEY_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.CAPUCHIN_MONKEY_HURT;
    }


    public boolean isOnSameTeam(Entity entityIn) {
        if (this.isTamed()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) {
                return true;
            }
            if (entityIn instanceof TameableEntity) {
                return ((TameableEntity) entityIn).isOwner(livingentity);
            }
            if (livingentity != null) {
                return livingentity.isOnSameTeam(entityIn);
            }
        }

        return super.isOnSameTeam(entityIn);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 10.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.4F);
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("MonkeySitting", this.isSitting());
        compound.putBoolean("ForcedToSit", this.forcedSit);
        compound.putInt("Command", this.getCommand());
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setSitting(compound.getBoolean("MonkeySitting"));
        this.forcedSit = compound.getBoolean("ForcedToSit");
        this.setCommand(compound.getInt("Command"));
    }
    public void tick() {
        super.tick();
        this.prevSitProgress = this.sitProgress;
        if (this.isSitting() && sitProgress < 10) {
            sitProgress += 1;
        }
        if (!this.isSitting() && sitProgress > 0) {
            sitProgress -= 1;
        }
        if (isSitting() && !forcedSit && ++sittingTime > maxSitTime) {
            this.setSitting(false);
            sittingTime = 0;
            maxSitTime = 75 + rand.nextInt(50);
        }
        if (!world.isRemote && this.getAnimation() == NO_ANIMATION && !this.isSitting() && this.getCommand() != 1 && rand.nextInt(1500) == 0) {
            maxSitTime = 300 + rand.nextInt(250);
            this.setSitting(true);
        }
        this.stepHeight = 2;
        if (!forcedSit && this.isSitting() && (this.getAttackTarget() != null || this.getCommand() == 1)) {
            this.setSitting(false);
        }
        if(!world.isRemote && this.getAttackTarget() != null && this.getAnimation() == ANIMATION_SCRATCH && this.getAnimationTick() == 10) {
            float f1 = this.rotationYaw * ((float)Math.PI / 180F);
            this.setMotion(this.getMotion().add((double)(-MathHelper.sin(f1) * 0.3F), 0.0D, (double)(MathHelper.cos(f1) * 0.3F)));
            getAttackTarget().applyKnockback(1F, getAttackTarget().getPosX() - this.getPosX(), getAttackTarget().getPosZ() - this.getPosZ());
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
            this.setAttackDecision(this.getAttackTarget());
        }
        if(!world.isRemote && this.getAttackTarget() != null && this.getAttackTarget().isAlive() && this.getAnimation() == ANIMATION_THROW && this.getAnimationTick() == 5) {
            Vector3d vector3d = this.getAttackTarget().getMotion();
            double d0 = this.getAttackTarget().getPosX() + vector3d.x - this.getPosX();
            double d1 = this.getAttackTarget().getPosYEye() - (double)1.1F - this.getPosY();
            double d2 = this.getAttackTarget().getPosZ() + vector3d.z - this.getPosZ();
            float f = MathHelper.sqrt(d0 * d0 + d2 * d2);
            EntityTossedItem potionentity = new EntityTossedItem(this.world, this);
            potionentity.rotationPitch -= -20.0F;
            potionentity.shoot(d0, d1 + (double)(f * 0.2F), d2, 0.75F, 8.0F);
            if (!this.isSilent()) {
                this.world.playSound((PlayerEntity)null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_WITCH_THROW, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
            }
            this.world.addEntity(potionentity);
            this.setAttackDecision(this.getAttackTarget());
        }
        if(rideCooldown > 0){
            rideCooldown--;
        }
        if(!world.isRemote && getAnimation() == NO_ANIMATION && this.getRNG().nextInt(300) == 0){
            setAnimation(ANIMATION_HEADTILT);
        }
        if(!world.isRemote && this.isSitting()){
            this.getNavigator().clearPath();
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        if(this.getAnimation() == NO_ANIMATION){
            this.setAnimation(ANIMATION_SCRATCH);
        }
        return true;
    }

    public void travel(Vector3d vec3d) {
        if (this.isSitting()) {
            if (this.getNavigator().getPath() != null) {
                this.getNavigator().clearPath();
            }
            vec3d = Vector3d.ZERO;
        }
        super.travel(vec3d);
    }


    public void updateRidden() {
        Entity entity = this.getRidingEntity();
        if (this.isPassenger() && !entity.isAlive()) {
            this.stopRiding();
        } else if(isTamed() && entity instanceof LivingEntity && isOwner((LivingEntity) entity)){
            this.setMotion(0, 0, 0);
            this.tick();
            if (this.isPassenger()) {
                Entity mount = this.getRidingEntity();
                if (mount instanceof PlayerEntity) {
                    this.renderYawOffset = ((LivingEntity) mount).renderYawOffset;
                    this.rotationYaw = ((LivingEntity) mount).rotationYaw;
                    this.rotationYawHead = ((LivingEntity) mount).rotationYawHead;
                    this.prevRotationYaw = ((LivingEntity) mount).rotationYawHead;
                    float radius = 0F;
                    float angle = (0.01745329251F * (((LivingEntity) mount).renderYawOffset - 180F));
                    double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                    double extraZ = radius * MathHelper.cos(angle);
                    this.setPosition(mount.getPosX() + extraX, Math.max(mount.getPosY() + mount.getHeight() + 0.1, mount.getPosY()), mount.getPosZ() + extraZ);
                    attackDecision = true;
                    if (!mount.isAlive() || rideCooldown == 0 && mount.isSneaking()) {
                        this.dismount();
                        attackDecision = false;
                    }
                }

            }
        }else{
            super.updateRidden();
        }

    }

    public void setAttackDecision(Entity target){
        if(target instanceof MonsterEntity){
            attackDecision = true;
        }else{
            attackDecision = !attackDecision;
        }
    }

    public void setCommand(int command) {
        this.dataManager.set(COMMAND, Integer.valueOf(command));
    }

    public int getCommand() {
        return this.dataManager.get(COMMAND).intValue();
    }

    public void setSitting(boolean sit) {
        this.dataManager.set(SITTING, Boolean.valueOf(sit));
    }

    public boolean isSitting() {
        return this.dataManager.get(SITTING).booleanValue();
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(COMMAND, Integer.valueOf(0));
        this.dataManager.register(SITTING, Boolean.valueOf(false));
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return AMEntityRegistry.CAPUCHIN_MONKEY.create(p_241840_1_);
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || super.isInvulnerableTo(source);
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        ActionResultType type = super.func_230254_b_(player, hand);
        if(!isTamed() && EntityGorilla.isBanana(itemstack)){
            this.consumeItemFromStack(player, itemstack);
            if(getRNG().nextInt(5) == 0){
                this.setTamedBy(player);
                this.world.setEntityState(this, (byte)7);
            }else{
                this.world.setEntityState(this, (byte)6);
            }
            return ActionResultType.SUCCESS;
        }
        if(isTamed() && (EntityGorilla.isBanana(itemstack) || temptationItems.test(itemstack) && !isBreedingItem(itemstack)) && this.getHealth() < this.getMaxHealth()){
            this.consumeItemFromStack(player, itemstack);
            this.playSound(SoundEvents.ENTITY_CAT_EAT, this.getSoundVolume(), this.getSoundPitch());
            this.heal(5);
            return ActionResultType.SUCCESS;
        }
        if(type != ActionResultType.SUCCESS && isTamed() && isOwner(player) && !isBreedingItem(itemstack) && !EntityGorilla.isBanana(itemstack) && !temptationItems.test(itemstack)){
            if(player.isSneaking() && player.getPassengers().isEmpty()){
                this.startRiding(player);
                rideCooldown = 20;
                return ActionResultType.SUCCESS;
            }else{
                this.setCommand(this.getCommand() + 1);
                if(this.getCommand() == 3){
                    this.setCommand(0);
                }
                player.sendStatusMessage(new TranslationTextComponent("entity.alexsmobs.all.command_" + this.getCommand(), this.getName()), true);
                boolean sit = this.getCommand() == 2;
                if(sit){
                    this.forcedSit = true;
                    this.setSitting(true);
                    return ActionResultType.SUCCESS;
                }else{
                    this.forcedSit = false;
                    this.setSitting(false);
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return type;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_THROW, ANIMATION_SCRATCH};
    }

    @Override
    public boolean shouldFollow() {
        return this.getCommand() == 1;
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return temptationItems.test(stack) || EntityGorilla.isBanana(stack);
    }

    public boolean isBreedingItem(ItemStack stack) {
        Item item = stack.getItem();
        return isTamed() && item == AMItemRegistry.MAGGOT;
    }

    @Override
    public void onGetItem(ItemEntity e) {
        this.heal(5);
        this.playSound(SoundEvents.ENTITY_CAT_EAT, this.getSoundVolume(), this.getSoundPitch());
        if(EntityGorilla.isBanana(e.getItem())){
            if(getRNG().nextInt(4) == 0){
                this.entityDropItem(new ItemStack(AMBlockRegistry.BANANA_PEEL));
            }
            if(e.getThrowerId() != null && !this.isTamed()){
                if(getRNG().nextInt(5) == 0){
                    this.setTamed(true);
                    this.setOwnerId(e.getThrowerId());
                    this.world.setEntityState(this, (byte)7);
                }else{
                    this.world.setEntityState(this, (byte)6);
                }
            }
        }
    }

    public static <T extends MobEntity> boolean canCapuchinSpawn(EntityType<EntityCapuchinMonkey> gorilla, IWorld worldIn, SpawnReason reason, BlockPos p_223317_3_, Random random) {
        BlockState blockstate = worldIn.getBlockState(p_223317_3_.down());
        return (blockstate.isIn(BlockTags.LEAVES) || blockstate.isIn(Blocks.GRASS_BLOCK) || blockstate.isIn(BlockTags.LOGS) || blockstate.isIn(Blocks.AIR)) && worldIn.getLightSubtracted(p_223317_3_, 0) > 8;
    }
}
