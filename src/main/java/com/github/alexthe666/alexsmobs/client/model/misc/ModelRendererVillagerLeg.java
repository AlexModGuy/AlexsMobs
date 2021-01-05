package com.github.alexthe666.alexsmobs.client.model.misc;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelRendererVillagerLeg extends ModelRenderer {

    public boolean left;

    public ModelRendererVillagerLeg(Model model, int p_i225949_3_, int p_i225949_4_) {
        super(model, p_i225949_3_, p_i225949_4_);
    }

    public void render(MatrixStack stack, IVertexBuilder builder, int i, int j, float f1, float f2, float f3, float f4) {
        if(left){
            this.rotateAngleX = -1.4137167F;
            this.rotateAngleY = -0.31415927F;
            this.rotateAngleZ = -0.07853982F;
        }else{
            this.rotateAngleX = -1.4137167F;
            this.rotateAngleY = 0.31415927F;
            this.rotateAngleZ = 0.07853982F;
        }

        super.render(stack, builder, i, j, f1, f2, f3, f4);
    }
}
