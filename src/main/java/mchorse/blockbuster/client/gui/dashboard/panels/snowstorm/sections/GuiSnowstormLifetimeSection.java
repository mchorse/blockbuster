package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.lifetime.BedrockComponentLifetime;
import mchorse.blockbuster.client.particles.components.lifetime.BedrockComponentLifetimeExpression;
import mchorse.blockbuster.client.particles.components.lifetime.BedrockComponentLifetimeLooping;
import mchorse.blockbuster.client.particles.components.lifetime.BedrockComponentLifetimeOnce;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiSnowstormLifetimeSection extends GuiSnowstormComponentSection<BedrockComponentLifetime>
{
	public GuiCirculateElement mode;
	public GuiTextElement active;
	public GuiTextElement expiration;

	public GuiSnowstormLifetimeSection(Minecraft mc)
	{
		super(mc);

		this.mode = new GuiCirculateElement(mc, (b) -> this.switchMode());
		this.mode.addLabel(IKey.lang("blockbuster.gui.snowstorm.lifetime.expression"));
		this.mode.addLabel(IKey.lang("blockbuster.gui.snowstorm.lifetime.looping"));
		this.mode.addLabel(IKey.lang("blockbuster.gui.snowstorm.lifetime.once"));
		this.active = new GuiTextElement(mc, (str) -> this.component.activeTime = this.parse(str, this.component.activeTime));
		this.active.tooltip(IKey.lang(""));
		this.expiration = new GuiTextElement(mc, (str) ->
		{
			if (this.component instanceof BedrockComponentLifetimeLooping)
			{
				BedrockComponentLifetimeLooping component = (BedrockComponentLifetimeLooping) this.component;

				component.sleepTime = this.parse(str, component.sleepTime);
			}
			else
			{
				BedrockComponentLifetimeExpression component = (BedrockComponentLifetimeExpression) this.component;

				component.expiration = this.parse(str, component.expiration);
			}
		});
		this.expiration.tooltip(IKey.lang(""));

		GuiLabel label = Elements.label(IKey.lang("blockbuster.gui.snowstorm.mode"), 20).anchor(0, 0.5F);

		this.fields.add(Elements.row(mc, 5, 0, 20, label, this.mode));
		this.fields.add(this.active);
	}

	private void switchMode()
	{
		BedrockComponentLifetime old = this.component;
		Class clazz = BedrockComponentLifetimeOnce.class;

		if (this.mode.getValue() == 0)
		{
			clazz = BedrockComponentLifetimeExpression.class;
		}
		else if (this.mode.getValue() == 1)
		{
			clazz = BedrockComponentLifetimeLooping.class;
		}

		this.component = this.scheme.replace(BedrockComponentLifetime.class, clazz);
		this.component.activeTime = old.activeTime;

		this.fillData();
	}

	@Override
	public String getTitle()
	{
		return "blockbuster.gui.snowstorm.lifetime.title";
	}

	@Override
	protected BedrockComponentLifetime getComponent(BedrockScheme scheme)
	{
		return scheme.getOrCreate(BedrockComponentLifetime.class, BedrockComponentLifetimeLooping.class);
	}

	@Override
	protected void fillData()
	{
		boolean once = this.component instanceof BedrockComponentLifetimeOnce;

		this.expiration.setVisible(!once);

		if (this.component instanceof BedrockComponentLifetimeExpression)
		{
			this.mode.setValue(0);
			this.expiration.setText(((BedrockComponentLifetimeExpression) this.component).expiration.toString());
			this.expiration.tooltip.label.set("blockbuster.gui.snowstorm.lifetime.expiration_expression");

			this.active.tooltip.label.set("blockbuster.gui.snowstorm.lifetime.active_expression");
		}
		else if (this.component instanceof BedrockComponentLifetimeLooping)
		{
			this.mode.setValue(1);
			this.expiration.setText(((BedrockComponentLifetimeLooping) this.component).sleepTime.toString());
			this.expiration.tooltip.label.set("blockbuster.gui.snowstorm.lifetime.sleep_time");

			this.active.tooltip.label.set("blockbuster.gui.snowstorm.lifetime.active_looping");
		}
		else
		{
			this.mode.setValue(2);

			this.active.tooltip.label.set("blockbuster.gui.snowstorm.lifetime.active_once");
		}

		this.active.setText(this.component.activeTime.toString());
		this.expiration.removeFromParent();

		if (!once)
		{
			this.fields.add(this.expiration);
		}

		this.resizeParent();
	}
}