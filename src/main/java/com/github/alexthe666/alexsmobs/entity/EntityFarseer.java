package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityFarseer extends Monster {

    private static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.defineId(EntityFarseer.class, EntityDataSerializers.BOOLEAN);
    public final double[][] positions = new double[64][4];
    public int posPointer = -1;
    public float angryProgress;
    public float prevAngryProgress;
    public final float[] claspProgress = new float[4];
    public final float[] prevClaspProgress = new float[4];
    public Vec3 angryShakeVec = Vec3.ZERO;
    private int claspingHand = -1;

    protected EntityFarseer(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 24D).add(Attributes.ARMOR, 2.0D).add(Attributes.ATTACK_DAMAGE, 4.5D).add(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANGRY, false);
    }

    public boolean isAngry() {
        return this.entityData.get(ANGRY).booleanValue();
    }

    public void setAngry(boolean angry) {
        this.entityData.set(ANGRY, Boolean.valueOf(angry));
    }

    public void tick() {
        super.tick();
        prevAngryProgress = angryProgress;
        for(int i = 0; i < claspProgress.length; i++){
            prevClaspProgress[i] = claspProgress[i];
        }
        if (this.posPointer < 0) {
            for (int i = 0; i < this.positions.length; ++i) {
                this.positions[i][0] = this.getX();
                this.positions[i][1] = this.getY();
                this.positions[i][2] = this.getZ();
                this.positions[i][3] = this.yBodyRot;
            }
        }
        if (++this.posPointer == this.positions.length) {
            this.posPointer = 0;
        }
        this.positions[this.posPointer][0] = this.getX();
        this.positions[this.posPointer][1] = this.getY();
        this.positions[this.posPointer][2] = this.getZ();
        this.positions[this.posPointer][3] = this.yBodyRot;
        this.setAngry(this.tickCount % 200 > 100);
        if(this.isAngry() && angryProgress < 5F){
            angryProgress++;
        }
        if(!this.isAngry() && angryProgress > 0F){
            angryProgress--;
        }
        if(random.nextInt(isAngry() ? 12 : 40) == 0 && claspingHand == -1){
            int i = Mth.clamp(random.nextInt(4), 0, 3);
            if(claspProgress[i] == 0){
                claspingHand = i;
            }
        }
        if(claspingHand >= 0){
            if(claspProgress[claspingHand] < 5F) {
                claspProgress[claspingHand]++;
            }else{
                claspingHand = -1;
            }
        }else{
            for(int i = 0; i < claspProgress.length; i++){
                if(claspProgress[i] > 0){
                    claspProgress[i]--;
                }
            }
        }
        if(this.isAngry()){
            angryShakeVec = new Vec3(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F);
        }else{
            angryShakeVec = Vec3.ZERO;
        }

    }

    public double getLatencyVar(int pointer, int index, float partialTick) {
        if (this.isDeadOrDying()) {
            partialTick = 1.0F;
        }
        int i = this.posPointer - pointer & 63;
        int j = this.posPointer - pointer - 1 & 63;
        double d0 = this.positions[j][index];
        double d1 = Mth.wrapDegrees(this.positions[i][index] - d0);
        return d0 + d1 * partialTick;
    }

    public Vec3 getLatencyOffsetVec(int offset, float partialTick) {
        double d0 = Mth.lerp(partialTick, this.xOld, this.getX());
        double d1 = Mth.lerp(partialTick, this.yOld, this.getY());
        double d2 = Mth.lerp(partialTick, this.zOld, this.getZ());
        float renderYaw = (float)this.getLatencyVar(0, 3, partialTick);
        return new Vec3(this.getLatencyVar(offset, 0, partialTick) - d0, this.getLatencyVar(offset, 1, partialTick) - d1, this.getLatencyVar(offset, 2, partialTick) - d2).yRot(renderYaw * ((float)Math.PI / 180F));
    }
}
