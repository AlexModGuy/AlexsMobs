package com.github.alexthe666.alexsmobs.client.sound;

import com.github.alexthe666.alexsmobs.ClientProxy;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.SoundCategory;

import java.util.Map;

public class SoundLaCucaracha extends TickableSound {
    private final EntityCockroach cockroach;

    public SoundLaCucaracha(EntityCockroach cockroach) {
        super(AMSoundRegistry.LA_CUCARACHA, SoundCategory.RECORDS);
        this.cockroach = cockroach;
        this.attenuationType = AttenuationType.LINEAR;
        this.repeat = true;
        this.repeatDelay = 0;
        this.priority = true;
        this.x = this.cockroach.getPosX();
        this.y = this.cockroach.getPosY();
        this.z = this.cockroach.getPosZ();
    }

    public boolean shouldPlaySound() {
        return !this.cockroach.isSilent() && this.cockroach.hasMaracas() && this.cockroach.isDancing() && ClientProxy.COCKROACH_SOUND_MAP.get(this.cockroach.getEntityId()) == this;
    }

    public boolean isOnlyCockroach() {
        for(Map.Entry<Integer, SoundLaCucaracha> entry : ClientProxy.COCKROACH_SOUND_MAP.entrySet()){
            SoundLaCucaracha cucaracha = entry.getValue();
            if(cucaracha != this && distanceSq(cucaracha.x, cucaracha.y, cucaracha.z) < 16 && cucaracha.shouldPlaySound()){
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
        if (!this.cockroach.removed && this.cockroach.isAlive() && this.cockroach.isDancing() && this.cockroach.hasMaracas()) {
            this.volume = 1;
            this.pitch = 1;
            this.x = this.cockroach.getPosX();
            this.y = this.cockroach.getPosY();
            this.z = this.cockroach.getPosZ();
        } else {
            this.finishPlaying();
            ClientProxy.COCKROACH_SOUND_MAP.remove(cockroach.getEntityId());
        }
    }

}
