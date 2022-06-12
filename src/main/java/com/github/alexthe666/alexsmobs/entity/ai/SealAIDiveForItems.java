package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntitySeal;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;

import java.util.List;
import java.util.Random;

public class SealAIDiveForItems extends Goal {

    private EntitySeal seal;
    private Player thrower;
    private BlockPos digPos;
    private boolean returnToPlayer = false;
    private int digTime = 0;
    public static final ResourceLocation SEAL_REWARD = new ResourceLocation("alexsmobs","gameplay/seal_reward");

    public SealAIDiveForItems(EntitySeal seal) {
        this.seal = seal;
    }

    private static List<ItemStack> getItemStacks(EntitySeal seal) {
        LootTable loottable = seal.level.getServer().getLootTables().get(SEAL_REWARD);
        return loottable.getRandomItems((new LootContext.Builder((ServerLevel) seal.level)).withParameter(LootContextParams.ORIGIN, seal.position()).withParameter(LootContextParams.THIS_ENTITY, seal).withRandom(seal.level.random).create(LootContextParamSets.PIGLIN_BARTER));
    }

    @Override
    public boolean canUse() {
        if (seal.feederUUID == null || seal.level.getPlayerByUUID(seal.feederUUID) == null || seal.revengeCooldown > 0) {
            return false;
        }
        thrower = seal.level.getPlayerByUUID(seal.feederUUID);
        digPos = genDigPos();
        return thrower != null && digPos != null;
    }

    public boolean canContinueToUse() {
        return seal.getTarget() == null && seal.revengeCooldown == 0 && seal.getLastHurtByMob() == null && thrower != null && seal.feederUUID != null && digPos != null && seal.level.getFluidState(digPos.above()).is(FluidTags.WATER);
    }

    public void tick() {
        seal.setBasking(false);
        if (returnToPlayer) {
            seal.getNavigation().moveTo(thrower, 1D);
            if (seal.distanceTo(thrower) < 2D) {
                ItemStack stack = seal.getMainHandItem().copy();
                seal.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                ItemEntity item = seal.spawnAtLocation(stack);
                if (item != null) {
                    double d0 = thrower.getX() - this.seal.getX();
                    double d1 = thrower.getEyeY() - this.seal.getEyeY();
                    double d2 = thrower.getZ() - this.seal.getZ();
                    double lvt_7_1_ = Mth.sqrt((float) (d0 * d0 + d2 * d2));
                    float pitch = (float) (-(Mth.atan2(d1, lvt_7_1_) * 57.2957763671875D));
                    float yaw = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                    float f8 = Mth.sin(pitch * ((float) Math.PI / 180F));
                    float f2 = Mth.cos(pitch * ((float) Math.PI / 180F));
                    float f3 = Mth.sin(yaw * ((float) Math.PI / 180F));
                    float f4 = Mth.cos(yaw * ((float) Math.PI / 180F));
                    float f5 = seal.getRandom().nextFloat() * ((float) Math.PI * 2F);
                    float f6 = 0.02F * seal.getRandom().nextFloat();
                    item.setDeltaMovement((double) (-f3 * f2 * 0.5F) + Math.cos(f5) * (double) f6, -f8 * 0.2F + 0.1F + (seal.getRandom().nextFloat() - seal.getRandom().nextFloat()) * 0.1F, (double) (f4 * f2 * 0.5F) + Math.sin(f5) * (double) f6);
                }
                seal.feederUUID = null;
                stop();
            }
        } else {
            double dist = seal.distanceToSqr(Vec3.atCenterOf(digPos.above()));
            double d0 = digPos.getX() + 0.5 - this.seal.getX();
            double d1 = digPos.getY() + 0.5 - this.seal.getEyeY();
            double d2 = digPos.getZ() + 0.5 - this.seal.getZ();
            float f = (float)(Mth.atan2(d2, d0) * 57.2957763671875D) - 90.0F;

            if (dist < 2) {
                seal.getNavigation().stop();
                digTime++;
                if(digTime  % 5 == 0){
                    SoundEvent sound = seal.level.getBlockState(digPos).getSoundType().getHitSound();
                    seal.playSound(sound, 1, 0.5F + seal.getRandom().nextFloat() * 0.5F);
                }
                if (digTime >= 100) {
                    List<ItemStack> lootList = getItemStacks(seal);
                    if (lootList.size() > 0) {
                        ItemStack copy = lootList.remove(0);
                        copy = copy.copy();
                        this.seal.setItemInHand(InteractionHand.MAIN_HAND, copy);
                        for (ItemStack stack : lootList) {
                            this.seal.spawnAtLocation(stack.copy());
                        }
                        this.returnToPlayer = true;
                    }
                    seal.setDigging(false);
                    digTime = 0;
                }else{
                    seal.setDigging(true);
                }
            }else{
                seal.setDigging(false);
                seal.getNavigation().moveTo(digPos.getX(), digPos.getY(), digPos.getZ(), 1);
                seal.setYRot(f);
            }
        }
    }

    public void stop() {
        seal.setDigging(false);
        digPos = null;
        thrower = null;
        digTime = 0;
        returnToPlayer = false;
        seal.fishFeedings = 0;
        if(!seal.getMainHandItem().isEmpty()){
            seal.spawnAtLocation(seal.getMainHandItem().copy());
            seal.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
    }

    private BlockPos genSeafloorPos(BlockPos parent) {
        LevelAccessor world = seal.level;
        RandomSource random = new Random();
        int range = 15;
        for (int i = 0; i < 15; i++) {
            BlockPos seafloor = parent.offset(random.nextInt(range) - range / 2, 0, random.nextInt(range) - range / 2);
            while (world.getFluidState(seafloor).is(FluidTags.WATER) && seafloor.getY() > 1) {
                seafloor = seafloor.below();
            }
            BlockState state = world.getBlockState(seafloor);
            if (state.is(AMTagRegistry.SEAL_DIGABLES)) {
                return seafloor;
            }
        }
        return null;
    }

    private BlockPos genDigPos() {
        RandomSource random = new Random();
        int range = 15;
        if (seal.isInWater()) {
            return genSeafloorPos(this.seal.blockPosition());
        } else {
            for (int i = 0; i < 15; i++) {
                BlockPos blockpos1 = this.seal.blockPosition().offset(random.nextInt(range) - range / 2, 3, random.nextInt(range) - range / 2);
                while (this.seal.level.isEmptyBlock(blockpos1) && blockpos1.getY() > 1) {
                    blockpos1 = blockpos1.below();
                }
                if (this.seal.level.getFluidState(blockpos1).is(FluidTags.WATER)) {
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
