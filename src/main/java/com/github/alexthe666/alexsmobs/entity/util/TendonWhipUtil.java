package com.github.alexthe666.alexsmobs.entity.util;

import com.github.alexthe666.alexsmobs.attachment.AMAttachmentRegistry;
import com.github.alexthe666.alexsmobs.attachment.EntityReference;
import com.github.alexthe666.alexsmobs.entity.EntityTendonSegment;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class TendonWhipUtil {

    public static void setLastTendon(LivingEntity entity, EntityTendonSegment tendon) {
        entity.setData(AMAttachmentRegistry.TENDON_WHIP, EntityReference.getReferenceForEntity(tendon));
    }

    public static void retractFarTendons(Level level, LivingEntity player) {
        EntityTendonSegment last = getLastTendon(player);
        if (last != null) {
            last.remove(Entity.RemovalReason.DISCARDED);
            setLastTendon(player, null);
        }
    }

    public static boolean canLaunchTendons(Level level, LivingEntity player) {
        EntityTendonSegment last = getLastTendon(player);
        if (last != null) {
            return last.isRemoved() || last.distanceTo(player) > 30;
        }
        return true;
    }

    public static EntityTendonSegment getLastTendon(LivingEntity player) {
        Entity e = player.getData(AMAttachmentRegistry.TENDON_WHIP).getEntity(player.level());

        if (e instanceof EntityTendonSegment tendonSegment)
            return tendonSegment;

        return null;
    }
}
