package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityCosmicCod;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Mth;

public class ModelCosmicCod extends AdvancedEntityModel<EntityCosmicCod> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox left_fin;
    private final AdvancedModelBox right_fin;
    private final AdvancedModelBox mouth;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox tail_tip;

    public ModelCosmicCod() {
        texWidth = 32;
        texHeight = 32;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);
        body = new AdvancedModelBox(this, "body");
        body.setRotationPoint(0.0F, -2.0F, -2.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-1.0F, -1.0F, -5.0F, 2.0F, 3.0F, 8.0F, 0.0F, false);
        body.setTextureOffset(15, 15).addBox(0.0F, -3.0F, -2.0F, 0.0F, 2.0F, 5.0F, 0.0F, false);

        left_fin = new AdvancedModelBox(this, "left_fin");
        left_fin.setRotationPoint(1.0F, 2.0F, 0.0F);
        body.addChild(left_fin);
        setRotationAngle(left_fin, 0.0F, 0.0F, -0.829F);
        left_fin.setTextureOffset(0, 12).addBox(0.0F, 0.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, false);

        right_fin = new AdvancedModelBox(this, "right_fin");
        right_fin.setRotationPoint(-1.0F, 2.0F, 0.0F);
        body.addChild(right_fin);
        setRotationAngle(right_fin, 0.0F, 0.0F, 0.829F);
        right_fin.setTextureOffset(0, 12).addBox(0.0F, 0.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, true);

        mouth = new AdvancedModelBox(this, "mouth");
        mouth.setRotationPoint(0.0F, 1.0F, -5.0F);
        body.addChild(mouth);
        mouth.setTextureOffset(0, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 1.0F, 0.0F, false);

        tail = new AdvancedModelBox(this, "tail");
        tail.setRotationPoint(0.0F, 0.0F, 3.0F);
        body.addChild(tail);
        tail.setTextureOffset(15, 6).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 0.0F, false);

        tail_tip = new AdvancedModelBox(this, "tail_tip");
        tail_tip.setRotationPoint(0.0F, 0.0F, 6.0F);
        tail.addChild(tail_tip);
        tail_tip.setTextureOffset(0, 12).addBox(0.0F, -3.0F, -1.0F, 0.0F, 5.0F, 7.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public void setupAnim(EntityCosmicCod entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        this.resetToDefaultPose();
        float idleSpeed = 0.14F;
        float idleDegree = 0.25F;
        float swimSpeed = 0.8F;
        float swimDegree = 0.5F;
        float pitch = (float) Math.toRadians(Mth.rotLerp(ageInTicks - entity.tickCount, entity.prevFishPitch, entity.getFishPitch()));
        this.swing(this.tail, idleSpeed, idleDegree, true, 1F, 0F, ageInTicks, 1);
        this.flap(this.left_fin, idleSpeed, idleDegree, true, 3F, 0F, ageInTicks, 1);
        this.flap(this.right_fin, idleSpeed, idleDegree, false, 3F, 0F, ageInTicks, 1);
        this.bob(this.body, idleSpeed, idleDegree * 4F, false, ageInTicks,  1F);
        this.swing(this.tail, swimSpeed, swimDegree, true, 2F, 0F, limbSwing, limbSwingAmount);
        this.swing(this.tail_tip, swimSpeed, swimDegree * 0.5F, true, 2.5F, 0F, limbSwing, limbSwingAmount);
        this.flap(this.left_fin, swimSpeed, swimDegree, true, 1F, 0.3F, limbSwing, limbSwingAmount);
        this.flap(this.right_fin, swimSpeed, swimDegree, false, 1F, 0.3F, limbSwing, limbSwingAmount);
        this.root.rotateAngleX += pitch;

    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, right_fin, left_fin, mouth, tail, tail_tip);
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}