package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSnowstorm;
import mchorse.blockbuster.client.particles.components.lifetime.BedrockComponentLifetime;
import mchorse.blockbuster.client.particles.components.lifetime.BedrockComponentLifetimeExpression;
import mchorse.blockbuster.client.particles.components.lifetime.BedrockComponentLifetimeLooping;
import mchorse.blockbuster.client.particles.components.lifetime.BedrockComponentLifetimeOnce;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.tooltips.LabelTooltip;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiSnowstormLifetimeSection extends GuiSnowstormModeSection<BedrockComponentLifetime>
{
    public GuiTextElement active;
    public GuiTextElement expiration;

    public GuiSnowstormLifetimeSection(Minecraft mc, GuiSnowstorm parent)
    {
        super(mc, parent);

        this.active = new GuiTextElement(mc, 10000, (str) -> this.component.activeTime = this.parse(str, this.active, this.component.activeTime));
        this.active.tooltip(IKey.lang(""));
        this.expiration = new GuiTextElement(mc, 10000, (str) ->
        {
            if (this.component instanceof BedrockComponentLifetimeLooping)
            {
                BedrockComponentLifetimeLooping component = (BedrockComponentLifetimeLooping) this.component;

                component.sleepTime = this.parse(str, this.expiration, component.sleepTime);
            }
            else
            {
                BedrockComponentLifetimeExpression component = (BedrockComponentLifetimeExpression) this.component;

                component.expiration = this.parse(str, this.expiration, component.expiration);
            }

            this.parent.dirty();
        });
        this.expiration.tooltip(IKey.lang(""));

        this.fields.add(this.active);
    }

    @Override
    public String getTitle()
    {
        return "blockbuster.gui.snowstorm.lifetime.title";
    }

    @Override
    protected void fillModes(GuiCirculateElement button)
    {
        button.addLabel(IKey.lang("blockbuster.gui.snowstorm.lifetime.expression"));
        button.addLabel(IKey.lang("blockbuster.gui.snowstorm.lifetime.looping"));
        button.addLabel(IKey.lang("blockbuster.gui.snowstorm.lifetime.once"));
    }

    @Override
    protected void restoreInfo(BedrockComponentLifetime component, BedrockComponentLifetime old)
    {
        component.activeTime = old.activeTime;
    }

    @Override
    protected Class<BedrockComponentLifetime> getBaseClass()
    {
        return BedrockComponentLifetime.class;
    }

    @Override
    protected Class getDefaultClass()
    {
        return BedrockComponentLifetimeLooping.class;
    }

    @Override
    protected Class getModeClass(int value)
    {
        if (value == 0)
        {
            return BedrockComponentLifetimeExpression.class;
        }
        else if (value == 1)
        {
            return BedrockComponentLifetimeLooping.class;
        }

        return BedrockComponentLifetimeOnce.class;
    }

    @Override
    protected void fillData()
    {
        super.fillData();

        boolean once = this.component instanceof BedrockComponentLifetimeOnce;

        this.expiration.setVisible(!once);

        if (this.component instanceof BedrockComponentLifetimeExpression)
        {
            this.set(this.expiration, ((BedrockComponentLifetimeExpression) this.component).expiration);
            ((LabelTooltip) this.expiration.tooltip).label.set("blockbuster.gui.snowstorm.lifetime.expiration_expression");

            ((LabelTooltip) this.active.tooltip).label.set("blockbuster.gui.snowstorm.lifetime.active_expression");
        }
        else if (this.component instanceof BedrockComponentLifetimeLooping)
        {
            this.set(this.expiration, ((BedrockComponentLifetimeLooping) this.component).sleepTime);
            ((LabelTooltip) this.expiration.tooltip).label.set("blockbuster.gui.snowstorm.lifetime.sleep_time");

            ((LabelTooltip) this.active.tooltip).label.set("blockbuster.gui.snowstorm.lifetime.active_looping");
        }
        else
        {
            ((LabelTooltip) this.active.tooltip).label.set("blockbuster.gui.snowstorm.lifetime.active_once");
        }

        this.set(this.active, this.component.activeTime);
        this.expiration.removeFromParent();

        if (!once)
        {
            this.fields.add(this.expiration);
        }

        this.resizeParent();
    }
}