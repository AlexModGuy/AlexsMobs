package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityVoidPortal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class ItemShatteredDimensionalCarver extends ItemDimensionalCarver {

    public ItemShatteredDimensionalCarver(Properties props) {
        super(props);
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsMobs.PROXY.getISTERProperties());
    }

    @Override
    public void onPortalOpen(Level worldIn, LivingEntity player, EntityVoidPortal portal, Direction dir) {
        portal.setAttachmentFacing(dir);
        portal.setShattered(true);
        portal.setLifespan(2000);
        portal.exitDimension = worldIn.dimension();
        BlockPos playerPos = player.blockPosition();
        if (dir == Direction.DOWN) {
            portal.setDestination(new BlockPos(playerPos.getX(), worldIn.getMinBuildHeight() + 1, player.getZ()));
        } else if (dir == Direction.UP) {
            portal.setDestination(new BlockPos(playerPos.getX(), worldIn.getMaxBuildHeight() - 1, player.getZ()));
        }else{
            double worldBorderDistance = worldIn.getWorldBorder().getDistanceToBorder(playerPos.getX(), playerPos.getZ()) - 5D;
            BlockPos millionPos = playerPos.relative(dir.getOpposite(), (int) Math.min(worldBorderDistance, 1000000));
            portal.setDestination(millionPos);
        }
    }
}
