package com.github.alexthe666.alexsmobs.misc;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.util.ResourceLocation;

public class AMAdvancementTriggerRegistry {

    public static AMAdvancementTrigger MOSQUITO_SICK = new AMAdvancementTrigger(new ResourceLocation("alexsmobs:mosquito_sick"));
    public static AMAdvancementTrigger EMU_DODGE = new AMAdvancementTrigger(new ResourceLocation("alexsmobs:emu_dodge"));
    public static AMAdvancementTrigger STOMP_LEAFCUTTER_ANTHILL = new AMAdvancementTrigger(new ResourceLocation("alexsmobs:stomp_leafcutter_anthill"));
    public static AMAdvancementTrigger BALD_EAGLE_CHALLENGE = new AMAdvancementTrigger(new ResourceLocation("alexsmobs:bald_eagle_challenge"));
    public static AMAdvancementTrigger VOID_WORM_SUMMON = new AMAdvancementTrigger(new ResourceLocation("alexsmobs:void_worm_summon"));
    public static AMAdvancementTrigger VOID_WORM_SPLIT = new AMAdvancementTrigger(new ResourceLocation("alexsmobs:void_worm_split"));
    public static AMAdvancementTrigger VOID_WORM_SLAY_HEAD = new AMAdvancementTrigger(new ResourceLocation("alexsmobs:void_worm_kill"));

    public static void init(){
        CriteriaTriggers.register(MOSQUITO_SICK);
        CriteriaTriggers.register(EMU_DODGE);
        CriteriaTriggers.register(STOMP_LEAFCUTTER_ANTHILL);
        CriteriaTriggers.register(BALD_EAGLE_CHALLENGE);
        CriteriaTriggers.register(VOID_WORM_SUMMON);
        CriteriaTriggers.register(VOID_WORM_SPLIT);
        CriteriaTriggers.register(VOID_WORM_SLAY_HEAD);
    }

}
