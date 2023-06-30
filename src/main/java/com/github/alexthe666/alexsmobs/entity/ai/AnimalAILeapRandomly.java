package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityBunfungus;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class AnimalAILeapRandomly extends Goal {

    private final PathfinderMob mob;
    private final int chance;
    private final int maxLeapDistance;
    private Vec3 leapToPos = null;

    public AnimalAILeapRandomly(PathfinderMob mob, int chance, int maxLeapDistance) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.mob = mob;
        this.chance = chance;
        this.maxLeapDistance = maxLeapDistance;
    }

    @Override
    public boolean canUse() {
        if(mob.getRandom().nextInt(this.chance) == 0 && mob.onGround() && mob.getNavigation().isDone()){
            Vec3 found = LandRandomPos.getPos(mob, maxLeapDistance, maxLeapDistance);
            if(found != null && mob.distanceToSqr(found) < maxLeapDistance * maxLeapDistance && hasLineOfSightBlock(found)){
                leapToPos = found;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return leapToPos != null && mob.distanceToSqr(leapToPos) < maxLeapDistance * maxLeapDistance && hasLineOfSightBlock(leapToPos);
    }

    private boolean hasLineOfSightBlock(Vec3 blockVec) {
        Vec3 Vector3d = new Vec3(mob.getX(), mob.getEyeY(), mob.getZ());
        BlockHitResult result = mob.level().clip(new ClipContext(Vector3d, blockVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob));
        return blockVec.distanceTo(result.getLocation()) < 1.2F;
    }

    @Override
    public void stop() {
        super.stop();
        leapToPos = null;
    }

    public void start(){
        if(leapToPos != null){
            Vec3 vector3d = this.mob.getDeltaMovement();
            Vec3 vector3d1 = new Vec3(this.leapToPos.x - this.mob.getX(), 0.0D, this.leapToPos.z - this.mob.getZ());
            if (vector3d1.lengthSqr() > 1.0E-7D) {
                vector3d1 = vector3d1.normalize().scale(0.9D).add(vector3d.scale(0.8D));
            }
            if(this.mob instanceof EntityBunfungus){
                ((EntityBunfungus) this.mob).onJump();
            }
            this.mob.setDeltaMovement(vector3d1.x, 0.6F, vector3d1.z);
            mob.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * Mth.RAD_TO_DEG);
            mob.yBodyRot = mob.getYRot();
            mob.yHeadRot = mob.getYRot();

            leapToPos = null;
        }
    }
}
