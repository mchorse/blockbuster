package mchorse.blockbuster.client.gui.dashboard.panels.director;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.dashboard.GuiSidebarButton;
import mchorse.blockbuster.client.gui.dashboard.panels.GuiDashboardPanel;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.common.tileentity.director.Director;
import mchorse.blockbuster.common.tileentity.director.Replay;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.PacketDirectorCast;
import mchorse.blockbuster.network.common.director.PacketDirectorRequestCast;
import mchorse.blockbuster.network.common.recording.PacketUpdatePlayerData;
import mchorse.blockbuster.utils.L10n;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.GuiTooltip.TooltipDirection;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiLegacy;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs.MorphCell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiDirectorPanel extends GuiDashboardPanel implements IGuiLegacy
{
    public static final List<BlockPos> lastBlocks = new ArrayList<BlockPos>();

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
    public GuiTrackpadElement health;

    public GuiDelegateElement<IGuiElement> popup;
    public GuiDirectorBlockList list;

    private Director director;
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
        this.title = new GuiTextElement(mc, 80, (str) -> this.director.title = str);
        this.startCommand = new GuiTextElement(mc, 10000, (str) -> this.director.startCommand = str);
        this.stopCommand = new GuiTextElement(mc, 10000, (str) -> this.director.stopCommand = str);
        this.loops = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.director.loops"), false, (b) -> this.director.loops = b.button.isChecked());
        this.disableStates = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.director.disable_states"), false, (b) -> this.director.disableStates = b.button.isChecked());
        this.hide = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.director.hide"), false, (b) -> this.director.hide = b.button.isChecked());

        this.title.resizer().set(10, 50, 0, 20).parent(this.area).w(1, -20);
        this.startCommand.resizer().set(10, 90, 0, 20).parent(this.area).w(1, -20);
        this.stopCommand.resizer().set(10, 130, 0, 20).parent(this.area).w(1, -20);
        this.loops.resizer().set(0, 30, 60, 11).relative(this.stopCommand.resizer());
        this.disableStates.resizer().set(0, 16, 60, 11).relative(this.loops.resizer());
        this.hide.resizer().set(0, 16, 60, 11).relative(this.disableStates.resizer());

        this.configOptions.add(this.title, this.loops, this.disableStates, this.hide, this.startCommand, this.stopCommand);

        /* Replay options */
        this.id = new GuiTextElement(mc, 120, (str) -> this.replay.id = str);
        this.name = new GuiTextElement(mc, 80, (str) -> this.replay.name = str);
        this.invincible = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.director.invincible"), false, (b) -> this.replay.invincible = b.button.isChecked());
        this.invisible = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.director.invisible"), false, (b) -> this.replay.invisible = b.button.isChecked());
        this.enabled = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.director.enabled"), false, (b) -> this.replay.enabled = b.button.isChecked());
        this.fake = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.director.fake_player"), false, (b) -> this.replay.fake = b.button.isChecked());
        this.health = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.director.health"), (value) -> this.replay.health = value);
        this.health.trackpad.min = 0;

        this.id.resizer().set(10, 30, 120, 20).parent(this.area);
        this.name.resizer().set(0, 40, 120, 20).relative(this.id.resizer());
        this.invincible.resizer().set(0, 30, 80, 11).relative(this.name.resizer());
        this.invisible.resizer().set(0, 16, 80, 11).relative(this.invincible.resizer());
        this.enabled.resizer().set(0, 16, 80, 11).relative(this.invisible.resizer());
        this.fake.resizer().set(0, 16, 80, 11).relative(this.enabled.resizer());
        this.health.resizer().set(0, 30, 80, 20).parent(this.area).x(1, -90);

        this.replayEditor.add(this.id, this.name, this.invincible, this.invisible, this.enabled, this.fake, this.health);
        this.replays.add(this.replayEditor, this.selector);

        /* Toggle view button */
        GuiElement element = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 48, 0, 48, 16, (b) ->
        {
            this.mainView.setDelegate(this.mainView.delegate == this.configOptions ? this.replays : this.configOptions);
        }).tooltip(I18n.format("blockbuster.gui.director.config"), TooltipDirection.LEFT);
        element.resizer().set(0, 6, 16, 16).parent(this.area).x(1, -48);

        this.subChildren.add(element);

        /* Add, duplicate and remove replay buttons */
        element = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 32, 32, 32, 48, (b) -> this.addReplay()).tooltip(I18n.format("blockbuster.gui.add"), TooltipDirection.LEFT);
        element.resizer().set(0, 8, 16, 16).relative(this.selector.resizer()).x(1, -24);

        this.replays.add(element);

        element = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 48, 32, 48, 48, (b) -> this.dupeReplay()).tooltip(I18n.format("blockbuster.gui.duplicate"), TooltipDirection.LEFT);
        element.resizer().set(0, 24, 16, 16).relative(this.selector.resizer()).x(1, -24);

        this.replays.add(element);

        element = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 64, 32, 64, 48, (b) -> this.removeReplay()).tooltip(I18n.format("blockbuster.gui.remove"), TooltipDirection.LEFT);
        element.resizer().set(0, 40, 16, 16).relative(this.selector.resizer()).x(1, -24);

        this.replays.add(element);

        /* Additional utility buttons */
        element = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.pick"), (b) -> this.dashboard.morphs.hide(false));
        element.resizer().set(10, 70, 80, 20).parent(this.area).x(0.5F, -40).y(1, -86);

        this.replayEditor.add(element);

        element = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.record"), (b) -> this.sendRecordMessage());
        element.resizer().set(10, 55, 60, 20).parent(this.area).x(1, -70);

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

        /* Model blocks */
        this.children.add(this.list = new GuiDirectorBlockList(mc, I18n.format("blockbuster.gui.director.title"), (director) -> this.pickDirector(director.getPos())));
        this.list.resizer().set(0, 0, 120, 0).parent(this.area).h(1, 0).x(1, -120);

        this.children.add(element = new GuiButtonElement<GuiSidebarButton>(mc, new GuiSidebarButton(0, 0, 0, new ItemStack(Blockbuster.directorBlock)), (b) -> this.list.toggleVisible()));
        element.resizer().set(0, 2, 24, 24).parent(this.area).x(1, -28);
    }

    public BlockPos getPos()
    {
        return this.pos;
    }

    private void pickDirector(BlockPos pos)
    {
        this.close();

        Dispatcher.sendToServer(new PacketDirectorRequestCast(pos));
    }

    public GuiDirectorPanel openDirector(Director director, BlockPos pos)
    {
        tryAddingBlock(pos);
        this.list.setVisible(false);

        return this.setDirector(director, pos);
    }

    public GuiDirectorPanel setDirector(Director director, BlockPos pos)
    {
        this.director = director;
        this.pos = pos;

        this.updateList();
        this.subChildren.setVisible(director != null);
        this.replayEditor.setVisible(director != null);

        if (director == null)
        {
            this.setReplay(null);

            return this;
        }

        this.selector.setDirector(director);

        if (!this.director.replays.isEmpty())
        {
            this.setReplay(this.director.replays.get(0));
        }
        else
        {
            this.setReplay(null);
        }

        this.fillData();

        return this;
    }

    @Override
    public void appear()
    {
        this.dashboard.morphs.callback = (morph) -> this.setMorph(morph);

        if (this.director != null)
        {
            this.setDirector(this.director, this.pos);
        }
    }

    @Override
    public void disappear()
    {
        this.dashboard.morphs.callback = null;
    }

    @Override
    public void open()
    {
        this.updateList();

        /* Resetting the current director block, if it was removed from the 
         * world */
        if (this.pos != null && this.mc.theWorld.getTileEntity(this.pos) == null)
        {
            this.setDirector(null, null);
        }
    }

    @Override
    public void close()
    {
        if (this.director != null && this.pos != null)
        {
            Dispatcher.sendToServer(new PacketDirectorCast(this.pos, this.director));
            TileEntity te = this.mc.theWorld.getTileEntity(this.pos);

            if (te instanceof TileEntityDirector)
            {
                ((TileEntityDirector) te).director.copy(this.director);
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
        this.title.setText(this.director.title);
        this.startCommand.setText(this.director.startCommand);
        this.stopCommand.setText(this.director.stopCommand);
        this.loops.button.setIsChecked(this.director.loops);
        this.disableStates.button.setIsChecked(this.director.disableStates);
        this.hide.button.setIsChecked(this.director.hide);
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

        this.director.replays.add(replay);
        this.setReplay(replay);
        this.selector.update();
    }

    /**
     * Duplicate a replay 
     */
    private void dupeReplay()
    {
        this.director.dupe(this.director.replays.indexOf(this.replay), true);
        this.selector.update();
    }

    /**
     * Remove replay 
     */
    private void removeReplay()
    {
        this.director.replays.remove(this.replay);
        int size = this.director.replays.size();

        this.setReplay(size == 0 ? null : this.director.replays.get(size - 1));
        this.selector.update();
    }

    private void setMorph(AbstractMorph morph)
    {
        if (this.replay != null)
        {
            this.replay.morph = morph == null ? null : morph.clone(true);
        }
    }

    private void updateList()
    {
        this.list.clear();

        for (BlockPos pos : lastBlocks)
        {
            this.list.addBlock(pos);
        }
    }

    /**
     * Send record message to the player
     */
    private void sendRecordMessage()
    {
        EntityPlayer player = this.mc.thePlayer;

        if (this.replay.id.isEmpty())
        {
            L10n.error(player, "recording.fill_filename");

            return;
        }

        String command = "/action record " + this.replay.id + " " + this.pos.getX() + " " + this.pos.getY() + " " + this.pos.getZ();

        ITextComponent component = new TextComponentString(I18n.format("blockbuster.info.recording.clickhere"));
        component.getStyle().setClickEvent(new ClickEvent(Action.RUN_COMMAND, command));
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
        this.director.renamePrefix(newPrefix);
        this.fillReplayData();
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
    public void resize(int width, int height)
    {
        super.resize(width, height);

        this.dashboard.morphs.updateRect(this.area.x, this.area.y, this.area.w, this.area.h);
        this.dashboard.morphs.initGui();
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        /* Draw additional stuff */
        if (this.mainView.delegate == this.replays)
        {
            if (this.replay != null)
            {
                MorphCell cell = this.dashboard.morphs.getSelected();
                AbstractMorph morph = this.replay.morph;

                if (morph == null && cell != null)
                {
                    morph = cell.current().morph;
                }

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

        super.draw(tooltip, mouseX, mouseY, partialTicks);

        if (this.director == null)
        {
            String no = I18n.format("blockbuster.gui.director.not_selected");

            this.drawCenteredString(this.font, no, this.area.getX(0.5F), this.area.getY(0.5F) - 6, 0xffffff);
        }

        this.dashboard.morphs.drawScreen(mouseX, mouseY, partialTicks);
    }
}