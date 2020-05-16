package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSnowstorm;
import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import net.minecraft.client.Minecraft;

public abstract class GuiSnowstormComponentSection <T extends BedrockComponentBase> extends GuiSnowstormSection
{
	protected T component;

	public GuiSnowstormComponentSection(Minecraft mc, GuiSnowstorm parent)
	{
		super(mc, parent);
	}

	@Override
	public void setScheme(BedrockScheme scheme)
	{
		super.setScheme(scheme);

		this.component = this.getComponent(scheme);
		this.fillData();
	}

	protected abstract T getComponent(BedrockScheme scheme);

	protected void fillData()
	{}
}