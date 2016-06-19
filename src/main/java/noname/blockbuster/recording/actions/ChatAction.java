package noname.blockbuster.recording.actions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import noname.blockbuster.recording.Mocap;

/**
 * Chat action
 *
 * Sends chat message with some formatting.
 * See {@link ChatAction#apply(noname.blockbuster.entity.ActorEntity)} for more
 * information.
 */
public class ChatAction extends Action
{
    public String message;

    public ChatAction()
    {
        super(Action.CHAT);
    }

    public ChatAction(String message)
    {
        this();
        this.message = message;
    }

    @Override
    public void apply(noname.blockbuster.entity.ActorEntity actor)
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
