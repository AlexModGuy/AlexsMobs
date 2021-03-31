package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

public class ItemLeafcutterPupa extends Item {

    public ItemLeafcutterPupa(Properties props) {
        super(props);
    }

    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos blockpos = context.getPos();
        BlockState blockstate = world.getBlockState(blockpos);
        if (Tags.Blocks.DIRT.contains(blockstate.getBlock()) && Tags.Blocks.DIRT.contains(world.getBlockState(blockpos.down()).getBlock())) {
            PlayerEntity playerentity = context.getPlayer();
            world.playSound(playerentity, blockpos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!world.isRemote) {
                world.setBlockState(blockpos, AMBlockRegistry.LEAFCUTTER_ANTHILL.getDefaultState(), 11);
                world.setBlockState(blockpos.down(), AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.getDefaultState(), 11);
                TileEntity tileentity = world.getTileEntity(blockpos);
                if (tileentity instanceof TileEntityLeafcutterAnthill) {
                    TileEntityLeafcutterAnthill beehivetileentity = (TileEntityLeafcutterAnthill)tileentity;
                    int j = 3;
                    for(int k = 0; k < j; ++k) {
                        EntityLeafcutterAnt beeentity = new EntityLeafcutterAnt(AMEntityRegistry.LEAFCUTTER_ANT, world);
                        beeentity.setQueen(k == 0);
                        beehivetileentity.tryEnterHive(beeentity, false, 100);
                    }
                }
                if (playerentity != null && !playerentity.isCreative()) {
                    context.getItem().shrink(1);
                }
            }

            return ActionResultType.func_233537_a_(world.isRemote);
        } else {
            return ActionResultType.PASS;
        }
    }
}
