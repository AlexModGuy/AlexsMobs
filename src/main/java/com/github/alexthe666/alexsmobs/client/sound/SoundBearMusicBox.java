package com.github.alexthe666.alexsmobs.client.sound;

import com.github.alexthe666.alexsmobs.ClientProxy;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;

public class SoundBearMusicBox extends AbstractTickableSoundInstance {
    private final EntityGrizzlyBear bear;

    public SoundBearMusicBox(EntityGrizzlyBear bear) {
        super(AMSoundRegistry.APRIL_FOOLS_MUSIC_BOX.get(), SoundSource.RECORDS, bear.getRandom());
        this.bear = bear;
        this.attenuation = Attenuation.LINEAR;
        this.looping = true;
        this.delay = 0;
        this.x = this.bear.getX();
        this.y = this.bear.getY();
        this.z = this.bear.getZ();
    }

    public boolean canPlaySound() {
        return this.bear.getAprilFoolsFlag() == 4 && ClientProxy.BEAR_MUSIC_BOX_SOUND_MAP.get(this.bear.getId()) == this;
    }

    public boolean isOnlyMusicBox() {
        for(SoundBearMusicBox s : ClientProxy.BEAR_MUSIC_BOX_SOUND_MAP.values()){
            if(s != this && distanceSq(s.x, s.y, s.z) < 16 && s.canPlaySound()){
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
        if (!this.bear.isRemoved() && this.bear.isAlive() && this.bear.getAprilFoolsFlag() == 4) {
            this.volume = 3;
            this.pitch = 1;
            this.x = this.bear.getX();
            this.y = this.bear.getY();
            this.z = this.bear.getZ();
        } else {
            this.stop();
            ClientProxy.BEAR_MUSIC_BOX_SOUND_MAP.remove(bear.getId());
        }
    }

}
