package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityPlatypus;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PlatypusAIDigForItems extends Goal {

    public static final ResourceLocation PLATYPUS_REWARD = new ResourceLocation("alexsmobs", "gameplay/platypus_reward");
    public static final ResourceLocation PLATYPUS_REWARD_CHARGED = new ResourceLocation("alexsmobs", "gameplay/platypus_supercharged_reward");
    private EntityPlatypus platypus;
    private BlockPos digPos;
    private int generatePosCooldown = 0;
    private int digTime = 0;
    private int maxDroppedItems = 3;

    public PlatypusAIDigForItems(EntityPlatypus platypus) {
        this.platypus = platypus;
    }

    private static List<ItemStack> getItemStacks(EntityPlatypus platypus) {
        LootTable loottable = platypus.level.getServer().getLootTables().get(platypus.superCharged ? PLATYPUS_REWARD_CHARGED : PLATYPUS_REWARD);
        return loottable.getRandomItems((new LootContext.Builder((ServerLevel) platypus.level())).withParameter(LootContextParams.THIS_ENTITY, platypus).withRandom(platypus.level.random).create(LootContextParamSets.PIGLIN_BARTER));
    }

    @Override
    public boolean canUse() {
        if (!platypus.isSensing()) {
            return false;
        }
        if(generatePosCooldown == 0){
            generatePosCooldown = 20 + platypus.getRandom().nextInt(20);
            digPos = genDigPos();
            maxDroppedItems = 2 + platypus.getRandom().nextInt(5);
            return digPos != null;
        }else{
            generatePosCooldown--;
            return false;
        }

    }

    public boolean canContinueToUse() {
        return platypus.getTarget() == null && platypus.isSensing() && platypus.getLastHurtByMob() == null && digPos != null && platypus.level().getBlockState(digPos).getBlock() == Blocks.CLAY && platypus.level().getFluidState(digPos.above()).is(FluidTags.WATER);
    }

    public void tick() {
        double dist = platypus.distanceToSqr(Vec3.atCenterOf(digPos.above()));
        double d0 = digPos.getX() + 0.5 - this.platypus.getX();
        double d1 = digPos.getY() + 0.5 - this.platypus.getEyeY();
        double d2 = digPos.getZ() + 0.5 - this.platypus.getZ();
        float f = (float) (Mth.atan2(d2, d0) * 57.2957763671875D) - 90.0F;
        if (dist < 2) {
            platypus.setDeltaMovement(platypus.getDeltaMovement().add(0, -0.01F, 0));
            platypus.getNavigation().stop();
            digTime++;
            if (digTime % 5 == 0) {
                SoundEvent sound = platypus.level().getBlockState(digPos).getSoundType().getHitSound();
                platypus.gameEvent(GameEvent.BLOCK_ACTIVATE);
                platypus.playSound(sound, 1, 0.5F + platypus.getRandom().nextFloat() * 0.5F);
            }
            int itemDivis = (int) Math.floor(100F / maxDroppedItems);
            if(digTime % itemDivis == 0){
                List<ItemStack> lootList = getItemStacks(platypus);
                if (lootList.size() > 0) {
                    for (ItemStack stack : lootList) {
                        ItemEntity e = this.platypus.spawnAtLocation(stack.copy());
                        e.hasImpulse = true;
                        e.setDeltaMovement(e.getDeltaMovement().multiply(0.2, 0.2, 0.2));
                    }
                }
            }
            if (digTime >= 100) {
                platypus.setSensing(false);
                platypus.setDigging(false);
                digTime = 0;
            } else {
                platypus.setDigging(true);
            }
        } else {
            platypus.setDigging(false);

            platypus.getNavigation().moveTo(digPos.getX(), digPos.getY() + 1, digPos.getZ(), 1);

            platypus.setYRot(f);
        }

    }

    public void stop() {
        generatePosCooldown = 0;
        platypus.setSensing(false);
        platypus.setDigging(false);
        digPos = null;
        digTime = 0;
    }

    private BlockPos genSeafloorPos(BlockPos parent) {
        LevelAccessor world = platypus.level;
        final RandomSource random = this.platypus.getRandom();
        int range = 15;
        for (int i = 0; i < 15; i++) {
            BlockPos seafloor = parent.offset(random.nextInt(range) - range / 2, 0, random.nextInt(range) - range / 2);
            while (world.getFluidState(seafloor).is(FluidTags.WATER) && seafloor.getY() > 1) {
                seafloor = seafloor.below();
            }
            BlockState state = world.getBlockState(seafloor);
            if (state.getBlock() == Blocks.CLAY) {
                return seafloor;
            }
        }
        return null;
    }

    private BlockPos genDigPos() {
        final RandomSource random = this.platypus.getRandom();
        int range = 15;
        if (platypus.isInWater()) {
            return genSeafloorPos(this.platypus.blockPosition());
        } else {
            for (int i = 0; i < 15; i++) {
                BlockPos blockpos1 = this.platypus.blockPosition().offset(random.nextInt(range) - range / 2, 3, random.nextInt(range) - range / 2);
                while (this.platypus.level().isEmptyBlock(blockpos1) && blockpos1.getY() > 1) {
                    blockpos1 = blockpos1.below();
                }
                if (this.platypus.level().getFluidState(blockpos1).is(FluidTags.WATER)) {
                    BlockPos pos3 = genSeafloorPos(blockpos1);
                    if (pos3 != null) {
                        return pos3;
                    }
                }
            }
        }
        return null;
    }
}
