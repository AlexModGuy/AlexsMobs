package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelLeafcutterAnt;
import com.github.alexthe666.alexsmobs.client.model.ModelLeafcutterAntQueen;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerLeafcutterAntLeaf;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TextFormatting;

public class RenderLeafcutterAnt extends MobRenderer<EntityLeafcutterAnt, EntityModel<EntityLeafcutterAnt>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/leafcutter_ant.png");
    private static final ResourceLocation TEXTURE_QUEEN = new ResourceLocation("alexsmobs:textures/entity/leafcutter_ant_queen.png");
    private static final ResourceLocation TEXTURE_ANGRY = new ResourceLocation("alexsmobs:textures/entity/leafcutter_ant_angry.png");
    private static final ResourceLocation TEXTURE_QUEEN_ANGRY = new ResourceLocation("alexsmobs:textures/entity/leafcutter_ant_queen_angry.png");
    private final ModelLeafcutterAnt model = new ModelLeafcutterAnt();
    private final ModelLeafcutterAntQueen modelQueen = new ModelLeafcutterAntQueen();

    public RenderLeafcutterAnt(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelLeafcutterAnt(), 0.25F);
        this.addLayer(new LayerLeafcutterAntLeaf(this));
    }


    @Override
    protected void applyRotations(EntityLeafcutterAnt entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        if (this.func_230495_a_(entityLiving)) {
            rotationYaw += (float)(Math.cos((double)entityLiving.ticksExisted * 3.25D) * Math.PI * (double)0.4F);
        }
        float trans = entityLiving.isChild() ? 0.25F : 0.5F;
        Pose pose = entityLiving.getPose();
        if (pose != Pose.SLEEPING) {
            float progresso = 1F - (entityLiving.prevAttachChangeProgress + (entityLiving.attachChangeProgress - entityLiving.prevAttachChangeProgress) * partialTicks);

            if(entityLiving.getAttachmentFacing() == Direction.DOWN){
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees (180.0F - rotationYaw));
                matrixStackIn.translate(0.0D, trans, 0.0D);
                if(entityLiving.prevPosY < entityLiving.getPosY()){
                    matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90 * (1 - progresso)));
                }else{
                    matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90 * (1 - progresso)));
                }
                matrixStackIn.translate(0.0D, -trans, 0.0D);

            }else if(entityLiving.getAttachmentFacing() == Direction.UP){
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees (180.0F - rotationYaw));
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180));
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180));
                matrixStackIn.translate(0.0D, -trans, 0.0D);

            }else{
                matrixStackIn.translate(0.0D, trans, 0.0D);
                switch (entityLiving.getAttachmentFacing()){
                    case NORTH:
                        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F * progresso));
                        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(0));
                        break;
                    case SOUTH:
                        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
                        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F * progresso ));
                        break;
                    case WEST:
                        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F));
                        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90F - 90.0F * progresso));
                        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(-90.0F));
                        break;
                    case EAST:
                        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F ));
                        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90.0F * progresso - 90F));
                        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90.0F));
                        break;
                }
                if(entityLiving.getMotion().y <= -0.001F){
                    matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-180.0F));
                }
                matrixStackIn.translate(0.0D, -trans, 0.0D);
            }
        }

        if (entityLiving.deathTime > 0) {
            float f = ((float)entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            f = MathHelper.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(f * this.getDeathMaxRotation(entityLiving)));
        } else if (entityLiving.isSpinAttacking()) {
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90.0F - entityLiving.rotationPitch));
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(((float)entityLiving.ticksExisted + partialTicks) * -75.0F));
        } else if (pose == Pose.SLEEPING) {

        } else if (entityLiving.hasCustomName() ) {
            String s = TextFormatting.getTextWithoutFormattingCodes(entityLiving.getName().getString());
            if (("Dinnerbone".equals(s) || "Grumm".equals(s))) {
                matrixStackIn.translate(0.0D, (double)(entityLiving.getHeight() + 0.1F), 0.0D);
                matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0F));
            }
        }
    }

    protected void preRenderCallback(EntityLeafcutterAnt entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        entityModel = entitylivingbaseIn.isQueen() ? modelQueen : model;
        float scale = entitylivingbaseIn.getAntScale();
    }


    public ResourceLocation getEntityTexture(EntityLeafcutterAnt entity) {
        if(entity.getAngerTime() > 0){
            return entity.isQueen() ? TEXTURE_QUEEN_ANGRY : TEXTURE_ANGRY;
        }else {
            return entity.isQueen() ? TEXTURE_QUEEN : TEXTURE;
        }
    }
}
