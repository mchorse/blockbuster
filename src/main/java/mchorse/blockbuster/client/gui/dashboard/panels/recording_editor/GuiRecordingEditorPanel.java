package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.client.gui.dashboard.GuiBlockbusterPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions.*;
import mchorse.blockbuster.events.ActionPanelRegisterEvent;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.recording.actions.PacketRequestAction;
import mchorse.blockbuster.network.common.recording.actions.PacketRequestActions;
import mchorse.blockbuster.network.common.scene.PacketSceneRecord;
import mchorse.blockbuster.network.common.scene.sync.PacketScenePlay;
import mchorse.blockbuster.network.server.recording.actions.ServerHandlerActionsChange;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.actions.*;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.recording.scene.SceneLocation;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiLabelSearchListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.mclib.GuiDashboard;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.Label;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiRecordingEditorPanel extends GuiBlockbusterPanel
{
    /**
     * A map of action editing panels mapped to their classes  
     */
    public Map<Class<? extends Action>, GuiActionPanel<? extends Action>> panels = new HashMap<Class<? extends Action>, GuiActionPanel<? extends Action>>();

    public GuiRecordList records;
    public GuiRecordTimeline timeline;
    public GuiDelegateElement<GuiActionPanel<? extends Action>> actionEditor;

    public GuiIconElement add;
    public GuiIconElement dupe;
    public GuiIconElement remove;
    public GuiIconElement capture;

    public GuiIconElement cut;
    public GuiIconElement copy;
    public GuiIconElement paste;
    public GuiIconElement teleport;

    public GuiIconElement open;
    public GuiLabelSearchListElement<String> list;

    public Record record;
    public NBTTagCompound buffer;

    public GuiRecordingEditorPanel(Minecraft mc, GuiDashboard dashboard)
    {
        super(mc, dashboard);

        this.timeline = new GuiRecordTimeline(mc, this);
        this.timeline.setVisible(false);
        this.records = new GuiRecordList(mc, this);
        this.actionEditor = new GuiDelegateElement<GuiActionPanel<? extends Action>>(mc, null);

        /* Add/remove */
        this.add = new GuiIconElement(mc, Icons.ADD, (b) -> this.list.toggleVisible());
        this.add.tooltip(IKey.lang("blockbuster.gui.add"), Direction.LEFT);
        this.dupe = new GuiIconElement(mc, Icons.DUPE, (b) -> this.timeline.dupeActions());
        this.dupe.tooltip(IKey.lang("blockbuster.gui.duplicate"), Direction.LEFT);
        this.remove = new GuiIconElement(mc, Icons.REMOVE, (b) -> this.timeline.removeActions());
        this.remove.tooltip(IKey.lang("blockbuster.gui.remove"), Direction.LEFT);
        this.capture = new GuiIconElement(mc, Icons.SPHERE, (b) -> this.capture());
        this.capture.tooltip(IKey.lang("blockbuster.gui.record_editor.capture"), Direction.LEFT);

        this.cut = new GuiIconElement(mc, Icons.CUT, (icon) -> this.timeline.cutActions());
        this.cut.tooltip(IKey.lang("blockbuster.gui.record_editor.cut"), Direction.RIGHT);
        this.copy = new GuiIconElement(mc, Icons.COPY, (icon) -> this.timeline.copyActions());
        this.copy.tooltip(IKey.lang("blockbuster.gui.record_editor.copy"), Direction.RIGHT);
        this.paste = new GuiIconElement(mc, Icons.PASTE, (b) -> this.timeline.pasteActions());
        this.paste.tooltip(IKey.lang("blockbuster.gui.record_editor.paste"), Direction.RIGHT);
        this.teleport = new GuiIconElement(mc, Icons.MOVE_TO, (b) -> this.teleport());
        this.teleport.tooltip(IKey.lang("blockbuster.gui.record_editor.teleport"), Direction.RIGHT);

        this.list = new GuiLabelSearchListElement<String>(mc, (str) ->
        {
            this.timeline.createAction(str.get(0).value);
            this.list.setVisible(false);
        });
        this.list.label(IKey.lang("blockbuster.gui.search"));
        this.list.list.background();

        for (String key : ActionRegistry.NAME_TO_CLASS.keySet())
        {
            IKey title = IKey.lang("blockbuster.gui.record_editor.actions." + key + ".title");

            this.list.list.add(new Label<String>(title, key));
        }

        this.list.filter("", false);

        this.add.flex().relative(this.timeline).x(1F);
        this.dupe.flex().relative(this.add.resizer()).y(20);
        this.remove.flex().relative(this.dupe.resizer()).y(20);
        this.capture.flex().relative(this.remove.resizer()).y(20);
        this.cut.flex().relative(this.timeline).x(-20);
        this.copy.flex().relative(this.cut.resizer()).y(20);
        this.paste.flex().relative(this.copy.resizer()).y(20);
        this.teleport.flex().relative(this.paste.resizer()).y(20);
        this.list.flex().set(0, 0, 80, 80).relative(this.timeline.area).x(1, -80);

        this.open = new GuiIconElement(mc, Icons.MORE, (b) -> this.records.toggleVisible());
        this.open.flex().relative(this.area).set(0, 2, 24, 24).x(1, -28);

        this.add(this.open);
        this.timeline.add(this.add, this.dupe, this.remove, this.capture, this.cut, this.copy, this.paste, this.teleport, this.list);

        IKey category = IKey.lang("blockbuster.gui.aperture.keys.category");

        this.timeline.keys().register(IKey.lang("blockbuster.gui.record_editor.capture"), Keyboard.KEY_R, this::capture)
            .held(Keyboard.KEY_LCONTROL).category(category);
        this.timeline.keys().register(IKey.lang("blockbuster.gui.record_editor.teleport"), Keyboard.KEY_T, this::teleport)
            .held(Keyboard.KEY_LCONTROL).category(category);
        this.keys().register(IKey.lang("blockbuster.gui.aperture.keys.toggle_list"), Keyboard.KEY_L, () -> this.open.clickItself(GuiBase.getCurrent()))
            .held(Keyboard.KEY_LCONTROL).category(category);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public GuiActionPanel<? extends Action> getActionPanel(Action action)
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

    private void capture()
    {
        if (this.record == null)
        {
            return;
        }

        int offset = this.getOffset();

        if (offset >= 0)
        {
            SceneLocation scene = CameraHandler.isCameraEditorOpen() ? CameraHandler.get() : null;

            CameraHandler.closeScreenOrCameraEditor();

            if (scene != null)
            {
                Dispatcher.sendToServer(new PacketScenePlay(scene, PacketScenePlay.STOP, 0));
            }

            Dispatcher.sendToServer(new PacketSceneRecord(scene, this.record.filename, offset));
        }
    }

    private void teleport()
    {
        if (this.record == null)
        {
            return;
        }

        int offset = this.getOffset();

        if (offset >= 0)
        {
            SceneLocation scene = CameraHandler.get();

            if (scene != null)
            {
                Dispatcher.sendToServer(new PacketScenePlay(scene, PacketScenePlay.STOP, 0));
            }

            GuiBase.getCurrent().screen.closeThisScreen();

            RecordUtils.applyFrameOnEntity(Minecraft.getMinecraft().player, this.record, offset - record.preDelay);
        }
    }

    private int getOffset()
    {
        return this.timeline.getCurrentTick();
    }

    @Override
    public void open()
    {
        this.records.clear();
        Dispatcher.sendToServer(new PacketRequestActions());

        this.timeline.removeFromParent();
        this.timeline.flex().reset().relative(this.area).x(20).y(1F, -80).h(80).w(1F, -40);
        this.actionEditor.removeFromParent();
        this.actionEditor.flex().reset().relative(this.area).w(1F).h(1, -80);
        this.records.removeFromParent();
        this.records.flex().reset().relative(this).w(120).x(1, -120).hTo(this.timeline.flex());

        this.prepend(this.records);
        this.add(this.actionEditor, this.timeline);

        this.updateEditorWidth();

        if (this.record != null && this.record != ClientProxy.manager.records.get(this.record.filename))
        {
            this.selectRecord(this.record.filename);
        }

        if (this.panels.isEmpty())
        {
            GuiEmptyActionPanel empty = new GuiEmptyActionPanel(this.mc, this);

            this.panels.put(Action.class, empty);
            this.panels.put(ChatAction.class, new GuiChatActionPanel(this.mc, this));
            this.panels.put(DropAction.class, new GuiDropActionPanel(this.mc, this));
            this.panels.put(EquipAction.class, new GuiEquipActionPanel(this.mc, this));
            this.panels.put(HotbarChangeAction.class, new GuiHotbarChangeActionPanel(this.mc, this));
            this.panels.put(ShootArrowAction.class, new GuiShootArrowActionPanel(this.mc, this));
            this.panels.put(PlaceBlockAction.class, new GuiPlaceBlockActionPanel(this.mc, this));
            this.panels.put(MountingAction.class, new GuiMountingActionPanel(this.mc, this));
            this.panels.put(InteractBlockAction.class, new GuiBlockActionPanel<InteractBlockAction>(this.mc, this));
            this.panels.put(BreakBlockAction.class, new GuiBreakBlockActionPanel(this.mc, this));
            this.panels.put(MorphAction.class, new GuiMorphActionPanel(this.mc, this));
            this.panels.put(AttackAction.class, new GuiDamageActionPanel(this.mc, this));
            this.panels.put(DamageAction.class, new GuiDamageActionPanel(this.mc, this));
            this.panels.put(CommandAction.class, new GuiCommandActionPanel(this.mc, this));
            this.panels.put(BreakBlockAnimation.class, new GuiBreakBlockAnimationPanel(this.mc, this));
            this.panels.put(ItemUseAction.class, new GuiItemUseActionPanel<ItemUseAction>(this.mc, this));
            this.panels.put(ItemUseBlockAction.class, new GuiItemUseBlockActionPanel(this.mc, this));
            this.panels.put(InteractEntityAction.class, new GuiInteractEntityActionPanel(this.mc, this));

            MinecraftForge.EVENT_BUS.post(new ActionPanelRegisterEvent(this));
        }
    }

    @Override
    public void appear()
    {
        super.appear();

        ClientProxy.panels.picker((morph) ->
        {
            if (this.actionEditor.delegate != null)
            {
                this.actionEditor.delegate.setMorph(morph);
            }
        });
    }

    @Override
    public void close()
    {
        this.saveAction();
    }

    public void saveAction()
    {
        if (this.actionEditor.delegate != null && this.record != null)
        {
            this.actionEditor.delegate.disappear();

            if (this.actionEditor.delegate.action != null)
            {
                /* so this method is independent from the current tick and index of GuiRecordTimeline */
                int[] found = this.record.findAction(this.actionEditor.delegate.action);

                if (found[0] != -1 && found[1] != -1)
                {
                    /* save the old action */
                    ServerHandlerActionsChange.editAction(this.actionEditor.delegate.action, this.record, found[0], found[1]);
                }
            }
            //TODO out of scope for this method to reset delegate GUI
            this.actionEditor.delegate = null;
        }
    }

    public void addRecords(List<String> records)
    {
        this.records.add(records);

        if (this.record != null)
        {
            this.records.records.list.setCurrent(this.record.filename);
        }
    }

    /**
     * Select the record by the filename - request actions from the server
     * @param str
     */
    public void selectRecord(String str)
    {
        //this.timeline.reset();
        this.saveAction();
        Dispatcher.sendToServer(new PacketRequestAction(str, true));
    }

    //TODO This needs refactoring... data flow is not clear and ClientHandlerActions receiver shouldn't control GUI...
    /**
     * When the server sends back actions after the request {@link #selectRecord(String)} - select the recording
     * @param record
     */
    public void selectRecord(Record record)
    {
        this.record = record;
        this.timeline.setVisible(record != null);
        this.timeline.reset();
        this.setDelegate(null);
        this.list.setVisible(false);
    }

    public void reselectRecord(Record record)
    {
        if (this.record != null && this.record.filename.equals(record.filename))
        {
            this.record.preDelay = record.preDelay;
            this.record.postDelay = record.postDelay;
        }
    }

    public void selectAction(Action action)
    {
        this.setDelegate(this.getActionPanel(action));
    }

    public void updateEditorWidth()
    {
        if (this.records.isVisible())
        {
            this.actionEditor.flex().wTo(this.records.area);
        }
        else
        {
            this.actionEditor.flex().w(1F);
        }

        this.actionEditor.resize();
    }

    public void setDelegate(GuiActionPanel<? extends Action> panel)
    {
        if (this.actionEditor.delegate != null)
        {
            this.actionEditor.delegate.disappear();
        }

        this.actionEditor.setDelegate(panel);
    }

    @Override
    public void draw(GuiContext context)
    {
        if (this.record == null)
        {
            this.drawCenteredString(this.font, I18n.format("blockbuster.gui.record_editor.not_selected"), this.area.mx(), this.area.my() - 6, 0xffffff);
        }

        super.draw(context);
    }
}