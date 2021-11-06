package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSnowstorm;
import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.appearance.BedrockComponentAppearanceLighting;
import mchorse.blockbuster.client.particles.components.appearance.BedrockComponentAppearanceTinting;
import mchorse.blockbuster.client.particles.components.appearance.Tint;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiColorElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.math.Constant;
import mchorse.mclib.math.molang.MolangParser;
import mchorse.mclib.math.molang.expressions.MolangExpression;
import mchorse.mclib.math.molang.expressions.MolangValue;
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
    public GuiTextElement interpolant;
    public GuiTrackpadElement range;
    public GuiToggleElement lighting;

    public GuiElement gradientElements;
    public GuiElement first;
    public GuiElement second;

    /** Solid, Expression, Gradient */
    protected final Tint[] tints = {new Tint.Solid(), new Tint.Solid(), new Tint.Gradient()};

    protected BedrockComponentAppearanceTinting component;

    public GuiSnowstormLightingSection(Minecraft mc, GuiSnowstorm parent)
    {
        super(mc, parent);

        this.mode = new GuiCirculateElement(mc, (b) ->
        {
            this.component.color = this.tints[b.getValue()];
            this.updateElements();
            this.parent.dirty();
        });
        this.mode.addLabel(IKey.lang("blockbuster.gui.snowstorm.lighting.solid"));
        this.mode.addLabel(IKey.lang("blockbuster.gui.snowstorm.lighting.expression"));
        this.mode.addLabel(IKey.lang("blockbuster.gui.snowstorm.lighting.gradient"));

        this.color = new GuiColorElement(mc, (color) ->
        {
            Tint.Solid solid = this.getSolid();
            Color original = this.color.picker.color;

            solid.r = this.set(solid.r, original.r);
            solid.g = this.set(solid.g, original.g);
            solid.b = this.set(solid.b, original.b);
            solid.a = this.set(solid.a, original.a);
            this.parent.dirty();
        });
        this.color.picker.editAlpha();

        this.r = new GuiTextElement(mc, 10000, (str) ->
        {
            Tint.Solid solid = this.getSolid();

            solid.r = this.parse(str, this.r, solid.r);
        });
        this.r.tooltip(IKey.lang("blockbuster.gui.snowstorm.lighting.red"));

        this.g = new GuiTextElement(mc, 10000, (str) ->
        {
            Tint.Solid solid = this.getSolid();

            solid.g = this.parse(str, this.r, solid.g);
        });
        this.g.tooltip(IKey.lang("blockbuster.gui.snowstorm.lighting.green"));

        this.b = new GuiTextElement(mc, 10000, (str) ->
        {
            Tint.Solid solid = this.getSolid();

            solid.b = this.parse(str, this.r, solid.b);
        });
        this.b.tooltip(IKey.lang("blockbuster.gui.snowstorm.lighting.blue"));

        this.a = new GuiTextElement(mc, 10000, (str) ->
        {
            Tint.Solid solid = this.getSolid();

            solid.a = this.parse(str, this.r, solid.a);
        });
        this.a.tooltip(IKey.lang("blockbuster.gui.snowstorm.lighting.alpha"));

        this.lighting = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.lighting.lighting"), (b) -> this.parent.dirty());
        this.lighting.tooltip(IKey.lang("blockbuster.gui.snowstorm.lighting.lighting_tooltip"));

        GuiLabel label = Elements.label(IKey.lang("blockbuster.gui.snowstorm.mode"), 20).anchor(0, 0.5F);

        this.interpolant = new GuiTextElement(mc, 10000, (str) ->
        {
            Tint.Gradient gradient = this.getGradient();
            gradient.interpolant = this.parse(str, this.interpolant, gradient.interpolant);

            this.parent.dirty();
        });
        this.interpolant.tooltip(IKey.lang("blockbuster.gui.snowstorm.lighting.interpolant_tooltip"));

        this.range = new GuiTrackpadElement(mc, (value) ->
        {
            Tint.Gradient gradient = this.getGradient();
            gradient.range = value.floatValue();

            this.parent.dirty();
        });
        this.range.tooltip(IKey.lang("blockbuster.gui.snowstorm.lighting.range_tooltip"));

        this.gradientElements = new GuiElement(mc);
        this.gradientElements.flex().column(4).stretch().vertical().height(4);

        this.gradientElements.add(this.interpolant, this.range);

        this.first = Elements.row(mc, 5, 0, 20, this.r, this.g);
        this.second = Elements.row(mc, 5, 0, 20, this.b, this.a);

        this.fields.add(this.lighting);
        this.fields.add(Elements.row(mc, 5, 0, 20, label, this.mode));
    }

    protected MolangExpression set(MolangExpression expression, float value)
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

    @Override
    public String getTitle()
    {
        return "blockbuster.gui.snowstorm.lighting.title";
    }

    protected Tint.Solid getSolid()
    {
        return (Tint.Solid) this.component.color;
    }

    protected Tint.Gradient getGradient()
    {
        return (Tint.Gradient) this.component.color;
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

        this.component = scheme.getOrCreateExact(BedrockComponentAppearanceTinting.class);

        this.lighting.toggled(scheme.get(BedrockComponentAppearanceLighting.class) != null);

        this.fillData();
    }

    protected void fillData()
    {
        if (this.component.color instanceof Tint.Solid)
        {
            Tint.Solid solid = this.getSolid();

            if (solid.isConstant())
            {
                this.tints[0] = solid;

                this.color.picker.color.set((float) solid.r.get(), (float) solid.g.get(), (float) solid.b.get(), (float) solid.a.get());
                this.mode.setValue(0);
            }
            else
            {
                this.tints[1] = solid;

                this.set(this.r, solid.r);
                this.set(this.g, solid.g);
                this.set(this.b, solid.b);
                this.set(this.a, solid.a);
                this.mode.setValue(1);
            }
        }
        else if (this.component.color instanceof Tint.Gradient)
        {
            Tint.Gradient gradient = this.getGradient();
            this.tints[2] = gradient;

            this.set(this.interpolant, gradient.interpolant);
            this.range.setValue(gradient.range);
            this.mode.setValue(2);
        }

        this.updateElements();
    }

    public void updateElements()
    {
        this.gradientElements.removeFromParent();
        this.color.removeFromParent();
        this.color.picker.removeFromParent();
        this.first.removeFromParent();
        this.second.removeFromParent();

        if (this.mode.getValue() == 0)
        {
            this.fields.add(this.color);
        }
        else if(this.mode.getValue() == 1)
        {
            this.fields.add(this.first);
            this.fields.add(this.second);
        }
        else if(this.mode.getValue() == 2)
        {
            this.fields.add(this.gradientElements);
        }

        this.resizeParent();
    }
}