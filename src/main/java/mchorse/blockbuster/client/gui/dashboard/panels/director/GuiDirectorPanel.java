package mchorse.blockbuster.client.gui.dashboard.panels.director;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import mchorse.blockbuster.aperture.gui.GuiPlayback;
import mchorse.blockbuster.common.item.ItemPlayback;
import mchorse.blockbuster.network.common.scene.PacketScenePause;
import mchorse.blockbuster.network.common.scene.PacketScenePlayback;
import mchorse.blockbuster.network.common.scene.PacketSceneRecord;
import mchorse.blockbuster.recording.scene.Scene;
import mchorse.blockbuster.recording.scene.SceneLocation;
import mchorse.blockbuster.utils.April;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.dashboard.panels.GuiDashboardPanel;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.recording.scene.Director;
import mchorse.blockbuster.recording.scene.Replay;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.scene.PacketSceneCast;
import mchorse.blockbuster.network.common.scene.PacketSceneRequestCast;
import mchorse.blockbuster.network.common.recording.PacketUpdatePlayerData;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.GuiTooltip.TooltipDirection;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiDirectorPanel extends GuiDashboardPanel
{
    public static final List<BlockPos> lastBlocks = new ArrayList<BlockPos>();
    public static final Pattern RECORDING_ID = Pattern.compile("^[\\w,\\-_]*$");

    private GuiElements<IGuiElement> subChildren;
    private GuiDelegateElement<IGuiElement> mainView;
    private GuiElements<IGuiElement> replays;
    private GuiElements<IGuiElement> replayEditor;
    private GuiElements<IGuiElement> configOptions;
    private GuiReplaySelector selector;

    /* Config fields */
    public GuiTextElement title;
    public GuiTextElement startCommand;
    public GuiTextElement stopCommand;
    public GuiButtonElement<GuiCheckBox> loops;
    public GuiButtonElement<GuiCheckBox> disableStates;
    public GuiButtonElement<GuiCheckBox> hide;

    /* Replay fields */
    public GuiTextElement id;
    public GuiTextElement name;
    public GuiButtonElement<GuiCheckBox> invincible;
    public GuiButtonElement<GuiCheckBox> invisible;
    public GuiButtonElement<GuiCheckBox> enabled;
    public GuiButtonElement<GuiCheckBox> fake;
    public GuiButtonElement<GuiCheckBox> teleportBack;
    public GuiTrackpadElement health;

    public GuiButtonElement<GuiButton> attach;
    public GuiButtonElement<GuiButton> record;

    public GuiDelegateElement<IGuiElement> popup;
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

        this.popup = new GuiDelegateElement<IGuiElement>(mc, null);
        this.subChildren = new GuiElements<>();
        this.subChildren.setVisible(false);
        this.replays = new GuiElements<>();
        this.replayEditor = new GuiElements<>();
        this.replayEditor.setVisible(false);
        this.configOptions = new GuiElements<>();
        this.mainView = new GuiDelegateElement<IGuiElement>(mc, this.replays);
        this.selector = new GuiReplaySelector(mc, (replay) -> this.setReplay(replay));
        this.selector.resizer().set(0, 0, 0, 60).parent(this.area).w(1, 0).y(1, -60);

        this.children.add(this.subChildren);
        this.subChildren.add(this.mainView);

        /* Config options */
        this.title = new GuiTextElement(mc, 80, (str) -> this.location.getScene().title = str);
        this.startCommand = new GuiTextElement(mc, 10000, (str) -> this.location.getScene().startCommand = str);
        this.stopCommand = new GuiTextElement(mc, 10000, (str) -> this.location.getScene().stopCommand = str);
        this.loops = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.director.loops"), false, (b) -> this.location.getScene().loops = b.button.isChecked());
        this.disableStates = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.director.disable_states"), false, (b) -> this.location.getDirector().disableStates = b.button.isChecked());
        this.hide = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.director.hide"), false, (b) -> this.location.getDirector().hide = b.button.isChecked());
        this.attach = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.director.attach"), (b) -> this.attach()).tooltip(I18n.format("blockbuster.gui.director.attach_tooltip"), TooltipDirection.BOTTOM);

        this.title.resizer().set(10, 50, 0, 20).parent(this.area).w(1, -20);
        this.startCommand.resizer().set(10, 90, 0, 20).parent(this.area).w(1, -20);
        this.stopCommand.resizer().set(10, 130, 0, 20).parent(this.area).w(1, -20);
        this.loops.resizer().set(0, 30, 60, 11).relative(this.stopCommand.resizer());
        this.disableStates.resizer().set(0, 16, 60, 11).relative(this.loops.resizer());
        this.hide.resizer().set(0, 16, 60, 11).relative(this.disableStates.resizer());
        this.attach.resizer().set(10, 155, 80, 20).parent(this.area).x(1, -90);

        this.configOptions.add(this.title, this.loops, this.disableStates, this.hide, this.startCommand, this.stopCommand);

        /* Replay options */
        this.id = new GuiTextElement(mc, 120, (str) -> this.replay.id = str);
        this.id.field.setValidator((str) -> RECORDING_ID.matcher(str).matches());
        this.name = new GuiTextElement(mc, 80, (str) -> this.replay.name = str);
        this.invincible = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.director.invincible"), false, (b) -> this.replay.invincible = b.button.isChecked());
        this.invisible = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.director.invisible"), false, (b) -> this.replay.invisible = b.button.isChecked());
        this.enabled = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.director.enabled"), false, (b) -> this.replay.enabled = b.button.isChecked());
        this.fake = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.director.fake_player"), false, (b) -> this.replay.fake = b.button.isChecked());
        this.teleportBack = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.director.tp_back"), false, (b) -> this.replay.teleportBack = b.button.isChecked()).tooltip(I18n.format("blockbuster.gui.director.tp_back_tooltip"), TooltipDirection.RIGHT);
        this.health = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.director.health"), (value) -> this.replay.health = value);
        this.health.trackpad.min = 0;

        this.id.resizer().set(10, 30, 120, 20).parent(this.area);
        this.name.resizer().set(0, 40, 120, 20).relative(this.id.resizer());
        this.invincible.resizer().set(0, 30, 80, 11).relative(this.name.resizer());
        this.invisible.resizer().set(0, 16, 80, 11).relative(this.invincible.resizer());
        this.enabled.resizer().set(0, 16, 80, 11).relative(this.invisible.resizer());
        this.fake.resizer().set(0, 16, 80, 11).relative(this.enabled.resizer());
        this.teleportBack.resizer().set(0, 16, this.teleportBack.button.width, 11).relative(this.fake.resizer());
        this.health.resizer().set(0, 30, 80, 20).parent(this.area).x(1, -90);

        this.replayEditor.add(this.id, this.name, this.invincible, this.invisible, this.enabled, this.fake, this.teleportBack, this.health, this.attach);
        this.replays.add(this.replayEditor, this.selector);

        /* Toggle view button */
        GuiElement element = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 48, 0, 48, 16, (b) ->
        {
            this.mainView.setDelegate(this.mainView.delegate == this.configOptions ? this.replays : this.configOptions);
        }).tooltip(I18n.format("blockbuster.gui.director.config"), TooltipDirection.LEFT);
        element.resizer().set(0, 6, 16, 16).parent(this.area).x(1, -42);

        this.subChildren.add(element);

        /* Add, duplicate and remove replay buttons */
        element = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 32, 32, 32, 48, (b) -> this.addReplay()).tooltip(I18n.format("blockbuster.gui.add"), TooltipDirection.LEFT);
        element.resizer().set(0, 8, 16, 16).relative(this.selector.resizer()).x(1, -24);

        this.replays.add(element);

        element = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 48, 32, 48, 48, (b) -> this.dupeReplay()).tooltip(I18n.format("blockbuster.gui.duplicate"), TooltipDirection.LEFT);
        element.resizer().set(0, 24, 16, 16).relative(this.selector.resizer()).x(1, -24);

        this.replays.add(element);

        element = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 64, 32, 64, 48, (b) -> this.removeReplay()).tooltip(I18n.format("blockbuster.gui.remove"), TooltipDirection.LEFT);
        element.resizer().set(0, 40, 16, 16).relative(this.selector.resizer()).x(1, -24);

        this.replays.add(element);

        /* Additional utility buttons */
        element = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.pick"), (b) ->
        {
            this.children.unfocus();
            this.dashboard.morphs.setVisible(true);
        });
        element.resizer().set(10, 70, 80, 20).parent(this.area).x(0.5F, -40).y(1, -86);

        this.replayEditor.add(element);

        element = this.record = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.record"), (b) -> this.sendRecordMessage());
        element.resizer().set(10, 55, 80, 20).parent(this.area).x(1, -90);

        this.replayEditor.add(element);

        element = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.director.edit_record"), (b) -> this.openRecordEditor());
        element.resizer().set(10, 80, 80, 20).parent(this.area).x(1, -90);

        this.replayEditor.add(element);

        element = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.director.update_data"), (b) -> this.updatePlayerData()).tooltip(I18n.format("blockbuster.gui.director.update_data_tooltip"), TooltipDirection.LEFT);
        element.resizer().set(10, 105, 80, 20).parent(this.area).x(1, -90);

        this.replayEditor.add(element);

        element = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.director.rename_prefix"), (b) -> this.renamePrefix()).tooltip(I18n.format("blockbuster.gui.director.rename_prefix_tooltip"), TooltipDirection.LEFT);
        element.resizer().set(10, 130, 80, 20).parent(this.area).x(1, -90);

        this.replayEditor.add(element);

        this.popup.resizer().parent(element.area).set(-125, -100, 120, 120);
        this.replayEditor.add(this.popup);

        /* Scene manager */
        this.children.add(element = GuiButtonElement.icon(mc, GuiDashboard.GUI_ICONS, 96, 32, 96, 48, (b) -> this.scenes.toggleVisible()));
        element.resizer().set(0, 6, 16, 16).parent(this.area).x(1, -24);

        this.children.add(this.scenes = new GuiSceneManager(mc, this));
        this.scenes.resizer().set(0, 24, 160, 0).parent(this.area).x(1, -166).h(1, -100);

        this.children.add(this.dashboard.morphDelegate);
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
        if (location.isDirector()) tryAddingBlock(location.getPosition());
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

        this.selector.setScene(location.getScene());

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
        this.dashboard.morphs.callback = (morph) -> this.setMorph(morph);
        this.dashboard.morphDelegate.resizer().parent(this.area).set(0, 0, 0, 0).w(1, 0).h(1, 0);
        this.dashboard.morphDelegate.resize(this.dashboard.width, this.dashboard.height);

        if (!this.location.isEmpty())
        {
            this.setScene(this.location);
        }
    }

    @Override
    public void open()
    {
        this.scenes.setScene(this.location.getScene());
        this.scenes.updateSceneList();

        /* Resetting the current scene block, if it was removed from the
         * world */
        if (this.location.isDirector() && this.mc.theWorld.getTileEntity(this.location.getPosition()) == null)
        {
            this.setScene(new SceneLocation());
        }
    }

    @Override
    public void close()
    {
        if (!this.location.isEmpty())
        {
            if (this.replay != null)
            {
                this.dashboard.morphs.finish();
            }

            if (this.location.isDirector())
            {
                TileEntity te = this.mc.theWorld.getTileEntity(this.location.getPosition());

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
        this.selector.setReplay(replay);
        this.fillReplayData();
    }

    private void fillData()
    {
        this.title.setText(this.location.getScene().title);
        this.startCommand.setText(this.location.getScene().startCommand);
        this.stopCommand.setText(this.location.getScene().stopCommand);
        this.loops.button.setIsChecked(this.location.getScene().loops);
        this.attach.setEnabled(false);
        this.record.setEnabled(this.location.isDirector());

        if (this.mc != null && this.mc.thePlayer != null)
        {
            ItemStack stack = this.mc.thePlayer.getHeldItemMainhand();

            this.attach.setEnabled(!this.location.isEmpty() && stack != null && stack.getItem() instanceof ItemPlayback);
        }

        if (this.location.isDirector())
        {
            this.disableStates.button.setIsChecked(this.location.getDirector().disableStates);
            this.hide.button.setIsChecked(this.location.getDirector().hide);
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
        this.invincible.button.setIsChecked(this.replay.invincible);
        this.invisible.button.setIsChecked(this.replay.invisible);
        this.enabled.button.setIsChecked(this.replay.enabled);
        this.fake.button.setIsChecked(this.replay.fake);
        this.teleportBack.button.setIsChecked(this.replay.teleportBack);
        this.health.setValue(this.replay.health);

        this.dashboard.morphs.setSelected(this.replay.morph);
        this.selector.setReplay(this.replay);
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
        Scene scene = this.location.getScene();

        if (scene.dupe(scene.replays.indexOf(this.replay), true))
        {
            this.selector.update();
            this.selector.scroll.scrollTo(this.selector.current * this.selector.scroll.scrollItemSize);
            this.setReplay(scene.replays.get(scene.replays.size() - 1));
        }
    }

    /**
     * Remove replay 
     */
    private void removeReplay()
    {
        Scene scene = this.location.getScene();

        scene.replays.remove(this.replay);
        int size = scene.replays.size();

        this.setReplay(size == 0 ? null : scene.replays.get(size - 1));
        this.selector.update();
    }

    private void setMorph(AbstractMorph morph)
    {
        if (this.replay != null)
        {
            this.replay.morph = morph;
        }
    }

    private void updateList()
    {
        this.scenes.setScene(this.location.getScene());
        this.scenes.updateList(lastBlocks);
    }

    /**
     * Send record message to the player
     */
    private void sendRecordMessage()
    {
        if (!this.location.isDirector())
        {
            return;
        }

        EntityPlayer player = this.mc.thePlayer;

        if (this.replay.id.isEmpty())
        {
            L10n.error(player, "recording.fill_filename");

            return;
        }

        BlockPos pos = this.location.getPosition();
        String command = "/action record " + this.replay.id + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ();

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
        GuiPlayback playback = new GuiPlayback();

        if (this.location.isDirector())
        {
            playback.setDirector(this.location.getPosition());
        }
        else if (this.location.isScene())
        {
            playback.setScene(this.location.getFilename());
        }

        this.dashboard.close();
        this.mc.displayGuiScreen(playback);
    }

    private void openRecordEditor()
    {
        if (this.replay != null && !this.replay.id.isEmpty())
        {
            this.dashboard.openPanel(this.dashboard.recordingEditorPanel);
            this.dashboard.recordingEditorPanel.selectRecord(this.replay.id);
            this.dashboard.recordingEditorPanel.records.setVisible(false);
        }
    }

    private void updatePlayerData()
    {
        Dispatcher.sendToServer(new PacketUpdatePlayerData(this.replay.id));
    }

    private void renamePrefix()
    {
        this.popup.setDelegate(new GuiPromptModal(this.mc, this.popup, I18n.format("blockbuster.gui.director.rename_prefix_popup"), (str) -> this.renamePrefix(str)));
    }

    private void renamePrefix(String newPrefix)
    {
        this.location.getScene().renamePrefix(newPrefix);
        this.fillReplayData();
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        if (this.scenes.isVisible())
        {
            int x = this.scenes.area.getX(1) - 20;
            int y = this.scenes.area.y - 20;

            Gui.drawRect(x, y, x + 20, y + 20, 0x88000000);
        }

        /* Draw additional stuff */
        if (this.mainView.delegate == this.replays)
        {
            if (this.replay != null)
            {
                AbstractMorph morph = this.replay.morph;

                if (morph != null)
                {
                    int x = this.area.getX(0.5F);
                    int y = this.area.getY(0.55F);

                    GuiScreen screen = this.mc.currentScreen;

                    GuiUtils.scissor(this.area.x, this.area.y, this.area.w, this.area.h, screen.width, screen.height);
                    morph.renderOnScreen(this.mc.thePlayer, x, y, this.area.h / 3.5F, 1.0F);
                    GL11.glDisable(GL11.GL_SCISSOR_TEST);
                }
            }

            boolean error = this.replay == null ? false : this.replay.id.isEmpty();

            if (this.replayEditor.isVisible())
            {
                this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.id"), this.id.area.x, this.id.area.y - 12, error ? 0xffff3355 : April.aprilColor(I18n.format("blockbuster.gui.director.id")));
                this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.name"), this.name.area.x, this.name.area.y - 12, April.aprilColor(I18n.format("blockbuster.gui.director.name")));
            }
        }
        else
        {
            Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.y + 24, 0x88000000);
            this.drawGradientRect(this.area.x, this.area.y + 24, this.area.getX(1), this.area.y + 32, 0x88000000, 0x00000000);

            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.config"), this.area.x + 10, this.area.y + 10, April.aprilColor(I18n.format("blockbuster.gui.director.config")));

            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.start_command"), this.startCommand.area.x, this.startCommand.area.y - 12, April.aprilColor(I18n.format("blockbuster.gui.director.start_command")));
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.stop_command"), this.stopCommand.area.x, this.stopCommand.area.y - 12, April.aprilColor(I18n.format("blockbuster.gui.director.stop_command")));
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.display_title"), this.title.area.x, this.title.area.y - 12, April.aprilColor(I18n.format("blockbuster.gui.director.display_title")));
        }

        if (this.location.isEmpty())
        {
            String no = I18n.format("blockbuster.gui.director.not_selected");

            this.drawCenteredString(this.font, no, this.area.getX(0.5F), this.area.getY(0.5F) - 6, April.aprilColor(no));
        }

        super.draw(tooltip, mouseX, mouseY, partialTicks);
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