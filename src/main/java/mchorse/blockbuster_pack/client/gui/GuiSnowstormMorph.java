package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.utils.mclib.BBIcons;
import mchorse.blockbuster_pack.morphs.SnowstormMorph;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Label;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.math.Variable;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GuiSnowstormMorph extends GuiAbstractMorph<SnowstormMorph>
{
    public GuiSnowstormMorph(Minecraft mc)
    {
        super(mc);

        this.defaultPanel = new GuiSnowstormVariablesMorphPanel(mc, this);
        this.registerPanel(this.defaultPanel, IKey.lang("blockbuster.gui.snowstorm.variables"), BBIcons.PARTICLE);
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof SnowstormMorph;
    }

    @Override
    public List<Label<NBTTagCompound>> getPresets(SnowstormMorph morph)
    {
        List<Label<NBTTagCompound>> labels = new ArrayList<Label<NBTTagCompound>>();

        for (String preset : Blockbuster.proxy.particles.presets.keySet())
        {
            NBTTagCompound tag = new NBTTagCompound();

            tag.setString("Scheme", preset);
            this.addPreset(morph, labels, preset, tag);
        }

        return labels;
    }

    public static class GuiSnowstormVariablesMorphPanel extends GuiMorphPanel<SnowstormMorph, GuiSnowstormMorph>
    {
        public GuiStringListElement variables;
        public GuiTextElement expression;

        private String variable;

        public GuiSnowstormVariablesMorphPanel(Minecraft mc, GuiSnowstormMorph editor)
        {
            super(mc, editor);

            this.variables = new GuiStringListElement(mc, (list) -> this.pickVariable(list.get(0)));
            this.variables.background();
            this.expression = new GuiTextElement(mc, 1000, this::replaceVariable);

            this.variables.flex().relative(this).xy(10, 22).w(110).hTo(this.expression.area, -17);
            this.expression.flex().relative(this).x(10).y(1F, -30).w(1F, -20).h(20);

            this.add(this.expression, this.variables);
        }

        @Override
        public void fillData(SnowstormMorph morph)
        {
            super.fillData(morph);

            Set<String> keys = new HashSet<String>();
            BedrockScheme scheme = this.morph.getEmitter().scheme;

            for (String key : this.morph.variables.keySet())
            {
                if (scheme != null && !scheme.parser.variables.containsKey(key))
                {
                    keys.add(key);
                }
            }

            for (String key : keys)
            {
                this.morph.variables.remove(key);
            }

            this.variables.clear();

            if (scheme != null)
            {
                this.variables.add(scheme.parser.variables.keySet());
                this.variables.sort();
            }

            String first = this.variables.getList().isEmpty() ? "" : this.variables.getList().get(0);

            this.pickVariable(first);
            this.expression.setEnabled(!first.isEmpty());
            this.variables.setCurrent(first);
        }

        private void pickVariable(String variable)
        {
            this.variable = variable;

            String expression = this.morph.variables.get(variable);

            this.expression.setEnabled(true);
            this.expression.setText(expression == null ? "" : expression);
        }

        private void replaceVariable(String expression)
        {
            if (this.variable.isEmpty())
            {
                return;
            }

            this.morph.replaceVariable(this.variable, expression);
        }

        @Override
        public void draw(GuiContext context)
        {
            super.draw(context);

            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.snowstorm.variables"), this.variables.area.x, this.variables.area.y - 12, 0xffffff);
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.snowstorm.expression"), this.expression.area.x, this.expression.area.y - 12, 0xffffff);
        }
    }
}