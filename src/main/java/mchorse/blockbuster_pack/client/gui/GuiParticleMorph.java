package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster.utils.BBIcons;
import mchorse.blockbuster_pack.morphs.ParticleMorph;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphs;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphsMenu;
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
    public GuiParticleMorphMorphPanel panel;

    public GuiParticleMorph(Minecraft mc)
    {
        super(mc);

        this.defaultPanel = this.general = new GuiParticleMorphGeneralPanel(mc, this);
        this.panel = new GuiParticleMorphMorphPanel(mc, this);

        this.registerPanel(this.general, IKey.lang("blockbuster.gui.particle.tooltip"), BBIcons.PARTICLE);
        this.registerPanel(this.panel, IKey.lang("blockbuster.gui.particle.morph"), Icons.POSE);
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof ParticleMorph;
    }

    public static class GuiParticleMorphGeneralPanel extends GuiMorphPanel<ParticleMorph, GuiParticleMorph>
    {
        public GuiCirculateElement mode;
        public GuiTrackpadElement frequency;
        public GuiTrackpadElement duration;
        public GuiTrackpadElement delay;
        public GuiTrackpadElement cap;

        public GuiButtonElement pickParticle;
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

            this.mode = new GuiCirculateElement(mc, (b) ->
            {
                this.morph.mode = ParticleMorph.ParticleMode.values()[this.mode.getValue()];
            });
            this.mode.addLabel(IKey.lang("blockbuster.gui.particle.types.vanilla"));
            this.mode.addLabel(IKey.lang("blockbuster.gui.particle.types.morph"));

            this.mode.tooltip(IKey.lang("blockbuster.gui.particle.type"), Direction.LEFT);
            this.frequency = new GuiTrackpadElement(mc, (value) -> this.morph.frequency = value.intValue());
            this.frequency.tooltip(IKey.lang("blockbuster.gui.particle.frequency"));
            this.frequency.limit(1, Integer.MAX_VALUE, true);
            this.duration = new GuiTrackpadElement(mc, (value) -> this.morph.duration = value.intValue());
            this.duration.tooltip(IKey.lang("blockbuster.gui.sequencer.duration"));
            this.duration.limit(-1, Integer.MAX_VALUE, true);
            this.delay = new GuiTrackpadElement(mc, (value) -> this.morph.delay = value.intValue());
            this.delay.tooltip(IKey.lang("blockbuster.gui.gun.delay"));
            this.delay.limit(0, Integer.MAX_VALUE, true);
            this.cap = new GuiTrackpadElement(mc, (value) -> this.morph.cap = value.intValue());
            this.cap.tooltip(IKey.lang("blockbuster.gui.particle.cap"));
            this.cap.limit(0, Integer.MAX_VALUE, true);

            this.pickParticle = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.particle.particle"), (b) -> this.type.toggleVisible());
            this.type = new GuiStringListElement(mc, (value) ->
            {
                this.morph.vanillaType = EnumParticleTypes.getByName(value.get(0));
            });
            this.type.background();

            for (EnumParticleTypes type : EnumParticleTypes.values())
            {
                this.type.add(type.getParticleName());
            }

            this.type.sort();

            this.x = new GuiTrackpadElement(mc, (value) -> this.morph.vanillaX = value);
            this.x.tooltip(IKey.lang("blockbuster.gui.model_block.x"));
            this.y = new GuiTrackpadElement(mc, (value) -> this.morph.vanillaY = value);
            this.y.tooltip(IKey.lang("blockbuster.gui.model_block.y"));
            this.z = new GuiTrackpadElement(mc, (value) -> this.morph.vanillaZ = value);
            this.z.tooltip(IKey.lang("blockbuster.gui.model_block.z"));
            this.dx = new GuiTrackpadElement(mc, (value) -> this.morph.vanillaDX = value);
            this.dx.tooltip(IKey.lang("blockbuster.gui.particle.dx"));
            this.dy = new GuiTrackpadElement(mc, (value) -> this.morph.vanillaDY = value);
            this.dy.tooltip(IKey.lang("blockbuster.gui.particle.dy"));
            this.dz = new GuiTrackpadElement(mc, (value) -> this.morph.vanillaDZ = value);
            this.dz.tooltip(IKey.lang("blockbuster.gui.particle.dz"));
            this.speed = new GuiTrackpadElement(mc, (value) -> this.morph.speed = value);
            this.speed.tooltip(IKey.lang("blockbuster.gui.particle.speed"));
            this.count = new GuiTrackpadElement(mc, (value) -> this.morph.count = value.intValue());
            this.count.tooltip(IKey.lang("blockbuster.gui.particle.count"));
            this.count.limit(1, Integer.MAX_VALUE, true);
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

            this.mode.flex().relative(this.area).set(0, 35, 100, 20).x(1, -110);
            this.frequency.flex().relative(this.pickParticle.resizer()).set(0, 25, 100, 20);
            this.duration.flex().relative(this.frequency.resizer()).set(0, 25, 100, 20);
            this.delay.flex().relative(this.duration.resizer()).set(0, 25, 100, 20);
            this.cap.flex().relative(this.delay.resizer()).set(0, 25, 100, 20);

            this.pickParticle.flex().relative(this.mode.resizer()).set(0, 25, 100, 20);
            this.type.flex().relative(this.pickParticle.resizer()).set(0, 20, 100, 80);
            this.x.flex().relative(this.area).set(10, 30, 100, 20);
            this.y.flex().relative(this.x.resizer()).set(0, 19, 100, 20);
            this.z.flex().relative(this.y.resizer()).set(0, 19, 100, 20);
            this.dx.flex().relative(this.x.resizer()).set(110, 0, 100, 20);
            this.dy.flex().relative(this.dx.resizer()).set(0, 19, 100, 20);
            this.dz.flex().relative(this.dy.resizer()).set(0, 19, 100, 20);
            this.speed.flex().relative(this.z.resizer()).set(0, 25, 100, 20);
            this.count.flex().relative(this.dz.resizer()).set(0, 25, 100, 20);
            this.args.flex().relative(this.speed.resizer()).set(0, 40, 100, 20);

            this.add(this.mode, this.frequency, this.duration, this.delay, this.cap, this.pickParticle, this.x, this.y, this.z, this.dx, this.dy, this.dz, this.speed, this.count, this.args, this.type);
        }

        @Override
        public void fillData(ParticleMorph morph)
        {
            super.fillData(morph);

            this.mode.setValue(morph.mode == ParticleMorph.ParticleMode.MORPH ? 1 : 0);
            this.frequency.setValue(morph.frequency);
            this.duration.setValue(morph.duration);
            this.delay.setValue(morph.delay);
            this.cap.setValue(morph.cap);

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
        public void draw(GuiContext context)
        {
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.particle.vanilla"), this.x.area.x, this.x.area.y - 16, 0xffffff);
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.particle.common"), this.dx.area.x, this.dx.area.y - 16, 0xffffff);
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.particle.args"), this.args.area.x, this.args.area.y - 11, 0xffffff);

            super.draw(context);
        }
    }

    public static class GuiParticleMorphMorphPanel extends GuiMorphPanel<ParticleMorph, GuiParticleMorph>
    {
        public GuiButtonElement pickMorph;
        public GuiButtonElement pickType;
        public GuiStringListElement type;
        public GuiToggleElement yaw;
        public GuiToggleElement pitch;
        public GuiToggleElement sequencer;
        public GuiToggleElement random;
        public GuiTrackpadElement fade;
        public GuiTrackpadElement lifeSpan;
        public GuiTrackpadElement maximum;

        public GuiParticleMorphMorphPanel(Minecraft mc, GuiParticleMorph editor)
        {
            super(mc, editor);

            this.pickMorph = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.pick"), (b) ->
            {
                ParticleMorph particle = this.morph;

                this.editor.morphs.nestEdit(particle.morph, (morph) ->
                {
                    particle.morph = MorphUtils.copy(morph);
                });
            });

            this.pickType = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.particle.pick_type"), (b) -> this.type.toggleVisible());
            this.pickType.tooltip(IKey.lang("blockbuster.gui.particle.pick_type_tooltip"), Direction.RIGHT);

            this.type = new GuiStringListElement(mc, (value) ->
            {
                this.morph.movementType = ParticleMorph.MorphParticle.MovementType.getType(value.get(0));
            });

            for (ParticleMorph.MorphParticle.MovementType type : ParticleMorph.MorphParticle.MovementType.values())
            {
                this.type.add(type.id);
            }

            this.type.sort();
            this.type.background();

            this.yaw = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.gun.yaw"), false, (b) -> this.morph.yaw = this.yaw.isToggled());
            this.pitch = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.gun.pitch"), false, (b) -> this.morph.pitch = this.pitch.isToggled());
            this.sequencer = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.gun.sequencer"), false, (b) -> this.morph.sequencer = this.sequencer.isToggled());
            this.random = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.gun.random"), false, (b) -> this.morph.random = this.random.isToggled());
            this.fade = new GuiTrackpadElement(mc, (value) -> this.morph.fade = value.intValue());
            this.fade.tooltip(IKey.lang("blockbuster.gui.particle.fade"));
            this.fade.limit(0, Integer.MAX_VALUE, true);
            this.lifeSpan = new GuiTrackpadElement(mc, (value) -> this.morph.lifeSpan = value.intValue());
            this.lifeSpan.tooltip(IKey.lang("blockbuster.gui.gun.life_span"));
            this.lifeSpan.limit(0, Integer.MAX_VALUE, true);
            this.maximum = new GuiTrackpadElement(mc, (value) -> this.morph.maximum = value.intValue());
            this.maximum.tooltip(IKey.lang("blockbuster.gui.particle.maximum"));
            this.maximum.limit(1, Integer.MAX_VALUE, true);

            this.pickMorph.flex().relative(this.area).set(10, 30, 100, 20);
            this.pickType.flex().relative(this.pickMorph.resizer()).set(0, 25, 100, 20);
            this.type.flex().relative(this.pickType.resizer()).set(0, 20, 100, 80);

            this.fade.flex().relative(this.pickType.resizer()).set(0, 25, 100, 20);
            this.lifeSpan.flex().relative(this.fade.resizer()).set(0, 25, 100, 20);
            this.maximum.flex().relative(this.lifeSpan.resizer()).set(0, 25, 100, 20);
            this.yaw.flex().relative(this.pickMorph.resizer()).set(0, 0, 100, 11).x(1, 10);
            this.pitch.flex().relative(this.yaw.resizer()).set(0, 16, 100, 11);
            this.sequencer.flex().relative(this.pitch.resizer()).set(0, 16, 100, 11);
            this.random.flex().relative(this.sequencer.resizer()).set(0, 16, 100, 11);

            this.add(this.pickMorph, this.pickType, this.yaw, this.pitch, this.sequencer, this.random, this.fade, this.lifeSpan, this.maximum, this.type);
        }

        @Override
        public void fillData(ParticleMorph morph)
        {
            super.fillData(morph);

            this.type.setVisible(false);
            this.type.setCurrentScroll(morph.movementType.id);
            this.yaw.toggled(morph.yaw);
            this.pitch.toggled(morph.pitch);
            this.sequencer.toggled(morph.sequencer);
            this.random.toggled(morph.random);
            this.fade.setValue(morph.fade);
            this.lifeSpan.setValue(morph.lifeSpan);
            this.maximum.setValue(morph.maximum);
        }

        @Override
        public void draw(GuiContext context)
        {
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.particle.morph"), this.pickMorph.area.x, this.pickMorph.area.y - 16, 0xffffff);

            super.draw(context);
        }
    }
}