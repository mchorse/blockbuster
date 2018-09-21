package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.dashboard.GuiSidebarButton;
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
import mchorse.blockbuster.client.gui.framework.GuiTooltip;
import mchorse.blockbuster.client.gui.framework.GuiTooltip.TooltipDirection;
import mchorse.blockbuster.client.gui.framework.elements.GuiButtonElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiDelegateElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiSearchListElement;
import mchorse.blockbuster.client.gui.framework.elements.IGuiLegacy;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiTextureButton;
import mchorse.blockbuster.events.ActionPanelRegisterEvent;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.recording.actions.PacketAction;
import mchorse.blockbuster.network.common.recording.actions.PacketRequestAction;
import mchorse.blockbuster.network.common.recording.actions.PacketRequestActions;
import mchorse.blockbuster.recording.ActionRegistry;
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
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;

public class GuiRecordingEditorPanel extends GuiDashboardPanel implements IGuiLegacy
{
    /**
     * A map of action editing panels mapped to their classes  
     */
    public Map<Class<? extends Action>, GuiActionPanel<? extends Action>> panels = new HashMap<Class<? extends Action>, GuiActionPanel<? extends Action>>();

    public GuiRecordList records;
    public GuiRecordSelector selector;
    public GuiDelegateElement<GuiActionPanel<? extends Action>> editor;

    public GuiButtonElement<GuiTextureButton> add;
    public GuiButtonElement<GuiTextureButton> dupe;
    public GuiButtonElement<GuiTextureButton> remove;

    public GuiButtonElement<GuiTextureButton> open;

    public GuiSearchListElement list;

    public Record record;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public GuiActionPanel<? extends Action> getPanel(Action action)
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

        this.records = new GuiRecordList(mc, this);
        this.records.resizer().set(0, 0, 120, 0).parent(this.area).x(1, -120).h(1, 0);

        this.selector = new GuiRecordSelector(mc, this, (action) -> this.selectAction(action));
        this.selector.resizer().parent(this.area).set(0, 0, 0, 80).y(1, -80).w(1, 0);
        this.selector.setVisible(false);

        this.editor = new GuiDelegateElement<GuiActionPanel<? extends Action>>(mc, null);
        this.editor.resizer().parent(this.area).set(0, 0, 0, 0).w(1, 0).h(1, -80);

        /* Add/remove */
        this.add = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 32, 32, 32, 48, (b) -> this.list.toggleVisible()).tooltip(I18n.format("blockbuster.gui.add"), TooltipDirection.LEFT);
        this.dupe = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 48, 32, 48, 48, (b) -> this.dupeAction()).tooltip(I18n.format("blockbuster.gui.duplicate"), TooltipDirection.LEFT);
        this.remove = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 64, 32, 64, 48, (b) -> this.removeAction()).tooltip(I18n.format("blockbuster.gui.remove"), TooltipDirection.LEFT);

        this.list = new GuiSearchListElement(mc, (str) -> this.createAction(str));
        this.list.label = I18n.format("blockbuster.gui.search") + "...";
        this.list.elements.addAll(ActionRegistry.NAME_TO_CLASS.keySet());
        this.list.background = true;

        this.add.resizer().set(0, 2, 16, 16).parent(this.selector.area).x(1F, -18);
        this.dupe.resizer().set(0, 20, 16, 16).relative(this.add.resizer());
        this.remove.resizer().set(0, 20, 16, 16).relative(this.dupe.resizer());
        this.list.resizer().set(0, 0, 80, 80).parent(this.selector.area).x(1, -100);

        GuiElement element = new GuiButtonElement<GuiSidebarButton>(mc, new GuiSidebarButton(0, 0, 0, new ItemStack(Items.RECORD_13)), (b) -> this.records.toggleVisible());
        element.resizer().set(0, 2, 24, 24).parent(this.area).x(1, -28);

        this.children.add(this.editor, this.selector, this.records, element);
        this.selector.children.add(this.add, this.dupe, this.remove, this.list);
    }

    private void createAction(String str)
    {
        if (!ActionRegistry.NAME_TO_CLASS.containsKey(str))
        {
            return;
        }

        try
        {
            Action action = ActionRegistry.fromName(str);
            int tick = this.selector.tick;
            int index = this.selector.index;

            this.record.addAction(tick, index, action);
            this.list.setVisible(false);
            this.selectAction(action);
            this.selector.index = index == -1 ? this.record.actions.get(tick).size() - 1 : index;
            this.selector.recalculateVertical();

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
            Action newAction = ActionRegistry.fromType(ActionRegistry.getType(action));
            NBTTagCompound tag = new NBTTagCompound();
            action.toNBT(tag);
            newAction.fromNBT(tag);
            action = newAction;
        }
        catch (Exception e)
        {}

        this.record.addAction(tick, index, action);
        this.selector.recalculateVertical();
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

        if (this.selector.index == 0)
        {
            this.selector.index = -1;
            this.editor.setDelegate(null);
        }
        else
        {
            this.selector.index--;
            this.editor.setDelegate(this.getPanel(this.record.getAction(this.selector.tick, this.selector.index)));
        }

        this.selector.recalculateVertical();
        Dispatcher.sendToServer(new PacketAction(this.record.filename, tick, index, null));
    }

    @Override
    public void open()
    {
        this.records.clear();
        Dispatcher.sendToServer(new PacketRequestActions());

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
            this.panels.put(AttackAction.class, new GuiDamageActionPanel(this.mc));
            this.panels.put(DamageAction.class, new GuiDamageActionPanel(this.mc));
            this.panels.put(CommandAction.class, new GuiCommandActionPanel(this.mc));
            this.panels.put(BreakBlockAnimation.class, new GuiBreakBlockAnimationPanel(this.mc));
            this.panels.put(ItemUseAction.class, new GuiItemUseActionPanel<ItemUseAction>(this.mc));
            this.panels.put(ItemUseBlockAction.class, new GuiItemUseBlockActionPanel(this.mc));

            MinecraftForge.EVENT_BUS.post(new ActionPanelRegisterEvent(this));
        }
    }

    @Override
    public void appear()
    {
        if (this.editor.delegate != null)
        {
            this.editor.delegate.appear();
        }
    }

    @Override
    public void close()
    {
        this.save();
    }

    private void save()
    {
        if (this.editor.delegate != null && this.record != null)
        {
            Action old = this.editor.delegate.action;

            Dispatcher.sendToServer(new PacketAction(this.record.filename, this.selector.tick, this.selector.index, old));
        }
    }

    public void addRecords(List<String> records)
    {
        this.records.add(records);
    }

    public void selectRecord(String str)
    {
        this.save();
        Dispatcher.sendToServer(new PacketRequestAction(str));
    }

    public void selectAction(Action action)
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

    public void moveTo(int tick)
    {
        if (tick < 0 || tick >= this.record.actions.size())
        {
            return;
        }

        Action action = this.record.getAction(this.selector.tick, this.selector.index);

        this.removeAction();
        this.record.addAction(tick, action);
        this.selector.recalculateVertical();
        this.selector.tick = tick;
        this.selector.index = this.record.actions.get(tick).size() - 1;
        this.editor.setDelegate(getPanel(action));
        Dispatcher.sendToServer(new PacketAction(this.record.filename, tick, -1, action, true));
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

        this.dashboard.morphs.updateRect(this.area.x, this.area.y, this.area.w, this.area.h);
        this.dashboard.morphs.initGui();
    }

    @Override
    public boolean handleMouseInput(int mouseX, int mouseY) throws IOException
    {
        boolean result = !this.dashboard.morphs.isHidden() && this.dashboard.morphs.isInside(mouseX, mouseY);

        this.dashboard.morphs.handleMouseInput();

        return result;
    }

    @Override
    public boolean handleKeyboardInput() throws IOException
    {
        this.dashboard.morphs.handleKeyboardInput();

        return !this.dashboard.morphs.isHidden();
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(tooltip, mouseX, mouseY, partialTicks);

        if (this.record == null)
        {
            this.drawCenteredString(this.font, I18n.format("blockbuster.gui.record_editor.not_selected"), this.area.getX(0.5F), this.area.getY(0.5F) - 6, 0xffffff);
        }

        this.dashboard.morphs.drawScreen(mouseX, mouseY, partialTicks);
    }
}