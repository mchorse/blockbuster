package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.client.gui.framework.elements.GuiButtonElement;
import mchorse.blockbuster.recording.actions.BreakBlockAction;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiBreakBlockActionPanel extends GuiBlockActionPanel<BreakBlockAction>
{
    public GuiButtonElement<GuiCheckBox> drop;

    public GuiBreakBlockActionPanel(Minecraft mc)
    {
        super(mc);

        this.title = "Break block action";
        this.drop = GuiButtonElement.checkbox(mc, "Drop block", false, (b) -> this.action.drop = b.button.isChecked());
        this.drop.resizer().set(0, -16, 70, 11).relative(this.x.resizer());

        this.children.add(this.drop);
    }

    @Override
    public void fill(BreakBlockAction action)
    {
        super.fill(action);

        this.drop.button.setIsChecked(action.drop);
    }
}