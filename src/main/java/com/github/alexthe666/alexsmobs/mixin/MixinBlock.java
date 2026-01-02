package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.item.ItemGhostlyPickaxe;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Block.class)
public abstract class MixinBlock {

    @Shadow
    public static List<ItemStack> getDrops(BlockState state, ServerLevel level, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool) {
        throw new AssertionError();
    }

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    private static void alexsmobs_dropResources(BlockState state, Level level, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool, CallbackInfo ci) {
        if (entity instanceof Player && tool.getItem() instanceof ItemGhostlyPickaxe && ItemGhostlyPickaxe.shouldStoreInGhost((LivingEntity)entity, tool)) {
            if (level instanceof ServerLevel) {
                ItemStack realTool = ((Player)entity).getMainHandItem();
                if(realTool.getItem() == tool.getItem()){
                    getDrops(state, (ServerLevel)level, pos, blockEntity, entity, tool).forEach((item) -> {
                        ItemGhostlyPickaxe.putItemInGhostInventoryOrDrop((LivingEntity)entity, realTool, item);
                    });
                }else{
                    getDrops(state, (ServerLevel)level, pos, blockEntity, entity, tool).forEach((item) -> {
                        Block.popResource(level, pos, item);
                    });
                }
            }
            ci.cancel();
        }
    }
}
