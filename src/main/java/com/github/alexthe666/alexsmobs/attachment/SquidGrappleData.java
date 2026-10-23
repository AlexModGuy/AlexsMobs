package com.github.alexthe666.alexsmobs.attachment;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record SquidGrappleData(
    List<EntityReference> hooks,
    int lastReplacedHook
) {
    public static final SquidGrappleData EMPTY = new SquidGrappleData(List.of(), 0);
    public static final int MAX_GRAPPLES = 4;

    public static final Codec<SquidGrappleData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            EntityReference.CODEC.sizeLimitedListOf(MAX_GRAPPLES)
                .optionalFieldOf("hooks", List.of())
                .forGetter(SquidGrappleData::hooks),
            Codec.INT.optionalFieldOf("lastReplacedHook", 0)
                .forGetter(SquidGrappleData::lastReplacedHook)
        )
            .apply(instance, SquidGrappleData::new)
    );

    public boolean hookExists(int index, Level level) {
        return this.hooks().size() > index && this.hooks().get(index).getEntity(level) != null;
    }

    public int getAnyNullHooks(Level level) {
        for (int i = 0; i < MAX_GRAPPLES; i++) {
            if (!this.hookExists(i, level))
                return i;
        }

        return -1;
    }

    public int getHookCount(Level level) {
        int count = 0;

        for (int i = 0; i < MAX_GRAPPLES; i++) {
            if (this.hookExists(i, level))
                count++;
        }

        return count;
    }

    public SquidGrappleData withLastReplacedHook(int hook) {
        return new SquidGrappleData(this.hooks(), hook);
    }

    public SquidGrappleData withNewHook(int index, Entity hook) {
        if (index >= MAX_GRAPPLES)
            throw new IllegalArgumentException("Tried to add more than " + MAX_GRAPPLES + " hooks to grapple! (got index " + index + ")");

        List<EntityReference> hooks = new ArrayList<>(this.hooks());
        if (index >= hooks.size()) {
            for (int i = hooks.size() - 1; i < MAX_GRAPPLES; i++) {
                hooks.add(EntityReference.Empty.INSTANCE);
            }
        }

        hooks.set(index, EntityReference.getReferenceForEntity(hook));
        return new SquidGrappleData(hooks, this.lastReplacedHook());
    }
}
