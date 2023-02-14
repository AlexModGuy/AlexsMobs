package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.EntityEndPirateRigging;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ItemEndPirateRigging extends Item {
    public ItemEndPirateRigging(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        if(level.getBlockState(blockpos).is(Blocks.END_ROD)){
            if(context.getPlayer() != null){
                context.getPlayer().swing(context.getHand());
                context.getPlayer().playSound(SoundEvents.LEASH_KNOT_PLACE);
            }
            if(!level.isClientSide && this.placeRigging((ServerLevel)level, context.getPlayer(), context.getItemInHand(), blockpos) && (context.getPlayer() == null || !context.getPlayer().isCreative())){
                context.getItemInHand().shrink(1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }else{
            return InteractionResult.PASS;
        }

    }

    private boolean placeRigging(ServerLevel level, Player player, ItemStack itemInHand, BlockPos blockpos) {
        EntityEndPirateRigging rigging = new EntityEndPirateRigging(level, blockpos);
        Entity connection = null;
        Vec3 blockVec = Vec3.atCenterOf(blockpos);
        AABB searchAABB = new AABB(-256, -256, -256, 256, 256, 256).move(blockpos);
        if(player != null){
            for(EntityEndPirateRigging otherRigging : level.getEntitiesOfClass(EntityEndPirateRigging.class, searchAABB)) {
                if (otherRigging.getConnectionUUID() != null && otherRigging.getConnectionUUID().equals(player.getUUID()) && (connection == null || connection.distanceToSqr(blockVec) > otherRigging.distanceToSqr(blockVec))) {
                    connection = otherRigging;
                }
            }
            if(connection == null){
                connection = player;
            }
        }
        if(connection != null){
            rigging.setConnectionUUID(connection.getUUID());
            if(connection instanceof EntityEndPirateRigging rigging1){
                rigging1.setConnectionUUID(null);
            }
        }
        BlockState state = level.getBlockState(blockpos);
        if(state.getBlock() instanceof DirectionalBlock){
            rigging.setAttachmentRotation(state.getValue(DirectionalBlock.FACING));
        }
        return level.addFreshEntity(rigging);
    }
}
