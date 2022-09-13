package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityMungus;
import com.github.alexthe666.alexsmobs.entity.EntityMurmur;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.LivingEntity;

public class ModelMurmurBody extends AdvancedEntityModel<EntityMurmur> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox arms;
    
    public ModelMurmurBody() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, -14.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-7.0F, -13.0F, -5.0F, 14.0F, 14.0F, 10.0F, 0.0F, false);
        body.setTextureOffset(72, 20).addBox(-7.0F, 1.0F, -5.0F, 14.0F, 13.0F, 10.0F, 0.0F, false);

        arms = new AdvancedModelBox(this);
        arms.setRotationPoint(0.0F, -8.5F, -1.0F);
        body.addChild(arms);
        arms.rotateAngleX = 0.4363F;
        arms.setTextureOffset(0, 25).addBox(-9.0F, -2.5F, -8.0F, 18.0F, 5.0F, 10.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, arms);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public void setupAnim(EntityMurmur entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
    }
}
