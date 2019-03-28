package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor;

import java.util.List;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringSearchListElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

public class GuiRecordList extends GuiElement
{
    public GuiRecordingEditorPanel panel;
    public GuiStringSearchListElement records;

    public GuiRecordList(Minecraft mc, GuiRecordingEditorPanel panel)
    {
        super(mc);

        this.panel = panel;
        this.records = new GuiStringSearchListElement(mc, (str) -> this.panel.selectRecord(str));
        this.records.resizer().parent(this.area).set(10, 35, 0, 0).h(1, -45).w(1, -20);
        this.records.label = I18n.format("blockbuster.gui.search") + "...";

        this.createChildren().children.add(this.records);
    }

    public void clear()
    {
        this.records.elements.clear();
        this.records.filter("", true);
    }

    public void add(List<String> records)
    {
        for (String record : records)
        {
            this.records.elements.add(record);
        }

        this.records.filter("", true);
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        this.mc.renderEngine.bindTexture(GuiDashboard.GUI_ICONS);
        net.minecraftforge.fml.client.config.GuiUtils.drawContinuousTexturedBox(this.area.x, this.area.y, 0, 64, this.area.w, this.area.h, 32, 32, 0, 0);
        Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.y + 30, 0x44000000);

        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.record_editor.title"), this.area.x + 10, this.area.y + 11, 0xcccccc);

        super.draw(tooltip, mouseX, mouseY, partialTicks);
    }
}