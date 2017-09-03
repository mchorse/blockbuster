package mchorse.blockbuster.network.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.ModelHandler;
import mchorse.blockbuster.api.ModelPack;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModels;
import mchorse.blockbuster.network.common.PacketRequestModels;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerRequestModels extends ServerMessageHandler<PacketRequestModels>
{
    /**
     * Send models to the player
     */
    @Override
    public void run(net.minecraft.entity.player.EntityPlayerMP player, PacketRequestModels message)
    {
        sendModels(Blockbuster.proxy.models, player);
    }

    /**
     * Send models and their skins over the network
     *
     * This method gets its models and skins via given {@link ModelPack} which
     * is stored by {@link ModelHandler}. Then this method converts models into
     * strings and skins into {@link ByteBuf}.
     *
     * If at least one of these maps isn't empty, the message is getting
     * send over the network.
     */
    public static void sendModels(ModelHandler handler, EntityPlayerMP player)
    {
        PacketModels message = new PacketModels();

        handler.pack.reload();

        /* Assemble models */
        for (String model : handler.pack.getAllSkins().keySet())
        {
            List<String> skins = handler.pack.getSkins(model);

            try
            {
                Map<String, ByteBuf> output = new HashMap<String, ByteBuf>();

                for (String skin : skins)
                {
                    InputStream skinStream = new FileInputStream(handler.pack.skins.get(model).get(skin));

                    output.put(skin, fileToBuffer(skinStream));
                }

                message.skins.put(model, output);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        /* Assemble skins */
        for (String model : handler.pack.getModels())
        {
            try
            {
                InputStream modelStream = new FileInputStream(handler.pack.models.get(model).customModel);

                if (modelStream != null)
                {
                    message.models.put(model, fileToString(modelStream));
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        /* Finally send */
        if (!message.models.isEmpty() || !message.skins.isEmpty())
        {
            Dispatcher.sendTo(message, player);
        }
    }

    /**
     * Convert file into string
     */
    private static String fileToString(InputStream input) throws IOException
    {
        StringBuilder builder = new StringBuilder();
        int letter;

        while ((letter = input.read()) != -1)
        {
            builder.append((char) letter);
        }

        input.close();

        return builder.toString();
    }

    /**
     * Convert file to netty's byte buffer
     */
    private static ByteBuf fileToBuffer(InputStream input) throws IOException
    {
        return Unpooled.wrappedBuffer(IOUtils.toByteArray(input));
    }
}