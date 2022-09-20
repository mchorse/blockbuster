package mchorse.blockbuster.network.common.recording.actions;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.GuiRecordTimeline;
import mchorse.blockbuster.recording.actions.ActionRegistry;
import mchorse.blockbuster.recording.actions.Action;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.ArrayList;
import java.util.List;

public class PacketActionsChange implements IMessage
{
    private String filename;
    private int fromTick = -1;
    private int index = -1;
    private List<List<Action>> actions;
    private List<List<Boolean>> mask;
    private Type type;

    public PacketActionsChange()
    {}

    /**
     * @param filename
     * @param from order does not matter - it will be sorted internally
     */
    public PacketActionsChange(String filename, int from, int index, Action action, Type type)
    {
        this.filename = filename;
        this.fromTick = from;
        this.index = index;

        List<List<Action>> actions = new ArrayList<>();
        actions.add(new ArrayList<>());
        actions.get(0).add(action);

        this.actions = actions;
        this.type = type;
    }

    public PacketActionsChange(String filename, int from, List<List<Action>> actions, Type type)
    {
        this.filename = filename;
        this.fromTick = from;
        this.actions = actions;
        this.type = type;
    }

    /**
     * A deletion package
     * @param filename
     * @param tick
     * @param deletionMask
     */
    public PacketActionsChange(String filename, int tick, List<List<Boolean>> deletionMask)
    {
        this(filename, tick, null, Type.DELETE);

        this.mask = deletionMask;
    }

    public boolean containsOneAction()
    {
        return this.actions != null && !this.actions.isEmpty() && this.actions.get(0) != null && !this.actions.get(0).isEmpty();
    }

    public int getIndex()
    {
        return this.index;
    }

    public int getFromTick()
    {
        return this.fromTick;
    }

    public List<List<Action>> getActions()
    {
        return this.actions;
    }

    public String getFilename()
    {
        return this.filename;
    }

    public Type getStatus()
    {
        return this.type;
    }

    public List<List<Boolean>> getMask()
    {
        return this.mask;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.filename = ByteBufUtils.readUTF8String(buf);
        this.fromTick = buf.readInt();
        this.index = buf.readInt();
        this.type = Type.values()[buf.readInt()];

        if (buf.readBoolean())
        {
            this.actions = new ArrayList<>();
            int size = buf.readInt();

            for (int i = 0; i < size; i++)
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
        }

        if (buf.readBoolean())
        {
            this.mask = new ArrayList<>();

            int size = buf.readInt();

            for (int i = 0; i < size; i++)
            {
                List<Boolean> maskFrame = new ArrayList<>();

                int count = buf.readByte();

                if (count != 0)
                {
                    for (int j = 0; j < count; j++)
                    {
                        maskFrame.add(buf.readBoolean());
                    }
                }
                else
                {
                    maskFrame.add(false);
                }

                this.mask.add(maskFrame);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.filename);
        buf.writeInt(this.fromTick);
        buf.writeInt(this.index);
        buf.writeInt(this.type.ordinal());
        buf.writeBoolean(this.actions != null);

        if (this.actions != null)
        {
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
        }

        buf.writeBoolean(this.mask != null);

        if (this.mask != null)
        {
            buf.writeInt(this.mask.size());

            for (List<Boolean> list : this.mask)
            {
                int count = list == null ? 0 : list.size();

                buf.writeByte(count);

                if (count != 0)
                {
                    for (Boolean bool : list)
                    {
                        buf.writeBoolean(bool);
                    }
                }
            }
        }
    }

    public enum Type
    {
        DELETE,
        ADD,
        EDIT
    }
}