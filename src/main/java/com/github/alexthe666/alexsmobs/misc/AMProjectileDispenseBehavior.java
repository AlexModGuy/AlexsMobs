package com.github.alexthe666.alexsmobs.misc;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

/**
 * A custom ProjectileDispenseBehavior that doesn't require ProjectileItem.
 * This is needed in 1.21 because ProjectileDispenseBehavior now requires ProjectileItem.
 */
public abstract class AMProjectileDispenseBehavior extends DefaultDispenseItemBehavior {
    
    @Override
    public ItemStack execute(BlockSource source, ItemStack stack) {
        Level level = source.level();
        Position position = DispenserBlock.getDispensePosition(source);
        Direction direction = source.state().getValue(DispenserBlock.FACING);
        Projectile projectile = this.getProjectile(level, position, stack);
        projectile.shoot(direction.getStepX(), (direction.getStepY() + 0.1F), direction.getStepZ(), this.getPower(), this.getUncertainty());
        level.addFreshEntity(projectile);
        stack.shrink(1);
        return stack;
    }
    
    @Override
    protected void playSound(BlockSource source) {
        source.level().levelEvent(1002, source.pos(), 0);
    }
    
    /**
     * Return the projectile entity spawned by this dispense behavior.
     */
    protected abstract Projectile getProjectile(Level level, Position position, ItemStack stack);
    
    protected float getUncertainty() {
        return 6.0F;
    }
    
    protected float getPower() {
        return 1.1F;
    }
}
