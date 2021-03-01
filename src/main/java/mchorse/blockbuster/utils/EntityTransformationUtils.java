package mchorse.blockbuster.utils;

import net.minecraft.entity.Entity;

/**
 * This is a utils class to avoid reflection to get asm injected variables of entity class.
 */

public class EntityTransformationUtils
{
    /* LEAVE THE RETURN TYPES 0 or the core asm transformation will fail!*/

    public static double getPrevPrevPosX(Entity entity)
    {
        return 0;
    }

    public static double getPrevPrevPosY(Entity entity)
    {
        return 0;
    }

    public static double getPrevPrevPosZ(Entity entity)
    {
        return 0;
    }
}
