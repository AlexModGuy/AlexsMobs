package com.github.alexthe666.alexsmobs.misc;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class AMDamageTypes {
    public static final DamageSource BEAR_FREDDY = new DamageSource("freddy");
    public static final DamageSource causeFarseerDamage(LivingEntity attacker){
       return new DamageSourceRandomMessages("farseer", attacker).setScalesWithDifficulty().bypassArmor().bypassEnchantments().setMagic();
    }

    private static class DamageSourceRandomMessages extends EntityDamageSource {

        public DamageSourceRandomMessages(String message, Entity entity) {
            super(message, entity);
        }

        @Override
        public Component getLocalizedDeathMessage(LivingEntity attacked) {
            int type = attacked.getRandom().nextInt(3);
            LivingEntity livingentity = attacked.getKillCredit();
            String s = "death.attack." + this.msgId + "_" + type;
            String s1 = s + ".player";
            return livingentity != null ? Component.translatable(s1, attacked.getDisplayName(), livingentity.getDisplayName()) : Component.translatable(s, attacked.getDisplayName());
        }
    }
}
