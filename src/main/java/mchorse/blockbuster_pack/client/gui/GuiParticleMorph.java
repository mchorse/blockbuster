package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster_pack.morphs.ParticleMorph;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.widgets.buttons.GuiCirculate;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumParticleTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class GuiParticleMorph extends GuiAbstractMorph<ParticleMorph>
{
    public GuiParticleMorphGeneralPanel general;

    public GuiParticleMorph(Minecraft mc)
    {
        super(mc);

        this.defaultPanel = this.general = new GuiParticleMorphGeneralPanel(mc, this);
        this.registerPanel(this.general, GuiDashboard.GUI_ICONS, I18n.format("blockbuster.gui.particle.tooltip"), 48, 96, 48, 112);
        // this.registerPanel(this.general, GuiDashboard.GUI_ICONS, I18n.format("blockbuster.gui.particle.morph"), 80, 32, 80, 48);
    }

    @Override
    protected void drawMorph(int mouseX, int mouseY, float partialTicks)
    {

    }

    public static class GuiParticleMorphGeneralPanel extends GuiMorphPanel<ParticleMorph, GuiParticleMorph>
    {
        public GuiButtonElement<GuiCirculate> mode;
        public GuiTrackpadElement frequency;

        public GuiStringListElement type;
        public GuiTrackpadElement x;
        public GuiTrackpadElement y;
        public GuiTrackpadElement z;
        public GuiTrackpadElement dx;
        public GuiTrackpadElement dy;
        public GuiTrackpadElement dz;
        public GuiTrackpadElement speed;
        public GuiTrackpadElement count;
        public GuiTextElement args;

        public GuiParticleMorphGeneralPanel(Minecraft mc, GuiParticleMorph editor)
        {
            super(mc, editor);

            GuiCirculate circulate = new GuiCirculate(0, 0, 0, 0, 0);
            circulate.addLabel(I18n.format("blockbuster.gui.particle.types.vanilla"));
            circulate.addLabel(I18n.format("blockbuster.gui.particle.types.morph"));

            this.mode = new GuiButtonElement<GuiCirculate>(mc, circulate, (b) ->
            {
                this.morph.mode = ParticleMorph.ParticleMode.values()[b.button.getValue()];
            });
            this.mode.tooltip(I18n.format("blockbuster.gui.particle.type"), GuiTooltip.TooltipDirection.LEFT);
            this.frequency = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.particle.frequency"), (value) -> this.morph.frequency = value.intValue());
            this.frequency.setLimit(1, Integer.MAX_VALUE, true);

            this.type = new GuiStringListElement(mc, (value) ->
            {
                this.morph.vanillaType = EnumParticleTypes.getByName(value);
            });

            for (EnumParticleTypes type : EnumParticleTypes.values())
            {
                this.type.add(type.getParticleName());
            }

            this.type.sort();

            this.x = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (value) -> this.morph.vanillaX = value);
            this.y = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (value) -> this.morph.vanillaY = value);
            this.z = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (value) -> this.morph.vanillaZ = value);
            this.dx = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.particle.dx"), (value) -> this.morph.vanillaDX = value);
            this.dy = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.particle.dy"), (value) -> this.morph.vanillaDY = value);
            this.dz = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.particle.dz"), (value) -> this.morph.vanillaDZ = value);
            this.speed = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.particle.speed"), (value) -> this.morph.speed = value);
            this.count = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.particle.count"), (value) -> this.morph.count = value.intValue());
            this.count.setLimit(1, Integer.MAX_VALUE, true);
            this.args = new GuiTextElement(mc, (value) ->
            {
                String[] splits = value.split(",");
                List<Integer> integerList = new ArrayList<Integer>();

                for (String split : splits)
                {
                    try
                    {
                        integerList.add(Integer.parseInt(split.trim()));
                    }
                    catch (Exception e) {}
                }

                int[] array = new int[integerList.size()];
                int i = 0;

                for (Integer integer : integerList)
                {
                    array[i ++] = integer;
                }

                this.morph.arguments = array;
            });

            this.children.add(this.mode, this.frequency, this.type, this.x, this.y, this.z, this.dx, this.dy, this.dz, this.speed, this.count, this.args);
        }

        @Override
        public void resize(int width, int height)
        {
            super.resize(width, height);

            this.mode.resizer().parent(this.area).set(0, 35, 100, 20).x(1, -110);
            this.frequency.resizer().relative(this.mode.resizer()).set(0, 25, 100, 20);

            this.type.resizer().relative(this.frequency.resizer()).set(0, 25, 100, 80);
            this.x.resizer().parent(this.area).set(10, 30, 100, 20);
            this.y.resizer().relative(this.x.resizer()).set(0, 19, 100, 20);
            this.z.resizer().relative(this.y.resizer()).set(0, 19, 100, 20);
            this.dx.resizer().relative(this.z.resizer()).set(0, 25, 100, 20);
            this.dy.resizer().relative(this.dx.resizer()).set(0, 19, 100, 20);
            this.dz.resizer().relative(this.dy.resizer()).set(0, 19, 100, 20);
            this.speed.resizer().relative(this.dz.resizer()).set(0, 25, 100, 20);
            this.count.resizer().relative(this.speed.resizer()).set(0, 19, 100, 20);
            this.args.resizer().relative(this.type.resizer()).set(0, 0, 100, 20).y(1, 0);
        }

        @Override
        public void fillData(ParticleMorph morph)
        {
            super.fillData(morph);

            this.mode.button.setValue(morph.mode == ParticleMorph.ParticleMode.MORPH ? 1 : 0);
            this.frequency.setValue(morph.frequency);

            this.type.setCurrent(morph.vanillaType == null ? "" : morph.vanillaType.getParticleName());
            this.x.setValue((float) morph.vanillaX);
            this.y.setValue((float) morph.vanillaY);
            this.z.setValue((float) morph.vanillaZ);
            this.dx.setValue((float) morph.vanillaDX);
            this.dy.setValue((float) morph.vanillaDY);
            this.dz.setValue((float) morph.vanillaDZ);
            this.speed.setValue((float) morph.speed);
            this.count.setValue(morph.count);

            StringJoiner joiner = new StringJoiner(", ");

            for (int value : morph.arguments)
            {
                joiner.add(String.valueOf(value));
            }

            this.args.setText(joiner.toString());
        }

        @Override
        public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
        {
            this.type.area.draw(0x88000000);

            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.particle.vanilla"), this.x.area.x, this.x.area.y - 16, 0xffffff);

            super.draw(tooltip, mouseX, mouseY, partialTicks);
        }
    }
}