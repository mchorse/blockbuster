package mchorse.blockbuster.capabilities;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.capabilities.recording.IRecording;
import mchorse.blockbuster.capabilities.recording.Recording;
import mchorse.blockbuster.capabilities.recording.RecordingProvider;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.scene.PacketSceneCast;
import mchorse.blockbuster.network.common.structure.PacketStructureList;
import mchorse.blockbuster.network.server.ServerHandlerStructureRequest;
import mchorse.blockbuster.recording.RecordPlayer;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.scene.Scene;
import mchorse.blockbuster.recording.scene.SceneLocation;
import mchorse.blockbuster.utils.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
    public static final ResourceLocation RECORDING_CAP = new ResourceLocation(Blockbuster.MOD_ID, "recording_capability");

    /**
     * Attach capabilities (well, only one, right now)
     */
    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public void attachPlayerCapability(AttachCapabilitiesEvent<Entity> event)
    {
        if (!(event.getObject() instanceof EntityPlayer))
        {
            return;
        }

        event.addCapability(RECORDING_CAP, new RecordingProvider());
    }

    /**
     * When player logs in, sent him his server counter partner's values.
     */
    @SubscribeEvent
    public void playerLogsIn(PlayerLoggedInEvent event)
    {
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        IRecording recording = Recording.get(player);

        Dispatcher.sendTo(new PacketStructureList(ServerHandlerStructureRequest.getAllStructures()), player);

        if (recording.getLastScene() != null)
        {
            Scene scene = CommonProxy.scenes.get(recording.getLastScene(), player.world);

            if (scene != null)
            {
                Dispatcher.sendTo(new PacketSceneCast(new SceneLocation(scene)).open(false), player);
            }
        }
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

        if (target instanceof EntityLivingBase)
        {
            RecordPlayer playback = EntityUtils.getRecordPlayer((EntityLivingBase) target);

            if (playback != null)
            {
                RecordUtils.sendRequestedRecord(target.getEntityId(), playback.record.filename, player);
            }
        }
    }
}