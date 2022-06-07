package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster_pack.morphs.LightMorph;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAnimation;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiLightMorph extends GuiAbstractMorph<LightMorph>
{
    public GuiLightMorph(Minecraft mc)
    {
        super(mc);

        this.defaultPanel = new GuiLightMorph.GuiLightMorphPanel(mc, this);
        this.registerPanel(this.defaultPanel, IKey.lang("blockbuster.gui.light_morph.name"), Icons.GEAR);
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof LightMorph;
    }

    public static class GuiLightMorphPanel extends GuiMorphPanel<LightMorph, GuiLightMorph>
    {
        private GuiTrackpadElement lightValue;
        private GuiAnimation animation;

        public GuiLightMorphPanel(Minecraft mc, GuiLightMorph editor)
        {
            super(mc, editor);

            this.animation = new GuiAnimation(mc, true);

            this.animation.flex().relative(this).x(1F, -130).w(130);

            this.lightValue = new GuiTrackpadElement(mc, (value) -> this.morph.setLightValue(value.intValue()));

            this.lightValue.integer().limit(0,15).tooltip(IKey.lang("blockbuster.gui.light_morph.light_value_tooltip"));

            GuiLabel lightLabel = Elements.label(IKey.lang("blockbuster.gui.light_morph.light_value"));

            GuiElement lightElements = Elements.column(mc, 3, 10, lightLabel, this.lightValue);
            lightElements.flex().relative(this.area).x(0.0F, 0).w(130);

            this.add(this.animation, lightElements);
        }

        @Override
        public void fillData(LightMorph morph)
        {
            super.fillData(morph);

            this.lightValue.setValue(morph.getLightValue());
            this.animation.fill(morph.getAnimation());
        }
    }
}
