package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiThreeElement;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.GuiRecordingEditorPanel;
import mchorse.blockbuster.recording.actions.ItemUseBlockAction;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class GuiItemUseBlockActionPanel extends GuiItemUseActionPanel<ItemUseBlockAction>
{
    public GuiCirculateElement facing;
    public GuiThreeElement block;
    public GuiThreeElement hit;

    public GuiItemUseBlockActionPanel(Minecraft mc, GuiRecordingEditorPanel panel)
    {
        super(mc, panel);
    }

    @Override
    public void fill(ItemUseBlockAction action)
    {
        super.fill(action);

        this.facing.setValue(action.facing.ordinal());
        this.block.setValues(action.pos.getX(), action.pos.getY(), action.pos.getZ());
        this.hit.setValues(action.hitX, action.hitY, action.hitZ);
    }
}