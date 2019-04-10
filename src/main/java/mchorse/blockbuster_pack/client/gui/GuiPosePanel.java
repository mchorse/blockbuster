package mchorse.blockbuster_pack.client.gui;

import java.util.Map;

import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * TODO: rename to panel not editor 
 */
@SideOnly(Side.CLIENT)
public class GuiPosePanel extends GuiMorphPanel<CustomMorph, GuiCustomMorph>
{
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

    public GuiPosePanel(Minecraft mc, GuiCustomMorph editor)
    {
        super(mc, editor);

        this.tx = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (value) -> this.trans.translate[0] = value);
        this.ty = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (value) -> this.trans.translate[1] = value);
        this.tz = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (value) -> this.trans.translate[2] = value);
        this.sx = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (value) -> this.trans.scale[0] = value);
        this.sy = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (value) -> this.trans.scale[1] = value);
        this.sz = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (value) -> this.trans.scale[2] = value);
        this.rx = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (value) -> this.trans.rotate[0] = value);
        this.ry = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (value) -> this.trans.rotate[1] = value);
        this.rz = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (value) -> this.trans.rotate[2] = value);

        this.tx.resizer().set(0, 0, 60, 20).parent(this.area).x(0.5F, -95).y(1, -75);
        this.ty.resizer().set(0, 25, 60, 20).relative(this.tx.resizer());
        this.tz.resizer().set(0, 25, 60, 20).relative(this.ty.resizer());
        this.sx.resizer().set(65, 0, 60, 20).relative(this.tx.resizer());
        this.sy.resizer().set(0, 25, 60, 20).relative(this.sx.resizer());
        this.sz.resizer().set(0, 25, 60, 20).relative(this.sy.resizer());
        this.rx.resizer().set(65, 0, 60, 20).relative(this.sx.resizer());
        this.ry.resizer().set(0, 25, 60, 20).relative(this.rx.resizer());
        this.rz.resizer().set(0, 25, 60, 20).relative(this.ry.resizer());

        this.limbs = new GuiStringListElement(mc, (str) -> this.setLimb(str));
        this.limbs.resizer().parent(this.area).set(10, 50, 105, 90).h(1, -55);

        this.resetPose = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.morphs.reset"), (b) ->
        {
            this.editor.setPanel(this.editor.general);
            this.editor.morph.customPose = null;
            this.editor.updateModelRenderer();
        });

        this.resetPose.resizer().parent(this.area).set(10, 10, 105, 20);

        this.children.add(this.tx, this.ty, this.tz, this.sx, this.sy, this.sz, this.rx, this.ry, this.rz, this.limbs, this.resetPose);
    }

    private void setLimb(String str)
    {
        ModelLimb limb = this.editor.morph.model.limbs.get(str);

        this.editor.modelRenderer.limb = limb;
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

    @Override
    public void fillData(CustomMorph morph)
    {
        super.fillData(morph);

        this.limbs.clear();
        this.limbs.add(this.morph.model.limbs.keySet());
        this.limbs.sort();
    }

    @Override
    public void startEditing()
    {
        CustomMorph custom = this.morph;

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
        this.editor.modelRenderer.pose = this.pose;
        this.limbs.setCurrent(entry.getKey());
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        Gui.drawRect(this.limbs.area.x, this.limbs.area.y, this.limbs.area.getX(1), this.limbs.area.getY(1), 0x88000000);
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.limbs"), this.limbs.area.x, this.limbs.area.y - 12, 0xffffff);

        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.model_block.translate"), this.tx.area.x, this.tx.area.y - 12, 0xffffff);
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.model_block.scale"), this.sx.area.x, this.sx.area.y - 12, 0xffffff);
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.model_block.rotate"), this.rx.area.x, this.rx.area.y - 12, 0xffffff);

        super.draw(tooltip, mouseX, mouseY, partialTicks);
    }
}