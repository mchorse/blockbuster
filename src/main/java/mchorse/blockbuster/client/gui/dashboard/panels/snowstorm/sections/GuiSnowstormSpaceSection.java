package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.meta.BedrockComponentLocalSpace;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiSnowstormSpaceSection extends GuiSnowstormSection
{
	public GuiToggleElement position;
	public GuiToggleElement rotation;

	private BedrockComponentLocalSpace component;

	public GuiSnowstormSpaceSection(Minecraft mc)
	{
		super(mc, IKey.lang("blockbuster.gui.snowstorm.space.title"));

		this.position = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.space.position"), (b) -> this.component.position = b.isToggled());
		this.rotation = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.space.rotation"), (b) -> this.component.rotation = b.isToggled());

		this.fields.add(this.position, this.rotation);
	}

	@Override
	public void setScheme(BedrockScheme scheme)
	{
		super.setScheme(scheme);

		this.component = scheme.getOrCreate(BedrockComponentLocalSpace.class);
		this.position.toggled(this.component.position);
		this.rotation.toggled(this.component.rotation);
	}
}