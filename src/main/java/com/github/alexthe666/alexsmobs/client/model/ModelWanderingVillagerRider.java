package com.github.alexthe666.alexsmobs.client.model;

import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class ModelWanderingVillagerRider extends VillagerModel {
    private ModelPart rightLegRider;
    private ModelPart leftLegRider;


    public ModelWanderingVillagerRider(ModelPart part) {
        super(part);
        this.rightLegRider = part.getChild("right_leg");
        this.leftLegRider = part.getChild("left_leg");
    }

    @Override
    public void setupAnim(Entity entity, float f, float f1, float f2, float f3, float f4) {
        super.setupAnim(entity, f, f1, f2, f3, f4);
     if(riding){
            this.rightLegRider.xRot = -1.4137167F;
            this.rightLegRider.yRot = 0.31415927F;
            this.rightLegRider.zRot = 0.07853982F;
            this.leftLegRider.xRot = -1.4137167F;
            this.leftLegRider.yRot = -0.31415927F;
            this.leftLegRider.zRot = -0.07853982F;
        }else{
            this.rightLegRider.yRot = 0F;
            this.rightLegRider.zRot = 0F;
            this.leftLegRider.yRot = 0F;
            this.leftLegRider.zRot = 0F;
        }
    }
}