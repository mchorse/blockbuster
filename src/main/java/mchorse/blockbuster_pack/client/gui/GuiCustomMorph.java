package mchorse.blockbuster_pack.client.gui;

import java.util.Map;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiBBModelRenderer;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.parsing.obj.OBJMaterial;
import mchorse.blockbuster_pack.client.gui.GuiCustomMorph.GuiCustomMorphPanel;
import mchorse.blockbuster_pack.client.gui.GuiCustomMorph.GuiMaterialsPanel;
import mchorse.blockbuster_pack.client.gui.GuiCustomMorph.GuiModelRendererBodyPart;
import mchorse.blockbuster_pack.client.render.layers.LayerBodyPart;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiTexturePicker;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.utils.GuiDrawable;
import mchorse.mclib.utils.DummyEntity;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCustomMorph extends GuiAbstractMorph<CustomMorph>
{
    public GuiPosePanel poseEditor;
    public GuiCustomBodyPartEditor bodyPart;
    public GuiCustomMorphPanel general;
    public GuiMaterialsPanel materials;

    public GuiModelRendererBodyPart modelRenderer;

    public GuiCustomMorph(Minecraft mc)
    {
        super(mc);

        this.modelRenderer = new GuiModelRendererBodyPart(Minecraft.getMinecraft());
        this.modelRenderer.looking = false;

        /* Nice shadow on bottom */
        this.children.elements.add(0, new GuiDrawable((n) ->
        {
            this.drawGradientRect(0, this.area.getY(1) - 30, this.area.w, this.area.getY(1), 0x00000000, 0x88000000);
        }));
        this.children.elements.add(0, this.modelRenderer);

        /* Morph panels */
        this.poseEditor = new GuiPosePanel(mc, this);
        this.bodyPart = new GuiCustomBodyPartEditor(mc, this);
        this.general = new GuiCustomMorphPanel(mc, this);
        this.materials = new GuiMaterialsPanel(mc, this);

        this.defaultPanel = this.general;
        this.registerPanel(this.materials, GuiDashboard.GUI_ICONS, I18n.format("blockbuster.gui.builder.materials"), 128, 64, 128, 80);
        this.registerPanel(this.bodyPart, GuiDashboard.GUI_ICONS, I18n.format("blockbuster.gui.builder.body_part"), 128, 0, 128, 16);
        this.registerPanel(this.poseEditor, GuiDashboard.GUI_ICONS, I18n.format("blockbuster.gui.builder.pose_editor"), 80, 32, 80, 48);
        this.registerPanel(this.general, GuiDashboard.GUI_ICONS, "", 48, 0, 48, 16);
    }

    @Override
    public void setPanel(GuiMorphPanel panel)
    {
        this.modelRenderer.limb = null;
        this.updateModelRenderer();

        super.setPanel(panel);
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
    public void startEdit(CustomMorph morph)
    {
        super.startEdit(morph);

        morph.parts.reinitBodyParts();
        this.updateModelRenderer();
        this.modelRenderer.morph = morph;
        this.modelRenderer.limb = null;
        this.modelRenderer.reset();
        this.bodyPart.setLimbs(morph.model.limbs.keySet());
    }

    public void updateModelRenderer()
    {
        CustomMorph custom = this.morph;

        this.modelRenderer.materials = custom.materials;
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

    /**
     * General custom morph panel which edits common custom morph 
     * properties 
     */
    public static class GuiCustomMorphPanel extends GuiMorphPanel<CustomMorph, GuiCustomMorph>
    {
        /* General options */
        public GuiTexturePicker textures;
        public GuiButtonElement<GuiButton> skin;
        public GuiButtonElement<GuiButton> reset;
        public GuiStringListElement poses;
        public GuiButtonElement<GuiCheckBox> poseOnSneak;
        public GuiTrackpadElement scale;
        public GuiTrackpadElement scaleGui;

        public GuiCustomMorphPanel(Minecraft mc, GuiCustomMorph editor)
        {
            super(mc, editor);

            /* General options */
            this.textures = new GuiTexturePicker(mc, (rl) ->
            {
                this.morph.skin = rl;
                this.editor.updateModelRenderer();
            });

            this.skin = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.builder.pick_skin"), (b) ->
            {
                this.textures.refresh();
                this.textures.fill(this.morph.skin);
                this.textures.setVisible(true);
            });

            this.reset = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.morphs.reset"), (b) ->
            {
                this.morph.currentPose = "";
                this.poses.setCurrent("");
                this.editor.updateModelRenderer();
            });

            this.poseOnSneak = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.builder.pose_sneak"), false, (b) ->
            {
                this.morph.currentPoseOnSneak = b.button.isChecked();
                this.editor.updateModelRenderer();
            });

            this.poses = new GuiStringListElement(mc, (str) ->
            {
                this.morph.currentPose = str;
                this.editor.updateModelRenderer();
            });

            this.scale = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.me.options.scale"), (value) ->
            {
                this.morph.scale = value;
            });

            this.scaleGui = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.me.options.scale_gui"), (value) ->
            {
                this.morph.scaleGui = value;
            });

            this.skin.resizer().parent(this.area).set(10, 10, 105, 20);
            this.reset.resizer().relative(this.skin.resizer()).set(0, 25, 105, 20);
            this.poseOnSneak.resizer().parent(this.area).set(10, 0, 105, 11).y(1, -21);
            this.poses.resizer().parent(this.area).set(10, 75, 105, 0).h(1, -100);
            this.textures.resizer().parent(this.area).set(10, 10, 0, 0).w(1, -20).h(1, -20);
            this.scale.resizer().parent(this.area).set(10, 10, 105, 20).x(1, -115).y(1, -50);
            this.scaleGui.resizer().relative(this.scale.resizer()).set(0, 25, 105, 20);

            this.children.add(this.skin, this.reset, this.poses, this.poseOnSneak, this.scale, this.scaleGui, this.textures);
        }

        @Override
        public void fillData(CustomMorph morph)
        {
            super.fillData(morph);

            this.textures.setVisible(false);
            this.poseOnSneak.button.setIsChecked(morph.currentPoseOnSneak);
            this.scale.setValue(morph.scale);
            this.scaleGui.setValue(morph.scaleGui);

            this.poses.clear();
            this.poses.add(morph.model.poses.keySet());
            this.poses.sort();

            if (!morph.currentPose.isEmpty())
            {
                this.poses.setCurrent(morph.currentPose);
            }
        }

        @Override
        public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
        {
            Gui.drawRect(this.poses.area.x, this.poses.area.y, this.poses.area.getX(1), this.poses.area.getY(1), 0x88000000);
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.pose"), this.poses.area.x, this.poses.area.y - 12, 0xffffff);
            super.draw(tooltip, mouseX, mouseY, partialTicks);
        }
    }

    /**
     * Custom model morph panel which allows editing custom textures 
     * for materials of the custom model morph 
     */
    public static class GuiMaterialsPanel extends GuiMorphPanel<CustomMorph, GuiCustomMorph>
    {
        /* Materials */
        public GuiStringListElement materials;
        public GuiButtonElement<GuiButton> pickTexture;
        public GuiTexturePicker picker;

        public GuiMaterialsPanel(Minecraft mc, GuiCustomMorph editor)
        {
            super(mc, editor);

            /* Materials view */
            this.materials = new GuiStringListElement(mc, (str) -> this.setCurrentMaterial(str));
            this.pickTexture = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.builder.pick_texture"), (b) ->
            {
                this.picker.setVisible(true);
                this.picker.refresh();
            });
            this.picker = new GuiTexturePicker(mc, (rl) -> this.setCurrentMaterialRL(rl));

            this.materials.resizer().parent(this.area).set(10, 50, 105, 0).h(1, -60);
            this.pickTexture.resizer().parent(this.area).set(10, 10, 105, 20);
            this.picker.resizer().parent(this.area).set(10, 10, 0, 0).w(1, -20).h(1, -20);

            this.children.add(this.materials, this.pickTexture, this.picker);
        }

        private void setCurrentMaterial(String str)
        {
            ResourceLocation rl = this.morph.materials.get(str);

            this.materials.setCurrent(str);
            this.picker.fill(rl);
        }

        private void setCurrentMaterialRL(ResourceLocation rl)
        {
            String key = this.materials.getCurrent();

            if (rl == null)
            {
                this.morph.materials.remove(key);
            }
            else
            {
                this.morph.materials.put(key, rl);
            }

            this.editor.updateModelRenderer();
        }

        @Override
        public void fillData(CustomMorph morph)
        {
            super.fillData(morph);

            this.materials.clear();

            for (Map.Entry<String, OBJMaterial> entry : morph.model.materials.entrySet())
            {
                if (entry.getValue().useTexture)
                {
                    this.materials.add(entry.getKey());
                }
            }

            this.materials.sort();
            this.picker.setVisible(false);

            if (!this.materials.getList().isEmpty())
            {
                this.setCurrentMaterial(this.materials.getList().get(0));
            }
        }

        @Override
        public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
        {
            Gui.drawRect(this.materials.area.x, this.materials.area.y, this.materials.area.getX(1), this.materials.area.getY(1), 0x88000000);
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.obj_materials"), this.materials.area.x, this.materials.area.y - 12, 0xffffff);
            super.draw(tooltip, mouseX, mouseY, partialTicks);
        }
    }

    /**
     * Model renderer, but it also renders body parts 
     */
    public static class GuiModelRendererBodyPart extends GuiBBModelRenderer
    {
        public CustomMorph morph;

        public GuiModelRendererBodyPart(Minecraft mc)
        {
            super(mc);
        }

        @Override
        protected float getScale()
        {
            return this.morph == null ? 1F : this.morph.scale;
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