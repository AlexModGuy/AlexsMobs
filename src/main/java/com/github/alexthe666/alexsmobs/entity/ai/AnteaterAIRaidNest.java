package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockLeafcutterAntChamber;
import com.github.alexthe666.alexsmobs.block.BlockLeafcutterAnthill;
import com.github.alexthe666.alexsmobs.entity.EntityAnteater;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.entity.EntityPlatypus;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;

public class AnteaterAIRaidNest extends MoveToBlockGoal {

    public static final ResourceLocation ANTEATER_REWARD = new ResourceLocation("alexsmobs", "gameplay/anteater_reward");
    private EntityAnteater anteater;
    private int idleAtHiveTime = 0;
    private boolean isAboveDestinationAnteater;
    private boolean shootTongue;
    private int maxEatingTime = 0;

    public AnteaterAIRaidNest(EntityAnteater anteater) {
        super(anteater, 1D, 32, 8);
        this.anteater = anteater;
    }

    private static List<ItemStack> getItemStacks(EntityAnteater anteater) {
        LootTable loottable = anteater.level.getServer().getLootTables().get(ANTEATER_REWARD);
        return loottable.getRandomItems((new LootContext.Builder((ServerLevel) anteater.level)).withParameter(LootContextParams.THIS_ENTITY, anteater).withRandom(anteater.level.random).create(LootContextParamSets.PIGLIN_BARTER));
    }

    private void dropDigItems(){
        List<ItemStack> lootList = getItemStacks(anteater);
        if (lootList.size() > 0) {
            for (ItemStack stack : lootList) {
                ItemEntity e = this.anteater.spawnAtLocation(stack.copy());
                e.hasImpulse = true;
                e.setDeltaMovement(e.getDeltaMovement().multiply(0.2, 0.2, 0.2));
            }
        }
    }
    public boolean canUse() {
        return !anteater.isBaby() && super.canUse() && anteater.eatAntCooldown <= 0;
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() && anteater.eatAntCooldown <= 0;
    }

    public void start() {
        super.start();
        maxEatingTime = 150 + anteater.getRandom().nextInt(200);
    }

    public void stop() {
        super.stop();
        idleAtHiveTime = 0;
        maxEatingTime = 150 + anteater.getRandom().nextInt(200);
        anteater.setLeaning(false);
        anteater.resetAntCooldown();
    }

    public double acceptedDistance() {
        return 1.2D;
    }

    public void tick() {
        super.tick();
        BlockPos blockpos = this.getMoveToTarget();
        if (!isWithinXZDist(blockpos, this.mob.position(), this.acceptedDistance())) {
            this.isAboveDestinationAnteater = false;
            ++this.tryTicks;
            if (this.shouldRecalculatePath()) {
                this.mob.getNavigation().moveTo((double) ((float) blockpos.getX()) + 0.5D, blockpos.getY(), (double) ((float) blockpos.getZ()) + 0.5D, this.speedModifier);
            }
        } else {
            this.isAboveDestinationAnteater = true;
            --this.tryTicks;
        }

        if (this.isReachedTarget()) {
            anteater.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(blockPos.getX() + 0.5D, blockPos.getY() - 1, blockPos.getZ() + 0.5));
            if (this.idleAtHiveTime >= 20 && this.idleAtHiveTime % 20 == 0) {
                shootTongue = anteater.getRandom().nextInt(2) == 0;
                if(shootTongue){
                    this.eatHive();
                }else{
                    this.breakHiveEffect();
                }
            }
            ++this.idleAtHiveTime;
            if (shootTongue && anteater.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                anteater.setLeaning(false);
                anteater.setAnimation(EntityAnteater.ANIMATION_TOUNGE_IDLE);
            }else if (anteater.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                anteater.setLeaning(true);
                anteater.setAnimation(anteater.getRandom().nextBoolean() ? EntityAnteater.ANIMATION_SLASH_L : EntityAnteater.ANIMATION_SLASH_R);
            }
            if(this.idleAtHiveTime > maxEatingTime){
                stop();
            }
        }

    }

    private boolean isWithinXZDist(BlockPos blockpos, Vec3 positionVec, double distance) {
        return blockpos.distSqr(new BlockPos(positionVec.x(), blockpos.getY(), positionVec.z())) < distance * distance;
    }

    protected boolean isReachedTarget() {
        return this.isAboveDestinationAnteater;
    }

    private void breakHiveEffect(){
        if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(anteater.level, anteater)) {
            BlockState blockstate = anteater.level.getBlockState(this.blockPos);
            if (blockstate.is(AMBlockRegistry.LEAFCUTTER_ANTHILL.get())) {
                if (anteater.level.getBlockEntity(this.blockPos) instanceof TileEntityLeafcutterAnthill) {
                    TileEntityLeafcutterAnthill anthill = (TileEntityLeafcutterAnthill) anteater.level.getBlockEntity(this.blockPos);
                    anthill.angerAntsBecauseAnteater(anteater, blockstate, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
                    anteater.level.destroyBlock(blockPos, false);
                    if (blockstate.getBlock() instanceof BlockLeafcutterAnthill) {
                        anteater.level.setBlockAndUpdate(blockPos, blockstate);
                    }
                    dropDigItems();
                }
            }else if(blockstate.is(AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get())){
                anteater.level.destroyBlock(blockPos, false);
                anteater.level.setBlockAndUpdate(blockPos, blockstate);
            }
        }
    }

    private void eatHive() {
        if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(anteater.level, anteater)) {
            BlockState blockstate = anteater.level.getBlockState(this.blockPos);
            if (blockstate.is(AMBlockRegistry.LEAFCUTTER_ANTHILL.get())) {
                if (anteater.level.getBlockEntity(this.blockPos) instanceof TileEntityLeafcutterAnthill) {
                    Random rand = new Random();
                    TileEntityLeafcutterAnthill anthill = (TileEntityLeafcutterAnthill) anteater.level.getBlockEntity(this.blockPos);
                    anthill.angerAntsBecauseAnteater(anteater, blockstate, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
                    anteater.level.updateNeighbourForOutputSignal(this.blockPos, blockstate.getBlock());
                    if(!anthill.hasNoAnts()){
                        BlockState state = anthill.shrinkFungus();
                        if(state != null && state.is(AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get()) && state.getValue(BlockLeafcutterAntChamber.FUNGUS) >= 5){
                            ItemStack stack = new ItemStack(AMItemRegistry.GONGYLIDIA.get());
                            ItemEntity itementity = new ItemEntity(anteater.level, blockPos.getX() + rand.nextFloat(), blockPos.getY() + rand.nextFloat(), blockPos.getZ() + rand.nextFloat(), stack);
                            itementity.setDefaultPickUpDelay();
                            anteater.level.addFreshEntity(itementity);
                        }
                        anteater.setAntOnTongue(true);
                    }
                }
            }else if(blockstate.is(AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get())){
                anteater.level.destroyBlock(blockPos, false);
                if(blockstate.getValue(BlockLeafcutterAntChamber.FUNGUS) >= 5){
                    Random rand = new Random();
                    ItemStack stack = new ItemStack(AMItemRegistry.GONGYLIDIA.get());
                    ItemEntity itementity = new ItemEntity(anteater.level, blockPos.getX() + rand.nextFloat(), blockPos.getY() + rand.nextFloat(), blockPos.getZ() + rand.nextFloat(), stack);
                    itementity.setDefaultPickUpDelay();
                    anteater.level.addFreshEntity(itementity);
                }
                anteater.level.setBlockAndUpdate(blockPos, Blocks.COARSE_DIRT.defaultBlockState());
                anteater.setAntOnTongue(true);
            }
            double d0 = 15;
            for (EntityLeafcutterAnt leafcutter : anteater.level.getEntitiesOfClass(EntityLeafcutterAnt.class, new AABB((double) blockPos.getX() - d0, (double) blockPos.getY() - d0, (double) blockPos.getZ() - d0, (double) blockPos.getX() + d0, (double) blockPos.getY() + d0, (double) blockPos.getZ() + d0))) {
                leafcutter.setRemainingPersistentAngerTime(100);
                leafcutter.setTarget(anteater);
                leafcutter.setStayOutOfHiveCountdown(400);
            }
        }
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).is(AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get()) || worldIn.getBlockState(pos).is(AMBlockRegistry.LEAFCUTTER_ANTHILL.get()) && worldIn.getBlockEntity(pos) instanceof TileEntityLeafcutterAnthill && this.isValidAnthill(pos, (TileEntityLeafcutterAnthill)worldIn.getBlockEntity(pos));
    }

    private boolean isValidAnthill(BlockPos pos, TileEntityLeafcutterAnthill blockEntity) {
        return blockEntity.hasAtleastThisManyAnts(2);
    }
}
