package mchorse.blockbuster.network.common;

import io.netty.buffer.ByteBuf;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketModifyActor implements IMessage
{
    public int id;
    public AbstractMorph morph;
    public boolean invisible;

    public PacketModifyActor()
    {}

    public PacketModifyActor(int id, AbstractMorph morph, boolean invisible)
    {
        this.id = id;
        this.morph = morph;
        this.invisible = invisible;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.id = buf.readInt();
        this.invisible = buf.readBoolean();

        if (buf.readBoolean())
        {
            this.morph = MorphManager.INSTANCE.morphFromNBT(ByteBufUtils.readTag(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.id);
        buf.writeBoolean(this.invisible);
        buf.writeBoolean(this.morph != null);

        if (this.morph != null)
        {
            NBTTagCompound tag = new NBTTagCompound();

            this.morph.toNBT(tag);
            ByteBufUtils.writeTag(buf, tag);
        }
    }
}