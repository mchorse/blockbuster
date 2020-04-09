package mchorse.blockbuster.client.gui.utils;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
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
    private GuiToggleElement one;

    public GuiTransformations(Minecraft mc)
    {
        super(mc);

        this.tx = new GuiTrackpadElement(mc, (value) -> this.setT(value, this.ty.value, this.tz.value));
        this.tx.tooltip(I18n.format("blockbuster.gui.model_block.x"));
        this.ty = new GuiTrackpadElement(mc, (value) -> this.setT(this.tx.value, value, this.tz.value));
        this.ty.tooltip(I18n.format("blockbuster.gui.model_block.y"));
        this.tz = new GuiTrackpadElement(mc, (value) -> this.setT(this.tx.value, this.ty.value, value));
        this.tz.tooltip(I18n.format("blockbuster.gui.model_block.z"));
        this.sx = new GuiTrackpadElement(mc, (value) ->
        {
            boolean one = this.one.isToggled();

            this.setS(value, one ? value : this.sy.value, one ? value : this.sz.value);
        });
        this.sx.tooltip(I18n.format("blockbuster.gui.model_block.x"));
        this.sy = new GuiTrackpadElement(mc, (value) -> this.setS(this.sx.value, value, this.sz.value));
        this.sy.tooltip(I18n.format("blockbuster.gui.model_block.y"));
        this.sz = new GuiTrackpadElement(mc, (value) -> this.setS(this.sx.value, this.sy.value, value));
        this.sz.tooltip(I18n.format("blockbuster.gui.model_block.z"));
        this.rx = new GuiTrackpadElement(mc, (value) -> this.setR(value, this.ry.value, this.rz.value));
        this.rx.tooltip(I18n.format("blockbuster.gui.model_block.x"));
        this.ry = new GuiTrackpadElement(mc, (value) -> this.setR(this.rx.value, value, this.rz.value));
        this.ry.tooltip(I18n.format("blockbuster.gui.model_block.y"));
        this.rz = new GuiTrackpadElement(mc, (value) -> this.setR(this.rx.value, this.ry.value, value));
        this.rz.tooltip(I18n.format("blockbuster.gui.model_block.z"));
        this.one = new GuiToggleElement(mc, "", false, (b) ->
        {
            boolean one = b.isToggled();

            this.sy.setVisible(!one);
            this.sz.setVisible(!one);

            if (!one)
            {
                this.sy.setValueAndNotify(this.sx.value);
                this.sz.setValueAndNotify(this.sx.value);
            }
        });

        this.tx.flex().set(0, 0, 60, 20).relative(this.area);
        this.ty.flex().set(0, 25, 60, 20).relative(this.tx.resizer());
        this.tz.flex().set(0, 25, 60, 20).relative(this.ty.resizer());
        this.sx.flex().set(65, 0, 60, 20).relative(this.tx.resizer());
        this.sy.flex().set(0, 25, 60, 20).relative(this.sx.resizer());
        this.sz.flex().set(0, 25, 60, 20).relative(this.sy.resizer());
        this.rx.flex().set(65, 0, 60, 20).relative(this.sx.resizer());
        this.ry.flex().set(0, 25, 60, 20).relative(this.rx.resizer());
        this.rz.flex().set(0, 25, 60, 20).relative(this.ry.resizer());
        this.one.flex().relative(this.sx.resizer()).set(49, -13, 11, 11);

        this.add(this.tx, this.ty, this.tz, this.sx, this.sy, this.sz, this.rx, this.ry, this.rz, this.one);
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
    public void draw(GuiContext context)
    {
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.model_block.translate"), this.tx.area.x, this.tx.area.y - 12, 0xffffff);
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.model_block.scale"), this.sx.area.x, this.sx.area.y - 12, 0xffffff);
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.model_block.rotate"), this.rx.area.x, this.rx.area.y - 12, 0xffffff);

        super.draw(context);
    }
}