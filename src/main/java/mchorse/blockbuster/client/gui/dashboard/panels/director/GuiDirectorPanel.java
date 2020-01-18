package mchorse.blockbuster.client.gui.dashboard.panels.director;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import mchorse.blockbuster.aperture.gui.GuiPlayback;
import mchorse.blockbuster.network.common.scene.PacketScenePlayback;
import mchorse.blockbuster.network.common.scene.PacketSceneRecord;
import mchorse.blockbuster.recording.scene.Scene;
import mchorse.blockbuster.recording.scene.SceneLocation;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.Blockbuster;
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
    public GuiButtonElement<GuiButton> attach;

    /* Replay fields */
    public GuiTextElement id;
    public GuiTextElement name;
    public GuiButtonElement<GuiCheckBox> invincible;
    public GuiButtonElement<GuiCheckBox> invisible;
    public GuiButtonElement<GuiCheckBox> enabled;
    public GuiButtonElement<GuiCheckBox> fake;
    public GuiButtonElement<GuiCheckBox> teleportBack;
    public GuiTrackpadElement health;

    public GuiDelegateElement<IGuiElement> popup;
    public GuiSceneManager scenes;

    private Scene scene;
    private Replay replay;
    private BlockPos pos;

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
        this.title = new GuiTextElement(mc, 80, (str) -> this.scene.title = str);
        this.startCommand = new GuiTextElement(mc, 10000, (str) -> this.scene.startCommand = str);
        this.stopCommand = new GuiTextElement(mc, 10000, (str) -> this.scene.stopCommand = str);
        this.loops = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.director.loops"), false, (b) -> this.scene.loops = b.button.isChecked());
        this.disableStates = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.director.disable_states"), false, (b) -> this.getDirector().disableStates = b.button.isChecked());
        this.hide = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.director.hide"), false, (b) -> this.getDirector().hide = b.button.isChecked());
        this.attach = GuiButtonElement.button(mc, "Attach", (b) -> this.attach()).tooltip("Attach this director/scene to playback button", TooltipDirection.BOTTOM);

        this.title.resizer().set(10, 50, 0, 20).parent(this.area).w(1, -20);
        this.startCommand.resizer().set(10, 90, 0, 20).parent(this.area).w(1, -20);
        this.stopCommand.resizer().set(10, 130, 0, 20).parent(this.area).w(1, -20);
        this.loops.resizer().set(0, 30, 60, 11).relative(this.stopCommand.resizer());
        this.disableStates.resizer().set(0, 16, 60, 11).relative(this.loops.resizer());
        this.hide.resizer().set(0, 16, 60, 11).relative(this.disableStates.resizer());
        this.attach.resizer().parent(this.area).set(0, 4, 60, 20).x(1, -104);

        this.configOptions.add(this.title, this.loops, this.disableStates, this.hide, this.startCommand, this.stopCommand, this.attach);

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

        this.replayEditor.add(this.id, this.name, this.invincible, this.invisible, this.enabled, this.fake, this.teleportBack, this.health);
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

        element = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.director.edit_record"), (b) -> this.openRecordEditor());
        element.resizer().set(10, 55, 80, 20).parent(this.area).x(1, -90);

        this.replayEditor.add(element);

        element = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.director.update_data"), (b) -> this.updatePlayerData()).tooltip(I18n.format("blockbuster.gui.director.update_data_tooltip"), TooltipDirection.LEFT);
        element.resizer().set(10, 80, 80, 20).parent(this.area).x(1, -90);

        this.replayEditor.add(element);

        element = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.director.rename_prefix"), (b) -> this.renamePrefix()).tooltip(I18n.format("blockbuster.gui.director.rename_prefix_tooltip"), TooltipDirection.LEFT);
        element.resizer().set(10, 105, 80, 20).parent(this.area).x(1, -90);

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

    public boolean isDirector()
    {
        return this.scene instanceof Director && this.pos != null;
    }

    public boolean isScene()
    {
        return this.scene instanceof Scene && this.pos == null;
    }

    public Director getDirector()
    {
        return this.isDirector() ? (Director) this.scene : null;
    }

    public Scene getScene()
    {
        return this.scene;
    }

    public BlockPos getPos()
    {
        return this.pos;
    }

    public Replay getReplay()
    {
        return this.replay;
    }

    public List<Replay> getReplays()
    {
        if (this.scene == null)
        {
            return null;
        }

        return this.scene.replays;
    }

    public void pickDirector(BlockPos pos)
    {
        this.close();

        Dispatcher.sendToServer(new PacketSceneRequestCast(new SceneLocation(pos)));
    }

    public GuiDirectorPanel openScene(Scene scene, BlockPos pos)
    {
        if (pos != null) tryAddingBlock(pos);
        this.scenes.setVisible(false);

        return this.setScene(scene, pos);
    }

    public GuiDirectorPanel setScene(Scene scene, BlockPos pos)
    {
        this.scene = scene;
        this.pos = pos;

        this.updateList();
        this.subChildren.setVisible(scene != null);
        this.replayEditor.setVisible(scene != null);
        this.hide.setVisible(this.isDirector());
        this.disableStates.setVisible(this.isDirector());

        if (scene == null)
        {
            this.setReplay(null);

            return this;
        }

        this.selector.setScene(scene);

        if (!this.scene.replays.isEmpty())
        {
            int current = this.scene.replays.indexOf(this.replay);

            this.setReplay(this.scene.replays.get(current == -1 ? 0 : current));
        }
        else
        {
            this.setReplay(null);
        }

        this.fillData();

        return this;
    }

    public GuiDirectorPanel set(Scene scene, BlockPos pos)
    {
        this.scene = scene;
        this.pos = pos;
        this.updateList();

        return this;
    }

    @Override
    public void appear()
    {
        this.dashboard.morphs.callback = (morph) -> this.setMorph(morph);
        this.dashboard.morphDelegate.resizer().parent(this.area).set(0, 0, 0, 0).w(1, 0).h(1, 0);
        this.dashboard.morphDelegate.resize(this.dashboard.width, this.dashboard.height);

        if (this.scene != null)
        {
            this.setScene(this.scene, this.pos);
        }
    }

    @Override
    public void open()
    {
        this.scenes.updateSceneList();
        this.scenes.setScene(this.scene);

        /* Resetting the current scene block, if it was removed from the
         * world */
        if (this.pos != null && this.mc.theWorld.getTileEntity(this.pos) == null)
        {
            this.setScene(null, null);
        }
    }

    @Override
    public void close()
    {
        if (this.scene != null)
        {
            if (this.replay != null)
            {
                this.dashboard.morphs.finish();
            }

            if (this.isDirector())
            {
                TileEntity te = this.mc.theWorld.getTileEntity(this.pos);

                if (te instanceof TileEntityDirector)
                {
                    ((TileEntityDirector) te).director.copy(this.getDirector());
                }

                Dispatcher.sendToServer(new PacketSceneCast(new SceneLocation(this.pos), this.getScene()));
            }
            else
            {
                Dispatcher.sendToServer(new PacketSceneCast(new SceneLocation(this.scene.getId()), this.getScene()));
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
        this.title.setText(this.scene.title);
        this.startCommand.setText(this.scene.startCommand);
        this.stopCommand.setText(this.scene.stopCommand);
        this.loops.button.setIsChecked(this.scene.loops);
        this.attach.setVisible(false);

        if (this.mc != null && this.mc.thePlayer != null)
        {
            ItemStack stack = this.mc.thePlayer.getHeldItemMainhand();

            this.attach.setVisible(this.scene != null && stack != null && stack.getItem() == Blockbuster.playbackItem);
        }

        if (this.isDirector())
        {
            this.disableStates.button.setIsChecked(this.getDirector().disableStates);
            this.hide.button.setIsChecked(this.getDirector().hide);
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

        if (this.isScene())
        {
            replay.id = this.scene.getNextSuffix(this.scene.getId());
        }

        this.scene.replays.add(replay);
        this.setReplay(replay);
        this.selector.update();
    }

    /**
     * Duplicate a replay 
     */
    private void dupeReplay()
    {
        if (this.scene.dupe(this.scene.replays.indexOf(this.replay), true))
        {
            this.selector.update();

            this.setReplay(this.scene.replays.get(this.scene.replays.size() - 1));
            this.selector.scroll.scrollTo(this.selector.current * this.selector.scroll.scrollItemSize);
        }
    }

    /**
     * Remove replay 
     */
    private void removeReplay()
    {
        this.scene.replays.remove(this.replay);
        int size = this.scene.replays.size();

        this.setReplay(size == 0 ? null : this.scene.replays.get(size - 1));
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
        this.scenes.setScene(this.scene);
        this.scenes.updateList(lastBlocks);
    }

    private void attach()
    {
        GuiPlayback playback = new GuiPlayback();

        if (this.isDirector())
        {
            playback.setDirector(this.pos);
        }
        else if (this.isScene())
        {
            playback.setScene(this.scene.getId());
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
        this.scene.renamePrefix(newPrefix);
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
                this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.id"), this.id.area.x, this.id.area.y - 12, error ? 0xffff3355 : 0xcccccc);
                this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.name"), this.name.area.x, this.name.area.y - 12, 0xcccccc);
            }
        }
        else
        {
            Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.y + 24, 0x88000000);
            this.drawGradientRect(this.area.x, this.area.y + 24, this.area.getX(1), this.area.y + 32, 0x88000000, 0x00000000);

            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.config"), this.area.x + 10, this.area.y + 10, 0xffffff);

            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.start_command"), this.startCommand.area.x, this.startCommand.area.y - 12, 0xcccccc);
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.stop_command"), this.stopCommand.area.x, this.stopCommand.area.y - 12, 0xcccccc);
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.display_title"), this.title.area.x, this.title.area.y - 12, 0xcccccc);
        }

        if (this.scene == null)
        {
            String no = I18n.format("blockbuster.gui.director.not_selected");

            this.drawCenteredString(this.font, no, this.area.getX(0.5F), this.area.getY(0.5F) - 6, 0xffffff);
        }

        super.draw(tooltip, mouseX, mouseY, partialTicks);
    }

    public void plause()
    {
        if (this.pos != null)
        {
            Dispatcher.sendToServer(new PacketScenePlayback(new SceneLocation(this.pos)));
        }
        else
        {
            Dispatcher.sendToServer(new PacketScenePlayback(new SceneLocation(this.scene.getId())));
        }
    }

    public void record()
    {
        Replay replay = this.replay;

        if (replay != null && !replay.id.isEmpty())
        {
            if (this.pos != null && this.isDirector())
            {
                Dispatcher.sendToServer(new PacketSceneRecord(new SceneLocation(this.pos), replay.id));
            }
            else
            {
                Dispatcher.sendToServer(new PacketSceneRecord(new SceneLocation(this.scene.getId()), replay.id));
            }
        }
    }
}