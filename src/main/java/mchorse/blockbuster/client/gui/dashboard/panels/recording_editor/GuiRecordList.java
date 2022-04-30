package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.recording.scene.Replay;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringSearchListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

import java.util.List;

public class GuiRecordList extends GuiElement
{
    public GuiRecordingEditorPanel panel;
    public GuiStringSearchListElement records;
    public boolean director;

    public GuiRecordList(Minecraft mc, GuiRecordingEditorPanel panel)
    {
        super(mc);

        this.panel = panel;
        this.records = new GuiStringSearchListElement(mc, (str) -> this.panel.selectRecord(str.get(0)));
        this.records.flex().relative(this.area).set(10, 35, 0, 0).h(1, -45).w(1, -20);
        this.records.label = IKey.lang("blockbuster.gui.search");

        this.add(this.records);
    }

    public void clear()
    {
        this.records.list.clear();
        this.records.filter("", true);
    }

    public void add(List<String> records)
    {
        List<Replay> replays = ClientProxy.panels.scenePanel.getReplays();
        boolean loadAll = replays == null || !CameraHandler.canSync() || !CameraHandler.isCameraEditorOpen();

        if (loadAll)
        {
            /* Display all replays */
            for (String record : records)
            {
                this.records.list.add(record);
            }
        }
        else
        {
            /* Display only current director block's replays */
            for (Replay replay : replays)
            {
                if (records.contains(replay.id) && !this.records.list.getList().contains(replay.id))
                {
                    this.records.list.getList().add(replay.id);
                }
            }
        }

        this.director = !loadAll;
        this.records.filter("", true);
        this.records.list.update();
    }

    @Override
    public void toggleVisible()
    {
        super.toggleVisible();

        this.panel.updateEditorWidth();
    }

    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);

        this.panel.updateEditorWidth();
    }

    @Override
    public void draw(GuiContext context)
    {
        this.area.draw(0xff222222);
        Gui.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.y + 30, 0x44000000);

        this.font.drawStringWithShadow(I18n.format(this.director ? "blockbuster.gui.record_editor.directors" : "blockbuster.gui.record_editor.title"), this.area.x + 10, this.area.y + 11, 0xcccccc);

        super.draw(context);
    }
}