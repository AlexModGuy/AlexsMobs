package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class ModelMysteriousWorm extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox head;
    private final AdvancedModelBox body1;
    private final AdvancedModelBox body2;
    private final AdvancedModelBox body3;

    public ModelMysteriousWorm() {
        textureWidth = 32;
        textureHeight = 32;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -2.0F, -6.0F);
        root.addChild(head);
        head.setTextureOffset(14, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 2.0F, 0.0F, false);
        head.setTextureOffset(0, 19).addBox(-1.0F, -1.0F, -4.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);

        body1 = new AdvancedModelBox(this);
        body1.setRotationPoint(0.0F, 0.0F, 0.0F);
        head.addChild(body1);
        body1.setTextureOffset(0, 11).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 5.0F, 0.0F, false);

        body2 = new AdvancedModelBox(this);
        body2.setRotationPoint(0.0F, 0.0F, 5.0F);
        body1.addChild(body2);
        body2.setTextureOffset(10, 14).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 5.0F, 0.1F, false);

        body3 = new AdvancedModelBox(this);
        body3.setRotationPoint(0.0F, 0.0F, 5.0F);
        body2.addChild(body3);
        body3.setTextureOffset(0, 0).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 7.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, head, body1, body2, body3);
    }

    @Override
    public void setRotationAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        this.resetToDefaultPose();
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        root.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }

    public void animateStack(ItemStack itemStackIn) {
        this.resetToDefaultPose();
        float partialTick = Minecraft.getInstance().getRenderPartialTicks();
        float tick = Minecraft.getInstance().player == null ? 0 : partialTick + Minecraft.getInstance().player.ticksExisted;
        AdvancedModelBox[] tail = new AdvancedModelBox[]{head, body1, body2, body3};
        this.chainSwing(tail, 0.7F, 0.2F, -3, tick, 1);
        this.chainFlap(tail, 0.7F, 0.2F, -3, tick, 1);
        this.chainWave(tail, 0.7F, 0.2F, -3, tick, MathHelper.clamp((float)(1.0F + Math.sin(tick * 0.04)), 0, 0.5F) * 2);

    }
}