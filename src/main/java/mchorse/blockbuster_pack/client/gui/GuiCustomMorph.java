package mchorse.blockbuster_pack.client.gui;

import java.io.File;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelRenderer;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.DummyEntity;
import mchorse.blockbuster.client.gui.elements.GuiTexturePicker;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.utils.TextureLocation;
import mchorse.blockbuster_pack.client.render.layers.LayerBodyPart;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.utils.GuiDrawable;
import mchorse.mclib.client.gui.utils.Resizer.Measure;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.elements.GuiAbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCustomMorph extends GuiAbstractMorph
{
    public GuiElements<IGuiElement> general = new GuiElements<IGuiElement>();
    public GuiElements<IGuiElement> materials = new GuiElements<IGuiElement>();
    public GuiPoseEditor poseEditor;
    public GuiBodyPartEditor bodyPart;

    public GuiDelegateElement<IGuiElement> view;
    public GuiModelRendererBodyPart modelRenderer;

    public GuiButtonElement<GuiButton> toggleNbt;
    public GuiButtonElement<GuiButton> togglePose;
    public GuiButtonElement<GuiButton> toggleBodyPart;
    public GuiButtonElement<GuiButton> toggleMaterials;

    /* General options */
    public GuiTexturePicker textures;
    public GuiButtonElement<GuiButton> skin;
    public GuiButtonElement<GuiButton> reset;
    public GuiStringListElement poses;
    public GuiButtonElement<GuiCheckBox> poseOnSneak;

    /* Materials */
    public GuiStringListElement materialList;
    public GuiButtonElement<GuiButton> pickMaterialTexture;
    public GuiTexturePicker materialPicker;

    public GuiCustomMorph(Minecraft mc)
    {
        super(mc);

        this.view = new GuiDelegateElement<IGuiElement>(mc, this.general);
        this.view.resizer().parent(this.area).set(0, 0, 1, 1, Measure.RELATIVE);
        this.modelRenderer = new GuiModelRendererBodyPart(Minecraft.getMinecraft());
        this.modelRenderer.looking = false;

        /* General options */
        this.textures = new GuiTexturePicker(mc, (rl) ->
        {
            this.getMorph().skin = rl;
            this.updateModelRenderer();
        });

        this.skin = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.builder.pick_skin"), (b) ->
        {
            this.textures.setVisible(true);
        });

        this.reset = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.morphs.reset"), (b) ->
        {
            this.getMorph().currentPose = "";
            this.poses.setCurrent("");
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

        this.skin.resizer().parent(this.area).set(10, 10, 105, 20);
        this.reset.resizer().relative(this.skin.resizer()).set(0, 25, 105, 20);
        this.poseOnSneak.resizer().parent(this.area).set(10, 0, 105, 11).y(1, -49);
        this.poses.resizer().parent(this.area).set(10, 75, 105, 0).h(1, -130);
        this.textures.resizer().parent(this.area).set(10, 10, 0, 0).w(1, -20).h(1, -20);

        this.general.add(this.skin, this.reset, this.poses, this.poseOnSneak, this.textures);

        /* Materials view */
        this.materialList = new GuiStringListElement(mc, (str) -> this.setCurrentMaterial(str));
        this.pickMaterialTexture = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.builder.pick_texture"), (b) -> this.materialPicker.setVisible(true));
        this.materialPicker = new GuiTexturePicker(mc, (rl) -> this.setCurrentMaterialRL(rl));

        this.materialList.resizer().parent(this.area).set(10, 50, 105, 0).h(1, -85);
        this.pickMaterialTexture.resizer().parent(this.area).set(10, 10, 105, 20);
        this.materialPicker.resizer().parent(this.area).set(10, 10, 0, 0).w(1, -20).h(1, -20);

        this.materials.add(this.materialList, this.pickMaterialTexture, this.materialPicker);

        /* Switches */
        this.toggleNbt = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.builder.nbt"), (b) -> this.toggleNbt());
        this.togglePose = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.builder.pose_editor"), (b) -> this.togglePose());
        this.toggleBodyPart = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.builder.body_part"), (b) -> this.toggleBodyPart());
        this.toggleMaterials = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.builder.materials"), (b) -> this.toggleMaterials());

        this.toggleNbt.resizer().parent(this.area).set(0, 10, 40, 20).x(1, -50).y(1, -25);
        this.togglePose.resizer().relative(this.toggleNbt.resizer()).set(-75, 0, 70, 20);
        this.toggleBodyPart.resizer().relative(this.togglePose.resizer()).set(-75, 0, 70, 20);
        this.toggleMaterials.resizer().relative(this.toggleBodyPart.resizer()).set(-65, 0, 60, 20);
        this.finish.resizer().parent(this.area).set(10, 0, 105, 20).y(1, -25);

        /* Draw overlay stuff */
        this.children.elements.add(0, new GuiDrawable((n) ->
        {
            this.drawGradientRect(0, this.area.getY(1) - 30, this.area.w, this.area.getY(1), 0x00000000, 0x88000000);

            if (this.view.delegate == this.general)
            {
                Gui.drawRect(this.poses.area.x, this.poses.area.y, this.poses.area.getX(1), this.poses.area.getY(1), 0x88000000);
                this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.pose"), this.poses.area.x, this.poses.area.y - 12, 0xffffff);
            }
            else if (this.view.delegate == this.materials)
            {
                Gui.drawRect(this.materialList.area.x, this.materialList.area.y, this.materialList.area.getX(1), this.materialList.area.getY(1), 0x88000000);
                this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.obj_materials"), this.materialList.area.x, this.materialList.area.y - 12, 0xffffff);
            }
        }));
        this.children.elements.add(0, this.modelRenderer);
        this.children.add(this.toggleNbt, this.togglePose, this.toggleBodyPart, this.toggleMaterials, this.view);

        this.data.setVisible(false);
        this.data.resizer().y(1, -55);

        /* External editors */
        this.poseEditor = new GuiPoseEditor(mc, this);
        this.bodyPart = new GuiBodyPartEditor(mc, this);
    }

    private void setCurrentMaterial(String str)
    {
        this.materialList.setCurrent(str);

        ResourceLocation rl = this.getMorph().materials.get(str);

        this.materialPicker.picker.clear();

        for (File folder : ClientProxy.actorPack.pack.folders)
        {
            for (File model : folder.listFiles())
            {
                if (!model.isDirectory())
                {
                    continue;
                }

                File material = new File(model, "skins/" + str);

                if (material.exists())
                {
                    for (File texture : material.listFiles())
                    {
                        String name = texture.getName();

                        if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".gif"))
                        {
                            this.materialPicker.picker.add(new TextureLocation("b.a", model.getName() + "/skins/" + str + "/" + name));
                        }
                    }
                }
            }
        }

        this.materialPicker.picker.sort();
        this.materialPicker.set(rl);
    }

    private void setCurrentMaterialRL(ResourceLocation rl)
    {
        String key = this.materialList.getCurrent();

        if (rl == null)
        {
            this.getMorph().materials.remove(key);
        }
        else
        {
            this.getMorph().materials.put(key, rl);
        }

        this.updateModelRenderer();
    }

    private void resetToggle()
    {
        this.modelRenderer.limb = null;
    }

    private void toggleNbt()
    {
        if (this.view.delegate == null)
        {
            this.startEdit(this.morph);
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

    public void toggleMaterials()
    {
        this.resetToggle();

        this.data.setVisible(false);
        this.view.setDelegate(this.view.delegate == this.materials ? this.general : this.materials);
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
        String key = custom.getKey();

        this.textures.picker.clear();

        for (String skin : ClientProxy.actorPack.pack.getSkins(key))
        {
            this.textures.picker.add(new TextureLocation("b.a:" + key + "/" + skin));
        }

        for (String skin : ClientProxy.actorPack.pack.getSkins(custom.model.skins))
        {
            this.textures.picker.add(new TextureLocation("b.a:" + custom.model.skins + "/" + skin));
        }

        this.textures.picker.sort();
        this.textures.set(custom.skin);
        this.textures.setVisible(false);
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

        this.toggleMaterials.setEnabled(!custom.model.materials.isEmpty());

        if (this.toggleMaterials.isEnabled())
        {
            this.materialList.clear();
            this.materialList.add(custom.model.materials.keySet());
            this.materialList.sort();

            this.materialPicker.setVisible(false);

            this.setCurrentMaterial(this.materialList.getList().get(0));
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