package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class EntityCentipedeTail extends EntityCentipedeBody {

    protected EntityCentipedeTail(EntityType type, World worldIn) {
        super(type, worldIn);
    }
    
    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.ARTHROPOD;
    }

}
