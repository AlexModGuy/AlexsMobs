package com.github.alexthe666.alexsmobs.event;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {

    @SubscribeEvent
    public void onEntityJoinWorld(LivingSpawnEvent.SpecialSpawn event) {
        try {
            if (event.getEntity() != null && event.getEntity() instanceof SpiderEntity) {
                SpiderEntity spider = (SpiderEntity) event.getEntity();
                spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal(spider, EntityFly.class, 1, true, false, null));
            }

        } catch (Exception e) {
            AlexsMobs.LOGGER.warn("Tried to add unique behaviors to vanilla mobs and encountered an error");
        }
    }

    @SubscribeEvent
    public void onBlockBreakEvent(BlockEvent.BreakEvent event) {
        if (BlockTags.getCollection().get(AMTagRegistry.DROPS_BANANAS).contains(event.getWorld().getBlockState(event.getPos()).getBlock()) && event.getWorld() instanceof World) {
            if(event.getPlayer() == null || !event.getPlayer().isCreative()){
                Random rand = new Random();
                int bonusLevel = 0;
                if(event.getPlayer() != null){
                    bonusLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, event.getPlayer().getHeldItemMainhand());
                }
                int bananaRarity = 200 - (bonusLevel * 20);
                if(bananaRarity < 1 || rand.nextInt(bananaRarity) == 0){
                    ItemEntity itemEntity = new ItemEntity((World) event.getWorld(), event.getPos().getX() + 0.5D, event.getPos().getY() + 0.5D, event.getPos().getZ() + 0.5D, new ItemStack(AMItemRegistry.BANANA));
                    itemEntity.setDefaultPickupDelay();
                    event.getWorld().addEntity(itemEntity);
                }
            }
        }
    }
}