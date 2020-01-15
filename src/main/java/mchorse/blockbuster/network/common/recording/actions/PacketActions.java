package mchorse.blockbuster.network.common.recording.actions;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.recording.actions.ActionRegistry;
import mchorse.blockbuster.recording.actions.Action;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketActions implements IMessage
{
    public String filename;
    public List<List<Action>> actions;
    public boolean open;

    public PacketActions()
    {
        this.actions = new ArrayList<List<Action>>();
    }

    public PacketActions(String filename, List<List<Action>> actions, boolean open)
    {
        this.filename = filename;
        this.actions = actions;
        this.open = open;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.filename = ByteBufUtils.readUTF8String(buf);

        for (int i = 0, c = buf.readInt(); i < c; i++)
        {
            int count = buf.readByte();

            if (count != 0)
            {
                List<Action> actions = new ArrayList<Action>();

                for (int j = 0; j < count; j++)
                {
                    Action action = ActionRegistry.fromByteBuf(buf);

                    if (action != null)
                    {
                        actions.add(action);
                    }
                }

                this.actions.add(actions);
            }
            else
            {
                this.actions.add(null);
            }
        }

        this.open = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        int i = 0;

        ByteBufUtils.writeUTF8String(buf, this.filename);
        buf.writeInt(this.actions.size());

        for (List<Action> list : this.actions)
        {
            int count = list == null ? 0 : list.size();

            buf.writeByte(count);

            if (count != 0)
            {
                for (Action action : list)
                {
                    ActionRegistry.toByteBuf(action, buf);
                }
            }
        }

        buf.writeBoolean(this.open);
    }
}