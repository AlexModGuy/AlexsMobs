package com.github.alexthe666.alexsmobs.misc;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.util.ResourceLocation;

public class AMAdvancementTriggerRegistry {

    public static AMAdvancementTrigger MOSQUITO_SICK = new AMAdvancementTrigger(new ResourceLocation("alexsmobs:mosquito_sick"));
    public static AMAdvancementTrigger EMU_DODGE = new AMAdvancementTrigger(new ResourceLocation("alexsmobs:emu_dodge"));
    public static AMAdvancementTrigger STOMP_LEAFCUTTER_ANTHILL = new AMAdvancementTrigger(new ResourceLocation("alexsmobs:stomp_leafcutter_anthill"));

    public static void init(){
        CriteriaTriggers.register(MOSQUITO_SICK);
        CriteriaTriggers.register(EMU_DODGE);
        CriteriaTriggers.register(STOMP_LEAFCUTTER_ANTHILL);
    }

}
