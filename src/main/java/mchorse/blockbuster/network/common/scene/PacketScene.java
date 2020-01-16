package mchorse.blockbuster.network.common.scene;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class PacketScene implements IMessage
{
    public BlockPos pos;
    public String filename;

    public PacketScene()
    {}

    public PacketScene(BlockPos pos)
    {
        this.pos = pos;
    }

	public PacketScene(String filename)
	{
		this.filename = filename;
	}

	public boolean isDirector()
	{
		return this.pos != null;
	}

    @Override
    public void fromBytes(ByteBuf buf)
    {
    	if (buf.readBoolean())
    	{
		    this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	    }
    	else
	    {
	    	this.filename = ByteBufUtils.readUTF8String(buf);
	    }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    	buf.writeBoolean(this.pos != null);

    	if (this.pos != null)
    	{
		    buf.writeInt(this.pos.getX());
		    buf.writeInt(this.pos.getY());
		    buf.writeInt(this.pos.getZ());
	    }
    	else if (this.filename != null)
	    {
	    	ByteBufUtils.writeUTF8String(buf, this.filename);
	    }
    }
}