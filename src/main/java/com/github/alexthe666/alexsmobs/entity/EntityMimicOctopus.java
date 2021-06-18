package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityMimicOctopus extends WaterMobEntity {

    private static final DataParameter<Integer> MIMIC_ORDINAL = EntityDataManager.createKey(EntityMimicOctopus.class, DataSerializers.VARINT);
    public MimicState prevMimicState;
    public float transProgress = 0F;
    public float prevTransProgress = 0F;

    protected EntityMimicOctopus(EntityType type, World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 16D).createMutableAttribute(Attributes.ARMOR, 0.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.35F);
    }

    public void tick() {
        this.prevTransProgress = transProgress;
        super.tick();
        this.setAir(100);
        if(prevMimicState != this.getMimicState() && transProgress < 5.0F){
            transProgress += 0.25F;
        }
        if(prevMimicState == this.getMimicState() && transProgress > 0F){
            transProgress -= 0.25F;
        }
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(MIMIC_ORDINAL, 0);
    }

    public MimicState getMimicState() {
        return MimicState.values()[dataManager.get(MIMIC_ORDINAL)];
    }

    public void setMimicState(MimicState state) {
        if(getMimicState() != state){
            prevMimicState = getMimicState();
            transProgress = 0.0F;
        }
        this.dataManager.set(MIMIC_ORDINAL, state.ordinal());
    }

    public BlockState getMimickingBlock() {
        return world.getBlockState(this.getPosition().down());
    }

    public enum MimicState {
        OVERLAY,
        CREEPER,
        GUARDIAN,
        PUFFERFISH
    }
}
