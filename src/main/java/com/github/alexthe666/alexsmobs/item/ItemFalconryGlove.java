package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.message.MessageMosquitoDismount;
import com.github.alexthe666.alexsmobs.message.MessageSyncEntityPos;
import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ItemFalconryGlove extends Item {

    public ItemFalconryGlove(Properties properties) {
        super(properties);
    }


    public static void onLeftClick(PlayerEntity playerIn, ItemStack stack) {
        if(stack.getItem() == AMItemRegistry.FALCONRY_GLOVE){
            boolean flag = false;
            float dist = 128;
            Vector3d Vector3d = playerIn.getEyePosition(1.0F);
            Vector3d Vector3d1 = playerIn.getLook(1.0F);
            Vector3d Vector3d2 = Vector3d.add(Vector3d1.x * dist, Vector3d1.y * dist, Vector3d1.z * dist);
            double d1 = dist;
            Entity pointedEntity = null;
            List<Entity> list = playerIn.world.getEntitiesInAABBexcluding(playerIn, playerIn.getBoundingBox().expand(Vector3d1.x * dist, Vector3d1.y * dist, Vector3d1.z * dist).grow(1.0D, 1.0D, 1.0D), new Predicate<Entity>() {
                public boolean apply(@Nullable Entity entity) {
                    return entity != null && entity.canBeCollidedWith() && (entity instanceof PlayerEntity || (entity instanceof LivingEntity));
                }
            });
            double d2 = d1;
            for (int j = 0; j < list.size(); ++j) {
                Entity entity1 = list.get(j);
                AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow(entity1.getCollisionBorderSize());
                Optional<Vector3d> optional = axisalignedbb.rayTrace(Vector3d, Vector3d2);

                if (axisalignedbb.contains(Vector3d)) {
                    if (d2 >= 0.0D) {
                        //pointedEntity = entity1;
                        d2 = 0.0D;
                    }
                } else if (optional.isPresent()) {
                    double d3 = Vector3d.distanceTo(optional.get());

                    if (d3 < d2 || d2 == 0.0D) {
                        if (entity1.getLowestRidingEntity() == playerIn.getLowestRidingEntity() && !playerIn.canRiderInteract()) {
                            if (d2 == 0.0D) {
                                pointedEntity = entity1;
                            }
                        } else {
                            pointedEntity = entity1;
                            d2 = d3;
                        }
                    }
                }
            }

            if(!playerIn.getPassengers().isEmpty()){
                for(Entity entity : playerIn.getPassengers()){
                    if(entity instanceof EntityBaldEagle){
                        EntityBaldEagle eagle = (EntityBaldEagle)entity;
                        eagle.setLaunched(true);
                        eagle.dismount();
                        eagle.setSitting(false);
                        eagle.setCommand(0);
                        eagle.setLocationAndAngles(playerIn.getPosX(), playerIn.getPosYEye(), playerIn.getPosZ(), eagle.rotationYaw, eagle.rotationPitch);
                        if(eagle.world.isRemote){
                            AlexsMobs.sendMSGToServer(new MessageSyncEntityPos(eagle.getEntityId(), playerIn.getPosX(), playerIn.getPosYEye(), playerIn.getPosZ()));
                        }else{
                            AlexsMobs.sendMSGToAll(new MessageSyncEntityPos(eagle.getEntityId(), playerIn.getPosX(), playerIn.getPosYEye(), playerIn.getPosZ()));
                        }
                        if(eagle.hasCap()){
                            eagle.setFlying(true);
                            eagle.getMoveHelper().setMoveTo(eagle.getPosX(), eagle.getPosY(), eagle.getPosZ(), 0.1F);
                            if(eagle.world.isRemote){
                                AlexsMobs.sendMSGToServer(new MessageMosquitoDismount(eagle.getEntityId(), playerIn.getEntityId()));
                            }
                            AlexsMobs.PROXY.setRenderViewEntity(eagle);
                        }else{
                            eagle.getNavigator().clearPath();
                            eagle.getMoveHelper().setMoveTo(eagle.getPosX(), eagle.getPosY(), eagle.getPosZ(), 0.1F);
                            if(pointedEntity != null && !eagle.isOnSameTeam(pointedEntity)){
                                eagle.setFlying(true);
                                if(pointedEntity instanceof LivingEntity){
                                    eagle.setAttackTarget((LivingEntity) pointedEntity);
                                }
                            }else{
                                eagle.setFlying(false);
                                eagle.setCommand(2);
                                eagle.setSitting(true);
                            }
                        }
                    }
                }
            }
        }
    }

}
