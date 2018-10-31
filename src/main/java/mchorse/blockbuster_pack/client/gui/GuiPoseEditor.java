package mchorse.blockbuster_pack.client.gui;

import java.util.Map;

import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPoseEditor extends GuiElement
{
    public GuiCustomMorph parent;

    private GuiTrackpadElement tx;
    private GuiTrackpadElement ty;
    private GuiTrackpadElement tz;
    private GuiTrackpadElement sx;
    private GuiTrackpadElement sy;
    private GuiTrackpadElement sz;
    private GuiTrackpadElement rx;
    private GuiTrackpadElement ry;
    private GuiTrackpadElement rz;

    private GuiStringListElement limbs;
    private GuiButtonElement<GuiButton> resetPose;

    private ModelPose pose;
    private ModelTransform trans;

    public GuiPoseEditor(Minecraft mc, GuiCustomMorph parent)
    {
        super(mc);

        this.parent = parent;
        this.createChildren();

        this.tx = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (value) -> this.trans.translate[0] = value);
        this.ty = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (value) -> this.trans.translate[1] = value);
        this.tz = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (value) -> this.trans.translate[2] = value);
        this.sx = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (value) -> this.trans.scale[0] = value);
        this.sy = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (value) -> this.trans.scale[1] = value);
        this.sz = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (value) -> this.trans.scale[2] = value);
        this.rx = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (value) -> this.trans.rotate[0] = value);
        this.ry = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (value) -> this.trans.rotate[1] = value);
        this.rz = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (value) -> this.trans.rotate[2] = value);

        this.tx.resizer().set(0, 35, 60, 20).parent(this.area).x(1, -70);
        this.ty.resizer().set(0, 25, 60, 20).relative(this.tx.resizer());
        this.tz.resizer().set(0, 25, 60, 20).relative(this.ty.resizer());
        this.sx.resizer().set(0, 35, 60, 20).parent(this.area).x(1, -135);
        this.sy.resizer().set(0, 25, 60, 20).relative(this.sx.resizer());
        this.sz.resizer().set(0, 25, 60, 20).relative(this.sy.resizer());
        this.rx.resizer().set(0, 35, 60, 20).parent(this.area).x(1, -135 - 65);
        this.ry.resizer().set(0, 25, 60, 20).relative(this.rx.resizer());
        this.rz.resizer().set(0, 25, 60, 20).relative(this.ry.resizer());

        this.limbs = new GuiStringListElement(mc, (str) -> this.setLimb(str));
        this.limbs.resizer().parent(this.area).set(10, 40, 80, 90).h(1, -50);

        this.resetPose = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.morphs.reset"), (b) ->
        {
            this.parent.togglePose();
            this.parent.getMorph().customPose = null;
            this.parent.updateModelRenderer();
        });

        this.resetPose.resizer().relative(this.parent.togglePose.resizer()).set(-75, 0, 70, 20);

        this.children.add(this.tx, this.ty, this.tz, this.sx, this.sy, this.sz, this.rx, this.ry, this.rz, this.limbs, this.resetPose);
    }

    public void startEditing(CustomMorph custom)
    {
        this.limbs.clear();
        this.limbs.add(custom.model.limbs.keySet());
        this.limbs.sort();
    }

    public void createPose()
    {
        CustomMorph custom = this.parent.getMorph();

        if (custom.customPose == null)
        {
            this.pose = custom.getPose(this.mc.player).clone();
            custom.customPose = this.pose;
        }
        else
        {
            this.pose = custom.customPose;
        }

        Map.Entry<String, ModelTransform> entry = this.pose.limbs.entrySet().iterator().next();

        this.setLimb(entry.getKey());
        this.parent.modelRenderer.pose = this.pose;
        this.limbs.setCurrent(entry.getKey());
    }

    private void setLimb(String str)
    {
        ModelLimb limb = this.parent.getMorph().model.limbs.get(str);

        this.parent.modelRenderer.limb = limb;
        this.setTransform(this.pose.limbs.get(str));
    }

    public void setTransform(ModelTransform trans)
    {
        this.trans = trans;

        if (trans != null)
        {
            this.tx.trackpad.setValue(trans.translate[0]);
            this.ty.trackpad.setValue(trans.translate[1]);
            this.tz.trackpad.setValue(trans.translate[2]);

            this.sx.trackpad.setValue(trans.scale[0]);
            this.sy.trackpad.setValue(trans.scale[1]);
            this.sz.trackpad.setValue(trans.scale[2]);

            this.rx.trackpad.setValue(trans.rotate[0]);
            this.ry.trackpad.setValue(trans.rotate[1]);
            this.rz.trackpad.setValue(trans.rotate[2]);
        }
    }
}