package com.github.alexthe666.alexsmobs;

import com.github.alexthe666.alexsmobs.client.gui.GUIAnimalDictionary;
import com.github.alexthe666.alexsmobs.client.render.AMItemstackRenderer;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.config.BiomeConfig;
import com.github.alexthe666.alexsmobs.config.CommonConfig;
import com.github.alexthe666.alexsmobs.config.ConfigHolder;
import com.github.alexthe666.alexsmobs.misc.BananaLootModifier;
import com.github.alexthe666.alexsmobs.misc.MatchesBananaTagCondition;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.util.concurrent.Callable;

import static com.github.alexthe666.alexsmobs.AlexsMobs.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonProxy {
    public static final LootConditionType MATCHES_BANANA_CONDTN = registerLootCondition("alexsmobs:matches_banana_tag", new MatchesBananaTagCondition.Serializer());

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent event) {
        final ModConfig config = event.getConfig();
        // Rebake the configs when they change
        if (config.getSpec() == ConfigHolder.COMMON_SPEC) {
            AMConfig.bake(config);
        } else if (config.getSpec() == ConfigHolder.BIOME_SPEC) {
            BiomeConfig.bake(config);
        }
    }

    @SubscribeEvent
    public static void registerModifierSerializers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        if (AMConfig.bananasDropFromLeaves) {
            event.getRegistry().register(new BananaLootModifier.Serializer().setRegistryName(new ResourceLocation("alexsmobs:banana_drop")));
        }
    }

    private static LootConditionType registerLootCondition(String registryName, ILootSerializer<? extends ILootCondition> serializer) {
        return Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(registryName), new LootConditionType(serializer));
    }

    public void init() {
    }

    public void clientInit() {
    }

    public Item.Properties setupISTER(Item.Properties group) {
        return group;
    }

    public PlayerEntity getClientSidePlayer() {
        return null;
    }

    public void openBookGUI(ItemStack itemStackIn) {
    }

    public Object getArmorModel(int armorId) {
        return null;
    }

    public void addBananaLoot() {
        //   Blocks.JUNGLE_LEAVES.lootTable;

    }
}
