package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.gui.dashboard.GuiBlockbusterPanel;
import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.mclib.client.gui.mclib.GuiDashboard;
import net.minecraft.client.Minecraft;

public class GuiSnowstorm extends GuiBlockbusterPanel
{
	public GuiSnowstormRenderer renderer;

	private BedrockScheme scheme;

	public GuiSnowstorm(Minecraft mc, GuiDashboard dashboard)
	{
		super(mc, dashboard);

		this.renderer = new GuiSnowstormRenderer(mc);
		this.renderer.flex().relative(this).wh(1F, 1F);

		this.add(this.renderer);
	}

	@Override
	public void appear()
	{
		super.appear();

		this.scheme = Blockbuster.proxy.particles.presets.get("default_snow");
		this.renderer.setScheme(this.scheme);
	}

	@Override
	public void close()
	{
		/* TODO: Clean up particles */
	}
}