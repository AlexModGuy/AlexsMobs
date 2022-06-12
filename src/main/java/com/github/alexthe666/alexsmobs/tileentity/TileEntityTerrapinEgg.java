package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.entity.EntityTerrapin;
import com.github.alexthe666.alexsmobs.entity.util.TerrapinTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.RandomSource;

import java.util.Random;

public class TileEntityTerrapinEgg extends BlockEntity {
    public ParentData parent1;
    public ParentData parent2;

    public TileEntityTerrapinEgg(BlockPos pos, BlockState state) {
        super(AMTileEntityRegistry.TERRAPIN_EGG.get(), pos, state);
    }

    public void addAttributesToOffspring(EntityTerrapin baby, RandomSource random){
        if(parent1 != null && parent2 != null){
            baby.setTurtleType(random.nextBoolean() ? parent1.type : parent2.type);
            baby.setShellType(random.nextBoolean() ? parent1.shellType : parent2.shellType);
            baby.setSkinType(random.nextBoolean() ? parent1.skinType : parent2.skinType);
            baby.setTurtleColor((parent1.turtleColor + parent2.turtleColor) / 2);
            baby.setShellColor((parent1.shellColor + parent2.shellColor) / 2);
            baby.setSkinColor((parent1.skinColor + parent2.skinColor) / 2);
            if(random.nextFloat() < 0.15F){
                baby.setTurtleType(TerrapinTypes.OVERLAY);
                switch (random.nextInt(2)){
                    case 0:
                        baby.setTurtleColor((int) (0xFFFFFF * random.nextFloat()));
                        break;
                    case 1:
                        baby.setShellColor((int) (0xFFFFFF * random.nextFloat()));
                        break;
                    case 2:
                        baby.setSkinColor((int) (0xFFFFFF * random.nextFloat()));
                        break;
                }
            }
        }
    }


    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if(compound.contains("Parent1Data")){
            this.parent1 = new ParentData(compound.getCompound("Parent1Data"));
        }
        if(compound.contains("Parent2Data")){
            this.parent2 = new ParentData(compound.getCompound("Parent2Data"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        if(this.parent1 != null){
            CompoundTag tag = new CompoundTag();
            parent1.writeToNBT(tag);
            compound.put("Parent1Data", tag);
        }
        if(this.parent2 != null){
            CompoundTag tag = new CompoundTag();
            parent2.writeToNBT(tag);
            compound.put("Parent2Data", tag);
        }
    }

    public static class ParentData {
        public TerrapinTypes type;
        public int shellType;
        public int skinType;
        public int turtleColor;
        public int shellColor;
        public int skinColor;

        public ParentData(TerrapinTypes type, int shellType, int skinType, int turtleColor, int shellColor, int skinColor) {
            this.type = type;
            this.shellType = shellType;
            this.skinType = skinType;
            this.turtleColor = turtleColor;
            this.shellColor = shellColor;
            this.skinColor = skinColor;
        }

        public ParentData(CompoundTag tag){
            this(TerrapinTypes.values()[Mth.clamp(tag.getInt("TerrapinType"), 0, TerrapinTypes.values().length - 1)],
                    tag.getInt("ShellType"),
                    tag.getInt("SkinType"),
                    tag.getInt("TurtleColor"),
                    tag.getInt("ShellColor"),
                    tag.getInt("SkinColor")
                    );
        }

        public boolean canMerge(ParentData other){
            if(type == TerrapinTypes.OVERLAY && other.type == TerrapinTypes.OVERLAY){
                return turtleColor == other.turtleColor && shellType == other.shellType && skinType == other.skinType && shellColor == other.shellColor && skinColor == other.skinColor;
            }
            return other.type == this.type;
        }

        public void writeToNBT(CompoundTag tag){
            tag.putInt("TerrapinType", type.ordinal());
            tag.putInt("ShellType", shellType);
            tag.putInt("SkinType", skinType);
            tag.putInt("TurtleColor", turtleColor);
            tag.putInt("ShellColor", shellColor);
            tag.putInt("SkinColor", skinColor);

        }
    }

}
