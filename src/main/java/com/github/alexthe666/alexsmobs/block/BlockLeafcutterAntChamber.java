package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMPointOfInterestRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import com.google.common.base.Predicates;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlockLeafcutterAntChamber extends Block {
    public static final IntegerProperty FUNGUS = IntegerProperty.create("fungus", 0, 5);

    public BlockLeafcutterAntChamber() {
        super(AbstractBlock.Properties.create(Material.ORGANIC).sound(SoundType.GROUND).harvestTool(ToolType.SHOVEL).hardnessAndResistance(4F).tickRandomly());
        this.setRegistryName("alexsmobs:leafcutter_ant_chamber");
        this.setDefaultState(this.stateContainer.getBaseState().with(FUNGUS, 0));
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        int fungalLevel = state.get(FUNGUS);
        if (fungalLevel == 5) {
            this.angerNearbyAnts(worldIn, pos);
            worldIn.setBlockState(pos, state.with(FUNGUS, 0));
            if(!worldIn.isRemote){
                if(worldIn.rand.nextInt(2) == 0){
                    Direction dir = Direction.getRandomDirection(worldIn.rand);
                    if(worldIn.getBlockState(pos.up()).getBlock() == AMBlockRegistry.LEAFCUTTER_ANTHILL){
                        dir = Direction.DOWN;
                    }
                    BlockPos offset = pos.offset(dir);
                    if(Tags.Blocks.DIRT.contains(worldIn.getBlockState(offset).getBlock()) && !worldIn.canSeeSky(offset)){
                        worldIn.setBlockState(offset, this.getDefaultState());
                    }
                }
                spawnAsEntity(worldIn, pos, new ItemStack(AMItemRegistry.GONGYLIDIA));
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
            if (!worldIn.isAreaLoaded(pos, 3))
                return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
        if(worldIn.canSeeSky(pos)){
            worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState());
        }
    }

    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        this.angerNearbyAnts(worldIn, pos);
    }

    private void angerNearbyAnts(World world, BlockPos pos) {
        List<EntityLeafcutterAnt> list = world.getEntitiesWithinAABB(EntityLeafcutterAnt.class, (new AxisAlignedBB(pos)).grow(20D, 6.0D, 20D));
        PlayerEntity player = null;
        List<PlayerEntity> list1 = world.getEntitiesWithinAABB(PlayerEntity.class, (new AxisAlignedBB(pos)).grow(20D, 6.0D, 20D));
        if (list1.isEmpty()) return; //Forge: Prevent Error when no players are around.
        int i = list1.size();
        player = list1.get(world.rand.nextInt(i));
        if (!list.isEmpty()) {
            for (EntityLeafcutterAnt beeentity : list) {
                if (beeentity.getAttackTarget() == null) {
                    beeentity.setAttackTarget(player);
                }
            }
        }
        if(!world.isRemote){
            PointOfInterestManager pointofinterestmanager = ((ServerWorld) world).getPointOfInterestManager();
            Stream<BlockPos> stream = pointofinterestmanager.findAll(AMPointOfInterestRegistry.LEAFCUTTER_ANT_HILL.getPredicate(), Predicates.alwaysTrue(), pos, 50, PointOfInterestManager.Status.ANY);
            List<BlockPos> listOfHives = stream.collect(Collectors.toList());
            for (BlockPos pos2 : listOfHives) {
                if(world.getTileEntity(pos2) instanceof TileEntityLeafcutterAnthill){
                    TileEntityLeafcutterAnthill beehivetileentity = (TileEntityLeafcutterAnthill) world.getTileEntity(pos2);
                    beehivetileentity.angerAnts(player, world.getBlockState(pos2), BeehiveTileEntity.State.EMERGENCY);
                }

            }
        }
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FUNGUS);
    }
}
