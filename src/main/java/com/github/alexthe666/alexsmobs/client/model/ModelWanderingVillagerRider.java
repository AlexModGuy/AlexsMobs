package com.github.alexthe666.alexsmobs.client.model;

import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class ModelWanderingVillagerRider extends VillagerModel {
    public ModelWanderingVillagerRider(ModelPart part) {
        super(part);

    }

    @Override
    public void setupAnim(Entity entity, float f, float f1, float f2, float f3, float f4) {
        super.setupAnim(entity, f, f1, f2, f3, f4);
   /*     if(riding){
            this.leg0.rotateAngleX = -1.4137167F;
            this.leg0.rotateAngleY = 0.31415927F;
            this.leg0.rotateAngleZ = 0.07853982F;
            this.leg1.rotateAngleX = -1.4137167F;
            this.leg1.rotateAngleY = -0.31415927F;
            this.leg1.rotateAngleZ = -0.07853982F;
        }else{
            this.leg0.rotateAngleY = 0F;
            this.leg0.rotateAngleZ = 0F;
            this.leg1.rotateAngleY = 0F;
            this.leg1.rotateAngleZ = 0F;

        }*/
    }
}