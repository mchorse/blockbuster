package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.recording.actions.Action;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiEmptyActionPanel extends GuiActionPanel<Action>
{
    public GuiEmptyActionPanel(Minecraft mc)
    {
        super(mc);
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(tooltip, mouseX, mouseY, partialTicks);

        this.drawCenteredString(this.font, I18n.format("blockbuster.gui.record_editor.no_fields"), this.area.getX(0.5F), this.area.getY(0.5F), 0xffffff);
    }
}