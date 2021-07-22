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
    public final AdvancedModelBox body;
    public final AdvancedModelBox midbody;
    public final AdvancedModelBox head;
    public final AdvancedModelBox snout;
    public final AdvancedModelBox left_ear;
    public final AdvancedModelBox right_ear;
    public final AdvancedModelBox left_leg;
    public final AdvancedModelBox right_leg;
    public final AdvancedModelBox left_arm;
    public final AdvancedModelBox right_arm;
    public final ModelAnimator animator;

    public ModelGrizzlyBear() {
        textureWidth = 128;
        textureHeight = 128;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, -19.0F, 6.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-7.0F, -7.0F, -19.0F, 14.0F, 15.0F, 28.0F, 0.0F, false);
        body.setTextureOffset(0, 44).addBox(-6.0F, 8.0F, -19.0F, 12.0F, 3.0F, 28.0F, 0.0F, false);

        midbody = new AdvancedModelBox(this);
        midbody.setRotationPoint(0.0F, 0.5F, -4.0F);
        body.addChild(midbody);
        midbody.setTextureOffset(27, 99).addBox(-8.0F, -8.5F, -6.0F, 16.0F, 17.0F, 12.0F, 0.1F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -0.8F, -21.0F);
        body.addChild(head);
        head.setTextureOffset(57, 0).addBox(-5.0F, -5.0F, -6.0F, 10.0F, 10.0F, 8.0F, 0.0F, false);

        snout = new AdvancedModelBox(this);
        snout.setRotationPoint(0.0F, 0.0F, -6.0F);
        head.addChild(snout);
        snout.setTextureOffset(0, 17).addBox(-2.0F, 0.0F, -5.0F, 4.0F, 5.0F, 5.0F, 0.0F, false);

        left_ear = new AdvancedModelBox(this);
        left_ear.setRotationPoint(3.5F, -5.0F, -3.0F);
        head.addChild(left_ear);
        left_ear.setTextureOffset(14, 17).addBox(-1.5F, -2.0F, -1.0F, 3.0F, 2.0F, 2.0F, 0.0F, false);

        right_ear = new AdvancedModelBox(this);
        right_ear.setRotationPoint(-3.5F, -5.0F, -3.0F);
        head.addChild(right_ear);
        right_ear.setTextureOffset(14, 17).addBox(-1.5F, -2.0F, -1.0F, 3.0F, 2.0F, 2.0F, 0.0F, true);

        left_leg = new AdvancedModelBox(this);
        left_leg.setRotationPoint(3.8F, 8.0F, 4.0F);
        body.addChild(left_leg);
        left_leg.setTextureOffset(0, 76).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 11.0F, 8.0F, 0.0F, false);

        right_leg = new AdvancedModelBox(this);
        right_leg.setRotationPoint(-3.8F, 8.0F, 4.0F);
        body.addChild(right_leg);
        right_leg.setTextureOffset(0, 76).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 11.0F, 8.0F, 0.0F, true);

        left_arm = new AdvancedModelBox(this);
        left_arm.setRotationPoint(4.5F, 4.0F, -13.0F);
        body.addChild(left_arm);
        left_arm.setTextureOffset(74, 78).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 18.0F, 7.0F, 0.0F, false);

        right_arm = new AdvancedModelBox(this);
        right_arm.setRotationPoint(-4.5F, 4.0F, -13.0F);
        body.addChild(right_arm);
        right_arm.setTextureOffset(74, 78).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 18.0F, 7.0F, 0.0F, true);
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
        animator.rotate(body, (float)Math.toRadians(6F), 0, 0);
        animator.rotate(left_arm, (float)Math.toRadians(70F), 0, 0);
        animator.rotate(right_arm, (float)Math.toRadians(-25F), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(body, (float)Math.toRadians(2F), 0, 0);
        animator.rotate(left_arm, (float)Math.toRadians(-25F), 0, 0);
        animator.rotate(right_arm, (float)Math.toRadians(70F), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(body, (float)Math.toRadians(6F), 0, 0);
        animator.rotate(left_arm, (float)Math.toRadians(70F), 0, 0);
        animator.rotate(right_arm, (float)Math.toRadians(-25F), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(body, (float)Math.toRadians(2F), 0, 0);
        animator.rotate(left_arm, (float)Math.toRadians(-25F), 0, 0);
        animator.rotate(right_arm, (float)Math.toRadians(70F), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(4);
        animator.endKeyframe();
        animator.setAnimation(EntityGrizzlyBear.ANIMATION_SWIPE_R);
        animator.startKeyframe(7);
        animator.rotate(body, 0, (float)Math.toRadians(20F), 0);
        animator.rotate(midbody, 0, (float)Math.toRadians(10F), 0);
        animator.rotate(head, 0, 0, (float)Math.toRadians(10F));
        animator.rotate(left_arm, (float)Math.toRadians(65F), 0, (float)Math.toRadians(-100F));
        animator.rotate(right_arm, (float)Math.toRadians(-15F), 0, (float)Math.toRadians(10F));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(body, 0, (float)Math.toRadians(-30F), 0);
        animator.rotate(midbody, 0, (float)Math.toRadians(-15F), 0);
        animator.rotate(head, 0, 0, (float)Math.toRadians(0));
        animator.rotate(left_arm, (float)Math.toRadians(20F), 0, (float)Math.toRadians(80F));
        animator.rotate(right_arm, (float)Math.toRadians(-15F), 0, (float)Math.toRadians(20F));
        animator.endKeyframe();
        animator.resetKeyframe(3);
        animator.setAnimation(EntityGrizzlyBear.ANIMATION_SWIPE_L);
        animator.startKeyframe(7);
        animator.rotate(body, 0, (float)Math.toRadians(-20F), 0);
        animator.rotate(midbody, 0, (float)Math.toRadians(-10F), 0);
        animator.rotate(head, 0, 0, (float)Math.toRadians(-10F));
        animator.rotate(right_arm, (float)Math.toRadians(65F), 0, (float)Math.toRadians(100F));
        animator.rotate(left_arm, (float)Math.toRadians(-15F), 0, (float)Math.toRadians(-10F));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(body, 0, (float)Math.toRadians(30F), 0);
        animator.rotate(midbody, 0, (float)Math.toRadians(15F), 0);
        animator.rotate(head, 0, 0, (float)Math.toRadians(0));
        animator.rotate(right_arm, (float)Math.toRadians(-20F), 0, (float)Math.toRadians(-80F));
        animator.rotate(left_arm, (float)Math.toRadians(15F), 0, (float)Math.toRadians(-20F));
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
        progressRotationPrev(body, sitProgress, (float)Math.toRadians(-80), 0, 0, 10F);
        progressRotationPrev(head, sitProgress, (float)Math.toRadians(80), 0, 0, 10F);
        progressPositionPrev(body, sitProgress, 0, 10, 0, 10F);
        progressRotationPrev(left_leg, sitProgress, 0, (float)Math.toRadians(10), (float)Math.toRadians(-30), 10F);
        progressRotationPrev(right_leg, sitProgress, 0, (float)Math.toRadians(-10), (float)Math.toRadians(30), 10F);
        progressRotationPrev(left_arm, sitProgress, (float)Math.toRadians(25), (float)Math.toRadians(10), 0, 10F);
        progressRotationPrev(right_arm, sitProgress, (float)Math.toRadians(25), (float)Math.toRadians(-10), 0, 10F);
        progressPositionPrev(head, sitProgress, 0, 4, -1, 10F);
        this.head.rotateAngleY += netHeadYaw * ((float)Math.PI / 180F);
        this.head.rotateAngleX += headPitch * ((float)Math.PI / 180F);

        progressRotationPrev(left_leg, standProgress, (float)Math.toRadians(80), 0, 0, 10F);
        progressRotationPrev(right_leg, standProgress, (float)Math.toRadians(80), 0, 0, 10F);
        progressPositionPrev(left_leg, standProgress, 0, -4, 4, 10F);
        progressPositionPrev(right_leg, standProgress, 0, -4, 4, 10F);
        progressPositionPrev(head, standProgress, 0, 0, 2, 10F);
        progressPositionPrev(body, standProgress, 0, -1, -5, 10F);
        progressPositionPrev(head, standProgress, 0, 1, -3, 10F);
        progressRotationPrev(body, standProgress, (float)Math.toRadians(-80), 0, 0, 10F);
        progressRotationPrev(left_arm, standProgress, (float)Math.toRadians(35), (float)Math.toRadians(-10), 0, 10F);
        progressRotationPrev(right_arm, standProgress, (float)Math.toRadians(35), (float)Math.toRadians(10), 0, 10F);
        progressRotationPrev(head, standProgress, (float)Math.toRadians(80), 0, 0, 10F);
        this.walk(left_leg, walkSpeed, walkDegree, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.bob(left_leg, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        this.walk(right_leg, walkSpeed, walkDegree, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.bob(left_leg, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        if(standProgress == 0 && sitProgress == 0){
            this.walk(right_arm, walkSpeed, walkDegree, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(left_arm, walkSpeed, walkDegree, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.flap(midbody, walkSpeed, walkDegree * 0.2F, false, 1F, 0, limbSwing, limbSwingAmount);
            this.flap(body, walkSpeed, walkDegree * 0.2F, false, 2F, 0, limbSwing, limbSwingAmount);
        }else{
            this.walk(right_arm, walkSpeed, walkDegree * 0.1F, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(left_arm, walkSpeed, walkDegree * 0.1F, true, 0F, 0F, limbSwing, limbSwingAmount);
            if(entityIn.isEating()){
                this.walk(right_arm, eatSpeed, eatDegree, true, 1F, 0.6F, ageInTicks, 1);
                this.walk(left_arm, eatSpeed, eatDegree, true, 1F, 0.6F, ageInTicks, 1);
                this.walk(body, eatSpeed, eatDegree * 0.1F, true, 2F, 0.1F, ageInTicks, 1);
                this.walk(head, eatSpeed, eatDegree * 0.3F, false, 1F, 0.3F, ageInTicks, 1);
            }
        }
        this.flap(head, walkSpeed, walkDegree * -0.1F, false, 2F, 0, limbSwing, limbSwingAmount);
        this.bob(body, walkSpeed, walkDegree, true, limbSwing, limbSwingAmount);
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root,
                body,
                right_arm,
                left_arm,
                head,
                left_ear,
                right_ear,
                snout,
                midbody,
                left_leg,
                right_leg);
    }

    public void setRotationAngle(AdvancedModelBox box, float x, float y, float z) {
        box.rotateAngleX = x;
        box.rotateAngleY = y;
        box.rotateAngleZ = z;
    }
}