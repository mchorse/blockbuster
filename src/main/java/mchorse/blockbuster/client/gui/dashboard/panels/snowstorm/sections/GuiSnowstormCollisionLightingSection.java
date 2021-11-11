package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSnowstorm;
import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.appearance.BedrockComponentCollisionAppearance;
import mchorse.blockbuster.client.particles.components.appearance.BedrockComponentCollisionTinting;
import mchorse.blockbuster.client.particles.components.appearance.Tint;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiColorElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.math.Constant;
import mchorse.mclib.math.molang.MolangParser;
import mchorse.mclib.math.molang.expressions.MolangExpression;
import mchorse.mclib.math.molang.expressions.MolangValue;
import mchorse.mclib.utils.Color;
import net.minecraft.client.Minecraft;

public class GuiSnowstormCollisionLightingSection extends GuiSnowstormLightingSection
{
    public GuiToggleElement enabled;

    private BedrockComponentCollisionAppearance appearanceComponent;

    public GuiSnowstormCollisionLightingSection(Minecraft mc, GuiSnowstorm parent)
    {
        super(mc, parent);

        this.enabled = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.collision.enabled"), (b) -> this.parent.dirty());

        this.lighting.callback = (b) ->
        {
            this.appearanceComponent.lit = !b.isToggled();
            this.parent.dirty();
        };

        this.fields.addBefore(this.lighting, this.enabled);
    }

    private BedrockComponentCollisionTinting getComponent()
    {
        return (BedrockComponentCollisionTinting) this.component;
    }

    @Override
    public String getTitle()
    {
        return "blockbuster.gui.snowstorm.collision.lighting.title";
    }

    @Override
    public void beforeSave(BedrockScheme scheme)
    {
        this.getComponent().enabled = this.enabled.isToggled() ? MolangParser.ONE : MolangParser.ZERO;
    }

    @Override
    public void setScheme(BedrockScheme scheme)
    {
        this.scheme = scheme; //cant call super as it would set the wrong component

        this.component = scheme.getOrCreate(BedrockComponentCollisionTinting.class);
        this.appearanceComponent = scheme.getOrCreate(BedrockComponentCollisionAppearance.class);
        this.lighting.toggled(!this.appearanceComponent.lit);
        this.enabled.toggled(MolangExpression.isOne(this.getComponent().enabled));

        this.setTintsCache();
        this.fillData();
    }
}
