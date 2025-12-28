package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

/**
 * Armor material helper class for NeoForge 1.21.
 * In 1.21, ArmorMaterial is a record and must be registered.
 */
public class AMArmorMaterial {

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, AlexsMobs.MODID);

    protected static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};

    private final String name;
    private final int durability;
    private final int[] damageReduction;
    private final int enchantability;
    private final Holder<SoundEvent> sound;
    private final float toughness;
    private Supplier<Ingredient> ingredient = () -> Ingredient.EMPTY;
    public float knockbackResistance = 0.0F;
    private Holder<ArmorMaterial> holder;

    public AMArmorMaterial(String name, int durability, int[] damageReduction, int enchantability, Holder<SoundEvent> sound, float toughness) {
        this.name = name;
        this.durability = durability;
        this.damageReduction = damageReduction;
        this.enchantability = enchantability;
        this.sound = sound;
        this.toughness = toughness;
        this.knockbackResistance = 0;
        registerMaterial();
    }

    public AMArmorMaterial(String name, int durability, int[] damageReduction, int enchantability, Holder<SoundEvent> sound, float toughness, float knockbackResist) {
        this.name = name;
        this.durability = durability;
        this.damageReduction = damageReduction;
        this.enchantability = enchantability;
        this.sound = sound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResist;
        registerMaterial();
    }

    private void registerMaterial() {
        this.holder = ARMOR_MATERIALS.register(this.name, () -> {
            EnumMap<ArmorItem.Type, Integer> defenseMap = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, this.damageReduction[3]);
                map.put(ArmorItem.Type.LEGGINGS, this.damageReduction[2]);
                map.put(ArmorItem.Type.CHESTPLATE, this.damageReduction[1]);
                map.put(ArmorItem.Type.HELMET, this.damageReduction[0]);
                map.put(ArmorItem.Type.BODY, this.damageReduction[1]);
            });
            return new ArmorMaterial(
                    defenseMap,
                    this.enchantability,
                    this.sound,
                    this.ingredient,
                    List.of(new ArmorMaterial.Layer(AlexsMobs.prefix(this.name))),
                    this.toughness,
                    this.knockbackResistance
            );
        });
    }

    public int getDurabilityForType(ArmorItem.Type type) {
        return MAX_DAMAGE_ARRAY[type.ordinal()] * this.durability;
    }

    public int getDefenseForType(ArmorItem.Type type) {
        return this.damageReduction[type.ordinal()];
    }

    public int getEnchantmentValue() {
        return this.enchantability;
    }

    public Holder<SoundEvent> getEquipSound() {
        return this.sound;
    }

    public Ingredient getRepairIngredient() {
        return this.ingredient.get();
    }

    public void setRepairMaterial(Ingredient ingredient) {
        this.ingredient = () -> ingredient;
    }

    public String getName() {
        return name;
    }

    public float getToughness() {
        return toughness;
    }

    public float getKnockbackResistance() {
        return knockbackResistance;
    }

    public Holder<ArmorMaterial> getHolder() {
        return holder;
    }
}
