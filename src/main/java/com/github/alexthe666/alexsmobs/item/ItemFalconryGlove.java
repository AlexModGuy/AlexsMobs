package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.IFalconry;
import com.github.alexthe666.alexsmobs.message.MessageSyncEntityPos;
import com.google.common.base.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ItemFalconryGlove extends Item implements ILeftClick {

    public ItemFalconryGlove(Properties properties) {
        super(properties);
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsMobs.PROXY.getISTERProperties());
    }

    public boolean onLeftClick(ItemStack stack, LivingEntity playerIn) {
        if(stack.getItem() == AMItemRegistry.FALCONRY_GLOVE.get()){
            boolean flag = false;
            float dist = 128;
            Vec3 Vector3d = playerIn.getEyePosition(1.0F);
            Vec3 Vector3d1 = playerIn.getViewVector(1.0F);
            Vec3 Vector3d2 = Vector3d.add(Vector3d1.x * dist, Vector3d1.y * dist, Vector3d1.z * dist);
            double d1 = dist;
            Entity pointedEntity = null;
            List<Entity> list = playerIn.level.getEntities(playerIn, playerIn.getBoundingBox().expandTowards(Vector3d1.x * dist, Vector3d1.y * dist, Vector3d1.z * dist).inflate(1.0D, 1.0D, 1.0D), new Predicate<Entity>() {
                public boolean apply(@Nullable Entity entity) {
                    return entity != null && entity.isPickable() && (entity instanceof Player || (entity instanceof LivingEntity));
                }
            });
            double d2 = d1;
            for (int j = 0; j < list.size(); ++j) {
                Entity entity1 = list.get(j);
                AABB axisalignedbb = entity1.getBoundingBox().inflate(entity1.getPickRadius());
                Optional<Vec3> optional = axisalignedbb.clip(Vector3d, Vector3d2);

                if (axisalignedbb.contains(Vector3d)) {
                    if (d2 >= 0.0D) {
                        //pointedEntity = entity1;
                        d2 = 0.0D;
                    }
                } else if (optional.isPresent()) {
                    double d3 = Vector3d.distanceTo(optional.get());

                    if (d3 < d2 || d2 == 0.0D) {
                        if (entity1.getRootVehicle() == playerIn.getRootVehicle() && !playerIn.canRiderInteract()) {
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
                    if(entity instanceof IFalconry && entity instanceof Animal){
                        IFalconry falcon = (IFalconry)entity;
                        Animal animal = (Animal)entity;
                        animal.removeVehicle();
                        animal.moveTo(playerIn.getX(), playerIn.getEyeY(), playerIn.getZ(), animal.getYRot(), animal.getXRot());
                        if(animal.level.isClientSide){
                            AlexsMobs.sendMSGToServer(new MessageSyncEntityPos(animal.getId(), playerIn.getX(), playerIn.getEyeY(), playerIn.getZ()));
                        }else{
                            AlexsMobs.sendMSGToAll(new MessageSyncEntityPos(animal.getId(), playerIn.getX(), playerIn.getEyeY(), playerIn.getZ()));
                        }
                        if(playerIn instanceof Player){
                            falcon.onLaunch((Player)playerIn, pointedEntity);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
