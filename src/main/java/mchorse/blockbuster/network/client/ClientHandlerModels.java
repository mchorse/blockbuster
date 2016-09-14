package mchorse.blockbuster.network.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.network.common.PacketModels;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;

/**
 * This handler is responsible for saving received models and skins to
 * directory where downloaded models and skins should be located.
 */
public class ClientHandlerModels extends ClientMessageHandler<PacketModels>
{
    @Override
    public void run(EntityPlayerSP player, PacketModels message)
    {
        String path = ClientProxy.config.getAbsolutePath() + "/downloads";

        try
        {
            int modelSize = 0;
            int skinSize = 0;

            /* Write skins to downloaded folder */
            for (Map.Entry<String, Map<String, ByteBuf>> entry : message.skins.entrySet())
            {
                String modelName = entry.getKey();

                for (Map.Entry<String, ByteBuf> skin : entry.getValue().entrySet())
                {
                    String skinPath = String.format("%s/%s/skins/%s.png", path, modelName, skin.getKey());

                    new File(path + "/" + modelName + "/skins").mkdirs();
                    this.bufferToFile(skinPath, skin.getValue());

                    skinSize++;
                }
            }

            /* Write models to downloaded folder */
            for (Map.Entry<String, String> model : message.models.entrySet())
            {
                String modelName = model.getKey();
                String modelPath = String.format("%s/%s/model.json", path, modelName);

                new File(path + "/" + modelName).mkdirs();
                this.stringToFile(modelPath, model.getValue());

                modelSize++;
            }

            player.addChatMessage(new TextComponentString(I18n.format("blockbuster.models.loaded", modelSize, skinSize)));

            ClientProxy.actorPack.reload();
            Blockbuster.proxy.models.loadModels(ClientProxy.actorPack);
            ((ClientProxy) Blockbuster.proxy).loadClientModels();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Write given string to file
     */
    private void stringToFile(String file, String output) throws IOException
    {
        PrintWriter writer = new PrintWriter(file);

        writer.print(output);
        writer.close();
    }

    /**
     * Write given byte buffer
     */
    private void bufferToFile(String string, ByteBuf value) throws IOException
    {
        FileOutputStream output = new FileOutputStream(string);

        byte[] bytes = new byte[value.readableBytes()];

        value.readBytes(bytes);
        output.write(bytes);
        output.close();
    }
}
