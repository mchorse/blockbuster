package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster.utils.mclib.BBIcons;
import mchorse.blockbuster_pack.morphs.ParticleMorph;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiNestedEdit;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@SideOnly(Side.CLIENT)
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

        this.renderer.setVisible(false);
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
        public GuiToggleElement localRotation;
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

            this.mode.tooltip(IKey.lang("blockbuster.gui.particle.type"));
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
            this.localRotation = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.particle.local_rotation"), (b) -> this.morph.localRotation = b.isToggled());
            this.localRotation.tooltip(IKey.lang("blockbuster.gui.particle.local_rotation_tooltip"));
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

            this.type.flex().relative(this.pickParticle).set(0, 20, 0, 80).w(1F);

            GuiElement element = new GuiElement(mc);

            element.flex().relative(this).wh(1F, 1F).column(5).width(110).padding(10).height(20);
            element.add(this.mode, this.pickParticle);
            element.add(Elements.label(IKey.lang("blockbuster.gui.particle.emission"), 16).anchor(0, 1F), this.frequency, this.duration, this.delay, this.cap, this.speed, this.count);
            element.add(Elements.label(IKey.lang("blockbuster.gui.particle.vanilla"), 16).anchor(0, 1F), this.x, this.y, this.z);
            element.add(Elements.label(IKey.lang("blockbuster.gui.particle.common"), 16).anchor(0, 1F), this.dx, this.dy, this.dz);
            element.add(Elements.label(IKey.lang("blockbuster.gui.particle.args"), 16).anchor(0, 1F), this.args, this.localRotation);

            this.add(element, this.type);
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
            this.localRotation.toggled(morph.localRotation);
            this.count.setValue(morph.count);

            StringJoiner joiner = new StringJoiner(", ");

            for (int value : morph.arguments)
            {
                joiner.add(String.valueOf(value));
            }

            this.args.setText(joiner.toString());
        }
    }

    public static class GuiParticleMorphMorphPanel extends GuiMorphPanel<ParticleMorph, GuiParticleMorph>
    {
        public GuiNestedEdit pickMorph;
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

            this.pickMorph = new GuiNestedEdit(mc, (editing) ->
            {
                ParticleMorph particle = this.morph;

                this.editor.morphs.nestEdit(particle.morph, editing, (morph) ->
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

            this.type.flex().relative(this.pickType).set(0, 20, 0, 80).w(1F);
            this.yaw.flex().h(14);
            this.pitch.flex().h(14);
            this.sequencer.flex().h(14);
            this.random.flex().h(14);

            GuiElement element = new GuiElement(mc);

            element.flex().relative(this).wh(1F, 1F).column(5).width(110).padding(10).height(20);
            element.add(Elements.label(IKey.lang("blockbuster.gui.particle.morph")), this.pickMorph, this.pickType, this.fade, this.lifeSpan, this.maximum, this.yaw, this.pitch, this.sequencer, this.random);

            this.add(element, this.type);
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
    }
}