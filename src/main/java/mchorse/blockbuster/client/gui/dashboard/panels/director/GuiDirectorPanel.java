package mchorse.blockbuster.client.gui.dashboard.panels.director;

import java.io.IOException;
import java.util.List;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.dashboard.panels.GuiDashboardPanel;
import mchorse.blockbuster.client.gui.elements.GuiMorphsPopup;
import mchorse.blockbuster.client.gui.framework.elements.GuiButtonElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiDelegateElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiElements;
import mchorse.blockbuster.client.gui.framework.elements.GuiTextElement;
import mchorse.blockbuster.client.gui.framework.elements.IGuiLegacy;
import mchorse.blockbuster.client.gui.utils.Resizer.Measure;
import mchorse.blockbuster.common.tileentity.director.Director;
import mchorse.blockbuster.common.tileentity.director.Replay;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.PacketDirectorCast;
import mchorse.blockbuster.utils.L10n;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs.MorphCell;
import mchorse.metamorph.client.gui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
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
    private GuiElements subChildren;
    private GuiDelegateElement mainView;
    private GuiElements replayEditor;
    private GuiElements configOptions;
    private GuiReplaySelector selector;

    /* Config fields */
    public GuiButtonElement<GuiCheckBox> loops;
    public GuiButtonElement<GuiCheckBox> disableStates;
    public GuiTextElement startCommand;
    public GuiTextElement stopCommand;

    /* Replay fields */
    public GuiTextElement id;
    public GuiTextElement name;
    public GuiButtonElement<GuiCheckBox> invincible;
    public GuiButtonElement<GuiCheckBox> invisible;
    public GuiButtonElement<GuiCheckBox> enabled;

    public GuiMorphsPopup morphs;

    private Director director;
    private Replay replay;
    private BlockPos pos;

    public GuiDirectorPanel(Minecraft mc)
    {
        super(mc);

        this.subChildren = new GuiElements();
        this.replayEditor = new GuiElements();
        this.configOptions = new GuiElements();
        this.mainView = new GuiDelegateElement(mc, this.replayEditor);
        this.selector = new GuiReplaySelector(mc, (replay) -> this.setReplay(replay));
        this.selector.resizer().set(0, 0, 0, 60).parent(this.area).w.set(1, Measure.RELATIVE);

        this.children.add(this.subChildren);
        this.subChildren.add(this.mainView);

        /* Config options */
        this.startCommand = new GuiTextElement(mc, (str) -> this.director.startCommand = str, 4000);
        this.stopCommand = new GuiTextElement(mc, (str) -> this.director.stopCommand = str, 4000);
        this.loops = GuiButtonElement.checkbox(mc, "Loops", false, (b) -> this.director.loops = b.button.isChecked());
        this.disableStates = GuiButtonElement.checkbox(mc, "Disable states", false, (b) -> this.director.disableStates = b.button.isChecked());

        this.startCommand.resizer().set(10, 50, 0, 20).parent(this.area).w.set(1, Measure.RELATIVE, -20);
        this.stopCommand.resizer().set(10, 90, 0, 20).parent(this.area).w.set(1, Measure.RELATIVE, -20);
        this.loops.resizer().set(0, 30, 60, 11).relative(this.stopCommand.resizer());
        this.disableStates.resizer().set(0, 20, 60, 11).relative(this.loops.resizer());

        this.configOptions.add(this.loops, this.disableStates, this.startCommand, this.stopCommand);

        /* Replay options */
        this.id = new GuiTextElement(mc, (str) -> this.replay.id = str, 80);
        this.name = new GuiTextElement(mc, (str) -> this.replay.name = str, 80);
        this.invincible = GuiButtonElement.checkbox(mc, "Invincible", false, (b) -> this.replay.invincible = b.button.isChecked());
        this.invisible = GuiButtonElement.checkbox(mc, "Invisible", false, (b) -> this.replay.invisible = b.button.isChecked());
        this.enabled = GuiButtonElement.checkbox(mc, "Enabled", false, (b) -> this.replay.enabled = b.button.isChecked());

        this.id.resizer().set(10, 90, 120, 20).parent(this.area);
        this.name.resizer().set(0, 40, 120, 20).relative(this.id.resizer());
        this.invincible.resizer().set(0, 30, 80, 11).relative(this.name.resizer());
        this.invisible.resizer().set(0, 16, 80, 11).relative(this.invincible.resizer());
        this.enabled.resizer().set(0, 16, 80, 11).relative(this.invisible.resizer());

        this.replayEditor.add(this.id, this.name, this.invincible, this.invisible, this.enabled);
        this.replayEditor.add(this.selector);

        /* Toggle view button */
        GuiElement element = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 48, 0, 48, 16, (b) ->
        {
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;

            this.mainView.delegate = this.mainView.delegate == this.configOptions ? this.replayEditor : this.configOptions;
            this.mainView.delegate.resize(screen.width, screen.height);
        });
        element.resizer().set(0, 0, 16, 16).parent(this.area).x.set(1, Measure.RELATIVE, -24);
        element.resizer().y.set(1, Measure.RELATIVE, -24);

        this.subChildren.add(element);

        /* Add, duplicate and remove replay buttons */
        element = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 32, 32, 32, 48, (b) -> this.addReplay());
        element.resizer().set(0, 8, 16, 16).parent(this.area).x.set(1, Measure.RELATIVE, -24);

        this.replayEditor.add(element);

        element = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 48, 32, 48, 48, (b) -> this.dupeReplay());
        element.resizer().set(0, 24, 16, 16).parent(this.area).x.set(1, Measure.RELATIVE, -24);

        this.replayEditor.add(element);

        element = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 64, 32, 64, 48, (b) -> this.removeReplay());
        element.resizer().set(0, 40, 16, 16).parent(this.area).x.set(1, Measure.RELATIVE, -24);

        this.replayEditor.add(element);

        /* Additional utility buttons */
        element = GuiButtonElement.button(mc, "Pick morph", (b) -> this.morphs.morphs.setHidden(false));
        element.resizer().set(10, 70, 80, 20).parent(this.area).x.set(0.5F, Measure.RELATIVE, -40);
        element.resizer().y.set(1, Measure.RELATIVE, -26);

        this.replayEditor.add(element);

        element = GuiButtonElement.button(mc, "Record", (b) -> this.sendRecordMessage());
        element.resizer().set(10, 0, 60, 20).parent(this.area);
        element.resizer().y.set(1, Measure.RELATIVE, -26);

        this.replayEditor.add(element);

        this.morphs = new GuiMorphsPopup(6, null, Morphing.get(this.mc.thePlayer));
    }

    public GuiDirectorPanel openDirector(Director director, BlockPos pos)
    {
        this.director = director;
        this.pos = pos;
        this.selector.setDirector(director);

        if (!this.director.replays.isEmpty())
        {
            this.setReplay(this.director.replays.get(0));
        }

        this.fillData();

        return this;
    }

    @Override
    public void close()
    {
        if (this.director != null && this.pos != null)
        {
            Dispatcher.sendToServer(new PacketDirectorCast(this.pos, this.director));
        }
    }

    private void setReplay(Replay replay)
    {
        if (this.replay != null)
        {
            MorphCell cell = this.morphs.morphs.getSelected();

            if (cell != null)
            {
                this.replay.morph = cell.current().morph;
            }
        }

        GuiScreen screen = Minecraft.getMinecraft().currentScreen;

        this.replay = replay;
        this.mainView.delegate = this.replayEditor;
        this.mainView.delegate.resize(screen.width, screen.height);
        this.selector.setReplay(replay);
        this.fillReplayData();
    }

    private void fillData()
    {
        this.loops.button.setIsChecked(this.director.loops);
        this.disableStates.button.setIsChecked(this.director.disableStates);
        this.startCommand.setText(this.director.startCommand);
        this.stopCommand.setText(this.director.stopCommand);
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

        this.morphs.morphs.setSelected(this.replay.morph);
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

    @Override
    public boolean handleMouseInput(int mouseX, int mouseY) throws IOException
    {
        boolean result = !this.morphs.morphs.getHidden() && this.morphs.isInside(mouseX, mouseY);

        this.morphs.handleMouseInput();

        return result;
    }

    @Override
    public boolean handleKeyboardInput() throws IOException
    {
        this.morphs.handleKeyboardInput();

        return !this.morphs.morphs.getHidden();
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

        this.morphs.updateRect(this.area.x, this.area.y, this.area.w, this.area.h);
        this.morphs.setWorldAndResolution(this.mc, width, height);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        /* Draw additional stuff */
        if (this.mainView.delegate == this.replayEditor)
        {
            if (this.replay != null)
            {
                MorphCell cell = this.morphs.morphs.getSelected();
                AbstractMorph morph = this.replay.morph;

                if (morph == null && cell != null)
                {
                    morph = cell.current().morph;
                }

                if (morph != null)
                {
                    int x = this.area.getX(0.5F);
                    int y = this.area.getY(0.75F);

                    GuiScreen screen = this.mc.currentScreen;

                    GuiUtils.scissor(this.area.x, this.area.y, this.area.w, this.area.h, screen.width, screen.height);
                    morph.renderOnScreen(this.mc.thePlayer, x, y, this.area.h / 3.5F, 1.0F);
                    GL11.glDisable(GL11.GL_SCISSOR_TEST);
                }
            }

            boolean error = this.replay == null ? false : this.replay.id.isEmpty();

            this.font.drawStringWithShadow("Recording ID", this.id.area.x, this.id.area.y - 12, error ? 0xffff3355 : 0xcccccc);
            this.font.drawStringWithShadow("Name tag", this.name.area.x, this.name.area.y - 12, 0xcccccc);
        }
        else
        {
            Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.y + 24, 0x88000000);
            this.drawGradientRect(this.area.x, this.area.y + 24, this.area.getX(1), this.area.y + 32, 0x88000000, 0x00000000);

            this.font.drawStringWithShadow("Director block configuration", this.area.x + 10, this.area.y + 10, 0xffffff);

            this.font.drawStringWithShadow("On start command", this.startCommand.area.x, this.startCommand.area.y - 12, 0xcccccc);
            this.font.drawStringWithShadow("On stop command", this.stopCommand.area.x, this.stopCommand.area.y - 12, 0xcccccc);
        }

        super.draw(mouseX, mouseY, partialTicks);

        this.morphs.drawScreen(mouseX, mouseY, partialTicks);
    }
}