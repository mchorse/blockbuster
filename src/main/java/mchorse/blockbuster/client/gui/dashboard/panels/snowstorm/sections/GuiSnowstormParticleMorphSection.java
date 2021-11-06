package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSnowstorm;
import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.components.appearance.BedrockComponentParticleMorph;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiNestedEdit;
import net.minecraft.client.Minecraft;

public class GuiSnowstormParticleMorphSection extends GuiSnowstormComponentSection<BedrockComponentParticleMorph>
{
    public GuiToggleElement enabled;
    public GuiToggleElement renderTexture;
    public GuiNestedEdit pickMorph;

    public GuiSnowstormParticleMorphSection(Minecraft mc, GuiSnowstorm parent)
    {
        super(mc, parent);

        this.enabled = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.particle_morph.enabled"), (b) ->
        {
            this.component.enabled = b.isToggled();

            this.parent.dirty();
        });

        this.renderTexture = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.snowstorm.particle_morph.render_texture"), (b) ->
        {
            this.component.renderTexture = b.isToggled();

            this.parent.dirty();
        });

        this.pickMorph = new GuiNestedEdit(mc, (editing) ->
        {
            ClientProxy.panels.addMorphs(this.parent, editing, this.component.morph.get());
            this.parent.dirty();
        });
        this.pickMorph.marginTop(80);

        this.fields.add(this.enabled, this.pickMorph, this.renderTexture);
    }

    public void setMorph(AbstractMorph morph)
    {
        this.component.morph.set(morph);

        this.pickMorph.setMorph(morph);
    }

    @Override
    public String getTitle()
    {
        return "blockbuster.gui.snowstorm.particle_morph.title";
    }

    @Override
    public void draw(GuiContext context)
    {
        if (!this.component.morph.isEmpty())
        {
            AbstractMorph morph = this.component.morph.get();
            int x = this.area.mx();
            int y = this.enabled.area.y(1F) + 50;

            GuiDraw.scissor(this.area.x, this.area.y, this.area.w, this.area.h, context);
            morph.renderOnScreen(this.mc.player, x, y, this.area.h / 3.5F, 1.0F);
            GuiDraw.unscissor(context);
        }

        super.draw(context);
    }

    @Override
    protected BedrockComponentParticleMorph getComponent(BedrockScheme scheme)
    {
        return this.scheme.getOrCreate(BedrockComponentParticleMorph.class);
    }

    @Override
    protected void fillData()
    {
        this.enabled.toggled(this.component.enabled);
        this.renderTexture.toggled(this.component.renderTexture);
        this.pickMorph.setMorph(this.component.morph.get());
    }
}
