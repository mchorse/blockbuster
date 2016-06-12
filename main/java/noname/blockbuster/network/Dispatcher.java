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
import noname.blockbuster.network.client.ClientHandlerChangeSkin;
import noname.blockbuster.network.client.ClientHandlerRecording;
import noname.blockbuster.network.common.ChangeSkin;
import noname.blockbuster.network.common.PacketCameraAttributes;
import noname.blockbuster.network.common.Recording;
import noname.blockbuster.network.common.SwitchCamera;
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

    public static void init()
    {
        /** Update camera attributes (speed, acceleration, flying */
        register(PacketCameraAttributes.class, ClientHandlerCameraAttributes.class, Side.CLIENT);
        register(PacketCameraAttributes.class, ServerHandlerCameraAttributes.class, Side.SERVER);

        /** Update actor's skin */
        register(ChangeSkin.class, ClientHandlerChangeSkin.class, Side.CLIENT);
        register(ChangeSkin.class, ServerHandlerChangeSkin.class, Side.SERVER);

        /** Teleport player to another camera */
        register(SwitchCamera.class, ServerHandlerSwitchCamera.class, Side.SERVER);

        /** Make cameras invinsible while playback */
        register(Recording.class, ClientHandlerRecording.class, Side.CLIENT);
    }

    private static <REQ extends IMessage, REPLY extends IMessage> void register(Class<REQ> message, Class<? extends IMessageHandler<REQ, REPLY>> handler, Side side)
    {
        DISPATCHER.registerMessage(handler, message, PACKET_ID++, side);
    }
}