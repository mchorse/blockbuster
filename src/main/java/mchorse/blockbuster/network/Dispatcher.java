package mchorse.blockbuster.network;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.network.client.ClientHandlerActorPause;
import mchorse.blockbuster.network.client.ClientHandlerCaption;
import mchorse.blockbuster.network.client.ClientHandlerModifyActor;
import mchorse.blockbuster.network.client.ClientHandlerModifyModelBlock;
import mchorse.blockbuster.network.client.director.ClientHandlerConfirmBreak;
import mchorse.blockbuster.network.client.director.ClientHandlerDirectorCast;
import mchorse.blockbuster.network.client.recording.ClientHandlerFrames;
import mchorse.blockbuster.network.client.recording.ClientHandlerPlayback;
import mchorse.blockbuster.network.client.recording.ClientHandlerPlayerRecording;
import mchorse.blockbuster.network.client.recording.ClientHandlerRequestedFrames;
import mchorse.blockbuster.network.client.recording.ClientHandlerSyncTick;
import mchorse.blockbuster.network.client.recording.ClientHandlerUnloadFrames;
import mchorse.blockbuster.network.client.recording.ClientHandlerUnloadRecordings;
import mchorse.blockbuster.network.common.PacketActorPause;
import mchorse.blockbuster.network.common.PacketActorRotate;
import mchorse.blockbuster.network.common.PacketCaption;
import mchorse.blockbuster.network.common.PacketModifyActor;
import mchorse.blockbuster.network.common.PacketModifyModelBlock;
import mchorse.blockbuster.network.common.PacketReloadModels;
import mchorse.blockbuster.network.common.PacketTickMarker;
import mchorse.blockbuster.network.common.director.PacketConfirmBreak;
import mchorse.blockbuster.network.common.director.PacketDirectorCast;
import mchorse.blockbuster.network.common.director.PacketDirectorRequestCast;
import mchorse.blockbuster.network.common.director.sync.PacketDirectorGoto;
import mchorse.blockbuster.network.common.director.sync.PacketDirectorPlay;
import mchorse.blockbuster.network.common.recording.PacketFramesChunk;
import mchorse.blockbuster.network.common.recording.PacketFramesLoad;
import mchorse.blockbuster.network.common.recording.PacketPlayback;
import mchorse.blockbuster.network.common.recording.PacketPlayerRecording;
import mchorse.blockbuster.network.common.recording.PacketRequestFrames;
import mchorse.blockbuster.network.common.recording.PacketRequestedFrames;
import mchorse.blockbuster.network.common.recording.PacketSyncTick;
import mchorse.blockbuster.network.common.recording.PacketUnloadFrames;
import mchorse.blockbuster.network.common.recording.PacketUnloadRecordings;
import mchorse.blockbuster.network.server.ServerHandlerActorRotate;
import mchorse.blockbuster.network.server.ServerHandlerModifyActor;
import mchorse.blockbuster.network.server.ServerHandlerModifyModelBlock;
import mchorse.blockbuster.network.server.ServerHandlerReloadModels;
import mchorse.blockbuster.network.server.ServerHandlerTickMarker;
import mchorse.blockbuster.network.server.director.ServerHandlerConfirmBreak;
import mchorse.blockbuster.network.server.director.ServerHandlerDirectorCast;
import mchorse.blockbuster.network.server.director.ServerHandlerDirectorRequestCast;
import mchorse.blockbuster.network.server.director.sync.ServerHandlerDirectorGoto;
import mchorse.blockbuster.network.server.director.sync.ServerHandlerDirectorPlay;
import mchorse.blockbuster.network.server.recording.ServerHandlerFramesChunk;
import mchorse.blockbuster.network.server.recording.ServerHandlerPlayback;
import mchorse.blockbuster.network.server.recording.ServerHandlerRequestFrames;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Network dispatcher
 *
 * @author Ernio (Ernest Sadowski)
 */
public class Dispatcher
{
    private static final SimpleNetworkWrapper DISPATCHER = NetworkRegistry.INSTANCE.newSimpleChannel(Blockbuster.MODID);
    private static byte PACKET_ID;

    public static SimpleNetworkWrapper get()
    {
        return DISPATCHER;
    }

    /**
     * Send message to players who are tracking given entity
     */
    public static void sendToTracked(Entity entity, IMessage message)
    {
        EntityTracker tracker = ((WorldServer) entity.worldObj).getEntityTracker();

        for (EntityPlayer player : tracker.getTrackingPlayers(entity))
        {
            DISPATCHER.sendTo(message, (EntityPlayerMP) player);
        }
    }

    /**
     * Send message to given player
     */
    public static void sendTo(IMessage message, EntityPlayerMP player)
    {
        DISPATCHER.sendTo(message, player);
    }

    /**
     * Send message to the server
     */
    public static void sendToServer(IMessage message)
    {
        DISPATCHER.sendToServer(message);
    }

    /**
     * Register all the networking messages and message handlers
     */
    public static void register()
    {
        /* Update actor properties */
        register(PacketModifyActor.class, ClientHandlerModifyActor.class, Side.CLIENT);
        register(PacketModifyActor.class, ServerHandlerModifyActor.class, Side.SERVER);
        register(PacketActorPause.class, ClientHandlerActorPause.class, Side.CLIENT);
        register(PacketActorRotate.class, ServerHandlerActorRotate.class, Side.SERVER);

        /* Update model block properties */
        register(PacketModifyModelBlock.class, ClientHandlerModifyModelBlock.class, Side.CLIENT);
        register(PacketModifyModelBlock.class, ServerHandlerModifyModelBlock.class, Side.SERVER);

        /* Recording */
        register(PacketPlayerRecording.class, ClientHandlerPlayerRecording.class, Side.CLIENT);

        register(PacketFramesLoad.class, ClientHandlerFrames.class, Side.CLIENT);
        register(PacketFramesChunk.class, ServerHandlerFramesChunk.class, Side.SERVER);

        register(PacketPlayback.class, ClientHandlerPlayback.class, Side.CLIENT);
        register(PacketPlayback.class, ServerHandlerPlayback.class, Side.SERVER);

        register(PacketRequestFrames.class, ServerHandlerRequestFrames.class, Side.SERVER);
        register(PacketRequestedFrames.class, ClientHandlerRequestedFrames.class, Side.CLIENT);

        register(PacketUnloadFrames.class, ClientHandlerUnloadFrames.class, Side.CLIENT);
        register(PacketUnloadRecordings.class, ClientHandlerUnloadRecordings.class, Side.CLIENT);

        register(PacketSyncTick.class, ClientHandlerSyncTick.class, Side.CLIENT);

        register(PacketCaption.class, ClientHandlerCaption.class, Side.CLIENT);

        /* Director block management messages */
        register(PacketDirectorCast.class, ClientHandlerDirectorCast.class, Side.CLIENT);
        register(PacketDirectorCast.class, ServerHandlerDirectorCast.class, Side.SERVER);
        register(PacketDirectorRequestCast.class, ServerHandlerDirectorRequestCast.class, Side.SERVER);

        register(PacketConfirmBreak.class, ClientHandlerConfirmBreak.class, Side.CLIENT);
        register(PacketConfirmBreak.class, ServerHandlerConfirmBreak.class, Side.SERVER);

        /* Director block syncing */
        register(PacketDirectorGoto.class, ServerHandlerDirectorGoto.class, Side.SERVER);
        register(PacketDirectorPlay.class, ServerHandlerDirectorPlay.class, Side.SERVER);

        /* Multiplayer */
        register(PacketReloadModels.class, ServerHandlerReloadModels.class, Side.SERVER);

        /* Miscellaneous */
        register(PacketTickMarker.class, ServerHandlerTickMarker.class, Side.SERVER);

        if (CameraHandler.isApertureLoaded())
        {
            CameraHandler.registerMessages();
        }
    }

    /**
     * Register given message with given message handler on a given side
     */
    public static <REQ extends IMessage, REPLY extends IMessage> void register(Class<REQ> message, Class<? extends IMessageHandler<REQ, REPLY>> handler, Side side)
    {
        DISPATCHER.registerMessage(handler, message, PACKET_ID++, side);
    }
}