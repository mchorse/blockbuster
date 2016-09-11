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

public class ClientHandlerModels extends ClientMessageHandler<PacketModels>
{
    @Override
    public void run(EntityPlayerSP player, PacketModels message)
    {
        String path = ClientProxy.config.getAbsolutePath() + "/downloads";

        try
        {
            for (Map.Entry<String, String> model : message.models.entrySet())
            {
                String modelName = model.getKey();
                String modelPath = String.format("%s/%s/model.json", path, modelName);

                new File(path + "/" + modelName + "/skins").mkdirs();
                this.stringToFile(modelPath, model.getValue());

                for (Map.Entry<String, ByteBuf> skin : message.skins.get(modelName).entrySet())
                {
                    String skinPath = String.format("%s/%s/skins/%s.png", path, modelName, skin.getKey());

                    this.bufferToFile(skinPath, skin.getValue());
                }
            }

            ClientProxy.actorPack.reload();
            ((ClientProxy) Blockbuster.proxy).loadActorModels();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void stringToFile(String file, String output) throws IOException
    {
        PrintWriter writer = new PrintWriter(file);

        writer.print(output);
        writer.close();
    }

    private void bufferToFile(String string, ByteBuf value) throws IOException
    {
        FileOutputStream output = new FileOutputStream(string);

        byte[] bytes = new byte[value.readableBytes()];

        value.readBytes(bytes);
        output.write(bytes);
        output.close();
    }
}
