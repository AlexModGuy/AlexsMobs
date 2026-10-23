package com.github.alexthe666.alexsmobs.entity.util;

import java.awt.Color;
import java.util.Locale;

import com.github.alexthe666.alexsmobs.attachment.AMAttachmentRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.ItemRainbowJelly;
import com.github.alexthe666.alexsmobs.misc.AMSimplexNoise;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class RainbowUtil {

    public static void setRainbowType(LivingEntity fabulous, @Nullable ItemRainbowJelly.RainbowType type) {
        if (type == null)
            fabulous.removeData(AMAttachmentRegistry.RAINBOW_TYPE);
        else
            fabulous.setData(AMAttachmentRegistry.RAINBOW_TYPE, type);
    }

    public static @Nullable ItemRainbowJelly.RainbowType getRainbowType(LivingEntity entity) {
        return entity.getExistingDataOrNull(AMAttachmentRegistry.RAINBOW_TYPE);
    }

    public static ItemRainbowJelly.RainbowType getRainbowTypeFromStack(ItemStack stack){
        String name = stack.getDisplayName().getString().toLowerCase(Locale.ROOT);
        return ItemRainbowJelly.RainbowType.getFromString(name);
    }

    public static int calculateGlassColor(BlockPos pos) {
        float f = (float)AMConfig.rainbowGlassFidelity;
        float f1 = (float)((AMSimplexNoise.noise((pos.getX() + f) / f, (pos.getY() + f) / f, (pos.getZ() + f) / f) + 1.0F) * 0.5F);
        return Color.HSBtoRGB(f1, 1.0F, 1.0F);
    }
}
