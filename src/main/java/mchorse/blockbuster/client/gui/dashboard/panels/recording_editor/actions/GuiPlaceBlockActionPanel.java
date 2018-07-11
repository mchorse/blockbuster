package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.client.gui.framework.elements.GuiTextElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.blockbuster.recording.actions.PlaceBlockAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiPlaceBlockActionPanel extends GuiBlockActionPanel<PlaceBlockAction>
{
    public GuiTextElement block;
    public GuiTrackpadElement meta;

    public GuiPlaceBlockActionPanel(Minecraft mc)
    {
        super(mc);

        this.block = new GuiTextElement(mc, (str) -> this.action.block = str);
        this.meta = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.record_editor.meta"), (value) -> this.action.metadata = value.byteValue());

        this.block.resizer().set(0, -30, 100, 20).relative(this.meta.resizer());
        this.meta.resizer().set(0, -30, 100, 20).relative(this.x.resizer());
        this.meta.setLimit(0, 15, true);

        this.children.add(this.block, this.meta);
    }

    @Override
    public void fill(PlaceBlockAction action)
    {
        super.fill(action);

        this.block.setText(action.block);
        this.meta.setValue(action.metadata);
    }
}