package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class ModelCockroach extends AdvancedEntityModel<EntityCockroach> {
    public AdvancedModelBox root;
    public AdvancedModelBox body;
    public AdvancedModelBox leg1_left;
    public AdvancedModelBox leg1_right;
    public AdvancedModelBox leg2_left;
    public AdvancedModelBox leg2_right;
    public AdvancedModelBox leg3_left;
    public AdvancedModelBox leg3_right;
    public AdvancedModelBox wing_right;
    public AdvancedModelBox wing_left;
    public AdvancedModelBox frontbody;
    public AdvancedModelBox head;
    public AdvancedModelBox antenna_left;
    public AdvancedModelBox antenna_right;

    public ModelCockroach() {
        textureWidth = 128;
        textureHeight = 128;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, -4.75F, -4.25F);
        root.addChild(body);
        body.setTextureOffset(34, 34).addBox(-4.0F, -1.25F, 0.25F, 8.0F, 3.0F, 17.0F, 0.0F, false);

        leg1_left = new AdvancedModelBox(this);
        leg1_left.setRotationPoint(4.0F, 1.25F, 0.75F);
        body.addChild(leg1_left);
        setRotationAngle(leg1_left, 0.0F, 0.48F, 0.3054F);
        leg1_left.setTextureOffset(0, 47).addBox(-1.0F, 0.0F, -3.0F, 11.0F, 0.0F, 8.0F, 0.0F, false);

        leg1_right = new AdvancedModelBox(this);
        leg1_right.setRotationPoint(-4.0F, 1.25F, 0.75F);
        body.addChild(leg1_right);
        setRotationAngle(leg1_right, 0.0F, -0.48F, -0.3054F);
        leg1_right.setTextureOffset(0, 47).addBox(-10.0F, 0.0F, -3.0F, 11.0F, 0.0F, 8.0F, 0.0F, true);

        leg2_left = new AdvancedModelBox(this);
        leg2_left.setRotationPoint(4.0F, 1.25F, 4.75F);
        body.addChild(leg2_left);
        setRotationAngle(leg2_left, -0.0436F, 0.1309F, 0.2705F);
        leg2_left.setTextureOffset(38, 14).addBox(-1.0F, 0.0F, -1.0F, 13.0F, 0.0F, 8.0F, 0.0F, false);

        leg2_right = new AdvancedModelBox(this);
        leg2_right.setRotationPoint(-4.0F, 1.25F, 4.75F);
        body.addChild(leg2_right);
        setRotationAngle(leg2_right, -0.0436F, -0.1309F, -0.2705F);
        leg2_right.setTextureOffset(38, 14).addBox(-12.0F, 0.0F, -1.0F, 13.0F, 0.0F, 8.0F, 0.0F, true);

        leg3_left = new AdvancedModelBox(this);
        leg3_left.setRotationPoint(4.0F, 1.25F, 8.75F);
        body.addChild(leg3_left);
        setRotationAngle(leg3_left, -0.0524F, -0.0873F, 0.2618F);
        leg3_left.setTextureOffset(38, 0).addBox(-1.0F, 0.0F, -1.0F, 14.0F, 0.0F, 13.0F, 0.0F, false);

        leg3_right = new AdvancedModelBox(this);
        leg3_right.setRotationPoint(-4.0F, 1.25F, 8.75F);
        body.addChild(leg3_right);
        setRotationAngle(leg3_right, -0.0524F, 0.0873F, -0.2618F);
        leg3_right.setTextureOffset(38, 0).addBox(-13.0F, 0.0F, -1.0F, 14.0F, 0.0F, 13.0F, 0.0F, true);

        wing_right = new AdvancedModelBox(this);
        wing_right.setRotationPoint(-4.5F, -1.25F, 0.25F);
        body.addChild(wing_right);
        setRotationAngle(wing_right, 0.0F, 0.0F, -0.1309F);
        wing_right.setTextureOffset(0, 26).addBox(-1.5F, -0.5F, 0.0F, 6.0F, 1.0F, 19.0F, 0.0F, false);

        wing_left = new AdvancedModelBox(this);
        wing_left.setRotationPoint(4.5F, -1.25F, 0.25F);
        body.addChild(wing_left);
        setRotationAngle(wing_left, 0.0F, 0.0F, 0.1309F);
        wing_left.setTextureOffset(0, 26).addBox(-4.5F, -0.5F, 0.0F, 6.0F, 1.0F, 19.0F, 0.0F, true);

        frontbody = new AdvancedModelBox(this);
        frontbody.setRotationPoint(0.0F, 0.25F, 0.25F);
        body.addChild(frontbody);
        frontbody.setTextureOffset(32, 55).addBox(-5.0F, -2.5F, -7.0F, 10.0F, 4.0F, 7.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -1.5F, -7.0F);
        frontbody.addChild(head);
        head.setTextureOffset(0, 0).addBox(-2.5F, 0.0F, -2.0F, 5.0F, 4.0F, 2.0F, 0.0F, false);

        antenna_left = new AdvancedModelBox(this);
        antenna_left.setRotationPoint(1.5F, 1.0F, -2.0F);
        head.addChild(antenna_left);
        setRotationAngle(antenna_left, -0.2618F, -0.6545F, 0.0F);
        antenna_left.setTextureOffset(0, 0).addBox(0.0F, 0.0F, -25.0F, 6.0F, 0.0F, 25.0F, 0.0F, false);

        antenna_right = new AdvancedModelBox(this);
        antenna_right.setRotationPoint(-1.5F, 1.0F, -2.0F);
        head.addChild(antenna_right);
        setRotationAngle(antenna_right, -0.2618F, 0.6545F, 0.0F);
        antenna_right.setTextureOffset(0, 0).addBox(-6.0F, 0.0F, -25.0F, 6.0F, 0.0F, 25.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, frontbody, head, antenna_left, antenna_right, leg1_left, leg1_right, leg2_left, leg2_right, leg3_left, leg3_right, wing_left, wing_right);
    }

    @Override
    public void setRotationAngles(EntityCockroach entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float idleSpeed = 0.25F;
        float idleDegree = 0.25F;
        float flySpeed = 0.5F;
        float flyDegree = 0.5F;
        float walkSpeed = 1.25F;
        float walkDegree = 0.5F;
        float partialTick = Minecraft.getInstance().getRenderPartialTicks();
        float danceProgress = entity.prevDanceProgress + (entity.danceProgress - entity.prevDanceProgress) * partialTick;
        progressRotationPrev(body, danceProgress, (float) Math.toRadians(-70), 0, 0, 5F);
        progressRotationPrev(leg1_left, danceProgress, 0, (float) Math.toRadians(-10), 0, 5F);
        progressRotationPrev(leg1_right, danceProgress, 0, (float) Math.toRadians(10), 0, 5F);
        progressRotationPrev(leg2_left, danceProgress, 0, (float) Math.toRadians(-10), 0, 5F);
        progressRotationPrev(leg2_right, danceProgress, 0, (float) Math.toRadians(10), 0, 5F);
        progressPositionPrev(body, danceProgress, 0, -15, 2, 5F);
        if (danceProgress > 0) {
            this.walk(antenna_left, 0.5F, 0.5F, false, -1, -0.05F, ageInTicks, 1);
            this.walk(antenna_right, 0.5F, 0.5F, false, -1, -0.05F, ageInTicks, 1);
            if (entity.hasMaracas()) {
                this.swing(body, 0.5F, 0.15F, false, 0, 0F, ageInTicks, 1);
                this.flap(body, 0.5F, 0.15F, false, 1, 0F, ageInTicks, 1);
                this.bob(body, 0.25F, 10F, true, ageInTicks, 1);
                this.swing(leg1_right, 0.5F, 0.5F, false, 0, -0.05F, ageInTicks, 1);
                this.swing(leg1_left, 0.5F, 0.5F, false, 0, -0.05F, ageInTicks, 1);
                this.swing(leg2_right, 0.5F, 0.5F, false, 2, -0.05F, ageInTicks, 1);
                this.swing(leg2_left, 0.5F, 0.5F, false, 2, -0.05F, ageInTicks, 1);
            } else {
                float spinDegree = MathHelper.wrapDegrees(ageInTicks * 15F);
                body.rotateAngleY = (float) (Math.toRadians(spinDegree) * danceProgress * 0.2F);
                this.bob(body, 0.25F, 10F, true, ageInTicks, 1);
            }
        }
        this.swing(antenna_left, idleSpeed, idleDegree, true, 1, -0.1F, ageInTicks, 1);
        this.swing(antenna_right, idleSpeed, idleDegree, false, 1, -0.1F, ageInTicks, 1);
        this.walk(antenna_left, idleSpeed, idleDegree * 0.25F, false, -1, -0.05F, ageInTicks, 1);
        this.walk(antenna_right, idleSpeed, idleDegree * 0.25F, false, -1, -0.05F, ageInTicks, 1);
        this.walk(head, idleSpeed * 0.5F, idleDegree * 0.5F, false, 0, 0.1F, ageInTicks, 1);
        if (entity.randomWingFlapTick > 0) {
            this.swing(wing_left, flySpeed * 3.3F, flyDegree * 0.6F, true, 0, -0.2F, ageInTicks, 1);
            this.swing(wing_right, flySpeed * 3.3F, flyDegree * 0.6F, false, 0, -0.2F, ageInTicks, 1);
        }
        this.swing(leg1_right, walkSpeed, walkDegree, false, -1, 0F, limbSwing, limbSwingAmount);
        this.swing(leg3_right, walkSpeed, walkDegree, false, 1, 0F, limbSwing, limbSwingAmount);
        this.swing(leg2_left, walkSpeed, walkDegree, false, 0, 0F, limbSwing, limbSwingAmount);
        this.bob(body, walkSpeed, walkDegree * 2.5F, true, limbSwing, limbSwingAmount);
        this.swing(leg1_left, walkSpeed, walkDegree, true, 1, 0F, limbSwing, limbSwingAmount);
        this.swing(leg3_left, walkSpeed, walkDegree, true, -1, 0F, limbSwing, limbSwingAmount);
        this.swing(leg2_right, walkSpeed, walkDegree, true, 0, 0F, limbSwing, limbSwingAmount);
        this.faceTarget(netHeadYaw, headPitch, 1, head);
        if(entity.isHeadless()){
            head.showModel = false;
            antenna_left.showModel = false;
            antenna_right.showModel = false;
        }else{
            head.showModel = true;
            antenna_left.showModel = true;
            antenna_right.showModel = true;
        }
        if(entity.isChild()){
            wing_left.showModel = false;
            wing_right.showModel = false;
        }else{
            wing_left.showModel = true;
            wing_right.showModel = true;
        }
    }

    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.isChild) {
            this.head.setScale(1.5F, 1.5F, 1.5F);
            matrixStackIn.push();
            matrixStackIn.scale(0.65F, 0.65F, 0.65F);
            matrixStackIn.translate(0.0D, 0.815D, 0.125D);
            getParts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.pop();
        } else {
            this.head.setScale(1F, 1F, 1F);
            matrixStackIn.push();
            getParts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.pop();
        }

    }

    public void setRotationAngle(AdvancedModelBox advancedModelBox, float x, float y, float z) {
        advancedModelBox.rotateAngleX = x;
        advancedModelBox.rotateAngleY = y;
        advancedModelBox.rotateAngleZ = z;
    }
}