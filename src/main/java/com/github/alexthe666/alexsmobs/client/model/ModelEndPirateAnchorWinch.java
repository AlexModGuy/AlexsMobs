package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateAnchor;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateAnchorWinch;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class ModelEndPirateAnchorWinch extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox chains;

    public ModelEndPirateAnchorWinch() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);
        chains = new AdvancedModelBox(this);
        chains.setRotationPoint(0.0F, -8.0F, 0.0F);
        root.addChild(chains);
        chains.setTextureOffset(0, 33).addBox(-8.0F, -5.0F, -5.0F, 16.0F, 10.0F, 10.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, chains);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    public void renderAnchor(TileEntityEndPirateAnchorWinch anchor, float partialTick, boolean east) {
        this.resetToDefaultPose();
        float timeWinching = anchor.windCounter + partialTick;
        float f = anchor.getWindProgress(partialTick);
        float f1 = anchor.isWindingUp() ? 1 : -1;
        if(anchor.isWinching()){
            chains.rotateAngleX = timeWinching * 0.2F * f * f1;
            anchor.clientRoll = chains.rotateAngleX;
        }else{
            float rollDeg = (float) Mth.wrapDegrees(Math.toDegrees(anchor.clientRoll));
            chains.rotateAngleX = f1 * f * 0.2F * (float)Math.toRadians(rollDeg);
        }
    }

    public void animateStack(ItemStack itemStackIn) {
        this.resetToDefaultPose();
    }
}
