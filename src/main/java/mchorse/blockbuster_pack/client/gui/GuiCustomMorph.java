package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.formats.obj.OBJMaterial;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiBBModelRenderer;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.utils.BBIcons;
import mchorse.blockbuster_pack.client.render.layers.LayerBodyPart;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTexturePicker;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiInterpolationList;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDrawable;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.DummyEntity;
import mchorse.mclib.utils.Interpolation;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public class GuiCustomMorph extends GuiAbstractMorph<CustomMorph>
{
    public GuiPosePanel poseEditor;
    public GuiCustomBodyPartEditor bodyPart;
    public GuiCustomMorphPanel general;
    public GuiMaterialsPanel materials;
    public GuiModelRendererBodyPart bbRenderer;

    public GuiCustomMorph(Minecraft mc)
    {
        super(mc);

        /* Nice shadow on bottom */
        this.prepend(new GuiDrawable((n) ->
        {
            this.drawGradientRect(0, this.area.ey() - 30, this.area.w, this.area.ey(), 0x00000000, 0x88000000);
        }));

        /* Morph panels */
        this.poseEditor = new GuiPosePanel(mc, this);
        this.bodyPart = new GuiCustomBodyPartEditor(mc, this);
        this.general = new GuiCustomMorphPanel(mc, this);
        this.materials = new GuiMaterialsPanel(mc, this);

        this.defaultPanel = this.general;
        this.registerPanel(this.materials, IKey.lang("blockbuster.gui.builder.materials"), Icons.MATERIAL);
        this.registerPanel(this.bodyPart, IKey.lang("blockbuster.gui.builder.body_part"), Icons.LIMB);
        this.registerPanel(this.poseEditor, IKey.lang("blockbuster.gui.builder.pose_editor"), Icons.POSE);
        this.registerPanel(this.general, IKey.EMPTY, Icons.GEAR);
    }

    @Override
    protected GuiModelRenderer createMorphRenderer(Minecraft mc)
    {
        this.bbRenderer = new GuiModelRendererBodyPart(Minecraft.getMinecraft());
        this.bbRenderer.looking = false;
        this.bbRenderer.origin = true;
        this.bbRenderer.picker((limb) ->
        {
            if (this.view.delegate instanceof ILimbSelector)
            {
                ((ILimbSelector) this.view.delegate).setLimb(limb);
            }
        });

        return this.bbRenderer;
    }

    @Override
    public void setPanel(GuiMorphPanel panel)
    {
        this.bbRenderer.limb = null;
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
        this.bbRenderer.morph = morph;
        this.bbRenderer.limb = null;
        this.bbRenderer.reset();
        this.bodyPart.setLimbs(morph.model.limbs.keySet());
    }

    public void updateModelRenderer()
    {
        CustomMorph custom = this.morph;

        this.bbRenderer.materials = custom.materials;
        this.bbRenderer.model = ModelCustom.MODELS.get(custom.getKey());
        this.bbRenderer.texture = custom.skin == null ? custom.model.defaultTexture : custom.skin;
        this.bbRenderer.pose = custom.customPose == null ? custom.model.getPose(custom.currentPose) : custom.customPose;
    }

    /**
     * General custom morph panel which edits common custom morph 
     * properties 
     */
    public static class GuiCustomMorphPanel extends GuiMorphPanel<CustomMorph, GuiCustomMorph>
    {
        /* General options */
        public GuiStringListElement models;
        public GuiButtonElement model;
        public GuiTexturePicker textures;
        public GuiButtonElement skin;
        public GuiButtonElement reset;
        public GuiStringListElement poses;
        public GuiToggleElement poseOnSneak;
        public GuiTrackpadElement scale;
        public GuiTrackpadElement scaleGui;
        public GuiToggleElement animates;
        public GuiToggleElement ignored;
        public GuiTrackpadElement animationDuration;
        public GuiButtonElement pickInterpolation;
        public GuiListElement<Interpolation> interpolations;

        public GuiCustomMorphPanel(Minecraft mc, GuiCustomMorph editor)
        {
            super(mc, editor);

            /* General options */
            this.models = new GuiStringListElement(mc, (string) ->
            {
                this.morph.changeModel(string.get(0));
                this.editor.updateModelRenderer();
                this.editor.poseEditor.fillData(this.morph);
                this.editor.bodyPart.setLimbs(this.morph.model.limbs.keySet());
            });
            this.models.background();

            this.model = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.builder.pick_model"), (b) -> this.models.toggleVisible());

            this.textures = new GuiTexturePicker(mc, (rl) ->
            {
                this.morph.skin = rl;
                this.editor.updateModelRenderer();
            });

            this.skin = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.builder.pick_skin"), (b) ->
            {
                this.textures.refresh();
                this.textures.fill(this.morph.skin);
                this.textures.setVisible(true);
            });

            this.reset = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.morphs.reset"), (b) ->
            {
                this.morph.currentPose = "";
                this.poses.setCurrent("");
                this.editor.updateModelRenderer();
            });

            this.poseOnSneak = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.builder.pose_sneak"), false, (b) ->
            {
                this.morph.currentPoseOnSneak = this.poseOnSneak.isToggled();
                this.editor.updateModelRenderer();
            });

            this.poses = new GuiStringListElement(mc, (str) ->
            {
                this.morph.currentPose = str.get(0);
                this.editor.updateModelRenderer();
            });

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

            this.animates = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.builder.animates"), false, (b) ->
            {
                this.morph.animation.animates = this.animates.isToggled();
            });

            this.ignored = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.builder.ignored"), false, (b) ->
            {
                this.morph.animation.ignored = this.ignored.isToggled();
            });

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

            this.skin.flex().relative(this.area).set(10, 10, 105, 20);
            this.model.flex().relative(this.skin.resizer()).set(0, 25, 105, 20);
            this.reset.flex().relative(this.model.resizer()).set(0, 25, 105, 20);
            this.poseOnSneak.flex().relative(this.area).set(10, 0, 105, 11).y(1, -21);
            this.poses.flex().relative(this.area).set(10, 100, 105, 0).h(1, -125);
            this.textures.flex().relative(this.area).set(10, 10, 0, 0).w(1, -20).h(1, -20);
            this.models.flex().relative(this.model.resizer()).set(0, 20, 0, 120).w(1, 0);
            this.scale.flex().relative(this.area).set(10, 10, 105, 20).x(1, -115).y(1, -50);
            this.scaleGui.flex().relative(this.scale.resizer()).set(0, 25, 105, 20);

            this.animates.flex().relative(this.area).set(0, 40, 105, 20).x(1, -110);
            this.ignored.flex().relative(this.animates.resizer()).set(0, 0, 105, 20).y(1, 5);
            this.animationDuration.flex().relative(this.ignored.resizer()).set(0, 0, 100, 20).y(1, 5);
            this.pickInterpolation.flex().relative(this.animationDuration.resizer()).set(0, 0, 100, 20).y(1, 5);
            this.interpolations.flex().relative(this.pickInterpolation.resizer()).set(0, 20, 100, 96);

            this.add(this.model, this.skin, this.reset, this.poses, this.poseOnSneak, this.scale, this.scaleGui);
            this.add(this.animates, this.ignored, this.animationDuration, this.pickInterpolation, this.interpolations);
            this.add(this.models, this.textures);
        }

        @Override
        public void fillData(CustomMorph morph)
        {
            super.fillData(morph);

            this.models.setVisible(false);
            this.models.clear();
            this.models.add(Blockbuster.proxy.models.models.keySet());
            this.models.sort();
            this.models.setCurrentScroll(morph.getKey());

            this.textures.setVisible(false);
            this.poseOnSneak.toggled(morph.currentPoseOnSneak);
            this.scale.setValue(morph.scale);
            this.scaleGui.setValue(morph.scaleGui);
            this.animates.toggled(morph.animation.animates);
            this.ignored.toggled(morph.animation.ignored);
            this.animationDuration.setValue(morph.animation.duration);
            this.interpolations.setCurrent(morph.animation.interp);
            this.interpolations.setVisible(false);

            this.poses.clear();
            this.poses.add(morph.model.poses.keySet());
            this.poses.sort();

            if (!morph.currentPose.isEmpty())
            {
                this.poses.setCurrent(morph.currentPose);
            }
        }

        @Override
        public void draw(GuiContext context)
        {
            Gui.drawRect(this.poses.area.x, this.poses.area.y, this.poses.area.ex(), this.poses.area.ey(), 0x88000000);
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.pose"), this.poses.area.x, this.poses.area.y - 12, 0xffffff);
            super.draw(context);
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
        public GuiButtonElement pickTexture;
        public GuiTexturePicker picker;

        public GuiMaterialsPanel(Minecraft mc, GuiCustomMorph editor)
        {
            super(mc, editor);

            /* Materials view */
            this.materials = new GuiStringListElement(mc, (str) -> this.setCurrentMaterial(str.get(0)));
            this.pickTexture = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.builder.pick_texture"), (b) ->
            {
                this.picker.setVisible(true);
                this.picker.refresh();
            });
            this.picker = new GuiTexturePicker(mc, this::setCurrentMaterialRL);

            this.materials.flex().relative(this.area).set(10, 50, 105, 0).h(1, -60);
            this.materials.background();
            this.pickTexture.flex().relative(this.area).set(10, 10, 105, 20);
            this.picker.flex().relative(this.area).set(10, 10, 0, 0).w(1, -20).h(1, -20);

            this.add(this.materials, this.pickTexture, this.picker);
        }

        private void setCurrentMaterial(String str)
        {
            ResourceLocation rl = this.morph.materials.get(str);

            this.materials.setCurrent(str);
            this.picker.fill(rl);
        }

        private void setCurrentMaterialRL(ResourceLocation rl)
        {
            String key = this.materials.getCurrentFirst();

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
        public void draw(GuiContext context)
        {
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.obj_materials"), this.materials.area.x, this.materials.area.y - 12, 0xffffff);
            super.draw(context);
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

            LayerBodyPart.renderBodyParts(dummy, this.morph, this.model, 0, 0, partialTicks, dummy.ticksExisted + partialTicks, headYaw, headPitch, factor);
        }
    }
}