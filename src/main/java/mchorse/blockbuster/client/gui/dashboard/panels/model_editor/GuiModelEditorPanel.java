package mchorse.blockbuster.client.gui.dashboard.panels.model_editor;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.api.loaders.lazy.IModelLazyLoader;
import mchorse.blockbuster.client.gui.dashboard.GuiBlockbusterPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs.GuiModelLimbs;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs.GuiModelList;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs.GuiModelOptions;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs.GuiModelPoses;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiBBModelRenderer;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.ModelUtils;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.parsing.ModelExtrudedLayer;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiPoseTransformations;
import mchorse.blockbuster.utils.mclib.BBIcons;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTexturePicker;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.mclib.GuiDashboard;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.DummyEntity;
import mchorse.mclib.utils.files.entries.AbstractEntry;
import mchorse.mclib.utils.files.entries.FolderEntry;
import mchorse.mclib.utils.resources.RLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.nio.charset.Charset;
import java.util.function.Consumer;

public class GuiModelEditorPanel extends GuiBlockbusterPanel
{
    /* GUI stuff */
    public GuiBBModelRenderer modelRenderer;

    private GuiElement icons;
    private GuiIconElement openModels;
    private GuiIconElement openOptions;
    private GuiIconElement openPoses;
    private GuiIconElement saveModel;
    private GuiIconElement swipe;
    private GuiIconElement running;
    private GuiIconElement items;
    private GuiIconElement hitbox;
    private GuiIconElement looking;
    private GuiIconElement skin;

    private GuiPoseTransformations poseEditor;
    private GuiModelLimbs limbs;
    private GuiModelPoses poses;
    private GuiModelList models;
    private GuiModelOptions options;

    private GuiTexturePicker picker;

    /* Current data */
    public String modelName;
    public Model model;
    public ModelPose pose;
    public ModelTransform transform;
    public ModelLimb limb;

    public IModelLazyLoader modelEntry;
    public ModelCustom renderModel;

    private boolean dirty;
    private boolean held;

    public GuiModelEditorPanel(Minecraft mc, GuiDashboard dashboard)
    {
        super(mc, dashboard);

        this.modelRenderer = new GuiBBModelRenderer(mc);
        this.modelRenderer.picker(this::setLimb);
        this.modelRenderer.flex().relative(this).wh(1F, 1F);
        this.modelRenderer.origin = this.modelRenderer.items = true;

        this.picker = new GuiTexturePicker(mc, null);
        this.picker.flex().relative(this).wh(1F, 1F);

        this.poseEditor = new GuiModelPoseTransformations(mc, this);
        this.poseEditor.flex().relative(this).set(0, 0, 190, 70).x(0.5F, -95).y(1, -80);

        this.limbs = new GuiModelLimbs(mc, this);
        this.limbs.flex().relative(this).x(1F).w(200).h(1F).anchorX(1F);

        this.poses = new GuiModelPoses(mc, this);
        this.poses.flex().relative(this).y(20).w(140).h(1F, -20);
        this.poses.setVisible(false);

        this.models = new GuiModelList(mc, this);
        this.models.flex().relative(this).y(20).w(140).h(1F, -20);
        this.models.setVisible(false);

        this.options = new GuiModelOptions(mc, this);
        this.options.flex().relative(this).y(20).w(200).h(1F, -20);
        this.options.setVisible(false);

        /* Toolbar buttons */
        this.openModels = new GuiIconElement(mc, Icons.MORE, (b) -> this.toggle(this.models));
        this.openOptions = new GuiIconElement(mc, Icons.GEAR, (b) -> this.toggle(this.options));
        this.openPoses = new GuiIconElement(mc, Icons.POSE, (b) -> this.toggle(this.poses));

        this.saveModel = new GuiIconElement(mc, Icons.SAVED, (b) -> this.saveModel());
        this.saveModel.tooltip(IKey.lang("blockbuster.gui.me.tooltips.save"));

        this.swipe = new GuiIconElement(mc, BBIcons.ARM1, (b) -> this.modelRenderer.swipe());
        this.swipe.tooltip(IKey.lang("blockbuster.gui.me.tooltips.swipe"));
        this.swipe.hovered(BBIcons.ARM2);

        this.running = new GuiIconElement(mc, BBIcons.LEGS1, (b) -> this.modelRenderer.swinging = !this.modelRenderer.swinging);
        this.running.hovered(BBIcons.LEGS2).hoverColor(0xffffffff).tooltip(IKey.lang("blockbuster.gui.me.tooltips.running"));

        this.items = new GuiIconElement(mc, BBIcons.NO_ITEMS, (b) ->
        {
            this.held = !this.held;
            ((DummyEntity) this.modelRenderer.getEntity()).toggleItems(this.held);
        });
        this.items.hovered(BBIcons.HELD_ITEMS).tooltip(IKey.lang("blockbuster.gui.me.tooltips.held_items"));

        this.hitbox = new GuiIconElement(mc, BBIcons.HITBOX, (b) -> this.modelRenderer.aabb = !this.modelRenderer.aabb);
        this.hitbox.tooltip(IKey.lang("blockbuster.gui.me.tooltips.hitbox"));

        this.looking = new GuiIconElement(mc, BBIcons.LOOKING, (b) -> this.modelRenderer.looking = !this.modelRenderer.looking);
        this.looking.tooltip(IKey.lang("blockbuster.gui.me.tooltips.looking"));

        this.skin = new GuiIconElement(mc, Icons.MATERIAL, (b) -> this.pickTexture(this.modelRenderer.texture, (rl) -> this.modelRenderer.texture = rl));
        this.skin.tooltip(IKey.lang("blockbuster.gui.me.tooltips.skin"));

        this.icons = new GuiElement(mc);
        this.icons.flex().relative(this).h(20).row(0).resize().height(20);
        this.icons.add(this.openModels, this.openOptions, this.openPoses, this.saveModel);

        GuiElement icons = new GuiElement(mc);
        icons.flex().relative(this.icons).x(1F, 20).h(20).row(0).resize().height(20);
        icons.add(this.swipe, this.running, this.items, this.hitbox, this.looking, this.skin);

        this.add(this.modelRenderer, this.poses, this.poseEditor, this.limbs, this.models, this.options, this.icons, icons);

        this.keys()
                .register(IKey.lang("blockbuster.gui.me.keys.save"), Keyboard.KEY_S, () -> this.saveModel.clickItself(GuiBase.getCurrent()))
                .held(Keyboard.KEY_LCONTROL).category(IKey.lang("blockbuster.gui.me.keys.category"));

        this.setModel("steve");
    }

    private void toggle(GuiElement element)
    {
        boolean visible = element.isVisible();

        this.models.setVisible(false);
        this.poses.setVisible(false);
        this.options.setVisible(false);

        element.setVisible(!visible);
    }

    public void dirty()
    {
        this.dirty(true);
    }

    public void dirty(boolean dirty)
    {
        this.dirty = dirty;
        this.updateSaveButton();
    }

    private void updateSaveButton()
    {
        this.saveModel.both(this.dirty ? Icons.SAVE : Icons.SAVED);
    }

    @Override
    public void open()
    {
        this.models.updateModelList();
    }

    public void pickTexture(ResourceLocation location, Consumer<ResourceLocation> callback)
    {
        this.picker.fill(location);
        this.picker.callback = callback;

        this.picker.resize();
        this.add(this.picker);
    }

    public void setLimb(String str)
    {
        ModelLimb limb = this.model.limbs.get(str);

        if (limb != null)
        {
            this.limb = limb;

            if (this.pose != null)
            {
                this.transform = this.pose.limbs.get(str);
            }

            this.modelRenderer.limb = limb;
            this.poseEditor.set(this.transform);
            this.limbs.fillLimbData(limb);
            this.limbs.setCurrent(str);
        }
    }

    public void setPose(String str)
    {
        ModelPose pose = this.model.poses.get(str);

        if (pose != null)
        {
            this.pose = pose;
            this.modelRenderer.setPose(pose);
            this.renderModel.pose = pose;

            if (this.limb != null)
            {
                this.transform = pose.limbs.get(this.limb.name);
            }

            this.poses.setCurrent(str);
            this.poses.fillPoseData();
            this.poseEditor.set(this.transform);
        }
    }

    public void saveModel()
    {
        this.saveModel(this.modelName);
    }

    /**
     * Save model
     *
     * This method is responsible for saving model into users's config folder.
     */
    public boolean saveModel(String name)
    {
        if (name.isEmpty())
        {
            return false;
        }

        File folder = new File(CommonProxy.configFile, "models/" + name);
        File file = new File(folder, "model.json");
        String output = ModelUtils.toJson(this.model);

        folder.mkdirs();

        try
        {
            FileUtils.write(file, output, Charset.defaultCharset());

            IModelLazyLoader previous = Blockbuster.proxy.pack.models.get(this.modelName);

            /* Copy OBJ files */
            if (previous != null)
            {
                previous.copyFiles(folder);
            }

            this.modelName = name;
            Blockbuster.proxy.loadModels(false);

            this.dirty(false);
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    /**
     * Build the model from data model
     */
    public void rebuildModel()
    {
        ModelPose oldPose = this.renderModel.pose;

        this.renderModel.delete();
        this.renderModel = this.buildModel();
        this.modelRenderer.model = this.renderModel;

        if (this.model != null)
        {
            this.renderModel.pose = oldPose;
            this.modelRenderer.setPose(oldPose);
        }

        this.dirty();
    }

    /**
     * Build the model from data model
     * 
     * TODO: optimize by rebuilding only one limb
     */
    public ModelCustom buildModel()
    {
        try
        {
            ModelExtrudedLayer.clearByModel(this.renderModel);

            return this.modelEntry.loadClientModel(this.modelName, this.model);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Set a model from the repository
     */
    public void setModel(String name)
    {
        ModelCustom model = ModelCustom.MODELS.get(name);

        if (model != null)
        {
            this.setModel(name, model.model, Blockbuster.proxy.pack.models.get(name));
        }
    }

    public void setModel(String name, Model model, IModelLazyLoader loader)
    {
        this.dirty(false);

        this.modelName = name;
        this.model = model.copy();
        this.modelEntry = loader;

        this.renderModel = this.buildModel();
        this.modelRenderer.model = this.renderModel;
        this.modelRenderer.texture = this.getFirstResourceLocation();
        this.modelRenderer.limb = this.limb;
        this.modelRenderer.setPose(this.pose);

        this.limbs.fillData(model);
        this.poses.fillData(model);
        this.options.fillData(model);

        this.setPose("standing");
        this.setLimb(this.model.limbs.keySet().iterator().next());
    }

    /**
     * Get the first available resource location for this model
     */
    private ResourceLocation getFirstResourceLocation()
    {
        ResourceLocation rl = this.model.defaultTexture;

        if (rl != null && rl.getResourcePath().isEmpty())
        {
            rl = null;
        }

        if (rl == null)
        {
            FolderEntry folder = ClientProxy.tree.getByPath(this.modelName + "/skins", null);

            if (folder != null)
            {
                for (AbstractEntry file : folder.getEntries())
                {
                    if (file instanceof mchorse.mclib.utils.files.entries.FileEntry)
                    {
                        rl = ((mchorse.mclib.utils.files.entries.FileEntry) file).resource;
                    }
                }
            }
        }

        return rl == null ? RLUtils.create("blockbuster", "textures/entity/actor.png") : rl;
    }

    @Override
    public void draw(GuiContext context)
    {
        if (this.models.isVisible())
        {
            this.openModels.area.draw(0xaa000000);
        }
        else if (this.poses.isVisible())
        {
            this.openPoses.area.draw(0xaa000000);
        }
        else if (this.options.isVisible())
        {
            this.openOptions.area.draw(0xaa000000);
        }

        if (this.modelRenderer.swinging)
        {
            this.running.area.draw(0x66000000);
        }

        if (this.held)
        {
            this.items.area.draw(0x66000000);
        }

        if (this.modelRenderer.aabb)
        {
            this.hitbox.area.draw(0x66000000);
        }

        if (this.modelRenderer.looking)
        {
            this.looking.area.draw(0x66000000);
        }

        super.draw(context);
    }

    public static class GuiModelPoseTransformations extends GuiPoseTransformations
    {
        public GuiModelEditorPanel panel;

        public GuiModelPoseTransformations(Minecraft mc, GuiModelEditorPanel panel)
        {
            super(mc);

            this.panel = panel;
        }

        @Override
        public void setT(double x, double y, double z)
        {
            super.setT(x, y, z);
            this.panel.dirty();
        }

        @Override
        public void setS(double x, double y, double z)
        {
            super.setS(x, y, z);
            this.panel.dirty();
        }

        @Override
        public void setR(double x, double y, double z)
        {
            super.setR(x, y, z);
            this.panel.dirty();
        }
    }
}