package mchorse.blockbuster.client.gui.dashboard.panels.model_editor;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.Model.Limb;
import mchorse.blockbuster.api.Model.Pose;
import mchorse.blockbuster.api.ModelPack.ModelEntry;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.dashboard.panels.GuiDashboardPanel;
import mchorse.blockbuster.client.gui.framework.elements.GuiButtonElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.blockbuster.client.gui.utils.Resizer.Measure;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiTextureButton;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.parsing.ModelParser;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.model_editor.ModelUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class GuiModelEditorPanel extends GuiDashboardPanel
{
    /* GUI stuff */
    private GuiModelRenderer modelRenderer;

    private GuiButtonElement<GuiTextureButton> openModel;
    private GuiButtonElement<GuiTextureButton> saveModel;
    private GuiButtonElement<GuiTextureButton> openPoses;
    private GuiButtonElement<GuiTextureButton> openOptions;
    private GuiButtonElement<GuiTextureButton> openLimbs;

    private GuiStringListElement modelList;
    private GuiModelPoses poses;
    private GuiModelLimbs limbs;

    /* Model options */
    private GuiModelOptions options;

    /* Limb props */

    /* Current data */
    public String modelName;
    public Model model;
    public Pose pose;
    public Limb limb;

    public ModelCustom renderModel;
    public ResourceLocation renderTexture;

    public GuiModelEditorPanel(Minecraft mc)
    {
        super(mc);

        this.modelRenderer = new GuiModelRenderer(mc, this);
        this.modelRenderer.resizer().parent(this.area).w.set(1, Measure.RELATIVE);
        this.modelRenderer.resizer().h.set(1, Measure.RELATIVE);
        this.children.add(this.modelRenderer);

        this.modelList = new GuiStringListElement(mc, (str) -> this.setModel(str));
        this.modelList.resizer().set(0, 20, 80, 0).parent(this.area).h.set(1, Measure.RELATIVE, -20);
        this.modelList.add(ModelCustom.MODELS.keySet());
        this.modelList.setVisible(false);
        this.children.add(this.modelList);

        /* Popups */
        this.limbs = new GuiModelLimbs(mc, this);
        this.limbs.resizer().set(0, 0, 220, 258).parent(this.area).x.set(1, Measure.RELATIVE, -240);
        this.limbs.setVisible(false);
        this.children.add(this.limbs);

        this.poses = new GuiModelPoses(mc, this);
        this.poses.setVisible(false);
        this.poses.resizer().set(0, 20, 220, 140).parent(this.area);
        this.children.add(this.poses);

        this.options = new GuiModelOptions(mc, this);
        this.options.setVisible(false);
        this.options.resizer().set(0, 0, 140, 225).parent(this.area).x.set(1, Measure.RELATIVE, -160);
        this.children.add(this.options);

        /* Top bar buttons */
        this.openModel = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 96, 32, 96, 48, (b) -> this.toggle(this.modelList, this.poses));
        this.saveModel = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 112, 32, 112, 48, (b) -> this.saveCurrentModel());
        this.openPoses = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 80, 32, 80, 48, (b) -> this.toggle(this.poses, this.modelList));
        this.openOptions = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 48, 0, 48, 16, (b) -> this.toggle(this.options, this.limbs));
        this.openLimbs = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 128, 0, 128, 16, (b) -> this.toggle(this.limbs, this.options));

        this.openModel.resizer().set(2, 2, 16, 16).parent(this.area);
        this.saveModel.resizer().set(20, 0, 16, 16).relative(this.openModel.resizer());
        this.openPoses.resizer().set(20, 0, 16, 16).relative(this.saveModel.resizer());
        this.openOptions.resizer().set(0, 2, 16, 16).parent(this.area).x.set(1, Measure.RELATIVE, -18);
        this.openLimbs.resizer().set(0, 22, 16, 16).parent(this.area).x.set(1, Measure.RELATIVE, -18);

        this.children.add(this.openModel, this.saveModel, this.openPoses, this.openOptions, this.openLimbs);

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

    public void saveCurrentModel()
    {
        this.saveModel(this.modelName);
    }

    /**
     * Save model
     *
     * This method is responsible for saving model into users's config folder.
     */
    private boolean saveModel(String name)
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
        Model.Pose oldPose = this.renderModel.pose;

        this.renderModel = this.buildModel();

        if (this.model != null)
        {
            this.renderModel.pose = oldPose;
        }
    }

    /**
     * Build the model from data model
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
            this.modelName = name;
            this.model = model.model.clone();
            this.renderModel = this.buildModel();
            this.renderModel.pose = this.model.getPose("standing");
            this.pose = this.renderModel.pose;
            this.renderTexture = this.model.defaultTexture;

            if (this.renderTexture == null)
            {
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
    }

    public void setLimb(String str)
    {
        Limb limb = this.model.limbs.get(str);

        if (limb != null)
        {
            this.limb = limb;
            this.limbs.setCurrent(str);
            this.poses.setLimb(str);
        }
    }

    public void setPose(String str)
    {
        Pose pose = this.model.poses.get(str);

        if (pose != null)
        {
            this.pose = pose;
            this.renderModel.pose = pose;
            this.poses.setLimb(this.getKey(this.limb, this.model.limbs));
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        if (this.options.isVisible())
        {
            int x = this.area.getX(1) - 20;

            Gui.drawRect(x, 0, x + 20, 20, 0x88000000);
        }

        if (this.limbs.isVisible())
        {
            int x = this.area.getX(1) - 20;

            Gui.drawRect(x, 20, x + 20, 40, 0x88000000);
        }

        if (this.poses.isVisible())
        {
            int x = this.openPoses.area.x - 2;

            Gui.drawRect(x, 0, x + 20, 20, 0x88000000);
        }

        super.draw(mouseX, mouseY, partialTicks);
    }

    private <T> String getKey(T value, Map<String, T> map)
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