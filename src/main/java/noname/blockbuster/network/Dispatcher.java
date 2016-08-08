package noname.blockbuster.network;

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
import noname.blockbuster.Blockbuster;
import noname.blockbuster.network.client.ClientHandlerModifyActor;
import noname.blockbuster.network.client.ClientHandlerPlayerRecording;
import noname.blockbuster.network.client.camera.ClientHandlerCameraProfile;
import noname.blockbuster.network.client.camera.ClientHandlerCameraState;
import noname.blockbuster.network.client.camera.ClientHandlerListCameraProfiles;
import noname.blockbuster.network.client.director.ClientHandlerDirectorCast;
import noname.blockbuster.network.client.director.ClientHandlerDirectorMapCast;
import noname.blockbuster.network.common.PacketCameraMarker;
import noname.blockbuster.network.common.PacketModifyActor;
import noname.blockbuster.network.common.PacketPlayback;
import noname.blockbuster.network.common.PacketPlayerRecording;
import noname.blockbuster.network.common.camera.PacketCameraProfile;
import noname.blockbuster.network.common.camera.PacketCameraState;
import noname.blockbuster.network.common.camera.PacketListCameraProfiles;
import noname.blockbuster.network.common.camera.PacketLoadCameraProfile;
import noname.blockbuster.network.common.camera.PacketRequestCameraProfiles;
import noname.blockbuster.network.common.director.PacketDirectorCast;
import noname.blockbuster.network.common.director.PacketDirectorMapAdd;
import noname.blockbuster.network.common.director.PacketDirectorMapCast;
import noname.blockbuster.network.common.director.PacketDirectorMapEdit;
import noname.blockbuster.network.common.director.PacketDirectorMapRemove;
import noname.blockbuster.network.common.director.PacketDirectorMapReset;
import noname.blockbuster.network.common.director.PacketDirectorRemove;
import noname.blockbuster.network.common.director.PacketDirectorRequestCast;
import noname.blockbuster.network.common.director.PacketDirectorReset;
import noname.blockbuster.network.server.ServerHandlerCameraMarker;
import noname.blockbuster.network.server.ServerHandlerModifyActor;
import noname.blockbuster.network.server.ServerHandlerPlaybackButton;
import noname.blockbuster.network.server.camera.ServerHandlerCameraProfile;
import noname.blockbuster.network.server.camera.ServerHandlerListCameraProfiles;
import noname.blockbuster.network.server.camera.ServerHandlerLoadCameraProfile;
import noname.blockbuster.network.server.director.ServerHandlerDirectorMapAdd;
import noname.blockbuster.network.server.director.ServerHandlerDirectorMapEdit;
import noname.blockbuster.network.server.director.ServerHandlerDirectorMapRemove;
import noname.blockbuster.network.server.director.ServerHandlerDirectorMapReset;
import noname.blockbuster.network.server.director.ServerHandlerDirectorRemove;
import noname.blockbuster.network.server.director.ServerHandlerDirectorRequestCast;
import noname.blockbuster.network.server.director.ServerHandlerDirectorReset;

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

    public static void updateTrackers(Entity entity, IMessage message)
    {
        EntityTracker et = ((WorldServer) entity.worldObj).getEntityTracker();

        for (EntityPlayer player : et.getTrackingPlayers(entity))
        {
            DISPATCHER.sendTo(message, (EntityPlayerMP) player);
        }
    }

    public static void sendTo(IMessage message, EntityPlayerMP player)
    {
        DISPATCHER.sendTo(message, player);
    }

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

        /* Show up recording label when player starts recording */
        register(PacketPlayerRecording.class, ClientHandlerPlayerRecording.class, Side.CLIENT);

        /* Director block management messages */
        register(PacketDirectorCast.class, ClientHandlerDirectorCast.class, Side.CLIENT);

        register(PacketDirectorRequestCast.class, ServerHandlerDirectorRequestCast.class, Side.SERVER);
        register(PacketDirectorReset.class, ServerHandlerDirectorReset.class, Side.SERVER);
        register(PacketDirectorRemove.class, ServerHandlerDirectorRemove.class, Side.SERVER);

        /* Director block map management messages */
        register(PacketDirectorMapCast.class, ClientHandlerDirectorMapCast.class, Side.CLIENT);

        register(PacketDirectorMapAdd.class, ServerHandlerDirectorMapAdd.class, Side.SERVER);
        register(PacketDirectorMapEdit.class, ServerHandlerDirectorMapEdit.class, Side.SERVER);
        register(PacketDirectorMapReset.class, ServerHandlerDirectorMapReset.class, Side.SERVER);
        register(PacketDirectorMapRemove.class, ServerHandlerDirectorMapRemove.class, Side.SERVER);

        /* Camera management */
        register(PacketCameraProfile.class, ClientHandlerCameraProfile.class, Side.CLIENT);
        register(PacketCameraProfile.class, ServerHandlerCameraProfile.class, Side.SERVER);
        register(PacketCameraState.class, ClientHandlerCameraState.class, Side.CLIENT);
        register(PacketLoadCameraProfile.class, ServerHandlerLoadCameraProfile.class, Side.SERVER);

        register(PacketRequestCameraProfiles.class, ServerHandlerListCameraProfiles.class, Side.SERVER);
        register(PacketListCameraProfiles.class, ClientHandlerListCameraProfiles.class, Side.CLIENT);

        register(PacketPlayback.class, ServerHandlerPlaybackButton.class, Side.SERVER);

        /* So undocumented!!! */
        register(PacketCameraMarker.class, ServerHandlerCameraMarker.class, Side.SERVER);
    }

    private static <REQ extends IMessage, REPLY extends IMessage> void register(Class<REQ> message, Class<? extends IMessageHandler<REQ, REPLY>> handler, Side side)
    {
        DISPATCHER.registerMessage(handler, message, PACKET_ID++, side);
    }
}