package mchorse.blockbuster.client.gui.dashboard.panels.model_editor;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelPack.ModelEntry;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.dashboard.panels.GuiDashboardPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs.GuiModelLimbs;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs.GuiModelModels;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs.GuiModelOptions;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs.GuiModelPoses;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.ModelUtils;
import mchorse.blockbuster.client.gui.framework.elements.GuiButtonElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.client.gui.utils.Area;
import mchorse.blockbuster.client.gui.utils.Resizer.Measure;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiTextureButton;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.parsing.ModelParser;
import mchorse.blockbuster.common.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiModelEditorPanel extends GuiDashboardPanel
{
    /* GUI stuff */
    public GuiModelRenderer modelRenderer;

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

    /* Limb props */

    /* Current data */
    public String modelName;
    public Model model;
    public ModelPose pose;
    public ModelLimb limb;

    public ModelCustom renderModel;
    public ResourceLocation renderTexture;

    public GuiModelEditorPanel(Minecraft mc)
    {
        super(mc);

        this.modelRenderer = new GuiModelRenderer(mc, this);
        this.modelRenderer.resizer().parent(this.area).w.set(1, Measure.RELATIVE);
        this.modelRenderer.resizer().h.set(1, Measure.RELATIVE);
        this.children.add(this.modelRenderer);

        this.models = new GuiModelModels(mc, this);
        this.models.resizer().set(20, 0, 120, 180).parent(this.area);
        this.models.setVisible(false);
        this.children.add(this.models);

        this.poses = new GuiModelPoses(mc, this);
        this.poses.setVisible(false);
        this.poses.resizer().set(20, 0, 210, 150).parent(this.area);
        this.children.add(this.poses);

        this.limbs = new GuiModelLimbs(mc, this);
        this.limbs.setVisible(false);
        this.limbs.resizer().set(0, 0, 240, 220).parent(this.area).x.set(1, Measure.RELATIVE, -260);
        this.children.add(this.limbs);

        this.options = new GuiModelOptions(mc, this);
        this.options.setVisible(false);
        this.options.resizer().set(0, 0, 140, 225).parent(this.area).x.set(1, Measure.RELATIVE, -160);
        this.children.add(this.options);

        /* Top bar buttons */
        this.openModels = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 96, 32, 96, 48, (b) -> this.toggle(this.models, this.poses));
        this.openPoses = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 80, 32, 80, 48, (b) -> this.toggle(this.poses, this.models));
        this.openOptions = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 48, 0, 48, 16, (b) -> this.toggle(this.options, this.limbs));
        this.openLimbs = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 128, 0, 128, 16, (b) -> this.toggle(this.limbs, this.options));

        this.openModels.resizer().set(2, 2, 16, 16).parent(this.area);
        this.openPoses.resizer().set(0, 20, 16, 16).relative(this.openModels.resizer());
        this.openOptions.resizer().set(0, 2, 16, 16).parent(this.area).x.set(1, Measure.RELATIVE, -18);
        this.openLimbs.resizer().set(0, 22, 16, 16).parent(this.area).x.set(1, Measure.RELATIVE, -18);

        this.children.add(this.openModels, this.openPoses, this.openOptions, this.openLimbs);

        /* Buttons */
        this.swipe = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 80, 0, 80, 16, (b) -> this.modelRenderer.swipe());
        this.running = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 96, 0, 96, 16, (b) -> this.modelRenderer.swinging = !this.modelRenderer.swinging);
        this.items = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 112, 0, 112, 16, (b) -> this.modelRenderer.toggleItems());
        this.hitbox = GuiButtonElement.checkbox(mc, "Hitbox", this.modelRenderer.aabb, (b) -> this.modelRenderer.aabb = b.button.isChecked());
        this.looking = GuiButtonElement.checkbox(mc, "Looking", this.modelRenderer.looking, (b) -> this.modelRenderer.looking = b.button.isChecked());

        this.swipe.resizer().set(0, 0, 16, 16).parent(this.area).x.set(0.5F, Measure.RELATIVE, -38);
        this.swipe.resizer().y.set(1, Measure.RELATIVE, -18);
        this.running.resizer().set(20, 0, 16, 16).relative(this.swipe.resizer());
        this.items.resizer().set(20, 0, 16, 16).relative(this.running.resizer());
        this.hitbox.resizer().set(6, 0, 40, 11).parent(this.area).y.set(1, Measure.RELATIVE, -16);
        this.looking.resizer().set(50, 0, 40, 11).relative(this.hitbox.resizer());

        this.children.add(this.swipe, this.running, this.items, this.hitbox, this.looking);

        this.setModel("steve");
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

        File folder = new File(ClientProxy.config, "models/" + name);
        File file = new File(folder, "model.json");
        String output = ModelUtils.toJson(this.model);

        boolean exists = folder.exists();

        folder.mkdirs();

        try
        {
            PrintWriter writer = new PrintWriter(file);

            writer.print(output);
            writer.close();

            String key = name;
            mchorse.blockbuster.api.ModelHandler.ModelCell model = Blockbuster.proxy.models.models.get(key);

            if (model != null)
            {
                ModelUtils.copy(this.model.clone(), model.model);
            }

            ModelCustom.MODELS.put(key, this.buildModel());
            this.modelName = name;

            if (!exists && this.model.defaultTexture == null)
            {
                /* TODO: warn? */

                return false;
            }
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

        this.renderModel = this.buildModel();

        if (this.model != null)
        {
            this.renderModel.pose = oldPose;
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
            File objModel = null;
            File mtlFile = null;
            ModelEntry entry = ClientProxy.actorPack.pack.models.get(this.modelName);

            if (entry != null)
            {
                objModel = entry.objModel;
                mtlFile = this.model.providesMtl ? entry.mtlFile : null;
            }

            return new ModelParser(this.modelName, objModel, mtlFile).parseModel(this.model, ModelCustom.class);
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

            Map<String, File> skins = Blockbuster.proxy.models.pack.skins.get(name);

            if (skins != null && !skins.isEmpty())
            {
                this.renderTexture = new ResourceLocation("blockbuster.actors", name + "/" + skins.keySet().iterator().next());
            }
        }

        if (this.renderTexture == null)
        {
            this.renderTexture = new ResourceLocation("blockbuster", "textures/entity/actor.png");
        }

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
            this.renderModel.pose = pose;
            this.poses.setLimb(this.limb.name);
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.enableAlpha();
        this.drawGradientRect(this.area.x, this.area.getY(1) - 20, this.area.getX(1), this.area.getY(1), 0x00000000, 0x88000000);

        if (this.options.isVisible()) this.drawIconBackground(this.openOptions.area);
        if (this.limbs.isVisible()) this.drawIconBackground(this.openLimbs.area);
        if (this.poses.isVisible()) this.drawIconBackground(this.openPoses.area);
        if (this.models.isVisible()) this.drawIconBackground(this.openModels.area);

        super.draw(mouseX, mouseY, partialTicks);
    }

    private void drawIconBackground(Area area)
    {
        Gui.drawRect(area.x - 2, area.y - 2, area.x + 18, area.y + 18, 0x88000000);
    }

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
}