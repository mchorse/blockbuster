package mchorse.blockbuster_pack.client.gui;

import java.util.Map;

import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelRenderer;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster_pack.morphs.CustomMorph;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiCustomMorph extends GuiAbstractMorph
{
    private GuiElements poseEditor = new GuiElements();
    private GuiElements general = new GuiElements();

    private GuiDelegateElement<IGuiElement> view;
    private GuiModelRenderer modelRenderer;

    private GuiButtonElement<GuiButton> toggleNbt;
    private GuiButtonElement<GuiButton> togglePose;

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

    private GuiStringListElement list;
    private GuiButtonElement<GuiButton> resetPose;

    private ModelPose pose;
    private ModelTransform trans;

    /* General options */
    public GuiTextElement skin;
    public GuiStringListElement poses;
    public GuiButtonElement<GuiCheckBox> poseOnSneak;

    public GuiCustomMorph(Minecraft mc)
    {
        super(mc);

        this.view = new GuiDelegateElement<IGuiElement>(mc, this.general);
        this.modelRenderer = new GuiModelRenderer(Minecraft.getMinecraft());
        this.modelRenderer.looking = false;

        /* Pose editor */
        this.tx = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (value) -> this.trans.translate[0] = value);
        this.ty = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (value) -> this.trans.translate[1] = value);
        this.tz = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (value) -> this.trans.translate[2] = value);
        this.sx = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (value) -> this.trans.scale[0] = value);
        this.sy = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (value) -> this.trans.scale[1] = value);
        this.sz = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (value) -> this.trans.scale[2] = value);
        this.rx = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (value) -> this.trans.rotate[0] = value);
        this.ry = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (value) -> this.trans.rotate[1] = value);
        this.rz = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (value) -> this.trans.rotate[2] = value);

        this.tx.resizer().set(0, 40, 60, 20).parent(this.area).x(1, -70).y(0.5F, -40);
        this.ty.resizer().set(0, 25, 60, 20).relative(this.tx.resizer());
        this.tz.resizer().set(0, 25, 60, 20).relative(this.ty.resizer());
        this.sx.resizer().set(0, 40, 60, 20).parent(this.area).x(1, -135).y(0.5F, -40);
        this.sy.resizer().set(0, 25, 60, 20).relative(this.sx.resizer());
        this.sz.resizer().set(0, 25, 60, 20).relative(this.sy.resizer());
        this.rx.resizer().set(0, 40, 60, 20).parent(this.area).x(1, -135 - 65).y(0.5F, -40);
        this.ry.resizer().set(0, 25, 60, 20).relative(this.rx.resizer());
        this.rz.resizer().set(0, 25, 60, 20).relative(this.ry.resizer());

        this.list = new GuiStringListElement(mc, (str) -> this.setLimb(str));
        this.list.resizer().parent(this.area).set(5, 10, 80, 90).h(1, -15);

        this.resetPose = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.morphs.reset"), (b) ->
        {
            this.togglePose();
            ((CustomMorph) this.morph).customPose = null;
            this.updateModelRenderer();
        });

        this.poseEditor.add(this.tx, this.ty, this.tz, this.sx, this.sy, this.sz, this.rx, this.ry, this.rz, this.list, this.resetPose);

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

        this.toggleNbt.resizer().parent(this.area).set(0, 10, 40, 20).x(1, -50);
        this.togglePose.resizer().relative(this.toggleNbt.resizer()).set(-75, 0, 70, 20);

        this.children.elements.add(0, this.modelRenderer);
        this.children.add(this.view, this.toggleNbt, this.togglePose);

        this.data.setVisible(false);
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

        this.skin.resizer().parent(this.area).set(0, 50, 115, 20).x(1, -125);
        this.poses.resizer().parent(this.area).set(0, 100, 90, 0).x(1, -100).h(1, -110);
        this.poseOnSneak.resizer().parent(this.area).set(10, 0, 150, 11).y(1, -21);
        this.resetPose.resizer().relative(this.togglePose.resizer()).set(-75, 0, 70, 20);
    }

    private void toggleNbt()
    {
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

            this.setTransform(entry.getValue());
            this.modelRenderer.pose = this.pose;

            this.list.clear();
            this.list.add(this.pose.limbs.keySet());
            this.list.sort();
            this.list.setCurrent(entry.getKey());
        }
        else
        {
            this.modelRenderer.limb = null;
        }
    }

    private void setLimb(String str)
    {
        ModelLimb limb = ((CustomMorph) this.morph).model.limbs.get(str);

        this.modelRenderer.limb = limb;
        this.setTransform(this.pose.limbs.get(str));
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
    }

    @Override
    protected void updateNBT()
    {
        super.updateNBT();

        this.updateModelRenderer();
    }

    private void updateModelRenderer()
    {
        CustomMorph custom = (CustomMorph) morph;

        this.modelRenderer.model = ModelCustom.MODELS.get(custom.getKey());
        this.modelRenderer.texture = custom.skin == null ? custom.model.defaultTexture : custom.skin;
        this.modelRenderer.pose = custom.customPose == null ? custom.model.getPose(custom.currentPose) : custom.customPose;
    }

    /** 
     * Don't draw default 
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
}