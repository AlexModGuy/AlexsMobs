package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.message.MessageStartDancing;
import com.github.alexthe666.alexsmobs.misc.AMPointOfInterestRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import com.google.common.base.Predicates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityManedWolf extends Animal implements ITargetsDroppedItems, IDancingMob {

    private static final EntityDataAccessor<Float> EAR_PITCH = SynchedEntityData.defineId(EntityManedWolf.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> EAR_YAW = SynchedEntityData.defineId(EntityManedWolf.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DANCING = SynchedEntityData.defineId(EntityManedWolf.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SHAKING_TIME = SynchedEntityData.defineId(EntityManedWolf.class, EntityDataSerializers.INT);
    private static final Ingredient allFoods = Ingredient.of(Items.APPLE, Items.RABBIT, Items.COOKED_RABBIT, Items.CHICKEN, Items.COOKED_CHICKEN);
    public float prevEarPitch;
    public float prevEarYaw;
    public float prevDanceProgress;
    public float danceProgress;
    public float prevShakeProgress;
    public float shakeProgress;
    private int earCooldown = 0;
    private float targetPitch;
    private float targetYaw;
    private boolean isJukeboxing;
    private BlockPos jukeboxPosition;
    private BlockPos nearestAnthill;

    protected EntityManedWolf(EntityType<? extends Animal> animal, Level level) {
        super(animal, level);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 16.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, allFoods, false));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1D, 60));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false, 30));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EAR_PITCH, 0F);
        this.entityData.define(EAR_YAW, 0F);
        this.entityData.define(SHAKING_TIME, 0);
        this.entityData.define(DANCING, Boolean.valueOf(false));
    }

    public float getEarYaw() {
        return entityData.get(EAR_YAW);
    }

    public void setEarYaw(float yaw) {
        entityData.set(EAR_YAW, yaw);
    }

    public float getEarPitch() {
        return entityData.get(EAR_PITCH);
    }

    public void setEarPitch(float pitch) {
        entityData.set(EAR_PITCH, pitch);
    }

    public boolean isDancing() {
        return this.entityData.get(DANCING).booleanValue();
    }

    public void setDancing(boolean dancing) {
        this.entityData.set(DANCING, dancing);
        this.isJukeboxing = dancing;
    }

    private void attractAnimals() {
        if (this.getShakingTime() % 5 == 0) {
            List<Animal> list = this.level.getEntitiesOfClass(Animal.class, this.getBoundingBox().inflate(16, 8, 16));
            for (Animal e : list) {
                if(!(e instanceof EntityManedWolf)){
                    e.setTarget(null);
                    e.setLastHurtByMob(null);
                    Vec3 vec = LandRandomPos.getPosTowards(e, 20, 7, this.position());
                    if (vec != null) {
                        e.getNavigation().moveTo(vec.x, vec.y, vec.z, 1.5D);
                    }
                }
            }

        }
    }

    private void pollinateAnthill(){
        if(nearestAnthill != null && level.getBlockEntity(nearestAnthill) instanceof TileEntityLeafcutterAnthill){
            if(this.getShakingTime() % 5 == 0){
                this.getNavigation().moveTo(nearestAnthill.getX() + 0.5F, nearestAnthill.getY() + 1F, nearestAnthill.getZ() + 0.5F, 1F);
            }
            if(nearestAnthill.closerThan(this.position(), 6) && this.getShakingTime() % 20 == 0){
                ((TileEntityLeafcutterAnthill)level.getBlockEntity(nearestAnthill)).growFungus();
            }
        }
    }

    private void findAnthill(){
        if(nearestAnthill == null || !(level.getBlockEntity(nearestAnthill) instanceof TileEntityLeafcutterAnthill)){
            PoiManager pointofinterestmanager = ((ServerLevel) level).getPoiManager();
            Stream<BlockPos> stream = pointofinterestmanager.findAll(AMPointOfInterestRegistry.LEAFCUTTER_ANT_HILL.getPredicate(), Predicates.alwaysTrue(), this.blockPosition(), 10, PoiManager.Occupancy.ANY);
            List<BlockPos> listOfHives = stream.collect(Collectors.toList());
            BlockPos nearest = null;
            for (BlockPos pos : listOfHives) {
                if (nearest == null || pos.distSqr(this.blockPosition()) < nearest.distSqr(this.blockPosition())) {
                    nearest = pos;
                }
            }
            nearestAnthill = nearest;
        }
    }
    @Override
    public void setJukeboxPos(BlockPos pos) {
        this.jukeboxPosition = pos;
    }

    public boolean isShaking() {
        return this.getShakingTime() > 0;
    }

    public int getShakingTime() {
        return this.entityData.get(SHAKING_TIME);
    }

    public void setShakingTime(int shaking) {
        this.entityData.set(SHAKING_TIME, shaking);
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        InteractionResult type = super.mobInteract(player, hand);
        if (itemstack.is(Items.APPLE) && !this.isShaking() && this.getMainHandItem().isEmpty()) {
            this.usePlayerItem(player, hand, itemstack);
            eatItemEffect(itemstack);
            this.setShakingTime(100 + random.nextInt(30));
            return InteractionResult.SUCCESS;
        } else {
            return type;
        }
    }

    private void eatItemEffect(ItemStack heldItemMainhand) {
        for (int i = 0; i < 2 + random.nextInt(2); i++) {
            double d2 = this.random.nextGaussian() * 0.02D;
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            float radius = this.getBbWidth() * 0.65F;
            float angle = (0.01745329251F * this.yBodyRot);
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            ParticleOptions data = new ItemParticleOption(ParticleTypes.ITEM, heldItemMainhand);
            if (heldItemMainhand.getItem() instanceof BlockItem) {
                data = new BlockParticleOption(ParticleTypes.BLOCK, ((BlockItem) heldItemMainhand.getItem()).getBlock().defaultBlockState());
            }
            this.level.addParticle(data, this.getX() + extraX, this.getY() + this.getBbHeight() * 0.6F, this.getZ() + extraZ, d0, d1, d2);
        }
    }

    public void tick() {
        super.tick();
        prevEarPitch = this.getEarPitch();
        prevEarYaw = this.getEarYaw();
        prevDanceProgress = danceProgress;
        prevShakeProgress = shakeProgress;
        if (!level.isClientSide) {
            updateEars();
        }
        boolean dance = isDancing();
        if (this.jukeboxPosition == null || !this.jukeboxPosition.closerThan(this.position(), 15) || !this.level.getBlockState(this.jukeboxPosition).is(Blocks.JUKEBOX)) {
            this.isJukeboxing = false;
            this.setDancing(false);
            this.jukeboxPosition = null;
        }
        if (dance && danceProgress < 5F) {
            danceProgress++;
        }
        if (!dance && danceProgress > 0F) {
            danceProgress--;
        }
        if (this.isShaking() && shakeProgress < 5F) {
            shakeProgress++;
        }
        if (!this.isShaking() && shakeProgress > 0F) {
            shakeProgress--;
        }
        if (this.isShaking()) {
            this.setShakingTime(this.getShakingTime() - 1);
            if (this.level.isClientSide) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = 0.05F + this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                this.level.addParticle(AMParticleRegistry.SMELLY, this.getRandomX(0.7F), this.getY(0.6F), this.getRandomZ(0.7F), d0, d1, d2);
            }else{
                attractAnimals();
                findAnthill();
                if(this.nearestAnthill != null){
                    pollinateAnthill();
                }
            }
        }
    }

    private void updateEars() {
        float pitchDist = Math.abs(targetPitch - this.getEarPitch());
        float yawDist = Math.abs(targetYaw - this.getEarYaw());
        if (earCooldown <= 0 && this.random.nextInt(30) == 0 && pitchDist <= 0.1F && yawDist <= 0.1F) {
            targetPitch = Mth.clamp(random.nextFloat() * 60F - 30, -30, 30);
            targetYaw = Mth.clamp(random.nextFloat() * 60F - 30, -30, 30);
            earCooldown = 8 + random.nextInt(15);
        }
        if (this.getEarPitch() < this.targetPitch && pitchDist > 0.1F) {
            this.setEarPitch(this.getEarPitch() + Math.min(pitchDist, 4F));
        }
        if (this.getEarPitch() > this.targetPitch && pitchDist > 0.1F) {
            this.setEarPitch(this.getEarPitch() - Math.min(pitchDist, 4F));
        }
        if (this.getEarYaw() < this.targetYaw && yawDist > 0.1F) {
            this.setEarYaw(this.getEarYaw() + Math.min(yawDist, 4F));
        }
        if (this.getEarYaw() > this.targetYaw && yawDist > 0.1F) {
            this.setEarYaw(this.getEarYaw() - Math.min(yawDist, 4F));
        }
        if (earCooldown > 0) {
            earCooldown--;
        }
    }

    public boolean isFood(ItemStack stack) {
        return !stack.is(Items.APPLE) && allFoods.test(stack);
    }

    public void travel(Vec3 vec3d) {
        if (this.isDancing() || danceProgress > 0) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return allFoods.test(stack) && !this.isShaking();
    }

    @Override
    public void onGetItem(ItemEntity e) {
        eatItemEffect(e.getItem());
        if (e.getItem().is(Items.APPLE)) {
            this.setShakingTime(100 + random.nextInt(30));
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
        return AMEntityRegistry.MANED_WOLF.create(serverWorld);
    }

    @OnlyIn(Dist.CLIENT)
    public void setRecordPlayingNearby(BlockPos pos, boolean isPartying) {
        AlexsMobs.sendMSGToServer(new MessageStartDancing(this.getId(), isPartying, pos));
        this.setDancing(isPartying);
        if (isPartying) {
            this.setJukeboxPos(pos);
        } else {
            this.setJukeboxPos(null);
        }
    }
}
