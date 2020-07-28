package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm;

import mchorse.blockbuster.api.formats.obj.Vector3f;
import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.expiration.BedrockComponentKillPlane;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.mclib.client.Draw;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class GuiSnowstormRenderer extends GuiModelRenderer
{
	public BedrockEmitter emitter;

	private Vector3f vector = new Vector3f(0, 0, 0);

	public GuiSnowstormRenderer(Minecraft mc)
	{
		super(mc);

		this.emitter = new BedrockEmitter();
	}

	public void setScheme(BedrockScheme scheme)
	{
		this.emitter = new BedrockEmitter();
		this.emitter.setScheme(scheme);
	}

	@Override
	protected void update()
	{
		super.update();

		if (this.emitter != null)
		{
			this.emitter.rotation.setIdentity();
			this.emitter.update();
		}
	}

	@Override
	protected void drawUserModel(GuiContext context)
	{
		if (this.emitter == null || this.emitter.scheme == null)
		{
			return;
		}

		this.emitter.cYaw = this.yaw;
		this.emitter.cPitch = this.pitch;
		this.emitter.cX = this.temp.x;
		this.emitter.cY = this.temp.y;
		this.emitter.cZ = this.temp.z;
		this.emitter.perspective = 100;
		this.emitter.rotation.setIdentity();

		GlStateManager.disableLighting();

		GlStateManager.disableDepth();
		GlStateManager.glLineWidth(3);
		GlStateManager.disableTexture2D();
		Draw.axis(1F);
		GlStateManager.enableTexture2D();
		GlStateManager.glLineWidth(1);
		GlStateManager.enableDepth();

		this.emitter.render(context.partialTicks);

		BedrockComponentKillPlane plane = this.emitter.scheme.get(BedrockComponentKillPlane.class);

		if (plane.a != 0 || plane.b != 0 || plane.c != 0)
		{
			this.drawKillPlane(plane.a, plane.b, plane.c, plane.d);
		}

		GlStateManager.enableLighting();
	}

	private void drawKillPlane(float a, float b, float c, float d)
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		// GL11.glLineWidth(2F);
		GL11.glPointSize(4F);
		GlStateManager.disableTexture2D();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();

		GlStateManager.color(1F, 1F, 1F);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

		this.calculate(0, 0, a, b, c, d);
		buffer.pos(this.vector.x, this.vector.y, this.vector.z).color(0, 1, 0, 0.5F).endVertex();
		this.calculate(1, 0, a, b, c, d);
		buffer.pos(this.vector.x, this.vector.y, this.vector.z).color(0, 1, 0, 0.5F).endVertex();
		this.calculate(1, 1, a, b, c, d);
		buffer.pos(this.vector.x, this.vector.y, this.vector.z).color(0, 1, 0, 0.5F).endVertex();
		this.calculate(0, 1, a, b, c, d);
		buffer.pos(this.vector.x, this.vector.y, this.vector.z).color(0, 1, 0, 0.5F).endVertex();

		tessellator.draw();

		GlStateManager.enableTexture2D();
	}

	private void calculate(float i, float j, float a, float b, float c, float d)
	{
		final float radius = 5;

		if (b != 0)
		{
			this.vector.x = -radius + radius * 2 * i;
			this.vector.z = -radius + radius * 2 * j;
			this.vector.y = (a * this.vector.x + c * this.vector.z + d) / -b;
		}
		else if (a != 0)
		{
			this.vector.y = -radius + radius * 2 * i;
			this.vector.z = -radius + radius * 2 * j;
			this.vector.x = (b * this.vector.y + c * this.vector.z + d) / -a;
		}
		else if (c != 0)
		{
			this.vector.x = -radius + radius * 2 * i;
			this.vector.y = -radius + radius * 2 * j;
			this.vector.z = (b * this.vector.y + a * this.vector.x + d) / -c;
		}
	}
}