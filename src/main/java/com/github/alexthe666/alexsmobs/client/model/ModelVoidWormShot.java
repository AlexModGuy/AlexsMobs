package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityVoidWormShot;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelVoidWormShot extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox glass;
    private final AdvancedModelBox cube;

    public ModelVoidWormShot() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this);
        root.setPos(0.0F, 24.0F, 0.0F);


        glass = new AdvancedModelBox(this);
        glass.setPos(0.0F, -5.0F, 0.0F);
        root.addChild(glass);
        glass.texOffs(0, 21).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

        cube = new AdvancedModelBox(this);
        cube.setPos(0.0F, -5.0F, 0.0F);
        root.addChild(cube);
        cube.texOffs(0, 0).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, cube, glass);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        this.resetToDefaultPose();
    }


    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.xRot = x;
        AdvancedModelBox.yRot = y;
        AdvancedModelBox.zRot = z;
    }

    public void animate(EntityVoidWormShot entityIn, float ageInTicks) {
        this.resetToDefaultPose();
        float innerScale = (float) (1.0F + 0.25F * Math.abs(Math.sin(ageInTicks * 0.6F)));
        float outerScale = (float) (1.0F + 0.5F * Math.abs(Math.cos(ageInTicks * 0.2F)));
        this.glass.setScale(innerScale, innerScale, innerScale);
        this.glass.xRot += ageInTicks * 0.25F;
        this.cube.xRot += ageInTicks * 0.5F;
        this.glass.setShouldScaleChildren(false);
        this.cube.setScale(outerScale, outerScale, outerScale);
    }
}