package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.dashboard.panels.GuiDashboardPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions.GuiActionPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions.GuiBlockActionPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions.GuiBreakBlockActionPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions.GuiBreakBlockAnimationPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions.GuiChatActionPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions.GuiCommandActionPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions.GuiDamageActionPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions.GuiDropActionPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions.GuiEmptyActionPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions.GuiEquipActionPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions.GuiItemUseActionPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions.GuiItemUseBlockActionPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions.GuiMorphActionPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions.GuiMountingActionPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions.GuiPlaceBlockActionPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions.GuiShootArrowActionPanel;
import mchorse.blockbuster.client.gui.framework.elements.GuiButtonElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiDelegateElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiSearchListElement;
import mchorse.blockbuster.client.gui.framework.elements.IGuiLegacy;
import mchorse.blockbuster.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiTextureButton;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.recording.actions.PacketAction;
import mchorse.blockbuster.network.common.recording.actions.PacketRequestActions;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.actions.AttackAction;
import mchorse.blockbuster.recording.actions.BreakBlockAction;
import mchorse.blockbuster.recording.actions.BreakBlockAnimation;
import mchorse.blockbuster.recording.actions.ChatAction;
import mchorse.blockbuster.recording.actions.CommandAction;
import mchorse.blockbuster.recording.actions.DamageAction;
import mchorse.blockbuster.recording.actions.DropAction;
import mchorse.blockbuster.recording.actions.EquipAction;
import mchorse.blockbuster.recording.actions.InteractBlockAction;
import mchorse.blockbuster.recording.actions.ItemUseAction;
import mchorse.blockbuster.recording.actions.ItemUseBlockAction;
import mchorse.blockbuster.recording.actions.MorphAction;
import mchorse.blockbuster.recording.actions.MountingAction;
import mchorse.blockbuster.recording.actions.PlaceBlockAction;
import mchorse.blockbuster.recording.actions.ShootArrowAction;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

public class GuiRecordingEditorPanel extends GuiDashboardPanel implements IGuiLegacy
{
    /**
     * A map of action editing panels mapped to their classes  
     */
    public Map<Class<? extends Action>, GuiActionPanel<? extends Action>> panels = new HashMap<Class<? extends Action>, GuiActionPanel<? extends Action>>();

    public GuiStringListElement records;
    public GuiRecordSelector selector;
    public GuiDelegateElement editor;

    public GuiButtonElement<GuiTextureButton> add;
    public GuiButtonElement<GuiTextureButton> dupe;
    public GuiButtonElement<GuiTextureButton> remove;

    public GuiSearchListElement list;

    public Record record;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public GuiActionPanel getPanel(Action action)
    {
        if (action == null)
        {
            return null;
        }

        GuiActionPanel panel = this.panels.get(action.getClass());

        if (panel == null)
        {
            panel = this.panels.get(Action.class);
        }

        panel.fill(action);

        return panel;
    }

    public GuiRecordingEditorPanel(Minecraft mc, GuiDashboard dashboard)
    {
        super(mc, dashboard);

        this.records = new GuiStringListElement(mc, (str) -> this.selectRecord(str));
        this.records.resizer().parent(this.area).set(0, 0, 80, 0).h(1, -100);

        this.selector = new GuiRecordSelector(mc, this, (action) -> this.selectAction(action));
        this.selector.resizer().parent(this.area).set(0, 0, 0, 80).y(1, -80).w(1, 0);
        this.selector.setVisible(false);

        this.editor = new GuiDelegateElement(mc, null);
        this.editor.resizer().parent(this.area).set(80, 0, 0, 0).w(1, -80).h(1, -80);

        /* Add/remove */
        this.add = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 32, 32, 32, 48, (b) -> this.list.setVisible(true));
        this.dupe = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 48, 32, 48, 48, (b) -> this.dupeAction());
        this.remove = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 64, 32, 64, 48, (b) -> this.removeAction());

        this.list = new GuiSearchListElement(mc, (str) -> this.createAction(str));
        this.list.elements.addAll(Action.TYPES.keySet());

        this.add.resizer().set(0, 8, 16, 16).parent(this.selector.area).x(1F, -24);
        this.dupe.resizer().set(0, 20, 16, 16).relative(this.add.resizer());
        this.remove.resizer().set(0, 20, 16, 16).relative(this.dupe.resizer());
        this.list.resizer().set(-90, 0, 80, 60).relative(this.add.resizer());

        this.children.add(this.records, this.editor, this.selector);
        this.selector.children.add(this.add, this.dupe, this.remove, this.list);
    }

    private void createAction(String str)
    {
        Integer type = Action.TYPES.get(str);

        if (type == null)
        {
            return;
        }

        try
        {
            Action action = Action.fromType(type.byteValue());
            int tick = this.selector.tick;
            int index = this.selector.index;

            this.record.addAction(tick, index, action);
            this.list.setVisible(false);
            this.selectAction(action);
            this.selector.index = index == -1 ? 0 : index;

            Dispatcher.sendToServer(new PacketAction(this.record.filename, tick, index, action, true));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void dupeAction()
    {
        int tick = this.selector.tick;
        int index = this.selector.index;

        Action action = this.record.getAction(tick, index);

        if (action == null)
        {
            return;
        }

        try
        {
            Action newAction = Action.fromType(action.getType());
            NBTTagCompound tag = new NBTTagCompound();
            action.toNBT(tag);
            newAction.fromNBT(tag);
            action = newAction;
        }
        catch (Exception e)
        {}

        this.record.addAction(tick, index, action);
        Dispatcher.sendToServer(new PacketAction(this.record.filename, tick, index, action, true));
    }

    private void removeAction()
    {
        if (this.selector.tick == -1)
        {
            return;
        }

        int tick = this.selector.tick;
        int index = this.selector.index;

        this.record.removeAction(tick, index);
        this.editor.setDelegate(null);
        this.selector.index = -1;
        Dispatcher.sendToServer(new PacketAction(this.record.filename, tick, index, null));
    }

    @Override
    public void open()
    {
        Set<String> records = ClientProxy.manager.records.keySet();

        this.records.clear();
        this.records.add(records);

        if (this.panels.isEmpty())
        {
            GuiEmptyActionPanel empty = new GuiEmptyActionPanel(this.mc);

            this.panels.put(Action.class, empty);
            this.panels.put(ChatAction.class, new GuiChatActionPanel(this.mc));
            this.panels.put(DropAction.class, new GuiDropActionPanel(this.mc));
            this.panels.put(EquipAction.class, new GuiEquipActionPanel(this.mc));
            this.panels.put(ShootArrowAction.class, new GuiShootArrowActionPanel(this.mc));
            this.panels.put(PlaceBlockAction.class, new GuiPlaceBlockActionPanel(this.mc));
            this.panels.put(MountingAction.class, new GuiMountingActionPanel(this.mc));
            this.panels.put(InteractBlockAction.class, new GuiBlockActionPanel<InteractBlockAction>(this.mc));
            this.panels.put(BreakBlockAction.class, new GuiBreakBlockActionPanel(this.mc));
            this.panels.put(MorphAction.class, new GuiMorphActionPanel(this.mc, this.dashboard));
            this.panels.put(AttackAction.class, new GuiDamageActionPanel(this.mc, "Attack action"));
            this.panels.put(DamageAction.class, new GuiDamageActionPanel(this.mc, "Damage action"));
            this.panels.put(CommandAction.class, new GuiCommandActionPanel(this.mc));
            this.panels.put(BreakBlockAnimation.class, new GuiBreakBlockAnimationPanel(this.mc));
            this.panels.put(ItemUseAction.class, new GuiItemUseActionPanel<ItemUseAction>(this.mc));
            this.panels.put(ItemUseBlockAction.class, new GuiItemUseBlockActionPanel(this.mc));
        }
    }

    @Override
    public void close()
    {
        this.save();
    }

    @SuppressWarnings("unchecked")
    private void save()
    {
        if (this.editor.delegate != null)
        {
            Action old = ((GuiActionPanel<? extends Action>) this.editor.delegate).action;

            Dispatcher.sendToServer(new PacketAction(this.record.filename, this.selector.tick, this.selector.index, old));
        }
    }

    private void selectRecord(String str)
    {
        Dispatcher.sendToServer(new PacketRequestActions(str));
    }

    private void selectAction(Action action)
    {
        this.save();
        this.editor.setDelegate(getPanel(action));
    }

    public void selectRecord(Record record)
    {
        this.record = record;
        this.selector.setVisible(record != null);
        this.selector.update();
        this.editor.setDelegate(null);
        this.list.filter("");
        this.list.setVisible(false);
    }

    @Override
    public boolean handleMouseInput(int mouseX, int mouseY) throws IOException
    {
        return this.children.handleMouseInput(mouseX, mouseY);
    }

    @Override
    public boolean handleKeyboardInput() throws IOException
    {
        return this.children.handleKeyboardInput();
    }
}