package mchorse.blockbuster.client.particles.components.appearance;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentParticleRender;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;
import mchorse.mclib.utils.Interpolations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class BedrockComponentAppearanceBillboard extends BedrockComponentBase implements IComponentParticleRender
{
	public MolangExpression sizeW = MolangParser.ZERO;
	public MolangExpression sizeH = MolangParser.ZERO;
	public CameraFacing facing = CameraFacing.LOOKAT_XYZ;
	public int textureWidth;
	public int textureHeight;
	public MolangExpression uvX = MolangParser.ZERO;
	public MolangExpression uvY = MolangParser.ZERO;
	public MolangExpression uvW = MolangParser.ZERO;
	public MolangExpression uvH = MolangParser.ZERO;

	public boolean flipbook = false;
	public float stepX;
	public float stepY;
	public float fps;
	public MolangExpression maxFrame;
	public boolean stretchFPS = false;
	public boolean loop = false;

	@Override
	public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
	{
		if (!elem.isJsonObject()) return super.fromJson(elem, parser);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("size") && element.get("size").isJsonArray())
		{
			JsonArray size = element.getAsJsonArray("size");

			if (size.size() >= 2)
			{
				this.sizeW = parser.parseJson(size.get(0));
				this.sizeH = parser.parseJson(size.get(1));
			}
		}

		if (element.has("facing_camera_mode"))
		{
			this.facing = CameraFacing.fromString(element.get("facing_camera_mode").getAsString());
		}

		if (element.has("uv") && element.get("uv").isJsonObject())
		{
			this.parseUv(element.get("uv").getAsJsonObject(), parser);
		}

		return super.fromJson(element, parser);
	}

	private void parseUv(JsonObject object, MolangParser parser) throws MolangException
	{
		if (object.has("texture_width")) this.textureWidth = object.get("texture_width").getAsInt();
		if (object.has("texture_height")) this.textureHeight = object.get("texture_height").getAsInt();

		if (object.has("uv") && object.get("uv").isJsonArray())
		{
			JsonArray uv = object.getAsJsonArray("uv");

			if (uv.size() >= 2)
			{
				this.uvX = parser.parseJson(uv.get(0));
				this.uvY = parser.parseJson(uv.get(1));
			}
		}

		if (object.has("uv_size") && object.get("uv_size").isJsonArray())
		{
			JsonArray uv = object.getAsJsonArray("uv_size");

			if (uv.size() >= 2)
			{
				this.uvW = parser.parseJson(uv.get(0));
				this.uvH = parser.parseJson(uv.get(1));
			}
		}

		if (object.has("flipbook") && object.get("flipbook").isJsonObject())
		{
			this.flipbook = true;
			this.parseFlipbook(object.get("flipbook").getAsJsonObject(), parser);
		}
	}

	private void parseFlipbook(JsonObject flipbook, MolangParser parser) throws MolangException
	{
		if (flipbook.has("base_UV") && flipbook.get("base_UV").isJsonArray())
		{
			JsonArray uv = flipbook.getAsJsonArray("base_UV");

			if (uv.size() >= 2)
			{
				this.uvX = parser.parseJson(uv.get(0));
				this.uvY = parser.parseJson(uv.get(1));
			}
		}

		if (flipbook.has("size_UV") && flipbook.get("size_UV").isJsonArray())
		{
			JsonArray uv = flipbook.getAsJsonArray("size_UV");

			if (uv.size() >= 2)
			{
				this.uvW = parser.parseJson(uv.get(0));
				this.uvH = parser.parseJson(uv.get(1));
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
		if (flipbook.has("max_frame")) this.maxFrame = parser.parseJson(flipbook.get("max_frame"));
		if (flipbook.has("stretch_to_lifetime")) this.stretchFPS = flipbook.get("stretch_to_lifetime").getAsBoolean();
		if (flipbook.has("loop")) this.loop = flipbook.get("loop").getAsBoolean();
	}

	@Override
	public void preRender(BedrockEmitter emitter, float partialTicks)
	{}

	@Override
	public void render(BedrockEmitter emitter, BedrockParticle particle, VertexBuffer builder, float partialTicks)
	{
		/* Update particle's UVs and size */
		float pw = (float) this.sizeW.get() * 2.25F;
		float ph = (float) this.sizeH.get() * 2.25F;

		float u = (float) this.uvX.get();
		float v = (float) this.uvY.get();
		float w = (float) this.uvW.get();
		float h = (float) this.uvH.get();

		if (this.flipbook)
		{
			int index = (int) (particle.getAge(partialTicks) * this.fps);
			int max = (int) this.maxFrame.get();

			if (this.stretchFPS)
			{
				index = (int) ((particle.age + partialTicks) / particle.lifetime * max);
			}

			if (this.loop)
			{
				index = index % max;
			}

			if (index > max)
			{
				index = max;
			}

			u += this.stepX * index;
			v += this.stepY * index;
		}

		float u1 = u / (float) this.textureWidth;
		float v1 = v / (float) this.textureHeight;
		float u2 = (u + w) / (float) this.textureWidth;
		float v2 = (v + h) / (float) this.textureHeight;

		/* Render the particle */
		double px = Interpolations.lerp(particle.prevPosition.x, particle.position.x, partialTicks);
		double py = Interpolations.lerp(particle.prevPosition.y, particle.position.y, partialTicks);
		double pz = Interpolations.lerp(particle.prevPosition.z, particle.position.z, partialTicks);
		float angle = Interpolations.lerp(particle.prevRotation, particle.rotation, partialTicks);

		if (particle.relative)
		{
			px += emitter.lastGlobalX;
			py += emitter.lastGlobalY;
			pz += emitter.lastGlobalZ;
		}

		/* Calculate yaw and pitch based on the facing mode */
		Entity camera = Minecraft.getMinecraft().getRenderViewEntity();

		float entityYaw = 180 - camera.prevRotationYaw + (camera.rotationYaw - camera.prevRotationYaw) * partialTicks;
		float entityPitch = 180 - camera.prevRotationPitch + (camera.rotationPitch - camera.prevRotationPitch) * partialTicks;

		if (this.facing == CameraFacing.LOOKAT_XYZ || this.facing == CameraFacing.LOOKAT_Y)
		{
			double cx = Interpolations.lerp(camera.prevPosX, camera.posX, partialTicks);
			double cy = Interpolations.lerp(camera.prevPosY, camera.posY, partialTicks) + camera.getEyeHeight();
			double cz = Interpolations.lerp(camera.prevPosZ, camera.posZ, partialTicks);

			double dX = cx - px;
			double dY = cy - py;
			double dZ = cz - pz;

			double horizontalDistance = MathHelper.sqrt_double(dX * dX + dZ * dZ);

			entityYaw = 180 - (float) (MathHelper.atan2(dZ, dX) * (180D / Math.PI)) - 90.0F;
			entityPitch = (float) (-(MathHelper.atan2(dY, horizontalDistance) * (180D / Math.PI)));
		}

		if (Minecraft.getMinecraft().gameSettings.thirdPersonView != 2)
		{
			entityPitch += 180;
		}

		/* Calculate the geometry for billboards */
		int light = emitter.getBrightnessForRender(partialTicks, px, py, pz);
		int lightX = light >> 16 & 65535;
		int lightY = light & 65535;

		Vector4f[] vertices = {
			new Vector4f(-pw / 2, -ph / 2, 0, 1),
			new Vector4f(pw / 2, -ph / 2, 0, 1),
			new Vector4f(pw / 2, ph / 2, 0, 1),
			new Vector4f(- pw / 2, ph / 2, 0, 1)
		};

		Matrix4f matrix4f = new Matrix4f();
		matrix4f.setIdentity();

		Matrix4f rotate = new Matrix4f();

		if (this.facing == CameraFacing.ROTATE_XYZ || this.facing == CameraFacing.LOOKAT_XYZ)
		{
			rotate.rotY(entityYaw / 180 * (float) Math.PI);
			matrix4f.mul(rotate);
			rotate.rotX(entityPitch / 180 * (float) Math.PI);
			matrix4f.mul(rotate);
		}
		else if (this.facing == CameraFacing.ROTATE_Y || this.facing == CameraFacing.LOOKAT_Y) {
			rotate.rotY(entityYaw / 180 * (float) Math.PI);
			matrix4f.mul(rotate);
		}

		rotate.rotZ(angle / 180 * (float) Math.PI);
		matrix4f.mul(rotate);

		matrix4f.setTranslation(new Vector3f((float) px, (float) py, (float) pz));

		for (Vector4f vertex : vertices)
		{
			matrix4f.transform(vertex);
		}

		builder.pos(vertices[0].x, vertices[0].y, vertices[0].z).tex(u1, v1).lightmap(lightX, lightY).color(particle.r, particle.g, particle.b, particle.a).endVertex();
		builder.pos(vertices[1].x, vertices[1].y, vertices[1].z).tex(u2, v1).lightmap(lightX, lightY).color(particle.r, particle.g, particle.b, particle.a).endVertex();
		builder.pos(vertices[2].x, vertices[2].y, vertices[2].z).tex(u2, v2).lightmap(lightX, lightY).color(particle.r, particle.g, particle.b, particle.a).endVertex();
		builder.pos(vertices[3].x, vertices[3].y, vertices[3].z).tex(u1, v2).lightmap(lightX, lightY).color(particle.r, particle.g, particle.b, particle.a).endVertex();
	}

	@Override
	public void postRender(BedrockEmitter emitter, float partialTicks)
	{}
}