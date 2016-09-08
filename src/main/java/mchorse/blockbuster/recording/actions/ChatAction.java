package mchorse.blockbuster.recording.actions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import mchorse.blockbuster.recording.Mocap;

/**
 * Chat action
 *
 * Sends chat message with some formatting.
 * See {@link ChatAction#apply(mchorse.blockbuster.entity.EntityActor)} for more
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
    public void apply(mchorse.blockbuster.entity.EntityActor actor)
    {
        Mocap.broadcastMessage(this.message.replace('[', '§'));
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
}
