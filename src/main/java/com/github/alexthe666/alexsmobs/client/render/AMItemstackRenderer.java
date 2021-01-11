package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityBlobfish;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemTabIcon;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AMItemstackRenderer extends ItemStackTileEntityRenderer {

    private static List<Pair<EntityType, Float>> MOB_ICONS = Util.make(Lists.newArrayList(), (list) -> {
        list.add(new Pair<>(AMEntityRegistry.GRIZZLY_BEAR, 0.6F));
        list.add(new Pair<>(AMEntityRegistry.ROADRUNNER, 0.8F));
        list.add(new Pair<>(AMEntityRegistry.BONE_SERPENT, 0.55F));
        list.add(new Pair<>(AMEntityRegistry.GAZELLE, 0.6F));
        list.add(new Pair<>(AMEntityRegistry.CROCODILE, 0.3F));
        list.add(new Pair<>(AMEntityRegistry.FLY, 1.3F));
        list.add(new Pair<>(AMEntityRegistry.HUMMINGBIRD, 1.5F));
        list.add(new Pair<>(AMEntityRegistry.ORCA, 0.2F));
        list.add(new Pair<>(AMEntityRegistry.SUNBIRD, 0.3F));
        list.add(new Pair<>(AMEntityRegistry.GORILLA, 0.85F));
        list.add(new Pair<>(AMEntityRegistry.CRIMSON_MOSQUITO, 0.6F));
        list.add(new Pair<>(AMEntityRegistry.RATTLESNAKE, 0.6F));
        list.add(new Pair<>(AMEntityRegistry.ENDERGRADE, 0.8F));
        list.add(new Pair<>(AMEntityRegistry.HAMMERHEAD_SHARK, 0.5F));
        list.add(new Pair<>(AMEntityRegistry.LOBSTER, 0.85F));
        list.add(new Pair<>(AMEntityRegistry.KOMODO_DRAGON, 0.5F));
        list.add(new Pair<>(AMEntityRegistry.CAPUCHIN_MONKEY, 0.85F));
        list.add(new Pair<>(AMEntityRegistry.CENTIPEDE_HEAD, 0.65F));
        list.add(new Pair<>(AMEntityRegistry.WARPED_TOAD, 0.6F));
        list.add(new Pair<>(AMEntityRegistry.MOOSE, 0.5F));
        list.add(new Pair<>(AMEntityRegistry.MIMICUBE, 0.95F));
        list.add(new Pair<>(AMEntityRegistry.RACCOON, 0.8F));
        list.add(new Pair<>(AMEntityRegistry.BLOBFISH, 1F));
        list.add(new Pair<>(AMEntityRegistry.SEAL, 0.7F));
        list.add(new Pair<>(AMEntityRegistry.COCKROACH, 1F));
        list.add(new Pair<>(AMEntityRegistry.SHOEBILL, 0.8F));
        list.add(new Pair<>(AMEntityRegistry.ELEPHANT, 0.45F));
        list.add(new Pair<>(AMEntityRegistry.SOUL_VULTURE, 0.8F));
    });
    private static int ticksExisted = 0;

    private Map<String, Entity> renderedEntites = new HashMap();

    public static void incrementTick() {
        ticksExisted++;
    }

    private static float getScaleFor(EntityType type) {
        for (Pair<EntityType, Float> pair : MOB_ICONS) {
            if (pair.getFirst() == type) {
                return pair.getSecond();
            }
        }
        return 1.0F;
    }

    public static void drawEntityOnScreen(MatrixStack matrixstack, int posX, int posY, float scale, boolean follow, double xRot, double yRot, double zRot, float mouseX, float mouseY, Entity entity) {
        float f = (float) Math.atan(-mouseX / 40.0F);
        float f1 = (float) Math.atan(mouseY / 40.0F);
        matrixstack.translate(0.0D, 0.0D, 0);
        matrixstack.scale(scale, scale, scale);
        entity.setOnGround(false);
        float partialTicks = Minecraft.getInstance().getRenderPartialTicks();
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(20.0F);
        if (follow) {
            float yaw = f * 45.0F;
            entity.rotationYaw = yaw;
            entity.ticksExisted = Minecraft.getInstance().player.ticksExisted;
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).renderYawOffset = yaw;
                ((LivingEntity) entity).prevRenderYawOffset = yaw;
                ((LivingEntity) entity).rotationYawHead = yaw;
                ((LivingEntity) entity).prevRotationYawHead = yaw;
            }

            quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
            quaternion.multiply(quaternion1);
        }

        matrixstack.rotate(quaternion);
        matrixstack.rotate(Vector3f.XP.rotationDegrees((float) (-xRot)));
        matrixstack.rotate(Vector3f.YP.rotationDegrees((float) yRot));
        matrixstack.rotate(Vector3f.ZP.rotationDegrees((float) zRot));
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        quaternion1.conjugate();
        entityrenderermanager.setCameraOrientation(quaternion1);
        entityrenderermanager.setRenderShadow(false);
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        RenderSystem.runAsFancy(() -> {
            entityrenderermanager.renderEntityStatic(entity, 0.0D, 0.0D, 0.0D, f, partialTicks, matrixstack, irendertypebuffer$impl, 15728880);
        });
        irendertypebuffer$impl.finish();
        entityrenderermanager.setRenderShadow(true);
        entity.rotationYaw = 0.0F;
        entity.rotationPitch = 0.0F;
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).renderYawOffset = 0.0F;
            ((LivingEntity) entity).prevRotationYawHead = 0.0F;
            ((LivingEntity) entity).rotationYawHead = 0.0F;
        }
    }

    @Override
    public void func_239207_a_(ItemStack itemStackIn, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (itemStackIn.getItem() == AMItemRegistry.TAB_ICON) {
            Entity fakeEntity = null;
            int entityIndex = (Minecraft.getInstance().player.ticksExisted / 40) % (MOB_ICONS.size());
            float scale = 1.0F;
            int flags = 0;
            if (ItemTabIcon.hasCustomEntityDisplay(itemStackIn)) {
                flags = itemStackIn.getTag().getInt("DisplayMobFlags");
                String index = ItemTabIcon.getCustomDisplayEntityString(itemStackIn);
                EntityType local = ItemTabIcon.getEntityType(itemStackIn.getTag());
                scale = getScaleFor(local);
                if (this.renderedEntites.get(index) == null) {
                    Entity entity = local.create(Minecraft.getInstance().world);
                    if (entity instanceof EntityBlobfish) {
                        ((EntityBlobfish) entity).setDepressurized(true);
                    }
                    fakeEntity = this.renderedEntites.putIfAbsent(local.getTranslationKey(), entity);
                } else {
                    fakeEntity = this.renderedEntites.get(local.getTranslationKey());
                }
            } else {
                EntityType type = MOB_ICONS.get(entityIndex).getFirst();
                scale = MOB_ICONS.get(entityIndex).getSecond();
                if (type != null) {
                    if (this.renderedEntites.get(type.getTranslationKey()) == null) {
                        Entity entity = type.create(Minecraft.getInstance().world);
                        if (entity instanceof EntityBlobfish) {
                            ((EntityBlobfish) entity).setDepressurized(true);
                        }
                        fakeEntity = this.renderedEntites.putIfAbsent(type.getTranslationKey(), entity);
                    } else {
                        fakeEntity = this.renderedEntites.get(type.getTranslationKey());
                    }
                }
            }
            if (fakeEntity instanceof EntityCockroach) {
                if (flags == 99) {
                    ((EntityCockroach) fakeEntity).setMaracas(true);
                } else {
                    ((EntityCockroach) fakeEntity).setMaracas(false);
                }
            }
            if (fakeEntity != null) {
                MouseHelper mouseHelper = Minecraft.getInstance().mouseHelper;
                double mouseX = (mouseHelper.getMouseX() * (double) Minecraft.getInstance().getMainWindow().getScaledWidth()) / (double) Minecraft.getInstance().getMainWindow().getWidth();
                double mouseY = mouseHelper.getMouseY() * (double) Minecraft.getInstance().getMainWindow().getScaledHeight() / (double) Minecraft.getInstance().getMainWindow().getHeight();
                matrixStackIn.translate(0.5F, 0.2F, 0);
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180F));
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180F));
                if (p_239207_2_ != ItemCameraTransforms.TransformType.GUI) {
                    mouseX = 0;
                    mouseY = 0;
                }
                drawEntityOnScreen(matrixStackIn, 0, 0, scale, true, 0, -45, 0, (float) mouseX, (float) mouseY, fakeEntity);
            }
        }
    }

}
