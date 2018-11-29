package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelRenderer;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.DummyEntity;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster_pack.client.render.layers.LayerBodyPart;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.utils.Resizer.Measure;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.elements.GuiAbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCustomMorph extends GuiAbstractMorph
{
    public GuiElements general = new GuiElements();
    public GuiPoseEditor poseEditor;
    public GuiBodyPartEditor bodyPart;

    public GuiDelegateElement<IGuiElement> view;
    public GuiModelRendererBodyPart modelRenderer;

    public GuiButtonElement<GuiButton> toggleNbt;
    public GuiButtonElement<GuiButton> togglePose;
    public GuiButtonElement<GuiButton> toggleBodyPart;

    /* General options */
    public GuiTextElement skin;
    public GuiStringListElement poses;
    public GuiButtonElement<GuiCheckBox> poseOnSneak;

    public GuiCustomMorph(Minecraft mc)
    {
        super(mc);

        this.view = new GuiDelegateElement<IGuiElement>(mc, this.general);
        this.view.resizer().parent(this.area).set(0, 0, 1, 1, Measure.RELATIVE);
        this.modelRenderer = new GuiModelRendererBodyPart(Minecraft.getMinecraft());
        this.modelRenderer.looking = false;

        /* General options */
        this.skin = new GuiTextElement(mc, 400, (str) ->
        {
            this.getMorph().skin = new ResourceLocation(str);
            this.updateModelRenderer();
        });

        this.poseOnSneak = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.builder.pose_sneak"), false, (b) ->
        {
            this.getMorph().currentPoseOnSneak = b.button.isChecked();
            this.updateModelRenderer();
        });

        this.poses = new GuiStringListElement(mc, (str) ->
        {
            this.getMorph().currentPose = str;
            this.updateModelRenderer();
        });

        this.skin.resizer().parent(this.area).set(0, 50, 115, 20).x(1, -125);
        this.poseOnSneak.resizer().parent(this.area).set(10, 0, 150, 11).y(1, -21);
        this.poses.resizer().parent(this.area).set(0, 100, 90, 0).x(1, -100).h(1, -110);

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

        /* External editors */
        this.poseEditor = new GuiPoseEditor(mc, this);
        this.bodyPart = new GuiBodyPartEditor(mc, this);
    }

    private void resetToggle()
    {
        this.modelRenderer.limb = null;
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

    public void togglePose()
    {
        this.resetToggle();

        this.data.setVisible(false);
        this.view.setDelegate(this.view.delegate == this.poseEditor ? this.general : this.poseEditor);

        if (this.view.delegate == this.poseEditor)
        {
            this.poseEditor.createPose();
        }
    }

    public void toggleBodyPart()
    {
        this.resetToggle();

        this.data.setVisible(false);
        this.view.setDelegate(this.view.delegate == this.bodyPart ? this.general : this.bodyPart);

        if (this.view.delegate == this.bodyPart)
        {
            this.bodyPart.setupBodyEditor();
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

        this.poseEditor.startEditing(custom);
        this.bodyPart.startEditing(custom);
    }

    public CustomMorph getMorph()
    {
        return (CustomMorph) this.morph;
    }

    @Override
    protected void updateNBT()
    {
        super.updateNBT();

        this.updateModelRenderer();
    }

    public void updateModelRenderer()
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

            LayerBodyPart.renderBodyParts(dummy, this.morph, this.model, partialTicks, factor);
        }

        @Override
        public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
        {
            super.draw(tooltip, mouseX, mouseY, partialTicks);
        }
    }
}