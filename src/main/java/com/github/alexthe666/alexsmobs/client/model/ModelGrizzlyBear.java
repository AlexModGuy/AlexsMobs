package com.github.alexthe666.alexsmobs.client.model;// Made with Blockbench 3.7.2

import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelGrizzlyBear extends AdvancedEntityModel<EntityGrizzlyBear> {
    public final AdvancedModelBox root;
    public final AdvancedModelBox Body;
    public final AdvancedModelBox bodyfront;
    public final AdvancedModelBox FrontlegR;
    public final AdvancedModelBox FrontlegL;
    public final AdvancedModelBox head;
    public final AdvancedModelBox EarL;
    public final AdvancedModelBox EarR;
    public final AdvancedModelBox snout;
    public final AdvancedModelBox bodymid;
    public final AdvancedModelBox bodyback;
    public final AdvancedModelBox backlegL;
    public final AdvancedModelBox backlegR;
    private ModelAnimator animator;

    public ModelGrizzlyBear() {
        textureWidth = 112;
        textureHeight = 112;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        Body = new AdvancedModelBox(this);
        Body.setRotationPoint(0.0F, -5.4444F, 0.2778F);
        root.addChild(Body);
        setRotationAngle(Body, -3.1416F, 0.0F, 3.1416F);


        bodyfront = new AdvancedModelBox(this);
        bodyfront.setRotationPoint(0.0F, -5.5556F, -0.2778F);
        Body.addChild(bodyfront);
        bodyfront.setTextureOffset(0, 26).addBox(-6.0F, -9.0F, 7.0F, 12.0F, 12.0F, 7.0F, 0.0F, false);

        FrontlegR = new AdvancedModelBox(this);
        FrontlegR.setRotationPoint(-3.0F, 3.0F, 10.0F);
        bodyfront.addChild(FrontlegR);
        FrontlegR.setTextureOffset(28, 45).addBox(-3.0F, 0.0F, -3.0F, 5.0F, 8.0F, 5.0F, 0.0F, false);

        FrontlegL = new AdvancedModelBox(this);
        FrontlegL.setRotationPoint(3.0F, 3.0F, 10.0F);
        bodyfront.addChild(FrontlegL);
        FrontlegL.setTextureOffset(0, 58).addBox(-2.0F, 0.0F, -3.0F, 5.0F, 8.0F, 5.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -4.0F, 14.0F);
        bodyfront.addChild(head);
        head.setTextureOffset(0, 45).addBox(-4.0F, -3.0F, 0.0F, 8.0F, 7.0F, 6.0F, 0.0F, false);

        EarL = new AdvancedModelBox(this);
        EarL.setRotationPoint(-2.5F, -2.5F, 2.5F);
        head.addChild(EarL);
        EarL.setTextureOffset(0, 71).addBox(-2.5F, -2.5F, -0.5F, 3.0F, 3.0F, 1.0F, 0.0F, false);

        EarR = new AdvancedModelBox(this);
        EarR.setRotationPoint(2.5F, -2.5F, 2.5F);
        head.addChild(EarR);
        EarR.setTextureOffset(40, 65).addBox(-0.5F, -2.5F, -0.5F, 3.0F, 3.0F, 1.0F, 0.0F, false);

        snout = new AdvancedModelBox(this);
        snout.setRotationPoint(0.0F, 2.0F, 6.0F);
        head.addChild(snout);
        snout.setTextureOffset(40, 58).addBox(-3.0F, -2.0F, 0.0F, 6.0F, 4.0F, 3.0F, 0.0F, false);

        bodymid = new AdvancedModelBox(this);
        bodymid.setRotationPoint(0.0F, -5.5556F, -11.2778F);
        Body.addChild(bodymid);
        bodymid.setTextureOffset(0, 0).addBox(-7.0F, -10.0F, 5.0F, 14.0F, 13.0F, 13.0F, 0.0F, false);
        bodymid.setTextureOffset(54, 0).addBox(-7.0F, 3.0F, 5.0F, 14.0F, 3.0F, 13.0F, 0.0F, false);

        bodyback = new AdvancedModelBox(this);
        bodyback.setRotationPoint(0.0F, -5.5556F, -11.2778F);
        Body.addChild(bodyback);
        bodyback.setTextureOffset(38, 26).addBox(-6.0F, -9.0F, -1.0F, 12.0F, 12.0F, 6.0F, 0.0F, false);

        backlegL = new AdvancedModelBox(this);
        backlegL.setRotationPoint(3.0F, 3.0F, 4.0F);
        bodyback.addChild(backlegL);
        backlegL.setTextureOffset(20, 58).addBox(-2.0F, 0.0F, -4.0F, 5.0F, 8.0F, 5.0F, 0.0F, false);

        backlegR = new AdvancedModelBox(this);
        backlegR.setRotationPoint(-3.0F, 3.0F, 4.0F);
        bodyback.addChild(backlegR);
        backlegR.setTextureOffset(48, 45).addBox(-3.0F, 0.0F, -4.0F, 5.0F, 8.0F, 5.0F, 0.0F, false);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    public void addChildSub(AdvancedModelBox parent, AdvancedModelBox child) {
        child.setRotationPoint(child.rotationPointX - parent.rotationPointX, child.rotationPointY - parent.rotationPointY, child.rotationPointZ - parent.rotationPointZ);
        parent.addChild(child);
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        this.resetToDefaultPose();
        animator.update(entity);
        animator.setAnimation(EntityGrizzlyBear.ANIMATION_MAUL);
        animator.startKeyframe(4);
        animator.rotate(bodyfront, (float)Math.toRadians(6F), 0, 0);
        animator.rotate(FrontlegL, (float)Math.toRadians(70F), 0, 0);
        animator.rotate(FrontlegR, (float)Math.toRadians(-25F), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(bodyfront, (float)Math.toRadians(2F), 0, 0);
        animator.rotate(FrontlegL, (float)Math.toRadians(-25F), 0, 0);
        animator.rotate(FrontlegR, (float)Math.toRadians(70F), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(bodyfront, (float)Math.toRadians(6F), 0, 0);
        animator.rotate(FrontlegL, (float)Math.toRadians(70F), 0, 0);
        animator.rotate(FrontlegR, (float)Math.toRadians(-25F), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(bodyfront, (float)Math.toRadians(2F), 0, 0);
        animator.rotate(FrontlegL, (float)Math.toRadians(-25F), 0, 0);
        animator.rotate(FrontlegR, (float)Math.toRadians(70F), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(4);
        animator.endKeyframe();
        animator.setAnimation(EntityGrizzlyBear.ANIMATION_SWIPE_R);
        animator.startKeyframe(7);
        animator.rotate(bodyfront, 0, (float)Math.toRadians(20F), 0);
        animator.rotate(bodymid, 0, (float)Math.toRadians(10F), 0);
        animator.rotate(head, 0, 0, (float)Math.toRadians(10F));
        animator.rotate(FrontlegL, (float)Math.toRadians(65F), 0, (float)Math.toRadians(-100F));
        animator.rotate(FrontlegR, (float)Math.toRadians(-15F), 0, (float)Math.toRadians(10F));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(bodyfront, 0, (float)Math.toRadians(-30F), 0);
        animator.rotate(bodymid, 0, (float)Math.toRadians(-15F), 0);
        animator.rotate(head, 0, 0, (float)Math.toRadians(0));
        animator.rotate(FrontlegL, (float)Math.toRadians(20F), 0, (float)Math.toRadians(80F));
        animator.rotate(FrontlegR, (float)Math.toRadians(-15F), 0, (float)Math.toRadians(20F));
        animator.endKeyframe();
        animator.resetKeyframe(3);
        animator.setAnimation(EntityGrizzlyBear.ANIMATION_SWIPE_L);
        animator.startKeyframe(7);
        animator.rotate(bodyfront, 0, (float)Math.toRadians(-20F), 0);
        animator.rotate(bodymid, 0, (float)Math.toRadians(-10F), 0);
        animator.rotate(head, 0, 0, (float)Math.toRadians(-10F));
        animator.rotate(FrontlegR, (float)Math.toRadians(65F), 0, (float)Math.toRadians(100F));
        animator.rotate(FrontlegL, (float)Math.toRadians(-15F), 0, (float)Math.toRadians(-10F));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(bodyfront, 0, (float)Math.toRadians(30F), 0);
        animator.rotate(bodymid, 0, (float)Math.toRadians(15F), 0);
        animator.rotate(head, 0, 0, (float)Math.toRadians(0));
        animator.rotate(FrontlegR, (float)Math.toRadians(-20F), 0, (float)Math.toRadians(-80F));
        animator.rotate(FrontlegL, (float)Math.toRadians(15F), 0, (float)Math.toRadians(-20F));
        animator.endKeyframe();
        animator.resetKeyframe(3);

        animator.setAnimation(EntityGrizzlyBear.ANIMATION_SNIFF);
        animator.startKeyframe(3);
        animator.rotate(head, (float)Math.toRadians(20), (float)Math.toRadians(3), 0);
        animator.endKeyframe();
        animator.startKeyframe(3);
        animator.rotate(head, (float)Math.toRadians(-10), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(3);
        animator.rotate(head, (float)Math.toRadians(20), (float)Math.toRadians(-3), 0);
        animator.endKeyframe();
        animator.resetKeyframe(3);
        animator.endKeyframe();
    }

    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.isChild) {
            float f = 1.75F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            matrixStackIn.push();
            matrixStackIn.scale(0.35F, 0.35F, 0.35F);
            matrixStackIn.translate(0.0D, 2.75D, 0.125D);
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
    public void setRotationAngles(EntityGrizzlyBear entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float walkSpeed = 0.7F;
        float walkDegree = 0.7F;
        float eatSpeed = 0.8F;
        float eatDegree = 0.3F;
        float partialTick = Minecraft.getInstance().getRenderPartialTicks();
        float sitProgress = entityIn.prevSitProgress + (entityIn.sitProgress - entityIn.prevSitProgress) * partialTick;
        float standProgress = entityIn.prevStandProgress + (entityIn.standProgress - entityIn.prevStandProgress) * partialTick;


        progressRotationPrev(bodyback, sitProgress, (float)Math.toRadians(80), 0, 0, 10F);
        progressRotationPrev(bodymid, sitProgress, (float)Math.toRadians(70), 0, 0, 10F);
        progressRotationPrev(bodyfront, sitProgress, (float)Math.toRadians(60), 0, 0, 10F);
        progressRotationPrev(head, sitProgress, (float)Math.toRadians(-60), 0, 0, 10F);
        progressRotationPrev(backlegL, sitProgress, 0, (float)Math.toRadians(10), (float)Math.toRadians(-30), 10F);
        progressRotationPrev(backlegR, sitProgress, 0, (float)Math.toRadians(-10), (float)Math.toRadians(30), 10F);
        progressRotationPrev(FrontlegL, sitProgress, (float)Math.toRadians(-15), (float)Math.toRadians(10), 0, 10F);
        progressRotationPrev(FrontlegR, sitProgress, (float)Math.toRadians(-15), (float)Math.toRadians(-10), 0, 10F);
        progressPositionPrev(bodyback, sitProgress, 0, 10, 0, 10F);
        progressPositionPrev(bodymid, sitProgress, 0, 11, -1, 10F);
        progressPositionPrev(bodyfront, sitProgress, 0, 2, -10, 10F);
        progressPositionPrev(head, sitProgress, 0, -2, 1, 10F);
        progressPositionPrev(root, sitProgress, 0, 0, -10, 10F);
        this.faceTarget(netHeadYaw, headPitch, 1, head);

        progressRotationPrev(backlegL, standProgress, (float)Math.toRadians(-80), 0, 0, 10F);
        progressRotationPrev(backlegR, standProgress, (float)Math.toRadians(-80), 0, 0, 10F);
        progressPositionPrev(backlegL, standProgress, 0, -3, -4.5F, 10F);
        progressPositionPrev(backlegR, standProgress, 0, -3, -4.5F, 10F);

        progressRotationPrev(bodyback, standProgress, (float)Math.toRadians(80), 0, 0, 10F);
        progressRotationPrev(bodymid, standProgress, (float)Math.toRadians(90), 0, 0, 10F);
        progressRotationPrev(bodyfront, standProgress, (float)Math.toRadians(70), 0, 0, 10F);
        progressRotationPrev(head, standProgress, (float)Math.toRadians(-80), 0, 0, 10F);
        progressRotationPrev(FrontlegL, standProgress, (float)Math.toRadians(-35), (float)Math.toRadians(-10), 0, 10F);
        progressRotationPrev(FrontlegR, standProgress, (float)Math.toRadians(-35), (float)Math.toRadians(10), 0, 10F);
        progressPositionPrev(bodyback, standProgress, 0, 1, 3, 10F);
        progressPositionPrev(bodymid, standProgress, 0, 1.5F, 4F, 10F);
        progressPositionPrev(bodyfront, standProgress, 0, -6F, -9F, 10F);
        progressPositionPrev(head, standProgress, 0, 0, 2, 10F);
        progressPositionPrev(FrontlegR, standProgress, 0, 0, -1, 10F);
        progressPositionPrev(FrontlegL, standProgress, 0, 0, -1, 10F);
        progressPositionPrev(root, standProgress, 0, 1F, -6, 10F);

        this.walk(backlegL, walkSpeed, walkDegree, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.bob(backlegL, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        this.walk(backlegR, walkSpeed, walkDegree, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.bob(backlegL, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        if(standProgress == 0 && sitProgress == 0){
            this.walk(FrontlegR, walkSpeed, walkDegree, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(FrontlegL, walkSpeed, walkDegree, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.flap(bodymid, walkSpeed, walkDegree * 0.2F, false, 1F, 0, limbSwing, limbSwingAmount);
            this.flap(bodyfront, walkSpeed, walkDegree * 0.2F, false, 2F, 0, limbSwing, limbSwingAmount);
        }else{
            this.walk(FrontlegR, walkSpeed, walkDegree * 0.1F, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(FrontlegL, walkSpeed, walkDegree * 0.1F, true, 0F, 0F, limbSwing, limbSwingAmount);
            if(entityIn.isEating()){
                this.walk(FrontlegR, eatSpeed, eatDegree, false, 1F, 0.3F, ageInTicks, 1);
                this.walk(FrontlegL, eatSpeed, eatDegree, false, 1F, 0.3F, ageInTicks, 1);
                this.walk(bodyfront, eatSpeed, eatDegree * 0.1F, false, 2F, 0.3F, ageInTicks, 1);
                this.walk(head, eatSpeed, eatDegree * 0.3F, true, 1F, 0.3F, ageInTicks, 1);
            }
        }
        this.flap(bodyback, walkSpeed, walkDegree * 0.2F, false, 1.5F, 0, limbSwing, limbSwingAmount);
        this.flap(head, walkSpeed, walkDegree * -0.1F, false, 2F, 0, limbSwing, limbSwingAmount);
        this.bob(bodyfront, walkSpeed, walkDegree, true, limbSwing, limbSwingAmount);
        this.bob(bodyback, walkSpeed, walkDegree, true, limbSwing, limbSwingAmount);
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root,
                Body,
                bodyfront,
                FrontlegR,
                FrontlegL,
                head,
                EarL,
                EarR,
                snout,
                bodymid,
                bodyback,
                backlegL,
                backlegR);
    }

    public void setRotationAngle(AdvancedModelBox box, float x, float y, float z) {
        box.rotateAngleX = x;
        box.rotateAngleY = y;
        box.rotateAngleZ = z;
    }
}