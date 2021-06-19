package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Optional;

public class EntityMimicOctopus extends WaterMobEntity {

    private static final DataParameter<Integer> MIMIC_ORDINAL = EntityDataManager.createKey(EntityMimicOctopus.class, DataSerializers.VARINT);
    private static final DataParameter<Optional<BlockState>> MIMICKED_BLOCK = EntityDataManager.createKey(EntityMimicOctopus.class, DataSerializers.OPTIONAL_BLOCK_STATE);
    public MimicState prevMimicState;
    public BlockState prevMimickedBlock;
    public float transProgress = 0F;
    public float prevTransProgress = 0F;
    public float colorShiftProgress = 0F;
    public float prevColorShiftProgress = 0F;
    public float swimProgress = 0F;
    public float prevSwimProgress = 0F;

    protected EntityMimicOctopus(EntityType type, World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 16D).createMutableAttribute(Attributes.ARMOR, 0.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.35F);
    }

    public void tick() {
        super.tick();
        this.prevTransProgress = transProgress;
        this.prevColorShiftProgress = colorShiftProgress;
        this.prevSwimProgress = swimProgress;
        this.setAir(100);
        if(prevMimicState != this.getMimicState() && transProgress < 5.0F){
            transProgress += 0.25F;
        }
        if(prevMimicState == this.getMimicState() && transProgress > 0F){
            transProgress -= 0.25F;
        }
        if(prevMimickedBlock != this.getMimickedBlock() && colorShiftProgress < 5.0F){
            colorShiftProgress += 0.25F;
        }
        if(prevMimickedBlock == this.getMimickedBlock() && colorShiftProgress > 0F){
            colorShiftProgress -= 0.25F;
        }
        if(ticksExisted % 20 == 0 ){
            BlockPos down = getPositionDown();
            if(!world.isAirBlock(down)){
                this.setMimickedBlock(world.getBlockState(down));
            }
        }
        this.setMimicState(MimicState.CREEPER);
    }

    private BlockPos getPositionDown() {
        BlockPos pos = new BlockPos(this.getPosX(), this.getPosYEye(), this.getPosZ());
        while(pos.getY() > 1 &&(world.isAirBlock(pos) || world.getBlockState(pos).getMaterial() == Material.WATER)){
            pos = pos.down();
        }
        return pos;
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(MIMIC_ORDINAL, 0);
        this.dataManager.register(MIMICKED_BLOCK, Optional.empty());
    }

    public MimicState getMimicState() {
        return MimicState.values()[dataManager.get(MIMIC_ORDINAL)];
    }

    public void setMimicState(MimicState state) {
        if(getMimicState() != state){
            prevMimicState = getMimicState();
            prevTransProgress = 0.0F;
            transProgress = 0.0F;
        }
        this.dataManager.set(MIMIC_ORDINAL, state.ordinal());
    }


    public void setMimickedBlock(@Nullable BlockState state) {
        if(getMimickedBlock() != state && getMimickedBlock() != null){
            prevMimickedBlock = getMimickedBlock();
            prevColorShiftProgress = 0.0F;
            colorShiftProgress = 0.0F;
        }
        this.dataManager.set(MIMICKED_BLOCK, Optional.ofNullable(state));
    }

    @Nullable
    public BlockState getMimickedBlock() {
        return this.dataManager.get(MIMICKED_BLOCK).orElse((BlockState)null);
    }

    public enum MimicState {
        OVERLAY,
        CREEPER,
        GUARDIAN,
        PUFFERFISH
    }
}
