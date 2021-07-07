package mchorse.blockbuster_pack.client.gui;

import java.util.List;

import com.google.common.collect.ImmutableList;

import mchorse.blockbuster.utils.mclib.BBIcons;
import mchorse.blockbuster_pack.client.gui.trackers.GuiBaseTracker;
import mchorse.blockbuster_pack.morphs.TrackerMorph;
import mchorse.blockbuster_pack.trackers.ApertureTracker;
import mchorse.blockbuster_pack.trackers.BaseTracker;
import mchorse.blockbuster_pack.trackers.TrackerRegistry;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;

public class GuiTrackerMorph extends GuiAbstractMorph<TrackerMorph>
{
    public GuiTrackerMorph(Minecraft mc)
    {
        super(mc);

        this.registerPanel(this.defaultPanel = new GuiTrackerMorphPanel(mc, this), IKey.lang("metamorph.gui.edit"), BBIcons.EDITOR);
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof TrackerMorph;
    }

    public static class GuiTrackerMorphPanel extends GuiMorphPanel<TrackerMorph, GuiTrackerMorph>
    {
        public GuiCirculateElement type;
        public GuiToggleElement hidden;
        public GuiDelegateElement<GuiBaseTracker> trackerPanel;

        private List<String> trackers;

        public GuiTrackerMorphPanel(Minecraft mc, GuiTrackerMorph editor)
        {
            super(mc, editor);

            GuiLabel typeTitle = Elements.label(IKey.lang("blockbuster.gui.tracker_morph.type.title"));
//            typeTitle.flex().relative(this).xy(10, 10);

            this.type = new GuiCirculateElement(mc, (element) ->
            {
                BaseTracker tracker = this.morph.tracker;
                Class<? extends BaseTracker> clazz = TrackerRegistry.ID_TO_CLASS.get(this.trackers.get(element.getValue()));

                if (clazz != null)
                {
                    try
                    {
                        this.morph.tracker = clazz.newInstance();
                        this.morph.tracker.copy(tracker);
                    }
                    catch (InstantiationException | IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }
                }

                this.updateTracker();
            });
//            this.type.flex().relative(typeTitle).w(150).x(0F).y(1F, 5).anchor(0F, 0F);

            this.trackers = ImmutableList.copyOf(TrackerRegistry.ID_TO_CLASS.keySet());

            for (String tracker : this.trackers)
            {
                this.type.addLabel(IKey.lang("blockbuster.gui.tracker_morph.type." + tracker));
            }

            //this.type.addLabel(IKey.EMPTY);

            this.hidden = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.tracker_morph.hidden"), element ->
            {
                this.morph.hidden = element.isToggled();
            });
//            this.hidden.flex().relative(this.type).w(150).x(0F).y(1F, 10).anchor(0F, 0F);

            GuiElement elements = Elements.column(mc, 10, 10, Elements.label(IKey.lang("blockbuster.gui.tracker_morph.type.title")), this.type, this.hidden);
            elements.flex().relative(this).xy(0, 0).w(170);

            this.trackerPanel = new GuiDelegateElement<GuiBaseTracker>(mc, null);
            this.trackerPanel.flex().relative(elements).x(0).y(1F).wTo(this.area, 1F).hTo(this.area, 1F);

            this.add(elements, this.trackerPanel);
        }

        @Override
        public void fillData(TrackerMorph morph)
        {
            super.fillData(morph);

            this.hidden.toggled(morph.hidden);
            this.updateTracker();
        }

        private void updateTracker()
        {
            this.trackerPanel.setDelegate(null);
            this.type.setValue(0);

            if (this.morph.tracker == null)
            {
                this.morph.tracker = new ApertureTracker();
            }

            this.type.setValue(this.trackers.indexOf(TrackerRegistry.CLASS_TO_ID.get(this.morph.tracker.getClass())));
            this.trackerPanel.setDelegate(TrackerRegistry.CLIENT.get(this.morph.tracker.getClass()));

            if (this.trackerPanel.delegate != null)
            {
                this.trackerPanel.delegate.fill(this.morph.tracker);
            }
        }
    }
}
