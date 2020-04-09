package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.recording.actions.Action;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiEmptyActionPanel extends GuiActionPanel<Action>
{
    public GuiEmptyActionPanel(Minecraft mc)
    {
        super(mc);
    }

    @Override
    public void draw(GuiContext context)
    {
        super.draw(context);

        this.drawCenteredString(this.font, I18n.format("blockbuster.gui.record_editor.no_fields"), this.area.mx(), this.area.my(), 0xffffff);
    }
}