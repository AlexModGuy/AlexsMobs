package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

public class BlockLeafcutterAnthill extends ContainerBlock {

    public BlockLeafcutterAnthill() {
        super(AbstractBlock.Properties.create(Material.ORGANIC).sound(SoundType.GROUND).harvestTool(ToolType.SHOVEL).hardnessAndResistance(2.5F));
        this.setRegistryName("alexsmobs:leafcutter_anthill");
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.getTileEntity(pos) instanceof TileEntityLeafcutterAnthill) {
            TileEntityLeafcutterAnthill hill = (TileEntityLeafcutterAnthill) worldIn.getTileEntity(pos);
            ItemStack heldItem = player.getHeldItem(handIn);
            if (heldItem.getItem() == AMItemRegistry.GONGYLIDIA && hill.hasQueen()) {
                hill.releaseQueens();
                if (!player.isCreative()) {
                    heldItem.shrink(1);
                }
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }


    public BlockRenderType getRenderType(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!worldIn.isRemote && player.isCreative() && worldIn.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof TileEntityLeafcutterAnthill) {
                TileEntityLeafcutterAnthill anthivetileentity = (TileEntityLeafcutterAnthill) tileentity;
                ItemStack itemstack = new ItemStack(this);
                boolean flag = !anthivetileentity.hasNoAnts();
                if (!flag) {
                    return;
                }
                if (flag) {
                    CompoundNBT compoundnbt = new CompoundNBT();
                    compoundnbt.put("Ants", anthivetileentity.getAnts());
                    itemstack.setTagInfo("BlockEntityTag", compoundnbt);
                }
                CompoundNBT compoundnbt1 = new CompoundNBT();
                itemstack.setTagInfo("BlockStateTag", compoundnbt1);
                ItemEntity itementity = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), itemstack);
                itementity.setDefaultPickupDelay();
                worldIn.addEntity(itementity);
            }
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        if (entityIn instanceof LivingEntity) {
            this.angerNearbyAnts(worldIn, (LivingEntity) entityIn, pos);
            if (!worldIn.isRemote && worldIn.getTileEntity(pos) instanceof TileEntityLeafcutterAnthill) {
                TileEntityLeafcutterAnthill beehivetileentity = (TileEntityLeafcutterAnthill) worldIn.getTileEntity(pos);
                beehivetileentity.angerAnts((LivingEntity) entityIn, worldIn.getBlockState(pos), BeehiveTileEntity.State.EMERGENCY);
                if(entityIn instanceof ServerPlayerEntity){
                    AMAdvancementTriggerRegistry.STOMP_LEAFCUTTER_ANTHILL.trigger((ServerPlayerEntity)entityIn);
                }
            }
        }
        super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
    }

    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        if (!worldIn.isRemote && te instanceof TileEntityLeafcutterAnthill) {
            TileEntityLeafcutterAnthill beehivetileentity = (TileEntityLeafcutterAnthill) te;
            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0) {
                beehivetileentity.angerAnts(player, state, BeehiveTileEntity.State.EMERGENCY);
                worldIn.updateComparatorOutputLevel(pos, this);
                this.angerNearbyAnts(worldIn, pos);
            }
        }
    }

    private void angerNearbyAnts(World world, BlockPos pos) {
        List<EntityLeafcutterAnt> list = world.getEntitiesWithinAABB(EntityLeafcutterAnt.class, (new AxisAlignedBB(pos)).grow(20D, 6.0D, 20D));
        if (!list.isEmpty()) {
            List<PlayerEntity> list1 = world.getEntitiesWithinAABB(PlayerEntity.class, (new AxisAlignedBB(pos)).grow(20D, 6.0D, 20D));
            if (list1.isEmpty()) return; //Forge: Prevent Error when no players are around.
            int i = list1.size();
            for (EntityLeafcutterAnt beeentity : list) {
                if (beeentity.getAttackTarget() == null) {
                    beeentity.setAttackTarget(list1.get(world.rand.nextInt(i)));
                }
            }
        }


    }

    private void angerNearbyAnts(World world, LivingEntity entity, BlockPos pos) {
        List<EntityLeafcutterAnt> list = world.getEntitiesWithinAABB(EntityLeafcutterAnt.class, (new AxisAlignedBB(pos)).grow(20D, 6.0D, 20D));
        if (!list.isEmpty()) {
            for (EntityLeafcutterAnt beeentity : list) {
                if (beeentity.getAttackTarget() == null) {
                    beeentity.setAttackTarget(entity);
                }
            }
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileEntityLeafcutterAnthill();
    }
}
