package com.github.alexthe666.alexsmobs.entity.util;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSimplexNoise;
import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.Locale;

public class RainbowUtil {

    private static final String RAINBOW_TYPE = "RainbowTypeAlexsMobs";
    public static NormalNoise noise = NormalNoise.create(new SingleThreadedRandomSource(100L), 4, 0, 1.0D);
    public static void setRainbowType(LivingEntity fabulous, int type) {
        CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(fabulous);
        tag.putInt(RAINBOW_TYPE, type);
        CitadelEntityData.setCitadelTag(fabulous, tag);
        if (!fabulous.level.isClientSide) {
            Citadel.sendMSGToAll(new PropertiesMessage("CitadelPatreonConfig", tag, fabulous.getId()));
        }else{
            Citadel.sendMSGToServer(new PropertiesMessage("CitadelPatreonConfig", tag, fabulous.getId()));
        }
    }

    public static int getRainbowType(LivingEntity entity) {
        CompoundTag lassoedTag = CitadelEntityData.getOrCreateCitadelTag(entity);
        if (lassoedTag.contains(RAINBOW_TYPE)) {
            return lassoedTag.getInt(RAINBOW_TYPE);
        }
        return 0;
    }

    public static int getRainbowTypeFromStack(ItemStack stack){
        String name = stack.getDisplayName().getString().toLowerCase(Locale.ROOT);
        if(name.contains("trans")){
            return 2;
        }
        if(name.contains("nonbi") || name.contains("non-bi")){
            return 3;
        }
        return 1;
    }

    public static int calculateGlassColor(BlockPos pos) {
        float f = (float)AMConfig.rainbowGlassFidelity;
        float f1 = (float)((AMSimplexNoise.noise((pos.getX() + f) / f, (pos.getY() + f) / f, (pos.getZ() + f) / f) + 1.0F) * 0.5F);
        return Color.HSBtoRGB(f1, 1.0F, 1.0F);
    }
}
