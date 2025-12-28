package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.NeoForgeMod;

import javax.annotation.Nullable;
import java.util.List;

public class ItemModArmor extends ArmorItem {
    private Multimap<Holder<Attribute>, AttributeModifier> attributeMapCroc;
    private Multimap<Holder<Attribute>, AttributeModifier> attributeMapMoose;
    private Multimap<Holder<Attribute>, AttributeModifier> attributeMapFlyingFish;
    private Multimap<Holder<Attribute>, AttributeModifier> attributeMapKimono;
    private final AMArmorMaterial amMaterial;

    public ItemModArmor(AMArmorMaterial armorMaterial, ArmorItem.Type slot) {
        super(armorMaterial.getHolder(), slot, new Item.Properties().durability(armorMaterial.getDurabilityForType(slot)));
        this.amMaterial = armorMaterial;
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsMobs.PROXY.getArmorRenderProperties());
    }


    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        if (this.amMaterial == AMItemRegistry.CENTIPEDE_ARMOR_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.centipede_leggings.desc").withStyle(ChatFormatting.GRAY));
        }
        if (this.amMaterial == AMItemRegistry.EMU_ARMOR_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.emu_leggings.desc").withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, tooltip, flagIn);
        if (this.amMaterial == AMItemRegistry.ROADRUNNER_ARMOR_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.roadrunner_boots.desc").withStyle(ChatFormatting.BLUE));
        }
        if (this.amMaterial == AMItemRegistry.RACCOON_ARMOR_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.frontier_cap.desc").withStyle(ChatFormatting.BLUE));
        }
        if (this.amMaterial == AMItemRegistry.FROSTSTALKER_ARMOR_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.froststalker_helmet.desc").withStyle(ChatFormatting.AQUA));
        }
        if (this.amMaterial == AMItemRegistry.ROCKY_ARMOR_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.rocky_chestplate.desc").withStyle(ChatFormatting.GRAY));
        }
        if (this.amMaterial == AMItemRegistry.SOMBRERO_ARMOR_MATERIAL && AlexsMobs.isAprilFools()) {
            tooltip.add(Component.translatable("item.alexsmobs.sombrero.special_desc").withStyle(ChatFormatting.GRAY));
        }
        if (this.amMaterial == AMItemRegistry.FLYING_FISH_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.flying_fish_boots.desc").withStyle(ChatFormatting.GRAY));
        }
        if (this.amMaterial == AMItemRegistry.NOVELTY_HAT_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.novelty_hat.desc").withStyle(ChatFormatting.GRAY));
        }
        if (this.amMaterial == AMItemRegistry.KIMONO_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.unsettling_kimono.desc").withStyle(ChatFormatting.GRAY));
        }
    }

    private void buildCrocAttributes(AMArmorMaterial materialIn) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "armor_croc_" + type.getName());
        builder.put(Attributes.ARMOR, new AttributeModifier(id, materialIn.getDefenseForType(this.type), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(id, materialIn.getToughness(), AttributeModifier.Operation.ADD_VALUE));
        builder.put(NeoForgeMod.SWIM_SPEED, new AttributeModifier(id, 1.0, AttributeModifier.Operation.ADD_VALUE));
        float knockbackResistance = materialIn.getKnockbackResistance();
        if (knockbackResistance > 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(id, knockbackResistance, AttributeModifier.Operation.ADD_VALUE));
        }
        attributeMapCroc = builder.build();
    }

    private void buildFlyingFishAttributes(AMArmorMaterial materialIn) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "armor_flyingfish_" + type.getName());
        builder.put(Attributes.ARMOR, new AttributeModifier(id, materialIn.getDefenseForType(this.type), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(id, materialIn.getToughness(), AttributeModifier.Operation.ADD_VALUE));
        builder.put(NeoForgeMod.SWIM_SPEED, new AttributeModifier(id, 0.5, AttributeModifier.Operation.ADD_VALUE));
        attributeMapFlyingFish = builder.build();
    }

    private void buildMooseAttributes(AMArmorMaterial materialIn) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "armor_moose_" + type.getName());
        builder.put(Attributes.ARMOR, new AttributeModifier(id, materialIn.getDefenseForType(this.type), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(id, materialIn.getToughness(), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(id, 2.0, AttributeModifier.Operation.ADD_VALUE));
        float knockbackResistance = materialIn.getKnockbackResistance();
        if (knockbackResistance > 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(id, knockbackResistance, AttributeModifier.Operation.ADD_VALUE));
        }
        attributeMapMoose = builder.build();
    }

    private void buildKimonoAttributes(AMArmorMaterial materialIn) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "armor_kimono_" + type.getName());
        builder.put(Attributes.ARMOR, new AttributeModifier(id, materialIn.getDefenseForType(this.type), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(id, materialIn.getToughness(), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(id, 2.0, AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(id, 2.0, AttributeModifier.Operation.ADD_VALUE));
        attributeMapKimono = builder.build();
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        if (amMaterial == AMItemRegistry.CROCODILE_ARMOR_MATERIAL && equipmentSlot == this.type.getSlot()) {
            if (attributeMapCroc == null) {
                buildCrocAttributes(AMItemRegistry.CROCODILE_ARMOR_MATERIAL);
            }
            return attributeMapCroc;
        }
        if (amMaterial == AMItemRegistry.MOOSE_ARMOR_MATERIAL && equipmentSlot == this.type.getSlot()) {
            if (attributeMapMoose == null) {
                buildMooseAttributes(AMItemRegistry.MOOSE_ARMOR_MATERIAL);
            }
            return attributeMapMoose;
        }
        if (amMaterial == AMItemRegistry.FLYING_FISH_MATERIAL && equipmentSlot == this.type.getSlot()) {
            if (attributeMapFlyingFish == null) {
                buildFlyingFishAttributes(AMItemRegistry.FLYING_FISH_MATERIAL);
            }
            return attributeMapFlyingFish;
        }
        if (amMaterial == AMItemRegistry.KIMONO_MATERIAL && equipmentSlot == this.type.getSlot()) {
            if (attributeMapKimono == null) {
                buildKimonoAttributes(AMItemRegistry.KIMONO_MATERIAL);
            }
            return attributeMapKimono;
        }
        return ImmutableMultimap.of();
    }

    @Nullable
    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        if (this.amMaterial == AMItemRegistry.CROCODILE_ARMOR_MATERIAL) {
            return ResourceLocation.parse("alexsmobs:textures/armor/crocodile_chestplate.png");
        } else if (this.amMaterial == AMItemRegistry.ROADRUNNER_ARMOR_MATERIAL) {
            return ResourceLocation.parse("alexsmobs:textures/armor/roadrunner_boots.png");
        } else if (this.amMaterial == AMItemRegistry.CENTIPEDE_ARMOR_MATERIAL) {
            return ResourceLocation.parse("alexsmobs:textures/armor/centipede_leggings.png");
        } else if (this.amMaterial == AMItemRegistry.MOOSE_ARMOR_MATERIAL) {
            return ResourceLocation.parse("alexsmobs:textures/armor/moose_headgear.png");
        } else if (this.amMaterial == AMItemRegistry.RACCOON_ARMOR_MATERIAL) {
            return ResourceLocation.parse("alexsmobs:textures/armor/frontier_cap.png");
        } else if (this.amMaterial == AMItemRegistry.SOMBRERO_ARMOR_MATERIAL) {
            return ResourceLocation.parse("alexsmobs:textures/armor/sombrero.png");
        } else if (this.amMaterial == AMItemRegistry.SPIKED_TURTLE_SHELL_ARMOR_MATERIAL) {
            return ResourceLocation.parse("alexsmobs:textures/armor/spiked_turtle_shell.png");
        } else if (this.amMaterial == AMItemRegistry.FEDORA_ARMOR_MATERIAL) {
            return ResourceLocation.parse("alexsmobs:textures/armor/fedora.png");
        } else if (this.amMaterial == AMItemRegistry.EMU_ARMOR_MATERIAL) {
            return ResourceLocation.parse("alexsmobs:textures/armor/emu_leggings.png");
        } else if (this.amMaterial == AMItemRegistry.FROSTSTALKER_ARMOR_MATERIAL) {
            return ResourceLocation.parse("alexsmobs:textures/armor/froststalker_helmet.png");
        } else if (this.amMaterial == AMItemRegistry.ROCKY_ARMOR_MATERIAL) {
            return ResourceLocation.parse("alexsmobs:textures/armor/rocky_chestplate.png");
        } else if (this.amMaterial == AMItemRegistry.FLYING_FISH_MATERIAL) {
            return ResourceLocation.parse("alexsmobs:textures/armor/flying_fish_boots.png");
        } else if (this.amMaterial == AMItemRegistry.NOVELTY_HAT_MATERIAL) {
            return ResourceLocation.parse("alexsmobs:textures/armor/novelty_hat.png");
        } else if (this.amMaterial == AMItemRegistry.KIMONO_MATERIAL) {
            return ResourceLocation.parse("alexsmobs:textures/armor/unsettling_kimono.png");
        }
        return super.getArmorTexture(stack, entity, slot, layer, innerModel);
    }
}

