package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityCentipedeBody;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.LivingEntity;

public class ModelCaveCentipede<T extends LivingEntity> extends AdvancedEntityModel<T> {
    private final int type;//0 = head, 1 = body, 2 = tail
    private final AdvancedModelBox root;
    private AdvancedModelBox body;
    private AdvancedModelBox leftLegBodyF;
    private AdvancedModelBox leftLeg2BodyF;
    private AdvancedModelBox rightLegBodyF;
    private AdvancedModelBox rightLegBodyF2;
    private AdvancedModelBox leftLegBodyB;
    private AdvancedModelBox leftLeg2BodyB;
    private AdvancedModelBox rightLegBodyB;
    private AdvancedModelBox rightLegBodyB2;

    private AdvancedModelBox tail;
    private AdvancedModelBox leftLegTailF;
    private AdvancedModelBox leftLeg2TailF;

    private AdvancedModelBox rightLegTailF;
    private AdvancedModelBox rightLeg2TailF;

    private AdvancedModelBox leftLegTailB;
    private AdvancedModelBox leftLegTailB2;
    private AdvancedModelBox rightLegTailB;
    private AdvancedModelBox rightLegTailB2;
    private AdvancedModelBox leftTail;
    private AdvancedModelBox leftTailEnd;
    private AdvancedModelBox rightTail;
    private AdvancedModelBox rightTailEnd;

    private AdvancedModelBox head;
    private AdvancedModelBox head2;
    private AdvancedModelBox fangs;
    private AdvancedModelBox antenna_left;
    private AdvancedModelBox antenna_left_r1;
    private AdvancedModelBox antenna_right;
    private AdvancedModelBox antenna_right_r1;

    public ModelCaveCentipede(int type) {
        texWidth = 128;
        texHeight = 128;
        this.type = type;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 21.0F);

        switch (type) {
            case 0://head

                head = new AdvancedModelBox(this, "        head");
                head.setRotationPoint(0.0F, -7.875F, -20.625F);
                root.addChild(head);
                head.setTextureOffset(0, 62).addBox(-7.0F, -3.125F, -5.375F, 14.0F, 7.0F, 13.0F, 0.0F, false);

                head2 = new AdvancedModelBox(this, "        head2");
                head2.setRotationPoint(0.0F, -2.125F, -6.375F);
                head.addChild(head2);
                head2.setTextureOffset(0, 0).addBox(-2.0F, -1.0F, -1.0F, 4.0F, 2.0F, 2.0F, 0.0F, false);

                antenna_left = new AdvancedModelBox(this, "        antenna_left");
                antenna_left.setRotationPoint(1.2F, -2.125F, -5.775F);
                head.addChild(antenna_left);
                setRotationAngle(antenna_left, -0.2618F, 0.48F, -0.2618F);


                antenna_left_r1 = new AdvancedModelBox(this, "        antenna_left_r1");
                antenna_left_r1.setRotationPoint(0.5F, 0.0F, 0.0F);
                antenna_left.addChild(antenna_left_r1);
                setRotationAngle(antenna_left_r1, 0.1309F, 0.0F, 0.0873F);
                antenna_left_r1.setTextureOffset(55, 17).addBox(-1.0F, 0.0F, -1.0F, 23.0F, 0.0F, 10.0F, 0.0F, false);

                antenna_right = new AdvancedModelBox(this, "        antenna_right");
                antenna_right.setRotationPoint(-1.2F, -2.125F, -5.775F);
                head.addChild(antenna_right);
                setRotationAngle(antenna_right, -0.2618F, -0.48F, 0.2618F);


                antenna_right_r1 = new AdvancedModelBox(this, "        antenna_right_r1");
                antenna_right_r1.setRotationPoint(-0.5F, 0.0F, 0.0F);
                antenna_right.addChild(antenna_right_r1);
                setRotationAngle(antenna_right_r1, 0.1309F, 0.0F, -0.0873F);
                antenna_right_r1.setTextureOffset(55, 17).addBox(-22.0F, 0.0F, -1.0F, 23.0F, 0.0F, 10.0F, 0.0F, true);

                fangs = new AdvancedModelBox(this, "        fangs");
                fangs.setRotationPoint(0.0F, 1.875F, -6.375F);
                head.addChild(fangs);
                fangs.setTextureOffset(62, 28).addBox(-7.0F, 0.0F, -5.0F, 14.0F, 0.0F, 6.0F, 0.0F, false);
                break;
            case 1:
                body = new AdvancedModelBox(this, "        body");
                body.setRotationPoint(0.0F, -7.6F, -21.0F);
                root.addChild(body);
                body.setTextureOffset(0, 0).addBox(-8.0F, -5.4F, -8.0F, 16.0F, 10.0F, 16.0F, 0.0F, false);

                leftLegBodyF = new AdvancedModelBox(this, "        leftLegBodyF");
                leftLegBodyF.setRotationPoint(7.6F, 3.6F, 5.0F);
                body.addChild(leftLegBodyF);
                setRotationAngle(leftLegBodyF, 0.0F, 0.0F, -0.5672F);
                leftLegBodyF.setTextureOffset(42, 62).addBox(0.0F, -2.0F, -1.0F, 10.0F, 3.0F, 2.0F, 0.0F, false);

                leftLeg2BodyF = new AdvancedModelBox(this, "        leftLeg2BodyF");
                leftLeg2BodyF.setRotationPoint(9.1F, 0.5F, 0.1F);
                leftLegBodyF.addChild(leftLeg2BodyF);
                setRotationAngle(leftLeg2BodyF, 0.0F, 0.0F, 1.4835F);
                leftLeg2BodyF.setTextureOffset(0, 53).addBox(-5.0F, -4.0F, -0.1F, 15.0F, 6.0F, 0.0F, 0.0F, false);

                rightLegBodyF = new AdvancedModelBox(this, "        rightLegBodyF");
                rightLegBodyF.setRotationPoint(-7.6F, 3.6F, 5.0F);
                body.addChild(rightLegBodyF);
                setRotationAngle(rightLegBodyF, 0.0F, 0.0F, 0.5672F);
                rightLegBodyF.setTextureOffset(42, 62).addBox(-10.0F, -2.0F, -1.0F, 10.0F, 3.0F, 2.0F, 0.0F, true);

                rightLegBodyF2 = new AdvancedModelBox(this, "        rightLegBodyF2");
                rightLegBodyF2.setRotationPoint(-9.1F, 0.5F, 0.1F);
                rightLegBodyF.addChild(rightLegBodyF2);
                setRotationAngle(rightLegBodyF2, 0.0F, 0.0F, -1.4835F);
                rightLegBodyF2.setTextureOffset(0, 53).addBox(-10.0F, -4.0F, -0.1F, 15.0F, 6.0F, 0.0F, 0.0F, true);

                leftLegBodyB = new AdvancedModelBox(this, "        leftLegBodyB");
                leftLegBodyB.setRotationPoint(7.6F, 3.6F, -5.0F);
                body.addChild(leftLegBodyB);
                setRotationAngle(leftLegBodyB, 0.0F, 0.0F, -0.5672F);
                leftLegBodyB.setTextureOffset(42, 62).addBox(0.0F, -2.0F, -1.0F, 10.0F, 3.0F, 2.0F, 0.0F, false);

                leftLeg2BodyB = new AdvancedModelBox(this, "        leftLeg2BodyB");
                leftLeg2BodyB.setRotationPoint(9.1F, 0.5F, 0.1F);
                leftLegBodyB.addChild(leftLeg2BodyB);
                setRotationAngle(leftLeg2BodyB, 0.0F, 0.0F, 1.4835F);
                leftLeg2BodyB.setTextureOffset(0, 53).addBox(-5.0F, -4.0F, -0.1F, 15.0F, 6.0F, 0.0F, 0.0F, false);

                rightLegBodyB = new AdvancedModelBox(this, "        rightLegBodyB");
                rightLegBodyB.setRotationPoint(-7.6F, 3.6F, -5.0F);
                body.addChild(rightLegBodyB);
                setRotationAngle(rightLegBodyB, 0.0F, 0.0F, 0.5672F);
                rightLegBodyB.setTextureOffset(42, 62).addBox(-10.0F, -2.0F, -1.0F, 10.0F, 3.0F, 2.0F, 0.0F, true);

                rightLegBodyB2 = new AdvancedModelBox(this, "        rightLegBodyB2");
                rightLegBodyB2.setRotationPoint(-9.1F, 0.5F, 0.1F);
                rightLegBodyB.addChild(rightLegBodyB2);
                setRotationAngle(rightLegBodyB2, 0.0F, 0.0F, -1.4835F);
                rightLegBodyB2.setTextureOffset(0, 53).addBox(-10.0F, -4.0F, -0.1F, 15.0F, 6.0F, 0.0F, 0.0F, true);
                break;
            case 2:
                tail = new AdvancedModelBox(this, "        tail");
                tail.setRotationPoint(0.0F, -7.6F, -21.0F);
                root.addChild(tail);
                tail.setTextureOffset(0, 27).addBox(-7.0F, -4.2F, -8.0F, 14.0F, 9.0F, 16.0F, 0.0F, false);

                leftLegTailF = new AdvancedModelBox(this, "        leftLegTailF");
                leftLegTailF.setRotationPoint(6.6F, 3.6F, -5.0F);
                tail.addChild(leftLegTailF);
                setRotationAngle(leftLegTailF, 0.2269F, -0.1833F, -0.5585F);
                leftLegTailF.setTextureOffset(42, 62).addBox(0.0F, -2.0F, -1.0F, 10.0F, 3.0F, 2.0F, 0.0F, false);

                leftLeg2TailF = new AdvancedModelBox(this, "        leftLeg2TailF");
                leftLeg2TailF.setRotationPoint(9.1F, 0.5F, 0.1F);
                leftLegTailF.addChild(leftLeg2TailF);
                setRotationAngle(leftLeg2TailF, 0.0F, 0.0F, 1.4835F);
                leftLeg2TailF.setTextureOffset(0, 53).addBox(-5.0F, -4.0F, 0.0F, 15.0F, 6.0F, 0.0F, 0.0F, false);

                rightLegTailF = new AdvancedModelBox(this, "        rightLegTailF");
                rightLegTailF.setRotationPoint(-6.6F, 3.6F, -5.0F);
                tail.addChild(rightLegTailF);
                setRotationAngle(rightLegTailF, 0.2269F, 0.1833F, 0.5585F);
                rightLegTailF.setTextureOffset(42, 62).addBox(-10.0F, -2.0F, -1.0F, 10.0F, 3.0F, 2.0F, 0.0F, true);

                rightLeg2TailF = new AdvancedModelBox(this, "        rightLeg2TailF");
                rightLeg2TailF.setRotationPoint(-9.1F, 0.5F, 0.1F);
                rightLegTailF.addChild(rightLeg2TailF);
                setRotationAngle(rightLeg2TailF, 0.0F, 0.0F, -1.4835F);
                rightLeg2TailF.setTextureOffset(0, 53).addBox(-10.0F, -4.0F, 0.0F, 15.0F, 6.0F, 0.0F, 0.0F, true);

                leftLegTailB = new AdvancedModelBox(this, "        leftLegTailB");
                leftLegTailB.setRotationPoint(6.6F, 3.6F, 4.0F);
                tail.addChild(leftLegTailB);
                setRotationAngle(leftLegTailB, 0.4977F, -0.6749F, -0.7314F);
                leftLegTailB.setTextureOffset(42, 62).addBox(0.0F, -2.0F, -1.0F, 10.0F, 3.0F, 2.0F, 0.0F, false);

                leftLegTailB2 = new AdvancedModelBox(this, "        leftLegTailB2");
                leftLegTailB2.setRotationPoint(9.1F, 0.5F, 0.1F);
                leftLegTailB.addChild(leftLegTailB2);
                setRotationAngle(leftLegTailB2, 0.0F, 0.0F, 1.4835F);
                leftLegTailB2.setTextureOffset(0, 53).addBox(-5.0F, -4.0F, 0.0F, 15.0F, 6.0F, 0.0F, 0.0F, false);

                rightLegTailB = new AdvancedModelBox(this, "        rightLegTailB");
                rightLegTailB.setRotationPoint(-6.6F, 3.6F, 4.0F);
                tail.addChild(rightLegTailB);
                setRotationAngle(rightLegTailB, 0.4977F, 0.6749F, 0.7314F);
                rightLegTailB.setTextureOffset(42, 62).addBox(-10.0F, -2.0F, -1.0F, 10.0F, 3.0F, 2.0F, 0.0F, true);

                rightLegTailB2 = new AdvancedModelBox(this, "        rightLegTailB2");
                rightLegTailB2.setRotationPoint(-9.1F, 0.5F, 0.1F);
                rightLegTailB.addChild(rightLegTailB2);
                setRotationAngle(rightLegTailB2, 0.0F, 0.0F, -1.4835F);
                rightLegTailB2.setTextureOffset(0, 53).addBox(-10.0F, -4.0F, 0.0F, 15.0F, 6.0F, 0.0F, 0.0F, true);

                leftTail = new AdvancedModelBox(this, "        leftTail");
                leftTail.setRotationPoint(2.5F, -0.1F, 8.0F);
                tail.addChild(leftTail);
                setRotationAngle(leftTail, 0.3054F, 0.3927F, 0.0F);
                leftTail.setTextureOffset(62, 35).addBox(-0.5F, -1.1F, -1.0F, 2.0F, 3.0F, 12.0F, 0.0F, false);

                leftTailEnd = new AdvancedModelBox(this, "        leftTailEnd");
                leftTailEnd.setRotationPoint(0.0F, 0.0F, 11.0F);
                leftTail.addChild(leftTailEnd);
                setRotationAngle(leftTailEnd, -0.5672F, 0.0F, 0.0F);
                leftTailEnd.setTextureOffset(38, 30).addBox(0.5F, -1.1F, -1.0F, 0.0F, 8.0F, 23.0F, 0.0F, false);

                rightTail = new AdvancedModelBox(this, "        rightTail");
                rightTail.setRotationPoint(-2.5F, -0.1F, 8.0F);
                tail.addChild(rightTail);
                setRotationAngle(rightTail, 0.3054F, -0.3927F, 0.0F);
                rightTail.setTextureOffset(62, 35).addBox(-1.5F, -1.1F, -1.0F, 2.0F, 3.0F, 12.0F, 0.0F, true);

                rightTailEnd = new AdvancedModelBox(this, "        rightTailEnd");
                rightTailEnd.setRotationPoint(0.0F, 0.0F, 11.0F);
                rightTail.addChild(rightTailEnd);
                setRotationAngle(rightTailEnd, -0.5672F, 0.0F, 0.0F);
                rightTailEnd.setTextureOffset(38, 30).addBox(-0.5F, -1.1F, -1.0F, 0.0F, 8.0F, 23.0F, 0.0F, true);
                break;
        }
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTick = ageInTicks - entity.tickCount;
        float walkSpeed = 1.5F;
        float walkDegree = 0.85F;
        float idleSpeed = 0.25F;
        float idleDegree = 0.35F;
        if(entity.deathTime > 0){
            limbSwing = ageInTicks;
            limbSwingAmount = 1;
        }
        if(type == 0){
            this.swing(antenna_left, idleSpeed, idleDegree, true, 1, -0.1F, ageInTicks, 1);
            this.swing(antenna_right, idleSpeed, idleDegree, false, 1, -0.1F, ageInTicks, 1);
            this.swing(fangs, idleSpeed, idleDegree * 0.1F, false, 0, 0, ageInTicks, 1);
            this.fangs.rotationPointZ = -6.2F;
        }else if(type == 1){
            if(entity instanceof EntityCentipedeBody){
                float offset = (float) ((((EntityCentipedeBody)entity).getBodyIndex() + 1 ) * Math.PI * 0.5F);
                double walkOffset = (offset ) * Math.PI * 0.5F;
                this.swing(leftLegBodyF, walkSpeed, walkDegree, true, offset, 0F, limbSwing, limbSwingAmount);
                this.flap(leftLeg2BodyF, walkSpeed, walkDegree * 0.5F, true, offset, 0.1F, limbSwing, limbSwingAmount);
                this.swing(leftLegBodyB, walkSpeed, walkDegree, true, offset + 0.5F, 0F, limbSwing, limbSwingAmount);
                this.flap(leftLeg2BodyB, walkSpeed, walkDegree * 0.5F, true, offset + 0.5F, 0.1F, limbSwing, limbSwingAmount);
                this.swing(rightLegBodyF, walkSpeed, walkDegree, false, offset, 0F, limbSwing, limbSwingAmount);
                this.flap(rightLegBodyF2, walkSpeed, walkDegree * 0.5F, false, offset, 0.1F, limbSwing, limbSwingAmount);
                this.swing(rightLegBodyB, walkSpeed, walkDegree, false, offset + 0.5F, 0F, limbSwing, limbSwingAmount);
                this.flap(rightLegBodyB2, walkSpeed, walkDegree * 0.5F, false, offset + 0.5F, 0.1F, limbSwing, limbSwingAmount);
                this.body.rotationPointY += (float)(Math.sin( (double)(limbSwing * walkSpeed) - walkOffset) * (double)limbSwingAmount * (double)walkDegree - (double)(limbSwingAmount * walkDegree) );
                this.body.rotationPointY += (float)(Math.sin( (double)(ageInTicks * 0.1) - walkOffset) * (double)0.01 );

            }

        }else{
            if(entity instanceof EntityCentipedeBody) {
                float offset = (float) ((((EntityCentipedeBody) entity).getBodyIndex() + 1) * Math.PI * 0.5F);
                double walkOffset = (offset ) * Math.PI * 0.5F;
                this.swing(leftLegTailF, walkSpeed, walkDegree, true, offset, 0F, limbSwing, limbSwingAmount);
                this.flap(leftLeg2TailF, walkSpeed, walkDegree * 0.5F, true, offset, 0.1F, limbSwing, limbSwingAmount);
                this.swing(leftLegTailB, walkSpeed, walkDegree, true, offset + 0.5F, 0F, limbSwing, limbSwingAmount);
                this.flap(leftLegTailB2, walkSpeed, walkDegree * 0.5F, true, offset + 0.5F, 0.1F, limbSwing, limbSwingAmount);

                this.swing(rightLegTailF, walkSpeed, walkDegree, false, offset, 0F, limbSwing, limbSwingAmount);
                this.flap(rightLeg2TailF, walkSpeed, walkDegree * 0.5F, false, offset, 0.1F, limbSwing, limbSwingAmount);
                this.swing(rightLegTailB, walkSpeed, walkDegree, false, offset + 0.5F, 0F, limbSwing, limbSwingAmount);
                this.flap(rightLegTailB2, walkSpeed, walkDegree * 0.5F, false, offset + 0.5F, 0.1F, limbSwing, limbSwingAmount);
                this.tail.rotationPointY += (float)(Math.sin( (double)(limbSwing * walkSpeed) - walkOffset) * (double)limbSwingAmount * (double)walkDegree - (double)(limbSwingAmount * walkDegree) );
                this.tail.rotationPointY += (float)(Math.sin( (double)(ageInTicks * 0.1) - walkOffset) * (double)0.01 );
                this.swing(leftTail, walkSpeed, walkDegree * 0.2F, true, offset + 1F, 0F, limbSwing, limbSwingAmount);
                this.swing(rightTail, walkSpeed, walkDegree * 0.2F, false, offset + 1F, 0F, limbSwing, limbSwingAmount);
                this.walk(leftTail, idleSpeed, idleDegree, true, offset + 1.5F, -0.5F, ageInTicks, 1);
                this.walk(rightTail, idleSpeed, idleDegree, false, offset + 1.5F, 0.5F, ageInTicks, 1);
            }
        }
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return switch (type) {
            case 0 -> ImmutableList.of(root, head, head2, fangs, antenna_left, antenna_left_r1, antenna_right, antenna_right_r1);
            case 1 -> ImmutableList.of(root, body, leftLegBodyF, leftLeg2BodyF, rightLegBodyF, rightLegBodyF2, leftLegBodyB, leftLeg2BodyB, rightLegBodyB, rightLegBodyB2);
            case 2 -> ImmutableList.of(root, tail, leftLegTailF, leftLeg2TailF, rightLegTailF, rightLeg2TailF, leftLegTailB, leftLegTailB2, rightLegTailB, rightLegTailB2, leftTail, leftTailEnd, rightTail, rightTailEnd);
            default -> ImmutableList.of(root);
        };
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}