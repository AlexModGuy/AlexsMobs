package com.github.alexthe666.alexsmobs.client.sound;

import com.github.alexthe666.alexsmobs.ClientProxy;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;

public class SoundWormBoss extends AbstractTickableSoundInstance {
    private final EntityVoidWorm voidWorm;
    private int ticksExisted = 0;
    public SoundWormBoss(EntityVoidWorm worm) {
        super(AMSoundRegistry.MUSIC_WORMBOSS.get(), SoundSource.RECORDS, worm.getRandom());
        this.voidWorm = worm;
        this.attenuation = Attenuation.NONE;
        this.looping = true;
        this.delay = 0;
        this.x = this.voidWorm.getX();
        this.y = this.voidWorm.getY();
        this.z = this.voidWorm.getZ();
    }

    public boolean canPlaySound() {
        return !this.voidWorm.isSilent() && ClientProxy.WORMBOSS_SOUND_MAP.get(this.voidWorm.getId()) == this;
    }

    public boolean isNearest() {
        float dist = 400;
        for(SoundWormBoss wormBoss : ClientProxy.WORMBOSS_SOUND_MAP.values()){
            if(wormBoss != this && distanceSq(wormBoss.x, wormBoss.y, wormBoss.z) < dist * dist && wormBoss.canPlaySound()){
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
        if(ticksExisted % 100 == 0){
            Minecraft.getInstance().getMusicManager().stopPlaying();

        }
        if (!this.voidWorm.isRemoved() && this.voidWorm.isAlive()) {
            this.volume = 1;
            this.pitch = 1;
            this.x = this.voidWorm.getX();
            this.y = this.voidWorm.getY();
            this.z = this.voidWorm.getZ();
        } else {
            this.stop();
            ClientProxy.WORMBOSS_SOUND_MAP.remove(voidWorm.getId());
        }
        ticksExisted++;
    }

}
