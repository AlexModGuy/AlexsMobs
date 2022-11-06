package com.github.alexthe666.alexsmobs.entity.ai;

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

    public MonsterAIWalkThroughHallsOfStructure(PathfinderMob mob, double speed, int chance, TagKey<Structure> structureTagKey, double maximumDistance) {
        super(mob, speed, chance, false);
        this.structureTagKey = structureTagKey;
        this.maximumDistance = 50;
    }

    @Nullable
    protected Vec3 getPosition() {
        StructureStart start = getNearestStructure(mob.blockPosition());
        if(start.isValid()){
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

    private StructureStart getNearestStructure(BlockPos pos){
        ServerLevel serverlevel = (ServerLevel)this.mob.level;
        StructureStart start = serverlevel.structureManager().getStructureWithPieceAt(pos, structureTagKey);
        if(start.isValid()){
            return start;
        }else{
            BlockPos nearestOf = serverlevel.findNearestMapStructure(structureTagKey, pos, (int)this.maximumDistance, false);
            if(nearestOf == null){
                return StructureStart.INVALID_START;
            }
            return serverlevel.structureManager().getStructureWithPieceAt(nearestOf, structureTagKey);
        }
    }
}
