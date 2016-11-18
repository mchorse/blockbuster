package mchorse.blockbuster.utils;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
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

        if (entity.isElytraFlying())
        {
            return "flying";
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
        for (Entity entity : world.loadedEntityList)
        {
            if (entity.getUniqueID().equals(target))
            {
                return entity;
            }
        }

        return null;
    }

    /**
     * Get the entity at which given player is looking at.
     * Taken from EntityRenderer class.
     *
     * That's a big method... Why Minecraft has lots of these big methods?
     */
    public static Entity getTargetEntity(Entity input)
    {
        double maxReach = 64;
        double blockDistance = maxReach;
        RayTraceResult result = input.rayTrace(maxReach, 1.0F);

        Vec3d eyes = input.getPositionEyes(1.0F);

        if (result != null)
        {
            blockDistance = result.hitVec.distanceTo(eyes);
        }

        Vec3d look = input.getLook(1.0F);
        Vec3d max = eyes.addVector(look.xCoord * maxReach, look.yCoord * maxReach, look.zCoord * maxReach);
        Entity target = null;

        float area = 1.0F;

        List<Entity> list = Minecraft.getMinecraft().theWorld.getEntitiesInAABBexcluding(input, input.getEntityBoundingBox().addCoord(look.xCoord * maxReach, look.yCoord * maxReach, look.zCoord * maxReach).expand(area, area, area), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
        {
            @Override
            public boolean apply(@Nullable Entity entity)
            {
                return entity != null && entity.canBeCollidedWith();
            }
        }));

        double entityDistance = blockDistance;

        for (int i = 0; i < list.size(); ++i)
        {
            Entity entity = list.get(i);
            AxisAlignedBB aabb = entity.getEntityBoundingBox().expandXyz(entity.getCollisionBorderSize());
            RayTraceResult intercept = aabb.calculateIntercept(eyes, max);

            if (aabb.isVecInside(eyes))
            {
                if (entityDistance >= 0.0D)
                {
                    target = entity;
                    entityDistance = 0.0D;
                }
            }
            else if (intercept != null)
            {
                double eyesDistance = eyes.distanceTo(intercept.hitVec);

                if (eyesDistance < entityDistance || entityDistance == 0.0D)
                {
                    if (entity.getLowestRidingEntity() == input.getLowestRidingEntity() && !input.canRiderInteract())
                    {
                        if (entityDistance == 0.0D)
                        {
                            target = entity;
                        }
                    }
                    else
                    {
                        target = entity;
                        entityDistance = eyesDistance;
                    }
                }
            }
        }

        return target;
    }
}