package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils;

import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs.GuiModelLimbs;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiCanvasEditor;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class GuiTextureCanvas extends GuiCanvasEditor
{
    public GuiTrackpadElement x;
    public GuiTrackpadElement y;
    public GuiIconElement close;

    public GuiModelLimbs panel;

    public GuiTextureCanvas(Minecraft mc, GuiModelLimbs panel)
    {
        super(mc);

        this.panel = panel;

        this.close = new GuiIconElement(mc, Icons.CLOSE, (b) -> this.toggleVisible());
        this.close.flex().relative(this).x(1F, -25).y(5);

        this.x = new GuiTrackpadElement(mc, (value) ->
        {
            this.panel.getPanel().limb.texture[0] = value.intValue();
            this.panel.getPanel().rebuildModel();
        });
        this.x.limit(0, 8192, true);

        this.y = new GuiTrackpadElement(mc, (value) ->
        {
            this.panel.getPanel().limb.texture[1] = value.intValue();
            this.panel.getPanel().rebuildModel();
        });
        this.y.limit(0, 8192, true);

        this.editor.add(Elements.label(IKey.lang("blockbuster.gui.me.limbs.texture")).background(0x88000000), this.x, this.y);
        this.add(this.editor, this.close);

        this.markContainer();
    }

    @Override
    protected boolean shouldDrawCanvas(GuiContext context)
    {
        return this.panel.getPanel().modelRenderer.texture != null;
    }

    @Override
    protected void drawCanvasFrame(GuiContext guiContext)
    {
        ResourceLocation location = this.panel.getPanel().modelRenderer.texture;
        Area area = this.calculate(-this.w / 2, -this.h / 2, this.w / 2, this.h / 2);

        this.mc.renderEngine.bindTexture(location);
        GuiDraw.drawBillboard(area.x, area.y, 0, 0, area.w, area.h, area.w, area.h);

        ModelLimb limb = this.panel.getPanel().limb;
        int lx = limb.texture[0];
        int ly = limb.texture[1];
        int lw = limb.size[0];
        int lh = limb.size[1];
        int ld = limb.size[2];

        /* Top and bottom */
        area = this.calculateRelative(lx + ld, ly, lx + ld + lw, ly + ld);

        Gui.drawRect(area.x, area.y, area.ex(), area.ey(), 0x5500ff00);

        area = this.calculateRelative(lx + ld + lw, ly, lx + ld + lw + lw, ly + ld);

        Gui.drawRect(area.x, area.y, area.ex(), area.ey(), 0x5500ffff);

        /* Front and back */
        area = this.calculateRelative(lx + ld, ly + ld, lx + ld + lw, ly + ld + lh);

        Gui.drawRect(area.x, area.y, area.ex(), area.ey(), 0x550000ff);

        area = this.calculateRelative(lx + ld * 2 + lw, ly + ld, lx + ld * 2 + lw * 2, ly + ld + lh);

        Gui.drawRect(area.x, area.y, area.ex(), area.ey(), 0x55ff00ff);

        /* Left and right */
        area = this.calculateRelative(lx, ly + ld, lx + ld, ly + ld + lh);

        Gui.drawRect(area.x, area.y, area.ex(), area.ey(), 0x55ff0000);

        area = this.calculateRelative(lx + ld + lw, ly + ld, lx + ld * 2 + lw, ly + ld + lh);

        Gui.drawRect(area.x, area.y, area.ex(), area.ey(), 0x55ffff00);

        /* Holes */
        area = this.calculateRelative(lx, ly, lx + ld, ly + ld);

        Gui.drawRect(area.x, area.y, area.ex(), area.ey(), 0xdd000000);

        area = this.calculateRelative(lx + ld + lw * 2, ly, lx + ld * 2 + lw * 2, ly + ld);

        Gui.drawRect(area.x, area.y, area.ex(), area.ey(), 0xdd000000);

        /* Outline */
        area = this.calculateRelative(lx, ly, lx + ld * 2 + lw * 2, ly + ld + lh);

        GuiDraw.drawOutline(area.x, area.y, area.ex(), area.ey(), 0xffff0000);
    }
}