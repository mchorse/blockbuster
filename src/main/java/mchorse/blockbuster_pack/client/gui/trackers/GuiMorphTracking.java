package mchorse.blockbuster_pack.client.gui.trackers;

import mchorse.blockbuster_pack.trackers.MorphTracker;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiMorphTracking extends GuiBaseTracker<MorphTracker>
{
    public GuiToggleElement combineTracking;

    public GuiMorphTracking(Minecraft mc)
    {
        super(mc);

        this.combineTracking = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.tracker_morph.aperture_tracker.combine_tracking"), (toggle) ->
        {
            this.tracker.setCombineTracking(toggle.isToggled());
        });
        this.combineTracking.flex().relative(name).w(150).x(0F).y(1F, 5).anchor(0F, 0F);
        this.combineTracking.tooltip(IKey.lang("blockbuster.gui.tracker_morph.aperture_tracker.combine_tracking_tooltip"));

        this.add(this.combineTracking);
    }

    @Override
    public void fill(MorphTracker tracker)
    {
        super.fill(tracker);

        this.combineTracking.toggled(tracker.getCombineTracking());
    }
}
