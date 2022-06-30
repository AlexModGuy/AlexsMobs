package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemFlutterPot extends Item implements DispensibleContainerItem {

    public ItemFlutterPot(Properties builder) {
        super(builder.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        if(!world.isClientSide){
            if(this.placeFish((ServerLevel)world, context.getItemInHand(), blockpos) && (context.getPlayer() == null || !context.getPlayer().isCreative())){
                context.getItemInHand().shrink(1);
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
        Entity entity = AMEntityRegistry.FLUTTER.get().spawn(worldIn, stack, (Player)null, pos, MobSpawnType.BUCKET, true, false);
        if (entity != null && entity instanceof EntityFlutter) {
            CompoundTag compoundnbt = stack.getOrCreateTag();
            if(compoundnbt.contains("FlutterData")){
                ((EntityFlutter)entity).readAdditionalSaveData(compoundnbt.getCompound("FlutterData"));
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
