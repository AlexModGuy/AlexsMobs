package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.entity.EntityRoadrunner;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class ModelFly extends AdvancedEntityModel<EntityFly> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox eyeL;
    private final AdvancedModelBox eyeR;
    private final AdvancedModelBox left_wing;
    private final AdvancedModelBox right_wing;
    private final AdvancedModelBox leg1;
    private final AdvancedModelBox leg2;
    private final AdvancedModelBox leg3;
    private final AdvancedModelBox mouth;

    public ModelFly() {
        textureWidth = 64;
        textureHeight = 64;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, -5.5F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-3.5F, -3.5F, -5.0F, 7.0F, 7.0F, 11.0F, 0.0F, false);

        eyeL = new AdvancedModelBox(this);
        eyeL.setRotationPoint(2.7F, -0.5F, -4.0F);
        body.addChild(eyeL);
        eyeL.setTextureOffset(10, 29).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 4.0F, 3.0F, 0.0F, false);

        eyeR = new AdvancedModelBox(this);
        eyeR.setRotationPoint(-2.7F, -0.5F, -4.0F);
        body.addChild(eyeR);
        eyeR.setTextureOffset(10, 29).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 4.0F, 3.0F, 0.0F, true);

        left_wing = new AdvancedModelBox(this);
        left_wing.setRotationPoint(1.5F, -3.5F, -3.0F);
        body.addChild(left_wing);
        left_wing.setTextureOffset(25, 25).addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.0F, false);

        right_wing = new AdvancedModelBox(this);
        right_wing.setRotationPoint(-1.5F, -3.5F, -3.0F);
        body.addChild(right_wing);
        right_wing.setTextureOffset(25, 25).addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.0F, true);

        leg1 = new AdvancedModelBox(this);
        leg1.setRotationPoint(0.0F, 3.5F, -2.0F);
        body.addChild(leg1);
        leg1.setTextureOffset(25, 19).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 2.0F, 0.0F, 0.0F, false);

        leg2 = new AdvancedModelBox(this);
        leg2.setRotationPoint(0.0F, 3.5F, 0.0F);
        body.addChild(leg2);
        leg2.setTextureOffset(0, 8).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 2.0F, 0.0F, 0.0F, false);

        leg3 = new AdvancedModelBox(this);
        leg3.setRotationPoint(0.0F, 3.5F, 3.0F);
        body.addChild(leg3);
        leg3.setTextureOffset(0, 5).addBox(-2.5F, 0.0F, -1.0F, 5.0F, 2.0F, 0.0F, 0.0F, false);

        mouth = new AdvancedModelBox(this);
        mouth.setRotationPoint(0.0F, 3.5F, -4.5F);
        body.addChild(mouth);
        mouth.setTextureOffset(0, 19).addBox(-0.5F, -1.0F, -0.7F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.isChild) {
            matrixStackIn.push();
            matrixStackIn.scale(0.65F, 0.65F, 0.65F);
            matrixStackIn.translate(0.0D, 0.95D, 0.125D);
            getParts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.pop();
        } else {
            matrixStackIn.push();
            getParts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.pop();
        }

    }

    @Override
    public void setRotationAngles(EntityFly entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float flySpeed = 1.4F;
        float flyDegree = 0.8F;
        float idleSpeed = 1.4F;
        float idleDegree = 0.8F;
        this.walk(mouth, idleSpeed * 0.2F, idleDegree * 0.2F, false, -1, 0.2F, ageInTicks, 1);
        this.flap(mouth, idleSpeed * 0.2F, idleDegree * 0.1F, false, -2, 0F, ageInTicks, 1);
        boolean flag = entityIn.isOnGround() && entityIn.getMotion().lengthSquared() < 1.0E-7D;
        if(flag){
            this.left_wing.rotateAngleZ = (float) Math.toRadians(-35);
            this.right_wing.rotateAngleZ = (float) Math.toRadians(35);
            this.swing(leg1, flySpeed * 0.6F, flyDegree * 0.4F, false, 1, 0F, limbSwing, limbSwingAmount);
            this.swing(leg2, flySpeed * 0.6F, flyDegree * 0.4F, false, 2, 0F, limbSwing, limbSwingAmount);
            this.swing(leg3, flySpeed * 0.6F, flyDegree * 0.4F, false, 3, 0F, limbSwing, limbSwingAmount);
        }else{
            this.flap(left_wing, flySpeed * 1.3F, flyDegree, true, 0, 0.2F, ageInTicks, 1);
            this.flap(right_wing, flySpeed * 1.3F, flyDegree, false, 0, 0.2F, ageInTicks, 1);
            this.walk(leg1, flySpeed * 0.2F, flyDegree * 0.2F, false, 1, 0.2F, ageInTicks, 1);
            this.walk(leg2, flySpeed * 0.2F, flyDegree * 0.2F, false, 2, 0.2F, ageInTicks, 1);
            this.walk(leg3, flySpeed * 0.2F, flyDegree * 0.2F, false, 3, 0.2F, ageInTicks, 1);
        }
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, eyeL, eyeR, left_wing, right_wing, leg1, leg2, leg3, mouth);
    }

    public void setRotationAngle(AdvancedModelBox advancedModelBox, float x, float y, float z) {
        advancedModelBox.rotateAngleX = x;
        advancedModelBox.rotateAngleY = y;
        advancedModelBox.rotateAngleZ = z;
    }
}