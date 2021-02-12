package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSnowstorm;
import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.meta.BedrockComponentInitialization;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiSnowstormInitializationSection extends GuiSnowstormComponentSection<BedrockComponentInitialization>
{
    public GuiTextElement create;
    public GuiTextElement update;
    public GuiTextElement updateParticle;

    public GuiSnowstormInitializationSection(Minecraft mc, GuiSnowstorm parent)
    {
        super(mc, parent);

        this.create = new GuiTextElement(mc, 10000, (str) -> this.component.creation = this.parse(str, this.create, this.component.creation));
        this.create.tooltip(IKey.lang("blockbuster.gui.snowstorm.initialization.create"));

        this.update = new GuiTextElement(mc, 10000, (str) -> this.component.update = this.parse(str, this.update, this.component.update));
        this.update.tooltip(IKey.lang("blockbuster.gui.snowstorm.initialization.update"));

        this.updateParticle = new GuiTextElement(mc, 10000, (str) -> this.component.particleUpdate = this.parse(str, this.updateParticle, this.component.particleUpdate));
        this.updateParticle.tooltip(IKey.lang("blockbuster.gui.snowstorm.initialization.particle_update_expression"));

        this.fields.add(Elements.label(IKey.lang("blockbuster.gui.snowstorm.initialization.emitter_expression_title"), 20).anchor(0, 1F), this.create, this.update);
        this.fields.add(Elements.label(IKey.lang("blockbuster.gui.snowstorm.initialization.particle_expression_title"), 20).anchor(0, 1F), this.updateParticle);
    }

    @Override
    public String getTitle()
    {
        return "blockbuster.gui.snowstorm.initialization.title";
    }

    @Override
    protected BedrockComponentInitialization getComponent(BedrockScheme scheme)
    {
        return this.scheme.getOrCreate(BedrockComponentInitialization.class);
    }

    @Override
    protected void fillData()
    {
        this.set(this.create, this.component.creation);
        this.set(this.update, this.component.update);
        this.set(this.updateParticle, this.component.particleUpdate);
    }
}