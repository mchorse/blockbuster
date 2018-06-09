package mchorse.blockbuster.client.gui.dashboard.panels;

import mchorse.blockbuster.client.gui.framework.elements.GuiButtonElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiDelegateElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiElements;
import mchorse.blockbuster.client.gui.framework.elements.GuiTextElement;
import mchorse.blockbuster.client.gui.utils.Resizer.Measure;
import mchorse.blockbuster.common.tileentity.director.Director;
import mchorse.blockbuster.common.tileentity.director.Replay;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.PacketDirectorCast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiDirectorPanel extends GuiDashboardPanel
{
    private GuiElements subChildren;
    private GuiDelegateElement mainView;
    private GuiElements replayEditor;
    private GuiElements configOptions;

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

    private Director director;
    private Replay replay;
    private BlockPos pos;

    public GuiDirectorPanel(Minecraft mc)
    {
        super(mc);

        this.subChildren = new GuiElements();
        this.replayEditor = new GuiElements();
        this.configOptions = new GuiElements();
        this.mainView = new GuiDelegateElement(mc, this.configOptions);

        this.children.add(this.subChildren);
        this.subChildren.add(this.mainView);

        /* Config options */
        this.loops = GuiButtonElement.checkbox(mc, "Loops", false, (b) -> this.director.loops = b.button.isChecked());
        this.disableStates = GuiButtonElement.checkbox(mc, "Disable states", false, (b) -> this.director.disableStates = b.button.isChecked());
        this.startCommand = new GuiTextElement(mc, (str) -> this.director.startCommand = str, 4000);
        this.stopCommand = new GuiTextElement(mc, (str) -> this.director.stopCommand = str, 4000);

        this.loops.resizer().set(10, 100, 60, 11).parent(this.area);
        this.disableStates.resizer().set(0, 30, 60, 11).relative(this.loops.resizer());
        this.startCommand.resizer().set(0, 30, 120, 20).relative(this.disableStates.resizer());
        this.stopCommand.resizer().set(0, 30, 120, 20).relative(this.startCommand.resizer());

        this.configOptions.add(this.loops, this.disableStates, this.startCommand, this.stopCommand);

        /* Replay options */
        this.id = new GuiTextElement(mc, (str) -> this.replay.id = str, 80);
        this.name = new GuiTextElement(mc, (str) -> this.replay.name = str, 80);
        this.invincible = GuiButtonElement.checkbox(mc, "Invincible", false, (b) -> this.replay.invincible = b.button.isChecked());
        this.invisible = GuiButtonElement.checkbox(mc, "Invisible", false, (b) -> this.replay.invisible = b.button.isChecked());
        this.enabled = GuiButtonElement.checkbox(mc, "Enabled", false, (b) -> this.replay.enabled = b.button.isChecked());

        this.id.resizer().set(10, 100, 80, 20).parent(this.area);
        this.name.resizer().set(0, 30, 80, 20).relative(this.id.resizer());
        this.invincible.resizer().set(0, 30, 80, 11).relative(this.name.resizer());
        this.invisible.resizer().set(0, 30, 80, 11).relative(this.invincible.resizer());
        this.enabled.resizer().set(0, 30, 80, 11).relative(this.invisible.resizer());

        this.replayEditor.add(this.id, this.name, this.invincible, this.invisible, this.enabled);

        /* Additional controls */
        GuiElement element = GuiButtonElement.button(mc, "Toggle", (b) ->
        {
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;

            this.mainView.delegate = this.mainView.delegate == this.configOptions ? this.replayEditor : this.configOptions;
            this.mainView.delegate.resize(screen.width, screen.height);
        });
        element.resizer().set(0, 10, 60, 20).parent(this.area).x.set(1, Measure.RELATIVE, -70);

        this.subChildren.add(element);
    }

    public GuiDirectorPanel openDirector(Director director, BlockPos pos)
    {
        this.director = director;
        this.pos = pos;

        if (!this.director.replays.isEmpty())
        {
            this.setReplay(this.director.replays.get(0));
        }
        else
        {
            this.mainView.delegate = this.configOptions;
        }

        GuiScreen screen = Minecraft.getMinecraft().currentScreen;

        this.mainView.delegate.resize(screen.width, screen.height);
        this.fillData();

        return this;
    }

    @Override
    public void close()
    {
        Dispatcher.sendToServer(new PacketDirectorCast(this.pos, this.director));
    }

    private void setReplay(Replay replay)
    {
        this.replay = replay;
        this.mainView.delegate = this.replayEditor;
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
        this.id.setText(this.replay.id);
        this.name.setText(this.replay.name);
        this.invincible.button.setIsChecked(this.replay.invincible);
        this.invisible.button.setIsChecked(this.replay.invisible);
        this.enabled.button.setIsChecked(this.replay.enabled);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        super.draw(mouseX, mouseY, partialTicks);
    }
}