package mchorse.blockbuster.recording.scene;

import io.netty.buffer.ByteBuf;
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
	private String filename;

	public SceneLocation()
	{}

	public SceneLocation(Scene scene)
	{
		this.scene = scene;
		this.filename = scene.getId();
	}

	public SceneLocation(String filename)
	{
		this.filename = filename;
	}

	public Scene getScene()
	{
		return this.scene;
	}

	public String getFilename()
	{
		return this.filename;
	}

	public int getType()
	{
		return this.isEmpty() ? 0 : 1;
	}

	public boolean isEmpty()
	{
		return !this.isScene();
	}

	public boolean isScene()
	{
		return this.filename != null && !this.filename.isEmpty();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof SceneLocation)
		{
			SceneLocation location = (SceneLocation) obj;

			if (this.getType() == location.getType())
			{
				return Objects.equals(this.filename, location.filename);
			}
		}

		return super.equals(obj);
	}

	public SceneLocation copyEmpty()
	{
		if (this.isScene())
		{
			return new SceneLocation(this.getFilename());
		}

		return new SceneLocation();
	}

	public void fromByteBuf(ByteBuf buf)
	{
		this.filename = null;

		if (buf.readBoolean())
		{
			this.filename = ByteBufUtils.readUTF8String(buf);
		}

		if (buf.readBoolean())
		{
			this.scene = this.isScene() ? new Scene() : null;

			if (this.scene != null)
			{
				this.scene.fromBuf(buf);
			}
		}
	}

	public void toByteBuf(ByteBuf buf)
	{
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