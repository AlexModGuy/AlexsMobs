package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityCapsid;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

public class BlockCapsid extends ContainerBlock {

    public static final DirectionProperty HORIZONTAL_FACING = HorizontalBlock.HORIZONTAL_FACING;
    public BlockCapsid() {
        super(Properties.create(Material.GLASS).notSolid().setAllowsSpawn(BlockCapsid::spawnOption).setOpaque(BlockCapsid::isntSolid).sound(SoundType.GLASS).setLightLevel((state) -> 5).harvestTool(ToolType.PICKAXE).hardnessAndResistance(4.5F));
        this.setRegistryName("alexsmobs:capsid");
    }

    public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
        return (BlockState)p_185499_1_.with(HORIZONTAL_FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(HORIZONTAL_FACING)));
    }

    public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
        return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(HORIZONTAL_FACING)));
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing());
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
    }

    private static Boolean spawnOption(BlockState state, IBlockReader reader, BlockPos pos, EntityType<?> entity) {
        return (boolean)false;
    }

    private static boolean isntSolid(BlockState state, IBlockReader reader, BlockPos pos) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isSideInvisible(BlockState p_200122_1_, BlockState p_200122_2_, Direction p_200122_3_) {
        return p_200122_2_.getBlock() == this ? true : super.isSideInvisible(p_200122_1_, p_200122_2_, p_200122_3_);
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack heldItem = player.getHeldItem(handIn);
        if (worldIn.getTileEntity(pos) instanceof TileEntityCapsid && (!player.isSneaking()  && heldItem.getItem() != this.asItem())) {
            TileEntityCapsid capsid = (TileEntityCapsid)worldIn.getTileEntity(pos);
            ItemStack copy = heldItem.copy();
            copy.setCount(1);
            if(capsid.getStackInSlot(0).isEmpty()){
                capsid.setInventorySlotContents(0, copy);
                if(!player.isCreative()){
                    heldItem.shrink(1);
                }
                return ActionResultType.SUCCESS;
            }else if(capsid.getStackInSlot(0).isItemEqual(copy) && capsid.getStackInSlot(0).getMaxStackSize() > capsid.getStackInSlot(0).getCount() + copy.getCount()){
                capsid.getStackInSlot(0).grow(1);
                if(!player.isCreative()){
                    heldItem.shrink(1);
                }
                return ActionResultType.SUCCESS;
            }else{
                spawnAsEntity(worldIn, pos, capsid.getStackInSlot(0).copy());
                capsid.setInventorySlotContents(0, ItemStack.EMPTY);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileEntityCapsid) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityCapsid) tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }


    public BlockRenderType getRenderType(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileEntityCapsid();
    }
}
