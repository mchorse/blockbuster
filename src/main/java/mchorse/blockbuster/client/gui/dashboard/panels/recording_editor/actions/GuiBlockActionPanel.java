package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.recording.actions.InteractBlockAction;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
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

        this.x = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (v) -> this.action.pos = new BlockPos(v.intValue(), this.action.pos.getY(), this.action.pos.getZ()));
        this.y = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (v) -> this.action.pos = new BlockPos(this.action.pos.getX(), v.intValue(), this.action.pos.getZ()));
        this.z = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (v) -> this.action.pos = new BlockPos(this.action.pos.getX(), this.action.pos.getY(), v.intValue()));

        this.x.trackpad.integer = this.y.trackpad.integer = this.z.trackpad.integer = true;

        this.x.resizer().set(10, 0, 80, 20).parent(this.area).y(1, -80);
        this.y.resizer().set(0, 25, 80, 20).relative(this.x.resizer());
        this.z.resizer().set(0, 25, 80, 20).relative(this.y.resizer());

        this.children.add(this.x, this.y, this.z);
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
