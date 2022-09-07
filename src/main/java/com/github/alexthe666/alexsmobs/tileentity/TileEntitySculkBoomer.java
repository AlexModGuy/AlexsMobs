package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.block.BlockSculkBoomer;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class TileEntitySculkBoomer extends BlockEntity implements GameEventListener {

    private final BlockPositionSource blockPosSource = new BlockPositionSource(this.worldPosition);
    private boolean prevOpen = false;
    private int screamTime = 0;

    public TileEntitySculkBoomer(BlockPos pos, BlockState state) {
        super(AMTileEntityRegistry.SCULK_BOOMER.get(), pos, state);
    }

    public static void commonTick(Level level, BlockPos pos, BlockState state, TileEntitySculkBoomer tileEntity) {
        boolean hasPower = false;
        if (state.getBlock() instanceof BlockSculkBoomer && !tileEntity.isRemoved()) {
            if(tileEntity.screamTime < 0 && !state.getValue(BlockSculkBoomer.POWERED)){
                AABB screamBox = new AABB(pos.getX() - 4, pos.getY() - 0.25F, pos.getZ() - 4, pos.getX() + 4, pos.getY() + 0.25F, pos.getZ() + 4F);
                level.setBlockAndUpdate(pos, state.setValue(BlockSculkBoomer.OPEN, true));
                tileEntity.screamTime++;
                if(tileEntity.screamTime >= 0){
                    tileEntity.screamTime = 100;
                    level.setBlockAndUpdate(pos, state.setValue(BlockSculkBoomer.OPEN, false));
                }
                float screamProgress = 1F - (tileEntity.screamTime / -20F);
                Vec3 center = screamBox.getCenter();
                for(LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, screamBox)){
                    double distance = 0.5F + entity.position().subtract(center).horizontalDistance();
                    if(distance < 4 * screamProgress && distance > 3.5F * screamProgress && !isOccluded(level, Vec3.atCenterOf(pos), entity.position())){
                        entity.hurt(DamageSource.MAGIC, 6 + entity.getRandom().nextInt(3));
                        entity.knockback(0.4F,  center.x - entity.getX(), center.z - entity.getZ());
                    }
                }
            }
            if(tileEntity.screamTime > 0){
                tileEntity.screamTime--;
            }
            boolean openNow = state.getValue(BlockSculkBoomer.OPEN);
            if(!tileEntity.prevOpen && openNow){
                SoundEvent sound = AMSoundRegistry.SCULK_BOOMER.get();
                if(level.getRandom().nextInt(100) == 0){
                    sound = AMSoundRegistry.SCULK_BOOMER_FART.get();
                }
                level.playSound((Player)null, pos, sound, SoundSource.BLOCKS, 4F, level.random.nextFloat() * 0.2F + 0.9F);
                level.addParticle(AMParticleRegistry.SKULK_BOOM.get(), pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0, 0, 0);
            }
            tileEntity.prevOpen = openNow;
        }
    }

    public void tick() {


    }

    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("ScreamCooldown", 99)) {
            this.screamTime = tag.getInt("ScreamCooldown");
        }
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("ScreamCooldown", this.screamTime);
    }

    @Override
    public boolean handleEventsImmediately() {
        return true;
    }

    @Override
    public PositionSource getListenerSource() {
        return blockPosSource;
    }

    @Override
    public int getListenerRadius() {
        return 8;
    }

    @Override
    public boolean handleGameEvent(ServerLevel serverLevel, GameEvent.Message message) {
        if(message.gameEvent() == GameEvent.SCULK_SENSOR_TENDRILS_CLICKING && !isOccluded(serverLevel, Vec3.atCenterOf(this.getBlockPos()), message.source())){
            double distance = message.source().distanceTo(Vec3.atCenterOf(this.getBlockPos()));
            serverLevel.sendParticles(new VibrationParticleOption(new BlockPositionSource(this.getBlockPos()), Mth.floor(distance)), message.source().x, message.source().y, message.source().z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            if(screamTime == 0){
                screamTime = -20;
            }
        }
        return false;
    }

    private static boolean isOccluded(Level level, Vec3 vec1, Vec3 vec2) {
        Vec3 vec3 = new Vec3((double) Mth.floor(vec1.x) + 0.5D, (double)Mth.floor(vec1.y) + 0.5D, (double)Mth.floor(vec1.z) + 0.5D);
        Vec3 vec31 = new Vec3((double)Mth.floor(vec2.x) + 0.5D, (double)Mth.floor(vec2.y) + 0.5D, (double)Mth.floor(vec2.z) + 0.5D);

        for(Direction direction : Direction.values()) {
            Vec3 vec32 = vec3.relative(direction, (double)1.0E-5F);
            if (level.isBlockInLine(new ClipBlockStateContext(vec32, vec31, (p_223780_) -> {
                return p_223780_.is(BlockTags.OCCLUDES_VIBRATION_SIGNALS);
            })).getType() != HitResult.Type.BLOCK) {
                return false;
            }
        }
        return true;
    }

}
