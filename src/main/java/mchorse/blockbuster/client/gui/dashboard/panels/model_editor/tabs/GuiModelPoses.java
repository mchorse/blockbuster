package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelEditorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.modals.GuiMessageModal;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.modals.GuiPromptModal;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiThreeElement;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiTwoElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiButtonElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiDelegateElement;
import mchorse.blockbuster.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.blockbuster.client.gui.utils.Resizer.Measure;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiTextureButton;
import net.minecraft.client.Minecraft;

public class GuiModelPoses extends GuiModelEditorTab
{
    private GuiButtonElement<GuiTextureButton> addPose;
    private GuiButtonElement<GuiTextureButton> removePose;
    private GuiDelegateElement modal;

    private GuiStringListElement posesList;
    private GuiThreeElement translate;
    private GuiThreeElement scale;
    private GuiThreeElement rotation;
    private GuiTwoElement hitbox;

    private ModelTransform transform;

    public GuiModelPoses(Minecraft mc, GuiModelEditorPanel panel)
    {
        super(mc, panel);

        this.title = "Poses";

        this.posesList = new GuiStringListElement(mc, (str) -> this.setPose(str));
        this.posesList.resizer().set(0, 20, 80, 0).parent(this.area).h.set(1, Measure.RELATIVE, -20);
        this.posesList.resizer().x.set(1, Measure.RELATIVE, -80);
        this.children.add(this.posesList);

        this.translate = new GuiThreeElement(mc, (values) ->
        {
            this.transform.translate[0] = values[0];
            this.transform.translate[1] = values[1];
            this.transform.translate[2] = values[2];
        });
        this.scale = new GuiThreeElement(mc, (values) ->
        {
            this.transform.scale[0] = values[0];
            this.transform.scale[1] = values[1];
            this.transform.scale[2] = values[2];
        });
        this.rotation = new GuiThreeElement(mc, (values) ->
        {
            this.transform.rotate[0] = values[0];
            this.transform.rotate[1] = values[1];
            this.transform.rotate[2] = values[2];
        });

        this.hitbox = new GuiTwoElement(mc, (values) ->
        {
            this.panel.pose.size[0] = values[0];
            this.panel.pose.size[1] = values[1];
        });

        this.translate.resizer().set(10, 30, 110, 20).parent(this.area);
        this.scale.resizer().set(0, 25, 110, 20).relative(this.translate.resizer());
        this.rotation.resizer().set(0, 25, 110, 20).relative(this.scale.resizer());

        this.hitbox.resizer().set(0, 40, 110, 20).relative(this.rotation.resizer());
        this.children.add(this.translate, this.scale, this.rotation, this.hitbox);

        /* Buttons */
        this.addPose = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 32, 32, 32, 48, (b) -> this.addPose());
        this.removePose = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 64, 32, 64, 48, (b) -> this.removePose());

        this.addPose.resizer().set(2, 2, 16, 16).parent(this.area);
        this.addPose.resizer().x.set(1, Measure.RELATIVE, -38);
        this.removePose.resizer().set(20, 0, 16, 16).relative(this.addPose.resizer());
        this.children.add(this.addPose, this.removePose);

        this.modal = new GuiDelegateElement(mc, null);
        this.modal.resizer().set(0, 0, 1, 1, Measure.RELATIVE).parent(this.area);
        this.children.add(this.modal);
    }

    private void addPose()
    {
        String key = GuiModelEditorPanel.getKey(this.panel.pose, this.panel.model.poses);

        this.modal.setDelegate(new GuiPromptModal(mc, this.modal, "Type a new name for a new pose:", (text) -> this.addPose(text)).setValue(key));
    }

    private void addPose(String text)
    {
        ModelPose pose = this.panel.pose.clone();

        this.panel.model.poses.put(text, pose);
        this.posesList.add(text);
        this.posesList.setCurrent(text);
    }

    private void removePose()
    {
        String pose = GuiModelEditorPanel.getKey(this.panel.pose, this.panel.model.poses);

        if (Model.REQUIRED_POSES.contains(pose))
        {
            this.modal.setDelegate(new GuiMessageModal(this.mc, this.modal, "You cannot remove one of the standard poses..."));
        }
        else
        {
            this.panel.model.poses.remove(pose);

            String newPose = this.panel.model.poses.keySet().iterator().next();

            this.setPose(newPose);
            this.posesList.remove(pose);
            this.posesList.setCurrent(newPose);
        }
    }

    public void setPose(String str)
    {
        this.panel.setPose(str);
        this.fillPoseData();
    }

    public void setCurrent(String pose)
    {
        this.posesList.setCurrent(pose);
        this.fillPoseData();
    }

    public void setLimb(String name)
    {
        this.transform = this.panel.pose.limbs.get(name);
        this.fillTransformData(this.transform);
    }

    public void fillData(Model model)
    {
        this.posesList.clear();
        this.posesList.add(model.poses.keySet());
        this.posesList.sort();
    }

    public void fillPoseData()
    {
        this.hitbox.setValues(this.panel.pose.size[0], this.panel.pose.size[1]);
    }

    private void fillTransformData(ModelTransform transform)
    {
        this.translate.setValues(transform.translate[0], transform.translate[1], transform.translate[2]);
        this.scale.setValues(transform.scale[0], transform.scale[1], transform.scale[2]);
        this.rotation.setValues(transform.rotate[0], transform.rotate[1], transform.rotate[2]);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        return super.mouseClicked(mouseX, mouseY, mouseButton) || this.area.isInside(mouseX, mouseY);
    }

    @Override
    protected void drawLabels()
    {
        super.drawLabels();

        this.font.drawStringWithShadow("Hitbox", this.hitbox.area.x, this.hitbox.area.y - 12, 0xeeeeee);
    }
}