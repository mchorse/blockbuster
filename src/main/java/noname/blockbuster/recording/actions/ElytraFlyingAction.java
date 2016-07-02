package noname.blockbuster.recording.actions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import noname.blockbuster.entity.EntityActor;

/**
 * Elytra flying action
 *
 * This action toggles actor's elytra flying state, this action is mostly used
 * for rendering, server doesn't really care.
 */
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
