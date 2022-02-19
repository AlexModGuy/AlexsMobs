package com.github.alexthe666.alexsmobs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Locale;

public class BlockEndPirateSail extends Block {

    public static final BooleanProperty EASTORWEST = BooleanProperty.create("eastorwest");
    public static final EnumProperty<SailType> SAIL = EnumProperty.create("sail", SailType.class);
    protected static final VoxelShape EW_AABB = Block.box(7.0D, 0.0D, 0.0D, 9.0D, 16.0D, 16.0D);
    protected static final VoxelShape NS_AABB = Block.box(0.0D, 0.0D, 7.0D, 16.0D, 16.0D, 9.0D);

    public BlockEndPirateSail(boolean spectre) {
        super(Properties.of(Material.GLASS).noOcclusion().emissiveRendering((a, b, c) -> true).sound(SoundType.GLASS).lightLevel((state) -> 5).requiresCorrectToolForDrops().strength(0.4F).color(MaterialColor.ICE));
        this.setRegistryName(spectre ? "alexsmobs:spectre_sail" : "alexsmobs:phantom_sail");
        this.registerDefaultState(this.stateDefinition.any().setValue(EASTORWEST, Boolean.valueOf(false)).setValue(SAIL, SailType.SINGLE));
    }

    public VoxelShape getShape(BlockState p_52807_, BlockGetter p_52808_, BlockPos p_52809_, CollisionContext p_52810_) {
        return p_52807_.getValue(EASTORWEST) ? EW_AABB : NS_AABB;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_58032_) {
        p_58032_.add(EASTORWEST, SAIL);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelReader levelreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockPos actualPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
        BlockPos u = blockpos.above();
        BlockPos d = blockpos.below();
        BlockState clickState = levelreader.getBlockState(actualPos);
        BlockState upState = levelreader.getBlockState(u);
        BlockState downState = levelreader.getBlockState(d);
        boolean axis = context.getClickedFace().getAxis() == Direction.Axis.Y ? context.getHorizontalDirection().getAxis() == Direction.Axis.X : context.getClickedFace().getAxis() == Direction.Axis.X;
        if(clickState.getBlock() instanceof BlockEndPirateSail){
            axis = clickState.getValue(EASTORWEST);
        }
        BlockState axisState = defaultBlockState().setValue(EASTORWEST, axis);
        return axisState.setValue(SAIL, getSailTypeFor(axisState, downState, upState));
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state2, LevelAccessor levelreader, BlockPos blockpos, BlockPos pos2) {
        BlockPos u = blockpos.above();
        BlockPos d = blockpos.below();
        BlockState upState = levelreader.getBlockState(u);
        BlockState downState = levelreader.getBlockState(d);
        return state.setValue(SAIL, getSailTypeFor(state, downState, upState));
    }


    private static SailType getSailTypeFor(BlockState us, BlockState below, BlockState above){
        if(below.getBlock() instanceof BlockEndPirateSail && below.getValue(EASTORWEST) == us.getValue(EASTORWEST)){
            return above.getBlock() instanceof BlockEndPirateSail ? SailType.MIDDLE : SailType.TOP;
        }else if(above.getBlock() instanceof BlockEndPirateSail && above.getValue(EASTORWEST) == us.getValue(EASTORWEST)){
            return SailType.BOTTOM;
        }else{
            return SailType.SINGLE;
        }
    }
    private enum SailType implements StringRepresentable {
        SINGLE,
        TOP,
        MIDDLE,
        BOTTOM;

        public String toString() {
            return this.getSerializedName();
        }

        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
