package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.citadel.client.model.container.JsonUtils;
import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CapsidRecipe {
    private final NonNullList<Ingredient> ingredients;
    // Store tags separately for direct matching (since Ingredient.of(TagKey) doesn't work correctly at load time)
    private final List<TagKey<Item>> tagIngredients;
    private final List<Item> itemIngredients;
    private ItemStack result = ItemStack.EMPTY;
    private int time = 0;

    public CapsidRecipe(NonNullList<Ingredient> ingredients, List<TagKey<Item>> tagIngredients, List<Item> itemIngredients, ItemStack result, int time) {
        this.result = result;
        this.ingredients = ingredients;
        this.tagIngredients = tagIngredients;
        this.itemIngredients = itemIngredients;
        this.time = time;
    }

    // Container class to hold parsed ingredient data
    private static class ParsedIngredients {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        List<TagKey<Item>> tags = new ArrayList<>();
        List<Item> items = new ArrayList<>();
    }

    private static ParsedIngredients readIngredients(JsonArray ingredientArray) {
        ParsedIngredients result = new ParsedIngredients();

        for (int i = 0; i < ingredientArray.size(); ++i) {
            try {
                JsonElement element = ingredientArray.get(i);
                
                // First, manually check for tag or item and store them directly
                if (element.isJsonObject()) {
                    JsonObject obj = element.getAsJsonObject();
                    if (obj.has("tag")) {
                        ResourceLocation tagLoc = ResourceLocation.parse(obj.get("tag").getAsString());
                        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagLoc);
                        result.tags.add(tagKey);
                        result.ingredients.add(Ingredient.EMPTY); // Placeholder
                        continue;
                    } else if (obj.has("item")) {
                        ResourceLocation itemLoc = ResourceLocation.parse(obj.get("item").getAsString());
                        Item item = BuiltInRegistries.ITEM.get(itemLoc);
                        result.items.add(item);
                        result.ingredients.add(Ingredient.of(item));
                        continue;
                    }
                }
                
                // Fallback to codec parsing
                Ingredient ingredient = Ingredient.CODEC.parse(JsonOps.INSTANCE, element).result().orElse(Ingredient.EMPTY);
                if (!ingredient.isEmpty()) {
                    result.ingredients.add(ingredient);
                } else {
                    AlexsMobs.LOGGER.error("Failed to parse ingredient at index {} in recipe: Result was empty. JSON: {}", i, element);
                }
            } catch (Exception e) {
                AlexsMobs.LOGGER.error("Exception parsing ingredient at index {} in recipe. JSON: {}", i, ingredientArray.get(i), e);
            }
        }
        return result;
    }

    public ItemStack getResult() {
        return result;
    }

    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public int getTime() {
        return time;
    }

    public int getTotalIngredientCount() {
        return tagIngredients.size() + itemIngredients.size() + (int) ingredients.stream().filter(ing -> !ing.isEmpty()).count();
    }

    public boolean matches(ItemStack... stacks) {
        // Check if we have any ingredients at all
        boolean hasTagIngredients = !tagIngredients.isEmpty();
        boolean hasItemIngredients = !itemIngredients.isEmpty();
        boolean hasRegularIngredients = ingredients.stream().anyMatch(ing -> !ing.isEmpty());
        
        if (!hasTagIngredients && !hasItemIngredients && !hasRegularIngredients) {
            return false;
        }

        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) continue;
            
            // Check tag ingredients first
            for (TagKey<Item> tag : tagIngredients) {
                if (stack.is(tag)) {
                    return true;
                }
            }
            
            // Check item ingredients
            for (Item item : itemIngredients) {
                if (stack.is(item)) {
                    return true;
                }
            }
            
            // Check regular ingredients (parsed by codec)
            for (int i = 0; i < ingredients.size(); i++) {
                Ingredient ing = ingredients.get(i);
                if (!ing.isEmpty() && ing.test(stack)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static class Deserializer implements JsonDeserializer<CapsidRecipe> {

        @Override
        public CapsidRecipe deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonobject = json.getAsJsonObject();
            int time = JsonUtils.getInt(jsonobject, "time");
            ItemStack result = ItemStack.EMPTY;
            if (jsonobject.has("result")) {
                // In 1.21, use ItemStack.CODEC for parsing
                JsonObject resultObj = JsonUtils.getJsonObject(jsonobject, "result");
                result = ItemStack.CODEC.parse(JsonOps.INSTANCE, resultObj).result().orElse(ItemStack.EMPTY);
            }
            ParsedIngredients parsed = readIngredients(JsonUtils.getJsonArray(jsonobject, "ingredients"));
            return new CapsidRecipe(parsed.ingredients, parsed.tags, parsed.items, result, time);
        }

    }
}
