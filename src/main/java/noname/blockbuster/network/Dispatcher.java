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
import noname.blockbuster.api.Comment;
import noname.blockbuster.network.client.ClientHandlerCameraAttributes;
import noname.blockbuster.network.client.ClientHandlerCameraRecording;
import noname.blockbuster.network.client.ClientHandlerDirectorCast;
import noname.blockbuster.network.client.ClientHandlerDirectorMapCast;
import noname.blockbuster.network.client.ClientHandlerModifyActor;
import noname.blockbuster.network.client.ClientHandlerPlayerRecording;
import noname.blockbuster.network.common.PacketCameraAttributes;
import noname.blockbuster.network.common.PacketCameraRecording;
import noname.blockbuster.network.common.PacketModifyActor;
import noname.blockbuster.network.common.PacketPlayerRecording;
import noname.blockbuster.network.common.PacketSwitchCamera;
import noname.blockbuster.network.common.director.PacketDirectorCast;
import noname.blockbuster.network.common.director.PacketDirectorMapAdd;
import noname.blockbuster.network.common.director.PacketDirectorMapCast;
import noname.blockbuster.network.common.director.PacketDirectorMapEdit;
import noname.blockbuster.network.common.director.PacketDirectorMapRemove;
import noname.blockbuster.network.common.director.PacketDirectorMapReset;
import noname.blockbuster.network.common.director.PacketDirectorRemove;
import noname.blockbuster.network.common.director.PacketDirectorReset;
import noname.blockbuster.network.server.ServerHandlerCameraAttributes;
import noname.blockbuster.network.server.ServerHandlerDirectorMapAdd;
import noname.blockbuster.network.server.ServerHandlerDirectorMapEdit;
import noname.blockbuster.network.server.ServerHandlerDirectorMapRemove;
import noname.blockbuster.network.server.ServerHandlerDirectorMapReset;
import noname.blockbuster.network.server.ServerHandlerDirectorRemove;
import noname.blockbuster.network.server.ServerHandlerDirectorReset;
import noname.blockbuster.network.server.ServerHandlerModifyActor;
import noname.blockbuster.network.server.ServerHandlerSwitchCamera;

/**
 * Network dispatcher
 *
 * @author Ernio (Ernest Sadowski)
 */
@Comment(author = "Ernio (Ernest Sadowski)")
public class Dispatcher
{
    private static final SimpleNetworkWrapper DISPATCHER = NetworkRegistry.INSTANCE.newSimpleChannel(Blockbuster.MODID);
    private static byte PACKET_ID;

    public static SimpleNetworkWrapper getInstance()
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

    /**
     * Register all the networking messages and message handlers
     */
    public static void register()
    {
        /* Update camera attributes (speed, acceleration, flying */
        register(PacketCameraAttributes.class, ClientHandlerCameraAttributes.class, Side.CLIENT);
        register(PacketCameraAttributes.class, ServerHandlerCameraAttributes.class, Side.SERVER);

        /* Update actor properties */
        register(PacketModifyActor.class, ClientHandlerModifyActor.class, Side.CLIENT);
        register(PacketModifyActor.class, ServerHandlerModifyActor.class, Side.SERVER);

        /* Teleport player to another camera */
        register(PacketSwitchCamera.class, ServerHandlerSwitchCamera.class, Side.SERVER);

        /* Make cameras invinsible during playback */
        register(PacketCameraRecording.class, ClientHandlerCameraRecording.class, Side.CLIENT);

        /* Show up recording label when player starts recording */
        register(PacketPlayerRecording.class, ClientHandlerPlayerRecording.class, Side.CLIENT);

        /* Director block management messages */
        register(PacketDirectorCast.class, ClientHandlerDirectorCast.class, Side.CLIENT);

        register(PacketDirectorReset.class, ServerHandlerDirectorReset.class, Side.SERVER);
        register(PacketDirectorRemove.class, ServerHandlerDirectorRemove.class, Side.SERVER);

        /* Director block map management messages */
        register(PacketDirectorMapCast.class, ClientHandlerDirectorMapCast.class, Side.CLIENT);

        register(PacketDirectorMapAdd.class, ServerHandlerDirectorMapAdd.class, Side.SERVER);
        register(PacketDirectorMapEdit.class, ServerHandlerDirectorMapEdit.class, Side.SERVER);
        register(PacketDirectorMapReset.class, ServerHandlerDirectorMapReset.class, Side.SERVER);
        register(PacketDirectorMapRemove.class, ServerHandlerDirectorMapRemove.class, Side.SERVER);
    }

    private static <REQ extends IMessage, REPLY extends IMessage> void register(Class<REQ> message, Class<? extends IMessageHandler<REQ, REPLY>> handler, Side side)
    {
        DISPATCHER.registerMessage(handler, message, PACKET_ID++, side);
    }
}