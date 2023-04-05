package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class AMDamageTypes {

    public static final DeferredRegister<DamageType> DEF_REG = DeferredRegister.create(Registries.DAMAGE_TYPE, AlexsMobs.MODID);
    public static final RegistryObject<DamageType> BEAR_FREDDY = DEF_REG.register("freddy", () -> new DamageType("freddy", 0.0F));
    public static final RegistryObject<DamageType> FARSEER = DEF_REG.register("farseer", () -> new DamageType("farseer", 0.0F));

    public static DamageSource causeFarseerDamage(LivingEntity attacker){
        return new DamageSourceRandomMessages(FARSEER.getHolder().get(), attacker);
    }

    public static DamageSource causeFreddyBearDamage(LivingEntity attacker){
        return new DamageSource(BEAR_FREDDY.getHolder().get(), attacker);
    }

    private static class DamageSourceRandomMessages extends DamageSource {


        public DamageSourceRandomMessages(Holder<DamageType> damageTypeHolder, @Nullable Entity entity1, @Nullable Entity entity2, @Nullable Vec3 from) {
            super(damageTypeHolder, entity1, entity2, from);
        }

        public DamageSourceRandomMessages(Holder<DamageType> damageTypeHolder, @Nullable Entity entity1, @Nullable Entity entity2) {
            super(damageTypeHolder, entity1, entity2);
        }

        public DamageSourceRandomMessages(Holder<DamageType> damageTypeHolder, Vec3 from) {
            super(damageTypeHolder, from);
        }

        public DamageSourceRandomMessages(Holder<DamageType> damageTypeHolder, @Nullable Entity entity) {
            super(damageTypeHolder, entity);
        }

        public DamageSourceRandomMessages(Holder<DamageType> p_270475_) {
            super(p_270475_);
        }

        @Override
        public Component getLocalizedDeathMessage(LivingEntity attacked) {
            int type = attacked.getRandom().nextInt(3);
            LivingEntity livingentity = attacked.getKillCredit();
            String s = "death.attack." + this.getMsgId() + "_" + type;
            String s1 = s + ".player";
            return livingentity != null ? Component.translatable(s1, attacked.getDisplayName(), livingentity.getDisplayName()) : Component.translatable(s, attacked.getDisplayName());
        }
    }
}
