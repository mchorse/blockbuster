package mchorse.blockbuster.utils;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

/**
 * Entity utilities
 *
 * Has some methods that relates to Minecraft entities.
 */
public class EntityUtils
{
    /**
     * Get string pose key for entity state.
     */
    public static String poseForEntity(EntityLivingBase entity)
    {
        if (entity.isRiding())
        {
            return "riding";
        }

        if (entity.isSneaking())
        {
            return "sneaking";
        }

        return "standing";
    }

    /**
     * Simple method that decreases the need for writing additional
     * UUID.fromString line
     */
    public static Entity entityByUUID(World world, String id)
    {
        return entityByUUID(world, UUID.fromString(id));
    }

    /**
     * Get entity by UUID in the server world.
     *
     * Looked up on minecraft forge forum, I don't remember where's exactly...
     */
    public static Entity entityByUUID(World world, UUID target)
    {
        for (Object object : world.loadedEntityList)
        {
            if (object instanceof Entity)
            {
                Entity entity = (Entity) object;

                if (entity.getUniqueID().equals(target))
                {
                    return entity;
                }
            }
        }

        return null;
    }
}