package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster_pack.morphs.SnowstormMorph;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import net.minecraft.client.Minecraft;

public class GuiSnowstormMorph extends GuiAbstractMorph<SnowstormMorph>
{
	public GuiSnowstormMorph(Minecraft mc)
	{
		super(mc);

		// this.defaultPanel = this.general = new GuiSnowstormMorph.GuiSnowstormMorphPanel(mc, this);
		// this.registerPanel(this.general, I18n.format("blockbuster.gui.snowstorm.tooltip"), BBIcons.PARTICLE);
	}

	@Override
	public boolean canEdit(AbstractMorph morph)
	{
		return morph instanceof SnowstormMorph;
	}
}