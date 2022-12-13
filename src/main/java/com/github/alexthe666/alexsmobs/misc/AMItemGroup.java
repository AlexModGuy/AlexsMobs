package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.CustomTabBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.registries.RegistryObject;

public class AMItemGroup {

    public static final ResourceLocation TAB = new ResourceLocation("alexsmobs:alexsmobs");

    private static ItemStack makeIcon() {
        return new ItemStack(AMItemRegistry.TAB_ICON.get());
    }

    public static void registerTab(CreativeModeTabEvent.Register event){
        event.registerCreativeModeTab(TAB, builder -> builder.title(Component.translatable("itemGroup.alexsmobs")).icon(AMItemGroup::makeIcon).displayItems((flags, output, isOp) -> {
            for(RegistryObject<Item> item : AMItemRegistry.DEF_REG.getEntries()){
                if(item.get() instanceof CustomTabBehavior customTabBehavior){
                    customTabBehavior.fillItemCategory(output);
                }else{
                    output.accept(item.get());
                }
            }
        }));

    }

}
