package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.citadel.server.entity.collision.ICustomCollisions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MovementControllerCustomCollisions extends MoveControl {

    public MovementControllerCustomCollisions(Mob mob) {
        super(mob);
    }


    public void tick() {
        if (this.operation == MoveControl.Operation.STRAFE) {
            float f = (float)this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
            float f1 = (float)this.speedModifier * f;
            float f2 = this.strafeForwards;
            float f3 = this.strafeRight;
            float f4 = Mth.sqrt(f2 * f2 + f3 * f3);
            if (f4 < 1.0F) {
                f4 = 1.0F;
            }

            f4 = f1 / f4;
            f2 = f2 * f4;
            f3 = f3 * f4;
            float f5 = Mth.sin(this.mob.getYRot() * Mth.DEG_TO_RAD);
            float f6 = Mth.cos(this.mob.getYRot() * Mth.DEG_TO_RAD);
            float f7 = f2 * f6 - f3 * f5;
            float f8 = f3 * f6 + f2 * f5;
            if (!this.isWalkable(f7, f8)) {
                this.strafeForwards = 1.0F;
                this.strafeRight = 0.0F;
            }

            this.mob.setSpeed(f1);
            this.mob.setZza(this.strafeForwards);
            this.mob.setXxa(this.strafeRight);
            this.operation = MoveControl.Operation.WAIT;
        } else if (this.operation == MoveControl.Operation.MOVE_TO) {
            this.operation = MoveControl.Operation.WAIT;
            double d0 = this.wantedX - this.mob.getX();
            double d1 = this.wantedZ - this.mob.getZ();
            double d2 = this.wantedY - this.mob.getY();
            double d3 = d0 * d0 + d2 * d2 + d1 * d1;
            if (d3 < (double)2.5000003E-7F) {
                this.mob.setZza(0.0F);
                return;
            }

            float f9 = (float)(Mth.atan2(d1, d0) * (double)Mth.RAD_TO_DEG) - 90.0F;
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f9, 90.0F));
            this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            BlockPos blockpos = this.mob.blockPosition();
            BlockState blockstate = this.mob.level.getBlockState(blockpos);
            VoxelShape voxelshape = blockstate.getBlockSupportShape(this.mob.level, blockpos);
            if(!(mob instanceof ICustomCollisions && ((ICustomCollisions) mob).canPassThrough(blockpos, blockstate, voxelshape))){
                if (d2 > (double)this.mob.getStepHeight() && d0 * d0 + d1 * d1 < (double)Math.max(1.0F, this.mob.getBbWidth()) || !voxelshape.isEmpty() && this.mob.getY() < voxelshape.max(Direction.Axis.Y) + (double)blockpos.getY() && !blockstate.is(BlockTags.DOORS) && !blockstate.is(BlockTags.FENCES)) {
                    this.mob.getJumpControl().jump();
                    this.operation = MoveControl.Operation.JUMPING;
                }
            }
        } else if (this.operation == MoveControl.Operation.JUMPING) {
            this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            if (this.mob.isOnGround()) {
                this.operation = MoveControl.Operation.WAIT;
            }
        } else {
            this.mob.setZza(0.0F);
        }

    }

    private boolean isWalkable(float p_234024_1_, float p_234024_2_) {
        PathNavigation pathnavigator = this.mob.getNavigation();
        if (pathnavigator != null) {
            NodeEvaluator nodeprocessor = pathnavigator.getNodeEvaluator();
            if (nodeprocessor != null && nodeprocessor.getBlockPathType(this.mob.level, Mth.floor(this.mob.getX() + (double)p_234024_1_), Mth.floor(this.mob.getY()), Mth.floor(this.mob.getZ() + (double)p_234024_2_)) != BlockPathTypes.WALKABLE) {
                return false;
            }
        }

        return true;
    }
}
