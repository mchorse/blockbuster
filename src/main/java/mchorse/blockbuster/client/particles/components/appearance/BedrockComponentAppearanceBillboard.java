package mchorse.blockbuster.client.particles.components.appearance;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.molang.Molang;
import mchorse.blockbuster.client.particles.molang.MolangExpression;

public class BedrockComponentAppearanceBillboard extends BedrockComponentBase
{
	public MolangExpression sizeW = Molang.ZERO;
	public MolangExpression sizeH = Molang.ZERO;
	public CameraFacing facing = CameraFacing.LOOKAT_XYZ;
	public int textureWidth;
	public int textureHeight;
	public MolangExpression uvX = Molang.ZERO;
	public MolangExpression uvY = Molang.ZERO;
	public MolangExpression uvW = Molang.ZERO;
	public MolangExpression uvH = Molang.ZERO;

	public boolean flipbook = false;
	public float stepX;
	public float stepY;
	public float fps;
	public MolangExpression maxFrame;
	public boolean stretchFPS = false;
	public boolean loop = false;

	@Override
	public BedrockComponentBase fromJson(JsonElement elem)
	{
		if (!elem.isJsonObject()) return super.fromJson(elem);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("size") && element.get("size").isJsonArray())
		{
			JsonArray size = element.getAsJsonArray("size");

			if (size.size() >= 2)
			{
				this.sizeW = Molang.parse(size.get(0));
				this.sizeH = Molang.parse(size.get(1));
			}
		}

		if (element.has("face_camera_mode"))
		{
			this.facing = CameraFacing.fromString(element.get("face_camera_mode").getAsString());
		}

		if (element.has("uv") && element.get("uv").isJsonObject())
		{
			this.parseUv(element.get("uv").getAsJsonObject());
		}

		return super.fromJson(element);
	}

	private void parseUv(JsonObject object)
	{
		if (object.has("texturewidth")) this.textureWidth = object.get("texturewidth").getAsInt();
		if (object.has("textureheight")) this.textureHeight = object.get("textureheight").getAsInt();

		if (object.has("uv") && object.get("uv").isJsonArray())
		{
			JsonArray uv = object.getAsJsonArray("uv");

			if (uv.size() >= 2)
			{
				this.uvX = Molang.parse(uv.get(0));
				this.uvY = Molang.parse(uv.get(1));
			}
		}

		if (object.has("uv_size") && object.get("uv_size").isJsonArray())
		{
			JsonArray uv = object.getAsJsonArray("uv_size");

			if (uv.size() >= 2)
			{
				this.uvW = Molang.parse(uv.get(0));
				this.uvH = Molang.parse(uv.get(1));
			}
		}

		if (object.has("flipbook") && object.get("flipbook").isJsonObject())
		{
			this.flipbook = true;
			this.parseFlipbook(object.get("flipbook").getAsJsonObject());
		}
	}

	private void parseFlipbook(JsonObject flipbook)
	{
		if (flipbook.has("base_UV") && flipbook.get("base_UV").isJsonArray())
		{
			JsonArray uv = flipbook.getAsJsonArray("base_UV");

			if (uv.size() >= 2)
			{
				this.uvX = Molang.parse(uv.get(0));
				this.uvX = Molang.parse(uv.get(1));
			}
		}

		if (flipbook.has("size_UV") && flipbook.get("size_UV").isJsonArray())
		{
			JsonArray uv = flipbook.getAsJsonArray("size_UV");

			if (uv.size() >= 2)
			{
				this.uvW = Molang.parse(uv.get(0));
				this.uvH = Molang.parse(uv.get(1));
			}
		}

		if (flipbook.has("step_UV") && flipbook.get("step_UV").isJsonArray())
		{
			JsonArray uv = flipbook.getAsJsonArray("step_UV");

			if (uv.size() >= 2)
			{
				this.stepX = uv.get(0).getAsFloat();
				this.stepY = uv.get(1).getAsFloat();
			}
		}

		if (flipbook.has("frames_per_second")) this.fps = flipbook.get("frames_per_second").getAsFloat();
		if (flipbook.has("max_frame")) this.maxFrame = Molang.parse(flipbook.get("max_frame"));
		if (flipbook.has("stretch_to_lifetime")) this.stretchFPS = flipbook.get("stretch_to_lifetime").getAsBoolean();
		if (flipbook.has("loop")) this.loop = flipbook.get("loop").getAsBoolean();
	}

	public static enum CameraFacing
	{
		ROTATE_XYZ("rotate_xyz"), ROTATE_Y("rotate_y"),
		LOOKAT_XYZ("lookat_xyz"), LOOKAT_Y("lookat_y"),
		DIRECTION_X("direction_x"), DIRECTION_Y("direction_y"), DIRECTION_Z("direction_z");

		public final String name;

		public static CameraFacing fromString(String string)
		{
			for (CameraFacing facing : values())
			{
				if (facing.name.equals(string)) return facing;
			}

			return null;
		}

		private CameraFacing(String name)
		{
			this.name = name;
		}
	}
}