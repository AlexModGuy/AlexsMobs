package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityGust;
import com.github.alexthe666.alexsmobs.entity.EntityGuster;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;

public class ModelGuster extends AdvancedEntityModel<EntityGuster> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox tornado;
    private final AdvancedModelBox tornado2;
    private final AdvancedModelBox tornadomid;
    private final AdvancedModelBox tornado3;
    private final AdvancedModelBox tornado4;
    private final AdvancedModelBox eyes;
    private final AdvancedModelBox eye_left;
    private final AdvancedModelBox eye_right;

    public ModelGuster() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this);
        root.setPos(0.0F, 24.0F, 0.0F);


        tornado = new AdvancedModelBox(this);
        tornado.setPos(0.0F, -4.0F, 0.0F);
        root.addChild(tornado);
        tornado.texOffs(65, 72).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

        tornado2 = new AdvancedModelBox(this);
        tornado2.setPos(0.0F, -8.0F, 0.0F);
        tornado.addChild(tornado2);
        tornado2.texOffs(0, 72).addBox(-8.0F, -4.0F, -8.0F, 16.0F, 8.0F, 16.0F, 0.0F, false);

        tornadomid = new AdvancedModelBox(this);
        tornadomid.setPos(0.0F, 6.0F, 0.0F);
        tornado2.addChild(tornadomid);
        tornadomid.texOffs(16, 96).addBox(-14.0F, -4.0F, -14.0F, 28.0F, 4.0F, 28.0F, 0.0F, false);

        tornado3 = new AdvancedModelBox(this);
        tornado3.setPos(0.0F, -8.0F, 0.0F);
        tornado2.addChild(tornado3);
        tornado3.texOffs(0, 39).addBox(-12.0F, -4.0F, -12.0F, 24.0F, 8.0F, 24.0F, 0.0F, false);

        tornado4 = new AdvancedModelBox(this);
        tornado4.setPos(0.0F, -8.0F, 0.0F);
        tornado3.addChild(tornado4);
        tornado4.texOffs(0, 0).addBox(-15.0F, -4.0F, -15.0F, 30.0F, 8.0F, 30.0F, 0.0F, false);

        eyes = new AdvancedModelBox(this);
        eyes.setPos(0.0F, -18.0F, -15.0F);
        root.addChild(eyes);


        eye_left = new AdvancedModelBox(this);
        eye_left.setPos(4.0F, 0.0F, 0.0F);
        eyes.addChild(eye_left);
        eye_left.texOffs(8, 13).addBox(-3.0F, -4.0F, 0.0F, 6.0F, 8.0F, 0.0F, 0.0F, false);

        eye_right = new AdvancedModelBox(this);
        eye_right.setPos(-4.0F, 0.0F, 0.0F);
        eyes.addChild(eye_right);
        eye_right.texOffs(8, 13).addBox(-3.0F, -4.0F, 0.0F, 6.0F, 8.0F, 0.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, eyes, eye_left, eye_right, tornado, tornado2, tornado3, tornado4, tornadomid);
    }

    @Override
    public void setupAnim(EntityGuster entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        this.resetToDefaultPose();
        AdvancedModelBox[] tornadoBoxes = new AdvancedModelBox[]{tornado, tornado2, tornado3, tornado4};
        float idleSpeed = 0.1F;
        float idleDegree = 1F;
        float walkSpeed = 0.5F;
        float walkDegree = 1F;
        this.bob(root, walkSpeed, walkDegree * 3, false, limbSwing, limbSwingAmount);
        this.chainFlap(tornadoBoxes, walkSpeed, walkDegree * 0.1F, -2, limbSwing, limbSwingAmount);
        this.bob(root, idleSpeed, idleDegree * 3, false, ageInTicks, 1);
        if(entity.isGooglyEyes()){
            this.eye_left.y += (float)(Math.sin( (double)(ageInTicks * 0.7) - 2) * (double)1.9);
            this.eye_right.y += (float)(Math.sin( (double)(ageInTicks * 0.7) + 2) * (double)1.9);
        }else{
            this.eye_left.y += (float)(Math.sin( (double)(ageInTicks * 0.1) - 2) * (double)0.9);
            this.eye_right.y += (float)(Math.sin( (double)(ageInTicks * 0.1) + 2) * (double)0.9);
        }
        this.bob(eyes, idleSpeed, idleDegree * -3.2F, false, ageInTicks, 1);
        this.eyes.yRot += netHeadYaw * 0.5F * ((float)Math.PI / 180F);
        this.eyes.xRot += headPitch * 0.8F * ((float)Math.PI / 180F);

        tornado.x += Math.cos(ageInTicks * 0.7F) * 4F;
        tornado.z += Math.sin(ageInTicks * 0.7F) * 4F;
        tornado.x += Math.cos(ageInTicks * 0.3F) * 2F - tornado.x;
        tornado.z += Math.sin(ageInTicks * 0.3F) * 2F - tornado.z;
        tornadomid.zRot += Math.sin(ageInTicks * 0.2F) * 0.1;
        tornado.yRot -= ageInTicks * 1F;
        tornado2.yRot -= tornado.yRot + ageInTicks * 0.3F;
        tornado3.yRot -= tornado.yRot + tornado2.yRot + ageInTicks * 0.2F;
        tornado4.yRot -= tornado.yRot + tornado2.yRot + tornado3.yRot + ageInTicks * 0.34F;
        tornadomid.yRot += ageInTicks * 0.5F;
        eyes.z -= 2 + Math.cos(tornado3.yRot);
    }

    public void animateGust(EntityGust entity, float limbSwing, float limbSwingAmount, float ageInTicks){
        this.resetToDefaultPose();
        AdvancedModelBox[] tornadoBoxes = new AdvancedModelBox[]{tornado, tornado2, tornado3, tornado4};
        float idleSpeed = 0.1F;
        float idleDegree = 1F;
        float walkSpeed = 0.5F;
        float walkDegree = 1F;
        this.bob(root, walkSpeed, walkDegree * 3, false, limbSwing, limbSwingAmount);
        this.chainFlap(tornadoBoxes, walkSpeed, walkDegree * 0.1F, -2, limbSwing, limbSwingAmount);
        this.bob(root, idleSpeed, idleDegree * 3, false, ageInTicks, 1);
        this.bob(eyes, idleSpeed, idleDegree * -3.2F, false, ageInTicks, 1);
        tornado.x += Math.cos(ageInTicks * 0.7F) * 4F;
        tornado.z += Math.sin(ageInTicks * 0.7F) * 4F;
        tornado.x += Math.cos(ageInTicks * 0.3F) * 2F - tornado.x;
        tornado.z += Math.sin(ageInTicks * 0.3F) * 2F - tornado.z;
        tornadomid.zRot += Math.sin(ageInTicks * 0.2F) * 0.1;
        tornado.yRot -= ageInTicks * 1F;
        tornado2.yRot -= tornado.yRot + ageInTicks * 0.3F;
        tornado3.yRot -= tornado.yRot + tornado2.yRot + ageInTicks * 0.2F;
        tornado4.yRot -= tornado.yRot + tornado2.yRot + tornado3.yRot + ageInTicks * 0.34F;
        tornadomid.yRot += ageInTicks * 0.5F;
        eyes.z -= 2 + Math.cos(tornado3.yRot);
    }

    public void hideEyes(){
        this.eyes.visible = false;
        this.eye_left.visible = false;
        this.eye_right.visible = false;
    }

    public void showEyes(){
        this.eyes.visible = true;
        this.eye_left.visible = true;
        this.eye_right.visible = true;
    }
    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        root.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(root);
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.xRot = x;
        AdvancedModelBox.yRot = y;
        AdvancedModelBox.zRot = z;
    }
}