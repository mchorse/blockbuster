package mchorse.blockbuster.recording.actions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import mchorse.blockbuster.entity.EntityActor;

/**
 * Morph action
 *
 * This action is responsible for changing the model and skin of actor during
 * the playback. This action is submitted when player executes /morph command.
 */
public class MorphAction extends Action
{
    public String model;
    public String skin;

    public MorphAction()
    {}

    public MorphAction(String model, String skin)
    {
        this.model = model;
        this.skin = skin;
    }

    @Override
    public byte getType()
    {
        return Action.MORPH;
    }

    @Override
    public void apply(EntityActor actor)
    {
        actor.model = this.model;
        actor.skin = this.skin;
        actor.notifyPlayers();
    }

    @Override
    public void fromBytes(DataInput in) throws IOException
    {
        this.model = in.readUTF();
        this.skin = in.readUTF();
    }

    @Override
    public void toBytes(DataOutput out) throws IOException
    {
        out.writeUTF(this.model);
        out.writeUTF(this.skin);
    }
}
