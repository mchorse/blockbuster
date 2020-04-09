package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.recording.actions.InteractBlockAction;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;

public class GuiBlockActionPanel<T extends InteractBlockAction> extends GuiActionPanel<T>
{
    public GuiTrackpadElement x;
    public GuiTrackpadElement y;
    public GuiTrackpadElement z;

    public GuiBlockActionPanel(Minecraft mc)
    {
        super(mc);

        this.x = new GuiTrackpadElement(mc, (v) -> this.action.pos = new BlockPos(v.intValue(), this.action.pos.getY(), this.action.pos.getZ()));
        this.x.tooltip(I18n.format("blockbuster.gui.model_block.x"));
        this.y = new GuiTrackpadElement(mc, (v) -> this.action.pos = new BlockPos(this.action.pos.getX(), v.intValue(), this.action.pos.getZ()));
        this.y.tooltip(I18n.format("blockbuster.gui.model_block.y"));
        this.z = new GuiTrackpadElement(mc, (v) -> this.action.pos = new BlockPos(this.action.pos.getX(), this.action.pos.getY(), v.intValue()));
        this.z.tooltip(I18n.format("blockbuster.gui.model_block.z"));

        this.x.integer = this.y.integer = this.z.integer = true;

        this.x.flex().set(10, 0, 80, 20).relative(this.area).y(1, -80);
        this.y.flex().set(0, 25, 80, 20).relative(this.x.resizer());
        this.z.flex().set(0, 25, 80, 20).relative(this.y.resizer());

        this.add(this.x, this.y, this.z);
    }

    @Override
    public void fill(T action)
    {
        super.fill(action);

        this.x.setValue(action.pos.getX());
        this.y.setValue(action.pos.getY());
        this.z.setValue(action.pos.getZ());
    }
}
