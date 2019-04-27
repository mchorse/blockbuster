package mchorse.blockbuster.client.gui.utils;

import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

/**
 * Transformation editor GUI
 * 
 * Must be exactly 190 by 70 (with extra 12 on top for labels)
 */
public class GuiTransformations extends GuiElement
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

    public GuiTransformations(Minecraft mc)
    {
        super(mc);

        this.createChildren();

        this.tx = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (value) -> this.setT(value, this.ty.trackpad.value, this.tz.trackpad.value));
        this.ty = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (value) -> this.setT(this.tx.trackpad.value, value, this.tz.trackpad.value));
        this.tz = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (value) -> this.setT(this.tx.trackpad.value, this.ty.trackpad.value, value));
        this.sx = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (value) -> this.setS(value, this.sy.trackpad.value, this.sz.trackpad.value));
        this.sy = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (value) -> this.setS(this.sx.trackpad.value, value, this.sz.trackpad.value));
        this.sz = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (value) -> this.setS(this.sx.trackpad.value, this.sy.trackpad.value, value));
        this.rx = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (value) -> this.setR(value, this.ry.trackpad.value, this.rz.trackpad.value));
        this.ry = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (value) -> this.setR(this.rx.trackpad.value, value, this.rz.trackpad.value));
        this.rz = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (value) -> this.setR(this.rx.trackpad.value, this.ry.trackpad.value, value));

        this.tx.resizer().set(0, 0, 60, 20).parent(this.area);
        this.ty.resizer().set(0, 25, 60, 20).relative(this.tx.resizer());
        this.tz.resizer().set(0, 25, 60, 20).relative(this.ty.resizer());
        this.sx.resizer().set(65, 0, 60, 20).relative(this.tx.resizer());
        this.sy.resizer().set(0, 25, 60, 20).relative(this.sx.resizer());
        this.sz.resizer().set(0, 25, 60, 20).relative(this.sy.resizer());
        this.rx.resizer().set(65, 0, 60, 20).relative(this.sx.resizer());
        this.ry.resizer().set(0, 25, 60, 20).relative(this.rx.resizer());
        this.rz.resizer().set(0, 25, 60, 20).relative(this.ry.resizer());

        this.children.add(this.tx, this.ty, this.tz, this.sx, this.sy, this.sz, this.rx, this.ry, this.rz);
    }

    public void fillT(float x, float y, float z)
    {
        this.tx.setValue(x);
        this.ty.setValue(y);
        this.tz.setValue(z);
    }

    public void fillS(float x, float y, float z)
    {
        this.sx.setValue(x);
        this.sy.setValue(y);
        this.sz.setValue(z);
    }

    public void fillR(float x, float y, float z)
    {
        this.rx.setValue(x);
        this.ry.setValue(y);
        this.rz.setValue(z);
    }

    public void setT(float x, float y, float z)
    {}

    public void setS(float x, float y, float z)
    {}

    public void setR(float x, float y, float z)
    {}

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.model_block.translate"), this.tx.area.x, this.tx.area.y - 12, 0xffffff);
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.model_block.scale"), this.sx.area.x, this.sx.area.y - 12, 0xffffff);
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.model_block.rotate"), this.rx.area.x, this.rx.area.y - 12, 0xffffff);

        super.draw(tooltip, mouseX, mouseY, partialTicks);
    }
}