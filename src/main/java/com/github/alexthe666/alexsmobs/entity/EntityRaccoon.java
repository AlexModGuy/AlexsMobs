package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import javax.swing.text.html.Option;
import java.util.Optional;

public class EntityRaccoon extends TameableEntity implements IAnimatedEntity, IFollower, ITargetsDroppedItems {

    private static final DataParameter<Boolean> STANDING = EntityDataManager.createKey(EntityRaccoon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityRaccoon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> BEGGING = EntityDataManager.createKey(EntityRaccoon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> WASHING = EntityDataManager.createKey(EntityRaccoon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Optional<BlockPos>> WASH_POS = EntityDataManager.createKey(EntityRaccoon.class, DataSerializers.OPTIONAL_BLOCK_POS);
    public float prevStandProgress;
    public float standProgress;
    public float prevBegProgress;
    public float begProgress;
    public float prevWashProgress;
    public float washProgress;
    public int maxStandTime = 75;
    private int standingTime = 0;
    public int lookForWaterBeforeEatingTimer = 0;
    public boolean postponeEating = false;
    private int animationTick;
    private Animation currentAnimation;
    private int washTime = 0;

    protected EntityRaccoon(EntityType type, World world) {
        super(type, world);
        this.setPathPriority(PathNodeType.WATER_BORDER, 0.0F);
    }

    protected float getWaterSlowDown() {
        return 0.98F;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SitGoal(this));
        this.goalSelector.addGoal(1, new RaccoonAIWash(this));
        this.goalSelector.addGoal(3, new SwimGoal(this));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(7, new RaccoonAIBeg(this, 0.65D));
        this.goalSelector.addGoal(9, new AnimalAIPanicBaby(this, 1.25D));
        this.goalSelector.addGoal(10, new AnimalAIWanderRanged(this, 120, 1.0D, 14, 7));
        this.goalSelector.addGoal(11, new LookAtGoal(this, PlayerEntity.class, 15.0F));
        this.goalSelector.addGoal(11, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, (new AnimalAIHurtByTargetNotBaby(this)));
        this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false));
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 20D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 4.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    public void tick() {
        super.tick();
        this.prevStandProgress = this.standProgress;
        this.prevBegProgress = this.begProgress;
        this.prevWashProgress = this.washProgress;
        if (this.isStanding() && standProgress < 5) {
            standProgress += 1;
        }
        if (!this.isStanding() && standProgress > 0) {
            standProgress -= 1;
        }
        if (this.isBegging() && begProgress < 5) {
            begProgress += 1;
        }
        if (!this.isBegging() && begProgress > 0) {
            begProgress -= 1;
        }
        if (this.isWashing() && washProgress < 5) {
            washProgress += 1;
        }
        if (!this.isWashing() && washProgress > 0) {
            washProgress -= 1;
        }
        if (isStanding() && ++standingTime > maxStandTime) {
            this.setStanding(false);
            standingTime = 0;
            maxStandTime = 75 + rand.nextInt(50);
        }
        if(!world.isRemote){
            if(lookForWaterBeforeEatingTimer > 0){
                lookForWaterBeforeEatingTimer--;
            }else if(!postponeEating && !isWashing() && this.getHeldItemMainhand().isFood()) {
                this.getHeldItemMainhand().shrink(1);
            }
        }
        if(isWashing()){
            if(getWashPos() != null){
                BlockPos washingPos = getWashPos();
                if(this.getDistanceSq(washingPos.getX() + 0.5D, washingPos.getY() + 0.5D, washingPos.getZ() + 0.5D) < 3){
                    for(int j = 0; (float)j < 4; ++j) {
                        double d2 = (this.rand.nextDouble()) ;
                        double d3 = (this.rand.nextDouble()) ;
                        float f1 = this.rand.nextFloat() - 0.5F;
                        float f2 = this.rand.nextFloat() - 0.5F;
                        float f3 = this.rand.nextFloat() - 0.5F;
                        Vector3d vector3d = this.getMotion();

                        this.world.addParticle(ParticleTypes.SPLASH, washingPos.getX() + d2, (double)(washingPos.getY() + 0.8F), washingPos.getZ() + d3, vector3d.x, vector3d.y, vector3d.z);
                    }
                }else{
                    setWashing(false);
                }
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

   public boolean isStanding() {
        return this.dataManager.get(STANDING).booleanValue();
    }

    public void setStanding(boolean standing) {
        this.dataManager.set(STANDING, Boolean.valueOf(standing));
    }

    public boolean isBegging() {
        return this.dataManager.get(BEGGING).booleanValue();
    }

    public void setBegging(boolean begging) {
        this.dataManager.set(BEGGING, Boolean.valueOf(begging));
    }

    public boolean isWashing() {
        return this.dataManager.get(WASHING).booleanValue();
    }

    public void setWashing(boolean washing) {
        this.dataManager.set(WASHING, Boolean.valueOf(washing));
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(STANDING, Boolean.valueOf(false));
        this.dataManager.register(SITTING, Boolean.valueOf(false));
        this.dataManager.register(BEGGING, Boolean.valueOf(false));
        this.dataManager.register(WASHING, Boolean.valueOf(false));
        this.dataManager.register(WASH_POS, Optional.empty());
    }


    public BlockPos getWashPos() {
        return this.dataManager.get(WASH_POS).orElse(null);
    }

    public void setWashPos(BlockPos washingPos) {
        this.dataManager.set(WASH_POS, Optional.ofNullable(washingPos));
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
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{};
    }


    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        return null;
    }

    public void travel(Vector3d vec3d) {
        if (this.isSitting() || this.isWashing()) {
            if (this.getNavigator().getPath() != null) {
                this.getNavigator().clearPath();
            }
            vec3d = Vector3d.ZERO;
        }
        super.travel(vec3d);
    }

    @Override
    public boolean shouldFollow() {
        return false;
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return stack.getItem().isFood();
    }

    @Override
    public void onGetItem(ItemEntity e) {
        lookForWaterBeforeEatingTimer = 100;
        ItemStack duplicate = e.getItem().copy();
        duplicate.setCount(1);
        if (!this.getHeldItem(Hand.MAIN_HAND).isEmpty() && !this.world.isRemote) {
            this.entityDropItem(this.getHeldItem(Hand.MAIN_HAND), 0.0F);
        }
        this.setHeldItem(Hand.MAIN_HAND, duplicate);
        postponeEating = false;
    }
}
