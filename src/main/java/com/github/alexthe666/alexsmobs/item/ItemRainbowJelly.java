package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.util.RainbowUtil;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

import java.util.Random;

public class ItemRainbowJelly extends Item{

    public ItemRainbowJelly(Item.Properties tab) {
        super(tab);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
        int i = RainbowUtil.getRainbowTypeFromStack(stack);
        if(RainbowUtil.getRainbowType(target) != i){
            RainbowUtil.setRainbowType(target, i);
            RandomSource random = playerIn.getRandom();
            for (int j = 0; j < 6 + random.nextInt(3); j++) {
                double d2 = random.nextGaussian() * 0.02D;
                double d0 = random.nextGaussian() * 0.02D;
                double d1 = random.nextGaussian() * 0.02D;
                playerIn.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), target.getX() + (double) (random.nextFloat() * target.getBbWidth()) - (double) target.getBbWidth() * 0.5F, target.getY() + target.getBbHeight() * 0.5F + (double) (random.nextFloat() * target.getBbHeight() * 0.5F), target.getZ() + (double) (random.nextFloat() * target.getBbWidth()) - (double) target.getBbWidth() * 0.5F, d0, d1, d2);
            }
            target.playSound(SoundEvents.SLIME_SQUISH_SMALL, 1F, target.getVoicePitch());
            if(!playerIn.isCreative()){
                stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public ItemStack finishUsingItem(ItemStack st, Level level, LivingEntity e) {
        RainbowUtil.setRainbowType(e, RainbowUtil.getRainbowTypeFromStack(st));
        return this.isEdible() ? e.eat(level, st) : st;
    }

    public int getUseDuration(ItemStack stack) {
        if (stack.getItem().isEdible()) {
            return 64;
        } else {
            return 0;
        }
    }

    public boolean isFoil(ItemStack stack) {
        return super.isFoil(stack) || RainbowUtil.getRainbowTypeFromStack(stack) > 1;
    }
}
