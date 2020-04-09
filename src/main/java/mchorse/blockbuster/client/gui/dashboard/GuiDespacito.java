package mchorse.blockbuster.client.gui.dashboard;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.resizers.layout.ColumnResizer;
import mchorse.mclib.utils.Direction;
import mchorse.metamorph.util.MMIcons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiUtils;

/**
 * Dashboard sidebar GUI
 * 
 * Des-pa-ci-to.
 */
public class GuiDespacito extends GuiElement
{
    public GuiDashboard dashboard;

    public GuiDespacito(Minecraft mc, GuiDashboard dashboard)
    {
        super(mc);

        this.dashboard = dashboard;

        GuiIconElement main = new GuiIconElement(mc, Icons.MORE, (button) -> dashboard.openPanel(dashboard.mainPanel));
        main.tooltip(I18n.format("blockbuster.gui.dashboard.main"), Direction.RIGHT);
        main.flex().wh(24, 24);

        GuiIconElement director = new GuiIconElement(mc, MMIcons.BLOCK, (button) -> dashboard.openPanel(dashboard.directorPanel));
        director.tooltip(I18n.format("blockbuster.gui.dashboard.director"), Direction.RIGHT);
        director.flex().wh(24, 24);

        GuiIconElement block = new GuiIconElement(mc, Icons.POSE, (button) -> dashboard.openPanel(dashboard.modelPanel));
        block.tooltip(I18n.format("blockbuster.gui.dashboard.model"), Direction.RIGHT);
        block.flex().wh(24, 24);

        GuiIconElement model = new GuiIconElement(mc, MMIcons.BLOCK, (button) -> dashboard.openPanel(dashboard.modelEditorPanel));
        model.tooltip(I18n.format("blockbuster.gui.dashboard.model_editor"), Direction.RIGHT);
        model.flex().wh(24, 24);

        GuiIconElement editor = new GuiIconElement(mc, Icons.POSE, (button) -> dashboard.openPanel(dashboard.recordingEditorPanel));
        editor.tooltip(I18n.format("blockbuster.gui.dashboard.player_recording"), Direction.RIGHT);
        editor.flex().wh(24, 24);

        GuiIconElement texture = new GuiIconElement(mc, Icons.MATERIAL, (button) -> dashboard.openPanel(dashboard.texturePanel));
        texture.tooltip(I18n.format("blockbuster.gui.dashboard.texture"), Direction.RIGHT);
        texture.flex().wh(24, 24);

        this.add(main, director, block, model, editor, texture);
        ColumnResizer.apply(this, 0).vertical().scroll().padding(4);
    }

    @Override
    public void draw(GuiContext context)
    {
        int h = this.area.h;
        int x = this.area.x + this.area.w;

        Gui.drawRect(this.area.x, this.area.y, this.area.w, h, 0xff333333);
        GuiDraw.drawHorizontalGradientRect(x, 0, x + 16, h, 0x22000000, 0x00000000, 0);
        GuiDraw.drawHorizontalGradientRect(x - 8, 0, x, h, 0x00000000, 0x22000000, 0);
        Gui.drawRect(x - 1, this.area.y, x, this.area.ey(), 0x22000000);

        super.draw(context);
    }
}