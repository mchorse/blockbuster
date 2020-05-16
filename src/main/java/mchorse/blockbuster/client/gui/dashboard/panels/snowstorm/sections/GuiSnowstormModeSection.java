package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public abstract class GuiSnowstormModeSection <T extends BedrockComponentBase> extends GuiSnowstormComponentSection<T>
{
	public GuiCirculateElement mode;
	public GuiLabel modeLabel;

	public GuiSnowstormModeSection(Minecraft mc)
	{
		super(mc);

		this.mode = new GuiCirculateElement(mc, (b) -> this.updateMode(this.mode.getValue()));
		this.fillModes(this.mode);
		this.modeLabel = Elements.label(IKey.lang("blockbuster.gui.snowstorm.mode"), 20).anchor(0, 0.5F);

		this.fields.add(Elements.row(mc, 5, 0, 20, this.modeLabel, this.mode));
	}

	@Override
	protected T getComponent(BedrockScheme scheme)
	{
		return scheme.getOrCreate(this.getBaseClass(), this.getDefaultClass());
	}

	@Override
	protected void fillData()
	{
		for (int i = 0, c = this.mode.getLabels().size(); i < c; i ++)
		{
			if (this.getModeClass(i) == this.component.getClass())
			{
				this.mode.setValue(i);

				break;
			}
		}

		super.fillData();
	}

	protected abstract void fillModes(GuiCirculateElement button);

	protected void updateMode(int value)
	{
		T old = this.component;

		this.component = this.scheme.replace(this.getBaseClass(), this.getModeClass(this.mode.getValue()));
		this.restoreInfo(this.component, old);

		this.fillData();
	}

	protected void restoreInfo(T component, T old)
	{}

	protected abstract Class<T> getBaseClass();

	protected abstract Class getDefaultClass();

	protected abstract Class getModeClass(int value);
}