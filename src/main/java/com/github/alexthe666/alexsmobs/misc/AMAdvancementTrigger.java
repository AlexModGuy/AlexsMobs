package com.github.alexthe666.alexsmobs.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class AMAdvancementTrigger extends SimpleCriterionTrigger<AMAdvancementTrigger.Instance> {
    
    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, (instance) -> true);
    }

    public static record Instance(Optional<ContextAwarePredicate> player) implements SimpleInstance {
        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Instance::player)
            ).apply(instance, Instance::new)
        );

        public static Instance simple() {
            return new Instance(Optional.empty());
        }

        @Override
        public Optional<ContextAwarePredicate> player() {
            return this.player;
        }
    }
}
