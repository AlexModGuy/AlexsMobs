package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockLeafcutterAntChamber;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class TileEntityLeafcutterAnthill extends TileEntity implements ITickableTileEntity {
    private static final Direction[] DIRECTIONS_UP = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    private final List<TileEntityLeafcutterAnthill.Ant> ants = Lists.newArrayList();
    private int leafFeedings = 0;

    public TileEntityLeafcutterAnthill() {
        super(AMTileEntityRegistry.LEAFCUTTER_ANTHILL);
    }

    public boolean hasNoAnts() {
        return this.ants.isEmpty();
    }

    public boolean isFullOfAnts() {
        return this.ants.size() == AMConfig.leafcutterAntColonySize;
    }

    public void angerAnts(@Nullable LivingEntity p_226963_1_, BlockState p_226963_2_, BeehiveTileEntity.State p_226963_3_) {
        List<Entity> list = this.tryReleaseAnt(p_226963_2_, p_226963_3_);
        if (p_226963_1_ != null) {
            for (Entity entity : list) {
                if (entity instanceof EntityLeafcutterAnt) {
                    EntityLeafcutterAnt entityLeafcutterAnt = (EntityLeafcutterAnt) entity;
                    if (p_226963_1_.getPositionVec().squareDistanceTo(entity.getPositionVec()) <= 16.0D) {
                        entityLeafcutterAnt.setAttackTarget(p_226963_1_);
                    }
                    entityLeafcutterAnt.setStayOutOfHiveCountdown(400);
                }
            }
        }

    }

    private List<Entity> tryReleaseAnt(BlockState p_226965_1_, BeehiveTileEntity.State p_226965_2_) {
        List<Entity> list = Lists.newArrayList();
        this.ants.removeIf((p_226966_4_) -> {
            return this.addAntToWorld(p_226965_1_, p_226966_4_, list, p_226965_2_);
        });
        return list;
    }

    private boolean addAntToWorld(BlockState p_235651_1_, Ant p_235651_2_, @Nullable List<Entity> p_235651_3_, BeehiveTileEntity.State p_235651_4_) {
        BlockPos blockpos = this.getPos();
        CompoundNBT compoundnbt = p_235651_2_.entityData;
        compoundnbt.remove("Passengers");
        compoundnbt.remove("Leash");
        compoundnbt.remove("UUID");
        BlockPos blockpos1 = blockpos.up();
        boolean flag = !this.world.getBlockState(blockpos1).getCollisionShape(this.world, blockpos1).isEmpty();
        if (flag && p_235651_4_ != BeehiveTileEntity.State.EMERGENCY) {
            return false;
        } else {
            Entity entity = loadEntityAndExecute(compoundnbt, this.world, (p_226960_0_) -> {
                return p_226960_0_;
            });
            if (entity != null) {

                if (entity instanceof EntityLeafcutterAnt) {
                    EntityLeafcutterAnt entityLeafcutterAnt = (EntityLeafcutterAnt) entity;
                    entityLeafcutterAnt.setLeaf(false);
                    if (p_235651_4_ == BeehiveTileEntity.State.HONEY_DELIVERED) {

                    }
                    if (p_235651_3_ != null) {
                        p_235651_3_.add(entityLeafcutterAnt);
                    }

                    float f = entity.getWidth();
                    double d0 = (double) blockpos.getX() + 0.5D;
                    double d1 = (double) blockpos.getY() + 1.0D;
                    double d2 = (double) blockpos.getZ() + 0.5D;
                    entity.setLocationAndAngles(d0, d1, d2, entity.rotationYaw, entity.rotationPitch);
                    if(((EntityLeafcutterAnt) entity).isQueen()){
                        entityLeafcutterAnt.setStayOutOfHiveCountdown(400);
                    }
                }

                this.world.playSound(null, blockpos, SoundEvents.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return this.world.addEntity(entity);

            } else {
                return false;
            }
        }

    }



    public void tryEnterHive(EntityLeafcutterAnt p_226962_1_, boolean p_226962_2_, int p_226962_3_) {
        if (this.ants.size() < AMConfig.leafcutterAntColonySize) {
            p_226962_1_.stopRiding();
            p_226962_1_.removePassengers();
            CompoundNBT compoundnbt = new CompoundNBT();
            p_226962_1_.writeUnlessPassenger(compoundnbt);
            if (p_226962_2_) {
                if(!world.isRemote && p_226962_1_.getRNG().nextFloat() < AMConfig.leafcutterAntFungusGrowChance){
                    growFungus();
                }
                leafFeedings++;
                if(leafFeedings >= AMConfig.leafcutterAntRepopulateFeedings && this.ants.size() < MathHelper.ceil(AMConfig.leafcutterAntColonySize * 0.5F) && hasQueen()){
                    leafFeedings = 0;
                    this.ants.add(new Ant(new CompoundNBT(), 0, 100, false));
                }
            }
            this.ants.add(new Ant(compoundnbt, p_226962_3_, p_226962_2_ ? 100 : 200, p_226962_1_.isQueen()));
            if (this.world != null) {

                BlockPos blockpos = this.getPos();
                this.world.playSound(null, blockpos.getX(), blockpos.getY(), blockpos.getZ(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            p_226962_1_.remove();
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
            return p_226966_4_.queen && this.addAntToWorld(getBlockState(), p_226966_4_, null, BeehiveTileEntity.State.BEE_RELEASED);
        });
    }
    public void tryEnterHive(EntityLeafcutterAnt p_226961_1_, boolean p_226961_2_) {
        this.tryEnterHive(p_226961_1_, p_226961_2_, 0);
    }

    public int getAntCount() {
        return this.ants.size();
    }


    public void markDirty() {
        if (this.isNearFire()) {
            this.angerAnts(null, this.world.getBlockState(this.getPos()), BeehiveTileEntity.State.EMERGENCY);
        }

        super.markDirty();
    }

    public boolean isNearFire() {
        if (this.world == null) {
            return false;
        } else {
            for (BlockPos blockpos : BlockPos.getAllInBoxMutable(this.pos.add(-1, -1, -1), this.pos.add(1, 1, 1))) {
                if (this.world.getBlockState(blockpos).getBlock() instanceof FireBlock) {
                    return true;
                }
            }

            return false;
        }
    }

    public void tick() {
        if (!world.isRemote) {
            tickAnts();
        }
    }

    private void growFungus() {
        BlockPos bottomChamber = this.getPos().down();
        while (world.getBlockState(bottomChamber.down()).getBlock() == AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER && bottomChamber.getY() > 0) {
            bottomChamber = bottomChamber.down();
        }
        BlockPos chamber = bottomChamber;
        if (isUnfilledChamber(chamber)) {
            int fungalLevel = world.getBlockState(chamber).get(BlockLeafcutterAntChamber.FUNGUS);
            int fungalLevel2 = MathHelper.clamp(fungalLevel + 1 + world.getRandom().nextInt(1), 0, 5);
            world.setBlockState(chamber, AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.getDefaultState().with(BlockLeafcutterAntChamber.FUNGUS, fungalLevel2));
        } else {
            boolean flag = false;
            List<BlockPos> possibleChambers = new ArrayList<>();
            while (!flag) {
                for (BlockPos blockpos : BlockPos.getAllInBoxMutable(chamber.add(-4, 0, -4), chamber.add(4, 0, 4))) {
                    if (isUnfilledChamber(blockpos)) {
                        possibleChambers.add(blockpos.toImmutable());
                        flag = true;
                    }
                }
                if (!flag) {
                    chamber = chamber.up();
                    if (chamber.getY() > this.pos.getY()) {
                        return;
                    }
                }
            }
            Collections.shuffle(possibleChambers);
            if (!possibleChambers.isEmpty()) {
                BlockPos newChamber = possibleChambers.get(0);
                if (newChamber != null && isUnfilledChamber(newChamber)) {
                    int fungalLevel = world.getBlockState(newChamber).get(BlockLeafcutterAntChamber.FUNGUS);
                    int fungalLevel2 = MathHelper.clamp(fungalLevel + 1 + world.getRandom().nextInt(1), 0, 5);
                    world.setBlockState(newChamber, AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.getDefaultState().with(BlockLeafcutterAntChamber.FUNGUS, fungalLevel2));
                }
            }
        }
    }

    private boolean isUnfilledChamber(BlockPos pos) {
        return world.getBlockState(pos).getBlock() == AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER && world.getBlockState(pos).get(BlockLeafcutterAntChamber.FUNGUS) < 5;
    }

    private void tickAnts() {
        Iterator<Ant> iterator = this.ants.iterator();

        Ant ant;
        for (BlockState blockstate = this.getBlockState(); iterator.hasNext(); ant.ticksInHive++) {
            ant = iterator.next();
            if (ant.ticksInHive > ant.minOccupationTicks && !ant.queen) {
                BeehiveTileEntity.State beehivetileentity$state = ant.entityData.getBoolean("HasNectar") ? BeehiveTileEntity.State.HONEY_DELIVERED : BeehiveTileEntity.State.BEE_RELEASED;
                if (this.addAntToWorld(blockstate, ant, null, beehivetileentity$state)) {
                    iterator.remove();
                }
            }
        }

    }


    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        this.ants.clear();
        this.leafFeedings = nbt.getInt("LeafFeedings");
        ListNBT listnbt = nbt.getList("Ants", 10);

        for (int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            Ant beehiveTileEntity$ant = new Ant(compoundnbt.getCompound("EntityData"), compoundnbt.getInt("TicksInHive"), compoundnbt.getInt("MinOccupationTicks"), compoundnbt.getBoolean("Queen"));
            this.ants.add(beehiveTileEntity$ant);
        }
    }

    public ListNBT getAnts() {
        ListNBT listnbt = new ListNBT();

        for (Ant beehiveTileEntity$ant : this.ants) {
            beehiveTileEntity$ant.entityData.remove("UUID");
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.put("EntityData", beehiveTileEntity$ant.entityData);
            compoundnbt.putInt("TicksInHive", beehiveTileEntity$ant.ticksInHive);
            compoundnbt.putInt("MinOccupationTicks", beehiveTileEntity$ant.minOccupationTicks);
            listnbt.add(compoundnbt);
        }

        return listnbt;
    }

    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.put("Ants", this.getAnts());
        compound.putInt("LeafFeedings",leafFeedings);
        return compound;
    }

    static class Ant {
        private final CompoundNBT entityData;
        private final int minOccupationTicks;
        private int ticksInHive;
        private boolean queen;

        private Ant(CompoundNBT p_i225767_1_, int p_i225767_2_, int p_i225767_3_, boolean queen) {
            p_i225767_1_.remove("UUID");
            this.entityData = p_i225767_1_;
            this.ticksInHive = p_i225767_2_;
            this.minOccupationTicks = p_i225767_3_;
            this.queen = queen;
        }
    }

    /* To avoid ants not being mapped to vanilla, we have to handle this seperately than the default entitytype implementation.*/
    @Nullable
    public static Entity loadEntityAndExecute(CompoundNBT compound, World worldIn, Function<Entity, Entity> p_220335_2_) {
        return loadEntity(compound, worldIn).map(p_220335_2_).map((p_220346_3_) -> {
            if (compound.contains("Passengers", 9)) {
                ListNBT listnbt = compound.getList("Passengers", 10);

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

    private static Optional<Entity> loadEntity(CompoundNBT compound, World worldIn) {
        try {
            return loadEntityUnchecked(compound, worldIn);
        } catch (RuntimeException runtimeexception) {
            return Optional.empty();
        }
    }

    public static Optional<Entity> loadEntityUnchecked(CompoundNBT compound, World worldIn) {
        EntityLeafcutterAnt leafcutterAnt = AMEntityRegistry.LEAFCUTTER_ANT.create(worldIn);
        leafcutterAnt.read(compound);
        return Optional.of(leafcutterAnt);
    }

}
