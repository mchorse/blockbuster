package mchorse.blockbuster.client.gui.dashboard.panels.model_editor;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.Model.Transform;
import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.blockbuster.client.gui.utils.Resizer.Measure;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class GuiModelPoses extends GuiElement
{
    public GuiModelEditorPanel panel;

    private GuiStringListElement posesList;
    private GuiThreeElement translate;
    private GuiThreeElement scale;
    private GuiThreeElement rotation;

    private GuiTwoElement hitbox;
    private Transform transform;

    public GuiModelPoses(Minecraft mc, GuiModelEditorPanel panel)
    {
        super(mc);

        this.createChildren();
        this.panel = panel;

        this.posesList = new GuiStringListElement(mc, (str) -> this.setPose(str));
        this.posesList.resizer().set(10, 20, 80, 0).parent(this.area).h.set(1, Measure.RELATIVE, -30);
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

        this.translate.resizer().set(100, 20, 110, 20).parent(this.area);
        this.scale.resizer().set(0, 25, 110, 20).relative(this.translate.resizer());
        this.rotation.resizer().set(0, 25, 110, 20).relative(this.scale.resizer());

        this.hitbox.resizer().set(0, 40, 110, 20).relative(this.rotation.resizer());
        this.children.add(this.translate, this.scale, this.rotation, this.hitbox);
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
    }

    public void fillPoseData()
    {
        this.hitbox.setValues(this.panel.pose.size[0], this.panel.pose.size[1]);
    }

    private void fillTransformData(Transform transform)
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
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.getY(1), 0x88000000);

        super.draw(mouseX, mouseY, partialTicks);

        this.font.drawStringWithShadow("Hitbox", this.hitbox.area.x, this.hitbox.area.y - 12, 0xeeeeee);
        this.font.drawStringWithShadow("Poses", this.posesList.area.x, this.posesList.area.y - 12, 0xeeeeee);
    }
}