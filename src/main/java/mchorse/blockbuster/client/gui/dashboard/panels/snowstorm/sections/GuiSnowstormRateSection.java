package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSnowstorm;
import mchorse.blockbuster.client.particles.components.rate.BedrockComponentRate;
import mchorse.blockbuster.client.particles.components.rate.BedrockComponentRateInstant;
import mchorse.blockbuster.client.particles.components.rate.BedrockComponentRateSteady;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiSnowstormRateSection extends GuiSnowstormModeSection<BedrockComponentRate>
{
    public GuiTextElement rate;
    public GuiTextElement particles;

    public GuiSnowstormRateSection(Minecraft mc, GuiSnowstorm parent)
    {
        super(mc, parent);

        this.rate = new GuiTextElement(mc, 10000, (str) ->
        {
            BedrockComponentRateSteady comp = (BedrockComponentRateSteady) this.component;

            comp.spawnRate = this.parse(str, this.rate, comp.spawnRate);
        });
        this.rate.tooltip(IKey.lang("blockbuster.gui.snowstorm.rate.spawn_rate"));
        this.particles = new GuiTextElement(mc, 10000, (str) -> this.component.particles = this.parse(str, this.particles, this.component.particles));
        this.particles.tooltip(IKey.lang(""));

        this.fields.add(this.particles);
    }

    @Override
    public String getTitle()
    {
        return "blockbuster.gui.snowstorm.rate.title";
    }

    @Override
    protected void fillModes(GuiCirculateElement button)
    {
        button.addLabel(IKey.lang("blockbuster.gui.snowstorm.rate.instant"));
        button.addLabel(IKey.lang("blockbuster.gui.snowstorm.rate.steady"));
    }

    @Override
    protected void restoreInfo(BedrockComponentRate component, BedrockComponentRate old)
    {
        component.particles = old.particles;
    }

    @Override
    protected Class<BedrockComponentRate> getBaseClass()
    {
        return BedrockComponentRate.class;
    }

    @Override
    protected Class getDefaultClass()
    {
        return BedrockComponentRateInstant.class;
    }

    @Override
    protected Class getModeClass(int value)
    {
        return value == 0 ? BedrockComponentRateInstant.class : BedrockComponentRateSteady.class;
    }

    @Override
    protected void fillData()
    {
        super.fillData();

        this.updateVisibility();
        this.set(this.particles, this.component.particles);
        this.particles.tooltip.label.set(this.isInstant() ? "blockbuster.gui.snowstorm.rate.particles" : "blockbuster.gui.snowstorm.rate.max_particles");

        if (this.component instanceof BedrockComponentRateSteady)
        {
            this.set(this.rate, ((BedrockComponentRateSteady) this.component).spawnRate );
        }
    }

    private void updateVisibility()
    {
        if (this.isInstant())
        {
            this.rate.removeFromParent();
        }
        else if (!this.rate.hasParent())
        {
            this.fields.add(this.rate);
        }

        this.resizeParent();
    }

    private boolean isInstant()
    {
        return this.component instanceof BedrockComponentRateInstant;
    }
}