package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityTerrapin;
import com.github.alexthe666.alexsmobs.entity.util.TerrapinTypes;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityCapsid;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityTerrapinEgg;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BlockTerrapinEgg extends BaseEntityBlock {
    public static final IntegerProperty HATCH = BlockStateProperties.HATCH;
    public static final IntegerProperty EGGS = BlockStateProperties.EGGS;
    private static final VoxelShape ONE_EGG_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 12.0D, 7.0D, 12.0D);
    private static final VoxelShape MULTI_EGG_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 7.0D, 15.0D);

    public BlockTerrapinEgg() {
        super(Properties.of(Material.EGG, MaterialColor.SAND).strength(0.5F).sound(SoundType.METAL).randomTicks().noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(HATCH, Integer.valueOf(0)).setValue(EGGS, Integer.valueOf(1)));
    }

    public static boolean hasProperHabitat(BlockGetter reader, BlockPos blockReader) {
        return isProperHabitat(reader, blockReader.below());
    }

    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }

    public static boolean isProperHabitat(BlockGetter reader, BlockPos pos) {
        return reader.getBlockState(pos).is(BlockTags.SAND) || BlockTags.getAllTags().getTag(AMTagRegistry.CROCODILE_SPAWNS).contains(reader.getBlockState(pos).getBlock());
    }

    public void stepOn(Level worldIn, BlockPos pos, BlockState state, Entity entityIn) {
        this.tryTrample(worldIn, pos, entityIn, 100);
        super.stepOn(worldIn, pos, state, entityIn);
    }

    public void fallOn(Level worldIn, BlockState state, BlockPos pos, Entity entityIn, float fallDistance) {
        if (!(entityIn instanceof Zombie)) {
            this.tryTrample(worldIn, pos, entityIn, 3);
        }

        super.fallOn(worldIn, state, pos, entityIn, fallDistance);
    }

    private void tryTrample(Level worldIn, BlockPos pos, Entity trampler, int chances) {
        if (this.canTrample(worldIn, trampler)) {
            if (!worldIn.isClientSide && worldIn.random.nextInt(chances) == 0) {
                BlockState blockstate = worldIn.getBlockState(pos);
                this.removeOneEgg(worldIn, pos, blockstate);

            }

        }
    }

    private void removeOneEgg(Level worldIn, BlockPos pos, BlockState state) {
        worldIn.playSound(null, pos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + worldIn.random.nextFloat() * 0.2F);
        int i = state.getValue(EGGS);
        if (i <= 1) {
            worldIn.destroyBlock(pos, false);
        } else {
            worldIn.setBlock(pos, state.setValue(EGGS, Integer.valueOf(i - 1)), 2);
            worldIn.levelEvent(2001, pos, Block.getId(state));
        }

    }

    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {
        if (this.canGrow(worldIn) && hasProperHabitat(worldIn, pos)) {
            int i = state.getValue(HATCH);
            if (i < 2) {
                worldIn.playSound(null, pos, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
                worldIn.setBlock(pos, state.setValue(HATCH, Integer.valueOf(i + 1)), 2);
            } else {
                worldIn.playSound(null, pos, SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
                worldIn.removeBlock(pos, false);
                for (int j = 0; j < state.getValue(EGGS); ++j) {
                    worldIn.levelEvent(2001, pos, Block.getId(state));
                    EntityTerrapin turtleentity = AMEntityRegistry.TERRAPIN.get().create(worldIn);
                    turtleentity.setAge(-24000);
                    if(worldIn.getBlockEntity(pos) instanceof TileEntityTerrapinEgg eggTE){
                        eggTE.addAttributesToOffspring(turtleentity, random);
                    }
                    turtleentity.setFromBucket(true);
                    turtleentity.moveTo((double) pos.getX() + 0.3D + (double) j * 0.2D, pos.getY(), (double) pos.getZ() + 0.3D, 0.0F, 0.0F);
                    worldIn.addFreshEntity(turtleentity);
                }
            }
        }

    }

    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (hasProperHabitat(worldIn, pos) && !worldIn.isClientSide) {
            worldIn.levelEvent(2005, pos, 0);
        }

    }

    private boolean canGrow(Level worldIn) {
        float f = worldIn.getTimeOfDay(1.0F);
        if ((double) f < 0.69D && (double) f > 0.65D) {
            return true;
        } else {
            return worldIn.random.nextInt(15) == 0;
        }
    }

    public void playerDestroy(Level worldIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
        super.playerDestroy(worldIn, player, pos, state, te, stack);
        this.removeOneEgg(worldIn, pos, state);
    }

    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return useContext.getItemInHand().getItem() == this.asItem() && state.getValue(EGGS) < 4 || super.canBeReplaced(state, useContext);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        return blockstate.getBlock() == this ? blockstate.setValue(EGGS, Integer.valueOf(Math.min(4, blockstate.getValue(EGGS) + 1))) : super.getStateForPlacement(context);
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return state.getValue(EGGS) > 1 ? MULTI_EGG_SHAPE : ONE_EGG_SHAPE;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HATCH, EGGS);
    }

    private boolean canTrample(Level worldIn, Entity trampler) {
        if (!(trampler instanceof EntityTerrapin) && !(trampler instanceof Bat)) {
            if (!(trampler instanceof LivingEntity)) {
                return false;
            } else {
                return trampler instanceof Player || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(worldIn, trampler);
            }
        } else {
            return false;
        }
    }

    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        BlockEntity blockentity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        ItemStack stack = new ItemStack(AMBlockRegistry.TERRAPIN_EGG.get());
        if (blockentity instanceof TileEntityTerrapinEgg) {
            TileEntityTerrapinEgg egg = (TileEntityTerrapinEgg)blockentity;
            CompoundTag tag = stack.getOrCreateTagElement("BlockEntityTag");
            CompoundTag parent1 = new CompoundTag();
            CompoundTag parent2 = new CompoundTag();
            boolean flag = false;
            if(egg.parent1 != null){
                flag = true;
                egg.parent1.writeToNBT(parent1);
            }
            if(egg.parent2 != null){
                flag = true;
                egg.parent2.writeToNBT(parent2);
            }
            if(flag){
                tag.put("Parent1Data", parent1);
                tag.put("Parent2Data", parent2);
            }
        }

        return List.of(stack);
    }

    public void appendHoverText(ItemStack stack, @Nullable BlockGetter w, List<Component> list, TooltipFlag flags) {
        super.appendHoverText(stack, w, list, flags);
        CompoundTag compoundtag = BlockItem.getBlockEntityData(stack);
        if (compoundtag != null && compoundtag.contains("Parent1Data") && compoundtag.contains("Parent2Data")) {
            TerrapinTypes parent1Type = TerrapinTypes.values()[Mth.clamp(compoundtag.getCompound("Parent1Data").getInt("TerrapinType"), 0, TerrapinTypes.values().length - 1)];
            TerrapinTypes parent2Type = TerrapinTypes.values()[Mth.clamp(compoundtag.getCompound("Parent2Data").getInt("TerrapinType"), 0, TerrapinTypes.values().length - 1)];
            String s1 = new TranslatableComponent(parent1Type.getTranslationName()).getString();
            String s2 = new TranslatableComponent(parent2Type.getTranslationName()).getString();
            list.add(new TranslatableComponent("block.alexsmobs.terrapin_egg.desc", s1, s2).withStyle(ChatFormatting.GRAY));
        }
    }

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState state2, boolean b) {
        if (state.is(AMBlockRegistry.TERRAPIN_EGG.get()) && state.getValue(EGGS) <= 1) {
            super.onRemove(state, level, pos, state2, b);
        }
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityTerrapinEgg(pos, state);
    }

}
