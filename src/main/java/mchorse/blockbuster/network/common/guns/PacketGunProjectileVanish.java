package mchorse.blockbuster.network.common.guns;

import io.netty.buffer.ByteBuf;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketGunProjectileVanish implements IMessage
{
    public int id;
    public int delay;

    public PacketGunProjectileVanish()
    {}

    public PacketGunProjectileVanish(int id, int delay)
    {
        this.id = id;
        this.delay = delay;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.id = buf.readInt();
        this.delay = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.id);
        buf.writeInt(this.delay);
    }
}