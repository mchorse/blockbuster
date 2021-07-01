package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSnowstorm;
import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.meta.BedrockComponentLocalSpace;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.math.Operation;
import net.minecraft.client.Minecraft;

public class GuiSnowstormSpaceSection extends GuiSnowstormComponentSection<BedrockComponentLocalSpace>
{
    public GuiToggleElement position;
    public GuiToggleElement rotation;
    public GuiToggleElement scale;
    public GuiToggleElement scaleBillboard;
    public GuiElement scaleColumns;
    public GuiToggleElement direction; //local direction for physical accurate systems
    public GuiToggleElement acceleration;
    public GuiToggleElement gravity;
    public GuiTrackpadElement linearVelocity;
    public GuiTrackpadElement angularVelocity;

    public GuiElement objectVelocity;

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

            updateButtons();
        });
        this.scale.tooltip(IKey.lang("blockbuster.gui.snowstorm.space.scale_tooltip"));

        this.scaleBillboard = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.space.scale_billboard"), (b) ->
        {
            this.component.scaleBillboard = b.isToggled();
            this.parent.dirty();
        });
        this.scaleBillboard.tooltip(IKey.lang("blockbuster.gui.snowstorm.space.scale_billboard_tooltip"));

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

        this.objectVelocity = new GuiElement(mc);
        this.scaleColumns = new GuiElement(mc);

        this.scaleColumns.flex().column(4).stretch().vertical().height(2);
        this.objectVelocity.flex().column(4).stretch().vertical().height(4);

        this.linearVelocity = new GuiTrackpadElement(mc, (value) ->
        {
            this.component.linearVelocity = value.floatValue();
            this.parent.dirty();
        });
        this.linearVelocity.tooltip(IKey.lang("blockbuster.gui.snowstorm.space.linear_velocity_tooltip"));
        this.objectVelocity.add(Elements.label(IKey.lang("blockbuster.gui.snowstorm.space.object_velocity_title")).marginTop(12), Elements.label(IKey.lang("blockbuster.gui.snowstorm.space.linear_velocity")).marginTop(12), this.linearVelocity);

        this.angularVelocity = new GuiTrackpadElement(mc, (value) ->
        {
            this.component.angularVelocity = value.floatValue();
            this.parent.dirty();
        });
        this.angularVelocity.tooltip(IKey.lang("blockbuster.gui.snowstorm.space.angular_velocity_tooltip"));
        this.objectVelocity.add(Elements.label(IKey.lang("blockbuster.gui.snowstorm.space.angular_velocity")).marginTop(12), this.angularVelocity);

        this.scaleColumns.add(this.scale);


        this.fields.add(this.position, this.rotation, this.scaleColumns, this.direction, this.acceleration, this.gravity, this.objectVelocity);
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
        this.scaleBillboard.toggled(this.component.scaleBillboard);
        this.direction.toggled(this.component.direction);
        this.acceleration.toggled(this.component.acceleration);
        this.gravity.toggled(this.component.gravity);
        this.linearVelocity.setValue(this.component.linearVelocity);
        this.angularVelocity.setValue(this.component.angularVelocity);

        updateButtons();
    }

    private void updateButtons()
    {
        /*this.scaleBillboard.removeFromParent();

        if (this.scale.isToggled())
        {
            this.scaleColumns.add(this.scaleBillboard);
        }

        this.resizeParent();*/
    }
}