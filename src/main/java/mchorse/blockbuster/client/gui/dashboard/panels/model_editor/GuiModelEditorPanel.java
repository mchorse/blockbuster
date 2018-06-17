package mchorse.blockbuster.client.gui.dashboard.panels.model_editor;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.Model.Limb;
import mchorse.blockbuster.api.Model.Pose;
import mchorse.blockbuster.api.ModelPack.ModelEntry;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.dashboard.panels.GuiDashboardPanel;
import mchorse.blockbuster.client.gui.framework.elements.GuiButtonElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiElements;
import mchorse.blockbuster.client.gui.framework.elements.GuiTextElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.blockbuster.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.blockbuster.client.gui.utils.Resizer.Measure;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiTextureButton;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.parsing.ModelParser;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.model_editor.ModelUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiModelEditorPanel extends GuiDashboardPanel
{
    /* GUI stuff */
    private GuiModelRenderer modelRenderer;

    private GuiButtonElement<GuiTextureButton> openModel;
    private GuiButtonElement<GuiTextureButton> saveModel;
    private GuiButtonElement<GuiTextureButton> openPoses;
    private GuiButtonElement<GuiTextureButton> openOptions;

    private GuiStringListElement modelList;
    private GuiStringListElement posesList;
    private GuiStringListElement limbList;

    private GuiElements options;
    private GuiTextElement name;
    private GuiTwoElement texture;
    private GuiThreeElement scale;
    private GuiTrackpadElement scaleGui;
    private GuiTextElement defaultTexture;
    private GuiButtonElement<GuiCheckBox> providesObj;
    private GuiButtonElement<GuiCheckBox> providesMtl;

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

        this.limbList = new GuiStringListElement(mc, (str) -> this.setLimb(str));
        this.limbList.resizer().set(0, 20, 80, 0).parent(this.area).h.set(1, Measure.RELATIVE);
        this.limbList.resizer().x.set(1, Measure.RELATIVE, -80);
        this.children.add(this.limbList);

        this.posesList = new GuiStringListElement(mc, (str) -> this.setPose(str));
        this.posesList.resizer().set(100, 20, 80, 0).parent(this.area).h.set(1, Measure.RELATIVE);
        this.posesList.setVisible(false);
        this.children.add(this.posesList);

        /* Model options */
        this.options = new GuiElements();
        this.options.setVisible(false);
        this.name = new GuiTextElement(mc, (str) -> this.model.name = str);
        this.texture = new GuiTwoElement(mc, (value) ->
        {
            this.model.texture[0] = value[0].intValue();
            this.model.texture[1] = value[1].intValue();
        });
        this.texture.setLimit(1, 8196);
        this.scale = new GuiThreeElement(mc, (value) ->
        {
            this.model.scale[0] = value[0];
            this.model.scale[1] = value[1];
            this.model.scale[2] = value[2];
        });
        this.scaleGui = new GuiTrackpadElement(mc, "GUI scale", (value) -> this.model.scaleGui = value);
        this.defaultTexture = new GuiTextElement(mc, (str) -> this.model.defaultTexture = new ResourceLocation(str));
        this.providesObj = GuiButtonElement.checkbox(mc, "Provides OBJ", false, (b) -> this.model.providesObj = b.button.isChecked());
        this.providesMtl = GuiButtonElement.checkbox(mc, "Provides MTL", false, (b) -> this.model.providesMtl = b.button.isChecked());

        this.name.resizer().parent(this.area).set(0, 30, 100, 20).x.set(1, Measure.RELATIVE, -110);
        this.texture.resizer().set(0, 25, 100, 20).relative(this.name.resizer());
        this.scale.resizer().set(0, 25, 100, 20).relative(this.texture.resizer());
        this.scaleGui.resizer().set(0, 25, 100, 20).relative(this.scale.resizer());
        this.defaultTexture.resizer().set(0, 25, 100, 20).relative(this.scaleGui.resizer());
        this.providesObj.resizer().set(0, 25, 80, 11).relative(this.defaultTexture.resizer());
        this.providesMtl.resizer().set(0, 16, 80, 11).relative(this.providesObj.resizer());

        this.options.add(this.name, this.texture, this.scale, this.scaleGui, this.defaultTexture, this.providesObj, this.providesMtl);
        this.children.add(this.options);

        /* Top bar buttons */

        this.openModel = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 96, 32, 96, 48, (b) -> this.toggleModels());
        this.saveModel = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 112, 32, 112, 48, (b) -> this.saveCurrentModel());
        this.openPoses = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 80, 32, 80, 48, (b) -> this.togglePoses());
        this.openOptions = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 48, 0, 48, 16, (b) -> this.toggleOptions());

        this.openModel.resizer().set(2, 2, 16, 16).parent(this.area);
        this.saveModel.resizer().set(20, 0, 16, 16).relative(this.openModel.resizer());
        this.openPoses.resizer().set(20, 0, 16, 16).relative(this.saveModel.resizer());
        this.openOptions.resizer().set(0, 2, 16, 16).parent(this.area).x.set(1, Measure.RELATIVE, -18);

        this.children.add(this.openModel, this.saveModel, this.openPoses, this.openOptions);

        this.setModel("steve");
    }

    private void toggleModels()
    {
        this.modelList.setVisible(!this.modelList.isVisible());

        if (this.modelList.isVisible())
        {
            this.posesList.setVisible(false);
        }
    }

    public void saveCurrentModel()
    {
        this.saveModel(this.modelName);
    }

    private void togglePoses()
    {
        this.posesList.setVisible(!this.posesList.isVisible());

        if (this.posesList.isVisible())
        {
            this.modelList.setVisible(false);
        }
    }

    private void toggleOptions()
    {
        this.options.setVisible(!this.options.isVisible());
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

            this.limbList.clear();
            this.limbList.add(this.model.limbs.keySet());

            this.posesList.clear();
            this.posesList.add(this.model.poses.keySet());
        }
    }

    public void setLimb(String str)
    {
        Limb limb = this.model.limbs.get(str);

        if (limb != null)
        {
            this.limb = limb;
        }
    }

    public void setPose(String str)
    {
        Pose pose = this.model.poses.get(str);

        if (pose != null)
        {
            this.pose = pose;
            this.renderModel.pose = pose;
        }
    }
}