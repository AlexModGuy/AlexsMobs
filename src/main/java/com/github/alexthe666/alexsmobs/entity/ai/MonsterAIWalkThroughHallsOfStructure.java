package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MonsterAIWalkThroughHallsOfStructure extends RandomStrollGoal {

    private TagKey<Structure> structureTagKey;
    private double maximumDistance = 0;
    private double maximumYDistance = 3;

    private int errorCooldown = 0;

    public MonsterAIWalkThroughHallsOfStructure(PathfinderMob mob, double speed, int chance, TagKey<Structure> structureTagKey, double maximumDistance) {
        super(mob, speed, chance, false);
        this.structureTagKey = structureTagKey;
        this.maximumDistance = 32;
    }

    public boolean canUse() {
        if(errorCooldown > 0){
            errorCooldown--;
        }
        return super.canUse();
    }

    public void tick(){
        super.tick();
        if(errorCooldown > 0){
            errorCooldown--;
        }
    }

    @Nullable
    protected Vec3 getPosition() {
        StructureStart start = getNearestStructure(mob.blockPosition());
        if(start != null && start.isValid() || errorCooldown > 0){
            List<BlockPos> validPieceCenters = new ArrayList<>();
            for(StructurePiece piece : start.getPieces()){
                BoundingBox boundingbox = piece.getBoundingBox();
                BlockPos blockpos = boundingbox.getCenter();
                BlockPos blockpos1 = new BlockPos(blockpos.getX(), boundingbox.minY(), blockpos.getZ());
                double yDist = Math.abs(blockpos1.getY() - mob.blockPosition().getY());
                if(this.mob.distanceToSqr(Vec3.atCenterOf(blockpos1)) <= this.maximumDistance * this.maximumDistance && yDist < maximumYDistance){
                    validPieceCenters.add(blockpos1);
                }
            }
            if(!validPieceCenters.isEmpty()){
                BlockPos randomCenter = validPieceCenters.size() > 1 ? validPieceCenters.get(mob.getRandom().nextInt(validPieceCenters.size() - 1)) : validPieceCenters.get(0);
                return Vec3.atCenterOf(randomCenter.offset(mob.getRandom().nextInt(2) - 1, 0, mob.getRandom().nextInt(2) - 1));

            }

        }
        return getPositionTowardsAnywhere();
    }


    @Nullable
    private Vec3 getPositionTowardsAnywhere() {
        return DefaultRandomPos.getPos(this.mob, 10, 7);
    }

    @Nullable
    private StructureStart getNearestStructure(BlockPos pos){
        ServerLevel serverlevel = (ServerLevel)this.mob.level();
        try{
            StructureStart start = serverlevel.structureManager().getStructureWithPieceAt(pos, structureTagKey);
            if(start.isValid()){
                return start;
            }else{
                BlockPos nearestOf = serverlevel.findNearestMapStructure(structureTagKey, pos, (int)(this.maximumDistance / 16), false);
                if(nearestOf == null || nearestOf.distToCenterSqr(this.mob.getX(), this.mob.getY(), this.mob.getZ()) > 256 || !serverlevel.isLoaded(nearestOf)){
                    return null;
                }
                return serverlevel.structureManager().getStructureWithPieceAt(nearestOf, structureTagKey);
            }
        }catch (Exception e){
            AlexsMobs.LOGGER.warn(this.mob + " encountered an issue searching for a nearby structure.");
            errorCooldown = 2000 + this.mob.getRandom().nextInt(2000);
            return null;
        }
    }
}
