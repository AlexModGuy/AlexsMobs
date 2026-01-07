package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.entity.EntityManedWolf;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public class BlockLeafcutterAnthill extends BaseEntityBlock {
    public static final MapCodec<BlockLeafcutterAnthill> CODEC = simpleCodec(p -> new BlockLeafcutterAnthill());

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public BlockLeafcutterAnthill() {
        super(BlockBehaviour.Properties.of().sound(SoundType.GRAVEL).strength(0.75F));
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack heldItem, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (worldIn.getBlockEntity(pos) instanceof TileEntityLeafcutterAnthill) {
            TileEntityLeafcutterAnthill hill = (TileEntityLeafcutterAnthill) worldIn.getBlockEntity(pos);
            if (heldItem.getItem() == AMItemRegistry.GONGYLIDIA.get() && hill.hasQueen()) {
                hill.releaseQueens();
                if (!player.isCreative()) {
                    heldItem.shrink(1);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(heldItem, state, worldIn, pos, player, handIn, hit);
    }


    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }

    public BlockState playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        if (!worldIn.isClientSide && player.isCreative() && worldIn.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof TileEntityLeafcutterAnthill) {
                TileEntityLeafcutterAnthill anthivetileentity = (TileEntityLeafcutterAnthill) tileentity;
                ItemStack itemstack = new ItemStack(this);
                boolean flag = !anthivetileentity.hasNoAnts();
                if (!flag) {
                    return super.playerWillDestroy(worldIn, pos, state, player);
                }
                if (flag) {
                    CompoundTag compoundnbt = new CompoundTag();
                    compoundnbt.put("Ants", anthivetileentity.getAnts());
                    itemstack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(compoundnbt));
                }
                ItemEntity itementity = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), itemstack);
                itementity.setDefaultPickUpDelay();
                worldIn.addFreshEntity(itementity);
            }
        }

        return super.playerWillDestroy(worldIn, pos, state, player);
    }

    public void fallOn(Level worldIn, BlockState state, BlockPos pos, Entity entityIn, float fallDistance) {
        if (entityIn instanceof LivingEntity && !(entityIn instanceof EntityManedWolf)) {
            this.angerNearbyAnts(worldIn, (LivingEntity) entityIn, pos);
            if (!worldIn.isClientSide && worldIn.getBlockEntity(pos) instanceof TileEntityLeafcutterAnthill) {
                TileEntityLeafcutterAnthill beehivetileentity = (TileEntityLeafcutterAnthill) worldIn.getBlockEntity(pos);
                beehivetileentity.angerAnts((LivingEntity) entityIn, worldIn.getBlockState(pos), BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
                if(entityIn instanceof ServerPlayer){
                    AMAdvancementTriggerRegistry.STOMP_LEAFCUTTER_ANTHILL.get().trigger((ServerPlayer)entityIn);
                }
            }
        }
        super.fallOn(worldIn, state, pos, entityIn, fallDistance);
    }

    public void playerDestroy(Level worldIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
        super.playerDestroy(worldIn, player, pos, state, te, stack);
        if (!worldIn.isClientSide && te instanceof TileEntityLeafcutterAnthill) {
            TileEntityLeafcutterAnthill beehivetileentity = (TileEntityLeafcutterAnthill) te;
            if (stack.getEnchantmentLevel(worldIn.holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH)) == 0) {
                beehivetileentity.angerAnts(player, state, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
                worldIn.updateNeighbourForOutputSignal(pos, this);
                this.angerNearbyAnts(worldIn, pos);
            }
        }
    }

    private void angerNearbyAnts(Level world, BlockPos pos) {
        List<EntityLeafcutterAnt> list = world.getEntitiesOfClass(EntityLeafcutterAnt.class, (new AABB(pos)).inflate(20D, 6.0D, 20D));
        if (!list.isEmpty()) {
            List<Player> list1 = world.getEntitiesOfClass(Player.class, (new AABB(pos)).inflate(20D, 6.0D, 20D));
            if (list1.isEmpty()) return; //Forge: Prevent Error when no players are around.
            int i = list1.size();
            for (EntityLeafcutterAnt beeentity : list) {
                if (beeentity.getTarget() == null) {
                    beeentity.setTarget(list1.get(world.random.nextInt(i)));
                }
            }
        }


    }

    private void angerNearbyAnts(Level world, LivingEntity entity, BlockPos pos) {
        List<EntityLeafcutterAnt> list = world.getEntitiesOfClass(EntityLeafcutterAnt.class, (new AABB(pos)).inflate(20D, 6.0D, 20D));
        if (!list.isEmpty()) {
            for (EntityLeafcutterAnt beeentity : list) {
                if (beeentity.getTarget() == null) {
                    beeentity.setTarget(entity);
                }
            }
        }
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityLeafcutterAnthill(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
        return p_152180_.isClientSide ? null : createTickerHelper(p_152182_, AMTileEntityRegistry.LEAFCUTTER_ANTHILL.get(), TileEntityLeafcutterAnthill::serverTick);
    }
}
