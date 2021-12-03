package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMPointOfInterestRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import com.google.common.base.Predicates;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.Tags;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BlockLeafcutterAntChamber extends Block {
    public static final IntegerProperty FUNGUS = IntegerProperty.create("fungus", 0, 5);

    public BlockLeafcutterAntChamber() {
        super(BlockBehaviour.Properties.of(Material.DIRT).sound(SoundType.GRAVEL).strength(1.3F).randomTicks());
        this.setRegistryName("alexsmobs:leafcutter_ant_chamber");
        this.registerDefaultState(this.stateDefinition.any().setValue(FUNGUS, 0));
    }

    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        int fungalLevel = state.getValue(FUNGUS);
        if (fungalLevel == 5) {
            boolean shroomlight = false;
            for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
                if(worldIn.getBlockState(blockpos).getBlock() == Blocks.SHROOMLIGHT){
                    shroomlight = true;
                }
            }
            if(!shroomlight){
                this.angerNearbyAnts(worldIn, pos);
            }
            worldIn.setBlockAndUpdate(pos, state.setValue(FUNGUS, 0));
            if(!worldIn.isClientSide){
                if(worldIn.random.nextInt(2) == 0){
                    Direction dir = Direction.getRandom(worldIn.random);
                    if(worldIn.getBlockState(pos.above()).getBlock() == AMBlockRegistry.LEAFCUTTER_ANTHILL){
                        dir = Direction.DOWN;
                    }
                    BlockPos offset = pos.relative(dir);
                    if(Tags.Blocks.DIRT.contains(worldIn.getBlockState(offset).getBlock()) && !worldIn.canSeeSky(offset)){
                        worldIn.setBlockAndUpdate(offset, this.defaultBlockState());
                    }
                }
                popResource(worldIn, pos, new ItemStack(AMItemRegistry.GONGYLIDIA));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {
            if (!worldIn.isAreaLoaded(pos, 3))
                return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
        if(worldIn.canSeeSky(pos.above())){
            worldIn.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
        }
    }

    public void playerDestroy(Level worldIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
        super.playerDestroy(worldIn, player, pos, state, te, stack);
        this.angerNearbyAnts(worldIn, pos);
    }

    private void angerNearbyAnts(Level world, BlockPos pos) {
        List<EntityLeafcutterAnt> list = world.getEntitiesOfClass(EntityLeafcutterAnt.class, (new AABB(pos)).inflate(20D, 6.0D, 20D));
        Player player = null;
        List<Player> list1 = world.getEntitiesOfClass(Player.class, (new AABB(pos)).inflate(20D, 6.0D, 20D));
        if (list1.isEmpty()) return; //Forge: Prevent Error when no players are around.
        int i = list1.size();
        player = list1.get(world.random.nextInt(i));
        if (!list.isEmpty()) {
            for (EntityLeafcutterAnt beeentity : list) {
                if (beeentity.getTarget() == null) {
                    beeentity.setTarget(player);
                }
            }
        }
        if(!world.isClientSide){
            PoiManager pointofinterestmanager = ((ServerLevel) world).getPoiManager();
            Stream<BlockPos> stream = pointofinterestmanager.findAll(AMPointOfInterestRegistry.LEAFCUTTER_ANT_HILL.getPredicate(), Predicates.alwaysTrue(), pos, 50, PoiManager.Occupancy.ANY);
            List<BlockPos> listOfHives = stream.collect(Collectors.toList());
            for (BlockPos pos2 : listOfHives) {
                if(world.getBlockEntity(pos2) instanceof TileEntityLeafcutterAnthill){
                    TileEntityLeafcutterAnthill beehivetileentity = (TileEntityLeafcutterAnthill) world.getBlockEntity(pos2);
                    beehivetileentity.angerAnts(player, world.getBlockState(pos2), BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
                }

            }
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FUNGUS);
    }

}
