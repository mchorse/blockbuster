package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor;

import java.util.List;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.framework.GuiTooltip;
import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiSearchListElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class GuiRecordList extends GuiElement
{
    public GuiRecordingEditorPanel panel;

    public GuiSearchListElement records;

    public GuiRecordList(Minecraft mc, GuiRecordingEditorPanel panel)
    {
        super(mc);

        this.panel = panel;
        this.records = new GuiSearchListElement(mc, (str) -> this.panel.selectRecord(str));
        this.records.resizer().parent(this.area).set(10, 35, 0, 0).h(1, -35).w(1, -20);
        this.records.label = "Search...";

        this.createChildren().children.add(this.records);
    }

    public void clear()
    {
        this.records.elements.clear();
        this.records.filter("");
    }

    public void add(List<String> records)
    {
        for (String record : records)
        {
            this.records.elements.add(record);
        }

        this.records.filter("");
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        this.mc.renderEngine.bindTexture(GuiDashboard.ICONS);
        net.minecraftforge.fml.client.config.GuiUtils.drawContinuousTexturedBox(this.area.x, this.area.y, 0, 64, this.area.w, this.area.h, 32, 32, 0, 0);
        Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.y + 30, 0x44000000);

        this.font.drawStringWithShadow("Recordings", this.area.x + 10, this.area.y + 11, 0xcccccc);

        super.draw(tooltip, mouseX, mouseY, partialTicks);
    }
}