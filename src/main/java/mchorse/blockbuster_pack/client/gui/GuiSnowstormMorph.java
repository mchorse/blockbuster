package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster_pack.morphs.SnowstormMorph;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiSnowstormMorph extends GuiAbstractMorph<SnowstormMorph>
{
	public GuiSnowstormMorphPanel general;

	public GuiSnowstormMorph(Minecraft mc)
	{
		super(mc);

		this.defaultPanel = this.general = new GuiSnowstormMorph.GuiSnowstormMorphPanel(mc, this);
		this.registerPanel(this.general, GuiDashboard.GUI_ICONS, I18n.format("blockbuster.gui.snowstorm.tooltip"), 48, 96, 48, 112);
	}

	@Override
	public boolean canEdit(AbstractMorph morph)
	{
		return morph instanceof SnowstormMorph;
	}

	public static class GuiSnowstormMorphPanel extends GuiMorphPanel<SnowstormMorph, GuiSnowstormMorph>
	{
		public GuiButtonElement<GuiCheckBox> local;

		public GuiSnowstormMorphPanel(Minecraft mc, GuiSnowstormMorph editor)
		{
			super(mc, editor);

			this.local = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.snowstorm.local"), false, (b) ->
			{
				this.morph.local = b.button.isChecked();
			});

			this.local.resizer().parent(this.area).set(10, 10, 80, 11);

			this.children.add(this.local);
		}

		@Override
		public void fillData(SnowstormMorph morph)
		{
			super.fillData(morph);

			this.local.button.setIsChecked(morph.local);
		}
	}
}