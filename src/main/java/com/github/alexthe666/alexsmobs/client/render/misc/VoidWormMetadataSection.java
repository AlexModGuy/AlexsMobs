package com.github.alexthe666.alexsmobs.client.render.misc;

import com.google.gson.JsonObject;
import net.minecraft.client.resources.metadata.animation.VillagerMetaDataSection;
import net.minecraft.client.resources.metadata.animation.VillagerMetadataSectionSerializer;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class VoidWormMetadataSection {
    public static final VoidWormMetadataSection.Serializer SERIALIZER = new VoidWormMetadataSection.Serializer();
    private final boolean hasEndPortalTexture;

    public VoidWormMetadataSection(){
        this.hasEndPortalTexture = false;
    }

    public VoidWormMetadataSection(boolean hasEndPortalTexture) {
        this.hasEndPortalTexture = hasEndPortalTexture;
    }

    public boolean isEndPortalTexture() {
        return this.hasEndPortalTexture;
    }

    private static class Serializer  implements MetadataSectionSerializer<VoidWormMetadataSection> {
        private Serializer() {
        }

        public VoidWormMetadataSection fromJson(JsonObject json) {
            return new VoidWormMetadataSection(GsonHelper.getAsBoolean(json, "end_portal_texture"));
        }

        public String getMetadataSectionName() {
            return "void_worm";
        }
    }

}
