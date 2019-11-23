package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster_pack.morphs.ParticleMorph;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.widgets.buttons.GuiCirculate;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphsMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.client.config.GuiCheckBox;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class GuiParticleMorph extends GuiAbstractMorph<ParticleMorph>
{
    public GuiParticleMorphGeneralPanel general;
    public GuiParticleMorphMorphPanel panel;

    public GuiParticleMorph(Minecraft mc)
    {
        super(mc);

        this.defaultPanel = this.general = new GuiParticleMorphGeneralPanel(mc, this);
        this.panel = new GuiParticleMorphMorphPanel(mc, this);

        this.registerPanel(this.general, GuiDashboard.GUI_ICONS, I18n.format("blockbuster.gui.particle.tooltip"), 48, 96, 48, 112);
        this.registerPanel(this.panel, GuiDashboard.GUI_ICONS, I18n.format("blockbuster.gui.particle.morph"), 80, 32, 80, 48);
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof ParticleMorph;
    }

    @Override
    protected void drawMorph(int mouseX, int mouseY, float partialTicks)
    {
        try {
            if (this.view.delegate == this.panel && this.morph.morph != null)
            {
                this.morph.morph.renderOnScreen(this.mc.thePlayer, this.area.getX(0.5F), this.area.getY(0.66F), (float) (this.area.h / 3), 1.0F);
            }
        } catch (Exception e) {}
    }

    public static class GuiParticleMorphGeneralPanel extends GuiMorphPanel<ParticleMorph, GuiParticleMorph>
    {
        public GuiButtonElement<GuiCirculate> mode;
        public GuiTrackpadElement frequency;
        public GuiTrackpadElement duration;
        public GuiTrackpadElement delay;

        public GuiButtonElement<GuiButton> pickParticle;
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
            this.duration = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.sequencer.duration"), (value) -> this.morph.duration = value.intValue());
            this.duration.setLimit(-1, Integer.MAX_VALUE, true);
            this.delay = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.gun.delay"), (value) -> this.morph.delay = value.intValue());
            this.delay.setLimit(0, Integer.MAX_VALUE, true);

            this.pickParticle = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.particle.particle"), (b) -> this.type.toggleVisible());
            this.type = new GuiStringListElement(mc, (value) ->
            {
                this.morph.vanillaType = EnumParticleTypes.getByName(value);
            });
            this.type.setBackground();

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

            this.mode.resizer().parent(this.area).set(0, 35, 100, 20).x(1, -110);
            this.frequency.resizer().relative(this.pickParticle.resizer()).set(0, 25, 100, 20);
            this.duration.resizer().relative(this.frequency.resizer()).set(0, 25, 100, 20);
            this.delay.resizer().relative(this.duration.resizer()).set(0, 25, 100, 20);

            this.pickParticle.resizer().relative(this.mode.resizer()).set(0, 25, 100, 20);
            this.type.resizer().relative(this.pickParticle.resizer()).set(0, 20, 100, 80);
            this.x.resizer().parent(this.area).set(10, 30, 100, 20);
            this.y.resizer().relative(this.x.resizer()).set(0, 19, 100, 20);
            this.z.resizer().relative(this.y.resizer()).set(0, 19, 100, 20);
            this.dx.resizer().relative(this.x.resizer()).set(110, 0, 100, 20);
            this.dy.resizer().relative(this.dx.resizer()).set(0, 19, 100, 20);
            this.dz.resizer().relative(this.dy.resizer()).set(0, 19, 100, 20);
            this.speed.resizer().relative(this.z.resizer()).set(0, 25, 100, 20);
            this.count.resizer().relative(this.dz.resizer()).set(0, 25, 100, 20);
            this.args.resizer().relative(this.speed.resizer()).set(0, 40, 100, 20);

            this.children.add(this.mode, this.frequency, this.duration, this.delay, this.pickParticle, this.x, this.y, this.z, this.dx, this.dy, this.dz, this.speed, this.count, this.args, this.type);
        }

        @Override
        public void fillData(ParticleMorph morph)
        {
            super.fillData(morph);

            this.mode.button.setValue(morph.mode == ParticleMorph.ParticleMode.MORPH ? 1 : 0);
            this.frequency.setValue(morph.frequency);
            this.duration.setValue(morph.duration);
            this.delay.setValue(morph.delay);

            this.type.setVisible(false);
            this.type.setCurrentScroll(morph.vanillaType == null ? "" : morph.vanillaType.getParticleName());
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
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.particle.vanilla"), this.x.area.x, this.x.area.y - 16, 0xffffff);
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.particle.common"), this.dx.area.x, this.dx.area.y - 16, 0xffffff);
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.particle.args"), this.args.area.x, this.args.area.y - 11, 0xffffff);

            super.draw(tooltip, mouseX, mouseY, partialTicks);
        }
    }

    public static class GuiParticleMorphMorphPanel extends GuiMorphPanel<ParticleMorph, GuiParticleMorph>
    {
        public GuiButtonElement<GuiButton> pickMorph;
        public GuiCreativeMorphs morphPicker;
        public GuiButtonElement<GuiButton> pickType;
        public GuiStringListElement type;
        public GuiButtonElement<GuiCheckBox> yaw;
        public GuiButtonElement<GuiCheckBox> pitch;
        public GuiButtonElement<GuiCheckBox> sequencer;
        public GuiButtonElement<GuiCheckBox> random;
        public GuiTrackpadElement fade;
        public GuiTrackpadElement lifeSpan;
        public GuiTrackpadElement maximum;

        public GuiParticleMorphMorphPanel(Minecraft mc, GuiParticleMorph editor)
        {
            super(mc, editor);

            this.pickMorph = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.pick"), (b) ->
            {
                if (this.morphPicker == null) {
                    IMorphing morphing = Morphing.get(this.mc.thePlayer);
                    this.morphPicker = new GuiCreativeMorphsMenu(mc, 6, (AbstractMorph)null, morphing);
                    this.morphPicker.resizer().parent(this.area).set(0.0F, 0.0F, 0.0F, 0.0F).w(1.0F, 0).h(1.0F, 0);
                    this.morphPicker.callback = (morph) -> {
                        this.morph.morph = morph;
                    };
                    GuiScreen screen = Minecraft.getMinecraft().currentScreen;
                    this.morphPicker.resize(screen.width, screen.height);
                    this.children.add(this.morphPicker);
                }

                this.children.unfocus();
                this.morphPicker.setSelected(this.morph.morph);
                this.morphPicker.setVisible(true);
            });

            this.pickType = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.particle.pick_type"), (b) ->
            {
                this.type.toggleVisible();
            });
            this.pickType.tooltip(I18n.format("blockbuster.gui.particle.pick_type_tooltip"), GuiTooltip.TooltipDirection.RIGHT);

            this.type = new GuiStringListElement(mc, (value) ->
            {
                this.morph.movementType = ParticleMorph.MorphParticle.MovementType.getType(value);
            });

            for (ParticleMorph.MorphParticle.MovementType type : ParticleMorph.MorphParticle.MovementType.values())
            {
                this.type.add(type.id);
            }

            this.type.sort();
            this.type.setBackground();

            this.yaw = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.gun.yaw"), false, (b) -> this.morph.yaw = b.button.isChecked());
            this.pitch = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.gun.pitch"), false, (b) -> this.morph.pitch = b.button.isChecked());
            this.sequencer = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.gun.sequencer"), false, (b) -> this.morph.sequencer = b.button.isChecked());
            this.random = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.gun.random"), false, (b) -> this.morph.random = b.button.isChecked());
            this.fade = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.particle.fade"), (value) -> this.morph.fade = value.intValue());
            this.fade.setLimit(0, Integer.MAX_VALUE, true);
            this.lifeSpan = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.gun.life_span"), (value) -> this.morph.lifeSpan = value.intValue());
            this.lifeSpan.setLimit(0, Integer.MAX_VALUE, true);
            this.maximum = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.particle.maximum"), (value) -> this.morph.maximum = value.intValue());
            this.maximum.setLimit(1, Integer.MAX_VALUE, true);

            this.pickMorph.resizer().parent(this.area).set(10, 30, 100, 20);
            this.pickType.resizer().relative(this.pickMorph.resizer()).set(0, 25, 100, 20);
            this.type.resizer().relative(this.pickType.resizer()).set(0, 20, 100, 80);

            this.fade.resizer().relative(this.pickType.resizer()).set(0, 25, 100, 20);
            this.lifeSpan.resizer().relative(this.fade.resizer()).set(0, 25, 100, 20);
            this.maximum.resizer().relative(this.lifeSpan.resizer()).set(0, 25, 100, 20);
            this.yaw.resizer().relative(this.pickMorph.resizer()).set(0, 0, 100, 11).x(1, 10);
            this.pitch.resizer().relative(this.yaw.resizer()).set(0, 16, 100, 11);
            this.sequencer.resizer().relative(this.pitch.resizer()).set(0, 16, 100, 11);
            this.random.resizer().relative(this.sequencer.resizer()).set(0, 16, 100, 11);

            this.children.add(this.pickMorph, this.morphPicker, this.pickType, this.yaw, this.pitch, this.sequencer, this.random, this.fade, this.lifeSpan, this.maximum, this.type);
        }

        @Override
        public void resize(int width, int height)
        {
            super.resize(width, height);
        }

        @Override
        public void fillData(ParticleMorph morph)
        {
            super.fillData(morph);

            this.type.setVisible(false);
            this.type.setCurrentScroll(morph.movementType.id);
            this.yaw.button.setIsChecked(morph.yaw);
            this.pitch.button.setIsChecked(morph.pitch);
            this.sequencer.button.setIsChecked(morph.sequencer);
            this.random.button.setIsChecked(morph.random);
            this.fade.setValue(morph.fade);
            this.lifeSpan.setValue(morph.lifeSpan);
            this.maximum.setValue(morph.maximum);
        }

        @Override
        public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
        {
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.particle.morph"), this.pickMorph.area.x, this.pickMorph.area.y - 16, 0xffffff);

            super.draw(tooltip, mouseX, mouseY, partialTicks);
        }
    }
}