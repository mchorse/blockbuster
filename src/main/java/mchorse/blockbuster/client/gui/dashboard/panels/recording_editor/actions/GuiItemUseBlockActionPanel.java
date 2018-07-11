package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiThreeElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiButtonElement;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiCirculate;
import mchorse.blockbuster.recording.actions.ItemUseBlockAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class GuiItemUseBlockActionPanel extends GuiItemUseActionPanel<ItemUseBlockAction>
{
    public GuiButtonElement<GuiCirculate> facing;
    public GuiThreeElement block;
    public GuiThreeElement hit;

    public GuiItemUseBlockActionPanel(Minecraft mc)
    {
        super(mc);

        this.facing = new GuiButtonElement<GuiCirculate>(mc, new GuiCirculate(0, 0, 0, 0, 0), (b) -> this.action.facing = EnumFacing.values()[b.button.getValue()]);
        this.facing.button.addLabel(I18n.format("blockbuster.gui.record_editor.actions.use_item_block.down"));
        this.facing.button.addLabel(I18n.format("blockbuster.gui.record_editor.actions.use_item_block.up"));
        this.facing.button.addLabel(I18n.format("blockbuster.gui.record_editor.actions.use_item_block.north"));
        this.facing.button.addLabel(I18n.format("blockbuster.gui.record_editor.actions.use_item_block.south"));
        this.facing.button.addLabel(I18n.format("blockbuster.gui.record_editor.actions.use_item_block.west"));
        this.facing.button.addLabel(I18n.format("blockbuster.gui.record_editor.actions.use_item_block.east"));
        this.block = new GuiThreeElement(mc, (values) -> this.action.pos = new BlockPos(values[0], values[1], values[2]));
        this.block.a.trackpad.integer = true;
        this.block.b.trackpad.integer = true;
        this.block.c.trackpad.integer = true;
        this.hit = new GuiThreeElement(mc, (values) ->
        {
            this.action.hitX = values[0];
            this.action.hitY = values[1];
            this.action.hitZ = values[2];
        });

        this.hit.resizer().set(0, -25, 100, 20).relative(this.hand.resizer());
        this.block.resizer().set(0, -25, 100, 20).relative(this.hit.resizer());
        this.facing.resizer().set(0, -25, 70, 20).relative(this.block.resizer());

        this.children.add(this.facing, this.block, this.hit);
    }

    @Override
    public void fill(ItemUseBlockAction action)
    {
        super.fill(action);

        this.facing.button.setValue(action.facing.ordinal());
        this.block.setValues(action.pos.getX(), action.pos.getY(), action.pos.getZ());
        this.hit.setValues(action.hitX, action.hitY, action.hitZ);
    }
}