package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntitySeal;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Random;

public class SealAIDiveForItems extends Goal {

    private EntitySeal seal;
    private PlayerEntity thrower;
    private BlockPos digPos;
    private boolean returnToPlayer = false;
    private int digTime = 0;
    public static final ResourceLocation SEAL_REWARD = new ResourceLocation("alexsmobs","gameplay/seal_reward");

    public SealAIDiveForItems(EntitySeal seal) {
        this.seal = seal;
    }

    private static List<ItemStack> getItemStacks(EntitySeal seal) {
        LootTable loottable = seal.world.getServer().getLootTableManager().getLootTableFromLocation(SEAL_REWARD);
        return loottable.generate((new LootContext.Builder((ServerWorld) seal.world)).withParameter(LootParameters.THIS_ENTITY, seal).withRandom(seal.world.rand).build(LootParameterSets.BARTER));
    }

    @Override
    public boolean shouldExecute() {
        if (seal.feederUUID == null || seal.world.getPlayerByUuid(seal.feederUUID) == null || seal.revengeCooldown > 0) {
            return false;
        }
        thrower = seal.world.getPlayerByUuid(seal.feederUUID);
        digPos = genDigPos();
        return thrower != null && digPos != null;
    }

    public boolean shouldContinueExecuting() {
        return seal.getAttackTarget() == null && seal.revengeCooldown == 0 && seal.getRevengeTarget() == null && thrower != null && seal.feederUUID != null && digPos != null && seal.world.getFluidState(digPos.up()).isTagged(FluidTags.WATER);
    }

    public void tick() {
        seal.setBasking(false);
        if (returnToPlayer) {
            seal.getNavigator().tryMoveToEntityLiving(thrower, 1D);
            if (seal.getDistance(thrower) < 2D) {
                ItemStack stack = seal.getHeldItemMainhand().copy();
                seal.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                ItemEntity item = seal.entityDropItem(stack);
                if (item != null) {
                    double d0 = thrower.getPosX() - this.seal.getPosX();
                    double d1 = thrower.getPosYEye() - this.seal.getPosYEye();
                    double d2 = thrower.getPosZ() - this.seal.getPosZ();
                    double lvt_7_1_ = MathHelper.sqrt(d0 * d0 + d2 * d2);
                    float pitch = (float) (-(MathHelper.atan2(d1, lvt_7_1_) * 57.2957763671875D));
                    float yaw = (float) (MathHelper.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                    float f8 = MathHelper.sin(pitch * ((float) Math.PI / 180F));
                    float f2 = MathHelper.cos(pitch * ((float) Math.PI / 180F));
                    float f3 = MathHelper.sin(yaw * ((float) Math.PI / 180F));
                    float f4 = MathHelper.cos(yaw * ((float) Math.PI / 180F));
                    float f5 = seal.getRNG().nextFloat() * ((float) Math.PI * 2F);
                    float f6 = 0.02F * seal.getRNG().nextFloat();
                    item.setMotion((double) (-f3 * f2 * 0.5F) + Math.cos(f5) * (double) f6, -f8 * 0.2F + 0.1F + (seal.getRNG().nextFloat() - seal.getRNG().nextFloat()) * 0.1F, (double) (f4 * f2 * 0.5F) + Math.sin(f5) * (double) f6);
                }
                seal.feederUUID = null;
                resetTask();
            }
        } else {
            double dist = seal.getDistanceSq(Vector3d.copyCentered(digPos.up()));
            double d0 = digPos.getX() + 0.5 - this.seal.getPosX();
            double d1 = digPos.getY() + 0.5 - this.seal.getPosYEye();
            double d2 = digPos.getZ() + 0.5 - this.seal.getPosZ();
            float f = (float)(MathHelper.atan2(d2, d0) * 57.2957763671875D) - 90.0F;

            if (dist < 2) {
                seal.getNavigator().clearPath();
                digTime++;
                if(digTime  % 5 == 0){
                    SoundEvent sound = seal.world.getBlockState(digPos).getSoundType().getHitSound();
                    seal.playSound(sound, 1, 0.5F + seal.getRNG().nextFloat() * 0.5F);
                }
                if (digTime >= 100) {
                    List<ItemStack> lootList = getItemStacks(seal);
                    if (lootList.size() > 0) {
                        ItemStack copy = lootList.remove(0);
                        copy = copy.copy();
                        this.seal.setHeldItem(Hand.MAIN_HAND, copy);
                        for (ItemStack stack : lootList) {
                            this.seal.entityDropItem(stack.copy());
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
                seal.getNavigator().tryMoveToXYZ(digPos.getX(), digPos.getY(), digPos.getZ(), 1);
                seal.rotationYaw = f;
            }
        }
    }

    public void resetTask() {
        seal.setDigging(false);
        digPos = null;
        thrower = null;
        digTime = 0;
        returnToPlayer = false;
        seal.fishFeedings = 0;
        if(!seal.getHeldItemMainhand().isEmpty()){
            seal.entityDropItem(seal.getHeldItemMainhand().copy());
            seal.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
        }
    }

    private BlockPos genSeafloorPos(BlockPos parent) {
        IWorld world = seal.world;
        Random random = new Random();
        int range = 15;
        for (int i = 0; i < 15; i++) {
            BlockPos seafloor = parent.add(random.nextInt(range) - range / 2, 0, random.nextInt(range) - range / 2);
            while (world.getFluidState(seafloor).isTagged(FluidTags.WATER) && seafloor.getY() > 1) {
                seafloor = seafloor.down();
            }
            BlockState state = world.getBlockState(seafloor);
            if (BlockTags.getCollection().get(AMTagRegistry.SEAL_DIGABLES).contains(state.getBlock())) {
                return seafloor;
            }
        }
        return null;
    }

    private BlockPos genDigPos() {
        Random random = new Random();
        int range = 15;
        if (seal.isInWater()) {
            return genSeafloorPos(this.seal.getPosition());
        } else {
            for (int i = 0; i < 15; i++) {
                BlockPos blockpos1 = this.seal.getPosition().add(random.nextInt(range) - range / 2, 3, random.nextInt(range) - range / 2);
                while (this.seal.world.isAirBlock(blockpos1) && blockpos1.getY() > 1) {
                    blockpos1 = blockpos1.down();
                }
                if (this.seal.world.getFluidState(blockpos1).isTagged(FluidTags.WATER)) {
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
