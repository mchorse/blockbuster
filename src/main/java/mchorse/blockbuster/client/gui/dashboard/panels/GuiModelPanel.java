package mchorse.blockbuster.client.gui.dashboard.panels;

import mchorse.blockbuster.client.gui.elements.GuiMorphsPopup;
import mchorse.blockbuster.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.blockbuster.client.gui.utils.Resizer.UnitMeasurement;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs.MorphCell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

public class GuiModelPanel extends GuiDashboardPanel
{
    private TileEntityModel model;
    private TileEntityModel temp = new TileEntityModel();

    private GuiMorphsPopup morphs;

    private GuiTrackpadElement yaw;
    private GuiTrackpadElement pitch;
    private GuiTrackpadElement body;

    private GuiTrackpadElement x;
    private GuiTrackpadElement y;
    private GuiTrackpadElement z;

    private GuiTrackpadElement sx;
    private GuiTrackpadElement sy;
    private GuiTrackpadElement sz;

    private GuiTrackpadElement rx;
    private GuiTrackpadElement ry;
    private GuiTrackpadElement rz;

    public GuiModelPanel(Minecraft mc)
    {
        super(mc);

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        this.morphs = new GuiMorphsPopup(6, null, Morphing.get(player));

        /* Entity angles */
        this.children.add(this.yaw = new GuiTrackpadElement(mc, "Yaw", (value) -> this.model.rotateYawHead = value));
        this.yaw.resizer().set(10, 20, 80, 20).setParent(this.area);

        this.children.add(this.pitch = new GuiTrackpadElement(mc, "Pitch", (value) -> this.model.rotatePitch = value));
        this.pitch.resizer().set(0, 25, 80, 20).setRelative(this.yaw.resizer);

        this.children.add(this.body = new GuiTrackpadElement(mc, "Body", (value) -> this.model.rotateBody = value));
        this.body.resizer().set(0, 25, 80, 20).setRelative(this.pitch.resizer);

        /* Rotation */
        this.children.add(this.rx = new GuiTrackpadElement(mc, "X", (value) -> this.model.rx = value));
        this.rx.resizer().set(0, 40, 80, 20).setRelative(this.body.resizer);

        this.children.add(this.ry = new GuiTrackpadElement(mc, "Y", (value) -> this.model.ry = value));
        this.ry.resizer().set(0, 25, 80, 20).setRelative(this.rx.resizer);

        this.children.add(this.rz = new GuiTrackpadElement(mc, "Z", (value) -> this.model.rz = value));
        this.rz.resizer().set(0, 25, 80, 20).setRelative(this.ry.resizer);

        /* Translation */
        this.children.add(this.x = new GuiTrackpadElement(mc, "X", (value) -> this.model.x = value));
        this.x.resizer().set(0, 20, 80, 20).setParent(this.area).x.set(1, UnitMeasurement.PERCENTAGE, -90);

        this.children.add(this.y = new GuiTrackpadElement(mc, "Y", (value) -> this.model.y = value));
        this.y.resizer().set(0, 25, 80, 20).setRelative(this.x.resizer);

        this.children.add(this.z = new GuiTrackpadElement(mc, "Z", (value) -> this.model.z = value));
        this.z.resizer().set(0, 25, 80, 20).setRelative(this.y.resizer);

        /* Scale */
        this.children.add(this.sx = new GuiTrackpadElement(mc, "X", (value) -> this.model.sx = value));
        this.sx.resizer().set(0, 40, 80, 20).setRelative(this.z.resizer);

        this.children.add(this.sy = new GuiTrackpadElement(mc, "Y", (value) -> this.model.sy = value));
        this.sy.resizer().set(0, 25, 80, 20).setRelative(this.sx.resizer);

        this.children.add(this.sz = new GuiTrackpadElement(mc, "Z", (value) -> this.model.sz = value));
        this.sz.resizer().set(0, 25, 80, 20).setRelative(this.sy.resizer);
    }

    public GuiModelPanel setModelBlock(TileEntityModel model)
    {
        this.model = model;
        this.temp.copyData(model);

        return this;
    }

    @Override
    public boolean needsBackground()
    {
        return true;
    }

    @Override
    public void resize(int width, int height)
    {
        if (height >= 380)
        {
            this.x.resizer().setParent(null).setRelative(this.rz.resizer).set(0, 40, 80, 20).x.set(0, UnitMeasurement.PIXELS, 0);
            this.x.resizer().y.set(40, UnitMeasurement.PIXELS, 0);
            this.yaw.resizer().y.set(0.5F, UnitMeasurement.PERCENTAGE, -165);
        }
        else
        {
            this.x.resizer().setParent(this.area).setRelative(null).set(0, 20, 80, 20).x.set(1, UnitMeasurement.PERCENTAGE, -90);
            this.x.resizer().y.set(0.5F, UnitMeasurement.PERCENTAGE, -80);
            this.yaw.resizer().y.set(0.5F, UnitMeasurement.PERCENTAGE, -80);
        }

        super.resize(width, height);

        this.morphs.updateRect(this.area.x, this.area.y, this.area.w, this.area.h);
        this.morphs.setWorldAndResolution(this.mc, width, height);

        if (this.model != null)
        {
            this.morphs.morphs.setSelected(this.model.morph);

            this.yaw.trackpad.setValue(this.model.rotateYawHead);
            this.pitch.trackpad.setValue(this.model.rotatePitch);
            this.body.trackpad.setValue(this.model.rotateBody);

            this.x.trackpad.setValue(this.model.x);
            this.y.trackpad.setValue(this.model.y);
            this.z.trackpad.setValue(this.model.z);

            this.rx.trackpad.setValue(this.model.rx);
            this.ry.trackpad.setValue(this.model.ry);
            this.rz.trackpad.setValue(this.model.rz);

            this.sx.trackpad.setValue(this.model.sx);
            this.sy.trackpad.setValue(this.model.sy);
            this.sz.trackpad.setValue(this.model.sz);
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        MorphCell cell = this.morphs.morphs.getSelected();

        if (cell != null)
        {
            int x = this.area.getX(0.5F);
            int y = this.area.getY(0.65F);

            GlStateManager.pushMatrix();
            cell.current().morph.renderOnScreen(this.mc.thePlayer, x, y, 50, 1.0F);
            GlStateManager.popMatrix();
        }

        this.drawString(this.font, I18n.format("blockbuster.gui.model_block.entity"), this.yaw.area.x + 2, this.yaw.area.y - 12, 0xffffff);
        this.drawString(this.font, I18n.format("blockbuster.gui.model_block.translate"), this.x.area.x + 2, this.x.area.y - 12, 0xffffff);
        this.drawString(this.font, I18n.format("blockbuster.gui.model_block.rotate"), this.rx.area.x + 2, this.rx.area.y - 12, 0xffffff);
        this.drawString(this.font, I18n.format("blockbuster.gui.model_block.scale"), this.sx.area.x + 2, this.sx.area.y - 12, 0xffffff);

        super.draw(mouseX, mouseY, partialTicks);
    }
}