package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.recording.actions.PlaceBlockAction;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
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
        this.meta = new GuiTrackpadElement(mc, (value) -> this.action.metadata = value.byteValue());
        this.meta.tooltip(I18n.format("blockbuster.gui.record_editor.meta"));

        this.block.flex().set(0, -30, 100, 20).relative(this.meta.resizer());
        this.meta.flex().set(0, -30, 100, 20).relative(this.x.resizer());
        this.meta.limit(0, 15, true);

        this.add(this.block, this.meta);
    }

    @Override
    public void fill(PlaceBlockAction action)
    {
        super.fill(action);

        this.block.setText(action.block);
        this.meta.setValue(action.metadata);
    }
}