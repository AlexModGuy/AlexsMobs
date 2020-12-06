package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Predicate;

public class EntityKomodoDragon extends TameableEntity implements ITargetsDroppedItems {

    private static final Ingredient TEMPTATION_ITEMS = Ingredient.fromItems(Items.ROTTEN_FLESH);
    public int slaughterCooldown = 0;
    public int timeUntilSpit = this.rand.nextInt(12000) + 24000;

    public static final Predicate<EntityKomodoDragon> HURT_OR_BABY = (p_213616_0_) -> {
        return p_213616_0_.isChild() || p_213616_0_.getHealth() <= 0.7F * p_213616_0_.getMaxHealth();
    };

    public static <T extends MobEntity> boolean canKomodoDragonSpawn(EntityType<? extends AnimalEntity> animal, IWorld worldIn, SpawnReason reason, BlockPos pos, Random random) {
        boolean spawnBlock = BlockTags.getCollection().get(AMTagRegistry.KOMODO_DRAGON_SPAWNS).contains(worldIn.getBlockState(pos.down()).getBlock());
        return spawnBlock && worldIn.getLightSubtracted(pos, 0) > 8;
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.komodoDragonSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new SitGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 2D, false));
        this.goalSelector.addGoal(2, new TameableAIRide(this, 2D));
        this.goalSelector.addGoal(4, new TameableAITempt(this, 1.1D, TEMPTATION_ITEMS, false));
        this.goalSelector.addGoal(4, new AnimalAIFleeAdult(this, 1.25D, 32));
        this.goalSelector.addGoal(5, new KomodoDragonAIBreed(this, 1.0D));
        this.goalSelector.addGoal(6, new RandomWalkingGoal(this, 1D, 50));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new CreatureAITargetItems(this, false));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, EntityKomodoDragon.class, 50, true, false, HURT_OR_BABY));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, PlayerEntity.class, 150, true, true, null));
        this.targetSelector.addGoal(8, new EntityAINearestTarget3D(this, LivingEntity.class, 180, false, true, AMEntityRegistry.buildPredicateFromTag(EntityTypeTags.getCollection().get(AMTagRegistry.KOMODO_DRAGON_TARGETS))));
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getTrueSource();
            this.func_233687_w_(false);
            if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof AbstractArrowEntity)) {
                amount = (amount + 1.0F) / 3.0F;
            }
            return super.attackEntityFrom(source, amount);
        }
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.KOMODO_DRAGON_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.KOMODO_DRAGON_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.KOMODO_DRAGON_HURT;
    }



    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("SpitTime")) {
            this.timeUntilSpit = compound.getInt("SpitTime");
        }

    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("SpitTime", this.timeUntilSpit);
    }

    public boolean isBreedingItem(ItemStack stack) {
        Item item = stack.getItem();
        return isTamed() && item == Items.ROTTEN_FLESH;
    }

    public void tick() {
        super.tick();
        if(slaughterCooldown > 0){
            slaughterCooldown--;
        }
        if (!this.world.isRemote && this.isAlive() && !this.isChild() && --this.timeUntilSpit <= 0) {
            this.entityDropItem(AMItemRegistry.KOMODO_SPIT);
            this.timeUntilSpit = this.rand.nextInt(12000) + 24000;
        }
    }
    public boolean attackEntityAsMob(Entity entityIn) {
        if (super.attackEntityAsMob(entityIn)) {
            if (entityIn instanceof LivingEntity) {
                int i = 5;
                if (this.world.getDifficulty() == Difficulty.NORMAL) {
                    i = 10;
                } else if (this.world.getDifficulty() == Difficulty.HARD) {
                    i = 20;
                }
                ((LivingEntity)entityIn).addPotionEffect(new EffectInstance(Effects.POISON, i * 20, 0));
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean isPotionApplicable(EffectInstance potioneffectIn) {
        if (potioneffectIn.getPotion() == Effects.POISON) {
            return false;
        }
        return super.isPotionApplicable(potioneffectIn);
    }

    @Nullable
    public Entity getControllingPassenger() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) passenger;
                return player;
            }
        }
        return null;
    }

    public void updatePassenger(Entity passenger) {
        if (this.isPassenger(passenger)) {
            float radius = 0;
            float angle = (0.01745329251F * this.renderYawOffset);
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            passenger.setPosition(this.getPosX() + extraX, this.getPosY() + this.getMountedYOffset() + passenger.getYOffset(), this.getPosZ() + extraZ);
        }
    }

    public double getMountedYOffset() {
        float f = Math.min(0.25F, this.limbSwingAmount);
        float f1 = this.limbSwing;
        return (double)this.getHeight() - 0.2D + (double)(0.12F * MathHelper.cos(f1 * 0.7F) * 0.7F * f);
    }



    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        ActionResultType type = super.func_230254_b_(player, hand);

        if(item == Items.ROTTEN_FLESH && !isTamed()){
            int size = itemstack.getCount();
            int tameAmount = 58 + rand.nextInt(16);
            if(size > tameAmount){
                this.setTamedBy(player);
            }
            itemstack.shrink(size);
            return ActionResultType.SUCCESS;
        }
        if(type != ActionResultType.SUCCESS && isTamed() && isOwner(player)){
            if(isBreedingItem(itemstack)){
                this.setInLove(600);
                this.consumeItemFromStack(player, itemstack);
                return ActionResultType.SUCCESS;
            }
            if(!player.isSneaking() && !isBreedingItem(itemstack)){
                player.startRiding(this);
                return ActionResultType.SUCCESS;
            }
        }
        return type;
    }

    protected EntityKomodoDragon(EntityType type, World worldIn) {
        super(type, worldIn);
    }

    protected float getWaterSlowDown() {
        return 0.98F;
    }

    public void setAttackTarget(@Nullable LivingEntity entitylivingbaseIn) {
        if(!this.isChild() || slaughterCooldown > 0){
            super.setAttackTarget(entitylivingbaseIn);
        }
    }
    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 40D).createMutableAttribute(Attributes.ARMOR, 6.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return AMEntityRegistry.KOMODO_DRAGON.create(p_241840_1_);
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return stack.getItem() == Items.ROTTEN_FLESH || stack.getItem().getFood() != null && stack.getItem().getFood().isMeat();
    }

    @Override
    public void onGetItem(ItemEntity e) {
        this.heal(10);
    }
}
