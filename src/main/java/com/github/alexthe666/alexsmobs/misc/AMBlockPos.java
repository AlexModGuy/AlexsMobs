package com.github.alexthe666.alexsmobs.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class AMBlockPos {
    public static BlockPos get(double x, double y, double z) {
        return new BlockPos((int) x, (int) y, (int) z);
    }

    public static BlockPos get(final Vec3 vec3) {
        return get(vec3.x, vec3.y, vec3.z);
    }

    @Deprecated
    public static BlockPos fromCoords(double x, double y, double z) {
        return get(x, y, z);
    }

    @Deprecated
    public static BlockPos fromVec3(final Vec3 vec3) {
        return fromCoords(vec3.x, vec3.y, vec3.z);
    }
}
