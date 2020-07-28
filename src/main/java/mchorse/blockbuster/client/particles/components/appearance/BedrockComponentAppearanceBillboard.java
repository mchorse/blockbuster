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
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class BedrockComponentAppearanceBillboard extends BedrockComponentBase implements IComponentParticleRender
{
	/* Options */
	public MolangExpression sizeW = MolangParser.ZERO;
	public MolangExpression sizeH = MolangParser.ZERO;
	public CameraFacing facing = CameraFacing.LOOKAT_XYZ;
	public int textureWidth = 128;
	public int textureHeight = 128;
	public MolangExpression uvX = MolangParser.ZERO;
	public MolangExpression uvY = MolangParser.ZERO;
	public MolangExpression uvW = MolangParser.ZERO;
	public MolangExpression uvH = MolangParser.ZERO;

	public boolean flipbook = false;
	public float stepX;
	public float stepY;
	public float fps;
	public MolangExpression maxFrame = MolangParser.ZERO;
	public boolean stretchFPS = false;
	public boolean loop = false;

	/* Runtime properties */
	private float w;
	private float h;

	private float u1;
	private float v1;
	private float u2;
	private float v2;

	private Matrix4f transform = new Matrix4f();
	private Matrix4f rotation = new Matrix4f();
	private Vector4f[] vertices = new Vector4f[] {
		new Vector4f(0, 0, 0, 1),
		new Vector4f(0, 0, 0, 1),
		new Vector4f(0, 0, 0, 1),
		new Vector4f(0, 0, 0, 1)
	};
	private Vector3f vector = new Vector3f();

	public BedrockComponentAppearanceBillboard()
	{}

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
	public JsonElement toJson()
	{
		JsonObject object = new JsonObject();
		JsonArray size = new JsonArray();
		JsonObject uv = new JsonObject();

		size.add(this.sizeW.toJson());
		size.add(this.sizeH.toJson());

		/* Adding "uv" properties */
		uv.addProperty("texture_width", this.textureWidth);
		uv.addProperty("texture_height", this.textureHeight);

		if (!this.flipbook && !MolangExpression.isZero(this.uvX) || !MolangExpression.isZero(this.uvY))
		{
			JsonArray uvs = new JsonArray();
			uvs.add(this.uvX.toJson());
			uvs.add(this.uvY.toJson());

			uv.add("uv", uvs);
		}

		if (!this.flipbook && !MolangExpression.isZero(this.uvW) || !MolangExpression.isZero(this.uvH))
		{
			JsonArray uvs = new JsonArray();
			uvs.add(this.uvW.toJson());
			uvs.add(this.uvH.toJson());

			uv.add("uv_size", uvs);
		}

		/* Adding "flipbook" properties to "uv" */
		if (this.flipbook)
		{
			JsonObject flipbook = new JsonObject();

			if (!MolangExpression.isZero(this.uvX) || !MolangExpression.isZero(this.uvY))
			{
				JsonArray base = new JsonArray();
				base.add(this.uvX.toJson());
				base.add(this.uvY.toJson());

				flipbook.add("base_UV", base);
			}

			if (!MolangExpression.isZero(this.uvW) || !MolangExpression.isZero(this.uvH))
			{
				JsonArray uvSize = new JsonArray();
				uvSize.add(this.uvW.toJson());
				uvSize.add(this.uvH.toJson());

				flipbook.add("size_UV", uvSize);
			}

			if (this.stepX != 0 || this.stepY != 0)
			{
				JsonArray step = new JsonArray();
				step.add(this.stepX);
				step.add(this.stepY);

				flipbook.add("step_UV", step);
			}

			if (this.fps != 0) flipbook.addProperty("frames_per_second", this.fps);
			if (!MolangExpression.isZero(this.maxFrame)) flipbook.add("max_frame", this.maxFrame.toJson());
			if (this.stretchFPS) flipbook.addProperty("stretch_to_lifetime", true);
			if (this.loop) flipbook.addProperty("loop", true);

			uv.add("flipbook", flipbook);
		}

		/* Add main properties */
		object.add("size", size);
		object.addProperty("facing_camera_mode", this.facing.id);
		object.add("uv", uv);

		return object;
	}

	@Override
	public void preRender(BedrockEmitter emitter, float partialTicks)
	{}

	@Override
	public void render(BedrockEmitter emitter, BedrockParticle particle, BufferBuilder builder, float partialTicks)
	{
		this.calculateUVs(particle, partialTicks);

		/* Render the particle */
		double px = Interpolations.lerp(particle.prevPosition.x, particle.position.x, partialTicks);
		double py = Interpolations.lerp(particle.prevPosition.y, particle.position.y, partialTicks);
		double pz = Interpolations.lerp(particle.prevPosition.z, particle.position.z, partialTicks);
		float angle = Interpolations.lerp(particle.prevRotation, particle.rotation, partialTicks);

		if (particle.relativePosition && particle.relativeRotation)
		{
			this.vector.set((float) px, (float) py, (float) pz);
			emitter.rotation.transform(this.vector);

			px = this.vector.x;
			py = this.vector.y;
			pz = this.vector.z;

			px += emitter.lastGlobal.x;
			py += emitter.lastGlobal.y;
			pz += emitter.lastGlobal.z;
		}

		/* Calculate yaw and pitch based on the facing mode */
		float entityYaw = emitter.cYaw;
		float entityPitch = emitter.cPitch;
		double entityX = emitter.cX;
		double entityY = emitter.cY;
		double entityZ = emitter.cZ;
		boolean lookAt = this.facing == CameraFacing.LOOKAT_XYZ || this.facing == CameraFacing.LOOKAT_Y;

		/* Flip width when frontal perspective mode */
		if (emitter.perspective == 2)
		{
			this.w = -this.w;
		}
		/* In GUI renderer */
		else if (emitter.perspective == 100 && !lookAt)
		{
			entityYaw = 180 - entityYaw;

			this.w = -this.w;
			this.h = -this.h;
		}

		if (lookAt)
		{
			double dX = entityX - px;
			double dY = entityY - py;
			double dZ = entityZ - pz;
			double horizontalDistance = MathHelper.sqrt(dX * dX + dZ * dZ);

			entityYaw = 180 - (float) (MathHelper.atan2(dZ, dX) * (180D / Math.PI)) - 90.0F;
			entityPitch = (float) (-(MathHelper.atan2(dY, horizontalDistance) * (180D / Math.PI))) + 180;
		}

		/* Calculate the geometry for billboards using cool matrix math */
		int light = emitter.getBrightnessForRender(partialTicks, px, py, pz);
		int lightX = light >> 16 & 65535;
		int lightY = light & 65535;

		this.vertices[0].set(-this.w / 2, -this.h / 2, 0, 1);
		this.vertices[1].set(this.w / 2, -this.h / 2, 0, 1);
		this.vertices[2].set(this.w / 2, this.h / 2, 0, 1);
		this.vertices[3].set(-this.w / 2, this.h / 2, 0, 1);
		this.transform.setIdentity();

		if (this.facing == CameraFacing.ROTATE_XYZ || this.facing == CameraFacing.LOOKAT_XYZ)
		{
			this.rotation.rotY(entityYaw / 180 * (float) Math.PI);
			this.transform.mul(this.rotation);
			this.rotation.rotX(entityPitch / 180 * (float) Math.PI);
			this.transform.mul(this.rotation);
		}
		else if (this.facing == CameraFacing.ROTATE_Y || this.facing == CameraFacing.LOOKAT_Y) {
			this.rotation.rotY(entityYaw / 180 * (float) Math.PI);
			this.transform.mul(this.rotation);
		}

		this.rotation.rotZ(angle / 180 * (float) Math.PI);
		this.transform.mul(this.rotation);
		this.transform.setTranslation(new Vector3f((float) px, (float) py, (float) pz));

		for (Vector4f vertex : this.vertices)
		{
			this.transform.transform(vertex);
		}

		float u1 = this.u1 / (float) this.textureWidth;
		float u2 = this.u2 / (float) this.textureWidth;
		float v1 = this.v1 / (float) this.textureHeight;
		float v2 = this.v2 / (float) this.textureHeight;

		builder.pos(this.vertices[0].x, this.vertices[0].y, this.vertices[0].z).tex(u1, v1).lightmap(lightX, lightY).color(particle.r, particle.g, particle.b, particle.a).endVertex();
		builder.pos(this.vertices[1].x, this.vertices[1].y, this.vertices[1].z).tex(u2, v1).lightmap(lightX, lightY).color(particle.r, particle.g, particle.b, particle.a).endVertex();
		builder.pos(this.vertices[2].x, this.vertices[2].y, this.vertices[2].z).tex(u2, v2).lightmap(lightX, lightY).color(particle.r, particle.g, particle.b, particle.a).endVertex();
		builder.pos(this.vertices[3].x, this.vertices[3].y, this.vertices[3].z).tex(u1, v2).lightmap(lightX, lightY).color(particle.r, particle.g, particle.b, particle.a).endVertex();
	}

	@Override
	public void renderOnScreen(BedrockParticle particle, int x, int y, float scale, float partialTicks)
	{
		this.calculateUVs(particle, partialTicks);

		this.w = this.h = 0.5F;
		float angle = Interpolations.lerp(particle.prevRotation, particle.rotation, partialTicks);

		/* Calculate the geometry for billboards using cool matrix math */
		this.vertices[0].set(-this.w / 2, -this.h / 2, 0, 1);
		this.vertices[1].set(this.w / 2, -this.h / 2, 0, 1);
		this.vertices[2].set(this.w / 2, this.h / 2, 0, 1);
		this.vertices[3].set(-this.w / 2, this.h / 2, 0, 1);
		this.transform.setIdentity();
		this.transform.setScale(scale * 2.75F);
		this.transform.setTranslation(new Vector3f(x, y - scale / 2, 0));

		this.rotation.rotZ(angle / 180 * (float) Math.PI);
		this.transform.mul(this.rotation);

		for (Vector4f vertex : this.vertices)
		{
			this.transform.transform(vertex);
		}

		float u1 = this.u1 / (float) this.textureWidth;
		float u2 = this.u2 / (float) this.textureWidth;
		float v1 = this.v1 / (float) this.textureHeight;
		float v2 = this.v2 / (float) this.textureHeight;

		BufferBuilder builder = Tessellator.getInstance().getBuffer();

		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		builder.pos(this.vertices[0].x, this.vertices[0].y, this.vertices[0].z).tex(u1, v1).color(particle.r, particle.g, particle.b, particle.a).endVertex();
		builder.pos(this.vertices[1].x, this.vertices[1].y, this.vertices[1].z).tex(u2, v1).color(particle.r, particle.g, particle.b, particle.a).endVertex();
		builder.pos(this.vertices[2].x, this.vertices[2].y, this.vertices[2].z).tex(u2, v2).color(particle.r, particle.g, particle.b, particle.a).endVertex();
		builder.pos(this.vertices[3].x, this.vertices[3].y, this.vertices[3].z).tex(u1, v2).color(particle.r, particle.g, particle.b, particle.a).endVertex();

		Tessellator.getInstance().draw();
	}

	public void calculateUVs(BedrockParticle particle, float partialTicks)
	{
		/* Update particle's UVs and size */
		this.w = (float) this.sizeW.get() * 2.25F;
		this.h = (float) this.sizeH.get() * 2.25F;

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
				float lifetime = particle.lifetime <= 0 ? 0 : (particle.age + partialTicks) / particle.lifetime;

				index = (int) (lifetime * max);
			}

			if (this.loop && max != 0)
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

		this.u1 = u;
		this.v1 = v;
		this.u2 = u + w;
		this.v2 = v + h;
	}

	@Override
	public void postRender(BedrockEmitter emitter, float partialTicks)
	{}
}