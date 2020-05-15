package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.appearance.BedrockComponentAppearanceLighting;
import mchorse.blockbuster.client.particles.components.appearance.BedrockComponentAppearanceTinting;
import mchorse.blockbuster.client.particles.components.appearance.Tint;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;
import mchorse.blockbuster.client.particles.molang.expressions.MolangValue;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiColorElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.math.Constant;
import mchorse.mclib.utils.Color;
import net.minecraft.client.Minecraft;

public class GuiSnowstormLightingSection extends GuiSnowstormSection
{
	public GuiCirculateElement mode;
	public GuiColorElement color;
	public GuiTextElement r;
	public GuiTextElement g;
	public GuiTextElement b;
	public GuiTextElement a;
	public GuiToggleElement lighting;

	public GuiElement first;
	public GuiElement second;

	private BedrockComponentAppearanceTinting component;

	public GuiSnowstormLightingSection(Minecraft mc)
	{
		super(mc);

		this.mode = new GuiCirculateElement(mc, (b) -> this.updateElements());
		this.mode.addLabel(IKey.lang("blockbuster.gui.snowstorm.lighting.solid"));
		this.mode.addLabel(IKey.lang("blockbuster.gui.snowstorm.lighting.expression"));

		this.color = new GuiColorElement(mc, (color) ->
		{
			Tint.Solid solid = this.getSolid();
			Color original = this.color.picker.color;

			solid.r = this.set(solid.r, original.r);
			solid.g = this.set(solid.g, original.g);
			solid.b = this.set(solid.b, original.b);
			solid.a = this.set(solid.a, original.a);
		});
		this.color.picker.editAlpha();

		this.r = new GuiTextElement(mc, (str) ->
		{
			Tint.Solid solid = this.getSolid();

			solid.r = this.parse(str, solid.r);
		});
		this.r.tooltip(IKey.lang("blockbuster.gui.snowstorm.lighting.red"));

		this.g = new GuiTextElement(mc, (str) ->
		{
			Tint.Solid solid = this.getSolid();

			solid.g = this.parse(str, solid.g);
		});
		this.g.tooltip(IKey.lang("blockbuster.gui.snowstorm.lighting.green"));

		this.b = new GuiTextElement(mc, (str) ->
		{
			Tint.Solid solid = this.getSolid();

			solid.b = this.parse(str, solid.b);
		});
		this.b.tooltip(IKey.lang("blockbuster.gui.snowstorm.lighting.blue"));

		this.a = new GuiTextElement(mc, (str) ->
		{
			Tint.Solid solid = this.getSolid();

			solid.a = this.parse(str, solid.a);
		});
		this.a.tooltip(IKey.lang("blockbuster.gui.snowstorm.lighting.alpha"));

		this.lighting = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.lighting.lighting"), null);

		GuiLabel label = Elements.label(IKey.lang("blockbuster.gui.snowstorm.mode"), 20).anchor(0, 0.5F);

		this.first = Elements.row(mc, 5, 0, 20, this.r, this.g);
		this.second = Elements.row(mc, 5, 0, 20, this.b, this.a);

		this.fields.add(this.lighting);
		this.fields.add(Elements.row(mc, 5, 0, 20, label, this.mode));
	}

	private MolangExpression set(MolangExpression expression, float value)
	{
		if (expression == MolangParser.ZERO || expression == MolangParser.ONE)
		{
			return new MolangValue(null, new Constant(value));
		}

		if (!(expression instanceof MolangValue))
		{
			expression = new MolangValue(null, new Constant(0));
		}

		if (expression instanceof MolangValue)
		{
			MolangValue v = (MolangValue) expression;

			if (!(v.value instanceof Constant))
			{
				v.value = new Constant(0);
			}

			if (v.value instanceof Constant)
			{
				((Constant) v.value).set(value);
			}
		}

		return expression;
	}

	private void updateElements()
	{
		this.fillData();
	}

	private Tint.Solid getSolid()
	{
		return (Tint.Solid) this.component.color;
	}

	@Override
	public String getTitle()
	{
		return "blockbuster.gui.snowstorm.lighting.title";
	}

	@Override
	public void beforeSave(BedrockScheme scheme)
	{
		if (this.lighting.isToggled())
		{
			scheme.getOrCreate(BedrockComponentAppearanceLighting.class);
		}
		else
		{
			scheme.remove(BedrockComponentAppearanceLighting.class);
		}
	}

	@Override
	public void setScheme(BedrockScheme scheme)
	{
		super.setScheme(scheme);

		this.component = scheme.getOrCreate(BedrockComponentAppearanceTinting.class);
		this.lighting.toggled(scheme.get(BedrockComponentAppearanceLighting.class) != null);

		if (this.component.color instanceof Tint.Solid)
		{
			Tint.Solid solid = this.getSolid();

			if (solid.isConstant())
			{
				this.mode.setValue(0);
			}
			else
			{
				this.mode.setValue(1);
			}

			this.fillData();
		}
	}

	public void fillData()
	{
		Tint.Solid solid = this.getSolid();

		this.color.removeFromParent();
		this.color.picker.removeFromParent();
		this.first.removeFromParent();
		this.second.removeFromParent();

		if (this.mode.getValue() == 0)
		{
			this.color.picker.color.set((float) solid.r.get(), (float) solid.g.get(), (float) solid.b.get(), (float) solid.a.get());

			this.fields.add(this.color);
		}
		else
		{
			this.r.setText(solid.r.toString());
			this.g.setText(solid.g.toString());
			this.b.setText(solid.b.toString());
			this.a.setText(solid.a.toString());

			this.fields.add(this.first);
			this.fields.add(this.second);
		}

		this.resizeParent();
	}
}