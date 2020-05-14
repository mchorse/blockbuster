package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiSnowstormSection extends GuiElement
{
	public GuiLabel title;
	public GuiElement fields;

	protected BedrockScheme scheme;

	public GuiSnowstormSection(Minecraft mc, IKey title)
	{
		super(mc);

		this.title = Elements.label(title).background(0x88000000 + McLib.primaryColor.get());
		this.fields = new GuiElement(mc);
		this.fields.flex().column(5).stretch().vertical().height(20);

		this.flex().column(5).stretch().vertical();
		this.add(this.title, this.fields);
	}

	public void setScheme(BedrockScheme scheme)
	{
		this.scheme = scheme;
	}
}