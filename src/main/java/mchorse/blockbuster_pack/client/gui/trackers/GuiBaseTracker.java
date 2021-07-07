package mchorse.blockbuster_pack.client.gui.trackers;

import mchorse.blockbuster_pack.trackers.BaseTracker;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiBaseTracker<T extends BaseTracker> extends GuiElement
{
    public GuiTextElement name;

    public T tracker;

    public GuiBaseTracker(Minecraft mc)
    {
        super(mc);

        GuiLabel labelTitle = Elements.label(IKey.lang("blockbuster.gui.tracker_morph.label.title"));
        labelTitle.flex().relative(this).xy(10, 0);

        this.name = new GuiTextElement(mc, str -> this.tracker.name = str);
        this.name.flex().relative(labelTitle).w(150).x(0F).y(1F, 5).anchor(0F, 0F);

        this.add(labelTitle, this.name);
    }

    public void fill(T tracker)
    {
        this.tracker = tracker;

        this.name.setText(tracker.name);
    }
}
