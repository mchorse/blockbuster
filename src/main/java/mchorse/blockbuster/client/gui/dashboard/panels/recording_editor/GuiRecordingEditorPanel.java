package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.dashboard.panels.GuiDashboardPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions.GuiActionPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions.GuiCommandActionPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions.GuiEmptyActionPanel;
import mchorse.blockbuster.client.gui.framework.elements.GuiDelegateElement;
import mchorse.blockbuster.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.blockbuster.client.gui.utils.Resizer.Measure;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.recording.actions.PacketAction;
import mchorse.blockbuster.network.common.recording.actions.PacketRequestActions;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.actions.CommandAction;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.client.Minecraft;

public class GuiRecordingEditorPanel extends GuiDashboardPanel
{
    /**
     * A map of  
     */
    public static final Map<Class<? extends Action>, GuiActionPanel<? extends Action>> PANELS = new HashMap<Class<? extends Action>, GuiActionPanel<? extends Action>>();

    public GuiStringListElement records;
    public GuiRecordSelector selector;
    public GuiDelegateElement editor;

    public Record record;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static GuiActionPanel getPanel(Action action)
    {
        if (action == null)
        {
            return null;
        }

        GuiActionPanel panel = PANELS.get(action.getClass());

        if (panel == null)
        {
            panel = PANELS.get(Action.class);
        }

        panel.fill(action);

        return panel;
    }

    public GuiRecordingEditorPanel(Minecraft mc, GuiDashboard dashboard)
    {
        super(mc, dashboard);

        this.records = new GuiStringListElement(mc, (str) -> this.selectRecord(str));
        this.records.resizer().parent(this.area).set(0, 0, 80, 0).h.set(1, Measure.RELATIVE, -100);

        this.selector = new GuiRecordSelector(mc, this, (action) -> this.selectAction(action));
        this.selector.resizer().parent(this.area).set(0, 0, 0, 100);
        this.selector.resizer().y.set(1, Measure.RELATIVE, -100);
        this.selector.resizer().w.set(1, Measure.RELATIVE);
        this.selector.setVisible(false);

        this.editor = new GuiDelegateElement(mc, null);
        this.editor.resizer().parent(this.area).set(80, 0, 0, 0);
        this.editor.resizer().w.set(1, Measure.RELATIVE, -80);
        this.editor.resizer().h.set(1, Measure.RELATIVE, -100);

        this.children.add(this.records, this.selector, this.editor);
    }

    private void selectRecord(String str)
    {
        Dispatcher.sendToServer(new PacketRequestActions(str));
    }

    @SuppressWarnings("unchecked")
    private void selectAction(Action action)
    {
        if (this.editor.delegate != null)
        {
            Action old = ((GuiActionPanel<? extends Action>) this.editor.delegate).action;

            Dispatcher.sendToServer(new PacketAction(this.record.filename, this.selector.tick, this.selector.index, old));
        }

        this.editor.setDelegate(getPanel(action));
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

        if (PANELS.isEmpty())
        {
            Minecraft mc = Minecraft.getMinecraft();
            GuiEmptyActionPanel empty = new GuiEmptyActionPanel(mc);

            PANELS.put(Action.class, empty);
            PANELS.put(CommandAction.class, new GuiCommandActionPanel(mc));
        }
    }
}