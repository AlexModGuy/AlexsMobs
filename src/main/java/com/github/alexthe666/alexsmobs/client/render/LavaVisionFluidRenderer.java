package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LavaVisionFluidRenderer extends LiquidBlockRenderer {

    /* Copied from the vanilla superclass */
    private static boolean isFaceOccludedByNeighbor(BlockGetter p_239283_0_, BlockPos p_239283_1_, Direction p_239283_2_, float p_239283_3_) {
        BlockPos blockpos = p_239283_1_.relative(p_239283_2_);
        BlockState blockstate = p_239283_0_.getBlockState(blockpos);
        return isFaceOccludedByState(p_239283_0_, p_239283_2_, p_239283_3_, blockpos, blockstate);
    }

    private static boolean isFaceOccludedByState(BlockGetter p_239284_0_, Direction p_239284_1_, float p_239284_2_, BlockPos p_239284_3_, BlockState p_239284_4_) {
        if (p_239284_4_.canOcclude()) {
            VoxelShape voxelshape = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, p_239284_2_, 1.0D);
            VoxelShape voxelshape1 = p_239284_4_.getOcclusionShape(p_239284_0_, p_239284_3_);
            return Shapes.blockOccudes(voxelshape, voxelshape1, p_239284_1_);
        } else {
            return false;
        }
    }

    private static boolean isAdjacentFluidSameAs(BlockGetter worldIn, BlockPos pos, Direction side, FluidState state) {
        BlockPos blockpos = pos.relative(side);
        FluidState fluidstate = worldIn.getFluidState(blockpos);
        return fluidstate.getType().isSame(state.getType());
    }

    @Override
    public void tesselate(BlockAndTintGetter lightReaderIn, BlockPos posIn, VertexConsumer vertexBuilderIn, BlockState blockstateIn, FluidState fluidStateIn) {
        try {
            if (fluidStateIn.is(FluidTags.LAVA)) {
                boolean flag = fluidStateIn.is(FluidTags.LAVA);
                TextureAtlasSprite[] atextureatlassprite = net.minecraftforge.client.ForgeHooksClient.getFluidSprites(lightReaderIn, posIn, fluidStateIn);
                int i = net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions.of(fluidStateIn).getTintColor(fluidStateIn, lightReaderIn, posIn);
                float alpha = (float) AMConfig.lavaOpacity;
                float f = (float)(i >> 16 & 255) / 255.0F;
                float f1 = (float)(i >> 8 & 255) / 255.0F;
                float f2 = (float)(i & 255) / 255.0F;
                BlockState blockstate = lightReaderIn.getBlockState(posIn.relative(Direction.DOWN));
                FluidState fluidstate = blockstate.getFluidState();
                BlockState blockstate1 = lightReaderIn.getBlockState(posIn.relative(Direction.UP));
                FluidState fluidstate1 = blockstate1.getFluidState();
                BlockState blockstate2 = lightReaderIn.getBlockState(posIn.relative(Direction.NORTH));
                FluidState fluidstate2 = blockstate2.getFluidState();
                BlockState blockstate3 = lightReaderIn.getBlockState(posIn.relative(Direction.SOUTH));
                FluidState fluidstate3 = blockstate3.getFluidState();
                BlockState blockstate4 = lightReaderIn.getBlockState(posIn.relative(Direction.WEST));
                FluidState fluidstate4 = blockstate4.getFluidState();
                BlockState blockstate5 = lightReaderIn.getBlockState(posIn.relative(Direction.EAST));
                FluidState fluidstate5 = blockstate5.getFluidState();
                boolean flag1 = !isNeighborSameFluidVanilla(fluidStateIn, fluidstate1);
                boolean flag2 = shouldRenderFace(lightReaderIn, posIn, fluidStateIn, blockstateIn, Direction.DOWN, fluidstate) && !isFaceOccludedByNeighborVanilla(lightReaderIn, posIn, Direction.DOWN, 0.8888889F, blockstate);
                boolean flag3 = shouldRenderFace(lightReaderIn, posIn, fluidStateIn, blockstateIn, Direction.NORTH, fluidstate2);
                boolean flag4 = shouldRenderFace(lightReaderIn, posIn, fluidStateIn, blockstateIn, Direction.SOUTH, fluidstate3);
                boolean flag5 = shouldRenderFace(lightReaderIn, posIn, fluidStateIn, blockstateIn, Direction.WEST, fluidstate4);
                boolean flag6 = shouldRenderFace(lightReaderIn, posIn, fluidStateIn, blockstateIn, Direction.EAST, fluidstate5);
                if (!flag1 && !flag2 && !flag6 && !flag5 && !flag3 && !flag4) {
                    return;
                } else {
                    boolean flag7 = false;
                    float f3 = lightReaderIn.getShade(Direction.DOWN, true);
                    float f4 = lightReaderIn.getShade(Direction.UP, true);
                    float f5 = lightReaderIn.getShade(Direction.NORTH, true);
                    float f6 = lightReaderIn.getShade(Direction.WEST, true);
                    Fluid fluid = fluidStateIn.getType();
                    float f11 = this.getFluidHeight(lightReaderIn, posIn, fluid);
                    float f7;
                    float f8;
                    float f9;
                    float f10;
                    if (f11 >= 1.0F) {
                        f7 = 1.0F;
                        f8 = 1.0F;
                        f9 = 1.0F;
                        f10 = 1.0F;
                    } else {
                        float f12 = this.getHeight(lightReaderIn, fluid, posIn.north(), blockstate2, fluidstate2);
                        float f13 = this.getHeight(lightReaderIn, fluid, posIn.south(), blockstate3, fluidstate3);
                        float f14 = this.getHeight(lightReaderIn, fluid, posIn.east(), blockstate5, fluidstate5);
                        float f15 = this.getHeight(lightReaderIn, fluid, posIn.west(), blockstate4, fluidstate4);
                        f7 = this.calculateAverageHeight(lightReaderIn, fluid, f11, f12, f14, posIn.relative(Direction.NORTH).relative(Direction.EAST));
                        f8 = this.calculateAverageHeight(lightReaderIn, fluid, f11, f12, f15, posIn.relative(Direction.NORTH).relative(Direction.WEST));
                        f9 = this.calculateAverageHeight(lightReaderIn, fluid, f11, f13, f14, posIn.relative(Direction.SOUTH).relative(Direction.EAST));
                        f10 = this.calculateAverageHeight(lightReaderIn, fluid, f11, f13, f15, posIn.relative(Direction.SOUTH).relative(Direction.WEST));
                    }

                    double d1 = (double)(posIn.getX() & 15);
                    double d2 = (double)(posIn.getY() & 15);
                    double d0 = (double)(posIn.getZ() & 15);
                    float f16 = 0.001F;
                    float f17 = flag2 ? 0.001F : 0.0F;
                    if (flag1 && !isFaceOccludedByNeighborVanilla(lightReaderIn, posIn, Direction.UP, Math.min(Math.min(f8, f10), Math.min(f9, f7)), blockstate1)) {
                        flag7 = true;
                        f8 -= 0.001F;
                        f10 -= 0.001F;
                        f9 -= 0.001F;
                        f7 -= 0.001F;
                        Vec3 vec3 = fluidStateIn.getFlow(lightReaderIn, posIn);
                        float f18;
                        float f19;
                        float f20;
                        float f21;
                        float f22;
                        float f23;
                        float f24;
                        float f25;
                        if (vec3.x == 0.0D && vec3.z == 0.0D) {
                            TextureAtlasSprite textureatlassprite1 = atextureatlassprite[0];
                            f18 = textureatlassprite1.getU(0.0D);
                            f22 = textureatlassprite1.getV(0.0D);
                            f19 = f18;
                            f23 = textureatlassprite1.getV(16.0D);
                            f20 = textureatlassprite1.getU(16.0D);
                            f24 = f23;
                            f21 = f20;
                            f25 = f22;
                        } else {
                            TextureAtlasSprite textureatlassprite = atextureatlassprite[1];
                            float f26 = (float)Mth.atan2(vec3.z, vec3.x) - ((float)Math.PI / 2F);
                            float f27 = Mth.sin(f26) * 0.25F;
                            float f28 = Mth.cos(f26) * 0.25F;
                            float f29 = 8.0F;
                            f18 = textureatlassprite.getU((double)(8.0F + (-f28 - f27) * 16.0F));
                            f22 = textureatlassprite.getV((double)(8.0F + (-f28 + f27) * 16.0F));
                            f19 = textureatlassprite.getU((double)(8.0F + (-f28 + f27) * 16.0F));
                            f23 = textureatlassprite.getV((double)(8.0F + (f28 + f27) * 16.0F));
                            f20 = textureatlassprite.getU((double)(8.0F + (f28 + f27) * 16.0F));
                            f24 = textureatlassprite.getV((double)(8.0F + (f28 - f27) * 16.0F));
                            f21 = textureatlassprite.getU((double)(8.0F + (f28 - f27) * 16.0F));
                            f25 = textureatlassprite.getV((double)(8.0F + (-f28 - f27) * 16.0F));
                        }

                        float f49 = (f18 + f19 + f20 + f21) / 4.0F;
                        float f50 = (f22 + f23 + f24 + f25) / 4.0F;
                        float f51 = (float)atextureatlassprite[0].getWidth() / (atextureatlassprite[0].getU1() - atextureatlassprite[0].getU0());
                        float f52 = (float)atextureatlassprite[0].getHeight() / (atextureatlassprite[0].getV1() - atextureatlassprite[0].getV0());
                        float f53 = 4.0F / Math.max(f52, f51);
                        f18 = Mth.lerp(f53, f18, f49);
                        f19 = Mth.lerp(f53, f19, f49);
                        f20 = Mth.lerp(f53, f20, f49);
                        f21 = Mth.lerp(f53, f21, f49);
                        f22 = Mth.lerp(f53, f22, f50);
                        f23 = Mth.lerp(f53, f23, f50);
                        f24 = Mth.lerp(f53, f24, f50);
                        f25 = Mth.lerp(f53, f25, f50);
                        int j = this.getCombinedAverageLight(lightReaderIn, posIn);
                        float f30 = f4 * f;
                        float f31 = f4 * f1;
                        float f32 = f4 * f2;

                        this.vertexVanilla(vertexBuilderIn, d1 + 0.0D, d2 + (double)f8, d0 + 0.0D, f30, f31, f32, alpha, f18, f22, j);
                        this.vertexVanilla(vertexBuilderIn, d1 + 0.0D, d2 + (double)f10, d0 + 1.0D, f30, f31, f32, alpha, f19, f23, j);
                        this.vertexVanilla(vertexBuilderIn, d1 + 1.0D, d2 + (double)f9, d0 + 1.0D, f30, f31, f32, alpha, f20, f24, j);
                        this.vertexVanilla(vertexBuilderIn, d1 + 1.0D, d2 + (double)f7, d0 + 0.0D, f30, f31, f32, alpha, f21, f25, j);
                        if (fluidStateIn.shouldRenderBackwardUpFace(lightReaderIn, posIn.above())) {
                            this.vertexVanilla(vertexBuilderIn, d1 + 0.0D, d2 + (double)f8, d0 + 0.0D, f30, f31, f32, alpha, f18, f22, j);
                            this.vertexVanilla(vertexBuilderIn, d1 + 1.0D, d2 + (double)f7, d0 + 0.0D, f30, f31, f32, alpha, f21, f25, j);
                            this.vertexVanilla(vertexBuilderIn, d1 + 1.0D, d2 + (double)f9, d0 + 1.0D, f30, f31, f32, alpha, f20, f24, j);
                            this.vertexVanilla(vertexBuilderIn, d1 + 0.0D, d2 + (double)f10, d0 + 1.0D, f30, f31, f32, alpha, f19, f23, j);
                        }
                    }

                    if (flag2) {
                        float f40 = atextureatlassprite[0].getU0();
                        float f41 = atextureatlassprite[0].getU1();
                        float f42 = atextureatlassprite[0].getV0();
                        float f43 = atextureatlassprite[0].getV1();
                        int l = this.getCombinedAverageLight(lightReaderIn, posIn.below());
                        float f46 = f3 * f;
                        float f47 = f3 * f1;
                        float f48 = f3 * f2;

                        this.vertexVanilla(vertexBuilderIn, d1, d2 + (double)f17, d0 + 1.0D, f46, f47, f48, alpha, f40, f43, l);
                        this.vertexVanilla(vertexBuilderIn, d1, d2 + (double)f17, d0, f46, f47, f48, alpha, f40, f42, l);
                        this.vertexVanilla(vertexBuilderIn, d1 + 1.0D, d2 + (double)f17, d0, f46, f47, f48, alpha, f41, f42, l);
                        this.vertexVanilla(vertexBuilderIn, d1 + 1.0D, d2 + (double)f17, d0 + 1.0D, f46, f47, f48, alpha, f41, f43, l);
                        flag7 = true;
                    }

                    int k = this.getCombinedAverageLight(lightReaderIn, posIn);

                    for(Direction direction : Direction.Plane.HORIZONTAL) {
                        float f44;
                        float f45;
                        double d3;
                        double d4;
                        double d5;
                        double d6;
                        boolean flag8;
                        switch(direction) {
                            case NORTH:
                                f44 = f8;
                                f45 = f7;
                                d3 = d1;
                                d5 = d1 + 1.0D;
                                d4 = d0 + (double)0.001F;
                                d6 = d0 + (double)0.001F;
                                flag8 = flag3;
                                break;
                            case SOUTH:
                                f44 = f9;
                                f45 = f10;
                                d3 = d1 + 1.0D;
                                d5 = d1;
                                d4 = d0 + 1.0D - (double)0.001F;
                                d6 = d0 + 1.0D - (double)0.001F;
                                flag8 = flag4;
                                break;
                            case WEST:
                                f44 = f10;
                                f45 = f8;
                                d3 = d1 + (double)0.001F;
                                d5 = d1 + (double)0.001F;
                                d4 = d0 + 1.0D;
                                d6 = d0;
                                flag8 = flag5;
                                break;
                            default:
                                f44 = f7;
                                f45 = f9;
                                d3 = d1 + 1.0D - (double)0.001F;
                                d5 = d1 + 1.0D - (double)0.001F;
                                d4 = d0;
                                d6 = d0 + 1.0D;
                                flag8 = flag6;
                        }

                        if (flag8 && !isFaceOccludedByNeighborVanilla(lightReaderIn, posIn, direction, Math.max(f44, f45), lightReaderIn.getBlockState(posIn.relative(direction)))) {
                            flag7 = true;
                            BlockPos blockpos = posIn.relative(direction);
                            TextureAtlasSprite textureatlassprite2 = atextureatlassprite[1];
                            if (atextureatlassprite[2] != null) {
                                if (lightReaderIn.getBlockState(blockpos).shouldDisplayFluidOverlay(lightReaderIn, blockpos, fluidStateIn)) {
                                    textureatlassprite2 = atextureatlassprite[2];
                                }
                            }

                            float f54 = textureatlassprite2.getU(0.0D);
                            float f55 = textureatlassprite2.getU(8.0D);
                            float f33 = textureatlassprite2.getV((double)((1.0F - f44) * 16.0F * 0.5F));
                            float f34 = textureatlassprite2.getV((double)((1.0F - f45) * 16.0F * 0.5F));
                            float f35 = textureatlassprite2.getV(8.0D);
                            float f36 = direction.getAxis() == Direction.Axis.Z ? f5 : f6;
                            float f37 = f4 * f36 * f;
                            float f38 = f4 * f36 * f1;
                            float f39 = f4 * f36 * f2;

                            this.vertexVanilla(vertexBuilderIn, d3, d2 + (double)f44, d4, f37, f38, f39, alpha, f54, f33, k);
                            this.vertexVanilla(vertexBuilderIn, d5, d2 + (double)f45, d6, f37, f38, f39, alpha, f55, f34, k);
                            this.vertexVanilla(vertexBuilderIn, d5, d2 + (double)f17, d6, f37, f38, f39, alpha, f55, f35, k);
                            this.vertexVanilla(vertexBuilderIn, d3, d2 + (double)f17, d4, f37, f38, f39, alpha, f54, f35, k);
                            if (false) {
                                this.vertexVanilla(vertexBuilderIn, d3, d2 + (double)f17, d4, f37, f38, f39, alpha, f54, f35, k);
                                this.vertexVanilla(vertexBuilderIn, d5, d2 + (double)f17, d6, f37, f38, f39, alpha, f55, f35, k);
                                this.vertexVanilla(vertexBuilderIn, d5, d2 + (double)f45, d6, f37, f38, f39, alpha, f55, f34, k);
                                this.vertexVanilla(vertexBuilderIn, d3, d2 + (double)f44, d4, f37, f38, f39, alpha, f54, f33, k);
                            }
                        }
                    }

                    return;
                }
            } else {
                 super.tesselate(lightReaderIn, posIn, vertexBuilderIn, blockstateIn, fluidStateIn);
            }
        } catch (Exception e) {
        }
    }

    private void vertexVanilla(VertexConsumer vertexBuilderIn, double x, double y, double z, float red, float green, float blue, float alpha, float u, float v, int packedLight) {
        vertexBuilderIn.vertex(x, y, z).color(red, green, blue, alpha).uv(u, v).uv2(packedLight).normal(0.0F, 1.0F, 0.0F).endVertex();
    }

    private int getCombinedAverageLight(BlockAndTintGetter lightReaderIn, BlockPos posIn) {
        int i = LevelRenderer.getLightColor(lightReaderIn, posIn);
        int j = LevelRenderer.getLightColor(lightReaderIn, posIn.above());
        int k = i & 255;
        int l = j & 255;
        int i1 = i >> 16 & 255;
        int j1 = j >> 16 & 255;
        return (k > l ? k : l) | (i1 > j1 ? i1 : j1) << 16;
    }

    private float getFluidHeight(BlockGetter reader, BlockPos pos, Fluid fluidIn) {
        int i = 0;
        float f = 0.0F;

        for (int j = 0; j < 4; ++j) {
            BlockPos blockpos = pos.offset(-(j & 1), 0, -(j >> 1 & 1));
            if (reader.getFluidState(blockpos.above()).getType().isSame(fluidIn)) {
                return 1.0F;
            }

            FluidState fluidstate = reader.getFluidState(blockpos);
            if (fluidstate.getType().isSame(fluidIn)) {
                float f1 = fluidstate.getHeight(reader, blockpos);
                if (f1 >= 0.8F) {
                    f += f1 * 10.0F;
                    i += 10;
                } else {
                    f += f1;
                    ++i;
                }
            } else if (!reader.getBlockState(blockpos).getMaterial().isSolid()) {
                ++i;
            }
        }

        return f / (float) i;
    }

    private static boolean isNeighborSameFluidVanilla(FluidState p_203186_, FluidState p_203187_) {
        return p_203187_.getType().isSame(p_203186_.getType());
    }

    private static boolean isFaceOccludedByStateVanilla(BlockGetter p_110979_, Direction p_110980_, float p_110981_, BlockPos p_110982_, BlockState p_110983_) {
        if (p_110983_.canOcclude()) {
            VoxelShape voxelshape = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, (double)p_110981_, 1.0D);
            VoxelShape voxelshape1 = p_110983_.getOcclusionShape(p_110979_, p_110982_);
            return Shapes.blockOccudes(voxelshape, voxelshape1, p_110980_);
        } else {
            return false;
        }
    }

    private static boolean isFaceOccludedByNeighborVanilla(BlockGetter p_203180_, BlockPos p_203181_, Direction p_203182_, float p_203183_, BlockState p_203184_) {
        return isFaceOccludedByStateVanilla(p_203180_, p_203182_, p_203183_, p_203181_.relative(p_203182_), p_203184_);
    }

    private static boolean isFaceOccludedBySelfVanilla(BlockGetter p_110960_, BlockPos p_110961_, BlockState p_110962_, Direction p_110963_) {
        return isFaceOccludedByStateVanilla(p_110960_, p_110963_.getOpposite(), 1.0F, p_110961_, p_110962_);
    }

    private float calculateAverageHeight(BlockAndTintGetter p_203150_, Fluid p_203151_, float p_203152_, float p_203153_, float p_203154_, BlockPos p_203155_) {
        if (!(p_203154_ >= 1.0F) && !(p_203153_ >= 1.0F)) {
            float[] afloat = new float[2];
            if (p_203154_ > 0.0F || p_203153_ > 0.0F) {
                float f = this.getHeight(p_203150_, p_203151_, p_203155_);
                if (f >= 1.0F) {
                    return 1.0F;
                }

                this.addWeightedHeight(afloat, f);
            }

            this.addWeightedHeight(afloat, p_203152_);
            this.addWeightedHeight(afloat, p_203154_);
            this.addWeightedHeight(afloat, p_203153_);
            return afloat[0] / afloat[1];
        } else {
            return 1.0F;
        }
    }

    private void addWeightedHeight(float[] p_203189_, float p_203190_) {
        if (p_203190_ >= 0.8F) {
            p_203189_[0] += p_203190_ * 10.0F;
            p_203189_[1] += 10.0F;
        } else if (p_203190_ >= 0.0F) {
            p_203189_[0] += p_203190_;
            p_203189_[1] += 1.0F;
        }

    }

    private float getHeight(BlockAndTintGetter p_203157_, Fluid p_203158_, BlockPos p_203159_) {
        BlockState blockstate = p_203157_.getBlockState(p_203159_);
        return this.getHeight(p_203157_, p_203158_, p_203159_, blockstate, blockstate.getFluidState());
    }

    private float getHeight(BlockAndTintGetter p_203161_, Fluid p_203162_, BlockPos p_203163_, BlockState p_203164_, FluidState p_203165_) {
        if (p_203162_.isSame(p_203165_.getType())) {
            BlockState blockstate = p_203161_.getBlockState(p_203163_.above());
            return p_203162_.isSame(blockstate.getFluidState().getType()) ? 1.0F : p_203165_.getOwnHeight();
        } else {
            return !p_203164_.getMaterial().isSolid() ? 0.0F : -1.0F;
        }
    }
}

