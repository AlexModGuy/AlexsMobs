package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class EntityPlatypus extends AnimalEntity implements ISemiAquatic, ITargetsDroppedItems {

    private static final DataParameter<Boolean> SENSING = EntityDataManager.createKey(EntityPlatypus.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SENSING_VISUAL = EntityDataManager.createKey(EntityPlatypus.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DIGGING = EntityDataManager.createKey(EntityPlatypus.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> FEDORA = EntityDataManager.createKey(EntityPlatypus.class, DataSerializers.BOOLEAN);
    public float prevInWaterProgress;
    public float inWaterProgress;
    public float prevDigProgress;
    public float digProgress;
    public boolean superCharged = false;
    private boolean isLandNavigator;
    private int swimTimer = -1000;

    protected EntityPlatypus(EntityType type, World world) {
        super(type, world);
        this.setPathPriority(PathNodeType.WATER, 0.0F);
        this.setPathPriority(PathNodeType.WATER_BORDER, 0.0F);
        switchNavigator(false);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 10.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 16.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    protected ItemStack getFishBucket() {
        ItemStack stack = new ItemStack(AMItemRegistry.PLATYPUS_BUCKET);
        CompoundNBT platTag = new CompoundNBT();
        this.writeAdditional(platTag);
        stack.getOrCreateTag().put("PlatypusData", platTag);
        return stack;
    }

    public ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_) {
        ItemStack itemstack = p_230254_1_.getHeldItem(p_230254_2_);
        boolean redstone = itemstack.getItem() == Items.REDSTONE || itemstack.getItem() == Items.REDSTONE_BLOCK;
        if(itemstack.getItem() == AMItemRegistry.FEDORA && !this.hasFedora()){
            if (!p_230254_1_.isCreative()) {
                itemstack.shrink(1);
            }
            this.setFedora(true);
            return ActionResultType.func_233537_a_(this.world.isRemote);
        }
        if (redstone && !this.isSensing()) {
            superCharged = itemstack.getItem() == Items.REDSTONE_BLOCK;
            if (!p_230254_1_.isCreative()) {
                itemstack.shrink(1);
            }
            this.setSensing(true);
            return ActionResultType.func_233537_a_(this.world.isRemote);
        }
        if (itemstack.getItem() == Items.WATER_BUCKET && this.isAlive()) {
            this.playSound(SoundEvents.ITEM_BUCKET_FILL_FISH, 1.0F, 1.0F);
            itemstack.shrink(1);
            ItemStack itemstack1 = this.getFishBucket();
            if (!this.world.isRemote) {
                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity) p_230254_1_, itemstack1);
            }

            if (itemstack.isEmpty()) {
                p_230254_1_.setHeldItem(p_230254_2_, itemstack1);
            } else if (!p_230254_1_.inventory.addItemStackToInventory(itemstack1)) {
                p_230254_1_.dropItem(itemstack1, false);
            }

            this.remove();
            return ActionResultType.func_233537_a_(this.world.isRemote);
        } else {
            return super.func_230254_b_(p_230254_1_, p_230254_2_);
        }
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BreatheAirGoal(this));
        this.goalSelector.addGoal(1, new AnimalAIFindWater(this));
        this.goalSelector.addGoal(1, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(2, new BreedGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new PanicGoal(this, 1.1D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, Ingredient.fromItems(Items.REDSTONE, Items.REDSTONE_BLOCK), false){
            public void startExecuting() {
                super.startExecuting();
                EntityPlatypus.this.setSensingVisual(true);
            }

            public boolean shouldExecute(){
                return super.shouldExecute() && !EntityPlatypus.this.isSensing();
            }

            public void resetTask() {
                super.resetTask();
                EntityPlatypus.this.setSensingVisual(false);
            }
        });
        this.goalSelector.addGoal(5, new TemptGoal(this, 1.1D, Ingredient.fromTag(ItemTags.getCollection().get(AMTagRegistry.PLATYPUS_FOODSTUFFS)), false){
            public boolean shouldExecute(){
                return super.shouldExecute() && !EntityPlatypus.this.isSensing();
            }
        });
        this.goalSelector.addGoal(5, new PlatypusAIDigForItems(this));
        this.goalSelector.addGoal(6, new AnimalAIRandomSwimming(this, 1.0D, 30));
        this.goalSelector.addGoal(7, new RandomWalkingGoal(this, 1.0D, 60));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false, false, 40, 15){
            public boolean shouldExecute(){
                return super.shouldExecute() && !EntityPlatypus.this.isSensing();
            }

            public boolean shouldContinueExecuting(){
                return super.shouldContinueExecuting() && !EntityPlatypus.this.isSensing();
            }
        });
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean prev = super.attackEntityFrom(source, amount);
        if(prev && source.getImmediateSource() instanceof LivingEntity){
            LivingEntity entity = (LivingEntity)source.getImmediateSource();
            entity.addPotionEffect(new EffectInstance(Effects.POISON, 100));
        }
        return prev;
    }

    public boolean isPerry() {
        String s = TextFormatting.getTextWithoutFormattingCodes(this.getName().getString());
        return s != null && s.toLowerCase().contains("perry");
    }


    public int getMaxAir() {
        return 4800;
    }

    protected int determineNextAir(int currentAir) {
        return this.getMaxAir();
    }

    public void spawnGroundEffects() {
        float radius = 0.3F;
        for (int i1 = 0; i1 < 3; i1++) {
            double motionX = getRNG().nextGaussian() * 0.07D;
            double motionY = getRNG().nextGaussian() * 0.07D;
            double motionZ = getRNG().nextGaussian() * 0.07D;
            float angle = (0.01745329251F * this.renderYawOffset) + i1;
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraY = 0.8F;
            double extraZ = radius * MathHelper.cos(angle);
            BlockPos ground = this.getPositionUnderneath();
            BlockState BlockState = this.world.getBlockState(ground);
            if (BlockState.getMaterial() != Material.AIR && BlockState.getMaterial() != Material.WATER) {
                if (world.isRemote) {
                    world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, BlockState), true, this.getPosX() + extraX, ground.getY() + extraY, this.getPosZ() + extraZ, motionX, motionY, motionZ);
                }
            }
        }
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason
            reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.setAir(this.getMaxAir());
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public boolean isPushedByWater() {
        return false;
    }

    public void travel(Vector3d travelVector) {
        if (this.isServerWorld() && this.isInWater()) {
            this.moveRelative(this.getAIMoveSpeed(), travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(0.9D));
        } else {
            super.travel(travelVector);
        }
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(DIGGING, false);
        this.dataManager.register(SENSING, Boolean.valueOf(false));
        this.dataManager.register(SENSING_VISUAL, Boolean.valueOf(false));
        this.dataManager.register(FEDORA, false);
    }

    protected void dropInventory() {
        super.dropInventory();
        if (this.hasFedora()) {
            this.entityDropItem(AMItemRegistry.FEDORA);
        }

    }

    public boolean isSensing() {
        return this.dataManager.get(SENSING).booleanValue();
    }

    public void setSensing(boolean sensing) {
        this.dataManager.set(SENSING, Boolean.valueOf(sensing));
    }

    public boolean isSensingVisual() {
        return this.dataManager.get(SENSING_VISUAL).booleanValue();
    }

    public void setSensingVisual(boolean sensing) {
        this.dataManager.set(SENSING_VISUAL, Boolean.valueOf(sensing));
    }

    public boolean hasFedora() {
        return this.dataManager.get(FEDORA).booleanValue();
    }

    public void setFedora(boolean sensing) {
        this.dataManager.set(FEDORA, Boolean.valueOf(sensing));
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("Fedora", this.hasFedora());
        compound.putBoolean("Sensing", this.isSensing());
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setFedora(compound.getBoolean("Fedora"));
        this.setSensing(compound.getBoolean("Sensing"));
    }

        public void tick() {
        super.tick();
        prevInWaterProgress = inWaterProgress;
        prevDigProgress = digProgress;
        boolean dig = isDigging() && isInWaterOrBubbleColumn();
        if (dig && digProgress < 5F) {
            digProgress++;
        }
        if (!dig && digProgress > 0F) {
            digProgress--;
        }
        if (this.isInWaterOrBubbleColumn() && inWaterProgress < 5F) {
            inWaterProgress++;
        }
        if (!this.isInWaterOrBubbleColumn() && inWaterProgress > 0F) {
            inWaterProgress--;
        }
        if (this.isInWaterOrBubbleColumn() && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (!this.isInWaterOrBubbleColumn() && !this.isLandNavigator) {
            switchNavigator(true);
        }
        if (this.onGround && isDigging()) {
            spawnGroundEffects();
        }
        if (inWaterProgress > 0) {
            this.stepHeight = 1;
        } else {
            this.stepHeight = 0.6F;
        }
        if (!world.isRemote) {
            if (isInWater()) {
                swimTimer++;
            } else {
                swimTimer--;
            }
        }
        if (this.isAlive() && (this.isSensing() || this.isSensingVisual())) {
            for (int j = 0; j < 2; ++j) {
                float radius = this.getWidth() * 0.65F;
                float angle = (0.01745329251F * this.renderYawOffset);
                double extraX = (radius * (1.5F + rand.nextFloat() * 0.3F)) * MathHelper.sin((float) (Math.PI + angle)) + (rand.nextFloat() - 0.5F) + this.getMotion().x * 2F;
                double extraZ = (radius * (1.5F + rand.nextFloat() * 0.3F)) * MathHelper.cos(angle) + (rand.nextFloat() - 0.5F) + this.getMotion().z * 2F;
                double actualX = radius * MathHelper.sin((float) (Math.PI + angle));
                double actualZ = radius * MathHelper.cos(angle);
                double motX = actualX - extraX;
                double motZ = actualZ - extraZ;
                this.world.addParticle(AMParticleRegistry.PLATYPUS_SENSE, this.getPosX() + extraX, this.getHeight() * 0.3F + this.getPosY(), this.getPosZ() + extraZ, motX * 0.1F, 0, motZ * 0.1F);
            }
        }
    }

    public boolean isDigging() {
        return this.dataManager.get(DIGGING);
    }

    public void setDigging(boolean digging) {
        this.dataManager.set(DIGGING, digging);
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveController = new MovementController(this);
            this.navigator = new GroundPathNavigatorWide(this, world);
            this.isLandNavigator = true;
        } else {
            this.moveController = new AnimalSwimMoveControllerSink(this, 1.2F, 1.6F);
            this.navigator = new SemiAquaticPathNavigator(this, world);
            this.isLandNavigator = false;
        }
    }

    @Override
    public boolean shouldEnterWater() {
        return this.getRevengeTarget() != null || swimTimer <= -1000 || this.isSensing();
    }

    @Override
    public boolean shouldLeaveWater() {
        return swimTimer > 600 && !this.isSensing();
    }

    @Override
    public boolean shouldStopMoving() {
        return this.isDigging();
    }

    @Override
    public int getWaterSearchRange() {
        return 10;
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        return AMEntityRegistry.PLATYPUS.create(serverWorld);
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return !this.isSensing() && ItemTags.getCollection().get(AMTagRegistry.PLATYPUS_FOODSTUFFS).contains(stack.getItem());
    }

    @Override
    public void onGetItem(ItemEntity e) {
        this.playSound(SoundEvents.ENTITY_CAT_EAT, this.getSoundVolume(), this.getSoundPitch());
        if(e.getItem().getItem() == Items.REDSTONE || e.getItem().getItem() == Items.REDSTONE_BLOCK){
            superCharged = e.getItem().getItem().getItem() == Items.REDSTONE_BLOCK;
            this.setSensing(true);
        }else{
            this.heal(6);
        }
    }
}
