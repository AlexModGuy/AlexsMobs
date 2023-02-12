package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityDragonsBreathCannonball;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class BlockDragonsBreathCannon extends Block {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final IntegerProperty PHASE = IntegerProperty.create("phase", 0, 2);
    public BlockDragonsBreathCannon() {
        super(BlockBehaviour.Properties.of(Material.METAL).requiresCorrectToolForDrops().noOcclusion().lightLevel((state) -> 2).emissiveRendering((blockState, blockGetter, blockPos) -> true).strength(2F));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PHASE, 0));
    }

    public static Vec3 getDispensePosition(BlockPos coords, Direction dir) {
        float f = dir.getAxis().isHorizontal() ? 0F : 0.5F;
        float f2 = dir.getAxis().isHorizontal() ? -0.2F : 0;
        double d0 = coords.getX() + 0.5D + 0.7D * (double) dir.getStepX();
        double d1 = coords.getY() + 0.5D + f * (double) dir.getStepY() + f2;
        double d2 = coords.getZ() + 0.5D + 0.7D * (double) dir.getStepZ();
        return new Vec3(d0, d1, d2);
    }

    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(handIn);
        if(heldItem.is(Items.DRAGON_BREATH) && state.getValue(PHASE) == 0){
            worldIn.setBlock(pos, state.setValue(PHASE, 1), 2);
            worldIn.scheduleTick(pos, this, 60);
            if(!player.isCreative()){
                if(heldItem.hasCraftingRemainingItem()){
                    player.addItem(heldItem.getCraftingRemainingItem().copy());
                }
                heldItem.shrink(1);
            }
            player.swing(handIn);
            worldIn.playSound((Player)null, pos, AMSoundRegistry.DRAGONS_BREATH_CANNON_CHARGE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
    }

    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
        tickCannon(state, worldIn, pos, true);
    }

    public void tickCannon(BlockState state, Level worldIn, BlockPos pos, boolean tickOff) {
        int phase = state.getValue(PHASE);
        if(phase == 1){
            worldIn.setBlock(pos, state.setValue(PHASE, 2), 2);
            worldIn.scheduleTick(pos, this, 10);
            Direction direction = state.getValue(FACING);
            Vec3 dispensePosition = getDispensePosition(pos, state.getValue(FACING));
            EntityDragonsBreathCannonball projectile = new EntityDragonsBreathCannonball(AMEntityRegistry.DRAGONS_BREATH_CANNONBALL.get(), worldIn);
            float strength = 1.5F + worldIn.random.nextFloat();
            projectile.setDeltaMovement((double)((float)direction.getStepX() * strength), (double)((float)direction.getStepY() * strength), (double)((float)direction.getStepZ() * strength));
            projectile.setPos(dispensePosition.x, dispensePosition.y, dispensePosition.z);
            if (!worldIn.isClientSide) {
                worldIn.addFreshEntity(projectile);
            }
            worldIn.playSound((Player)null, pos, SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.BLOCKS, 1.0F, 1.0F);
        }else if(phase == 2){
            worldIn.setBlock(pos, state.setValue(PHASE, 0), 2);
        }
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PHASE);
    }

}
