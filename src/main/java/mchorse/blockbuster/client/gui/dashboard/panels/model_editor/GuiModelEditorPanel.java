package mchorse.blockbuster.client.gui.dashboard.panels.model_editor;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.api.loaders.lazy.IModelLazyLoader;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.dashboard.panels.GuiDashboardPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs.GuiModelLimbs;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs.GuiModelModels;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs.GuiModelOptions;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs.GuiModelPoses;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.ModelUtils;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.parsing.ModelExtrudedLayer;
import mchorse.blockbuster.utils.BBIcons;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTexturePicker;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import mchorse.mclib.utils.files.entries.AbstractEntry;
import mchorse.mclib.utils.files.entries.FolderEntry;
import mchorse.mclib.utils.resources.RLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;

public class GuiModelEditorPanel extends GuiDashboardPanel
{
    /* GUI stuff */
    public GuiBBModelRenderer modelRenderer;

    private GuiIconElement openModels;
    private GuiIconElement openPoses;
    private GuiIconElement openOptions;
    private GuiIconElement openLimbs;

    private GuiModelModels models;
    private GuiModelPoses poses;
    private GuiModelLimbs limbs;
    private GuiModelOptions options;

    private GuiIconElement swipe;
    private GuiIconElement running;
    private GuiIconElement items;
    private GuiToggleElement hitbox;
    private GuiToggleElement looking;
    private GuiButtonElement pickSkin;
    private GuiTexturePicker skinner;

    /* Limb props */

    /* Current data */
    public String modelName;
    public Model model;
    public ModelPose pose;
    public ModelLimb limb;

    public IModelLazyLoader modelEntry;
    public ModelCustom renderModel;
    public ResourceLocation renderTexture;

    public static <T> String getKey(T value, Map<String, T> map)
    {
        for (Map.Entry<String, T> entry : map.entrySet())
        {
            if (Objects.equals(value, entry.getValue()))
            {
                return entry.getKey();
            }
        }

        return null;
    }

    public GuiModelEditorPanel(Minecraft mc, GuiDashboard dashboard)
    {
        super(mc, dashboard);

        this.modelRenderer = new GuiBBModelRenderer(mc);
        this.modelRenderer.picker(this::setLimb);
        this.modelRenderer.flex().relative(this.area).w(1, 0).h(1, 0);
        this.add(this.modelRenderer);

        this.pickSkin = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.builder.pick_skin"), (b) ->
        {
            if (!this.skinner.hasParent())
            {
                this.skinner.fill(this.renderTexture);
            }

            this.skinner.resize();
            this.add(this.skinner);
        });
        this.skinner = new GuiTexturePicker(mc, (rl) ->
        {
            this.renderTexture = rl;
            this.modelRenderer.texture = this.renderTexture;
        });

        this.pickSkin.flex().set(0, 0, 70, 20).relative(this.area).x(1, -76).y(1, -23);
        this.skinner.flex().relative(this.area).w(1, 0).h(1, 0);
        this.add(this.pickSkin);

        this.models = new GuiModelModels(mc, this);
        this.models.flex().set(20, 0, 140, 0).h(1, -20).relative(this.area);
        this.models.setVisible(false);
        this.add(this.models);

        this.poses = new GuiModelPoses(mc, this);
        this.poses.setVisible(false);
        this.poses.flex().set(20, 0, 210, 150).relative(this.area);
        this.add(this.poses);

        this.limbs = new GuiModelLimbs(mc, this);
        this.limbs.setVisible(false);
        this.limbs.flex().set(0, 0, 240, 240).relative(this.area).x(1, -260);
        this.add(this.limbs);

        this.options = new GuiModelOptions(mc, this);
        this.options.setVisible(false);
        this.options.flex().set(0, 0, 140, 225 + 42).relative(this.area).x(1, -160);
        this.add(this.options);

        /* Top bar buttons */
        this.openModels = new GuiIconElement(mc, Icons.MORE, (b) -> this.toggle(this.models, this.poses));
        this.openModels.tooltip(IKey.lang("blockbuster.gui.me.tooltips.models"), Direction.RIGHT);
        this.openPoses = new GuiIconElement(mc, Icons.POSE, (b) -> this.toggle(this.poses, this.models));
        this.openPoses.tooltip(IKey.lang("blockbuster.gui.me.tooltips.poses"), Direction.RIGHT);
        this.openOptions = new GuiIconElement(mc, Icons.GEAR, (b) -> this.toggle(this.options, this.limbs));
        this.openOptions.tooltip(IKey.lang("blockbuster.gui.me.tooltips.options"), Direction.LEFT);
        this.openLimbs = new GuiIconElement(mc, Icons.LIMB, (b) -> this.toggle(this.limbs, this.options));
        this.openLimbs.tooltip(IKey.lang("blockbuster.gui.me.tooltips.limbs"), Direction.LEFT);

        this.openModels.flex().set(2, 2, 16, 16).relative(this.area);
        this.openPoses.flex().set(0, 20, 16, 16).relative(this.openModels.resizer());
        this.openOptions.flex().set(0, 2, 16, 16).relative(this.area).x(1, -18);
        this.openLimbs.flex().set(0, 22, 16, 16).relative(this.area).x(1, -18);

        this.add(this.openModels, this.openPoses, this.openOptions, this.openLimbs);

        /* Buttons */
        this.swipe = new GuiIconElement(mc, BBIcons.ARM1, (b) -> this.modelRenderer.swipe()).hovered(BBIcons.ARM2);
        this.swipe.tooltip(IKey.lang("blockbuster.gui.me.tooltips.models"), Direction.TOP);
        this.running = new GuiIconElement(mc, BBIcons.LEGS1, (b) -> this.modelRenderer.swinging = !this.modelRenderer.swinging).hovered(BBIcons.LEGS2);
        this.running.tooltip(IKey.lang("blockbuster.gui.me.tooltips.swing"), Direction.TOP);
        this.items = new GuiIconElement(mc, BBIcons.NO_ITEMS, (b) -> this.modelRenderer.toggleItems()).hovered(BBIcons.HELD_ITEMS);
        this.items.tooltip(IKey.lang("blockbuster.gui.me.tooltips.items"), Direction.TOP);
        this.hitbox = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.me.hitbox"), this.modelRenderer.aabb, (b) ->
        {
            this.modelRenderer.aabb = this.modelRenderer.origin = b.isToggled();
        });
        this.looking = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.me.looking"), this.modelRenderer.looking, (b) -> this.modelRenderer.looking = b.isToggled());

        this.swipe.flex().set(0, 0, 16, 16).relative(this.area).x(0.5F, -38).y(1, -18);
        this.running.flex().set(20, 0, 16, 16).relative(this.swipe.resizer());
        this.items.flex().set(20, 0, 16, 16).relative(this.running.resizer());
        this.hitbox.flex().set(6, 0, 40, 11).relative(this.area).y(1, -16);
        this.looking.flex().set(50, 0, 40, 11).relative(this.hitbox.resizer());

        this.add(this.swipe, this.running, this.items, this.hitbox, this.looking);

        this.setModel("steve");
    }

    @Override
    public void open()
    {
        this.models.updateModelList();
        this.models.modelList.setCurrent(this.modelName);
        this.models.export.setVisible(Minecraft.getMinecraft().world != null);

        if (this.models.modelList.isDeselected())
        {
            this.setModel("steve");
        }
    }

    @Override
    public void disappear()
    {
        this.skinner.setVisible(false);
    }

    private void toggle(GuiElement main, GuiElement secondary)
    {
        main.toggleVisible();

        if (main.isVisible())
        {
            secondary.setVisible(false);
        }
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

            mchorse.blockbuster.api.ModelHandler.ModelCell model = Blockbuster.proxy.models.models.get(name);
            IModelLazyLoader loader = Blockbuster.proxy.models.pack.models.get(this.modelName);

            if (model != null)
            {
                model.model.copy(this.model.clone());
            }

            /* Copy OBJ files */
            if (loader != null)
            {
                loader.copyFiles(folder);
            }

            Blockbuster.proxy.models.pack.reload();
            Blockbuster.proxy.models.addModel(name, loader, file.lastModified());
            this.modelName = name;
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
            this.modelRenderer.pose = oldPose;
        }
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

    public void setModel(String name)
    {
        ModelCustom model = ModelCustom.MODELS.get(name);

        if (model != null)
        {
            this.setModel(name, model.model, ClientProxy.actorPack.pack.models.get(name));
        }
    }

    public void setModel(String name, Model model, IModelLazyLoader loader)
    {
        this.modelName = name;
        this.model = model.clone();
        this.modelEntry = loader;

        this.renderModel = this.buildModel();
        this.renderModel.pose = this.model.getPose("standing");
        this.pose = this.renderModel.pose;
        this.renderTexture = this.model.defaultTexture;

        if (this.renderTexture != null && this.renderTexture.getResourcePath().isEmpty())
        {
            this.renderTexture = null;
        }

        if (this.renderTexture == null)
        {
            Blockbuster.proxy.models.pack.reload();

            FolderEntry folder = ClientProxy.tree.getByPath(name + "/skins", null);

            if (folder != null)
            {
                for (AbstractEntry file : folder.getEntries())
                {
                    if (file instanceof mchorse.mclib.utils.files.entries.FileEntry)
                    {
                        this.renderTexture = ((mchorse.mclib.utils.files.entries.FileEntry) file).resource;
                    }
                }
            }
        }

        if (this.renderTexture == null)
        {
            this.renderTexture = RLUtils.create("blockbuster", "textures/entity/actor.png");
        }

        this.modelRenderer.model = this.renderModel;
        this.modelRenderer.texture = this.renderTexture;
        this.modelRenderer.limb = this.limb;
        this.modelRenderer.pose = this.pose;

        this.limbs.fillData(this.model);
        this.options.fillData(this.model);
        this.poses.fillData(this.model);
        this.poses.setCurrent("standing");

        this.setLimb(this.model.limbs.keySet().iterator().next());
    }

    public void setLimb(String str)
    {
        ModelLimb limb = this.model.limbs.get(str);

        if (limb != null)
        {
            this.limb = limb;
            this.modelRenderer.limb = limb;

            this.limbs.setCurrent(str);
            this.poses.setLimb(str);
        }
    }

    public void setPose(String str)
    {
        ModelPose pose = this.model.poses.get(str);

        if (pose != null)
        {
            this.pose = pose;
            this.modelRenderer.pose = pose;

            this.renderModel.pose = pose;
            this.poses.setLimb(this.limb.name);
        }
    }

    @Override
    public void resize()
    {
        if (GuiBase.getCurrent().screen.height > 280)
        {
            this.poses.flex().w(130).h(1, -20);
            this.limbs.flex().x(1, -160).w(140).h(1, -20);
        }
        else
        {
            this.poses.flex().w(210).h(150);
            this.limbs.flex().x(1, -260).w(240).h(240);
        }

        super.resize();
    }

    @Override
    public void draw(GuiContext context)
    {
        GlStateManager.enableAlpha();
        this.drawGradientRect(this.area.x, this.area.ey() - 20, this.area.ex(), this.area.ey(), 0x00000000, 0x88000000);

        if (this.options.isVisible()) this.drawIconBackground(this.openOptions.area);
        if (this.limbs.isVisible()) this.drawIconBackground(this.openLimbs.area);
        if (this.poses.isVisible()) this.drawIconBackground(this.openPoses.area);
        if (this.models.isVisible()) this.drawIconBackground(this.openModels.area);

        super.draw(context);
    }

    private void drawIconBackground(Area area)
    {
        Gui.drawRect(area.x - 2, area.y - 2, area.x + 18, area.y + 18, 0x88000000);
    }
}