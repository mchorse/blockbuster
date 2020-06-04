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
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs.GuiModelPoses;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.ModelUtils;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.parsing.ModelExtrudedLayer;
import mchorse.blockbuster_pack.client.gui.GuiPosePanel;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.mclib.GuiDashboard;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.utils.files.entries.AbstractEntry;
import mchorse.mclib.utils.files.entries.FolderEntry;
import mchorse.mclib.utils.resources.RLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;

public class GuiModelEditorPanel extends GuiBlockbusterPanel
{
    /* GUI stuff */
    public GuiBBModelRenderer modelRenderer;

    private GuiElement icons;
    private GuiIconElement openModels;
    private GuiIconElement openPoses;
    private GuiIconElement saveModel;

    private GuiPosePanel.GuiPoseTransformations poseEditor;
    private GuiModelLimbs limbs;
    private GuiModelPoses poses;
    private GuiModelList models;

    /* Current data */
    public String modelName;
    public Model model;
    public ModelPose pose;
    public ModelTransform transform;
    public ModelLimb limb;

    public IModelLazyLoader modelEntry;
    public ModelCustom renderModel;

    public GuiModelEditorPanel(Minecraft mc, GuiDashboard dashboard)
    {
        super(mc, dashboard);

        this.modelRenderer = new GuiBBModelRenderer(mc);
        this.modelRenderer.picker(this::setLimb);
        this.modelRenderer.flex().relative(this).wh(1F, 1F);

        this.poseEditor = new GuiPosePanel.GuiPoseTransformations(mc);
        this.poseEditor.flex().relative(this).set(0, 0, 190, 70).x(0.5F, -95).y(1, -80);

        this.limbs = new GuiModelLimbs(mc, this);
        this.limbs.flex().relative(this).x(1F).w(200).h(1F).anchorX(1F);

        this.poses = new GuiModelPoses(mc, this);
        this.poses.flex().relative(this).y(20).w(140).h(1F, -20);
        this.poses.setVisible(false);

        this.models = new GuiModelList(mc, this);
        this.models.flex().relative(this).y(20).wTo(this.limbs.area, -10).hTo(this.poseEditor.area).maxH(200);
        this.models.setVisible(false);

        this.openModels = new GuiIconElement(mc, Icons.MORE, (b) -> this.toggle(this.models));
        this.openPoses = new GuiIconElement(mc, Icons.POSE, (b) -> this.toggle(this.poses));
        this.saveModel = new GuiIconElement(mc, Icons.SAVED, (b) -> System.out.println("save..."));

        this.icons = new GuiElement(mc);
        this.icons.flex().relative(this).h(20).row(0).resize().height(20);
        this.icons.add(this.openModels, this.openPoses, this.saveModel);

        this.add(this.modelRenderer, this.poses, this.poseEditor, this.limbs, this.icons, this.models);
        this.setModel("steve");
    }

    private void toggle(GuiElement element)
    {
        boolean visible = element.isVisible();

        this.models.setVisible(false);
        this.poses.setVisible(false);

        element.setVisible(!visible);
    }

    @Override
    public void open()
    {
        this.models.updateModelList();
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
            this.modelRenderer.pose = pose;
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

            Model model = Blockbuster.proxy.models.models.get(name);
            IModelLazyLoader loader = Blockbuster.proxy.pack.models.get(this.modelName);

            if (model != null)
            {
                model.copy(this.model.clone());
            }

            /* Copy OBJ files */
            if (loader != null)
            {
                loader.copyFiles(folder);
            }

            Blockbuster.proxy.models.addModel(name, loader);
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
        this.modelName = name;
        this.model = model.clone();
        this.modelEntry = loader;

        this.renderModel = this.buildModel();
        this.modelRenderer.model = this.renderModel;
        this.modelRenderer.texture = this.getFirstResourceLocation();
        this.modelRenderer.limb = this.limb;
        this.modelRenderer.pose = this.pose;

        this.limbs.fillData(model);
        this.poses.fillData(model);
        this.models.fillData(model);

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

        super.draw(context);
    }
}