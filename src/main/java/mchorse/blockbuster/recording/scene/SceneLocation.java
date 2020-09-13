package mchorse.blockbuster.recording.scene;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.Objects;

/**
 * Scene location data class
 *
 * This bad boy allows to unify director blocks and scene identifier into one structure
 */
public class SceneLocation
{
	private Scene scene;
	private BlockPos position;
	private String filename;

	public SceneLocation()
	{}

	public SceneLocation(Director scene, BlockPos position)
	{
		this.scene = scene;
		this.position = position;
	}

	public SceneLocation(Scene scene)
	{
		this.scene = scene;
		this.filename = scene.getId();
	}

	public SceneLocation(BlockPos position)
	{
		this.position = position;
	}

	public SceneLocation(String filename)
	{
		this.filename = filename;
	}

	public Scene getScene()
	{
		return this.scene;
	}

	public Director getDirector()
	{
		return this.isDirector() ? (Director) this.scene : null;
	}

	public BlockPos getPosition()
	{
		return this.position;
	}

	public String getFilename()
	{
		return this.filename;
	}

	public int getType()
	{
		return this.isEmpty() ? 0 : (this.isDirector() ? 1 : 2);
	}

	public boolean isEmpty()
	{
		return this.scene == null;
	}

	public boolean isScene()
	{
		return this.filename != null && !this.filename.isEmpty();
	}

	public boolean isDirector()
	{
		return this.position != null;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof SceneLocation)
		{
			SceneLocation location = (SceneLocation) obj;

			if (this.getType() == location.getType())
			{
				if (this.isDirector())
				{
					return Objects.equals(this.position, location.position);
				}
				else if (this.isScene())
				{
					return Objects.equals(this.filename, location.filename);
				}
				else
				{
					return true;
				}
			}
		}

		return super.equals(obj);
	}

	public SceneLocation copyEmpty()
	{
		if (this.isDirector())
		{
			return new SceneLocation(this.getPosition());
		}
		else if (this.isScene())
		{
			return new SceneLocation(this.getFilename());
		}

		return new SceneLocation();
	}

	public void fromByteBuf(ByteBuf buf)
	{
		this.position = null;
		this.filename = null;

		if (buf.readBoolean())
		{
			this.position = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		}

		if (buf.readBoolean())
		{
			this.filename = ByteBufUtils.readUTF8String(buf);
		}

		if (buf.readBoolean())
		{
			this.scene = this.isDirector() ? new Director(null) : (this.isScene() ? new Scene() : null);

			if (this.scene != null)
			{
				this.scene.fromBuf(buf);
			}
		}
	}

	public void toByteBuf(ByteBuf buf)
	{
		buf.writeBoolean(this.position != null);

		if (this.position != null)
		{
			buf.writeInt(this.position.getX());
			buf.writeInt(this.position.getY());
			buf.writeInt(this.position.getZ());
		}

		buf.writeBoolean(this.filename != null);

		if (this.filename != null)
		{
			ByteBufUtils.writeUTF8String(buf, this.filename);
		}

		buf.writeBoolean(this.scene != null);

		if (this.scene != null)
		{
			this.scene.toBuf(buf);
		}
	}
}