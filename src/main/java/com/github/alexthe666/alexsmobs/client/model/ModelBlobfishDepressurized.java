package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityBlobfish;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public class ModelBlobfishDepressurized extends AdvancedEntityModel<EntityBlobfish> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox nose;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox tail_fin;
    private final AdvancedModelBox fin_left;
    private final AdvancedModelBox fin_right;

    public ModelBlobfishDepressurized() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this, "root");
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setPos(0.0F, -2.5F, 1.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-4.0F, -3.5F, -9.0F, 8.0F, 6.0F, 9.0F, 0.0F, false);

        nose = new AdvancedModelBox(this, "nose");
        nose.setPos(0.0F, -0.5F, -9.0F);
        body.addChild(nose);
        nose.setTextureOffset(0, 0).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 2.0F, 1.0F, 0.0F, false);

        tail = new AdvancedModelBox(this, "tail");
        tail.setPos(0.0F, 0.75F, 0.0F);
        body.addChild(tail);
        tail.setTextureOffset(0, 16).addBox(-3.0F, -3.25F, 0.0F, 6.0F, 5.0F, 4.0F, 0.0F, false);

        tail_fin = new AdvancedModelBox(this, "tail_fin");
        tail_fin.setPos(0.0F, -0.25F, 4.0F);
        tail.addChild(tail_fin);
        tail_fin.setTextureOffset(16, 21).addBox(0.0F, -3.0F, 0.0F, 0.0F, 5.0F, 5.0F, 0.0F, false);

        fin_left = new AdvancedModelBox(this, "fin_left");
        fin_left.setPos(4.0F, 2.0F, -4.0F);
        body.addChild(fin_left);
        setRotationAngle(fin_left, -1.5708F, 0.0F, 0.0F);
        fin_left.setTextureOffset(0, 4).addBox(0.0F, -1.5F, 0.0F, 3.0F, 3.0F, 0.0F, 0.0F, false);

        fin_right = new AdvancedModelBox(this, "fin_right");
        fin_right.setPos(-4.0F, 2.0F, -4.0F);
        body.addChild(fin_right);
        setRotationAngle(fin_right, -1.5708F, 0.0F, 0.0F);
        fin_right.setTextureOffset(0, 4).addBox(-3.0F, -1.5F, 0.0F, 3.0F, 3.0F, 0.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, fin_left, fin_right, tail, tail_fin, nose);
    }

    @Override
    public void setupAnim(EntityBlobfish entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        this.resetToDefaultPose();
        if(!entity.onGround()){
            this.body.rotateAngleX = headPitch * ((float)Math.PI / 180F);
        }
        float swimSpeed = 1.5F;
        float swimDegree = 0.85F;
        this.swing(tail, swimSpeed, swimDegree * 0.5F, false, 0, 0, limbSwing, limbSwingAmount);
        this.swing(tail_fin, swimSpeed, swimDegree * 0.5F, false, 0, 0, limbSwing, limbSwingAmount);
        this.flap(fin_left, swimSpeed, swimDegree, false, 3F, -0.3F, limbSwing, limbSwingAmount);
        this.flap(fin_right, swimSpeed, swimDegree, true, 3F, -0.3F, limbSwing, limbSwingAmount);
        float lvt_6_1_ = Mth.lerp(Minecraft.getInstance().getFrameTime(), entity.prevSquishFactor, entity.squishFactor);
        float lvt_7_1_ = 1.0F / (lvt_6_1_ + 1.0F);
        float squishScale = 1.0F / lvt_7_1_;
        this.body.setScale(1F, squishScale, 1F);
        this.body.rotationPointY += lvt_6_1_ * -5F;
        this.body.setShouldScaleChildren(true);
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}