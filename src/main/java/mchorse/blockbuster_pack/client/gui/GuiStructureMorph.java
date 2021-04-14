package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiPoseTransformations;
import mchorse.blockbuster_pack.morphs.StructureMorph;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAnimation;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;

public class GuiStructureMorph extends GuiAbstractMorph<StructureMorph>
{
    public GuiStructureMorphPanel general;

    public GuiStructureMorph(Minecraft mc)
    {
        super(mc);

        this.defaultPanel = this.general = new GuiStructureMorphPanel(mc, this);
        this.registerPanel(this.general, IKey.lang("blockbuster.morph.structure"), Icons.GEAR);
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof StructureMorph;
    }

    public static class GuiStructureMorphPanel extends GuiMorphPanel<StructureMorph, GuiStructureMorph>
    {
        public GuiPoseTransformations pose;
        public GuiAnimation animation;

        public GuiStructureMorphPanel(Minecraft mc, GuiStructureMorph editor)
        {
            super(mc, editor);

            this.pose = new GuiPoseTransformations(mc);
            this.pose.flex().relative(this.area).set(0, 0, 190, 70).x(0.5F, -95).y(1, -75);

            this.animation = new GuiAnimation(mc, true);
            this.animation.flex().relative(this).x(1F, -130).w(130);

            this.add(this.pose, this.animation);
        }

        @Override
        public void fillData(StructureMorph morph)
        {
            super.fillData(morph);

            this.pose.set(morph.pose);
            this.animation.fill(morph.animation);
        }
    }
}
