package com.github.alexthe666.alexsmobs.client.model;

import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.entity.Entity;

public class ModelWanderingVillagerRider extends VillagerModel {
    public ModelWanderingVillagerRider() {
        super(0);

    }

    @Override
    public void setRotationAngles(Entity entity, float f, float f1, float f2, float f3, float f4) {
        super.setRotationAngles(entity, f, f1, f2, f3, f4);
        if(isSitting){
            this.rightVillagerLeg.rotateAngleX = -1.4137167F;
            this.rightVillagerLeg.rotateAngleY = 0.31415927F;
            this.rightVillagerLeg.rotateAngleZ = 0.07853982F;
            this.leftVillagerLeg.rotateAngleX = -1.4137167F;
            this.leftVillagerLeg.rotateAngleY = -0.31415927F;
            this.leftVillagerLeg.rotateAngleZ = -0.07853982F;
        }else{
            this.rightVillagerLeg.rotateAngleY = 0F;
            this.rightVillagerLeg.rotateAngleZ = 0F;
            this.leftVillagerLeg.rotateAngleY = 0F;
            this.leftVillagerLeg.rotateAngleZ = 0F;

        }
    }
}