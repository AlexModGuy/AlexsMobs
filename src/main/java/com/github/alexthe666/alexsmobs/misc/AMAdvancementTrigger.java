package com.github.alexthe666.alexsmobs.misc;

import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.util.ResourceLocation;

public class AMAdvancementTrigger extends AbstractCriterionTrigger<AMAdvancementTrigger.Instance> {
    public final ResourceLocation resourceLocation;

    public AMAdvancementTrigger(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public AMAdvancementTrigger.Instance deserializeTrigger(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
        return new AMAdvancementTrigger.Instance(p_230241_2_, resourceLocation);
    }

    public void trigger(ServerPlayerEntity p_192180_1_) {
        this.triggerListeners(p_192180_1_, (p_226308_1_) -> {
            return true;
        });
    }

    @Override
    public ResourceLocation getId() {
        return resourceLocation;
    }


    public static class Instance extends CriterionInstance {

        public Instance(EntityPredicate.AndPredicate p_i231507_1_, ResourceLocation res) {
            super(res, p_i231507_1_);
        }

        public static ConstructBeaconTrigger.Instance forLevel(MinMaxBounds.IntBound p_203912_0_) {
            return new ConstructBeaconTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, p_203912_0_);
        }



        public JsonObject serialize(ConditionArraySerializer p_230240_1_) {
            JsonObject lvt_2_1_ = super.serialize(p_230240_1_);
            return lvt_2_1_;
        }
    }
}
