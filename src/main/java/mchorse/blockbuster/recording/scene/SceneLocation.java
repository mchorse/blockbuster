package mchorse.blockbuster.recording.scene;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * Scene location data class
 *
 * This bad boy allows to unify director blocks and scene identifier into one structure
 */
public class SceneLocation
{
	private BlockPos director;
	private String scene;

	public SceneLocation()
	{}

	public SceneLocation(BlockPos director)
	{
		this.director = director;
	}

	public SceneLocation(String scene)
	{
		this.scene = scene;
	}

	public BlockPos getDirector()
	{
		return this.director;
	}

	public String getScene()
	{
		return this.scene;
	}

	public boolean isScene()
	{
		return this.scene != null && !this.scene.isEmpty();
	}

	public boolean isDirector()
	{
		return this.director != null;
	}

	public void fromByteBuf(ByteBuf buf)
	{
		this.director = null;
		this.scene = null;

		if (buf.readBoolean())
		{
			this.director = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		}

		if (buf.readBoolean())
		{
			this.scene = ByteBufUtils.readUTF8String(buf);
		}
	}

	public void toByteBuf(ByteBuf buf)
	{
		buf.writeBoolean(this.director != null);

		if (this.director != null)
		{
			buf.writeInt(this.director.getX());
			buf.writeInt(this.director.getY());
			buf.writeInt(this.director.getZ());
		}

		buf.writeBoolean(this.scene != null);

		if (this.scene != null)
		{
			ByteBufUtils.writeUTF8String(buf, this.scene);
		}
	}
}