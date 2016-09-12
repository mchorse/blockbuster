package mchorse.blockbuster.network.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Packet models
 *
 * This is a big packet that is responsible for delivering missing models and
 * skins to clients.
 */
public class PacketModels implements IMessage
{
    public Map<String, String> models;
    public Map<String, Map<String, ByteBuf>> skins;

    public PacketModels()
    {
        this.models = new HashMap<String, String>();
        this.skins = new HashMap<String, Map<String, ByteBuf>>();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        ByteBufInputStream stream = new ByteBufInputStream(buf);

        try
        {
            int skinModels = stream.readInt();

            /* Read skins */
            for (int i = 0; i < skinModels; i++)
            {
                String modelName = stream.readUTF();
                int skinSize = stream.readInt();

                if (skinSize == 0) continue;

                Map<String, ByteBuf> skins = new HashMap<String, ByteBuf>();

                for (int j = 0; j < skinSize; j++)
                {
                    String skinName = stream.readUTF();
                    int length = stream.readInt();

                    skins.put(skinName, buf.readBytes(length));
                }

                this.skins.put(modelName, skins);
            }

            int models = stream.readInt();

            /* Read models */
            for (int i = 0; i < models; i++)
            {
                this.models.put(stream.readUTF(), stream.readUTF());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufOutputStream stream = new ByteBufOutputStream(buf);

        try
        {
            stream.writeInt(this.skins.size());

            /* Write skins */
            for (Map.Entry<String, Map<String, ByteBuf>> entry : this.skins.entrySet())
            {
                stream.writeUTF(entry.getKey());
                stream.writeInt(entry.getValue().size());

                for (Map.Entry<String, ByteBuf> skin : entry.getValue().entrySet())
                {
                    ByteBuf buffer = skin.getValue();

                    stream.writeUTF(skin.getKey());
                    buf.writeInt(buffer.readableBytes());
                    buf.writeBytes(buffer);
                }
            }

            stream.writeInt(this.models.size());

            /* Write models */
            for (Map.Entry<String, String> model : this.models.entrySet())
            {
                stream.writeUTF(model.getKey());
                stream.writeUTF(model.getValue());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
