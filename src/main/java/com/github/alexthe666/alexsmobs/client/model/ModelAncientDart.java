package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class ModelAncientDart extends AdvancedEntityModel<Entity> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox main;
	private final AdvancedModelBox feathers;
	private final AdvancedModelBox cube_r1;
	private final AdvancedModelBox cube_r2;

	public ModelAncientDart() {
		texWidth = 32;
		texHeight = 32;

		root = new AdvancedModelBox(this, "root");
		root.setPos(0.0F, 0.0F, 0.0F);


		main = new AdvancedModelBox(this, "main");
		main.setPos(0.0F, -1.0F, 0.0F);
		root.addChild(main);
		main.setTextureOffset(11, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
		main.setTextureOffset(0, 0).addBox(-0.5F, -0.5F, -5.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);

		feathers = new AdvancedModelBox(this, "feathers");
		feathers.setPos(0.0F, 1.0F, 1.0F);
		main.addChild(feathers);


		cube_r1 = new AdvancedModelBox(this, "cube_r1");
		cube_r1.setPos(0.0F, -1.0F, 0.5F);
		feathers.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, 0.0F, 0.7854F);
		cube_r1.setTextureOffset(0, 6).addBox(0.0F, -1.5F, -0.5F, 0.0F, 3.0F, 3.0F, 0.0F, false);

		cube_r2 = new AdvancedModelBox(this, "cube_r2");
		cube_r2.setPos(0.0F, -1.0F, 0.5F);
		feathers.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, 0.0F, -0.7854F);
		cube_r2.setTextureOffset(7, 6).addBox(0.0F, -1.5F, -0.5F, 0.0F, 3.0F, 3.0F, 0.0F, false);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, main, feathers, cube_r1, cube_r2);
	}

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
	}

	@Override
	public void setupAnim(Entity entity, float v, float v1, float v2, float v3, float v4) {

	}

	public void setRotationAngle(AdvancedModelBox modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}