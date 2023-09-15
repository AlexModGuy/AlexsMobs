package com.github.alexthe666.alexsmobs.entity.util;

import com.github.alexthe666.alexsmobs.entity.EntitySquidGrapple;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class SquidGrappleUtil {

    private static final String HOOK_1 = "SquidGrappleHook1AlexsMobs";
    private static final String HOOK_2 = "SquidGrappleHook2AlexsMobs";
    private static final String HOOK_3 = "SquidGrappleHook3AlexsMobs";
    private static final String HOOK_4 = "SquidGrappleHook4AlexsMobs";
    private static final String LAST_REPLACED_HOOK = "LastSquidGrappleHookAlexsMobs";

    public static int onFireHook(LivingEntity entity, UUID newHookUUID) {
        CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(entity);
        int index = getFirstAvailableHookIndex(entity);
        String indexStr = getHookStrFromIndex(index);
        if(tag.contains(indexStr)){
            EntitySquidGrapple hook = getHookEntity(entity.level, tag.getUUID(indexStr));
            if(hook != null && !hook.isRemoved()){
                hook.setWithdrawing(true);
            }
        }
        tag.putUUID(indexStr, newHookUUID);
        CitadelEntityData.setCitadelTag(entity, tag);
        return index;
    }

    public static int getFirstAvailableHookIndex(LivingEntity entity){
        int nulls = getAnyNullHooks(entity);
        if(nulls != -1){
            return nulls;
        }
        int i = getHookCount(entity);
        if(i < 4){
            return i;
        }else{
            CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(entity);
            int j = tag.getInt(LAST_REPLACED_HOOK);
            tag.putInt(LAST_REPLACED_HOOK, (j + 1) % 4);
            CitadelEntityData.setCitadelTag(entity, tag);
            return j;
        }
    }

    public static String getHookStrFromIndex(int i){
        switch (i){
            case 0:
                return HOOK_1;
            case 1:
                return HOOK_2;
            case 2:
                return HOOK_3;
            case 3:
                return HOOK_4;
        }
        return HOOK_1;
    }

    public static int getAnyNullHooks(LivingEntity entity) {
        CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(entity);
        if (!tag.contains(HOOK_1) || getHookEntity(entity.level, tag.getUUID(HOOK_1)) == null) {
            return 0;
        }
        if (!tag.contains(HOOK_2) || getHookEntity(entity.level, tag.getUUID(HOOK_2)) == null) {
            return 1;
        }
        if (!tag.contains(HOOK_3) || getHookEntity(entity.level, tag.getUUID(HOOK_3)) == null) {
            return 2;
        }
        if (!tag.contains(HOOK_4) || getHookEntity(entity.level, tag.getUUID(HOOK_4)) == null) {
            return 3;
        }
        return -1;
    }


    public static int getHookCount(LivingEntity entity) {
        CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(entity);
        int count = 0;
        if (tag.contains(HOOK_1) && getHookEntity(entity.level, tag.getUUID(HOOK_1)) != null) {
            count++;
        }
        if (tag.contains(HOOK_2) && getHookEntity(entity.level, tag.getUUID(HOOK_2)) != null) {
            count++;
        }
        if (tag.contains(HOOK_3) && getHookEntity(entity.level, tag.getUUID(HOOK_3)) != null) {
            count++;
        }
        if (tag.contains(HOOK_4) && getHookEntity(entity.level, tag.getUUID(HOOK_4)) != null) {
            count++;
        }
        return count;
    }

    public static EntitySquidGrapple getHookEntity(Level level, UUID id) {
        if (id != null && level instanceof ServerLevel serverLevel) {
            Entity e = serverLevel.getEntity(id);
            return e instanceof EntitySquidGrapple ? (EntitySquidGrapple) e : null;
        }
        return null;
    }
}
