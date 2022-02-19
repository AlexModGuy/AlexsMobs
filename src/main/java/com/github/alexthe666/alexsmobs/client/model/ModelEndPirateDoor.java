package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateDoor;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityVoidWormBeak;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class ModelEndPirateDoor extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox doorRightHinge;
    private final AdvancedModelBox doorLeftHinge;

    public ModelEndPirateDoor() {
        texWidth = 64;
        texHeight = 64;
        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);
        doorRightHinge = new AdvancedModelBox(this);
        doorRightHinge.setRotationPoint(7.0F, -24.0F, -7.0F);
        root.addChild(doorRightHinge);
        doorRightHinge.setTextureOffset(0, 0).addBox(-15.0F, -24.0F, -1.0F, 16.0F, 48.0F, 2.0F, 0.0F, false);

        doorLeftHinge = new AdvancedModelBox(this);
        doorLeftHinge.setRotationPoint(-7.0F, -24.0F, -7.0F);
        root.addChild(doorLeftHinge);
        doorLeftHinge.setTextureOffset(0, 0).addBox(-1.0F, -24.0F, -1.0F, 16.0F, 48.0F, 2.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, doorRightHinge, doorLeftHinge);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    public void renderDoor(TileEntityEndPirateDoor door, float partialTick, boolean left) {
        this.resetToDefaultPose();
        float ageInTicks = door.ticksExisted + partialTick;
        float openAmount = door.getOpenProgress(partialTick);
        double d = Math.sin(ageInTicks * 0.8F) - 0.5F;
        float wiggle = (float) (door.getWiggleProgress(partialTick) * d * Math.PI * 0.1F);
        if(left){
            this.doorRightHinge.showModel = false;
            this.doorLeftHinge.showModel = true;
        }else{
            this.doorRightHinge.showModel = true;
            this.doorLeftHinge.showModel = false;
        }
        this.doorRightHinge.rotateAngleY += openAmount * Math.PI * 0.5F + wiggle;
        this.doorLeftHinge.rotateAngleY -= openAmount * Math.PI * 0.5F + wiggle;
    }

}
