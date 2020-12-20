package com.github.alexthe666.alexsmobs.client.model;
import com.github.alexthe666.alexsmobs.entity.EntityOrca;
import com.github.alexthe666.alexsmobs.entity.EntityRattlesnake;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelRattlesnake extends AdvancedEntityModel<EntityRattlesnake> {
    private final AdvancedModelBox body;
    private final AdvancedModelBox tail1;
    private final AdvancedModelBox tail2;
    private final AdvancedModelBox neck1;
    private final AdvancedModelBox neck2;
    private final AdvancedModelBox head;
    private final AdvancedModelBox tongue;
    private ModelAnimator animator;

    public ModelRattlesnake() {
        textureWidth = 64;
        textureHeight = 64;

        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, 24.0F, 0.0F);
        body.setTextureOffset(0, 0).addBox(-2.0F, -3.0F, -4.0F, 4.0F, 3.0F, 7.0F, 0.0F, false);

        tail1 = new AdvancedModelBox(this);
        tail1.setRotationPoint(0.0F, -1.75F, 2.95F);
        body.addChild(tail1);
        tail1.setTextureOffset(0, 11).addBox(-1.5F, -1.25F, 0.05F, 3.0F, 3.0F, 7.0F, 0.0F, false);

        tail2 = new AdvancedModelBox(this);
        tail2.setRotationPoint(0.0F, 0.45F, 7.05F);
        tail1.addChild(tail2);
        tail2.setTextureOffset(15, 16).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 6.0F, 0.0F, false);

        neck1 = new AdvancedModelBox(this);
        neck1.setRotationPoint(0.0F, -1.5F, -4.0F);
        body.addChild(neck1);
        neck1.setTextureOffset(18, 6).addBox(-1.5F, -1.5F, -5.0F, 3.0F, 3.0F, 5.0F, 0.0F, false);

        neck2 = new AdvancedModelBox(this);
        neck2.setRotationPoint(0.0F, 0.0F, -4.9F);
        neck1.addChild(neck2);
        neck2.setTextureOffset(12, 25).addBox(-1.0F, -1.5F, -5.1F, 2.0F, 3.0F, 5.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, 0.0F, -5.0F);
        neck2.addChild(head);
        head.setTextureOffset(0, 22).addBox(-2.0F, -1.0F, -3.8F, 4.0F, 2.0F, 4.0F, 0.0F, false);

        tongue = new AdvancedModelBox(this);
        tongue.setRotationPoint(0.0F, 0.0F, -3.8F);
        head.addChild(tongue);
        tongue.setTextureOffset(0, 0).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 0.0F, 2.0F, 0.0F, false);
        this.updateDefaultPose();
        this.animator = ModelAnimator.create();
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        this.resetToDefaultPose();
        animator.update(entity);
        animator.setAnimation(EntityRattlesnake.ANIMATION_BITE);
        animator.startKeyframe(7);
        animator.move(body, 0, 0, 2);
        animator.rotate(neck1, (float)Math.toRadians(-45), 0, 0);
        animator.rotate(neck2, (float)Math.toRadians(15), 0, 0);
        animator.rotate(tail1, 0, (float)Math.toRadians(15), 0);
        animator.rotate(tail2, 0, (float)Math.toRadians(-15), 0);
        animator.rotate(head, (float)Math.toRadians(20), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(body, 0, 0, -2);

        animator.endKeyframe();
        animator.resetKeyframe(3);

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
    public void setRotationAngles(EntityRattlesnake entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float walkSpeed = 1.0F;
        float walkDegree = 0.4F;
        float partialTick = Minecraft.getInstance().getRenderPartialTicks();
        AdvancedModelBox[] bodyParts = new AdvancedModelBox[]{neck1, neck2, body, tail1, tail2};
        float curlProgress = entity.prevCurlProgress + (entity.curlProgress - entity.prevCurlProgress) * partialTick;
        progressPositionPrev(body, curlProgress, 0, 0, 3, 5F);
        progressRotationPrev(body, curlProgress, 0, (float) Math.toRadians(-90), 0, 5F);
        progressRotationPrev(tail1, curlProgress, (float) Math.toRadians(-10), (float) Math.toRadians(-70), 0, 5F);
        progressRotationPrev(neck1, curlProgress, (float) Math.toRadians(-20), (float) Math.toRadians(60), 0, 5F);
        progressRotationPrev(neck2, curlProgress, (float) Math.toRadians(-20), (float) Math.toRadians(60), 0, 5F);
        progressRotationPrev(head, curlProgress, (float) Math.toRadians(20), (float) Math.toRadians(-30), (float) Math.toRadians(10), 5F);
        if (entity.randomToungeTick > 0) {
            tongue.showModel = true;
        }else{
			tongue.showModel = false;
		}
        this.walk(tongue, 1, 0.5F, false, 1F, 0f, ageInTicks, 1);
        if (entity.isRattling()) {
            progressRotationPrev(tail2, curlProgress, (float) Math.toRadians(70), (float) Math.toRadians(-60), 0, 5F);
            this.walk(tail2, 18, 0.1F, false, 1F, 0.2F, ageInTicks, 1);
            this.swing(tail2, 18, 0.1F, false, 0f, 0f, ageInTicks, 1);
        } else {
            progressRotationPrev(tail2, curlProgress, (float) Math.toRadians(10), (float) Math.toRadians(-90), 0, 5F);
        }
        this.faceTarget(netHeadYaw, headPitch, 2, neck2, head);
        this.chainSwing(bodyParts, walkSpeed, walkDegree, -5, limbSwing, limbSwingAmount);
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(body);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, tail1, tail2, neck1, neck2, head, tongue);
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}