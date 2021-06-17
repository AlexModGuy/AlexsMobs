package com.github.alexthe666.alexsmobs.client.sound;

import com.github.alexthe666.alexsmobs.ClientProxy;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.SoundCategory;

import java.util.Map;

public class SoundWormBoss extends TickableSound {
    private final EntityVoidWorm voidWorm;
    private int ticksExisted = 0;
    public SoundWormBoss(EntityVoidWorm worm) {
        super(AMSoundRegistry.MUSIC_WORMBOSS, SoundCategory.RECORDS);
        this.voidWorm = worm;
        this.attenuationType = AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;
        this.priority = true;
        this.x = this.voidWorm.getPosX();
        this.y = this.voidWorm.getPosY();
        this.z = this.voidWorm.getPosZ();
    }

    public boolean shouldPlaySound() {
        return !this.voidWorm.isSilent() && ClientProxy.WORMBOSS_SOUND_MAP.get(this.voidWorm.getEntityId()) == this;
    }

    public boolean isNearest() {
        float dist = 400;
        for(Map.Entry<Integer, SoundWormBoss> entry : ClientProxy.WORMBOSS_SOUND_MAP.entrySet()){
            SoundWormBoss wormBoss = entry.getValue();
            if(wormBoss != this && distanceSq(wormBoss.x, wormBoss.y, wormBoss.z) < dist * dist && wormBoss.shouldPlaySound()){
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
            Minecraft.getInstance().getMusicTicker().stop();

        }
        if (!this.voidWorm.removed && this.voidWorm.isAlive()) {
            this.volume = 1;
            this.pitch = 1;
            this.x = this.voidWorm.getPosX();
            this.y = this.voidWorm.getPosY();
            this.z = this.voidWorm.getPosZ();
        } else {
            this.finishPlaying();
            ClientProxy.WORMBOSS_SOUND_MAP.remove(voidWorm.getEntityId());
        }
        ticksExisted++;
    }

}
