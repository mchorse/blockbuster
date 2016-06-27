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
import noname.blockbuster.network.client.ClientHandlerChangeSkin;
import noname.blockbuster.network.client.ClientHandlerPlayerRecording;
import noname.blockbuster.network.common.PacketCameraAttributes;
import noname.blockbuster.network.common.PacketCameraRecording;
import noname.blockbuster.network.common.PacketChangeSkin;
import noname.blockbuster.network.common.PacketPlayerRecording;
import noname.blockbuster.network.common.PacketSwitchCamera;
import noname.blockbuster.network.server.ServerHandlerCameraAttributes;
import noname.blockbuster.network.server.ServerHandlerChangeSkin;
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
        /** Update camera attributes (speed, acceleration, flying */
        register(PacketCameraAttributes.class, ClientHandlerCameraAttributes.class, Side.CLIENT);
        register(PacketCameraAttributes.class, ServerHandlerCameraAttributes.class, Side.SERVER);

        /** Update actor's skin */
        register(PacketChangeSkin.class, ClientHandlerChangeSkin.class, Side.CLIENT);
        register(PacketChangeSkin.class, ServerHandlerChangeSkin.class, Side.SERVER);

        /** Teleport player to another camera */
        register(PacketSwitchCamera.class, ServerHandlerSwitchCamera.class, Side.SERVER);

        /** Make cameras invinsible while playback */
        register(PacketCameraRecording.class, ClientHandlerCameraRecording.class, Side.CLIENT);

        /** Show up recording label when player starts recording */
        register(PacketPlayerRecording.class, ClientHandlerPlayerRecording.class, Side.CLIENT);
    }

    private static <REQ extends IMessage, REPLY extends IMessage> void register(Class<REQ> message, Class<? extends IMessageHandler<REQ, REPLY>> handler, Side side)
    {
        DISPATCHER.registerMessage(handler, message, PACKET_ID++, side);
    }
}