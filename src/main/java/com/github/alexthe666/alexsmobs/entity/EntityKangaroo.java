package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.message.MessageKangarooEat;
import com.github.alexthe666.alexsmobs.message.MessageKangarooInventorySync;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.DispenserContainer;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

public class EntityKangaroo extends TameableEntity implements IInventoryChangedListener, IAnimatedEntity, IFollower {

    public static final Animation ANIMATION_EAT_GRASS = Animation.create(30);
    public static final Animation ANIMATION_KICK = Animation.create(15);
    public static final Animation ANIMATION_PUNCH_R = Animation.create(13);
    public static final Animation ANIMATION_PUNCH_L = Animation.create(13);
    private static final DataParameter<Boolean> STANDING = EntityDataManager.createKey(EntityKangaroo.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityKangaroo.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COMMAND = EntityDataManager.createKey(EntityKangaroo.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> VISUAL_FLAG = EntityDataManager.createKey(EntityKangaroo.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> POUCH_TICK = EntityDataManager.createKey(EntityKangaroo.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HELMET_INDEX = EntityDataManager.createKey(EntityKangaroo.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> SWORD_INDEX = EntityDataManager.createKey(EntityKangaroo.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> CHEST_INDEX = EntityDataManager.createKey(EntityKangaroo.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> FORCED_SIT = EntityDataManager.createKey(EntityKangaroo.class, DataSerializers.BOOLEAN);
    public float prevPouchProgress;
    public float pouchProgress;
    public float sitProgress;
    public float prevSitProgress;
    public float standProgress;
    public float prevStandProgress;
    public float totalMovingProgress;
    public float prevTotalMovingProgress;
    public int maxStandTime = 75;
    public Inventory kangarooInventory;
    private int animationTick;
    private Animation currentAnimation;
    private int jumpTicks;
    private int jumpDuration;
    private boolean wasOnGround;
    private int currentMoveTypeDuration;
    private int standingTime = 0;
    private int sittingTime = 0;
    private int maxSitTime = 75;
    private int eatCooldown = 0;
    private int carrotFeedings = 0;
    private int clientArmorCooldown = 0;

    protected EntityKangaroo(EntityType type, World world) {
        super(type, world);
        initKangarooInventory();
        this.jumpController = new EntityKangaroo.JumpHelperController(this);
        this.moveController = new EntityKangaroo.MoveHelperController(this);

    }

    public static <T extends MobEntity> boolean canKangarooSpawn(EntityType<? extends AnimalEntity> animal, IWorld worldIn, SpawnReason reason, BlockPos pos, Random random) {
        boolean spawnBlock = BlockTags.getCollection().get(AMTagRegistry.KANGAROO_SPAWNS).contains(worldIn.getBlockState(pos.down()).getBlock());
        return spawnBlock && worldIn.getLightSubtracted(pos, 0) > 8;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 22.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.5F).createMutableAttribute(Attributes.ATTACK_DAMAGE, 4F);
    }

    protected void updateLeashedState() {
        super.updateLeashedState();
        Entity lvt_1_1_ = this.getLeashHolder();
        if (lvt_1_1_ != null && lvt_1_1_.world == this.world) {
            this.setHomePosAndDistance(lvt_1_1_.getPosition(), 5);
            float lvt_2_1_ = this.getDistance(lvt_1_1_);
            if (this.isSitting()) {
                if (lvt_2_1_ > 10.0F) {
                    this.clearLeashed(true, true);
                }

                return;
            }

            this.onLeashDistance(lvt_2_1_);
            if (lvt_2_1_ > 10.0F) {
                this.clearLeashed(true, true);
                this.goalSelector.disableFlag(Goal.Flag.MOVE);
            } else if (lvt_2_1_ > 6.0F) {
                double lvt_3_1_ = (lvt_1_1_.getPosX() - this.getPosX()) / (double) lvt_2_1_;
                double lvt_5_1_ = (lvt_1_1_.getPosY() - this.getPosY()) / (double) lvt_2_1_;
                double lvt_7_1_ = (lvt_1_1_.getPosZ() - this.getPosZ()) / (double) lvt_2_1_;
                this.setMotion(this.getMotion().add(Math.copySign(lvt_3_1_ * lvt_3_1_ * 0.4D, lvt_3_1_), Math.copySign(lvt_5_1_ * lvt_5_1_ * 0.4D, lvt_5_1_), Math.copySign(lvt_7_1_ * lvt_7_1_ * 0.4D, lvt_7_1_)));
            } else {
                this.goalSelector.enableFlag(Goal.Flag.MOVE);
                float lvt_3_2_ = 2.0F;
                try {
                    Vector3d lvt_4_1_ = (new Vector3d(lvt_1_1_.getPosX() - this.getPosX(), lvt_1_1_.getPosY() - this.getPosY(), lvt_1_1_.getPosZ() - this.getPosZ())).normalize().scale(Math.max(lvt_2_1_ - 2.0F, 0.0F));
                    this.getNavigator().tryMoveToXYZ(this.getPosX() + lvt_4_1_.x, this.getPosY() + lvt_4_1_.y, this.getPosZ() + lvt_4_1_.z, this.followLeashSpeed());
                } catch (Exception e) {

                }
            }
        }

    }

    public boolean forcedSit() {
        return dataManager.get(FORCED_SIT);
    }

    public boolean isRoger() {
        String s = TextFormatting.getTextWithoutFormattingCodes(this.getName().getString());
        return s != null && s.toLowerCase().equals("roger");
    }
    
    public boolean isKangarooJack() {
        String s = TextFormatting.getTextWithoutFormattingCodes(this.getName().getString());
        return s != null && (s.toLowerCase().contains("kangaroo") && s.toLowerCase().contains("jack");
    }
    
    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.emuSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.KANGAROO_IDLE;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.KANGAROO_IDLE;
    }

    private void initKangarooInventory() {
        Inventory animalchest = this.kangarooInventory;
        this.kangarooInventory = new Inventory(9) {
            public void closeInventory(PlayerEntity player) {
                EntityKangaroo.this.dataManager.set(POUCH_TICK, 10);
                EntityKangaroo.this.resetKangarooSlots();
            }
        };
        kangarooInventory.addListener(this);
        if (animalchest != null) {
            int i = Math.min(animalchest.getSizeInventory(), this.kangarooInventory.getSizeInventory());
            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = animalchest.getStackInSlot(j);
                if (!itemstack.isEmpty()) {
                    this.kangarooInventory.setInventorySlotContents(j, itemstack.copy());
                }
            }
            resetKangarooSlots();
        }

    }


    protected void dropInventory() {
        super.dropInventory();
        for (int i = 0; i < kangarooInventory.getSizeInventory(); i++) {
            this.entityDropItem(kangarooInventory.getStackInSlot(i));
        }
        kangarooInventory.clear();
    }

    public ActionResultType getEntityInteractionResult(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        ActionResultType type = super.getEntityInteractionResult(player, hand);
        if (!isTamed() && item == Items.CARROT && item == Items.GOLDEN_CARROT) {
            this.consumeItemFromStack(player, itemstack);
            this.playSound(SoundEvents.ENTITY_HORSE_EAT, this.getSoundVolume(), this.getSoundPitch());
            carrotFeedings++;
            if (carrotFeedings > 10 && getRNG().nextInt(2) == 0 || carrotFeedings > 15) {
                this.setTamedBy(player);
                this.world.setEntityState(this, (byte) 7);
            } else {
                this.world.setEntityState(this, (byte) 6);
            }
            return ActionResultType.SUCCESS;
        }
        if (isTamed() && this.getHealth() < this.getMaxHealth() && item.isFood() && item.getFood() != null && !item.getFood().isMeat()) {
            this.consumeItemFromStack(player, itemstack);
            this.playSound(SoundEvents.ENTITY_HORSE_EAT, this.getSoundVolume(), this.getSoundPitch());
            this.heal(item.getFood().getHealing());
            return ActionResultType.SUCCESS;
        }
        if (type != ActionResultType.SUCCESS && isTamed() && isOwner(player) && !isBreedingItem(itemstack)) {
            if (player.isSneaking()) {
                this.openGUI(player);
                this.removePassengers();
                this.dataManager.set(POUCH_TICK, -1);
                return ActionResultType.SUCCESS;
            } else {
                this.setCommand(this.getCommand() + 1);
                if (this.getCommand() == 3) {
                    this.setCommand(0);
                }
                player.sendStatusMessage(new TranslationTextComponent("entity.alexsmobs.all.command_" + this.getCommand(), this.getName()), true);
                boolean sit = this.getCommand() == 2;
                if (sit) {
                    this.dataManager.set(FORCED_SIT, true);
                    this.setSitting(true);
                    return ActionResultType.SUCCESS;
                } else {
                    this.dataManager.set(FORCED_SIT, false);
                    maxSitTime = 0;
                    this.setSitting(false);
                    return ActionResultType.SUCCESS;
                }
            }
        }

        return type;
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("KangarooSitting", this.isSitting());
        compound.putBoolean("KangarooSittingForced", this.forcedSit());
        compound.putBoolean("Standing", this.isStanding());
        compound.putInt("Command", this.getCommand());
        compound.putInt("HelmetInvIndex", this.dataManager.get(HELMET_INDEX));
        compound.putInt("SwordInvIndex", this.dataManager.get(SWORD_INDEX));
        compound.putInt("ChestInvIndex", this.dataManager.get(CHEST_INDEX));
        if (kangarooInventory != null) {
            ListNBT nbttaglist = new ListNBT();
            for (int i = 0; i < this.kangarooInventory.getSizeInventory(); ++i) {
                ItemStack itemstack = this.kangarooInventory.getStackInSlot(i);
                if (!itemstack.isEmpty()) {
                    CompoundNBT CompoundNBT = new CompoundNBT();
                    CompoundNBT.putByte("Slot", (byte) i);
                    itemstack.write(CompoundNBT);
                    nbttaglist.add(CompoundNBT);
                }
            }
            compound.put("Items", nbttaglist);
        }
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setSitting(compound.getBoolean("KangarooSitting"));
        this.dataManager.set(FORCED_SIT, compound.getBoolean("KangarooSittingForced"));
        this.setStanding(compound.getBoolean("Standing"));
        this.setCommand(compound.getInt("Command"));
        this.dataManager.set(HELMET_INDEX, compound.getInt("HelmetInvIndex"));
        this.dataManager.set(SWORD_INDEX, compound.getInt("SwordInvIndex"));
        this.dataManager.set(CHEST_INDEX, compound.getInt("ChestInvIndex"));
        if (kangarooInventory != null) {
            ListNBT nbttaglist = compound.getList("Items", 10);
            this.initKangarooInventory();
            for (int i = 0; i < nbttaglist.size(); ++i) {
                CompoundNBT CompoundNBT = nbttaglist.getCompound(i);
                int j = CompoundNBT.getByte("Slot") & 255;
                this.kangarooInventory.setInventorySlotContents(j, ItemStack.read(CompoundNBT));
            }
        } else {
            ListNBT nbttaglist = compound.getList("Items", 10);
            this.initKangarooInventory();
            for (int i = 0; i < nbttaglist.size(); ++i) {
                CompoundNBT CompoundNBT = nbttaglist.getCompound(i);
                int j = CompoundNBT.getByte("Slot") & 255;
                this.initKangarooInventory();
                this.kangarooInventory.setInventorySlotContents(j, ItemStack.read(CompoundNBT));
            }
        }
        resetKangarooSlots();
    }

    public void openGUI(PlayerEntity playerEntity) {
        if (!this.world.isRemote && (!this.isPassenger(playerEntity))) {
            NetworkHooks.openGui((ServerPlayerEntity) playerEntity, new INamedContainerProvider() {
                @Override
                public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
                    return new DispenserContainer(p_createMenu_1_, p_createMenu_2_, kangarooInventory);
                }

                @Override
                public ITextComponent getDisplayName() {
                    return new TranslationTextComponent("entity.alexsmobs.kangaroo.pouch");
                }
            });
        }
    }

    public boolean isSitting() {
        return this.dataManager.get(SITTING).booleanValue();
    }

    public void setSitting(boolean sit) {
        this.dataManager.set(SITTING, Boolean.valueOf(sit));
    }

    public boolean isStanding() {
        return this.dataManager.get(STANDING).booleanValue();
    }

    public void setStanding(boolean standing) {
        this.dataManager.set(STANDING, Boolean.valueOf(standing));
    }

    public int getCommand() {
        return this.dataManager.get(COMMAND).intValue();
    }

    public void setCommand(int command) {
        this.dataManager.set(COMMAND, Integer.valueOf(command));
    }

    public int getVisualFlag() {
        return this.dataManager.get(VISUAL_FLAG).intValue();
    }

    public void setVisualFlag(int visualFlag) {
        this.dataManager.set(VISUAL_FLAG, Integer.valueOf(visualFlag));
    }


    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(STANDING, Boolean.valueOf(false));
        this.dataManager.register(SITTING, Boolean.valueOf(false));
        this.dataManager.register(FORCED_SIT, Boolean.valueOf(false));
        this.dataManager.register(COMMAND, Integer.valueOf(0));
        this.dataManager.register(VISUAL_FLAG, Integer.valueOf(0));
        this.dataManager.register(POUCH_TICK, Integer.valueOf(0));
        this.dataManager.register(CHEST_INDEX, Integer.valueOf(-1));
        this.dataManager.register(HELMET_INDEX, Integer.valueOf(-1));
        this.dataManager.register(SWORD_INDEX, Integer.valueOf(-1));
    }

    @Override
    protected PathNavigator createNavigator(World worldIn) {
        return new GroundPathNavigatorWide(this, worldIn, 2F);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SitGoal(this));
        this.goalSelector.addGoal(1, new KangarooAIMelee(this, 1.2D, false));
        this.goalSelector.addGoal(2, new SwimGoal(this));
        this.goalSelector.addGoal(2, new TameableAIFollowOwner(this, 1.2D, 5.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1D));
        this.goalSelector.addGoal(4, new AnimalAIRideParent(this, 1.25D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, Ingredient.fromItems(Items.CARROT), false));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, Ingredient.fromItems(Items.GOLDEN_CARROT), false));
        this.goalSelector.addGoal(5, new AnimalAIWanderRanged(this, 110, 1.2D, 10, 7));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 10.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, (new AnimalAIHurtByTargetNotBaby(this)));
    }

    protected boolean canFitPassenger(Entity passenger) {
        return super.canFitPassenger(passenger) && this.dataManager.get(POUCH_TICK) == 0;
    }


    public double getMountedYOffset() {
        return (double) this.getHeight() * 0.35F;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        updateClientInventory();
    }

    public void tick() {
        super.tick();
        boolean moving = this.getMotion().lengthSquared() > 0.03D;
        int pouchTick = this.dataManager.get(POUCH_TICK);
        this.prevTotalMovingProgress = totalMovingProgress;
        this.prevPouchProgress = pouchProgress;
        this.prevSitProgress = sitProgress;
        this.prevStandProgress = standProgress;
        if (this.isSitting() && sitProgress < 5) {
            sitProgress += 1;
        }
        if (eatCooldown > 0) {
            eatCooldown--;
        }
        if (!this.isSitting() && sitProgress > 0) {
            sitProgress -= 1;
        }
        if (this.isStanding() && standProgress < 5) {
            standProgress += 1;
        }
        if (!this.isStanding() && standProgress > 0) {
            standProgress -= 1;
        }
        if (moving && totalMovingProgress < 5) {
            totalMovingProgress += 1;
        }
        if (!moving && totalMovingProgress > 0) {
            totalMovingProgress -= 1;
        }
        if (pouchTick != 0 && pouchProgress < 5) {
            pouchProgress += 1;
        }
        if (pouchTick == 0 && pouchProgress > 0) {
            pouchProgress -= 1;
        }
        if (pouchTick > 0) {
            this.dataManager.set(POUCH_TICK, pouchTick - 1);
        }
        if (isStanding() && ++standingTime > maxStandTime) {
            this.setStanding(false);
            standingTime = 0;
            maxStandTime = 75 + rand.nextInt(50);
        }
        if (isSitting() && !forcedSit() && ++sittingTime > maxSitTime) {
            this.setSitting(false);
            sittingTime = 0;
            maxSitTime = 75 + rand.nextInt(50);
        }
        if (!world.isRemote && this.getAnimation() == NO_ANIMATION && this.getCommand() != 1 && !this.isStanding() && !this.isSitting() && rand.nextInt(1500) == 0) {
            maxSitTime = 500 + rand.nextInt(350);
            this.setSitting(true);
        }
        if (!forcedSit() && this.isSitting() && (this.getAttackTarget() != null || this.isStanding())) {
            this.setSitting(false);
        }
        if (this.getAnimation() == NO_ANIMATION && !this.isStanding() && !this.isSitting() && rand.nextInt(1500) == 0) {
            maxStandTime = 75 + rand.nextInt(50);
            this.setStanding(true);
        }
        if (this.forcedSit() && !this.isBeingRidden() && this.isTamed()) {
            this.setSitting(true);
        }
        if (!world.isRemote) {
            if (ticksExisted == 1) {
                updateClientInventory();
            }

            if (!moving && this.getAnimation() == NO_ANIMATION && !this.isSitting() && !this.isStanding()) {
                if ((getRNG().nextInt(180) == 0 || this.getHealth() < this.getMaxHealth() && getRNG().nextInt(40) == 0) && world.getBlockState(this.getPosition().down()).matchesBlock(Blocks.GRASS_BLOCK)) {
                    this.setAnimation(ANIMATION_EAT_GRASS);
                }
            }
            if (this.getAnimation() == ANIMATION_EAT_GRASS && this.getAnimationTick() == 20 && this.getHealth() < this.getMaxHealth() && world.getBlockState(this.getPosition().down()).matchesBlock(Blocks.GRASS_BLOCK)) {
                this.heal(6);
                this.world.playEvent(2001, getPosition().down(), Block.getStateId(Blocks.GRASS_BLOCK.getDefaultState()));
                this.world.setBlockState(getPosition().down(), Blocks.DIRT.getDefaultState(), 2);
            }
            if (this.getHealth() < this.getMaxHealth() && this.isTamed() && eatCooldown == 0) {
                eatCooldown = 20 + rand.nextInt(40);
                if (!this.kangarooInventory.isEmpty()) {
                    ItemStack foodStack = ItemStack.EMPTY;
                    for (int i = 0; i < this.kangarooInventory.getSizeInventory(); i++) {
                        ItemStack stack = this.kangarooInventory.getStackInSlot(i);
                        if (stack.getItem().isFood() && stack.getItem().getFood() != null && !stack.getItem().getFood().isMeat()) {
                            foodStack = stack;
                        }
                    }
                    if (!foodStack.isEmpty() && foodStack.getItem().getFood() != null) {
                        AlexsMobs.sendMSGToAll(new MessageKangarooEat(this.getEntityId(), foodStack));
                        this.heal(foodStack.getItem().getFood().getHealing() * 2);
                        foodStack.shrink(1);
                        this.playSound(SoundEvents.ENTITY_GENERIC_EAT, this.getSoundVolume(), this.getSoundPitch());
                    }
                }
            }
        }
        if (this.jumpTicks < this.jumpDuration) {
            ++this.jumpTicks;
        } else if (this.jumpDuration != 0) {
            this.jumpTicks = 0;
            this.jumpDuration = 0;
            this.setJumping(false);
        }
        LivingEntity attackTarget = this.getAttackTarget();
        if (attackTarget != null && this.canEntityBeSeen(attackTarget)) {
            if (getDistance(attackTarget) < attackTarget.getWidth() + this.getWidth() + 1) {
                if (this.getAnimation() == ANIMATION_KICK && this.getAnimationTick() == 8) {
                    attackTarget.applyKnockback(1.3F, MathHelper.sin(this.rotationYaw * ((float) Math.PI / 180F)), -MathHelper.cos(this.rotationYaw * ((float) Math.PI / 180F)));
                    this.attackEntityAsMob(this.getAttackTarget());
                }
                if ((this.getAnimation() == ANIMATION_PUNCH_L) && this.getAnimationTick() == 6) {
                    float rot = rotationYaw + 90;
                    attackTarget.applyKnockback(0.85F, MathHelper.sin(rot * ((float) Math.PI / 180F)), -MathHelper.cos(rot * ((float) Math.PI / 180F)));
                    this.attackEntityAsMob(this.getAttackTarget());
                }
                if ((this.getAnimation() == ANIMATION_PUNCH_R) && this.getAnimationTick() == 6) {
                    float rot = rotationYaw - 90;
                    attackTarget.applyKnockback(0.85F, MathHelper.sin(rot * ((float) Math.PI / 180F)), -MathHelper.cos(rot * ((float) Math.PI / 180F)));
                    this.attackEntityAsMob(this.getAttackTarget());
                }
            }
            this.faceEntity(attackTarget, 360, 360);
        }
        if (this.isChild() && attackTarget != null) {
            this.setAttackTarget(null);
        }
        if (this.isBeingRidden()) {
            this.dataManager.set(POUCH_TICK, 10);
            this.setStanding(true);
            maxStandTime = 25;
        }
        if (this.isChild() && this.isPassenger() && this.getRidingEntity() instanceof EntityKangaroo) {
            EntityKangaroo mount = (EntityKangaroo) this.getRidingEntity();
            this.rotationYaw = mount.renderYawOffset;
            this.rotationYawHead = mount.renderYawOffset;
            this.renderYawOffset = mount.renderYawOffset;
        }
        if (this.isPassenger() && this.getRidingEntity() instanceof EntityKangaroo && !this.isChild()) {
            this.dismount();
        }
        if (clientArmorCooldown > 0) {
            clientArmorCooldown--;
        }
        if (ticksExisted > 5 && !world.isRemote && clientArmorCooldown == 0 && this.isTamed()) {
            this.updateClientInventory();
            clientArmorCooldown = 20;
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        boolean prev = super.attackEntityAsMob(entityIn);
        if (prev) {
            if (!this.getHeldItemMainhand().isEmpty()) {
                damageItem(this.getHeldItemMainhand());
            }
        }
        return prev;
    }

    public boolean attackEntityFrom(DamageSource src, float amount) {
        boolean prev = super.attackEntityFrom(src, amount);
        if (prev) {
            if (!this.getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty()) {
                damageItem(this.getItemStackFromSlot(EquipmentSlotType.HEAD));
            }
            if (!this.getItemStackFromSlot(EquipmentSlotType.CHEST).isEmpty()) {
                damageItem(this.getItemStackFromSlot(EquipmentSlotType.CHEST));
            }
        }
        return prev;
    }

    private void damageItem(ItemStack stack) {
        if (stack != null) {
            stack.attemptDamageItem(1, this.getRNG(), null);
            if (stack.getDamage() < 0) {
                stack.shrink(1);
            }
        }
    }

    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source == DamageSource.IN_WALL;
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

    public MovementController getMoveHelper() {
        return this.moveController;
    }

    public boolean canPassengerSteer() {
        return false;
    }

    public void travel(Vector3d vec3d) {
        if (this.isSitting() || this.getAnimation() == ANIMATION_EAT_GRASS) {
            if (this.getNavigator().getPath() != null) {
                this.getNavigator().clearPath();
            }
            vec3d = Vector3d.ZERO;
        }
        super.travel(vec3d);
    }


    private void checkLandingDelay() {
        this.updateMoveTypeDuration();
        this.disableJumpControl();
    }

    public PathNavigator getNavigator() {
        return this.navigator;
    }

    private void enableJumpControl() {
        if (jumpController instanceof EntityKangaroo.JumpHelperController) {
            ((EntityKangaroo.JumpHelperController) this.jumpController).setCanJump(true);
        }
    }

    private void disableJumpControl() {
        if (jumpController instanceof EntityKangaroo.JumpHelperController) {
            ((EntityKangaroo.JumpHelperController) this.jumpController).setCanJump(false);
        }
    }

    private void updateMoveTypeDuration() {
        if (this.moveController.getSpeed() < 2D) {
            this.currentMoveTypeDuration = 2;
        } else {
            this.currentMoveTypeDuration = 1;
        }

    }

    private void calculateRotationYaw(double x, double z) {
        this.rotationYaw = (float) (MathHelper.atan2(z - this.getPosZ(), x - this.getPosX()) * (double) (180F / (float) Math.PI)) - 90.0F;
    }

    public boolean shouldSpawnRunningEffects() {
        return false;
    }

    public void updateAITasks() {
        super.updateAITasks();

        if (this.currentMoveTypeDuration > 0) {
            --this.currentMoveTypeDuration;
        }

        if (this.onGround) {
            if (!this.wasOnGround) {
                this.setJumping(false);
                this.checkLandingDelay();
            }

            if (this.currentMoveTypeDuration == 0) {
                LivingEntity livingentity = this.getAttackTarget();
                if (livingentity != null && this.getDistanceSq(livingentity) < 16.0D) {
                    this.calculateRotationYaw(livingentity.getPosX(), livingentity.getPosZ());
                    this.moveController.setMoveTo(livingentity.getPosX(), livingentity.getPosY(), livingentity.getPosZ(), this.moveController.getSpeed());
                    this.startJumping();
                    this.wasOnGround = true;
                }
            }
            if (this.jumpController instanceof EntityKangaroo.JumpHelperController) {
                EntityKangaroo.JumpHelperController rabbitController = (EntityKangaroo.JumpHelperController) this.jumpController;
                if (!rabbitController.getIsJumping()) {
                    if (this.moveController.isUpdating() && this.currentMoveTypeDuration == 0) {
                        Path path = this.navigator.getPath();
                        Vector3d vector3d = new Vector3d(this.moveController.getX(), this.moveController.getY(), this.moveController.getZ());
                        if (path != null && !path.isFinished()) {
                            vector3d = path.getPosition(this);
                        }
                        this.startJumping();
                    }
                } else if (!rabbitController.canJump()) {
                    this.enableJumpControl();
                }
            }
        }

        this.wasOnGround = this.onGround;
    }

    public float getJumpCompletion(float partialTicks) {
        return this.jumpDuration == 0 ? 0.0F : ((float) this.jumpTicks + partialTicks) / (float) this.jumpDuration;
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int i) {
        animationTick = i;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
        if (animation == ANIMATION_KICK) {
            this.setStanding(true);
            maxStandTime = 30;
        } else if (animation == ANIMATION_PUNCH_R) {
            this.setStanding(true);
            maxStandTime = 15;
        } else if (animation == ANIMATION_PUNCH_L) {
            this.setStanding(true);
            maxStandTime = 15;
        }

    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_EAT_GRASS, ANIMATION_KICK, ANIMATION_PUNCH_L, ANIMATION_PUNCH_R};
    }

    @Nullable
    @Override
    public AgeableEntity createChild(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        return AMEntityRegistry.KANGAROO.create(serverWorld);
    }

    public void setMovementSpeed(double newSpeed) {
        this.getNavigator().setSpeed(newSpeed);
        this.moveController.setMoveTo(this.moveController.getX(), this.moveController.getY(), this.moveController.getZ(), newSpeed);
    }

    protected float getJumpUpwardsMotion() {
        return 0.5F;
    }

    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean isBreedingItem(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.DEAD_BUSH || item == Items.GRASS;
    }

    public void resetKangarooSlots() {
        if (!world.isRemote) {
            int swordIndex = -1;
            double swordDamage = 0;
            int helmetIndex = -1;
            double helmetArmor = 0;
            int chestplateIndex = -1;
            double chestplateArmor = 0;
            for (int i = 0; i < this.kangarooInventory.getSizeInventory(); ++i) {
                ItemStack stack = this.kangarooInventory.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    double dmg = getDamageForItem(stack);
                    if (dmg > 0 && dmg > swordDamage) {
                        swordDamage = dmg;
                        swordIndex = i;
                    }
                    if (stack.getItem().canEquip(stack, EquipmentSlotType.HEAD, this)  && !this.isChild() && helmetIndex == -1) {
                        helmetIndex = i;
                    }
                    if (stack.getItem() instanceof ArmorItem && !this.isChild()) {
                        ArmorItem armorItem = (ArmorItem) stack.getItem();
                        if (armorItem.getEquipmentSlot() == EquipmentSlotType.HEAD) {
                            double prot = getProtectionForItem(stack, EquipmentSlotType.HEAD);
                            if (prot > 0 && prot > helmetArmor) {
                                helmetArmor = prot;
                                helmetIndex = i;
                            }
                        }
                        if (armorItem.getEquipmentSlot() == EquipmentSlotType.CHEST) {
                            double prot = getProtectionForItem(stack, EquipmentSlotType.CHEST);
                            if (prot > 0 && prot > chestplateArmor) {
                                chestplateArmor = prot;
                                chestplateIndex = i;
                            }
                        }
                    }
                }
            }
            this.dataManager.set(SWORD_INDEX, swordIndex);
            this.dataManager.set(CHEST_INDEX, chestplateIndex);
            this.dataManager.set(HELMET_INDEX, helmetIndex);
            updateClientInventory();
        }
    }

    private void updateClientInventory() {
        if (!world.isRemote) {
            for (int i = 0; i < 9; i++) {
                AlexsMobs.sendMSGToAll(new MessageKangarooInventorySync(this.getEntityId(), i, kangarooInventory.getStackInSlot(i)));
            }
        }
    }

    @Nullable
    private Map<EquipmentSlotType, ItemStack> func_241354_r_() {
        Map<EquipmentSlotType, ItemStack> map = null;

        for (EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
            ItemStack itemstack;
            switch (equipmentslottype.getSlotType()) {
                case HAND:
                    itemstack = this.getItemInHand(equipmentslottype);
                    break;
                case ARMOR:
                    itemstack = this.getArmorInSlot(equipmentslottype);
                    break;
                default:
                    continue;
            }

            ItemStack itemstack1 = this.getItemStackFromSlot(equipmentslottype);
            if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent(this, equipmentslottype, itemstack, itemstack1));
                if (map == null) {
                    map = Maps.newEnumMap(EquipmentSlotType.class);
                }

                map.put(equipmentslottype, itemstack1);
                if (!itemstack.isEmpty()) {
                    this.getAttributeManager().removeModifiers(itemstack.getAttributeModifiers(equipmentslottype));
                }

                if (!itemstack1.isEmpty()) {
                    this.getAttributeManager().reapplyModifiers(itemstack1.getAttributeModifiers(equipmentslottype));
                }
            }
        }

        return map;
    }

    public ItemStack getItemStackFromSlot(EquipmentSlotType slotIn) {
        switch (slotIn.getSlotType()) {
            case HAND:
                return getItemInHand(slotIn);
            case ARMOR:
                return getArmorInSlot(slotIn);
            default:
                return ItemStack.EMPTY;
        }
    }

    private ItemStack getArmorInSlot(EquipmentSlotType slot) {
        int helmIndex = dataManager.get(HELMET_INDEX);
        int chestIndex = dataManager.get(CHEST_INDEX);
        return slot == EquipmentSlotType.HEAD && helmIndex >= 0 ? kangarooInventory.getStackInSlot(helmIndex) : slot == EquipmentSlotType.CHEST && chestIndex >= 0 ? kangarooInventory.getStackInSlot(chestIndex) : ItemStack.EMPTY;
    }

    private ItemStack getItemInHand(EquipmentSlotType slot) {
        int index = dataManager.get(SWORD_INDEX);
        return slot == EquipmentSlotType.MAINHAND && index >= 0 ? kangarooInventory.getStackInSlot(index) : ItemStack.EMPTY;
    }

    public double getDamageForItem(ItemStack itemStack) {
        Multimap<Attribute, AttributeModifier> map = itemStack.getAttributeModifiers(EquipmentSlotType.MAINHAND);
        if (!map.isEmpty()) {
            double d = 0;
            for (AttributeModifier mod : map.get(Attributes.ATTACK_DAMAGE)) {
                d += mod.getAmount();
            }
            return d;
        }
        return 0;
    }


    public double getProtectionForItem(ItemStack itemStack, EquipmentSlotType type) {
        Multimap<Attribute, AttributeModifier> map = itemStack.getAttributeModifiers(type);
        if (!map.isEmpty()) {
            double d = 0;
            for (AttributeModifier mod : map.get(Attributes.ARMOR)) {
                d += mod.getAmount();
            }
            return d;
        }
        return 0;
    }

    protected void jump() {
        super.jump();
        double d0 = this.moveController.getSpeed();
        if (d0 > 0.0D) {
            double d1 = horizontalMag(this.getMotion());
            if (d1 < 0.01D) {
            }
        }

        if (!this.world.isRemote) {
            this.world.setEntityState(this, (byte) 1);
        }

    }

    public boolean hasJumper() {
        return jumpController instanceof JumpHelperController;
    }

    public void startJumping() {
        if (!this.isSitting() || this.isInWater()) {
            this.setJumping(true);
            this.jumpDuration = 16;
            this.jumpTicks = 0;
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 1) {
            this.handleRunningEffect();
            this.jumpDuration = 16;
            this.jumpTicks = 0;
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public boolean shouldFollow() {
        return this.getCommand() == 1;
    }

    @Override
    public void onInventoryChanged(IInventory iInventory) {
        this.resetKangarooSlots();
    }

    static class MoveHelperController extends MovementController {
        private final EntityKangaroo kangaroo;
        private double nextJumpSpeed;

        public MoveHelperController(EntityKangaroo kangaroo) {
            super(kangaroo);
            this.kangaroo = kangaroo;
        }

        public void tick() {
            if (this.kangaroo.hasJumper() && this.kangaroo.onGround && !this.kangaroo.isJumping && !((EntityKangaroo.JumpHelperController) this.kangaroo.jumpController).getIsJumping()) {
                this.kangaroo.setMovementSpeed(0.0D);
            } else if (this.isUpdating()) {
                this.kangaroo.setMovementSpeed(this.nextJumpSpeed);
            }

            super.tick();
        }

        /**
         * Sets the speed and location to move to
         */
        public void setMoveTo(double x, double y, double z, double speedIn) {
            if (this.kangaroo.isInWater()) {
                speedIn = 1.5D;
            }

            super.setMoveTo(x, y, z, speedIn);
            if (speedIn > 0.0D) {
                this.nextJumpSpeed = speedIn;
            }

        }
    }

    public class JumpHelperController extends JumpController {
        private final EntityKangaroo kangaroo;
        private boolean canJump;

        public JumpHelperController(EntityKangaroo kangaroo) {
            super(kangaroo);
            this.kangaroo = kangaroo;
        }

        public boolean getIsJumping() {
            return this.isJumping;
        }

        public boolean canJump() {
            return this.canJump;
        }

        public void setCanJump(boolean canJumpIn) {
            this.canJump = canJumpIn;
        }

        public void tick() {
            if (this.isJumping) {
                this.kangaroo.startJumping();
                this.isJumping = false;
            }

        }
    }
}
