package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateAnchor;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class ModelEndPirateAnchor extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox anchor;
    private final AdvancedModelBox chain_start;
    private final AdvancedModelBox left_side;
    private final AdvancedModelBox right_side;

    public ModelEndPirateAnchor() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        anchor = new AdvancedModelBox(this, "anchor");
        anchor.setRotationPoint(0.0F, -47.25F, 0.0F);
        root.addChild(anchor);
        anchor.setTextureOffset(0, 0).addBox(-3.5F, 11.25F, -3.5F, 7.0F, 36.0F, 7.0F, 0.0F, false);
        anchor.setTextureOffset(29, 13).addBox(-5.5F, 2.25F, -4.5F, 11.0F, 9.0F, 9.0F, 0.0F, false);

        chain_start = new AdvancedModelBox(this, "chain_start");
        chain_start.setRotationPoint(1.5F, 2.25F, 0.5F);
        anchor.addChild(chain_start);
        chain_start.setTextureOffset(23, 46).addBox(-3.0F, -5.0F, -4.0F, 3.0F, 5.0F, 7.0F, 0.0F, false);

        left_side = new AdvancedModelBox(this, "left_side");
        left_side.setRotationPoint(16.5F, 29.9167F, 0.0F);
        anchor.addChild(left_side);
        left_side.setTextureOffset(29, 32).addBox(-3.0F, -13.6667F, -3.5F, 10.0F, 6.0F, 7.0F, 0.0F, false);
        left_side.setTextureOffset(0, 44).addBox(-1.0F, -7.6667F, -2.5F, 6.0F, 15.0F, 5.0F, 0.0F, false);
        left_side.setTextureOffset(29, 0).addBox(-13.0F, 7.3333F, -2.5F, 18.0F, 7.0F, 5.0F, 0.0F, false);

        right_side = new AdvancedModelBox(this, "right_side");
        right_side.setRotationPoint(-16.5F, 29.9167F, 0.0F);
        anchor.addChild(right_side);
        right_side.setTextureOffset(29, 32).addBox(-7.0F, -13.6667F, -3.5F, 10.0F, 6.0F, 7.0F, 0.0F, true);
        right_side.setTextureOffset(0, 44).addBox(-5.0F, -7.6667F, -2.5F, 6.0F, 15.0F, 5.0F, 0.0F, true);
        right_side.setTextureOffset(29, 0).addBox(-5.0F, 7.3333F, -2.5F, 18.0F, 7.0F, 5.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, anchor, chain_start, left_side, right_side);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    public void renderAnchor(TileEntityEndPirateAnchor anchor, float partialTick, boolean east) {
        this.resetToDefaultPose();
    }

    public void animateStack(ItemStack itemStackIn) {
        this.resetToDefaultPose();
    }
}