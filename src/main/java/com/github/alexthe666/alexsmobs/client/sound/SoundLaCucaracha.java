package com.github.alexthe666.alexsmobs.client.sound;

import com.github.alexthe666.alexsmobs.ClientProxy;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;

public class SoundLaCucaracha extends AbstractTickableSoundInstance {
    private final EntityCockroach cockroach;

    public SoundLaCucaracha(EntityCockroach cockroach) {
        super(AMSoundRegistry.LA_CUCARACHA.get(), SoundSource.RECORDS, cockroach.getRandom());
        this.cockroach = cockroach;
        this.attenuation = Attenuation.LINEAR;
        this.looping = true;
        this.delay = 0;
        this.x = this.cockroach.getX();
        this.y = this.cockroach.getY();
        this.z = this.cockroach.getZ();
    }

    public boolean canPlaySound() {
        return !this.cockroach.isSilent() && this.cockroach.hasMaracas() && this.cockroach.isDancing() && ClientProxy.COCKROACH_SOUND_MAP.get(this.cockroach.getId()) == this;
    }

    public boolean isOnlyCockroach() {
        for(SoundLaCucaracha cucaracha : ClientProxy.COCKROACH_SOUND_MAP.values()){
            if(cucaracha != this && distanceSq(cucaracha.x, cucaracha.y, cucaracha.z) < 16 && cucaracha.canPlaySound()){
                return false;
            }
        }
        return true;
    }


    public double distanceSq(double p_218140_1_, double p_218140_3_, double p_218140_5_) {
        double lvt_10_1_ = (double)this.getX() - p_218140_1_;
        double lvt_12_1_ = (double)this.getY() - p_218140_3_;
        double lvt_14_1_ = (double)this.getZ() - p_218140_5_;
        return lvt_10_1_ * lvt_10_1_ + lvt_12_1_ * lvt_12_1_ + lvt_14_1_ * lvt_14_1_;
    }

    public void tick() {
        if (!this.cockroach.isRemoved() && this.cockroach.isAlive() && this.cockroach.isDancing() && this.cockroach.hasMaracas()) {
            this.volume = 1;
            this.pitch = 1;
            this.x = this.cockroach.getX();
            this.y = this.cockroach.getY();
            this.z = this.cockroach.getZ();
        } else {
            this.stop();
            ClientProxy.COCKROACH_SOUND_MAP.remove(cockroach.getId());
        }
    }

}
