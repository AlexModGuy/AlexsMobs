package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.util.TendonWhipUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EntityTendonSegment  extends Entity {

    private static final EntityDataAccessor<Optional<UUID>> CREATOR_ID = SynchedEntityData.defineId(EntityTendonSegment.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> FROM_ID = SynchedEntityData.defineId(EntityTendonSegment.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TARGET_COUNT = SynchedEntityData.defineId(EntityTendonSegment.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CURRENT_TARGET_ID = SynchedEntityData.defineId(EntityTendonSegment.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> PROGRESS = SynchedEntityData.defineId(EntityTendonSegment.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(EntityTendonSegment.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> RETRACTING = SynchedEntityData.defineId(EntityTendonSegment.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_CLAW = SynchedEntityData.defineId(EntityTendonSegment.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_GLINT = SynchedEntityData.defineId(EntityTendonSegment.class, EntityDataSerializers.BOOLEAN);
    private List<Entity> previouslyTouched = new ArrayList<>();
    private boolean hasTouched = false;
    private boolean hasChained = false;
    public float prevProgress = 0;
    public static final float MAX_EXTEND_TIME = 3F;

    public EntityTendonSegment(EntityType<?> type, Level level) {
        super(type, level);
    }

    public EntityTendonSegment(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(AMEntityRegistry.TENDON_SEGMENT.get(), world);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(CREATOR_ID, Optional.empty());
        this.entityData.define(FROM_ID, -1);
        this.entityData.define(TARGET_COUNT, 0);
        this.entityData.define(CURRENT_TARGET_ID, -1);
        this.entityData.define(PROGRESS, 0F);
        this.entityData.define(DAMAGE, 5F);
        this.entityData.define(RETRACTING, false);
        this.entityData.define(HAS_CLAW, true);
        this.entityData.define(HAS_GLINT, false);
    }

    @Override
    public void tick() {
        float progress = this.getProgress();
        this.prevProgress = progress;
        if(tickCount < 1){
            onJoinWorld();
        }else if(tickCount == 1){
            if(!this.level().isClientSide){
                this.playSound(AMSoundRegistry.TENDON_WHIP.get(),1.0F, 0.8F + this.random.nextFloat() * 0.4F);
            }
        }
        super.tick();
        Entity creator = getCreatorEntity();
        Entity current = getToEntity();
        if(!this.isRetracting() && progress < MAX_EXTEND_TIME){
            this.setProgress(progress + 1);
        }
        if(this.isRetracting() && progress > 0F){
            this.setProgress(progress - 1);
        }
        if(this.isRetracting() && progress == 0F){
            Entity from = this.getFromEntity();
            if(from instanceof EntityTendonSegment){
                EntityTendonSegment tendonSegment = (EntityTendonSegment) from;
                tendonSegment.setRetracting(true);
                updateLastTendon(tendonSegment);
            }else{
                updateLastTendon(null);
            }

            this.remove(RemovalReason.DISCARDED);
        }
        if (creator instanceof LivingEntity) {
            if (current != null) {
                Vec3 target = new Vec3(current.getX(), current.getY(0.4F), current.getZ());
                Vec3 lerp = target.subtract(this.position());
                this.setDeltaMovement(lerp.scale(0.5F));
                if(!this.level().isClientSide){
                    if(!hasTouched && progress >= MAX_EXTEND_TIME){
                        hasTouched = true;
                        Entity entity = getCreatorEntity();
                        if(entity instanceof LivingEntity){
                            if(current != creator && current.hurt(damageSources().mobProjectile(this, (LivingEntity)entity), (float) getDamageFor((LivingEntity)creator, (LivingEntity)entity))){
                                this.doEnchantDamageEffects((LivingEntity) creator, entity);
                            }
                        }
                    }
                }
            }
        }
        Vec3 vector3d = this.getDeltaMovement();
        if(!this.level().isClientSide){
            if(!hasChained){
                if(this.getTargetsHit() > 3){
                    this.setRetracting(true);
                }else if(creator instanceof LivingEntity && this.getProgress() >= MAX_EXTEND_TIME) {
                    Entity closestValid = null;
                    for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(8.0D))) {
                        if (!entity.equals(creator) && !previouslyTouched.contains(entity) && isValidTarget((LivingEntity) creator, entity) && this.hasLineOfSight(entity)) {
                            if (closestValid == null || this.distanceTo(entity) < this.distanceTo(closestValid)) {
                                closestValid = entity;
                            }
                        }
                    }
                    if(closestValid != null){
                        createChain(closestValid);
                        hasChained = true;
                    }else{
                        this.setRetracting(true);
                    }
                }
            }
        }
        double d0 = this.getX() + vector3d.x;
        double d1 = this.getY() + vector3d.y;
        double d2 = this.getZ() + vector3d.z;
        this.setDeltaMovement(vector3d.scale(0.99F));
        this.setPos(d0, d1, d2);
    }

    private boolean isValidTarget(LivingEntity creator, Entity entity) {
        if(!creator.isAlliedTo(entity) && !entity.isAlliedTo(creator) && entity instanceof Mob){
            return true;
        }
        return creator.getLastHurtMob() != null && creator.getLastHurtMob().getUUID().equals(entity.getUUID()) || creator.getLastHurtByMob() != null && creator.getLastHurtByMob().getUUID().equals(entity.getUUID());
    }

    private double getDamageFor(LivingEntity creator, LivingEntity entity) {
        ItemStack stack = creator.getItemInHand(InteractionHand.MAIN_HAND).is(AMItemRegistry.TENDON_WHIP.get()) ? creator.getItemInHand(InteractionHand.MAIN_HAND) : creator.getItemInHand(InteractionHand.OFF_HAND);
        double dmg = this.getBaseDamage();
        if(stack.is(AMItemRegistry.TENDON_WHIP.get())){
            dmg += EnchantmentHelper.getDamageBonus(stack, entity.getMobType());
        }
        return dmg;
    }

    private double getDamageForItem(ItemStack itemStack) {
        Multimap<Attribute, AttributeModifier> map = itemStack.getAttributeModifiers(EquipmentSlot.MAINHAND);
        if (!map.isEmpty()) {
            double d = 0;
            for (AttributeModifier mod : map.get(Attributes.ATTACK_DAMAGE)) {
                d += mod.getAmount();
            }
            return d;
        }
        return 0;
    }

    private boolean hasLineOfSight(Entity entity) {
        if (entity.level != this.level()) {
            return false;
        } else {
            Vec3 vec3 = new Vec3(this.getX(), this.getEyeY(), this.getZ());
            Vec3 vec31 = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
            if (vec31.distanceTo(vec3) > 128.0D) {
                return false;
            } else {
                return this.level().clip(new ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
            }
        }
    }

    private void updateLastTendon(EntityTendonSegment lastTendon){
        Entity creator = getCreatorEntity();
        if(creator == null){
            creator = level.getPlayerByUUID(this.getCreatorEntityUUID());
        }
        if(creator instanceof LivingEntity){
            TendonWhipUtil.setLastTendon((LivingEntity)creator, lastTendon);
        }
    }

    private void createChain(Entity closestValid) {
        this.entityData.set(HAS_CLAW, false);
        EntityTendonSegment child = AMEntityRegistry.TENDON_SEGMENT.get().create(this.level());
        child.previouslyTouched = new ArrayList<>(previouslyTouched);
        child.previouslyTouched.add(closestValid);
        child.setCreatorEntityUUID(this.getCreatorEntityUUID());
        child.setFromEntityID(this.getId());
        child.setToEntityID(closestValid.getId());
        child.setPos(closestValid.getX(), closestValid.getY(0.4F), closestValid.getZ());
        child.setTargetsHit(this.getTargetsHit() + 1);
        updateLastTendon(child);
        child.setHasGlint(this.hasGlint());
        this.level().addFreshEntity(child);
    }

    private void onJoinWorld(){
        Entity creator = getCreatorEntity();
        if(creator == null){
            creator = level.getPlayerByUUID(this.getCreatorEntityUUID());
        }
        Entity prior = getFromEntity();
        if(creator instanceof Player){
            Player player = (Player)creator;
            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND).is(AMItemRegistry.TENDON_WHIP.get()) ? player.getItemInHand(InteractionHand.MAIN_HAND) : player.getItemInHand(InteractionHand.OFF_HAND);
            if(stack.is(AMItemRegistry.TENDON_WHIP.get())){
                this.setHasGlint(stack.hasFoil());
            }
            float dmg = 2;
            if(prior instanceof EntityTendonSegment){
                dmg = Math.max(((EntityTendonSegment)prior).getBaseDamage() - 1, 2);
            }else{
                dmg = (float)getDamageForItem(stack);
            }
            this.entityData.set(DAMAGE, dmg);
        }
    }

    private float getBaseDamage() {
        return this.entityData.get(DAMAGE);
    }

    public UUID getCreatorEntityUUID() {
        return this.entityData.get(CREATOR_ID).orElse(null);
    }

    public void setCreatorEntityUUID(UUID id) {
        this.entityData.set(CREATOR_ID, Optional.ofNullable(id));
    }

    public Entity getCreatorEntity() {
        UUID uuid = getCreatorEntityUUID();
        if(uuid != null && !this.level().isClientSide){
            return ((ServerLevel) level()).getEntity(uuid);
        }
        return null;
    }

    public int getFromEntityID() {
        return this.entityData.get(FROM_ID);
    }

    public void setFromEntityID(int id) {
        this.entityData.set(FROM_ID, id);
    }

    public Entity getFromEntity() {
        return getFromEntityID() == -1 ? null : this.level().getEntity(getFromEntityID());
    }

    public int getToEntityID() {
        return this.entityData.get(CURRENT_TARGET_ID);
    }

    public void setToEntityID(int id) {
        this.entityData.set(CURRENT_TARGET_ID, id);
    }

    public Entity getToEntity() {
        return getToEntityID() == -1 ? null : this.level().getEntity(getToEntityID());
    }

    public int getTargetsHit() {
        return this.entityData.get(TARGET_COUNT);
    }

    public void setTargetsHit(int i) {
        this.entityData.set(TARGET_COUNT, i);
    }

    public float getProgress() {
        return this.entityData.get(PROGRESS);
    }

    public void setProgress(float progress) {
        this.entityData.set(PROGRESS, progress);
    }

    public boolean isRetracting() {
        return this.entityData.get(RETRACTING);
    }

    public void setRetracting(boolean retract) {
        this.entityData.set(RETRACTING, retract);
    }

    public boolean hasGlint() {
        return this.entityData.get(HAS_GLINT);
    }

    public void setHasGlint(boolean glint) {
        this.entityData.set(HAS_GLINT, glint);
    }

    public boolean hasClaw() {
        return this.entityData.get(HAS_CLAW);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_20052_) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag p_20139_) {

    }

    public boolean isCreator(Entity mob) {
        return this.getCreatorEntityUUID() != null && mob.getUUID().equals(this.getCreatorEntityUUID());
    }
}
