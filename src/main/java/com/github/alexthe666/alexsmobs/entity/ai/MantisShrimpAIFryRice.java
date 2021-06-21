package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityMantisShrimp;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.block.*;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import java.util.EnumSet;

public class MantisShrimpAIFryRice extends MoveToBlockGoal {

    private EntityMantisShrimp mantisShrimp;
    private ITag<Item> tag;
    private boolean wasLitPrior = false;
    private int cookingTicks = 0;

    public MantisShrimpAIFryRice(EntityMantisShrimp entityMantisShrimp) {
        super(entityMantisShrimp, 1, 8);
        this.mantisShrimp = entityMantisShrimp;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        tag = ItemTags.getCollection().get(AMTagRegistry.SHRIMP_RICE_FRYABLES);
    }

    public void resetTask(){
        cookingTicks = 0;
        if(!wasLitPrior){
            BlockPos blockpos = this.func_241846_j().down();
            BlockState state = mantisShrimp.world.getBlockState(blockpos);
            if(state.getBlock() instanceof AbstractFurnaceBlock && !wasLitPrior){
                mantisShrimp.world.setBlockState(blockpos, state.with(AbstractFurnaceBlock.LIT, false));
            }
        }
        super.resetTask();
    }

    public void tick() {
        super.tick();
        BlockPos blockpos = this.func_241846_j().down();
        if(this.getIsAboveDestination()){
            BlockState state = mantisShrimp.world.getBlockState(blockpos);
            if(mantisShrimp.punchProgress == 0){
                mantisShrimp.punch();
            }
            if(state.getBlock() instanceof AbstractFurnaceBlock && !wasLitPrior){
                mantisShrimp.world.setBlockState(blockpos, state.with(AbstractFurnaceBlock.LIT, true));
            }
            cookingTicks++;
            if(cookingTicks > 200){
                cookingTicks = 0;
                ItemStack rice = new ItemStack(AMItemRegistry.SHRIMP_FRIED_RICE);
                rice.setCount(mantisShrimp.getHeldItemMainhand().getCount());
                mantisShrimp.setHeldItem(Hand.MAIN_HAND, rice);

            }
        }else{
            cookingTicks = 0;
        }
    }

    @Override
    public boolean shouldExecute() {
        return tag.contains(this.mantisShrimp.getHeldItemMainhand().getItem()) && !mantisShrimp.isSitting() && super.shouldExecute();
    }

    public boolean shouldContinueExecuting() {
        return tag.contains(this.mantisShrimp.getHeldItemMainhand().getItem()) && !mantisShrimp.isSitting() && super.shouldContinueExecuting();
    }

    public double getTargetDistanceSq() {
        return 3.9F;
    }

    @Override
    protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
        if (!worldIn.isAirBlock(pos.up())) {
            return false;
        } else {
            BlockState blockstate = worldIn.getBlockState(pos);
            if(blockstate.getBlock() instanceof AbstractFurnaceBlock){
                wasLitPrior = blockstate.get(AbstractFurnaceBlock.LIT);
                return true;
            }
            return blockstate.getBlock().isIn(BlockTags.CAMPFIRES);
        }
    }


}
