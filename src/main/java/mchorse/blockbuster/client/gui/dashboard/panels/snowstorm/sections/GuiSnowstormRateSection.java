package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.rate.BedrockComponentRate;
import mchorse.blockbuster.client.particles.components.rate.BedrockComponentRateInstant;
import mchorse.blockbuster.client.particles.components.rate.BedrockComponentRateSteady;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiSnowstormRateSection extends GuiSnowstormComponentSection<BedrockComponentRate>
{
	public GuiCirculateElement mode;
	public GuiTextElement rate;
	public GuiTextElement particles;

	public GuiSnowstormRateSection(Minecraft mc)
	{
		super(mc);

		this.mode = new GuiCirculateElement(mc, (b) -> this.toggleMode());
		this.mode.addLabel(IKey.lang("blockbuster.gui.snowstorm.rate.instant"));
		this.mode.addLabel(IKey.lang("blockbuster.gui.snowstorm.rate.steady"));
		this.rate = new GuiTextElement(mc, 10000, (str) ->
		{
			BedrockComponentRateSteady comp = (BedrockComponentRateSteady) this.component;

			comp.spawnRate = this.parse(str, comp.spawnRate);
		});
		this.rate.tooltip(IKey.lang("blockbuster.gui.snowstorm.rate.spawn_rate"));
		this.particles = new GuiTextElement(mc, 10000, (str) -> this.component.particles = this.parse(str, this.component.particles));
		this.particles.tooltip(IKey.lang(""));

		GuiElement row = Elements.row(mc, 5, 0, 20, Elements.label(IKey.lang("blockbuster.gui.snowstorm.mode"), 20).anchor(0, 0.5F), this.mode);

		this.fields.add(row, this.particles);
	}

	@Override
	public String getTitle()
	{
		return "blockbuster.gui.snowstorm.rate.title";
	}

	private void toggleMode()
	{
		BedrockComponentRate old = this.component;

		this.component = this.scheme.replace(BedrockComponentRate.class, this.isInstant() ? BedrockComponentRateSteady.class : BedrockComponentRateInstant.class);
		this.component.particles = old.particles;
		this.fillData();
	}

	private boolean isInstant()
	{
		return this.component instanceof BedrockComponentRateInstant;
	}

	private void updateVisibility()
	{
		if (this.isInstant())
		{
			this.rate.removeFromParent();
		}
		else if (!this.rate.hasParent())
		{
			this.fields.add(this.rate);
		}

		this.resizeParent();
	}

	@Override
	protected BedrockComponentRate getComponent(BedrockScheme scheme)
	{
		return scheme.getOrCreate(BedrockComponentRate.class, BedrockComponentRateInstant.class);
	}

	@Override
	protected void fillData()
	{
		this.updateVisibility();
		this.mode.setValue(this.isInstant() ? 0 : 1);
		this.particles.setText(this.component.particles.toString());
		this.particles.tooltip.label.set(this.isInstant() ? "blockbuster.gui.snowstorm.rate.particles" : "blockbuster.gui.snowstorm.rate.max_particles");

		if (this.component instanceof BedrockComponentRateSteady)
		{
			this.rate.setText(((BedrockComponentRateSteady) this.component).spawnRate.toString());
		}
	}
}