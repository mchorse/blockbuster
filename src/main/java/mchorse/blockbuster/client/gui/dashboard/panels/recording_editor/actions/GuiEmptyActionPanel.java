package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.client.gui.framework.GuiTooltip;
import mchorse.blockbuster.recording.actions.Action;
import net.minecraft.client.Minecraft;

public class GuiEmptyActionPanel extends GuiActionPanel<Action>
{
    public GuiEmptyActionPanel(Minecraft mc)
    {
        super(mc);
    }

    @Override
    public void fill(Action action)
    {
        super.fill(action);

        this.title = action.getClass().getSimpleName();
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(tooltip, mouseX, mouseY, partialTicks);

        this.drawCenteredString(this.font, "This action doesn't have any editable fields", this.area.getX(0.5F), this.area.getY(0.5F), 0xffffff);
    }
}