package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.meta.BedrockComponentInitialization;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiSnowstormInitializationSection extends GuiSnowstormSection
{
	public GuiTextElement create;
	public GuiTextElement update;

	private BedrockComponentInitialization component;

	public GuiSnowstormInitializationSection(Minecraft mc)
	{
		super(mc, IKey.lang("blockbuster.gui.snowstorm.initialization.title"));

		this.create = new GuiTextElement(mc, 10000, (str) -> this.component.creation = this.parse(str, this.component.creation));
		this.create.tooltip(IKey.lang("blockbuster.gui.snowstorm.initialization.create"));
		this.update = new GuiTextElement(mc, 10000, (str) -> this.component.update = this.parse(str, this.component.update));
		this.update.tooltip(IKey.lang("blockbuster.gui.snowstorm.initialization.update"));

		this.fields.add(this.create, this.update);
	}

	@Override
	public void setScheme(BedrockScheme scheme)
	{
		super.setScheme(scheme);

		this.component = this.scheme.getOrCreate(BedrockComponentInitialization.class);
		this.create.setText(this.component.creation.toString());
		this.update.setText(this.component.update.toString());
	}
}