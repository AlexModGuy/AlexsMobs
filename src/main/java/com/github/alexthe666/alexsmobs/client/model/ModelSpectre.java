package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntitySpectre;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public class ModelSpectre extends AdvancedEntityModel<EntitySpectre> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox spine;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox wing_left;
    private final AdvancedModelBox wing_right;
    private final AdvancedModelBox wing_left_p;
    private final AdvancedModelBox wing_right_p;

    public ModelSpectre() {
        texWidth = 256;
        texHeight = 256;

        root = new AdvancedModelBox(this, "root");
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setPos(0.0F, -0.5F, 0.0F);
        root.addChild(body);
        setRotationAngle(body, 0.0F, -0.7854F, 0.0F);
        body.setTextureOffset(43, 0).addBox(-12.0F, -5.5F, -12.0F, 24.0F, 6.0F, 24.0F, 0.0F, false);

        spine = new AdvancedModelBox(this, "spine");
        spine.setPos(0.0F, -5.5F, 0.0F);
        body.addChild(spine);
        setRotationAngle(spine, 0.0F, 0.7854F, 0.0F);
        spine.setTextureOffset(0, 0).addBox(0.0F, -3.0F, -14.0F, 0.0F, 8.0F, 42.0F, 0.0F, false);

        tail = new AdvancedModelBox(this, "tail");
        tail.setPos(0.0F, 1.0F, 28.0F);
        spine.addChild(tail);
        tail.setTextureOffset(76, 31).addBox(0.0F, -6.0F, 0.0F, 0.0F, 11.0F, 27.0F, 0.0F, false);

        wing_left_p = new AdvancedModelBox(this, "wing_left_p");
        wing_left_p.setPos(12.0F, -2.5F, -12.0F);
        body.addChild(wing_left_p);

        wing_left = new AdvancedModelBox(this, "wing_left");
        wing_left_p.addChild(wing_left);
        wing_left.setTextureOffset(76, 76).addBox(0.0F, -1.5F, 0.0F, 26.0F, 3.0F, 23.0F, 0.0F, false);

        wing_right_p = new AdvancedModelBox(this, "wing_right_p");
        wing_right_p.setPos(-12.0F, -2.5F, 12.0F);
        body.addChild(wing_right_p);
        setRotationAngle(wing_right_p, 0.0F, 1.5708F, 0.0F);

        wing_right = new AdvancedModelBox(this, "wing_right");
        wing_right_p.addChild(wing_right);
        wing_right.setTextureOffset(0, 51).addBox(-26.0F, -1.5F, 0.0F, 26.0F, 3.0F, 23.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, wing_right_p, wing_left_p, wing_left, wing_right, spine, tail);
    }

    @Override
    public void setupAnim(EntitySpectre entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
       this.resetToDefaultPose();
        float flySpeed = 0.2F;
        float flyDegree = 0.6F;
        this.swing(spine, flySpeed, flyDegree * 0.05F, false, 0F, 0F, ageInTicks, 1);
        this.swing(tail, flySpeed, flyDegree * 0.7F, true, 3F, 0F, ageInTicks, 1);
        this.flap(wing_left, flySpeed, flyDegree * 0.85F, true, 7F, 0, ageInTicks, 1);
        this.flap(wing_right, flySpeed, flyDegree * 0.85F, false, 7F, 0, ageInTicks, 1);
        this.walk(root, flySpeed, flyDegree * 0.15F, true, 7.3F, 0, ageInTicks, 1);
        float partialTick = Minecraft.getInstance().getFrameTime();
        float birdPitch = entity.prevBirdPitch + (entity.birdPitch - entity.prevBirdPitch) * partialTick;
        this.root.rotateAngleX += birdPitch * Mth.DEG_TO_RAD;

    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}