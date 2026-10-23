package com.github.alexthe666.alexsmobs.entity.util;

import com.github.alexthe666.alexsmobs.attachment.AMAttachmentRegistry;
import com.github.alexthe666.alexsmobs.attachment.EntityReference;
import com.github.alexthe666.alexsmobs.attachment.LassoData;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class VineLassoUtil {

    public static void lassoTo(@Nullable LivingEntity lassoer, LivingEntity lassoed) {
        if (lassoer != null) {
            if (!hasLassoData(lassoed)) {
                lassoed.setData(AMAttachmentRegistry.LASSO_DATA, new LassoData(EntityReference.getReferenceForEntity(lassoer)));
            }
        } else {
            lassoed.removeData(AMAttachmentRegistry.LASSO_DATA);
        }
    }

    public static boolean hasLassoData(LivingEntity lasso) {
        return lasso.getExistingDataOrNull(AMAttachmentRegistry.LASSO_DATA) == null;
    }

    public static Entity getLassoedTo(LivingEntity lassoed) {
        LassoData lassoData = lassoed.getExistingDataOrNull(AMAttachmentRegistry.LASSO_DATA);
        if(lassoData == null){
            return null;
        }
        if (hasLassoData(lassoed)) {
            return lassoData.lassoedTo().getEntity(lassoed.level());
        }
        return null;
    }

    public static void tickLasso(LivingEntity lassoed) {
        Entity lassoedOwner = VineLassoUtil.getLassoedTo(lassoed);
        if (lassoedOwner != null) {
            double distance = lassoed.distanceTo(lassoedOwner);

            if (lassoed instanceof Mob mob) {
                if (distance > 3.0F) {
                    mob.getNavigation().moveTo(lassoedOwner, 1.0F);
                } else {
                    mob.getNavigation().stop();
                }
            }
            if (distance > 10) {
                double d0 = (lassoedOwner.getX() - lassoed.getX()) / distance;
                double d1 = (lassoedOwner.getY() - lassoed.getY()) / distance;
                double d2 = (lassoedOwner.getZ() - lassoed.getZ()) / distance;
                double yd = Math.copySign(d1 * d1 * 0.4D, d1);
                if(lassoed instanceof Player){
                    yd = 0;
                }
                lassoed.setDeltaMovement(lassoed.getDeltaMovement().add(Math.copySign(d0 * d0 * 0.4D, d0), yd, Math.copySign(d2 * d2 * 0.4D, d2)));
            }
        }
    }
}
