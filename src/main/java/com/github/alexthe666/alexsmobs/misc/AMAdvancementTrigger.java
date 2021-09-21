package com.github.alexthe666.alexsmobs.misc;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ConstructBeaconTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;

public class AMAdvancementTrigger extends SimpleCriterionTrigger<AMAdvancementTrigger.Instance> {
    public final ResourceLocation resourceLocation;

    public AMAdvancementTrigger(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public AMAdvancementTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.Composite p_230241_2_, DeserializationContext p_230241_3_) {
        return new AMAdvancementTrigger.Instance(p_230241_2_, resourceLocation);
    }

    public void trigger(ServerPlayer p_192180_1_) {
        this.trigger(p_192180_1_, (p_226308_1_) -> {
            return true;
        });
    }

    @Override
    public ResourceLocation getId() {
        return resourceLocation;
    }


    public static class Instance extends AbstractCriterionTriggerInstance {

        public Instance(EntityPredicate.Composite p_i231507_1_, ResourceLocation res) {
            super(res, p_i231507_1_);
        }

        public static ConstructBeaconTrigger.TriggerInstance forLevel(MinMaxBounds.Ints p_203912_0_) {
            return new ConstructBeaconTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_203912_0_);
        }



        public JsonObject serializeToJson(SerializationContext p_230240_1_) {
            JsonObject lvt_2_1_ = super.serializeToJson(p_230240_1_);
            return lvt_2_1_;
        }
    }
}
