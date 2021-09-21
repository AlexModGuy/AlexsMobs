package com.github.alexthe666.alexsmobs.client.model;

import net.minecraft.client.model.VillagerModel;
import net.minecraft.world.entity.Entity;

public class ModelWanderingVillagerRider extends VillagerModel {
    public ModelWanderingVillagerRider() {
        super(0);

    }

    @Override
    public void setupAnim(Entity entity, float f, float f1, float f2, float f3, float f4) {
        super.setupAnim(entity, f, f1, f2, f3, f4);
        if(riding){
            this.leg0.xRot = -1.4137167F;
            this.leg0.yRot = 0.31415927F;
            this.leg0.zRot = 0.07853982F;
            this.leg1.xRot = -1.4137167F;
            this.leg1.yRot = -0.31415927F;
            this.leg1.zRot = -0.07853982F;
        }else{
            this.leg0.yRot = 0F;
            this.leg0.zRot = 0F;
            this.leg1.yRot = 0F;
            this.leg1.zRot = 0F;

        }
    }
}