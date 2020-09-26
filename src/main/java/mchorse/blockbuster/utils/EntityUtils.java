package mchorse.blockbuster.utils;

import mchorse.blockbuster.capabilities.recording.IRecording;
import mchorse.blockbuster.capabilities.recording.Recording;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.RecordPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.UUID;

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
        player.sendStatusMessage(message, true);
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
}