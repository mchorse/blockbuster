package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor;

import java.util.Set;

import mchorse.blockbuster.client.gui.dashboard.panels.GuiDashboardPanel;
import mchorse.blockbuster.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.blockbuster.client.gui.utils.Resizer.Measure;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.recording.PacketRequestActions;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.client.Minecraft;

public class GuiRecordingEditorPanel extends GuiDashboardPanel
{
    public GuiStringListElement records;
    public Record record;

    public GuiRecordingEditorPanel(Minecraft mc)
    {
        super(mc);

        this.records = new GuiStringListElement(mc, (str) -> this.selectRecord(str));
        this.records.resizer().parent(this.area).set(10, 10, 80, 0).h.set(1, Measure.RELATIVE, -20);

        this.children.add(this.records);
    }

    private void selectRecord(String str)
    {
        Dispatcher.sendToServer(new PacketRequestActions(str));
    }

    public void selectRecord(Record record)
    {
        this.record = record;
    }

    @Override
    public void open()
    {
        Set<String> records = ClientProxy.manager.records.keySet();

        this.records.clear();
        this.records.add(records);
    }
}