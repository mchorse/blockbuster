package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTransformations;
import mchorse.mclib.client.gui.framework.elements.list.GuiInterpolationList;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Interpolation;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPosePanel extends GuiMorphPanel<CustomMorph, GuiCustomMorph> implements ILimbSelector
{
    /* Custom pose editor */
    public GuiButtonElement reset;
    public GuiButtonElement create;
    public GuiStringListElement list;
    public GuiToggleElement poseOnSneak;
    public GuiPoseTransformations transforms;

    /* Animated poses */
    public GuiToggleElement animates;
    public GuiToggleElement ignored;
    public GuiTrackpadElement animationDuration;
    public GuiButtonElement pickInterpolation;
    public GuiListElement<Interpolation> interpolations;

    /* General options */
    public GuiStringListElement models;
    public GuiButtonElement model;
    public GuiTrackpadElement scale;
    public GuiTrackpadElement scaleGui;

    public GuiPosePanel(Minecraft mc, GuiCustomMorph editor)
    {
        super(mc, editor);

        /* Custom pose editor */
        this.reset = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.morphs.reset"), (b) ->
        {
            if (this.morph.customPose == null)
            {
                this.morph.currentPose = "";
                this.list.setIndex(-1);
            }
            else
            {
                this.editor.morph.customPose = null;
                this.editor.bbRenderer.limb = null;
                this.updateList();
                this.updateElements();
            }

            this.editor.updateModelRenderer();
        });
        this.reset.flex().relative(this).xy(10, 10).wh(110, 20);

        this.create = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.morphs.create"), (b) ->
        {
            if (this.morph.customPose == null)
            {
                this.morph.customPose = this.morph.getPose(this.mc.player, 0).clone();
            }

            this.updateList();
            this.updateElements();
            this.editor.updateModelRenderer();
        });
        this.create.flex().relative(this.reset).y(25).wh(110, 20);

        this.poseOnSneak = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.builder.pose_sneak"), false, (b) ->
        {
            this.morph.currentPoseOnSneak = this.poseOnSneak.isToggled();
            this.editor.updateModelRenderer();
        });
        this.poseOnSneak.flex().relative(this).x(10).y(1F, -24).wh(110, 14);

        this.list = new GuiStringListElement(mc, (str) ->
        {
            if (this.morph.customPose == null)
            {
                this.morph.currentPose = str.get(0);
                this.editor.updateModelRenderer();
            }
            else
            {
                this.setLimb(str.get(0));
            }
        });
        this.list.background();
        this.list.flex().xy(0, 40).w(110).hTo(this.poseOnSneak.area, -5);

        this.transforms = new GuiPoseTransformations(mc);
        this.transforms.flex().relative(this.area).set(0, 0, 190, 70).x(0.5F, -95).y(1, -75);

        /* Animated poses */
        this.animates = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.builder.animates"), false, (b) ->
        {
            this.morph.animation.animates = this.animates.isToggled();
        });
        this.animates.flex().h(14);

        this.ignored = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.builder.ignored"), false, (b) ->
        {
            this.morph.animation.ignored = this.ignored.isToggled();
        });
        this.ignored.flex().h(14);

        this.animationDuration = new GuiTrackpadElement(mc, (value) ->
        {
            this.morph.animation.duration = value.intValue();
        });
        this.animationDuration.tooltip(IKey.lang("blockbuster.gui.builder.animation_duration"));
        this.animationDuration.limit(0).integer();

        this.pickInterpolation = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.builder.pick_interpolation"), (b) ->
        {
            this.interpolations.toggleVisible();
        });

        this.interpolations = new GuiInterpolationList(mc, (interp) ->
        {
            this.morph.animation.interp = interp.get(0);
        });
        this.interpolations.flex().relative(this.pickInterpolation).y(1F).w(1F).h(96);

        GuiElement animated = new GuiElement(mc);

        animated.flex().relative(this).x(1F, -130).w(130).column(5).vertical().stretch().height(20).padding(10);
        animated.add(this.animates, this.animationDuration, this.ignored, this.pickInterpolation);

        /* General options */
        this.model = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.builder.pick_model"), (b) -> this.models.toggleVisible());
        this.scale = new GuiTrackpadElement(mc, (value) ->
        {
            this.morph.scale = value;
        });
        this.scale.tooltip(IKey.lang("blockbuster.gui.me.options.scale"));
        this.scaleGui = new GuiTrackpadElement(mc, (value) ->
        {
            this.morph.scaleGui = value;
        });
        this.scaleGui.tooltip(IKey.lang("blockbuster.gui.me.options.scale_gui"));

        this.models = new GuiStringListElement(mc, (string) ->
        {
            this.morph.changeModel(string.get(0));
            this.editor.updateModelRenderer();
            this.editor.poseEditor.fillData(this.morph);
            this.editor.bodyPart.setLimbs(this.morph.model.limbs.keySet());
        });
        this.models.background();
        this.models.flex().relative(this.model).w(1F).h(96).anchorY(1F);

        GuiElement options = new GuiElement(mc);

        options.flex().relative(this).x(1F, -130).y(1F).w(130).anchorY(1F).column(5).vertical().stretch().height(20).padding(10);
        options.add(this.model, this.scale, this.scaleGui);

        this.add(this.reset, this.create, this.poseOnSneak, this.list, animated, options, this.transforms, this.models, this.interpolations);
    }

    @Override
    public void setLimb(String limbName)
    {
        if (this.morph.customPose == null)
        {
            return;
        }

        ModelLimb limb = this.morph.model.limbs.get(limbName);

        this.editor.bbRenderer.limb = limb;
        this.list.setCurrent(limbName);
        this.transforms.set(this.morph.customPose.limbs.get(limbName));
    }

    @Override
    public void fillData(CustomMorph morph)
    {
        super.fillData(morph);

        this.updateList();
        this.updateElements();

        this.poseOnSneak.toggled(morph.currentPoseOnSneak);
        this.animates.toggled(morph.animation.animates);
        this.ignored.toggled(morph.animation.ignored);
        this.animationDuration.setValue(morph.animation.duration);
        this.interpolations.setCurrent(morph.animation.interp);
        this.interpolations.setVisible(false);

        this.scale.setValue(morph.scale);
        this.scaleGui.setValue(morph.scaleGui);

        this.models.setVisible(false);
        this.models.clear();
        this.models.add(Blockbuster.proxy.models.models.keySet());
        this.models.sort();
        this.models.setCurrentScroll(morph.getKey());
    }

    private void updateElements()
    {
        this.create.setVisible(this.morph.customPose == null);
        this.transforms.setVisible(this.morph.customPose != null);
        this.list.flex().relative(this.morph.customPose == null ? this.create : this.reset);
        this.list.resize();
    }

    private void updateList()
    {
        String current = this.list.getCurrentFirst();

        this.list.clear();

        if (this.morph.customPose == null)
        {
            current = this.morph.currentPose;
            this.list.add(this.morph.model.poses.keySet());
            this.list.sort();
        }
        else
        {
            this.list.add(this.morph.model.limbs.keySet());
            this.list.sort();
            this.list.setIndex(0);
            current = this.list.getCurrentFirst();

            if (!this.list.isDeselected())
            {
                this.setLimb(this.list.getCurrentFirst());
            }
        }

        this.list.setCurrent(current);
    }

    @Override
    public void draw(GuiContext context)
    {
        if (this.morph.customPose == null)
        {
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.pose"), this.list.area.x, this.list.area.y - 12, 0xffffff);
        }
        else
        {
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.limbs"), this.list.area.x, this.list.area.y - 12, 0xffffff);
        }

        super.draw(context);
    }

    public static class GuiPoseTransformations extends GuiTransformations
    {
        public ModelTransform trans;

        public GuiPoseTransformations(Minecraft mc)
        {
            super(mc);
        }

        public void set(ModelTransform trans)
        {
            this.trans = trans;

            if (trans != null)
            {
                this.fillT(trans.translate[0], trans.translate[1], trans.translate[2]);
                this.fillS(trans.scale[0], trans.scale[1], trans.scale[2]);
                this.fillR(trans.rotate[0], trans.rotate[1], trans.rotate[2]);
            }
        }

        @Override
        public void setT(float x, float y, float z)
        {
            this.trans.translate[0] = x;
            this.trans.translate[1] = y;
            this.trans.translate[2] = z;
        }

        @Override
        public void setS(float x, float y, float z)
        {
            this.trans.scale[0] = x;
            this.trans.scale[1] = y;
            this.trans.scale[2] = z;
        }

        @Override
        public void setR(float x, float y, float z)
        {
            this.trans.rotate[0] = x;
            this.trans.rotate[1] = y;
            this.trans.rotate[2] = z;
        }
    }
}