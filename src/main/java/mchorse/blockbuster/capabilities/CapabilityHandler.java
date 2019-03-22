package mchorse.blockbuster.capabilities;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.aperture.network.common.PacketAperture;
import mchorse.blockbuster.capabilities.recording.RecordingProvider;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.structure.PacketStructureList;
import mchorse.blockbuster.network.server.ServerHandlerStructureRequest;
import mchorse.blockbuster.recording.RecordPlayer;
import mchorse.blockbuster.recording.Utils;
import mchorse.blockbuster.utils.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

/**
 * Capability handler class
 *
 * This class is responsible for managing capabilities, i.e. attaching
 * capabilities and syncing values on the client.
 */
public class CapabilityHandler
{
    public static final ResourceLocation RECORDING_CAP = new ResourceLocation(Blockbuster.MODID, "recording_capability");

    /**
     * Attach capabilities (well, only one, right now)
     */
    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public void attachCapability(AttachCapabilitiesEvent.Entity event)
    {
        if (!(event.getEntity() instanceof EntityPlayer)) return;

        event.addCapability(RECORDING_CAP, new RecordingProvider());
    }

    /**
     * When player logs in, sent him his server counter partner's values.
     */
    @SubscribeEvent
    public void playerLogsIn(PlayerLoggedInEvent event)
    {
        EntityPlayerMP player = (EntityPlayerMP) event.player;

        /* Do something? */
        if (CameraHandler.isApertureLoaded())
        {
            Dispatcher.sendTo(new PacketAperture(), player);
        }

        Dispatcher.sendTo(new PacketStructureList(ServerHandlerStructureRequest.getAllStructures()), player);
    }

    /**
     * When player starts tracking an actor, server has to send actor's record
     * frames to the player
     */
    @SubscribeEvent
    public void playerStartsTracking(StartTracking event)
    {
        Entity target = event.getTarget();
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();

        if (target instanceof EntityActor)
        {
            EntityActor actor = (EntityActor) target;

            if (actor.isPlaying())
            {
                Utils.sendRequestedRecord(actor.getEntityId(), actor.playback.record.filename, player);
            }
        }

        if (target instanceof EntityPlayer)
        {
            EntityPlayer other = (EntityPlayer) target;
            RecordPlayer playback = EntityUtils.getRecordPlayer(other);

            if (playback != null)
            {
                Utils.sendRequestedRecord(other.getEntityId(), playback.record.filename, player);
            }
        }
    }
}