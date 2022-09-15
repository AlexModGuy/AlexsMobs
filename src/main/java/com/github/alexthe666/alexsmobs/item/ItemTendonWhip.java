package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityTendonSegment;
import com.github.alexthe666.alexsmobs.entity.util.TendonWhipUtil;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

public class ItemTendonWhip extends SwordItem implements ILeftClick {

    private final ImmutableMultimap<Attribute, AttributeModifier> tendonModifiers;

    public ItemTendonWhip(Item.Properties props) {
        super(Tiers.IRON, 3, 0, props);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)4F, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)-3.0F, AttributeModifier.Operation.ADDITION));
        this.tendonModifiers = builder.build();
    }

    public static boolean isActive(ItemStack stack, LivingEntity holder) {
        if (holder != null && (holder.getMainHandItem() == stack || holder.getOffhandItem() == stack)) {
            return !TendonWhipUtil.canLaunchTendons(holder.level, holder);
        }
        return false;
    }


    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.tendonModifiers : super.getDefaultAttributeModifiers(slot);
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity entity, LivingEntity player) {
        launchTendonsAt(stack, player, entity);
        return super.hurtEnemy(stack, entity, player);
    }

    private boolean isCharged(Player player, ItemStack stack){
        return player.getAttackStrengthScale(0.5F) > 0.9F;
    }

    public boolean onLeftClick(ItemStack stack, LivingEntity playerIn){
        if(stack.is(AMItemRegistry.TENDON_WHIP.get()) && (!(playerIn instanceof Player) || isCharged((Player)playerIn, stack))){
            Level worldIn = playerIn.level;
            Entity closestValid = null;
            Vec3 playerEyes = playerIn.getEyePosition(1.0F);
            HitResult hitresult = worldIn.clip(new ClipContext(playerEyes, playerEyes.add(playerIn.getLookAngle().scale(10)), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, playerIn));
            if (hitresult instanceof EntityHitResult) {
                Entity entity = ((EntityHitResult) hitresult).getEntity();
                if (!entity.equals(playerIn) && !playerIn.isAlliedTo(entity) && !entity.isAlliedTo(playerIn) && entity instanceof Mob && playerIn.hasLineOfSight(entity)) {
                    closestValid = entity;
                }
            } else {
                for (Entity entity : worldIn.getEntitiesOfClass(LivingEntity.class, playerIn.getBoundingBox().inflate(12.0D))) {
                    if (!entity.equals(playerIn) && !playerIn.isAlliedTo(entity) && !entity.isAlliedTo(playerIn) && entity instanceof Mob && playerIn.hasLineOfSight(entity)) {
                        if (closestValid == null || playerIn.distanceTo(entity) < playerIn.distanceTo(closestValid)) {
                            closestValid = entity;
                        }
                    }
                }
            }
            return launchTendonsAt(stack, playerIn, closestValid);
        }
        return false;
    }

    public boolean launchTendonsAt(ItemStack stack, LivingEntity playerIn, Entity closestValid) {
        Level worldIn = playerIn.level;
        if (TendonWhipUtil.canLaunchTendons(worldIn, playerIn)) {
            TendonWhipUtil.retractFarTendons(worldIn, playerIn);
            if (!worldIn.isClientSide) {
                if (closestValid != null) {
                    EntityTendonSegment segment = AMEntityRegistry.TENDON_SEGMENT.get().create(worldIn);
                    worldIn.addFreshEntity(segment);
                    segment.setCreatorEntityUUID(playerIn.getUUID());
                    segment.setFromEntityID(playerIn.getId());
                    segment.setToEntityID(closestValid.getId());
                    segment.copyPosition(playerIn);
                    segment.setProgress(0.0F);
                    segment.setHasGlint(stack.hasFoil());
                    TendonWhipUtil.setLastTendon(playerIn, segment);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return toolAction != ToolActions.SWORD_SWEEP && super.canPerformAction(stack, toolAction);
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.sameItem(newStack);
    }

    public int getMaxDamage(ItemStack stack) {
        return 450;
    }

    public boolean isValidRepairItem(ItemStack pickaxe, ItemStack stack) {
        return stack.is(AMItemRegistry.ELASTIC_TENDON.get());
    }

}
