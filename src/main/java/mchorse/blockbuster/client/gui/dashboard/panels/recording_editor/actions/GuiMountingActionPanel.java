package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.recording.actions.MountingAction;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiMountingActionPanel extends GuiActionPanel<MountingAction>
{
    public GuiButtonElement<GuiCheckBox> mounting;

    public GuiMountingActionPanel(Minecraft mc)
    {
        super(mc);

        this.mounting = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.record_editor.mounting"), false, (b) -> this.action.isMounting = b.button.isChecked());
        this.mounting.resizer().set(10, 0, 60, 11).parent(this.area).y(1, -21);

        this.children.add(this.mounting);
    }

    @Override
    public void fill(MountingAction action)
    {
        super.fill(action);

        this.mounting.button.setIsChecked(action.isMounting);
    }
}
