package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCosmicCod;
import com.github.alexthe666.alexsmobs.entity.EntityFlutter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class ItemCosmicCodBucket extends Item implements DispensibleContainerItem {

    public ItemCosmicCodBucket(Properties builder) {
        super(builder.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        if(!world.isClientSide){
            if(this.placeFish((ServerLevel)world, context.getItemInHand(), blockpos)){
                boolean flag = false;
                if(context.getPlayer() != null){
                    if(context.getPlayer().isCreative()){
                        flag = true;
                    }
                    if(!context.getPlayer().addItem(new ItemStack(Items.BUCKET))){
                        context.getPlayer().drop(new ItemStack(Items.BUCKET), true);
                    }
                }
                if(!flag){
                    context.getItemInHand().shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(world.isClientSide);
        }else{
            return InteractionResult.PASS;
        }

    }

    protected void playEmptySound(@Nullable Player player, LevelAccessor worldIn, BlockPos pos) {
        worldIn.playSound(player, pos, SoundEvents.BUCKET_EMPTY_FISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
    }

    private boolean placeFish(ServerLevel worldIn, ItemStack stack, BlockPos pos) {
        Entity entity = AMEntityRegistry.COSMIC_COD.spawn(worldIn, stack, (Player)null, pos, MobSpawnType.BUCKET, true, false);
        if (entity != null && entity instanceof EntityCosmicCod) {
            CompoundTag compoundnbt = stack.getOrCreateTag();
            if(compoundnbt.contains("CosmicCodData")){
                ((EntityFlutter)entity).readAdditionalSaveData(compoundnbt.getCompound("CosmicCodData"));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean emptyContents(@org.jetbrains.annotations.Nullable Player p_150821_, Level p_150822_, BlockPos p_150823_, @org.jetbrains.annotations.Nullable BlockHitResult p_150824_) {
        return false;
    }
}
