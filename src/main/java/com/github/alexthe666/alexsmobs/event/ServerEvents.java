package com.github.alexthe666.alexsmobs.event;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {

    private static final UUID SAND_SPEED_MODIFIER = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF28E");
    private static final AttributeModifier SAND_SPEED_BONUS = new AttributeModifier(SAND_SPEED_MODIFIER, "roadrunner speed bonus", 0.1F, AttributeModifier.Operation.ADDITION);

    @SubscribeEvent
    public void onUseItem(PlayerInteractEvent.RightClickItem event) {
        if(event.getItemStack().getItem() == Items.GLASS_BOTTLE && AMConfig.lavaBottleEnabled){
            RayTraceResult raytraceresult = rayTrace(event.getWorld(), event.getPlayer(), RayTraceContext.FluidMode.SOURCE_ONLY);
            if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
                BlockPos blockpos = ((BlockRayTraceResult)raytraceresult).getPos();
                if (event.getWorld().isBlockModifiable(event.getPlayer(), blockpos)) {
                    if (event.getWorld().getFluidState(blockpos).isTagged(FluidTags.LAVA)) {
                        event.getWorld().playSound(event.getPlayer(), event.getPlayer().getPosX(), event.getPlayer().getPosY(), event.getPlayer().getPosZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                        event.getPlayer().addStat(Stats.ITEM_USED.get(Items.GLASS_BOTTLE));
                        event.getPlayer().setFire(6);
                        if(!event.getPlayer().addItemStackToInventory(new ItemStack(AMItemRegistry.LAVA_BOTTLE))){
                            event.getPlayer().entityDropItem(new ItemStack(AMItemRegistry.LAVA_BOTTLE));
                        }
                        event.getPlayer().swingArm(event.getHand());
                        if(!event.getPlayer().isCreative()){
                            event.getItemStack().shrink(1);
                        }
                    }
                }
            }
        }

    }

    protected static BlockRayTraceResult rayTrace(World worldIn, PlayerEntity player, RayTraceContext.FluidMode fluidMode) {
        float f = player.rotationPitch;
        float f1 = player.rotationYaw;
        Vector3d vector3d = player.getEyePosition(1.0F);
        float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
        float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = player.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue();;
        Vector3d vector3d1 = vector3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
        return worldIn.rayTraceBlocks(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.OUTLINE, fluidMode, player));
    }

    @SubscribeEvent
    public void onEntityJoinWorld(LivingSpawnEvent.SpecialSpawn event) {
        try {
            if (event.getEntity() != null && event.getEntity() instanceof SpiderEntity && AMConfig.spidersAttackFlies) {
                SpiderEntity spider = (SpiderEntity) event.getEntity();
                spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal(spider, EntityFly.class, 1, true, false, null));
            }

        } catch (Exception e) {
            AlexsMobs.LOGGER.warn("Tried to add unique behaviors to vanilla mobs and encountered an error");
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (AMConfig.giveBookOnStartup) {
            CompoundNBT playerData = event.getPlayer().getPersistentData();
            CompoundNBT data = playerData.getCompound(PlayerEntity.PERSISTED_NBT_TAG);
            if (data != null && !data.getBoolean("alexsmobs_has_book")) {
                ItemHandlerHelper.giveItemToPlayer(event.getPlayer(), new ItemStack(AMItemRegistry.ANIMAL_DICTIONARY));
                data.putBoolean("alexsmobs_has_book", true);
                playerData.put(PlayerEntity.PERSISTED_NBT_TAG, data);
            }
        }
    }


    @SubscribeEvent
    public void onBlockBreakEvent(BlockEvent.BreakEvent event) {
        if (AMConfig.bananasDropFromLeaves && BlockTags.getCollection().get(AMTagRegistry.DROPS_BANANAS).contains(event.getWorld().getBlockState(event.getPos()).getBlock()) && event.getWorld() instanceof World) {
            if (event.getPlayer() == null || !event.getPlayer().isCreative()) {
                Random rand = new Random();
                int bonusLevel = 0;
                if (event.getPlayer() != null) {
                    bonusLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, event.getPlayer().getHeldItemMainhand());
                }
                int bananaStep = (int)Math.min(AMConfig.bananaChance * 0.1F, 0);
                int bananaRarity = AMConfig.bananaChance - (bonusLevel * bananaStep);
                if (bananaRarity < 1 || rand.nextInt(bananaRarity) == 0) {
                    ItemEntity itemEntity = new ItemEntity((World) event.getWorld(), event.getPos().getX() + 0.5D, event.getPos().getY() + 0.5D, event.getPos().getZ() + 0.5D, new ItemStack(AMItemRegistry.BANANA));
                    itemEntity.setDefaultPickupDelay();
                    event.getWorld().addEntity(itemEntity);
                }
            }
        }
    }


    @SubscribeEvent
    public void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        if(event.getEntityLiving() instanceof PlayerEntity){
            ModifiableAttributeInstance modifiableattributeinstance = event.getEntityLiving().getAttribute(Attributes.MOVEMENT_SPEED);
            if (event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.FEET).getItem() == AMItemRegistry.ROADDRUNNER_BOOTS || modifiableattributeinstance.hasModifier(SAND_SPEED_BONUS)) {
                boolean sand = event.getEntityLiving().world.getBlockState(getDownPos(event.getEntityLiving().getPosition(), event.getEntityLiving().world)).getBlock().isIn(BlockTags.SAND);
                if (sand && !modifiableattributeinstance.hasModifier(SAND_SPEED_BONUS)) {
                    modifiableattributeinstance.applyPersistentModifier(SAND_SPEED_BONUS);
                }
                if (event.getEntityLiving().ticksExisted % 25 == 0 && (event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.FEET).getItem() != AMItemRegistry.ROADDRUNNER_BOOTS || !sand) && modifiableattributeinstance.hasModifier(SAND_SPEED_BONUS)) {
                    modifiableattributeinstance.removeModifier(SAND_SPEED_BONUS);
                }
            }
        }

        if (event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.LEGS).getItem() == AMItemRegistry.CENTIPEDE_LEGGINGS) {
            if(event.getEntityLiving().collidedHorizontally && !event.getEntityLiving().isInWater()){
                event.getEntityLiving().fallDistance = 0.0F;
                Vector3d motion = event.getEntityLiving().getMotion();
                double d0 = MathHelper.clamp(motion.x, (double)-0.15F, (double)0.15F);
                double d1 = MathHelper.clamp(motion.z, (double)-0.15F, (double)0.15F);
                double d2 = 0.1D;
                if (d2 < 0.0D && !event.getEntityLiving().getBlockState().isScaffolding(event.getEntityLiving()) && event.getEntityLiving().hasStoppedClimbing()) {
                    d2 = 0.0D;
                }
                motion = new Vector3d(d0, d2, d1);
                event.getEntityLiving().setMotion(motion);
            }


        }
    }

    private BlockPos getDownPos(BlockPos entered, IWorld world){
        int i = 0;
        while (world.isAirBlock(entered) && i < 3){
            entered = entered.down();
            i++;
        }
        return entered;
    }
}