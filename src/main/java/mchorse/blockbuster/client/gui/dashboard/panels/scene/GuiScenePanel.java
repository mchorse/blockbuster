package mchorse.blockbuster.client.gui.dashboard.panels.scene;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.client.gui.dashboard.GuiBlockbusterPanel;
import mchorse.blockbuster.common.item.ItemPlayback;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketPlaybackButton;
import mchorse.blockbuster.network.common.recording.PacketUpdatePlayerData;
import mchorse.blockbuster.network.common.scene.PacketSceneCast;
import mchorse.blockbuster.network.common.scene.PacketScenePause;
import mchorse.blockbuster.network.common.scene.PacketScenePlayback;
import mchorse.blockbuster.network.common.scene.PacketSceneRecord;
import mchorse.blockbuster.network.common.scene.PacketSceneTeleport;
import mchorse.blockbuster.recording.scene.Replay;
import mchorse.blockbuster.recording.scene.Scene;
import mchorse.blockbuster.recording.scene.SceneLocation;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPopUpModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.mclib.GuiDashboard;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.Direction;
import mchorse.mclib.utils.OpHelper;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiNestedEdit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.function.Supplier;

public class GuiScenePanel extends GuiBlockbusterPanel
{
    private GuiElement subChildren;
    private GuiDelegateElement<GuiElement> mainView;
    private GuiElement replays;
    private GuiElement replayEditor;
    private GuiElement configOptions;
    private GuiReplaySelector selector;

    /* Config fields */
    public GuiTextElement title;
    public GuiTextElement startCommand;
    public GuiTextElement stopCommand;
    public GuiToggleElement loops;

    public GuiStringListElement audio;
    public GuiTrackpadElement audioShift;
    public GuiIconElement openAudioFolder;

    /* Replay fields */
    public GuiTextElement id;
    public GuiTextElement name;
    public GuiTextElement target;
    public GuiToggleElement invincible;
    public GuiToggleElement invisible;
    public GuiToggleElement enableBurning;
    public GuiToggleElement enabled;
    public GuiToggleElement fake;
    public GuiToggleElement teleportBack;
    public GuiToggleElement renderLast;
    public GuiTrackpadElement health;

    public GuiButtonElement record;
    public GuiButtonElement rename;
    public GuiButtonElement attach;
    public GuiButtonElement camera;
    public GuiButtonElement teleport;

    public GuiLabel recordingId;
    public GuiNestedEdit pickMorph;

    public GuiSceneManager scenes;

    private SceneLocation location = new SceneLocation();
    private Replay replay;

    private IKey noneAudioTrack = IKey.lang("blockbuster.gui.director.none");

    public GuiScenePanel(Minecraft mc, GuiDashboard dashboard)
    {
        super(mc, dashboard);

        this.selector = new GuiReplaySelector(mc, (replay) -> this.setReplay(replay.get(0)));
        this.selector.flex().set(0, 0, 0, 60).relative(this).w(1, -20).y(1, -60);

        GuiElement left = new GuiElement(mc);
        GuiElement right = new GuiElement(mc);

        left.flex().relative(this).w(120).y(20).hTo(this.selector.flex()).column(5).width(100).height(20).padding(10);
        right.flex().relative(this).x(1F).y(20).w(120).hTo(this.selector.flex()).anchorX(1F).column(5).flip().width(100).height(20).padding(10);

        this.subChildren = new GuiElement(mc).noCulling();
        this.subChildren.setVisible(false);
        this.replays = new GuiElement(mc).noCulling();
        this.replayEditor = new GuiElement(mc).noCulling();
        this.replayEditor.setVisible(false);
        this.replayEditor.add(left, right);
        this.configOptions = new GuiElement(mc).noCulling();
        this.mainView = new GuiDelegateElement<GuiElement>(mc, this.replays);
        this.mainView.noCulling();

        this.add(this.subChildren);
        this.subChildren.add(this.mainView);

        /* Config options */
        this.title = new GuiTextElement(mc, 80, (str) -> this.location.getScene().title = str);
        this.startCommand = new GuiTextElement(mc, 10000, (str) -> this.location.getScene().startCommand = str);
        this.stopCommand = new GuiTextElement(mc, 10000, (str) -> this.location.getScene().stopCommand = str);
        this.loops = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.loops"), false, (b) -> this.location.getScene().loops = b.isToggled());

        this.audio = new GuiStringListElement(mc, (value) -> this.location.getScene().setAudio(value.get(0).equals(this.noneAudioTrack.get()) ? "" : value.get(0)));
        this.audio.background().tooltip(IKey.lang("blockbuster.gui.director.audio_tooltip"), Direction.RIGHT);
        this.audioShift = new GuiTrackpadElement(mc, (value) -> this.location.getScene().setAudioShift(value.intValue()));
        this.audioShift.limit(0).integer().tooltip(IKey.lang("blockbuster.gui.director.audio_shift_tooltip"));
        this.openAudioFolder = new GuiIconElement(mc, Icons.FOLDER, (b) -> GuiUtils.openFolder(ClientProxy.audio.folder.getAbsolutePath()));
        this.openAudioFolder.tooltip(IKey.lang("blockbuster.gui.director.open_audio_folder"));

        this.title.flex().set(120, 50, 0, 20).relative(this.area).w(1, -130);
        this.startCommand.flex().set(120, 90, 0, 20).relative(this.area).w(1, -130);
        this.stopCommand.flex().set(120, 130, 0, 20).relative(this.area).w(1, -130);

        this.audio.flex().relative(this).xy(10, 50).w(100).hTo(this.stopCommand.area, 1F);
        this.audioShift.flex().relative(this.audio).y(1F, 5).w(1F);
        this.openAudioFolder.flex().relative(this.audio).x(1F, -16).y(-16).wh(16, 16);

        GuiElement row = Elements.row(mc, 5, 0, 20, this.loops);

        row.flex().relative(this.stopCommand).y(25).w(1F);
        this.loops.flex().h(20);

        this.configOptions.add(this.title, this.startCommand, this.stopCommand, row, this.audio, this.audioShift, this.openAudioFolder);

        /* Replay options */
        this.id = new GuiTextElement(mc, 120, (str) ->
        {
            this.replay.id = str;

            this.updateLabel(true);
        }).filename();
        this.name = new GuiTextElement(mc, 80, (str) -> this.replay.name = str);
        this.name.tooltip(IKey.lang("blockbuster.gui.director.name_tooltip"), Direction.RIGHT);
        this.target = new GuiTextElement(mc, 80, (str) -> this.replay.target = str);
        this.target.tooltip(IKey.lang("blockbuster.gui.director.target_tooltip"), Direction.LEFT);
        this.invincible = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.invincible"), false, (b) -> this.replay.invincible = b.isToggled());
        this.invisible = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.invisible"), false, (b) -> this.replay.invisible = b.isToggled());
        this.enableBurning = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.enable_burning"), true, (b) -> this.replay.enableBurning = b.isToggled());
        this.enabled = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.enabled"), false, (b) -> this.replay.enabled = b.isToggled());
        this.fake = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.fake_player"), false, (b) -> this.replay.fake = b.isToggled());
        this.teleportBack = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.tp_back"), false, (b) -> this.replay.teleportBack = b.isToggled());
        this.teleportBack.tooltip(IKey.lang("blockbuster.gui.director.tp_back_tooltip"), Direction.RIGHT);
        this.renderLast = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.render_last"), false, (b) -> this.replay.renderLast = b.isToggled());
        this.renderLast.tooltip(IKey.lang("blockbuster.gui.director.render_last_tooltip"), Direction.RIGHT);
        this.health = new GuiTrackpadElement(mc, (value) -> this.replay.health = value.floatValue());
        this.health.limit(0);
        this.recordingId = Elements.label(IKey.lang("blockbuster.gui.director.id")).color(0xcccccc);

        left.add(this.recordingId, this.id);
        left.add(Elements.label(IKey.lang("blockbuster.gui.director.name")).color(0xcccccc), this.name);
        left.add(Elements.label(IKey.lang("blockbuster.gui.director.health")).color(0xcccccc), this.health, this.invincible, this.invisible, this.enableBurning, this.enabled, this.fake, this.teleportBack, this.renderLast);
        this.replays.add(this.selector, this.replayEditor);

        /* Toggle view button */
        GuiIconElement toggle = new GuiIconElement(mc, Icons.GEAR, (b) ->
        {
            this.mainView.setDelegate(this.mainView.delegate == this.configOptions ? this.replays : this.configOptions);
        });

        GuiIconElement toggleScenes = new GuiIconElement(mc, Icons.MORE, (b) -> this.scenes.toggleVisible());
        toggleScenes.flex().y(4).relative(this.area).x(1, -24);

        toggle.tooltip(IKey.lang("blockbuster.gui.director.config"), Direction.LEFT);
        toggle.flex().y(4).relative(this.area).x(1, -44);

        this.add(toggleScenes);
        this.subChildren.add(toggle);

        /* Add, duplicate and remove replay buttons */
        GuiIconElement add = new GuiIconElement(mc, Icons.ADD, (b) -> this.addReplay());
        GuiIconElement dupe = new GuiIconElement(mc, Icons.DUPE, (b) -> this.dupeReplay());
        GuiIconElement remove = new GuiIconElement(mc, Icons.REMOVE, (b) -> this.removeReplay());

        add.tooltip(IKey.lang("blockbuster.gui.director.add_replay"), Direction.LEFT);
        dupe.tooltip(IKey.lang("blockbuster.gui.director.dupe_replay"), Direction.LEFT);
        remove.tooltip(IKey.lang("blockbuster.gui.director.remove_replay"), Direction.LEFT);

        add.flex().set(0, 0, 20, 20).relative(this.selector.resizer()).x(1F);
        dupe.flex().set(0, 20, 20, 20).relative(this.selector.resizer()).x(1F);
        remove.flex().set(0, 40, 20, 20).relative(this.selector.resizer()).x(1F);

        this.replays.add(add, dupe, remove);

        /* Additional utility buttons */
        IKey category = IKey.lang("blockbuster.gui.director.keys.category");
        Supplier<Boolean> active = () -> this.replay != null;

        this.pickMorph = new GuiNestedEdit(mc, (editing) -> ClientProxy.panels.addMorphs(this, editing, this.replay.morph));
        this.record = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.record"), (b) -> this.sendRecordMessage());
        GuiButtonElement edit = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.director.edit_record"), (b) -> this.openRecordEditor());
        GuiButtonElement update = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.director.update_data"), (b) -> this.updatePlayerData());
        this.rename = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.director.rename_prefix"), (b) -> this.renamePrefix());
        this.attach = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.director.attach"), (b) -> this.attach());
        this.teleport = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.director.tp"), (b) -> this.teleport());
        this.teleport.keys().register(this.teleport.label, Keyboard.KEY_T, () -> this.teleport.clickItself(GuiBase.getCurrent())).category(category).active(active);

        this.pickMorph.flex().relative(this.selector).x(0.5F).y(-10).w(100).anchor(0.5F, 1F);

        update.tooltip(IKey.lang("blockbuster.gui.director.update_data_tooltip"), Direction.LEFT);
        this.rename.tooltip(IKey.lang("blockbuster.gui.director.rename_prefix_tooltip"), Direction.LEFT);
        this.attach.tooltip(IKey.lang("blockbuster.gui.director.attach_tooltip"), Direction.LEFT);
        this.teleport.tooltip(IKey.lang("blockbuster.gui.director.tp_tooltip"), Direction.LEFT);

        right.add(this.attach, this.record, update, this.rename, edit);

        if (CameraHandler.isApertureLoaded())
        {
            this.camera = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.director.camera"), (b) ->
            {
                CameraHandler.location = this.location;
                CameraHandler.openCameraEditor();
            });
            this.camera.keys().register(this.camera.label, Keyboard.KEY_C, () -> this.camera.clickItself(GuiBase.getCurrent())).category(category).active(active);

            right.add(this.camera);
        }

        right.add(this.teleport);
        right.add(Elements.label(IKey.lang("blockbuster.gui.director.target")).color(0xcccccc).marginTop(12), this.target);
        this.replayEditor.add(this.pickMorph);

        /* Scene manager */
        this.add(this.scenes = new GuiSceneManager(mc, this));
        this.scenes.flex().relative(toggleScenes).xy(1F, 1F).w(160).hTo(this.selector.flex()).anchorX(1F);
        this.scenes.setVisible(false);

        this.keys().register(IKey.lang("blockbuster.gui.director.keys.toggle_list"), Keyboard.KEY_N, () -> toggleScenes.clickItself(GuiBase.getCurrent())).category(category);
        this.keys().register(IKey.lang("blockbuster.gui.director.keys.toggle_options"), Keyboard.KEY_O, () -> toggle.clickItself(GuiBase.getCurrent())).category(category);
    }

    public SceneLocation getLocation()
    {
        return this.location;
    }

    public Replay getReplay()
    {
        return this.replay;
    }

    public List<Replay> getReplays()
    {
        if (this.location.isEmpty())
        {
            return null;
        }

        return this.getLocation().getScene().replays;
    }

    public GuiScenePanel openScene(SceneLocation location)
    {
        this.scenes.setVisible(false);

        return this.setScene(location);
    }

    public GuiScenePanel setScene(SceneLocation location)
    {
        this.location = location == null ? new SceneLocation() : location;

        this.subChildren.setVisible(!location.isEmpty());
        this.replayEditor.setVisible(!location.isEmpty());
        this.scenes.setScene(location.getScene());

        if (location.isEmpty())
        {
            this.setReplay(null);

            return this;
        }

        this.selector.setList(location.getScene().replays);

        if (!this.location.getScene().replays.isEmpty())
        {
            int current = this.location.getScene().replays.indexOf(this.replay);

            this.setReplay(this.location.getScene().replays.get(current == -1 ? 0 : current));
        }
        else
        {
            this.setReplay(null);
        }

        this.fillData();

        return this;
    }

    public GuiScenePanel set(SceneLocation location)
    {
        this.location = location;
        this.scenes.setScene(location.getScene());

        return this;
    }

    @Override
    public void appear()
    {
        super.appear();

        ClientProxy.panels.picker(this::setMorph);

        if (!this.location.isEmpty())
        {
            this.setScene(this.location);
        }
    }

    @Override
    public void open()
    {
        if (!OpHelper.isPlayerOp())
        {
            return;
        }

        ClientProxy.panels.morphs.reload();

        this.setScene(this.location);
        this.scenes.setScene(this.location.getScene());
        this.scenes.updateSceneList();
    }

    @Override
    public void close()
    {
        if (!OpHelper.isPlayerOp())
        {
            return;
        }

        if (this.location.isScene())
        {
            if (ClientProxy.panels.morphs.hasParent())
            {
                ClientProxy.panels.morphs.finish();
            }

            Dispatcher.sendToServer(new PacketSceneCast(this.location));
        }
    }

    private void setReplay(Replay replay)
    {
        if (this.replay != null)
        {
            this.replay.morph = MorphUtils.copy(this.replay.morph);
        }

        this.replay = replay;
        this.replayEditor.setVisible(this.replay != null);
        this.mainView.setDelegate(this.replays);
        this.selector.setCurrent(replay);
        this.fillReplayData();
    }

    private void fillData()
    {
        this.title.setText(this.location.getScene().title);
        this.startCommand.setText(this.location.getScene().startCommand);
        this.stopCommand.setText(this.location.getScene().stopCommand);
        this.loops.toggled(this.location.getScene().loops);
        this.attach.setEnabled(false);

        this.audio.clear();
        this.audio.add(this.noneAudioTrack.get());
        this.audio.add(ClientProxy.audio.getFileNames());
        this.audio.sort();

        String audio = this.location.getScene().getAudio();

        this.audio.setCurrentScroll(audio == null || audio.isEmpty() ? this.noneAudioTrack.get() : audio);

        this.audioShift.setValue(this.location.getScene().getAudioShift());

        if (this.mc != null && this.mc.player != null)
        {
            ItemStack stack = this.mc.player.getHeldItemMainhand();

            this.attach.setEnabled(!this.location.isEmpty() && stack.getItem() instanceof ItemPlayback);
        }
    }

    private void fillReplayData()
    {
        if (this.replay == null)
        {
            return;
        }

        this.id.setText(this.replay.id);
        this.name.setText(this.replay.name);
        this.target.setText(this.replay.target);
        this.invincible.toggled(this.replay.invincible);
        this.invisible.toggled(this.replay.invisible);
        this.enableBurning.toggled(this.replay.enableBurning);
        this.enabled.toggled(this.replay.enabled);
        this.fake.toggled(this.replay.fake);
        this.teleportBack.toggled(this.replay.teleportBack);
        this.renderLast.toggled(this.replay.renderLast);
        this.health.setValue(this.replay.health);
        this.pickMorph.setMorph(this.replay.morph);

        this.selector.setCurrent(this.replay);
        this.updateLabel(false);
    }

    /**
     * Add an empty replay 
     */
    private void addReplay()
    {
        Replay replay = new Replay("");

        if (this.location.isScene())
        {
            replay.id = this.location.getScene().getNextBaseSuffix(this.location.getScene().getId());
        }

        this.location.getScene().replays.add(replay);
        this.setReplay(replay);
        this.selector.update();
    }

    /**
     * Duplicate a replay 
     */
    private void dupeReplay()
    {
        if (this.selector.isDeselected())
        {
            return;
        }

        Scene scene = this.location.getScene();

        if (scene.dupe(scene.replays.indexOf(this.replay)))
        {
            this.selector.update();
            this.selector.scroll.scrollTo(this.selector.getIndex() * this.selector.scroll.scrollItemSize);
            this.setReplay(scene.replays.get(scene.replays.size() - 1));
        }
    }

    /**
     * Remove replay 
     */
    private void removeReplay()
    {
        if (this.selector.isDeselected())
        {
            return;
        }

        Scene scene = this.location.getScene();
        int index = this.selector.getIndex();

        scene.replays.remove(this.replay);

        int size = scene.replays.size();
        index = MathHelper.clamp(index, 0, size - 1);

        this.setReplay(size == 0 ? null : scene.replays.get(index));
        this.selector.update();
    }

    private void setMorph(AbstractMorph morph)
    {
        if (this.replay != null)
        {
            this.replay.morph = morph;
        }

        this.pickMorph.setMorph(morph);
    }

    /**
     * update the labels
     * @param gui true when this is called by a callback from a GuiElement
     */
    private void updateLabel(boolean gui)
    {
        boolean error = this.replay != null && this.replay.id.isEmpty();

        this.recordingId.color(error ? 0xff3355 : 0xcccccc);

        if (this.replay != null && !this.replay.id.isEmpty() && gui)
        {
            boolean isDuplicate = false;

            for (Replay element : this.scenes.parent.getLocation().getScene().replays)
            {
                if (element.id.equals(this.replay.id) && this.replay != element)
                {
                    isDuplicate = true;

                    break;
                }
            }

            if (isDuplicate)
            {
                GuiModal.addModal(this, () ->
                {
                    GuiPopUpModal modal = new GuiPopUpModal(this.mc, IKey.lang("blockbuster.gui.director.rename_replay_dupe_modal"));
                    modal.flex().relative(this.parent).wh(220, 50);

                    return modal;
                });
            }
        }
    }

    /**
     * Send record message to the player
     */
    private void sendRecordMessage()
    {
        EntityPlayer player = this.mc.player;

        if (this.replay.id.isEmpty())
        {
            Blockbuster.l10n.error(player, "recording.fill_filename");

            return;
        }

        String command = "/action record " + this.replay.id + " " + this.location.getFilename();
        ITextComponent component = new TextComponentString(I18n.format("blockbuster.info.recording.clickhere"));

        component.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        component.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(command)));
        component.getStyle().setColor(TextFormatting.GRAY).setUnderlined(true);

        Blockbuster.l10n.info(player, "recording.message", this.replay.id, component);

        /* Add the command to the history */
        List<String> messages = this.mc.ingameGUI.getChatGUI().getSentMessages();

        boolean empty = messages.isEmpty();
        boolean lastMessageIsntCommand = !empty && !messages.get(messages.size() - 1).equals(command);

        if (lastMessageIsntCommand || empty)
        {
            messages.add(command);
        }
    }

    private void attach()
    {
        if (CameraHandler.isApertureLoaded())
        {
            CameraHandler.attach(this.location, this.scenes.sceneList.getList());
        }
        else
        {
            Dispatcher.sendToServer(new PacketPlaybackButton(this.location, 0, ""));

            this.mc.displayGuiScreen(null);
        }
    }

    private void openRecordEditor()
    {
        if (this.replay != null && !this.replay.id.isEmpty())
        {
            this.dashboard.panels.setPanel(ClientProxy.panels.recordingEditorPanel);
            ClientProxy.panels.recordingEditorPanel.selectRecord(this.replay.id);
            ClientProxy.panels.recordingEditorPanel.records.setVisible(false);
        }
    }

    private void updatePlayerData()
    {
        Dispatcher.sendToServer(new PacketUpdatePlayerData(this.replay.id));
    }

    private void teleport()
    {
        if (this.replay == null)
        {
            return;
        }

        this.mc.displayGuiScreen(null);
        Dispatcher.sendToServer(new PacketSceneTeleport(this.replay.id));
    }

    private void renamePrefix()
    {
        GuiModal.addModal(this.replayEditor, () ->
        {
            GuiPromptModal modal = new GuiPromptModal(this.mc, IKey.lang("blockbuster.gui.director.rename_prefix_popup"), this::renamePrefix);

            modal.markIgnored().flex().relative(this.rename).y(1F).w(1F).h(120);

            return modal;
        });
    }

    private void renamePrefix(String newPrefix)
    {
        this.location.getScene().renamePrefix(newPrefix);
        this.fillReplayData();
    }

    @Override
    public void draw(GuiContext context)
    {
        if (this.scenes.isVisible())
        {
            int x = this.scenes.area.ex() - 20;
            int y = this.scenes.area.y - 20;

            Gui.drawRect(x, y, x + 20, y + 20, ColorUtils.HALF_BLACK);
        }

        /* Draw additional stuff */
        if (this.mainView.delegate == this.replays)
        {
            Gui.drawRect(this.selector.area.x, this.selector.area.y, this.selector.area.ex() + 20, this.selector.area.ey(), ColorUtils.HALF_BLACK);
            this.drawGradientRect(this.selector.area.x, this.selector.area.y - 16, this.selector.area.ex() + 20, this.selector.area.y, 0, ColorUtils.HALF_BLACK);

            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.scenes.title"), this.area.x + 10, this.area.y + 10, 0xffffff);

            if (this.replay != null)
            {
                AbstractMorph morph = this.replay.morph;

                if (morph != null)
                {
                    int x = this.area.mx();
                    int y = this.area.y(0.55F);

                    GuiDraw.scissor(this.area.x, this.area.y, this.area.w, this.area.h, context);
                    morph.renderOnScreen(this.mc.player, x, y, this.area.h / 3.5F, 1.0F);
                    GuiDraw.unscissor(context);
                }
            }
        }
        else
        {
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.config"), this.area.x + 10, this.area.y + 10, 0xffffff);

            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.audio"), this.audio.area.x, this.audio.area.y - 12, 0xcccccc);
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.start_command"), this.startCommand.area.x, this.startCommand.area.y - 12, 0xcccccc);
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.stop_command"), this.stopCommand.area.x, this.stopCommand.area.y - 12, 0xcccccc);
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.display_title"), this.title.area.x, this.title.area.y - 12, 0xcccccc);
        }

        if (this.location.isEmpty())
        {
            String no = I18n.format("blockbuster.gui.director.not_selected");

            this.drawCenteredString(this.font, no, this.area.mx(), this.area.my() - 6, 0xffffff);
        }

        super.draw(context);
    }

    public void plause()
    {
        if (!OpHelper.isPlayerOp())
        {
            return;
        }

        if (this.location.isScene())
        {
            Dispatcher.sendToServer(new PacketScenePlayback(this.location));
        }
    }

    public void record()
    {
        if (!OpHelper.isPlayerOp())
        {
            return;
        }

        Replay replay = this.replay;

        if (replay != null && !replay.id.isEmpty() && this.location.isScene())
        {
            Dispatcher.sendToServer(new PacketSceneRecord(this.location, replay.id));
        }
    }

    public void pause()
    {
        if (!OpHelper.isPlayerOp())
        {
            return;
        }

        if (this.location.isScene())
        {
            Dispatcher.sendToServer(new PacketScenePause(this.location));
        }
    }
}