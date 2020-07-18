package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.GuiModelEditorPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiTwoElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiListModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiMessageModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

import java.util.List;

public class GuiModelPoses extends GuiModelEditorTab
{
    private GuiIconElement addPose;
    private GuiIconElement removePose;
    private GuiIconElement copyPose;
    private GuiIconElement applyPose;

    private GuiStringListElement posesList;
    private GuiTwoElement hitbox;

    private String pose;

    public GuiModelPoses(Minecraft mc, GuiModelEditorPanel panel)
    {
        super(mc, panel);

        this.title = IKey.lang("blockbuster.gui.me.poses.title");

        this.hitbox = new GuiTwoElement(mc, (values) ->
        {
            this.panel.pose.size[0] = values[0].floatValue();
            this.panel.pose.size[1] = values[1].floatValue();
            this.panel.dirty();
        });

        /* Buttons */
        this.addPose = new GuiIconElement(mc, Icons.ADD, (b) -> this.addPose());
        this.removePose = new GuiIconElement(mc, Icons.REMOVE, (b) -> this.removePose());
        this.copyPose = new GuiIconElement(mc, Icons.COPY, (b) -> this.copyPose());
        this.copyPose.tooltip(IKey.lang("blockbuster.gui.me.poses.copy_pose_tooltip"));
        this.applyPose = new GuiIconElement(mc, Icons.PASTE, (b) -> this.applyPose());
        this.applyPose.tooltip(IKey.lang("blockbuster.gui.me.poses.apply_pose_tooltip"));

        GuiElement sidebar = Elements.row(mc, 0, 0, 20, this.addPose, this.removePose, this.copyPose, this.applyPose);
        GuiElement bottom = new GuiElement(mc);

        sidebar.flex().relative(this).x(1F).h(20).anchorX(1F).row(0).resize();
        bottom.flex().relative(this).y(1F).w(1F).anchorY(1F).column(5).vertical().stretch().height(20).padding(10);

        this.posesList = new GuiStringListElement(mc, (str) -> this.setPose(str.get(0)));
        this.posesList.flex().relative(this).y(20).w(1F).hTo(bottom.area);

        bottom.add(Elements.label(IKey.lang("blockbuster.gui.me.poses.hitbox")), this.hitbox);
        this.add(sidebar, bottom, this.posesList);
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
            this.panel.dirty();
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
            this.panel.dirty();
        }
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

        this.panel.transform.copy(pose.limbs.get(this.panel.limb.name));
        this.panel.dirty();
    }

    private void applyPose()
    {
        GuiModal.addFullModal(this, () ->
        {
            GuiListModal modal = new GuiListModal(this.mc, IKey.lang("blockbuster.gui.me.poses.apply_pose"), null).callback(this::copyPose);

            modal.list.getList().remove(0);
            modal.list.multi();

            modal.addValues(this.panel.model.poses.keySet());
            modal.list.selectAll();
            modal.list.toggleIndex(modal.list.getList().indexOf(this.pose));

            return modal;
        });
    }

    private void copyPose(List<String> poses)
    {
        ModelPose pose = this.panel.model.poses.get(this.pose);

        if (pose == null)
        {
            return;
        }

        ModelTransform current = pose.limbs.get(this.panel.limb.name);

        if (current == null || poses.isEmpty())
        {
            return;
        }

        for (String name : poses)
        {
            ModelPose target = this.panel.model.poses.get(name);

            if (target != null)
            {
                ModelTransform transform = target.limbs.get(this.panel.limb.name);

                if (transform != null)
                {
                    transform.copy(current);
                }
            }
        }

        this.panel.dirty();
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

    @Override
    public void draw(GuiContext context)
    {
        this.area.draw(0xaa000000);

        super.draw(context);
    }
}