package com.github.alexthe666.alexsmobs.entity.util;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class FlyingFishBootsUtil {

    private static final String BOOST_TICKS = "FlyingFishBoostAlexsMobs";
    private static final int MIN_BOOST_TIME = 35;

    public static void setBoostTicks(LivingEntity entity, int ticks) {
        CompoundTag lassoedTag = CitadelEntityData.getOrCreateCitadelTag(entity);
        lassoedTag.putInt(BOOST_TICKS, ticks);

        CitadelEntityData.setCitadelTag(entity, lassoedTag);
        if (!entity.level.isClientSide) {
            Citadel.sendMSGToAll(new PropertiesMessage("CitadelPatreonConfig", lassoedTag, entity.getId()));
        }else{
            Citadel.sendMSGToServer(new PropertiesMessage("CitadelPatreonConfig", lassoedTag, entity.getId()));
        }
    }

    public static int getBoostTicks(LivingEntity entity) {
        CompoundTag lassoedTag = CitadelEntityData.getOrCreateCitadelTag(entity);
        if (lassoedTag.contains(BOOST_TICKS)) {
            return lassoedTag.getInt(BOOST_TICKS);
        }
        return 0;
    }

    public static boolean isWearing(LivingEntity entity) {
        return entity.getItemBySlot(EquipmentSlot.FEET).getItem() == AMItemRegistry.FLYING_FISH_BOOTS.get();
    }

    public static void tickFlyingFishBoots(LivingEntity fishy) {
        int boostTime = getBoostTicks(fishy);
        if(boostTime <= 15 && fishy.isInWaterOrBubble() && !fishy.isOnGround()){
            if(fishy.getFluidHeight(FluidTags.WATER) < 0.2F && fishy.jumping &&( !(fishy instanceof Player) || !((Player) fishy).getAbilities().flying)){
                RandomSource rand = fishy.getRandom();
                boostTime = MIN_BOOST_TIME;
                Vec3 forward = new Vec3(0, 0.0F, 0.5F + rand.nextFloat() * 1.2F).xRot(-fishy.getXRot() * ((float) Math.PI / 180F)).yRot(-fishy.getYHeadRot() * ((float) Math.PI / 180F));
                Vec3 delta = fishy.getDeltaMovement().add(forward);
                fishy.setDeltaMovement(delta.x, 0.3 + rand.nextFloat() * 0.3F, delta.z);
                fishy.setYRot(fishy.getYHeadRot());
            }
        }
        if(boostTime > 0){
            if(!fishy.isInWaterOrBubble() && !fishy.isOnGround()){
                if(fishy.getDeltaMovement().y < 0){
                    fishy.setDeltaMovement(fishy.getDeltaMovement().multiply(1F, 0.75F, 1F));
                }
                fishy.setPose(Pose.FALL_FLYING);
            }
            setBoostTicks(fishy, boostTime - 1);
        }
    }
}
