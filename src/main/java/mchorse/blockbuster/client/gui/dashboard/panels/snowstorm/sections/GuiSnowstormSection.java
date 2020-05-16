package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;
import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public abstract class GuiSnowstormSection extends GuiElement
{
	public GuiLabel title;
	public GuiElement fields;

	protected BedrockScheme scheme;

	public GuiSnowstormSection(Minecraft mc)
	{
		super(mc);

		this.title = Elements.label(IKey.lang(this.getTitle())).background(0x88000000 + McLib.primaryColor.get());
		this.fields = new GuiElement(mc);
		this.fields.flex().column(5).stretch().vertical().height(20);

		this.flex().column(5).stretch().vertical();
		this.add(this.title, this.fields);
	}

	public abstract String getTitle();

	public MolangExpression parse(String string, GuiTextElement element, MolangExpression old)
	{
		if (string.isEmpty())
		{
			return MolangParser.ZERO;
		}

		try
		{
			MolangExpression expression = this.scheme.parser.parseExpression(string);

			element.field.setTextColor(0xffffff);

			return expression;
		}
		catch (Exception e)
		{}

		element.field.setTextColor(0xff2244);

		return old;
	}

	public void set(GuiTextElement element, MolangExpression expression)
	{
		element.field.setTextColor(0xffffff);
		element.setText(expression.toString());
	}

	public void setScheme(BedrockScheme scheme)
	{
		this.scheme = scheme;
	}

	public void beforeSave(BedrockScheme scheme)
	{}

	/**
	 * Toggle visibility of the field section
	 */
	@Override
	public boolean mouseClicked(GuiContext context)
	{
		if (super.mouseClicked(context))
		{
			return true;
		}

		if (this.title.area.isInside(context))
		{
			if (this.fields.hasParent())
			{
				this.fields.removeFromParent();
			}
			else
			{
				this.add(this.fields);
			}

			this.resizeParent();

			return true;
		}

		return false;
	}

	protected void resizeParent()
	{
		this.getParent().resize();
	}
}