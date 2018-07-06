package mchorse.blockbuster.utils;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import mchorse.blockbuster.capabilities.recording.IRecording;
import mchorse.blockbuster.capabilities.recording.Recording;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.RecordPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

/**
 * Entity utilities
 *
 * Has some methods that relates to Minecraft entities.
 */
public class EntityUtils
{
    /**
     * Send status message. It's like chat, but it will be displayed 
     * over the hotbar and won't clutter the chat 
     */
    public static void sendStatusMessage(EntityPlayerMP player, ITextComponent message)
    {
        player.connection.sendPacket(new SPacketChat(message, (byte) 2));
    }

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
     * Get record player 
     */
    public static RecordPlayer getRecordPlayer(EntityLivingBase entity)
    {
        if (entity instanceof EntityActor)
        {
            return ((EntityActor) entity).playback;
        }
        else if (entity instanceof EntityPlayer)
        {
            IRecording record = Recording.get((EntityPlayer) entity);

            return record.getRecordPlayer();
        }

        return null;
    }

    /**
     * Set record player 
     */
    public static void setRecordPlayer(EntityLivingBase entity, RecordPlayer playback)
    {
        if (entity instanceof EntityActor)
        {
            ((EntityActor) entity).playback = playback;
        }
        else if (entity instanceof EntityPlayer)
        {
            IRecording record = Recording.get((EntityPlayer) entity);

            record.setRecordPlayer(playback);
        }
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
     */
    public static Entity getTargetEntity(Entity input, double maxReach)
    {
        double blockDistance = maxReach;

        RayTraceResult result = rayTrace(input, maxReach, 1.0F);
        Vec3d eyes = new Vec3d(input.posX, input.posY + input.getEyeHeight(), input.posZ);

        if (result != null)
        {
            blockDistance = result.hitVec.distanceTo(eyes);
        }

        Vec3d look = input.getLook(1.0F);
        Vec3d max = eyes.addVector(look.xCoord * maxReach, look.yCoord * maxReach, look.zCoord * maxReach);
        Entity target = null;

        float area = 1.0F;

        List<Entity> list = input.world.getEntitiesInAABBexcluding(input, input.getEntityBoundingBox().addCoord(look.xCoord * maxReach, look.yCoord * maxReach, look.zCoord * maxReach).expand(area, area, area), new Predicate<Entity>()
        {
            @Override
            public boolean apply(@Nullable Entity entity)
            {
                return entity != null && entity.canBeCollidedWith();
            }
        });

        double entityDistance = blockDistance;

        for (int i = 0; i < list.size(); ++i)
        {
            Entity entity = list.get(i);

            if (entity == input)
            {
                continue;
            }

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

    /**
     * This method is extracted from {@link Entity} class, because it was marked
     * as client side only code.
     */
    public static RayTraceResult rayTrace(Entity input, double blockReachDistance, float partialTicks)
    {
        Vec3d eyePos = new Vec3d(input.posX, input.posY + input.getEyeHeight(), input.posZ);
        Vec3d eyeDir = input.getLook(partialTicks);
        Vec3d eyeReach = eyePos.addVector(eyeDir.xCoord * blockReachDistance, eyeDir.yCoord * blockReachDistance, eyeDir.zCoord * blockReachDistance);

        return input.world.rayTraceBlocks(eyePos, eyeReach, false, false, true);
    }
}