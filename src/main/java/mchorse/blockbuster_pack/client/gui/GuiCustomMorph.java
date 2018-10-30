package mchorse.blockbuster_pack.client.gui;

import java.util.List;
import java.util.Map;

import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelRenderer;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.DummyEntity;
import mchorse.blockbuster.client.gui.elements.GuiMorphsPopup.GuiCreativeMorphsMenu;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster_pack.client.render.layers.LayerBodyPart;
import mchorse.blockbuster_pack.client.render.part.MorphBodyPart;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.blockbuster_pack.morphs.CustomMorph.BodyPart;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.elements.GuiAbstractMorph;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCustomMorph extends GuiAbstractMorph
{
    private GuiElements poseEditor = new GuiElements();
    private GuiElements general = new GuiElements();
    private GuiElements bodyPart = new GuiElements();

    private GuiDelegateElement<IGuiElement> view;
    private GuiModelRendererBodyPart modelRenderer;

    private GuiButtonElement<GuiButton> toggleNbt;
    private GuiButtonElement<GuiButton> togglePose;
    private GuiButtonElement<GuiButton> toggleBodyPart;

    /* Pose editor */
    private GuiTrackpadElement tx;
    private GuiTrackpadElement ty;
    private GuiTrackpadElement tz;
    private GuiTrackpadElement sx;
    private GuiTrackpadElement sy;
    private GuiTrackpadElement sz;
    private GuiTrackpadElement rx;
    private GuiTrackpadElement ry;
    private GuiTrackpadElement rz;

    private GuiStringListElement limbs;
    private GuiButtonElement<GuiButton> resetPose;

    private ModelPose pose;
    private ModelTransform trans;

    /* Body part editor */
    private GuiStringListElement bodyParts;
    private GuiButtonElement<GuiButton> pickMorph;
    private GuiCreativeMorphs morphPicker;

    private GuiButtonElement<GuiButton> addPart;
    private GuiButtonElement<GuiButton> removePart;

    private BodyPart part;

    /* General options */
    public GuiTextElement skin;
    public GuiStringListElement poses;
    public GuiButtonElement<GuiCheckBox> poseOnSneak;

    public GuiCustomMorph(Minecraft mc)
    {
        super(mc);

        this.view = new GuiDelegateElement<IGuiElement>(mc, this.general);
        this.modelRenderer = new GuiModelRendererBodyPart(Minecraft.getMinecraft());
        this.modelRenderer.looking = false;

        /* Pose editor */
        this.tx = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (value) ->
        {
            if (this.view.delegate == this.poseEditor) this.trans.translate[0] = value;
            else if (this.part != null) this.part.part.translate[0] = value;
        });
        this.ty = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (value) ->
        {
            if (this.view.delegate == this.poseEditor) this.trans.translate[1] = value;
            else if (this.part != null) this.part.part.translate[1] = value;
        });
        this.tz = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (value) ->
        {
            if (this.view.delegate == this.poseEditor) this.trans.translate[2] = value;
            else if (this.part != null) this.part.part.translate[2] = value;
        });
        this.sx = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (value) ->
        {
            if (this.view.delegate == this.poseEditor) this.trans.scale[0] = value;
            else if (this.part != null) this.part.part.scale[0] = value;
        });
        this.sy = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (value) ->
        {
            if (this.view.delegate == this.poseEditor) this.trans.scale[1] = value;
            else if (this.part != null) this.part.part.scale[1] = value;
        });
        this.sz = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (value) ->
        {
            if (this.view.delegate == this.poseEditor) this.trans.scale[2] = value;
            else if (this.part != null) this.part.part.scale[2] = value;
        });
        this.rx = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (value) ->
        {
            if (this.view.delegate == this.poseEditor) this.trans.rotate[0] = value;
            else if (this.part != null) this.part.part.rotate[0] = value;
        });
        this.ry = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (value) ->
        {
            if (this.view.delegate == this.poseEditor) this.trans.rotate[1] = value;
            else if (this.part != null) this.part.part.rotate[1] = value;
        });
        this.rz = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (value) ->
        {
            if (this.view.delegate == this.poseEditor) this.trans.rotate[2] = value;
            else if (this.part != null) this.part.part.rotate[2] = value;
        });

        this.tx.resizer().set(0, 35, 60, 20).parent(this.area).x(1, -70);
        this.ty.resizer().set(0, 25, 60, 20).relative(this.tx.resizer());
        this.tz.resizer().set(0, 25, 60, 20).relative(this.ty.resizer());
        this.sx.resizer().set(0, 35, 60, 20).parent(this.area).x(1, -135);
        this.sy.resizer().set(0, 25, 60, 20).relative(this.sx.resizer());
        this.sz.resizer().set(0, 25, 60, 20).relative(this.sy.resizer());
        this.rx.resizer().set(0, 35, 60, 20).parent(this.area).x(1, -135 - 65);
        this.ry.resizer().set(0, 25, 60, 20).relative(this.rx.resizer());
        this.rz.resizer().set(0, 25, 60, 20).relative(this.ry.resizer());

        this.limbs = new GuiStringListElement(mc, (str) -> this.setLimb(str));
        this.limbs.resizer().parent(this.area).set(10, 40, 80, 90).h(1, -50);

        this.resetPose = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.morphs.reset"), (b) ->
        {
            this.togglePose();
            ((CustomMorph) this.morph).customPose = null;
            this.updateModelRenderer();
        });

        this.poseEditor.add(this.tx, this.ty, this.tz, this.sx, this.sy, this.sz, this.rx, this.ry, this.rz, this.limbs, this.resetPose);

        /* Body part */
        this.bodyParts = new GuiStringListElement(mc, (str) ->
        {
            this.part = ((CustomMorph) this.morph).parts.get(Integer.parseInt(str));
            this.setLimb(this.part.limb);
            this.limbs.setCurrent(this.part.limb);
            this.setPart(this.part);
        });

        this.pickMorph = GuiButtonElement.button(mc, "Pick morph", (b) ->
        {
            if (this.morphPicker == null)
            {
                this.morphPicker = new GuiCreativeMorphsMenu(mc, 6, null, null);
                this.morphPicker.resizer().parent(this.area).set(20, 20, 0, 0).w(1, -40).h(1, -40);
                this.morphPicker.callback = (morph) ->
                {
                    this.part.part.morph = morph;
                };

                GuiScreen screen = Minecraft.getMinecraft().currentScreen;

                this.morphPicker.resize(screen.width, screen.height);
                this.bodyPart.add(this.morphPicker);
            }

            this.morphPicker.setSelected(this.part.part.morph);
            this.morphPicker.setVisible(true);
        });

        this.addPart = GuiButtonElement.button(mc, "Add", (b) ->
        {
            BodyPart part = new BodyPart();
            part.limb = this.limbs.getCurrent();
            part.part = new MorphBodyPart();
            part.init();

            String str = String.valueOf(((CustomMorph) this.morph).parts.size());

            this.bodyParts.add(str);
            this.bodyParts.setCurrent(str);
            ((CustomMorph) this.morph).parts.add(part);
            this.part = part;
            this.fillBodyPart(part.part);
        });

        this.removePart = GuiButtonElement.button(mc, "Remove", (b) ->
        {
            if (this.part == null)
            {
                return;
            }

            List<BodyPart> parts = ((CustomMorph) this.morph).parts;
            int index = parts.indexOf(this.part);

            if (index != -1)
            {
                this.bodyParts.remove(String.valueOf(index));
                parts.remove(this.part);

                index--;

                if (parts.size() >= 1)
                {
                    this.setPart(parts.get(index >= 0 ? index : 0));
                }
                else
                {
                    this.setPart(null);
                }
            }
        });

        this.bodyPart.add(this.tx, this.ty, this.tz, this.sx, this.sy, this.sz, this.rx, this.ry, this.rz, this.limbs, this.bodyParts, this.pickMorph, this.addPart, this.removePart);

        /* General options */
        this.skin = new GuiTextElement(mc, 400, (str) ->
        {
            ((CustomMorph) this.morph).skin = new ResourceLocation(str);
            this.updateModelRenderer();
        });

        this.poseOnSneak = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.builder.pose_sneak"), false, (b) ->
        {
            ((CustomMorph) this.morph).currentPoseOnSneak = b.button.isChecked();
            this.updateModelRenderer();
        });

        this.poses = new GuiStringListElement(mc, (str) ->
        {
            ((CustomMorph) this.morph).currentPose = str;
            this.updateModelRenderer();
        });

        this.general.add(this.skin, this.poses, this.poseOnSneak);

        /* Switches */
        this.toggleNbt = GuiButtonElement.button(mc, "NBT", (b) -> this.toggleNbt());
        this.togglePose = GuiButtonElement.button(mc, "Pose editor", (b) -> this.togglePose());
        this.toggleBodyPart = GuiButtonElement.button(mc, "Body part", (b) -> this.toggleBodyPart());

        this.toggleNbt.resizer().parent(this.area).set(0, 10, 40, 20).x(1, -50);
        this.togglePose.resizer().relative(this.toggleNbt.resizer()).set(-75, 0, 70, 20);
        this.toggleBodyPart.resizer().parent(this.area).set(70, 10, 70, 20);

        this.children.elements.add(0, this.modelRenderer);
        this.children.add(this.toggleNbt, this.togglePose, this.toggleBodyPart, this.view);

        this.data.setVisible(false);
    }

    @Override
    public void resize(int width, int height)
    {
        this.skin.resizer().parent(this.area).set(0, 50, 115, 20).x(1, -125);
        this.poses.resizer().parent(this.area).set(0, 100, 90, 0).x(1, -100).h(1, -110);
        this.poseOnSneak.resizer().parent(this.area).set(10, 0, 150, 11).y(1, -21);
        this.resetPose.resizer().relative(this.togglePose.resizer()).set(-75, 0, 70, 20);

        this.pickMorph.resizer().relative(this.rx.resizer()).set(0, 75, 190, 20);
        this.addPart.resizer().parent(this.area).set(0, 135, 50, 20).x(1, -115);
        this.removePart.resizer().relative(this.addPart.resizer()).set(55, 0, 50, 20);
        this.bodyParts.resizer().parent(this.area).set(0, 160, 105, 0).x(1, -115).h(1, -170);

        super.resize(width, height);

        if (this.morphPicker != null)
        {
            this.morphPicker.setPerRow((int) Math.ceil(this.morphPicker.area.w / 50.0F));
        }
    }

    private void resetToggle()
    {
        this.pose = null;
        this.trans = null;
        this.modelRenderer.limb = null;

        this.part = null;

        this.tx.setVisible(true);
        this.ty.setVisible(true);
        this.tz.setVisible(true);
        this.sx.setVisible(true);
        this.sy.setVisible(true);
        this.sz.setVisible(true);
        this.rx.setVisible(true);
        this.ry.setVisible(true);
        this.rz.setVisible(true);
        this.limbs.setVisible(true);
    }

    private void toggleNbt()
    {
        this.resetToggle();

        if (this.view.delegate == null)
        {
            this.view.setDelegate(this.general);
            this.data.setVisible(false);
        }
        else
        {
            this.view.setDelegate(null);
            this.updateNBT();
            this.data.setVisible(true);
        }
    }

    private void togglePose()
    {
        this.resetToggle();

        this.data.setVisible(false);
        this.view.setDelegate(this.view.delegate == this.poseEditor ? this.general : this.poseEditor);

        if (this.view.delegate == this.poseEditor)
        {
            CustomMorph morph = (CustomMorph) this.morph;

            if (morph.customPose == null)
            {
                this.pose = morph.getPose(this.mc.thePlayer).clone();
                morph.customPose = this.pose;
            }
            else
            {
                this.pose = morph.customPose;
            }

            Map.Entry<String, ModelTransform> entry = this.pose.limbs.entrySet().iterator().next();

            this.modelRenderer.pose = this.pose;
            this.limbs.setCurrent(entry.getKey());
            this.setLimb(entry.getKey());
        }
    }

    private void toggleBodyPart()
    {
        this.resetToggle();

        this.data.setVisible(false);
        this.view.setDelegate(this.view.delegate == this.bodyPart ? this.general : this.bodyPart);

        if (this.view.delegate == this.bodyPart)
        {
            List<BodyPart> parts = ((CustomMorph) this.morph).parts;

            this.updateBodyParts();
            this.setPart(parts.size() == 0 ? null : parts.get(0));
        }
    }

    private void setPart(BodyPart part)
    {
        boolean visible = part != null;

        this.tx.setVisible(visible);
        this.ty.setVisible(visible);
        this.tz.setVisible(visible);
        this.sx.setVisible(visible);
        this.sy.setVisible(visible);
        this.sz.setVisible(visible);
        this.rx.setVisible(visible);
        this.ry.setVisible(visible);
        this.rz.setVisible(visible);
        this.limbs.setVisible(visible);
        this.pickMorph.setVisible(visible);

        if (this.part != null)
        {
            this.limbs.setCurrent(part.limb);
            this.fillBodyPart(part.part);
            this.bodyParts.setCurrent(String.valueOf(((CustomMorph) this.morph).parts.indexOf(part)));
        }
    }

    private void updateBodyParts()
    {
        CustomMorph morph = (CustomMorph) this.morph;

        this.bodyParts.clear();

        for (int i = 0; i < morph.parts.size(); i++)
        {
            this.bodyParts.add(String.valueOf(i));
        }
    }

    public void fillBodyPart(MorphBodyPart part)
    {
        if (part != null)
        {
            this.tx.trackpad.setValue(part.translate[0]);
            this.ty.trackpad.setValue(part.translate[1]);
            this.tz.trackpad.setValue(part.translate[2]);

            this.sx.trackpad.setValue(part.scale[0]);
            this.sy.trackpad.setValue(part.scale[1]);
            this.sz.trackpad.setValue(part.scale[2]);

            this.rx.trackpad.setValue(part.rotate[0]);
            this.ry.trackpad.setValue(part.rotate[1]);
            this.rz.trackpad.setValue(part.rotate[2]);
        }
    }

    private void setLimb(String str)
    {
        ModelLimb limb = ((CustomMorph) this.morph).model.limbs.get(str);

        this.modelRenderer.limb = limb;

        if (this.view.delegate == this.poseEditor)
        {
            this.setTransform(this.pose.limbs.get(str));
        }
        else if (this.part != null)
        {
            this.part.limb = str;
        }
    }

    public void setTransform(ModelTransform trans)
    {
        this.trans = trans;

        if (trans != null)
        {
            this.tx.trackpad.setValue(trans.translate[0]);
            this.ty.trackpad.setValue(trans.translate[1]);
            this.tz.trackpad.setValue(trans.translate[2]);

            this.sx.trackpad.setValue(trans.scale[0]);
            this.sy.trackpad.setValue(trans.scale[1]);
            this.sz.trackpad.setValue(trans.scale[2]);

            this.rx.trackpad.setValue(trans.rotate[0]);
            this.ry.trackpad.setValue(trans.rotate[1]);
            this.rz.trackpad.setValue(trans.rotate[2]);
        }
    }

    /**
     * This editor can only edit if the morph has a model 
     */
    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof CustomMorph && ((CustomMorph) morph).model != null;
    }

    @Override
    public void startEdit(AbstractMorph morph)
    {
        super.startEdit(morph);

        CustomMorph custom = (CustomMorph) morph;

        this.skin.setText(custom.skin == null ? "" : custom.skin.toString());
        this.poseOnSneak.button.setIsChecked(custom.currentPoseOnSneak);

        this.updateModelRenderer();
        this.modelRenderer.morph = custom;
        this.modelRenderer.limb = null;
        this.modelRenderer.reset();

        this.view.setDelegate(this.general);
        this.data.setVisible(false);

        this.poses.clear();
        this.poses.add(custom.model.poses.keySet());
        this.poses.sort();

        if (!custom.currentPose.isEmpty())
        {
            this.poses.setCurrent(custom.currentPose);
        }

        this.limbs.clear();
        this.limbs.add(custom.model.limbs.keySet());
        this.limbs.sort();
    }

    @Override
    public void finishEdit()
    {
        super.finishEdit();

        ((CustomMorph) morph).updateBodyParts();
    }

    @Override
    protected void updateNBT()
    {
        super.updateNBT();

        this.updateModelRenderer();
    }

    private void updateModelRenderer()
    {
        CustomMorph custom = (CustomMorph) this.morph;

        this.modelRenderer.model = ModelCustom.MODELS.get(custom.getKey());
        this.modelRenderer.texture = custom.skin == null ? custom.model.defaultTexture : custom.skin;
        this.modelRenderer.pose = custom.customPose == null ? custom.model.getPose(custom.currentPose) : custom.customPose;
    }

    /** 
     * Don't draw default morph
     */
    @Override
    protected void drawMorph(int mouseX, int mouseY, float partialTicks)
    {}

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(tooltip, mouseX, mouseY, partialTicks);

        if (this.view.delegate == this.general)
        {
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.skin"), this.skin.area.x, this.skin.area.y - 12, 0xffffff);
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.pose"), this.poses.area.x, this.poses.area.y - 12, 0xffffff);
        }
    }

    /**
     * Model renderer, but it also renders body parts 
     */
    public static class GuiModelRendererBodyPart extends GuiModelRenderer
    {
        public CustomMorph morph;

        public GuiModelRendererBodyPart(Minecraft mc)
        {
            super(mc);
        }

        @Override
        protected void renderModel(DummyEntity dummy, float headYaw, float headPitch, int timer, int yaw, int pitch, float partialTicks, float factor)
        {
            super.renderModel(dummy, headYaw, headPitch, timer, yaw, pitch, partialTicks, factor);

            LayerBodyPart.renderBodyParts(this.morph, this.model, partialTicks, factor);
        }

        @Override
        public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
        {
            super.draw(tooltip, mouseX, mouseY, partialTicks);
        }
    }
}