package noname.blockbuster.recording.actions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import noname.blockbuster.entity.EntityActor;

public class ElytraFlyingAction extends Action
{
    public boolean isFlying;

    public ElytraFlyingAction()
    {}

    public ElytraFlyingAction(boolean isFlying)
    {
        this.isFlying = isFlying;
    }

    @Override
    public byte getType()
    {
        return Action.ELYTRA_FLYING;
    }

    @Override
    public void apply(EntityActor actor)
    {
        actor.setElytraFlying(this.isFlying);
    }

    @Override
    public void fromBytes(DataInput in) throws IOException
    {
        this.isFlying = in.readBoolean();
    }

    @Override
    public void toBytes(DataOutput out) throws IOException
    {
        out.writeBoolean(this.isFlying);
    };
}
