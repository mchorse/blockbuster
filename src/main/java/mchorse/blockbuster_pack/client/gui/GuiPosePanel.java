package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.blockbuster_pack.utils.GuiAnimation;
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

    public GuiAnimation animation;

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
        this.reset.flex().relative(this).xy(10, 10).w(110);

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
        this.create.flex().relative(this.reset).y(25).w(110);

        this.poseOnSneak = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.builder.pose_sneak"), false, (b) ->
        {
            this.morph.currentPoseOnSneak = this.poseOnSneak.isToggled();
            this.editor.updateModelRenderer();
        });
        this.poseOnSneak.flex().relative(this).x(10).y(1F, -22).w(110);

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

        this.animation = new GuiAnimation(mc);
        this.animation.flex().relative(this).x(1F, -130).w(130);

        /* General options */
        this.model = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.builder.pick_model"), (b) -> this.models.toggleVisible());
        this.scale = new GuiTrackpadElement(mc, (value) ->
        {
            this.morph.scale = value.floatValue();
        });
        this.scale.tooltip(IKey.lang("blockbuster.gui.me.options.scale"));
        this.scaleGui = new GuiTrackpadElement(mc, (value) ->
        {
            this.morph.scaleGui = value.floatValue();
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

        this.add(this.reset, this.create, this.poseOnSneak, this.list, this.animation, options, this.transforms, this.models, this.animation.interpolations);
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
        this.animation.fill(morph.animation);

        this.scale.setValue(morph.scale);
        this.scaleGui.setValue(morph.scaleGui);

        this.models.setVisible(false);
        this.models.clear();
        this.models.add(Blockbuster.proxy.models.models.keySet());
        this.models.sort();
        this.models.setCurrentScroll(morph.getKey());
    }

    @Override
    public void startEditing()
    {
        super.startEditing();

        this.updateList();
        this.updateElements();
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
        String current;

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
        public void setT(double x, double y, double z)
        {
            this.trans.translate[0] = (float) x;
            this.trans.translate[1] = (float) y;
            this.trans.translate[2] = (float) z;
        }

        @Override
        public void setS(double x, double y, double z)
        {
            this.trans.scale[0] = (float) x;
            this.trans.scale[1] = (float) y;
            this.trans.scale[2] = (float) z;
        }

        @Override
        public void setR(double x, double y, double z)
        {
            this.trans.rotate[0] = (float) x;
            this.trans.rotate[1] = (float) y;
            this.trans.rotate[2] = (float) z;
        }
    }
}