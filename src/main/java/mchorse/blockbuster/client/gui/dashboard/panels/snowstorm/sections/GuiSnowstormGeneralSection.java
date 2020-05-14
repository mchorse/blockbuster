package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.particles.BedrockMaterial;
import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTexturePicker;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiSnowstormGeneralSection extends GuiSnowstormSection
{
	public GuiTextElement identifier;
	public GuiButtonElement pick;
	public GuiCirculateElement material;
	public GuiTexturePicker texture;

	public GuiSnowstormGeneralSection(Minecraft mc)
	{
		super(mc);

		this.identifier = new GuiTextElement(mc, (str) -> this.scheme.identifier = str);
		this.pick = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.snowstorm.general.pick"), (b) ->
		{
			GuiElement parent = this.getParentContainer();

			this.texture.fill(this.scheme.texture);
			this.texture.flex().relative(parent).wh(1F, 1F);
			this.texture.resize();
			parent.add(this.texture);
		});
		this.material = new GuiCirculateElement(mc, (b) -> this.scheme.material = BedrockMaterial.values()[this.material.getValue()]);
		this.material.addLabel(IKey.lang("blockbuster.gui.snowstorm.general.particles_opaque"));
		this.material.addLabel(IKey.lang("blockbuster.gui.snowstorm.general.particles_alpha"));
		this.material.addLabel(IKey.lang("blockbuster.gui.snowstorm.general.particles_blend"));
		this.texture = new GuiTexturePicker(mc, (rl) ->
		{
			if (rl == null)
			{
				rl = BedrockScheme.DEFAULT_TEXTURE;
			}

			this.scheme.texture = rl;

			/* TODO: set texture size */
		});

		this.fields.add(this.identifier, Elements.row(mc, 5, 0, 20, this.pick, this.material));
	}

	@Override
	public String getTitle()
	{
		return "blockbuster.gui.snowstorm.general.title";
	}

	@Override
	public void setScheme(BedrockScheme scheme)
	{
		super.setScheme(scheme);

		this.identifier.setText(scheme.identifier);
		this.material.setValue(scheme.material.ordinal());
	}
}