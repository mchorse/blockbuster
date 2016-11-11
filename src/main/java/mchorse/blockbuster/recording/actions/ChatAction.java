package mchorse.blockbuster.recording.actions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import mchorse.blockbuster.recording.Utils;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Chat action
 *
 * Sends chat message with some formatting.
 * See {@link ChatAction#apply(mchorse.blockbuster.common.entity.EntityActor)} for more
 * information.
 */
public class ChatAction extends Action
{
    public String message;

    public ChatAction()
    {}

    public ChatAction(String message)
    {
        this.message = message;
    }

    @Override
    public byte getType()
    {
        return Action.CHAT;
    }

    @Override
    public void apply(mchorse.blockbuster.common.entity.EntityActor actor)
    {
        Utils.broadcastMessage(this.message.replace('[', '§'));
    }

    @Override
    public void fromBytes(DataInput in) throws IOException
    {
        this.message = in.readUTF();
    }

    @Override
    public void toBytes(DataOutput out) throws IOException
    {
        out.writeUTF(this.message);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.message = tag.getString("Message");
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        tag.setString("Message", this.message);
    }
}