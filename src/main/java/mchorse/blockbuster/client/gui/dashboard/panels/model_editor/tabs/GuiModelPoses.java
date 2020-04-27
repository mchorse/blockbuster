package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelEditorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.modals.GuiListModal;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiThreeElement;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiTwoElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiMessageModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.client.gui.utils.resizers.Flex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.JsonToNBT;

public class GuiModelPoses extends GuiModelEditorTab
{
    private GuiIconElement addPose;
    private GuiIconElement removePose;
    private GuiIconElement importPose;
    private GuiIconElement copyPose;

    private GuiStringListElement posesList;
    private GuiThreeElement translate;
    private GuiThreeElement scale;
    private GuiThreeElement rotation;
    private GuiTwoElement hitbox;

    private ModelTransform transform;
    private String pose;

    public GuiModelPoses(Minecraft mc, GuiModelEditorPanel panel)
    {
        super(mc, panel);

        this.title = IKey.lang("blockbuster.gui.me.poses.title");

        this.posesList = new GuiStringListElement(mc, (str) -> this.setPose(str.get(0)));
        this.posesList.flex().set(0, 20, 80, 0).relative(this.area).h(1, -20).x(1, -80);
        this.add(this.posesList);

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

        this.translate.flex().set(10, 30, 110, 20).relative(this.area);
        this.scale.flex().set(0, 25, 110, 20).relative(this.translate.resizer());
        this.rotation.flex().set(0, 25, 110, 20).relative(this.scale.resizer());

        this.hitbox.flex().set(0, 40, 110, 20).relative(this.rotation.resizer());
        this.add(this.translate, this.scale, this.rotation, this.hitbox);

        /* Buttons */
        this.addPose = new GuiIconElement(mc, Icons.ADD, (b) -> this.addPose());
        this.removePose = new GuiIconElement(mc, Icons.REMOVE, (b) -> this.removePose());
        this.importPose = new GuiIconElement(mc, Icons.DOWNLOAD, (b) -> this.importPose());
        this.importPose.tooltip(IKey.lang("blockbuster.gui.me.poses.import_pose_tooltip"));
        this.copyPose = new GuiIconElement(mc, Icons.COPY, (b) -> this.copyPose());
        this.copyPose.tooltip(IKey.lang("blockbuster.gui.me.poses.copy_pose_tooltip"));

        this.copyPose.flex().set(2, 2, 16, 16).relative(this.area).x(1, -78);
        this.importPose.flex().set(20, 0, 16, 16).relative(this.copyPose.resizer());
        this.addPose.flex().set(20, 0, 16, 16).relative(this.importPose.resizer());
        this.removePose.flex().set(20, 0, 16, 16).relative(this.addPose.resizer());
        this.add(this.copyPose, this.importPose, this.addPose, this.removePose);
    }

    private void addPose()
    {
        GuiModal.addFullModal(this, () ->
        {
            GuiPromptModal modal = new GuiPromptModal(mc, IKey.lang("blockbuster.gui.me.poses.new_pose"), this::addPose);

            return modal.setValue(this.pose);
        });
    }

    private void addPose(String text)
    {
        if (!this.panel.model.poses.containsKey(text))
        {
            ModelPose pose = this.panel.pose.clone();

            this.panel.model.poses.put(text, pose);
            this.posesList.add(text);
            this.setCurrent(text);
            this.panel.setPose(text);
        }
    }

    private void removePose()
    {
        if (Model.REQUIRED_POSES.contains(this.pose))
        {
            GuiModal.addFullModal(this, () -> new GuiMessageModal(this.mc, IKey.lang("blockbuster.gui.me.poses.standard")));
        }
        else
        {
            this.panel.model.poses.remove(this.pose);

            String newPose = this.panel.model.poses.keySet().iterator().next();

            this.posesList.remove(this.pose);
            this.setPose(newPose);
            this.posesList.setCurrent(newPose);
        }
    }

    private void importPose()
    {
        GuiModal.addFullModal(this, () ->
        {
            GuiPromptModal modal = new GuiPromptModal(mc, IKey.lang("blockbuster.gui.me.poses.import_pose"), this::importPose);

            modal.text.field.setMaxStringLength(10000);

            return modal;
        });
    }

    private void importPose(String nbt)
    {
        try
        {
            this.panel.pose.fromNBT(JsonToNBT.getTagFromJson(nbt));
            this.panel.model.fillInMissing();
            this.setLimb(this.panel.limb.name);
        }
        catch (Exception e)
        {}
    }

    private void copyPose()
    {
        GuiModal.addFullModal(this, () ->
        {
            GuiListModal modal = new GuiListModal(this.mc, IKey.lang("blockbuster.gui.me.poses.copy_pose"), this::copyPose);

            return modal.addValues(this.panel.model.poses.keySet());
        });
    }

    private void copyPose(String text)
    {
        ModelPose pose = this.panel.model.poses.get(text);

        if (pose == null)
        {
            return;
        }

        this.transform.copy(pose.limbs.get(this.panel.limb.name));
        this.fillTransformData(this.transform);
    }

    public void setPose(String str)
    {
        this.pose = str;
        this.panel.setPose(str);
        this.fillPoseData();
    }

    public void setCurrent(String pose)
    {
        this.pose = pose;
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
    public void resize()
    {
        if (this.flex().h.unit == Flex.Measure.RELATIVE)
        {
            this.posesList.flex().x(0).y(150).w(1, 0).h(1, -150);
        }
        else
        {
            this.posesList.flex().y(20).h(1, -20).w(80).x(1, -80);
        }

        super.resize();
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        return super.mouseClicked(context) || this.area.isInside(context);
    }

    @Override
    protected void drawLabels()
    {
        super.drawLabels();

        if (this.flex().h.unit == Flex.Measure.RELATIVE)
        {
            int x = this.posesList.area.x;
            int y = this.posesList.area.y;

            Gui.drawRect(x, y, x + this.posesList.area.w, y + this.posesList.area.h, 0x88000000);
        }

        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.me.poses.hitbox"), this.hitbox.area.x, this.hitbox.area.y - 12, 0xeeeeee);
    }
}