package mchorse.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketModifyActor implements IMessage
{
    public int id;
    public AbstractMorph morph;
    public boolean invisible;

    public int offset;
    public AbstractMorph previous;
    public int previousOffset;

    public PacketModifyActor()
    {}

    public PacketModifyActor(EntityActor actor)
    {
        this.id = actor.getEntityId();
        this.morph = actor.morph.get();
        this.invisible = actor.invisible;

        this.offset = actor.pauseOffset;
        this.previous = actor.pausePreviousMorph;
        this.previousOffset = actor.pausePreviousOffset;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.id = buf.readInt();
        this.invisible = buf.readBoolean();
        this.morph = MorphUtils.morphFromBuf(buf);

        this.offset = buf.readInt();

        if (buf.readBoolean())
        {
            this.previous = MorphUtils.morphFromBuf(buf);
        }

        this.previousOffset = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.id);
        buf.writeBoolean(this.invisible);
        MorphUtils.morphToBuf(buf, this.morph);

        buf.writeInt(this.offset);
        buf.writeBoolean(this.previous != null);

        if (this.previous != null)
        {
            MorphUtils.morphToBuf(buf, this.previous);
        }

        buf.writeInt(this.previousOffset);
    }
}