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
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.ModelUtils;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.parsing.ModelExtrudedLayer;
import mchorse.blockbuster_pack.client.gui.GuiPosePanel;
import mchorse.mclib.client.gui.mclib.GuiDashboard;
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

    private GuiPosePanel.GuiPoseTransformations poseEditor;
    private GuiModelLimbs limbs;

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

        this.add(this.modelRenderer, this.poseEditor, this.limbs);

        this.setModel("steve");
    }

    @Override
    public void open()
    {

    }

    public void setLimb(String str)
    {
        ModelLimb limb = this.model.limbs.get(str);

        if (limb != null)
        {
            this.limb = limb;
            this.transform = this.pose.limbs.get(str);

            this.modelRenderer.limb = limb;
            this.poseEditor.set(this.transform);
            this.limbs.fillLimbData(limb);
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
        this.renderModel.pose = this.model.getPose("standing");
        this.pose = this.renderModel.pose;

        ResourceLocation rl = this.model.defaultTexture;

        if (rl != null && rl.getResourcePath().isEmpty())
        {
            rl = null;
        }

        if (rl == null)
        {
            FolderEntry folder = ClientProxy.tree.getByPath(name + "/skins", null);

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

        if (rl == null)
        {
            rl = RLUtils.create("blockbuster", "textures/entity/actor.png");
        }

        this.modelRenderer.model = this.renderModel;
        this.modelRenderer.texture = rl;
        this.modelRenderer.limb = this.limb;
        this.modelRenderer.pose = this.pose;

        this.limbs.fillData(model);

        this.setLimb(this.model.limbs.keySet().iterator().next());
    }
}