package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSnowstorm;
import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.meta.BedrockComponentLocalSpace;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiSnowstormSpaceSection extends GuiSnowstormComponentSection<BedrockComponentLocalSpace>
{
    public GuiToggleElement position;
    public GuiToggleElement rotation;
    public GuiToggleElement scale;
    public GuiToggleElement direction; //local direction for physical accurate systems
    public GuiToggleElement acceleration;
    public GuiToggleElement gravity;

    public GuiSnowstormSpaceSection(Minecraft mc, GuiSnowstorm parent)
    {
        super(mc, parent);

        this.position = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.space.position"), (b) ->
        {
            this.component.position = b.isToggled();
            this.parent.dirty();
        });
        this.position.tooltip(IKey.lang("blockbuster.gui.snowstorm.space.position_tooltip"));

        this.rotation = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.space.rotation"), (b) ->
        {
            this.component.rotation = b.isToggled();
            this.parent.dirty();
        });
        this.rotation.tooltip(IKey.lang("blockbuster.gui.snowstorm.space.rotation_tooltip"));

        this.scale = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.space.scale"), (b) ->
        {
            this.component.scale = b.isToggled();
            this.parent.dirty();
        });
        this.scale.tooltip(IKey.lang("blockbuster.gui.snowstorm.space.scale_tooltip"));

        this.direction = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.space.direction"), (b) ->
        {
            this.component.direction = b.isToggled();
            this.parent.dirty();
        });
        this.direction.tooltip(IKey.lang("blockbuster.gui.snowstorm.space.direction_tooltip"));

        this.acceleration = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.space.acceleration"), (b) ->
        {
            this.component.acceleration = b.isToggled();
            this.parent.dirty();
        });
        this.acceleration.tooltip(IKey.lang("blockbuster.gui.snowstorm.space.acceleration_tooltip"));

        this.gravity = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.space.gravity"), (b) ->
        {
            this.component.gravity = b.isToggled();
            this.parent.dirty();
        });

        this.fields.add(this.position, this.rotation, this.scale, this.direction, this.acceleration, this.gravity);
    }

    @Override
    public String getTitle()
    {
        return "blockbuster.gui.snowstorm.space.title";
    }

    @Override
    protected BedrockComponentLocalSpace getComponent(BedrockScheme scheme)
    {
        return scheme.getOrCreate(BedrockComponentLocalSpace.class);
    }

    @Override
    protected void fillData()
    {
        this.position.toggled(this.component.position);
        this.rotation.toggled(this.component.rotation);
        this.scale.toggled(this.component.scale);
        this.direction.toggled(this.component.direction);
        this.acceleration.toggled(this.component.acceleration);
        this.gravity.toggled(this.component.gravity);
    }
}
