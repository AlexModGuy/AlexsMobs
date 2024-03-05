package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelLeafcutterAnt;
import com.github.alexthe666.alexsmobs.client.model.ModelLeafcutterAntQueen;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerLeafcutterAntLeaf;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;

public class RenderLeafcutterAnt extends MobRenderer<EntityLeafcutterAnt, AdvancedEntityModel<EntityLeafcutterAnt>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/leafcutter_ant.png");
    private static final ResourceLocation TEXTURE_QUEEN = new ResourceLocation("alexsmobs:textures/entity/leafcutter_ant_queen.png");
    private static final ResourceLocation TEXTURE_ANGRY = new ResourceLocation("alexsmobs:textures/entity/leafcutter_ant_angry.png");
    private static final ResourceLocation TEXTURE_QUEEN_ANGRY = new ResourceLocation("alexsmobs:textures/entity/leafcutter_ant_queen_angry.png");
    private final ModelLeafcutterAnt modelAnt = new ModelLeafcutterAnt();
    private final ModelLeafcutterAntQueen modelQueen = new ModelLeafcutterAntQueen();

    public RenderLeafcutterAnt(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelLeafcutterAnt(), 0.25F);
        this.addLayer(new LayerLeafcutterAntLeaf(this));
    }


    @Override
    protected void setupRotations(EntityLeafcutterAnt entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        if (this.isShaking(entityLiving)) {
            rotationYaw += (float)(Math.cos((double)entityLiving.tickCount * 3.25D) * Math.PI * (double)0.4F);
        }
        float trans = entityLiving.isBaby() ? 0.25F : 0.5F;
        Pose pose = entityLiving.getPose();
        if (pose != Pose.SLEEPING) {
            float progresso = 1F - (entityLiving.prevAttachChangeProgress + (entityLiving.attachChangeProgress - entityLiving.prevAttachChangeProgress) * partialTicks);

            if(entityLiving.getAttachmentFacing() == Direction.DOWN){
                matrixStackIn.mulPose(Axis.YP.rotationDegrees (180.0F - rotationYaw));
                matrixStackIn.translate(0.0D, trans, 0.0D);
                if(entityLiving.yo < entityLiving.getY()){
                    matrixStackIn.mulPose(Axis.XP.rotationDegrees(90 * (1 - progresso)));
                }else{
                    matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90 * (1 - progresso)));
                }
                matrixStackIn.translate(0.0D, -trans, 0.0D);

            }else if(entityLiving.getAttachmentFacing() == Direction.UP){
                matrixStackIn.mulPose(Axis.YP.rotationDegrees (180.0F - rotationYaw));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(180));
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
                matrixStackIn.translate(0.0D, -trans, 0.0D);

            }else{
                matrixStackIn.translate(0.0D, trans, 0.0D);
                switch (entityLiving.getAttachmentFacing()){
                    case NORTH:
                        matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F * progresso));
                        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(0));
                        break;
                    case SOUTH:
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
                        matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F * progresso ));
                        break;
                    case WEST:
                        matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F));
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90F - 90.0F * progresso));
                        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-90.0F));
                        break;
                    case EAST:
                        matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F ));
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F * progresso - 90F));
                        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(90.0F));
                        break;
                }
                if(entityLiving.getDeltaMovement().y <= -0.001F){
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(-180.0F));
                }
                matrixStackIn.translate(0.0D, -trans, 0.0D);
            }
        }

        if (entityLiving.deathTime > 0) {
            float f = ((float)entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(f * this.getFlipDegrees(entityLiving)));
        } else if (entityLiving.isAutoSpinAttack()) {
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90.0F - entityLiving.getXRot()));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(((float)entityLiving.tickCount + partialTicks) * -75.0F));
        } else if (pose == Pose.SLEEPING) {

        } else if (entityLiving.hasCustomName() ) {
            String s = ChatFormatting.stripFormatting(entityLiving.getName().getString());
            if (("Dinnerbone".equals(s) || "Grumm".equals(s))) {
                matrixStackIn.translate(0.0D, (double)(entityLiving.getBbHeight() + 0.1F), 0.0D);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
            }
        }
    }

    protected void scale(EntityLeafcutterAnt entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        model = entitylivingbaseIn.isQueen() ? modelQueen : modelAnt;
    }


    public ResourceLocation getTextureLocation(EntityLeafcutterAnt entity) {
        if(entity.getRemainingPersistentAngerTime() > 0){
            return entity.isQueen() ? TEXTURE_QUEEN_ANGRY : TEXTURE_ANGRY;
        }else {
            return entity.isQueen() ? TEXTURE_QUEEN : TEXTURE;
        }
    }
}
