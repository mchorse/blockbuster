package mchorse.blockbuster.client.gui.dashboard.panels.model_editor;

import java.io.File;
import java.util.Map;
import java.util.Objects;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.loaders.lazy.IModelLazyLoader;
import mchorse.blockbuster.api.ModelHandler.ModelCell;
import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.dashboard.panels.GuiDashboardPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs.GuiModelLimbs;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs.GuiModelModels;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs.GuiModelOptions;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs.GuiModelPoses;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.ModelUtils;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.parsing.ModelExtrudedLayer;
import mchorse.blockbuster.client.model.parsing.ModelParser;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.GuiTooltip.TooltipDirection;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiTexturePicker;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.widgets.buttons.GuiTextureButton;
import mchorse.mclib.utils.files.entries.AbstractEntry;
import mchorse.mclib.utils.files.entries.FolderEntry;
import mchorse.mclib.utils.resources.RLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import org.apache.commons.io.FileUtils;

public class GuiModelEditorPanel extends GuiDashboardPanel
{
    /* GUI stuff */
    public GuiBBModelRenderer modelRenderer;

    private GuiButtonElement<GuiTextureButton> openModels;
    private GuiButtonElement<GuiTextureButton> openPoses;
    private GuiButtonElement<GuiTextureButton> openOptions;
    private GuiButtonElement<GuiTextureButton> openLimbs;

    private GuiModelModels models;
    private GuiModelPoses poses;
    private GuiModelLimbs limbs;
    private GuiModelOptions options;

    private GuiButtonElement<GuiTextureButton> swipe;
    private GuiButtonElement<GuiTextureButton> running;
    private GuiButtonElement<GuiTextureButton> items;
    private GuiButtonElement<GuiCheckBox> hitbox;
    private GuiButtonElement<GuiCheckBox> looking;
    private GuiButtonElement<GuiButton> pickSkin;
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
        this.modelRenderer.pickingCallback = (limb) -> this.setLimb(limb);
        this.modelRenderer.resizer().parent(this.area).w(1, 0).h(1, 0);
        this.children.add(this.modelRenderer);

        this.pickSkin = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.builder.pick_skin"), (b) ->
        {
            if (!this.skinner.isVisible())
            {
                this.skinner.fill(this.renderTexture);
            }

            this.skinner.toggleVisible();
        });
        this.skinner = new GuiTexturePicker(mc, (rl) ->
        {
            this.renderTexture = rl;
            this.modelRenderer.texture = this.renderTexture;
        });
        this.skinner.setVisible(false);

        this.pickSkin.resizer().set(0, 0, 70, 20).parent(this.area).x(1, -76).y(1, -23);
        this.skinner.resizer().parent(this.area).w(1, 0).h(1, 0);
        this.children.add(this.pickSkin);

        this.models = new GuiModelModels(mc, this);
        this.models.resizer().set(20, 0, 140, 0).h(1, -20).parent(this.area);
        this.models.setVisible(false);
        this.children.add(this.models);

        this.poses = new GuiModelPoses(mc, this);
        this.poses.setVisible(false);
        this.poses.resizer().set(20, 0, 210, 150).parent(this.area);
        this.children.add(this.poses);

        this.limbs = new GuiModelLimbs(mc, this);
        this.limbs.setVisible(false);
        this.limbs.resizer().set(0, 0, 240, 240).parent(this.area).x(1, -260);
        this.children.add(this.limbs);

        this.options = new GuiModelOptions(mc, this);
        this.options.setVisible(false);
        this.options.resizer().set(0, 0, 140, 225 + 42).parent(this.area).x(1, -160);
        this.children.add(this.options);

        /* Top bar buttons */
        this.openModels = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 96, 32, 96, 48, (b) -> this.toggle(this.models, this.poses)).tooltip(I18n.format("blockbuster.gui.me.tooltips.models"), TooltipDirection.RIGHT);
        this.openPoses = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 80, 32, 80, 48, (b) -> this.toggle(this.poses, this.models)).tooltip(I18n.format("blockbuster.gui.me.tooltips.poses"), TooltipDirection.RIGHT);
        this.openOptions = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 48, 0, 48, 16, (b) -> this.toggle(this.options, this.limbs)).tooltip(I18n.format("blockbuster.gui.me.tooltips.options"), TooltipDirection.LEFT);
        this.openLimbs = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 128, 0, 128, 16, (b) -> this.toggle(this.limbs, this.options)).tooltip(I18n.format("blockbuster.gui.me.tooltips.limbs"), TooltipDirection.LEFT);

        this.openModels.resizer().set(2, 2, 16, 16).parent(this.area);
        this.openPoses.resizer().set(0, 20, 16, 16).relative(this.openModels.resizer());
        this.openOptions.resizer().set(0, 2, 16, 16).parent(this.area).x(1, -18);
        this.openLimbs.resizer().set(0, 22, 16, 16).parent(this.area).x(1, -18);

        this.children.add(this.openModels, this.openPoses, this.openOptions, this.openLimbs);

        /* Buttons */
        this.swipe = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 80, 0, 80, 16, (b) -> this.modelRenderer.swipe()).tooltip(I18n.format("blockbuster.gui.me.tooltips.models"), TooltipDirection.TOP);
        this.running = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 96, 0, 96, 16, (b) -> this.modelRenderer.swinging = !this.modelRenderer.swinging).tooltip(I18n.format("blockbuster.gui.me.tooltips.swing"), TooltipDirection.TOP);
        this.items = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 112, 0, 112, 16, (b) -> this.modelRenderer.toggleItems()).tooltip(I18n.format("blockbuster.gui.me.tooltips.items"), TooltipDirection.TOP);
        this.hitbox = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.me.hitbox"), this.modelRenderer.aabb, (b) ->
        {
            this.modelRenderer.aabb = this.modelRenderer.origin = b.button.isChecked();
        });
        this.looking = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.me.looking"), this.modelRenderer.looking, (b) -> this.modelRenderer.looking = b.button.isChecked());

        this.swipe.resizer().set(0, 0, 16, 16).parent(this.area).x(0.5F, -38).y(1, -18);
        this.running.resizer().set(20, 0, 16, 16).relative(this.swipe.resizer());
        this.items.resizer().set(20, 0, 16, 16).relative(this.running.resizer());
        this.hitbox.resizer().set(6, 0, 40, 11).parent(this.area).y(1, -16);
        this.looking.resizer().set(50, 0, 40, 11).relative(this.hitbox.resizer());

        this.children.add(this.swipe, this.running, this.items, this.hitbox, this.looking, this.skinner);

        this.setModel("steve");
    }

    @Override
    public void open()
    {
        this.models.updateModelList();
        this.models.modelList.setCurrent(this.modelName);
        this.models.export.setVisible(Minecraft.getMinecraft().theWorld != null);

        if (this.models.modelList.current == -1)
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
            FileUtils.write(file, output);

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
            this.setModel(name, model.model);
        }
    }

    public void setModel(String name, Model model)
    {
        this.modelName = name;
        this.model = model.clone();
        this.modelEntry = ClientProxy.actorPack.pack.models.get(this.modelName);

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
    public void resize(int width, int height)
    {
        if (height > 280)
        {
            this.poses.resizer().w(130).h(1, -20);
            this.limbs.resizer().x(1, -160).w(140).h(1, -20);
        }
        else
        {
            this.poses.resizer().w(210).h(150);
            this.limbs.resizer().x(1, -260).w(240).h(240);
        }

        super.resize(width, height);
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.enableAlpha();
        this.drawGradientRect(this.area.x, this.area.getY(1) - 20, this.area.getX(1), this.area.getY(1), 0x00000000, 0x88000000);

        if (this.options.isVisible()) this.drawIconBackground(this.openOptions.area);
        if (this.limbs.isVisible()) this.drawIconBackground(this.openLimbs.area);
        if (this.poses.isVisible()) this.drawIconBackground(this.openPoses.area);
        if (this.models.isVisible()) this.drawIconBackground(this.openModels.area);

        super.draw(tooltip, mouseX, mouseY, partialTicks);
    }

    private void drawIconBackground(Area area)
    {
        Gui.drawRect(area.x - 2, area.y - 2, area.x + 18, area.y + 18, 0x88000000);
    }
}