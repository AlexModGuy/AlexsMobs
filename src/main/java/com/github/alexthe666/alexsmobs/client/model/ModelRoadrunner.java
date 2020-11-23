package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityRoadrunner;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class ModelRoadrunner extends AdvancedEntityModel<EntityRoadrunner> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox head;
    private final AdvancedModelBox crest;
    private final AdvancedModelBox beak;
    private final AdvancedModelBox legL;
    private final AdvancedModelBox legR;
    private final AdvancedModelBox wingL;
    private final AdvancedModelBox cube_r2;
    private final AdvancedModelBox wingR;
    private final AdvancedModelBox cube_r3;
    private final AdvancedModelBox tail;

    public ModelRoadrunner() {
        textureWidth = 64;
        textureHeight = 64;
        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, -0.5F);
        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, -7.5F, 0.0F);
        root.addChild(body);
        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        body.addChild(cube_r1);
        setRotationAngle(cube_r1, -0.1309F, 0.0F, 0.0F);
        cube_r1.setTextureOffset(0, 0).addBox(-2.0F, -2.5F, -4.5F, 4.0F, 5.0F, 9.0F, 0.0F, false);
        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -1.8F, -3.3F);
        body.addChild(head);
        setRotationAngle(head, 0.5236F, 0.0F, 0.0F);
        head.setTextureOffset(0, 27).addBox(-1.5F, -5.7F, -1.2F, 3.0F, 6.0F, 3.0F, 0.0F, false);
        crest = new AdvancedModelBox(this);
        crest.setRotationPoint(0.0F, -4.7F, 0.4F);
        head.addChild(crest);
        crest.setTextureOffset(0, 0).addBox(0.0F, -3.0F, -1.5F, 0.0F, 4.0F, 4.0F, 0.0F, false);
        beak = new AdvancedModelBox(this);
        beak.setRotationPoint(0.0F, -4.6F, -1.1F);
        head.addChild(beak);
        setRotationAngle(beak, -0.48F, 0.0F, 0.0F);
        beak.setTextureOffset(18, 0).addBox(-0.5F, -0.506F, -3.3706F, 1.0F, 1.0F, 4.0F, 0.0F, false);
        legL = new AdvancedModelBox(this);
        legL.setRotationPoint(1.0F, 2.5F, 1.0F);
        body.addChild(legL);
        legL.setTextureOffset(0, 15).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 5.0F, 2.0F, 0.0F, false);
        legR = new AdvancedModelBox(this);
        legR.setRotationPoint(-1.0F, 2.5F, 1.0F);
        body.addChild(legR);
        legR.setTextureOffset(0, 15).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 5.0F, 2.0F, 0.0F, true);
        wingL = new AdvancedModelBox(this);
        wingL.setRotationPoint(2.5F, -1.8F, -2.0F);
        body.addChild(wingL);
        setRotationAngle(wingL, -0.1309F, 0.0F, 0.0F);
        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(-0.5F, 2.0F, 2.0F);
        wingL.addChild(cube_r2);
        setRotationAngle(cube_r2, -0.1309F, 0.0F, 0.0F);
        cube_r2.setTextureOffset(18, 18).addBox(0.0F, -2.5F, -3.5F, 1.0F, 5.0F, 9.0F, 0.0F, false);
        wingR = new AdvancedModelBox(this);
        wingR.setRotationPoint(-2.5F, -1.8F, -2.0F);
        body.addChild(wingR);
        setRotationAngle(wingR, -0.1309F, 0.0F, 0.0F);
        cube_r3 = new AdvancedModelBox(this);
        cube_r3.setRotationPoint(0.5F, 2.0F, 2.0F);
        wingR.addChild(cube_r3);
        setRotationAngle(cube_r3, -0.1309F, 0.0F, 0.0F);
        cube_r3.setTextureOffset(18, 18).addBox(-1.0F, -2.5F, -3.5F, 1.0F, 5.0F, 9.0F, 0.0F, true);
        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(-0.5F, -0.9F, 4.8F);
        body.addChild(tail);
        setRotationAngle(tail, 0.2182F, 0.0F, 0.0F);
        tail.setTextureOffset(0, 15).addBox(-1.0F, -1.0F, -0.3F, 3.0F, 1.0F, 10.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public void setRotationAngles(EntityRoadrunner entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float walkSpeed = 0.7F;
        float walkDegree = 0.4F;
        float idleSpeed = 0.1F;
        float idleDegree = 0.4F;
        float runProgress = 5F * limbSwingAmount;
        progressRotationPrev(body, runProgress, (float)Math.toRadians(15), 0, 0, 5F);
        progressRotationPrev(head, runProgress, (float)Math.toRadians(5), 0, 0, 5F);
        progressRotationPrev(crest, runProgress, (float)Math.toRadians(-10), 0, 0, 5F);
        progressRotationPrev(legR, runProgress, (float)Math.toRadians(-15), 0, 0, 5F);
        progressRotationPrev(legL, runProgress, (float)Math.toRadians(-15), 0, 0, 5F);
        progressRotationPrev(tail, runProgress, (float)Math.toRadians(-10), 0, 0, 5F);
        progressRotationPrev(wingR, runProgress, (float)Math.toRadians(-10),  (float)Math.toRadians(-30),  (float)Math.toRadians(40), 5F);
        progressRotationPrev(wingL, runProgress, (float)Math.toRadians(-10),  (float)Math.toRadians(30),  (float)Math.toRadians(-40), 5F);

        this.swing(tail, idleSpeed, idleDegree, false, 0F, 0F, ageInTicks, 1);
        this.walk(tail, walkSpeed, walkDegree, false, 2F, 0F, limbSwing, limbSwingAmount);
        this.walk(head, walkSpeed, walkDegree, false, 1F, -0.2F, limbSwing, limbSwingAmount);
        this.walk(legR, walkSpeed, walkDegree * 1.85F, false, 0F, -0.3F, limbSwing, limbSwingAmount);
        this.walk(legL, walkSpeed, walkDegree * 1.85F, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.flap(wingL, walkSpeed, walkDegree, true, 2F, 0.1F, limbSwing, limbSwingAmount);
        this.flap(wingR, walkSpeed, walkDegree, false, 2F, 0.1F, limbSwing, limbSwingAmount);
        this.bob(body, walkSpeed * 2, walkDegree * 2.4F, false, limbSwing, limbSwingAmount);
        float partialTicks = Minecraft.getInstance().getRenderPartialTicks();
        float f = MathHelper.lerp(partialTicks, entityIn.oFlap, entityIn.wingRotation);
        float f1 = MathHelper.lerp(partialTicks, entityIn.oFlapSpeed, entityIn.destPos);
        float wingSwing = (MathHelper.sin(f) + 1.0F) * f1;
        this.flap(wingL, 0.95F,  0.9F, true, 0F, 0.2F, wingSwing, wingSwing > 0 ? 1 : 0);
        this.flap(wingR, 0.95F, 0.9F, false, 0F, 0.2F, wingSwing, wingSwing > 0 ? 1 : 0);
        this.faceTarget(netHeadYaw, headPitch, 1, head);

    }

    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.isChild) {
            float f = 1.75F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            matrixStackIn.push();
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0.125D);
            getParts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.pop();
            head.setScale(1, 1, 1);
        } else {
            matrixStackIn.push();
            getParts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.pop();
        }

    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root,body, cube_r1, head, crest, beak, legL, legR, wingL, cube_r2, wingR, cube_r3, tail);
    }


    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}
