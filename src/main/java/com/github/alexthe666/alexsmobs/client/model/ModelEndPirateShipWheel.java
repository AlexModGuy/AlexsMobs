package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateShipWheel;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class ModelEndPirateShipWheel extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox wheel;

    public ModelEndPirateShipWheel() {
        texWidth = 64;
        texHeight = 64;


        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);
        wheel = new AdvancedModelBox(this, "wheel");
        wheel.setRotationPoint(0.0F, -2.0F, 0.0F);
        root.addChild(wheel);
        wheel.rotateAngleX = -1.5708F;
        wheel.setTextureOffset(0, 37).addBox(-9.0F, 6.0F, -1.0F, 18.0F, 3.0F, 2.0F, 0.0F, false);
        wheel.setTextureOffset(37, 39).addBox(-3.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, 0.0F, false);
        wheel.setTextureOffset(0, 0).addBox(-15.0F, -15.0F, 0.0F, 30.0F, 30.0F, 0.0F, 0.0F, false);
        wheel.setTextureOffset(0, 31).addBox(-9.0F, -9.0F, -1.0F, 18.0F, 3.0F, 2.0F, 0.0F, false);
        wheel.setTextureOffset(11, 43).addBox(-9.0F, -6.0F, -1.0F, 3.0F, 12.0F, 2.0F, 0.0F, false);
        wheel.setTextureOffset(0, 43).addBox(6.0F, -6.0F, -1.0F, 3.0F, 12.0F, 2.0F, 0.0F, false);  this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, wheel);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    public void renderWheel(TileEntityEndPirateShipWheel wheel, float partialTick) {
        this.resetToDefaultPose();
        this.wheel.rotateAngleY = (float)Math.toRadians(wheel.getWheelRot(partialTick));
    }
}