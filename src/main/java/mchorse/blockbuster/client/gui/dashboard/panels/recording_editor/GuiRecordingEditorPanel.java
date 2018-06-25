package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor;

import java.util.Set;

import mchorse.blockbuster.client.gui.dashboard.panels.GuiDashboardPanel;
import mchorse.blockbuster.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.blockbuster.client.gui.utils.Resizer.Measure;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.recording.PacketRequestActions;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

public class GuiRecordingEditorPanel extends GuiDashboardPanel
{
    public GuiStringListElement records;
    public GuiRecordSelector selector;

    public Record record;
    public String current = "";

    public GuiRecordingEditorPanel(Minecraft mc)
    {
        super(mc);

        this.records = new GuiStringListElement(mc, (str) -> this.selectRecord(str));
        this.records.resizer().parent(this.area).set(0, 0, 80, 0).h.set(1, Measure.RELATIVE);

        this.selector = new GuiRecordSelector(mc, this, (action) -> this.selectAction(action));
        this.selector.resizer().parent(this.area).set(80, 0, 0, 120);
        this.selector.resizer().y.set(1, Measure.RELATIVE, -120);
        this.selector.resizer().w.set(1, Measure.RELATIVE, -80);
        this.selector.setVisible(false);

        this.children.add(this.records, this.selector);
    }

    private void selectRecord(String str)
    {
        Dispatcher.sendToServer(new PacketRequestActions(str));
    }

    private void selectAction(Action action)
    {
        if (action == null)
        {
            this.current = "";
        }
        else
        {
            NBTTagCompound tag = new NBTTagCompound();

            action.toNBT(tag);
            this.current = tag.toString();
        }
    }

    public void selectRecord(Record record)
    {
        this.record = record;
        this.selector.setVisible(record != null);
        this.selector.update();
    }

    @Override
    public void open()
    {
        Set<String> records = ClientProxy.manager.records.keySet();

        this.records.clear();
        this.records.add(records);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.font.drawSplitString(this.current, this.area.getX(0.5F) - (this.area.w - 180) / 2, this.area.y + 20, (this.area.w - 180), 0xcccccc);

        super.draw(mouseX, mouseY, partialTicks);
    }
}