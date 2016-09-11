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
            int models = stream.readInt();

            /* Read models */
            for (int i = 0; i < models; i++)
            {
                String model = stream.readUTF();

                this.models.put(model, stream.readUTF());

                int skins = stream.readInt();
                if (skins == 0) continue;

                Map<String, ByteBuf> skin = new HashMap<String, ByteBuf>();

                /* Read model skins */
                for (int j = 0; j < skins; j++)
                {
                    String skinName = stream.readUTF();
                    int length = stream.readInt();

                    skin.put(skinName, buf.readBytes(length));
                }

                this.skins.put(model, skin);
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
            stream.writeInt(this.models.size());

            /* Write models */
            for (Map.Entry<String, String> model : this.models.entrySet())
            {
                stream.writeUTF(model.getKey());
                stream.writeUTF(model.getValue());

                Map<String, ByteBuf> skins = this.skins.get(model.getKey());

                /* Write skins */
                if (skins == null || skins.size() == 0)
                {
                    stream.writeInt(0);
                    continue;
                }

                stream.writeInt(skins.size());

                for (Map.Entry<String, ByteBuf> skin : skins.entrySet())
                {
                    ByteBuf buffer = skin.getValue();

                    stream.writeUTF(skin.getKey());
                    buf.writeInt(buffer.readableBytes());
                    buf.writeBytes(buffer);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
