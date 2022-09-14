package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityMurmurHead;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public class ModelMurmurHead extends AdvancedEntityModel<EntityMurmurHead> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox head;
    private final AdvancedModelBox backHair;
    private final AdvancedModelBox leftHair;
    private final AdvancedModelBox rightHair;

    public ModelMurmurHead() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -1.0F, 0.0F);
        root.addChild(head);
        head.setTextureOffset(37, 41).addBox(-4.5F, -8.0F, -4.5F, 9.0F, 9.0F, 9.0F, 0.0F, false);
        head.setTextureOffset(0, 41).addBox(-4.5F, -8.0F, -4.5F, 9.0F, 9.0F, 9.0F, 0.2F, false);

        backHair = new AdvancedModelBox(this);
        backHair.setRotationPoint(0.0F, -5.0F, 5.0F);
        head.addChild(backHair);
        backHair.setTextureOffset(49, 0).addBox(-5.5F, -2.0F, -1.5F, 11.0F, 20.0F, 3.0F, 0.0F, false);

        leftHair = new AdvancedModelBox(this);
        leftHair.setRotationPoint(4.5F, -5.0F, 1.0F);
        head.addChild(leftHair);
        leftHair.setTextureOffset(17, 60).addBox(-1.0F, -2.0F, -2.5F, 2.0F, 16.0F, 5.0F, 0.0F, false);

        rightHair = new AdvancedModelBox(this);
        rightHair.setRotationPoint(-4.5F, -5.0F, 1.0F);
        head.addChild(rightHair);
        rightHair.setTextureOffset(17, 60).addBox(-1.0F, -2.0F, -2.5F, 2.0F, 16.0F, 5.0F, 0.0F, true);

     this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, head, leftHair, rightHair, backHair);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    public void animateHair(float ageInTicks){
        float idleSpeed = 0.05F;
        float idleDegree = 0.1F;
        this.walk(backHair, idleSpeed, idleDegree * 0.5F, false, 0F, -0.05F, ageInTicks, 1);
        this.flap(rightHair, idleSpeed, idleDegree * 0.5F, false, 1F, 0.05F, ageInTicks, 1);
        this.flap(leftHair, idleSpeed, idleDegree * 0.5F, true, 1F, 0.05F, ageInTicks, 1);
    }

    @Override
    public void setupAnim(EntityMurmurHead entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTicks = ageInTicks - entity.tickCount;
        float angerProgress = entity.prevAngerProgress + (entity.angerProgress - entity.prevAngerProgress) * partialTicks;
        //hair physics
        if(ageInTicks > 5F){
            float hairAnimateScale = Math.min(1F, (ageInTicks - 5F) / 10F);
            double d0 = Mth.lerp((double)partialTicks, entity.prevXHair, entity.xHair) - Mth.lerp((double)partialTicks, entity.xo, entity.getX());
            double d1 = Mth.lerp((double)partialTicks, entity.prevYHair, entity.yHair) - Mth.lerp((double)partialTicks, entity.yo, entity.getY());
            double d2 = Mth.lerp((double)partialTicks, entity.prevZHair, entity.zHair) - Mth.lerp((double)partialTicks, entity.zo, entity.getZ());
            float f = entity.yBodyRotO + (entity.yBodyRot - entity.yBodyRotO);
            double d3 = (double)Mth.sin(f * ((float)Math.PI / 180F));
            double d4 = (double)(-Mth.cos(f * ((float)Math.PI / 180F)));
            float f1 = (float)d1 * 10.0F;
            f1 = Mth.clamp(f1, -6.0F, 32.0F) * hairAnimateScale;
            float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
            f2 = Mth.clamp(f2, 0.0F, 150.0F) * hairAnimateScale;
            float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
            f3 = Mth.clamp(f3, -20.0F, 20.0F) * hairAnimateScale;
            if (f2 < 0.0F) {
                f2 = 0.0F;
            }
            f1 += Mth.sin(Mth.lerp(partialTicks, entity.walkDistO, entity.walkDist) * 6.0F) * 32.0F * 1F;
            float hairX = (float)Math.toRadians(6.0F + f2 / 2.0F + f1 - 180);
            float hairY = (float) Math.toRadians(f3 / 2.0F);
            float hairZ = (float) Math.toRadians(180.0F - f3 / 2.0F);
            this.backHair.rotateAngleX -= hairX;
            this.backHair.rotateAngleY -= hairY;
            this.backHair.rotateAngleZ -= hairZ;
            this.rightHair.rotateAngleX -= hairX;
            this.rightHair.rotateAngleY -= hairY;
            this.rightHair.rotateAngleZ -= hairZ;
            this.leftHair.rotateAngleX -= hairX;
            this.leftHair.rotateAngleY -= hairY;
            this.leftHair.rotateAngleZ -= hairZ;
        }
        this.animateHair(ageInTicks);
        this.faceTarget(netHeadYaw, headPitch, 1, head);
        progressRotationPrev(this.backHair, angerProgress, (float)Math.toRadians(-20F), 0, 0, 5F);
        progressRotationPrev(this.rightHair, angerProgress, (float)Math.toRadians(-10F), 0, (float)Math.toRadians(25F), 5F);
        progressRotationPrev(this.leftHair, angerProgress, (float)Math.toRadians(-10F), 0, (float)Math.toRadians(-25F), 5F);

    }
}
