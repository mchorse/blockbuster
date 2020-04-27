package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiThreeElement;
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

    public GuiItemUseBlockActionPanel(Minecraft mc)
    {
        super(mc);

        this.facing = new GuiCirculateElement(mc, (b) -> this.action.facing = EnumFacing.values()[this.facing.getValue()]);
        this.facing.addLabel(IKey.lang("blockbuster.gui.record_editor.actions.use_item_block.down"));
        this.facing.addLabel(IKey.lang("blockbuster.gui.record_editor.actions.use_item_block.up"));
        this.facing.addLabel(IKey.lang("blockbuster.gui.record_editor.actions.use_item_block.north"));
        this.facing.addLabel(IKey.lang("blockbuster.gui.record_editor.actions.use_item_block.south"));
        this.facing.addLabel(IKey.lang("blockbuster.gui.record_editor.actions.use_item_block.west"));
        this.facing.addLabel(IKey.lang("blockbuster.gui.record_editor.actions.use_item_block.east"));
        this.block = new GuiThreeElement(mc, (values) -> this.action.pos = new BlockPos(values[0], values[1], values[2]));
        this.block.a.integer = true;
        this.block.b.integer = true;
        this.block.c.integer = true;
        this.hit = new GuiThreeElement(mc, (values) ->
        {
            this.action.hitX = values[0];
            this.action.hitY = values[1];
            this.action.hitZ = values[2];
        });

        this.hit.flex().set(0, -25, 200, 20).relative(this.hand.resizer());
        this.block.flex().set(0, -25, 200, 20).relative(this.hit.resizer());
        this.facing.flex().set(0, -25, 70, 20).relative(this.block.resizer());

        this.add(this.facing, this.block, this.hit);
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