package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockLeafcutterAntChamber;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.google.common.collect.Lists;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class TileEntityLeafcutterAnthill extends BlockEntity {
    private static final Direction[] DIRECTIONS_UP = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    private final List<TileEntityLeafcutterAnthill.Ant> ants = Lists.newArrayList();
    private int leafFeedings = 0;

    public TileEntityLeafcutterAnthill(BlockPos pos, BlockState state) {
        super(AMTileEntityRegistry.LEAFCUTTER_ANTHILL, pos, state);
    }

    public boolean hasNoAnts() {
        return this.ants.isEmpty();
    }

    public boolean isFullOfAnts() {
        return this.ants.size() == AMConfig.leafcutterAntColonySize;
    }

    public void angerAnts(@Nullable LivingEntity p_226963_1_, BlockState p_226963_2_, BeehiveBlockEntity.BeeReleaseStatus p_226963_3_) {
        List<Entity> list = this.tryReleaseAnt(p_226963_2_, p_226963_3_);
        if (p_226963_1_ != null) {
            for (Entity entity : list) {
                if (entity instanceof EntityLeafcutterAnt) {
                    EntityLeafcutterAnt entityLeafcutterAnt = (EntityLeafcutterAnt) entity;
                    if (p_226963_1_.position().distanceToSqr(entity.position()) <= 16.0D) {
                        entityLeafcutterAnt.setTarget(p_226963_1_);
                    }
                    entityLeafcutterAnt.setStayOutOfHiveCountdown(400);
                }
            }
        }

    }

    private List<Entity> tryReleaseAnt(BlockState p_226965_1_, BeehiveBlockEntity.BeeReleaseStatus p_226965_2_) {
        List<Entity> list = Lists.newArrayList();
        this.ants.removeIf((p_226966_4_) -> {
            return this.addAntToWorld(p_226965_1_, p_226966_4_, list, p_226965_2_);
        });
        return list;
    }

    private boolean addAntToWorld(BlockState p_235651_1_, Ant p_235651_2_, @Nullable List<Entity> p_235651_3_, BeehiveBlockEntity.BeeReleaseStatus p_235651_4_) {
        BlockPos blockpos = this.getBlockPos();
        CompoundTag compoundnbt = p_235651_2_.entityData;
        compoundnbt.remove("Passengers");
        compoundnbt.remove("Leash");
        compoundnbt.remove("UUID");
        BlockPos blockpos1 = blockpos.above();
        boolean flag = !this.level.getBlockState(blockpos1).getBlockSupportShape(this.level, blockpos1).isEmpty();
        if (flag && p_235651_4_ != BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY) {
            return false;
        } else {
            Entity entity = loadEntityAndExecute(compoundnbt, this.level, (p_226960_0_) -> {
                return p_226960_0_;
            });
            if (entity != null) {

                if (entity instanceof EntityLeafcutterAnt) {
                    EntityLeafcutterAnt entityLeafcutterAnt = (EntityLeafcutterAnt) entity;
                    entityLeafcutterAnt.setLeaf(false);
                    if (p_235651_4_ == BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED) {

                    }
                    if (p_235651_3_ != null) {
                        p_235651_3_.add(entityLeafcutterAnt);
                    }

                    float f = entity.getBbWidth();
                    double d0 = (double) blockpos.getX() + 0.5D;
                    double d1 = (double) blockpos.getY() + 1.0D;
                    double d2 = (double) blockpos.getZ() + 0.5D;
                    entity.moveTo(d0, d1, d2, entity.getYRot(), entity.getXRot());
                    if(((EntityLeafcutterAnt) entity).isQueen()){
                        entityLeafcutterAnt.setStayOutOfHiveCountdown(400);
                    }
                }

                this.level.playSound(null, blockpos, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                return this.level.addFreshEntity(entity);

            } else {
                return false;
            }
        }

    }

    public void tryEnterHive(EntityLeafcutterAnt p_226962_1_, boolean p_226962_2_, int p_226962_3_) {
        if (this.ants.size() < AMConfig.leafcutterAntColonySize) {
            p_226962_1_.stopRiding();
            p_226962_1_.ejectPassengers();
            CompoundTag compoundnbt = new CompoundTag();
            p_226962_1_.save(compoundnbt);
            if (p_226962_2_) {
                if(!level.isClientSide && p_226962_1_.getRandom().nextFloat() < AMConfig.leafcutterAntFungusGrowChance){
                    growFungus();
                }
                leafFeedings++;
                if(leafFeedings >= AMConfig.leafcutterAntRepopulateFeedings && this.ants.size() < Mth.ceil(AMConfig.leafcutterAntColonySize * 0.5F) && hasQueen()){
                    leafFeedings = 0;
                    this.ants.add(new Ant(new CompoundTag(), 0, 100, false));
                }
            }
            this.ants.add(new Ant(compoundnbt, p_226962_3_, p_226962_2_ ? 100 : 200, p_226962_1_.isQueen()));
            if (this.level != null) {

                BlockPos blockpos = this.getBlockPos();
                this.level.playSound(null, blockpos.getX(), blockpos.getY(), blockpos.getZ(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            p_226962_1_.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    public boolean hasQueen(){
        for(Ant ant : ants){
            if(ant.queen){
                return true;
            }
        }
        return false;
    }

    public void releaseQueens(){
        this.ants.removeIf((p_226966_4_) -> {
            return p_226966_4_.queen && this.addAntToWorld(getBlockState(), p_226966_4_, null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
        });
    }
    public void tryEnterHive(EntityLeafcutterAnt p_226961_1_, boolean p_226961_2_) {
        this.tryEnterHive(p_226961_1_, p_226961_2_, 0);
    }

    public int getAntCount() {
        return this.ants.size();
    }


    public void setChanged() {
        if (this.isNearFire()) {
            this.angerAnts(null, this.level.getBlockState(this.getBlockPos()), BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
        }

        super.setChanged();
    }

    public boolean isNearFire() {
        if (this.level == null) {
            return false;
        } else {
            for (BlockPos blockpos : BlockPos.betweenClosed(this.worldPosition.offset(-1, -1, -1), this.worldPosition.offset(1, 1, 1))) {
                if (this.level.getBlockState(blockpos).getBlock() instanceof FireBlock) {
                    return true;
                }
            }

            return false;
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityLeafcutterAnthill entity) {
        entity.tickAnts();
    }

    private void growFungus() {
        BlockPos bottomChamber = this.getBlockPos().below();
        while (level.getBlockState(bottomChamber.below()).getBlock() == AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER && bottomChamber.getY() > 0) {
            bottomChamber = bottomChamber.below();
        }
        BlockPos chamber = bottomChamber;
        if (isUnfilledChamber(chamber)) {
            int fungalLevel = level.getBlockState(chamber).getValue(BlockLeafcutterAntChamber.FUNGUS);
            int fungalLevel2 = Mth.clamp(fungalLevel + 1 + level.getRandom().nextInt(1), 0, 5);
            level.setBlockAndUpdate(chamber, AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.defaultBlockState().setValue(BlockLeafcutterAntChamber.FUNGUS, fungalLevel2));
        } else {
            boolean flag = false;
            List<BlockPos> possibleChambers = new ArrayList<>();
            while (!flag) {
                for (BlockPos blockpos : BlockPos.betweenClosed(chamber.offset(-4, 0, -4), chamber.offset(4, 0, 4))) {
                    if (isUnfilledChamber(blockpos)) {
                        possibleChambers.add(blockpos.immutable());
                        flag = true;
                    }
                }
                if (!flag) {
                    chamber = chamber.above();
                    if (chamber.getY() > this.worldPosition.getY()) {
                        return;
                    }
                }
            }
            Collections.shuffle(possibleChambers);
            if (!possibleChambers.isEmpty()) {
                BlockPos newChamber = possibleChambers.get(0);
                if (newChamber != null && isUnfilledChamber(newChamber)) {
                    int fungalLevel = level.getBlockState(newChamber).getValue(BlockLeafcutterAntChamber.FUNGUS);
                    int fungalLevel2 = Mth.clamp(fungalLevel + 1 + level.getRandom().nextInt(1), 0, 5);
                    level.setBlockAndUpdate(newChamber, AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.defaultBlockState().setValue(BlockLeafcutterAntChamber.FUNGUS, fungalLevel2));
                }
            }
        }
    }

    private boolean isUnfilledChamber(BlockPos pos) {
        return level.getBlockState(pos).getBlock() == AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER && level.getBlockState(pos).getValue(BlockLeafcutterAntChamber.FUNGUS) < 5;
    }

    private void tickAnts() {
        Iterator<Ant> iterator = this.ants.iterator();

        Ant ant;
        for (BlockState blockstate = this.getBlockState(); iterator.hasNext(); ant.ticksInHive++) {
            ant = iterator.next();
            if (ant.ticksInHive > ant.minOccupationTicks && !ant.queen) {
                BeehiveBlockEntity.BeeReleaseStatus beehivetileentity$state = ant.entityData.getBoolean("HasNectar") ? BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED : BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED;
                if (this.addAntToWorld(blockstate, ant, null, beehivetileentity$state)) {
                    iterator.remove();
                }
            }
        }

    }


    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.ants.clear();
        this.leafFeedings = nbt.getInt("LeafFeedings");
        ListTag listnbt = nbt.getList("Ants", 10);

        for (int i = 0; i < listnbt.size(); ++i) {
            CompoundTag compoundnbt = listnbt.getCompound(i);
            Ant beehiveTileEntity$ant = new Ant(compoundnbt.getCompound("EntityData"), compoundnbt.getInt("TicksInHive"), compoundnbt.getInt("MinOccupationTicks"), compoundnbt.getBoolean("Queen"));
            this.ants.add(beehiveTileEntity$ant);
        }
    }

    public ListTag getAnts() {
        ListTag listnbt = new ListTag();

        for (Ant beehiveTileEntity$ant : this.ants) {
            beehiveTileEntity$ant.entityData.remove("UUID");
            CompoundTag compoundnbt = new CompoundTag();
            compoundnbt.put("EntityData", beehiveTileEntity$ant.entityData);
            compoundnbt.putInt("TicksInHive", beehiveTileEntity$ant.ticksInHive);
            compoundnbt.putInt("MinOccupationTicks", beehiveTileEntity$ant.minOccupationTicks);
            listnbt.add(compoundnbt);
        }

        return listnbt;
    }

    public CompoundTag save(CompoundTag compound) {
        super.save(compound);
        compound.put("Ants", this.getAnts());
        compound.putInt("LeafFeedings",leafFeedings);
        return compound;
    }

    static class Ant {
        private final CompoundTag entityData;
        private final int minOccupationTicks;
        private int ticksInHive;
        private boolean queen;

        private Ant(CompoundTag p_i225767_1_, int p_i225767_2_, int p_i225767_3_, boolean queen) {
            p_i225767_1_.remove("UUID");
            this.entityData = p_i225767_1_;
            this.ticksInHive = p_i225767_2_;
            this.minOccupationTicks = p_i225767_3_;
            this.queen = queen;
        }
    }

    /* To avoid ants not being mapped to vanilla, we have to handle this seperately than the default entitytype implementation.*/
    @Nullable
    public static Entity loadEntityAndExecute(CompoundTag compound, Level worldIn, Function<Entity, Entity> p_220335_2_) {
        return loadEntity(compound, worldIn).map(p_220335_2_).map((p_220346_3_) -> {
            if (compound.contains("Passengers", 9)) {
                ListTag listnbt = compound.getList("Passengers", 10);

                for(int i = 0; i < listnbt.size(); ++i) {
                    Entity entity = loadEntityAndExecute(listnbt.getCompound(i), worldIn, p_220335_2_);
                    if (entity != null) {
                        entity.startRiding(p_220346_3_, true);
                    }
                }
            }

            return p_220346_3_;
        }).orElse((Entity)null);
    }

    private static Optional<Entity> loadEntity(CompoundTag compound, Level worldIn) {
        try {
            return loadEntityUnchecked(compound, worldIn);
        } catch (RuntimeException runtimeexception) {
            return Optional.empty();
        }
    }

    public static Optional<Entity> loadEntityUnchecked(CompoundTag compound, Level worldIn) {
        EntityLeafcutterAnt leafcutterAnt = AMEntityRegistry.LEAFCUTTER_ANT.create(worldIn);
        leafcutterAnt.load(compound);
        return Optional.of(leafcutterAnt);
    }

}
