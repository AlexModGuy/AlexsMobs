package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityFlyingFish;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Mth;

public class ModelFlyingFish extends AdvancedEntityModel<EntityFlyingFish> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox left_pectoralFin;
    private final AdvancedModelBox right_pectoralFin;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox tail_fin;
    private final AdvancedModelBox left_pelvicFin;
    private final AdvancedModelBox right_pelvicFin;

    public ModelFlyingFish() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setRotationPoint(0.0F, -2.0F, -2.0F);
        root.addChild(body);
        body.setTextureOffset(11, 16).addBox(-1.5F, -2.0F, -4.0F, 3.0F, 4.0F, 8.0F, 0.0F, false);
        body.setTextureOffset(10, 6).addBox(-1.5F, -2.0F, -5.0F, 3.0F, 3.0F, 1.0F, 0.0F, false);

        left_pectoralFin = new AdvancedModelBox(this, "left_pectoralFin");
        left_pectoralFin.setRotationPoint(1.5F, 0.0F, -1.0F);
        body.addChild(left_pectoralFin);
        setRotationAngle(left_pectoralFin, -0.7503F, -1.3169F, -0.8498F);
        left_pectoralFin.setTextureOffset(0, 0).addBox(0.0F, 0.0F, -2.0F, 13.0F, 0.0F, 5.0F, 0.0F, false);

        right_pectoralFin = new AdvancedModelBox(this, "right_pectoralFin");
        right_pectoralFin.setRotationPoint(-1.5F, 0.0F, -1.0F);
        body.addChild(right_pectoralFin);
        setRotationAngle(right_pectoralFin, -0.7503F, 1.3169F, 0.8498F);
        right_pectoralFin.setTextureOffset(0, 0).addBox(-13.0F, 0.0F, -2.0F, 13.0F, 0.0F, 5.0F, 0.0F, true);

        tail = new AdvancedModelBox(this, "tail");
        tail.setRotationPoint(0.0F, -1.0F, 4.0F);
        body.addChild(tail);
        tail.setTextureOffset(26, 6).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 3.0F, 8.0F, 0.0F, false);
        tail.setTextureOffset(0, 0).addBox(0.0F, -2.0F, 2.0F, 0.0F, 1.0F, 2.0F, 0.0F, false);

        tail_fin = new AdvancedModelBox(this, "tail_fin");
        tail_fin.setRotationPoint(0.0F, 1.0F, 6.0F);
        tail.addChild(tail_fin);
        tail_fin.setTextureOffset(0, 6).addBox(0.0F, -5.0F, -1.0F, 0.0F, 8.0F, 9.0F, 0.0F, false);

        left_pelvicFin = new AdvancedModelBox(this, "left_pelvicFin");
        left_pelvicFin.setRotationPoint(1.0F, 2.0F, 1.0F);
        tail.addChild(left_pelvicFin);
        setRotationAngle(left_pelvicFin, 0.0F, 0.0F, -0.5672F);
        left_pelvicFin.setTextureOffset(0, 6).addBox(0.0F, 0.0F, 0.0F, 0.0F, 4.0F, 4.0F, 0.0F, false);

        right_pelvicFin = new AdvancedModelBox(this, "right_pelvicFin");
        right_pelvicFin.setRotationPoint(-1.0F, 2.0F, 1.0F);
        tail.addChild(right_pelvicFin);
        setRotationAngle(right_pelvicFin, 0.0F, 0.0F, 0.5672F);
        right_pelvicFin.setTextureOffset(0, 6).addBox(0.0F, 0.0F, 0.0F, 0.0F, 4.0F, 4.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public void setupAnim(EntityFlyingFish entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float idleSpeed = 0.2F;
        float idleDegree = 0.3F;
        float swimSpeed = 0.55F;
        float swimDegree = 0.5F;
        float partialTick = ageInTicks - entity.tickCount;
        float flyProgress = entity.prevFlyProgress + (entity.flyProgress - entity.prevFlyProgress) * partialTick;
        float landProgress = entity.prevOnLandProgress + (entity.onLandProgress - entity.prevOnLandProgress) * partialTick;
        float swimProgress = Math.max(0, 5F - flyProgress) * 0.2F;
        AdvancedModelBox[] tailBoxes = new AdvancedModelBox[]{body, tail, tail_fin};
        progressRotationPrev(left_pectoralFin, flyProgress, Maths.rad(45), Maths.rad(80), Maths.rad(45), 5F);
        progressRotationPrev(right_pectoralFin, flyProgress, Maths.rad(45), Maths.rad(-80), Maths.rad(-45), 5F);
        progressRotationPrev(left_pelvicFin, flyProgress, 0, 0, Maths.rad(-35), 5F);
        progressRotationPrev(right_pelvicFin, flyProgress, 0, 0, Maths.rad(35), 5F);
        progressPositionPrev(left_pectoralFin, flyProgress, 0, -1, 1, 5F);
        progressPositionPrev(right_pectoralFin, flyProgress, 0, -1, 1, 5F);
        progressRotationPrev(body, landProgress, 0, 0, Maths.rad(-90), 5F);
        progressRotation(left_pectoralFin, landProgress, 0, 0,  Maths.rad(70), 5F);
        progressRotation(right_pectoralFin, landProgress, 0, 0, Maths.rad(-80), 5F);
        this.bob(body, idleSpeed, idleDegree, false, ageInTicks, 1);
        this.chainSwing(tailBoxes, idleSpeed, idleDegree * 0.1F, -2.5F, ageInTicks, 1);
        this.flap(left_pelvicFin, idleSpeed, idleDegree, false, 3, 0.05F, ageInTicks, 1);
        this.flap(right_pelvicFin, idleSpeed, idleDegree, true, 3, 0.05F, ageInTicks, 1);
        this.flap(left_pectoralFin, idleSpeed, idleDegree * 0.25F, true, -1, -0.12F, ageInTicks, 1);
        this.flap(right_pectoralFin, idleSpeed, idleDegree * 0.25F, false, -1, -0.12F, ageInTicks, 1);
        this.chainSwing(tailBoxes, swimSpeed, swimDegree * 0.9F, -2, limbSwing, limbSwingAmount * swimProgress);
        this.chainSwing(tailBoxes, swimSpeed, swimDegree * 0.3F, -1, limbSwing, limbSwingAmount * flyProgress * 0.2F);
        this.flap(left_pectoralFin, swimSpeed * 2F, swimDegree * 0.2F, true, 1, 0.1F, ageInTicks, flyProgress * 0.2F);
        this.flap(right_pectoralFin, swimSpeed * 2F, swimDegree * 0.2F, false, 1, 0.1F, ageInTicks, flyProgress * 0.2F);
        this.flap(left_pelvicFin, swimSpeed * 2F, swimDegree * 0.2F, true, 1, 0.3F, ageInTicks, flyProgress * 0.2F);
        this.flap(right_pelvicFin, swimSpeed * 2F, swimDegree * 0.2F, false, 1, 0.3F, ageInTicks, flyProgress * 0.2F);
        this.body.rotateAngleX += headPitch * Mth.DEG_TO_RAD;
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, tail, tail_fin, left_pectoralFin, left_pelvicFin, right_pectoralFin, right_pelvicFin);
    }

    public void setRotationAngle(AdvancedModelBox modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}