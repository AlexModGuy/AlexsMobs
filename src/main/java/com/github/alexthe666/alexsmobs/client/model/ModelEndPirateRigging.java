package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;

public class ModelEndPirateRigging extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox box;

    public ModelEndPirateRigging() {
        texWidth = 32;
        texHeight = 32;

        box = new AdvancedModelBox(this, "root");
        box.setPos(0.0F, 20.0F, 0.0F);
        box.setTextureOffset(0, 0).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(box);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(box);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        this.resetToDefaultPose();
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }

    public void animate(Direction direction) {
        this.resetToDefaultPose();
        if(direction.getAxis().isHorizontal()){
            this.box.rotateAngleX = (float) (Math.PI / 2F);
            this.box.rotateAngleY = (float) Math.toRadians(direction.toYRot());
        }
    }
}