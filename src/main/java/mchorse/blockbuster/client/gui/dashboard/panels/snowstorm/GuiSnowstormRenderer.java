package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm;

import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class GuiSnowstormRenderer extends GuiModelRenderer
{
	public BedrockEmitter emitter;

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
		if (this.emitter == null)
		{
			return;
		}

		this.emitter.cYaw = 180 - this.yaw;
		this.emitter.cPitch = this.pitch;
		this.emitter.cX = this.temp.x;
		this.emitter.cY = this.temp.y;
		this.emitter.cZ = this.temp.z;
		this.emitter.rotation.setIdentity();

		GlStateManager.disableLighting();
		this.emitter.render(context.partialTicks);
		GlStateManager.enableLighting();
	}
}