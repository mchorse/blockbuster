package mchorse.blockbuster.client.gui.dashboard.panels.director;

import javafx.scene.Camera;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.network.common.PacketPlaybackButton;
import mchorse.blockbuster.client.gui.dashboard.GuiBlockbusterPanel;
import mchorse.blockbuster.common.item.ItemPlayback;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.recording.PacketUpdatePlayerData;
import mchorse.blockbuster.network.common.scene.PacketSceneCast;
import mchorse.blockbuster.network.common.scene.PacketScenePause;
import mchorse.blockbuster.network.common.scene.PacketScenePlayback;
import mchorse.blockbuster.network.common.scene.PacketSceneRecord;
import mchorse.blockbuster.network.common.scene.PacketSceneRequestCast;
import mchorse.blockbuster.recording.scene.Replay;
import mchorse.blockbuster.recording.scene.Scene;
import mchorse.blockbuster.recording.scene.SceneLocation;
import mchorse.blockbuster.utils.L10n;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.mclib.GuiDashboard;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiNestedEdit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GuiDirectorPanel extends GuiBlockbusterPanel
{
    public static final List<BlockPos> lastBlocks = new ArrayList<BlockPos>();
    public static final Pattern RECORDING_ID = Pattern.compile("^[\\w,\\-_]*$");

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
    public GuiToggleElement disableStates;
    public GuiToggleElement hide;

    /* Replay fields */
    public GuiTextElement id;
    public GuiTextElement name;
    public GuiToggleElement invincible;
    public GuiToggleElement invisible;
    public GuiToggleElement enabled;
    public GuiToggleElement fake;
    public GuiToggleElement teleportBack;
    public GuiTrackpadElement health;
    public GuiButtonElement record;
    public GuiButtonElement rename;
    public GuiButtonElement attach;
    public GuiButtonElement camera;

    public GuiLabel recordingId;
    public GuiNestedEdit pickMorph;

    public GuiSceneManager scenes;

    private SceneLocation location = new SceneLocation();
    private Replay replay;

    /**
     * Try adding a block position, if it doesn't exist in list already 
     */
    public static void tryAddingBlock(BlockPos pos)
    {
        for (BlockPos stored : lastBlocks)
        {
            if (pos.equals(stored))
            {
                return;
            }
        }

        lastBlocks.add(pos);
    }

    public GuiDirectorPanel(Minecraft mc, GuiDashboard dashboard)
    {
        super(mc, dashboard);

        this.selector = new GuiReplaySelector(mc, (replay) -> this.setReplay(replay.get(0)));
        this.selector.flex().set(0, 0, 0, 60).relative(this).w(1, -20).y(1, -60);

        GuiElement left = new GuiElement(mc);
        GuiElement right = new GuiElement(mc);

        left.flex().relative(this).w(120).y(20).hTo(this.selector.flex()).column(5).width(100).height(20).padding(10);
        right.flex().relative(this).x(1F).y(20).w(120).hTo(this.selector.flex()).anchorX(1F).column(5).width(100).height(20).padding(10);

        this.subChildren = new GuiElement(mc);
        this.subChildren.setVisible(false);
        this.replays = new GuiElement(mc);
        this.replayEditor = new GuiElement(mc);
        this.replayEditor.setVisible(false);
        this.replayEditor.add(left, right);
        this.configOptions = new GuiElement(mc);
        this.mainView = new GuiDelegateElement<GuiElement>(mc, this.replays);

        this.add(this.subChildren);
        this.subChildren.add(this.mainView);

        /* Config options */
        this.title = new GuiTextElement(mc, 80, (str) -> this.location.getScene().title = str);
        this.startCommand = new GuiTextElement(mc, 10000, (str) -> this.location.getScene().startCommand = str);
        this.stopCommand = new GuiTextElement(mc, 10000, (str) -> this.location.getScene().stopCommand = str);
        this.loops = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.loops"), false, (b) -> this.location.getScene().loops = b.isToggled());
        this.disableStates = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.disable_states"), false, (b) -> this.location.getDirector().disableStates = b.isToggled());
        this.hide = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.hide"), false, (b) -> this.location.getDirector().hide = b.isToggled());

        this.title.flex().set(10, 50, 0, 20).relative(this.area).w(1, -20);
        this.startCommand.flex().set(10, 90, 0, 20).relative(this.area).w(1, -20);
        this.stopCommand.flex().set(10, 130, 0, 20).relative(this.area).w(1, -20);
        this.loops.flex().set(0, 30, 60, 11).relative(this.stopCommand.resizer());
        this.disableStates.flex().set(0, 16, 60, 11).relative(this.loops.resizer());
        this.hide.flex().set(0, 16, 60, 11).relative(this.disableStates.resizer());

        this.configOptions.add(this.title, this.startCommand, this.stopCommand, this.loops, this.disableStates, this.hide);

        /* Replay options */
        this.id = new GuiTextElement(mc, 120, (str) ->
        {
            this.replay.id = str;
            this.updateLabel();
        });
        this.id.field.setValidator((str) -> RECORDING_ID.matcher(str).matches());
        this.name = new GuiTextElement(mc, 80, (str) -> this.replay.name = str);
        this.invincible = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.invincible"), false, (b) -> this.replay.invincible = b.isToggled());
        this.invisible = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.invisible"), false, (b) -> this.replay.invisible = b.isToggled());
        this.enabled = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.enabled"), false, (b) -> this.replay.enabled = b.isToggled());
        this.fake = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.fake_player"), false, (b) -> this.replay.fake = b.isToggled());
        this.teleportBack = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.tp_back"), false, (b) -> this.replay.teleportBack = b.isToggled());
        this.teleportBack.tooltip(IKey.lang("blockbuster.gui.director.tp_back_tooltip"), Direction.RIGHT);
        this.health = new GuiTrackpadElement(mc, (value) -> this.replay.health = value);
        this.health.limit(0);
        this.recordingId = Elements.label(IKey.lang("blockbuster.gui.director.id")).color(0xcccccc);

        left.add(this.recordingId, this.id);
        left.add(Elements.label(IKey.lang("blockbuster.gui.director.name")).color(0xcccccc), this.name);
        left.add(Elements.label(IKey.lang("blockbuster.gui.director.health")).color(0xcccccc), this.health, this.invincible, this.invisible, this.enabled, this.fake, this.teleportBack);
        this.replays.add(this.replayEditor, this.selector);

        /* Toggle view button */
        GuiIconElement toggle = new GuiIconElement(mc, Icons.GEAR, (b) ->
        {
            this.mainView.setDelegate(this.mainView.delegate == this.configOptions ? this.replays : this.configOptions);
        });

        GuiIconElement toggleScenes = new GuiIconElement(mc, Icons.MORE, (b) -> this.scenes.toggleVisible());
        toggleScenes.flex().y(4).wh(20, 20).relative(this.area).x(1, -24);

        toggle.tooltip(IKey.lang("blockbuster.gui.director.config"), Direction.LEFT);
        toggle.flex().y(4).wh(20, 20).relative(this.area).x(1, -44);

        this.add(toggleScenes);
        this.subChildren.add(toggle);

        /* Add, duplicate and remove replay buttons */
        GuiIconElement add = new GuiIconElement(mc, Icons.ADD, (b) -> this.addReplay());
        GuiIconElement dupe = new GuiIconElement(mc, Icons.DUPE, (b) -> this.dupeReplay());
        GuiIconElement remove = new GuiIconElement(mc, Icons.REMOVE, (b) -> this.removeReplay());

        add.tooltip(IKey.lang("blockbuster.gui.add"), Direction.LEFT);
        dupe.tooltip(IKey.lang("blockbuster.gui.duplicate"), Direction.LEFT);
        remove.tooltip(IKey.lang("blockbuster.gui.remove"), Direction.LEFT);

        add.flex().set(0, 0, 20, 20).relative(this.selector.resizer()).x(1F);
        dupe.flex().set(0, 20, 20, 20).relative(this.selector.resizer()).x(1F);
        remove.flex().set(0, 40, 20, 20).relative(this.selector.resizer()).x(1F);

        this.replays.add(add, dupe, remove);

        /* Additional utility buttons */
        this.pickMorph = new GuiNestedEdit(mc, (editing) -> ClientProxy.panels.addMorphs(this, editing, this.replay.morph));
        this.record = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.record"), (b) -> this.sendRecordMessage());
        GuiButtonElement edit = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.director.edit_record"), (b) -> this.openRecordEditor());
        GuiButtonElement update = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.director.update_data"), (b) -> this.updatePlayerData());
        this.rename = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.director.rename_prefix"), (b) -> this.renamePrefix());
        this.attach = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.director.attach"), (b) -> this.attach());

        this.pickMorph.flex().relative(this.selector).x(0.5F).y(-10).wh(100, 20).anchor(0.5F, 1F);

        update.tooltip(IKey.lang("blockbuster.gui.director.update_data_tooltip"), Direction.RIGHT);
        this.rename.tooltip(IKey.lang("blockbuster.gui.director.rename_prefix_tooltip"), Direction.RIGHT);
        this.attach.tooltip(IKey.lang("blockbuster.gui.director.attach_tooltip"));

        right.add(this.record, edit, update, this.rename, this.attach);

        if (CameraHandler.isApertureLoaded())
        {
            this.camera = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.director.camera"), (b) ->
            {
                CameraHandler.location = this.location;
                CameraHandler.openCameraEditor();
            });

            right.add(this.camera);
        }

        this.replayEditor.add(this.pickMorph);

        /* Scene manager */
        this.add(this.scenes = new GuiSceneManager(mc, this));
        this.scenes.flex().relative(toggleScenes).xy(1F, 1F).w(160).hTo(this.selector.flex()).anchorX(1F);
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

    public void pickDirector(BlockPos pos)
    {
        this.close();

        Dispatcher.sendToServer(new PacketSceneRequestCast(new SceneLocation(pos)));
    }

    public GuiDirectorPanel openScene(SceneLocation location)
    {
        if (location.isDirector())
        {
            tryAddingBlock(location.getPosition());
        }

        this.scenes.setVisible(false);

        return this.setScene(location);
    }

    public GuiDirectorPanel setScene(SceneLocation location)
    {
        this.location = location == null ? new SceneLocation() : location;

        this.updateList();
        this.subChildren.setVisible(!location.isEmpty());
        this.replayEditor.setVisible(!location.isEmpty());
        this.hide.setVisible(this.location.isDirector());
        this.disableStates.setVisible(this.location.isDirector());

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

    public GuiDirectorPanel set(SceneLocation location)
    {
        this.location = location;
        this.updateList();

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
        ClientProxy.panels.morphs.reload();

        this.setScene(this.location);
        this.scenes.setScene(this.location.getScene());
        this.scenes.updateSceneList();

        /* Resetting the current scene block, if it was removed from the
         * world */
        if (this.location.isDirector() && this.mc.world.getTileEntity(this.location.getPosition()) == null)
        {
            this.setScene(new SceneLocation());
        }
    }

    @Override
    public void close()
    {
        if (!this.location.isEmpty())
        {
            if (ClientProxy.panels.morphs.hasParent())
            {
                ClientProxy.panels.morphs.finish();
            }

            if (this.location.isDirector())
            {
                TileEntity te = this.mc.world.getTileEntity(this.location.getPosition());

                if (te instanceof TileEntityDirector)
                {
                    ((TileEntityDirector) te).director.copy(this.location.getDirector());
                }

                Dispatcher.sendToServer(new PacketSceneCast(this.location));
            }
            else if (this.location.isScene())
            {
                Dispatcher.sendToServer(new PacketSceneCast(this.location));
            }
        }
    }

    private void setReplay(Replay replay)
    {
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

        if (this.mc != null && this.mc.player != null)
        {
            ItemStack stack = this.mc.player.getHeldItemMainhand();

            this.attach.setEnabled(!this.location.isEmpty() && stack != null && stack.getItem() instanceof ItemPlayback);
        }

        if (this.location.isDirector())
        {
            this.disableStates.toggled(this.location.getDirector().disableStates);
            this.hide.toggled(this.location.getDirector().hide);
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
        this.invincible.toggled(this.replay.invincible);
        this.invisible.toggled(this.replay.invisible);
        this.enabled.toggled(this.replay.enabled);
        this.fake.toggled(this.replay.fake);
        this.teleportBack.toggled(this.replay.teleportBack);
        this.health.setValue(this.replay.health);
        this.pickMorph.setMorph(this.replay.morph);

        this.selector.setCurrent(this.replay);
        this.updateLabel();
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

        if (scene.dupe(scene.replays.indexOf(this.replay), true))
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

        scene.replays.remove(this.replay);

        int size = scene.replays.size();
        int index = MathHelper.clamp(this.selector.getIndex(), 0, size - 1);

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

    private void updateList()
    {
        this.scenes.setScene(this.location.getScene());
        this.scenes.updateList(lastBlocks);
    }

    private void updateLabel()
    {
        boolean error = this.replay != null && this.replay.id.isEmpty();

        this.recordingId.color(error ? 0xff3355 : 0xcccccc);
    }

    /**
     * Send record message to the player
     */
    private void sendRecordMessage()
    {
        EntityPlayer player = this.mc.player;

        if (this.replay.id.isEmpty())
        {
            L10n.error(player, "recording.fill_filename");

            return;
        }

        String command;

        if (this.location.isScene())
        {
            command = "/action record " + this.replay.id + " " + this.location.getFilename();
        }
        else
        {
            BlockPos pos = this.location.getPosition();

            command = "/action record " + this.replay.id + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ();
        }

        ITextComponent component = new TextComponentString(I18n.format("blockbuster.info.recording.clickhere"));
        component.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        component.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(command)));
        component.getStyle().setColor(TextFormatting.GRAY).setUnderlined(true);

        L10n.info(player, "recording.message", this.replay.id, component);

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
            CameraHandler.attach(this.location);
        }
        else
        {
            Dispatcher.sendToServer(new PacketPlaybackButton(0, "", this.location.getFilename(), this.location.getPosition()));

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

            Gui.drawRect(x, y, x + 20, y + 20, 0x88000000);
        }

        Gui.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.y + 24, 0x88000000);
        this.drawGradientRect(this.area.x, this.area.y + 24, this.area.ex(), this.area.y + 32, 0x88000000, 0x00000000);

        /* Draw additional stuff */
        if (this.mainView.delegate == this.replays)
        {
            Gui.drawRect(this.selector.area.x, this.selector.area.y, this.selector.area.ex() + 20, this.selector.area.ey(), 0x88000000);
            this.drawGradientRect(this.selector.area.x, this.selector.area.y - 16, this.selector.area.ex() + 20, this.selector.area.y, 0x00000000, 0x88000000);

            this.font.drawStringWithShadow(this.location.isScene() ? I18n.format("blockbuster.gui.scenes.title") : I18n.format("blockbuster.gui.director.title"), this.area.x + 10, this.area.y + 10, 0xffffff);

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
        if (this.location.isDirector() || this.location.isScene())
        {
            Dispatcher.sendToServer(new PacketScenePlayback(this.location));
        }
    }

    public void record()
    {
        Replay replay = this.replay;

        if (replay != null && !replay.id.isEmpty())
        {
            if (this.location.isDirector())
            {
                Dispatcher.sendToServer(new PacketSceneRecord(this.location, replay.id));
            }
            else if (this.location.isScene())
            {
                Dispatcher.sendToServer(new PacketSceneRecord(this.location, replay.id));
            }
        }
    }

	public void pause()
    {
        if (this.location.isDirector() || this.location.isScene())
        {
            Dispatcher.sendToServer(new PacketScenePause(this.location));
        }
	}
}