package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.api.formats.obj.OBJMaterial;
import mchorse.blockbuster.api.formats.obj.ShapeKey;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiBBModelRenderer;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster_pack.client.render.layers.LayerBodyPart;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.input.GuiTexturePicker;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDrawable;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.Label;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import mchorse.mclib.utils.MathUtils;
import mchorse.mclib.utils.files.entries.AbstractEntry;
import mchorse.mclib.utils.files.entries.FileEntry;
import mchorse.mclib.utils.files.entries.FolderEntry;
import mchorse.mclib.utils.resources.RLUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public class GuiCustomMorph extends GuiAbstractMorph<CustomMorph>
{
    public GuiPosePanel poseEditor;
    public GuiCustomBodyPartEditor bodyPart;
    public GuiMaterialsPanel materials;
    public GuiModelRendererBodyPart bbRenderer;

    public static void addSkins(AbstractMorph morph, List<Label<NBTTagCompound>> list, String name, FolderEntry entry)
    {
        if (entry == null)
        {
            return;
        }

        /* Add presets that are part of JSON file */
        if (morph instanceof CustomMorph)
        {
            CustomMorph custom = (CustomMorph) morph;

            if (custom.model != null && !custom.model.presets.isEmpty())
            {
                for (Map.Entry<String, String> preset : custom.model.presets.entrySet())
                {
                    NBTTagCompound tag = null;

                    try
                    {
                        tag = JsonToNBT.getTagFromJson(preset.getValue());
                    }
                    catch (Exception e)
                    {}

                    if (tag != null)
                    {
                        NBTTagCompound morphTag = custom.toNBT();

                        morphTag.merge(tag);
                        list.add(new Label<NBTTagCompound>(IKey.str(preset.getKey()), morphTag));
                    }
                }
            }
        }

        for (AbstractEntry childEntry : entry.getEntries())
        {
            if (childEntry instanceof FileEntry)
            {
                ResourceLocation location = ((FileEntry) childEntry).resource;
                String label = location.getResourcePath();
                int index = label.indexOf("/skins/");

                if (index != -1)
                {
                    label = label.substring(index + 7);
                }

                addPreset(morph, list, name, label, location);
            }
            else if (childEntry instanceof FolderEntry)
            {
                FolderEntry childFolder = (FolderEntry) childEntry;

                if (!childFolder.isTop())
                {
                    addSkins(morph, list, name, childFolder);
                }
            }
        }
    }

    public static void addPreset(AbstractMorph morph, List<Label<NBTTagCompound>> list, String name, String label, ResourceLocation skin)
    {
        try
        {
            NBTTagCompound tag = morph.toNBT();

            tag.setString(name, skin.toString());
            list.add(new Label<>(IKey.str(label), tag));
        }
        catch (Exception e)
        {}
    }

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
        this.materials = new GuiMaterialsPanel(mc, this);

        this.defaultPanel = this.poseEditor;
        this.registerPanel(this.materials, IKey.lang("blockbuster.gui.builder.materials"), Icons.MATERIAL);
        this.registerPanel(this.bodyPart, IKey.lang("blockbuster.gui.builder.body_part"), Icons.LIMB);
        this.registerPanel(this.poseEditor, IKey.lang("blockbuster.gui.builder.pose_editor"), Icons.POSE);

        this.keys().register(IKey.lang("blockbuster.gui.builder.pick_skin"), Keyboard.KEY_P, () ->
        {
            this.setPanel(this.materials);

            if (!this.materials.picker.hasParent())
            {
                this.materials.skin.clickItself(GuiBase.getCurrent());
            }
        }).held(Keyboard.KEY_LSHIFT);
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
    protected void setupRenderer(CustomMorph morph)
    {
        super.setupRenderer(morph);

        ModelPose pose = morph.getCurrentPose();

        if (pose != null)
        {
            this.bbRenderer.setScale(1.25F + pose.size[0]);
            this.bbRenderer.setPosition(0, pose.size[1] / 2F, 0);
        }
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
        morph.parts.reinitBodyParts();
        this.bodyPart.setLimbs(morph.model.limbs.keySet());

        this.bbRenderer.morph = morph;
        this.bbRenderer.limb = null;

        super.startEdit(morph);

        this.updateModelRenderer();
    }

    @Override
    public List<Label<NBTTagCompound>> getPresets(CustomMorph morph)
    {
        List<Label<NBTTagCompound>> list = new ArrayList<Label<NBTTagCompound>>();
        String key = morph.getKey();

        addSkins(morph, list, "Skin", ClientProxy.tree.getByPath(key + "/skins", null));
        addSkins(morph, list, "Skin", ClientProxy.tree.getByPath(morph.model.skins + "/skins", null));

        return list;
    }

    public void updateModelRenderer()
    {
        CustomMorph custom = this.morph;

        this.bbRenderer.materials = custom.materials;
        this.bbRenderer.shapes = custom.getShapes();
        this.bbRenderer.model = ModelCustom.MODELS.get(custom.getKey());
        this.bbRenderer.texture = custom.skin == null ? custom.model.defaultTexture : custom.skin;
        this.bbRenderer.pose = custom.customPose == null ? custom.model.getPose(custom.currentPose) : custom.customPose;
    }

    /**
     * Custom model morph panel which allows editing custom textures 
     * for materials of the custom model morph 
     */
    public static class GuiMaterialsPanel extends GuiMorphPanel<CustomMorph, GuiCustomMorph>
    {
        /* Materials */
        public GuiButtonElement skin;
        public GuiButtonElement texture;
        public GuiStringListElement materials;
        public GuiTexturePicker picker;
        public GuiToggleElement keying;

        public GuiListElement<ShapeKey> shapes;
        public GuiTrackpadElement factor;
        public GuiToggleElement relative;

        public GuiMaterialsPanel(Minecraft mc, GuiCustomMorph editor)
        {
            super(mc, editor);

            Consumer<ResourceLocation> skin = (rl) ->
            {
                this.morph.skin = RLUtils.clone(rl);
                this.editor.updateModelRenderer();
            };

            Consumer<ResourceLocation> material = this::setCurrentMaterialRL;

            /* Materials view */
            this.skin = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.builder.pick_skin"), (b) ->
            {
                this.picker.refresh();
                this.picker.fill(this.morph.skin);
                this.picker.callback = skin;
                this.add(this.picker);
                this.picker.resize();
            });
            this.texture = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.builder.pick_texture"), (b) ->
            {
                ResourceLocation location = this.morph.materials.get(this.materials.getCurrentFirst());

                this.picker.refresh();
                this.picker.fill(location);
                this.picker.callback = material;
                this.add(this.picker);
                this.picker.resize();
            });
            this.materials = new GuiStringListElement(mc, (str) -> this.materials.setCurrent(str.get(0)));
            this.materials.background();
            this.keying = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.image.keying"), false, (b) -> this.morph.keying = b.isToggled());
            this.keying.tooltip(IKey.lang("blockbuster.gui.image.keying_tooltip"), Direction.TOP);
            this.picker = new GuiTexturePicker(mc, skin);

            this.shapes = new GuiShapeKeyListElement(mc, (str) -> this.setFactor(str.get(0)));
            this.shapes.sorting().background();
            this.shapes.context(() ->
            {
                GuiSimpleContextMenu menu = new GuiSimpleContextMenu(mc);

                menu.action(Icons.ADD, IKey.lang("blockbuster.gui.builder.context.add"), () ->
                {
                    GuiSimpleContextMenu nested = new GuiSimpleContextMenu(mc);

                    for (String key : this.morph.model.shapes)
                    {
                        nested.action(Icons.ADD, IKey.format("blockbuster.gui.builder.context.add_to", key), () ->
                        {
                            ShapeKey shapeKey = new ShapeKey(key, 0);

                            this.morph.getShapes().add(shapeKey);
                            this.shapes.update();
                            this.shapes.setCurrent(shapeKey);
                            this.setFactor(shapeKey);
                        });
                    }

                    GuiBase.getCurrent().replaceContextMenu(nested);
                });

                if (this.shapes.getIndex() != -1)
                {
                    menu.action(Icons.REMOVE, IKey.lang("blockbuster.gui.builder.context.remove"), () ->
                    {
                        int index = this.shapes.getIndex();

                        this.morph.getShapes().remove(index);
                        index = MathUtils.clamp(index, 0, this.shapes.getList().size() - 1);

                        this.shapes.setIndex(index);
                        this.setFactor(this.shapes.getCurrentFirst());
                    });
                }

                return menu;
            });
            this.factor = new GuiTrackpadElement(mc, (value) -> this.setFactor(value.floatValue()));
            this.factor.tooltip(IKey.lang("blockbuster.gui.builder.shape_keys_factor_tooltip"), Direction.TOP);

            this.relative = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.builder.relative"), (b) -> this.shapes.getCurrentFirst().relative = b.isToggled());
            this.relative.tooltip(IKey.lang("blockbuster.gui.builder.relative_tooltip"), Direction.TOP);

            this.skin.flex().relative(this).set(10, 10, 110, 20);
            this.texture.flex().relative(this.skin).set(0, 25, 110, 20);
            this.materials.flex().relative(this.texture).set(0, 25, 110, 0).hTo(this.keying.flex(), -5);
            this.keying.flex().relative(this).x(10).w(110).y(1F, -24);
            this.picker.flex().relative(this).wh(1F, 1F);

            this.shapes.flex().relative(this).x(1F, -120).y(22).w(110).hTo(this.factor.flex(), -17);
            this.factor.flex().relative(this.relative.flex()).y(-25).wh(110, 20);
            this.relative.flex().relative(this).x(1F, -120).y(1F, -10).w(110).anchorY(1F);

            this.add(this.skin, this.texture, this.keying, this.materials, this.relative, this.factor, this.shapes);
        }

        private void setFactor(ShapeKey key)
        {
            this.factor.setVisible(key != null);
            this.relative.setVisible(key != null);

            if (key != null)
            {
                this.factor.setValue(key.value);
                this.relative.toggled(key.relative);
            }
        }

        private void setFactor(float value)
        {
            this.shapes.getCurrentFirst().value = value;
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
            this.picker.removeFromParent();

            boolean noMaterials = this.materials.getList().isEmpty();

            if (!noMaterials)
            {
                this.materials.setIndex(0);
            }

            this.materials.setVisible(!noMaterials);
            this.texture.setVisible(!noMaterials);
            this.keying.toggled(morph.keying);

            this.shapes.setList(morph.getShapes());

            boolean hidden = morph.model.shapes.isEmpty();

            this.shapes.setVisible(!hidden);
            this.factor.setVisible(!hidden);
            this.relative.setVisible(!hidden);

            if (!hidden)
            {
                this.shapes.setIndex(0);
                this.setFactor(this.shapes.getCurrentFirst());
            }
        }

        @Override
        public void finishEditing()
        {
            this.picker.close();

            super.finishEditing();
        }

        @Override
        public void draw(GuiContext context)
        {
            if (this.materials.isVisible())
            {
                this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.obj_materials"), this.materials.area.x, this.materials.area.y - 12, 0xffffff);
            }

            if (this.shapes.isVisible())
            {
                this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.shape_keys"), this.shapes.area.x, this.shapes.area.y - 12, 0xffffff);
            }

            if (this.factor.isVisible())
            {
                this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.shape_keys_factor"), this.factor.area.x, this.factor.area.y - 12, 0xffffff);
            }

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
        protected void renderModel(EntityLivingBase dummy, float headYaw, float headPitch, int timer, int yaw, int pitch, float partialTicks, float factor)
        {
            super.renderModel(dummy, headYaw, headPitch, timer, yaw, pitch, partialTicks, factor);

            LayerBodyPart.renderBodyParts(dummy, this.morph, this.model, 0, 0, partialTicks, dummy.ticksExisted + partialTicks, headYaw, headPitch, factor);
        }
    }

    public static class GuiShapeKeyListElement extends GuiListElement<ShapeKey>
    {
        public GuiShapeKeyListElement(Minecraft mc, Consumer<List<ShapeKey>> callback)
        {
            super(mc, callback);

            this.scroll.scrollItemSize = 16;
        }

        protected String elementToString(ShapeKey element)
        {
            return element.name;
        }
    }
}