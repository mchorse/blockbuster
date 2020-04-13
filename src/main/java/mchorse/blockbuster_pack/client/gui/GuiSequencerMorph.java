package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster_pack.morphs.SequencerMorph;
import mchorse.blockbuster_pack.morphs.SequencerMorph.SequenceEntry;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphs;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphsMenu;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import java.util.List;
import java.util.function.Consumer;

public class GuiSequencerMorph extends GuiAbstractMorph<SequencerMorph>
{
    public GuiSequencerMorphPanel general;

    public GuiSequencerMorph(Minecraft mc)
    {
        super(mc);

        this.defaultPanel = this.general = new GuiSequencerMorphPanel(mc, this);
        this.registerPanel(this.general, I18n.format("blockbuster.morph.sequencer"), Icons.GEAR);
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof SequencerMorph;
    }

    /**
     * Sequencer morph panel 
     */
    public static class GuiSequencerMorphPanel extends GuiMorphPanel<SequencerMorph, GuiSequencerMorph>
    {
        public GuiElement elements;

        private GuiListElement<SequenceEntry> list;
        private GuiButtonElement addPart;
        private GuiButtonElement removePart;

        private GuiButtonElement pick;
        private GuiTrackpadElement duration;
        private GuiTrackpadElement random;
        private GuiToggleElement reverse;
        private GuiToggleElement randomOrder;

        public SequenceEntry entry;

        public GuiSequencerMorphPanel(Minecraft mc, GuiSequencerMorph editor)
        {
            super(mc, editor);

            this.elements = new GuiElement(mc);

            this.list = new GuiSequenceEntryList(mc, (entry) -> this.select(entry.get(0)));
            this.list.background();
            this.addPart = new GuiButtonElement(mc, I18n.format("blockbuster.gui.add"), (b) ->
            {
                SequenceEntry current = this.list.getCurrentFirst();
                SequenceEntry entry = new SequenceEntry(current == null ? null : current.morph.clone(true));

                if (current != null)
                {
                    entry.duration = current.duration;
                    entry.random = current.random;
                }

                this.list.getList().add(entry);
                this.list.setIndex(this.list.getList().size() - 1);
                this.select(entry);
                this.list.update();
            });

            this.removePart = new GuiButtonElement(mc, I18n.format("blockbuster.gui.remove"), (b) ->
            {
                if (!this.list.isDeselected())
                {
                    this.list.getList().remove(this.list.getCurrentFirst());
                    this.list.setIndex(this.list.getIndex() - 1);

                    this.select(this.list.getCurrentFirst());
                    this.list.update();
                }
            });

            this.pick = new GuiButtonElement(mc, I18n.format("blockbuster.gui.pick"), (b) ->
            {
                if (this.entry == null)
                {
                    return;
                }

                SequenceEntry entry = this.entry;

                this.editor.morphs.nestEdit(entry.morph, (morph) ->
                {
                    entry.morph = morph;
                });
            });

            this.duration = new GuiTrackpadElement(mc, (value) ->
            {
                if (this.entry != null)
                {
                    this.entry.duration = value;
                }
            });
            this.duration.tooltip(I18n.format("blockbuster.gui.sequencer.duration"));
            this.duration.limit(0, Float.MAX_VALUE);

            this.random = new GuiTrackpadElement(mc, (value) ->
            {
                if (this.entry != null)
                {
                    this.entry.random = value;
                }
            });
            this.random.tooltip(I18n.format("blockbuster.gui.sequencer.random"));
            this.random.limit(0, Float.MAX_VALUE);

            this.reverse = new GuiToggleElement(mc, I18n.format("blockbuster.gui.sequencer.reverse"), false, (b) ->
            {
                this.morph.reverse = b.isToggled();
            });

            this.randomOrder = new GuiToggleElement(mc, I18n.format("blockbuster.gui.sequencer.random_order"), false, (b) ->
            {
                this.morph.random = b.isToggled();
            });

            this.pick.flex().relative(this.area).set(0, 0, 105, 20).x(1, -115);
            this.addPart.flex().relative(this.area).set(10, 10, 50, 20);
            this.removePart.flex().relative(this.addPart.resizer()).set(55, 0, 50, 20);
            this.list.flex().relative(this.area).set(10, 50, 105, 0).h(1, -60);
            this.duration.flex().relative(this.pick.resizer()).set(0, 25, 105, 20);
            this.random.flex().relative(this.duration.resizer()).set(0, 25, 105, 20);
            this.reverse.flex().relative(this.removePart.resizer()).set(55, 4, 105, 11);
            this.randomOrder.flex().relative(this.reverse.resizer()).set(0, 0, 105, 11).y(1, 5);

            this.pick.flex().y(1, -(this.random.resizer().getY() + this.random.resizer().getH() + 10));

            this.elements.add(this.pick, this.duration, this.random);
            this.add(this.addPart, this.removePart, this.list, this.reverse, this.randomOrder, this.elements);
        }

        private void select(SequenceEntry entry)
        {
            this.entry = entry;

            if (entry != null)
            {
                this.duration.setValue(entry.duration);
                this.random.setValue(entry.random);
            }

            this.elements.setVisible(entry != null);
        }

        @Override
        public void fillData(SequencerMorph morph)
        {
            super.fillData(morph);

            this.list.setList(morph.morphs);
            this.list.setIndex(-1);
            this.reverse.toggled(morph.reverse);
            this.randomOrder.toggled(morph.random);

            this.elements.setVisible(false);
        }

        @Override
        public void draw(GuiContext context)
        {
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.sequencer.morphs"), this.list.area.x, this.list.area.y - 12, 0xffffff);

            super.draw(context);
        }
    }

    /**
     * List that shows up the sequencer entries 
     */
    public static class GuiSequenceEntryList extends GuiListElement<SequenceEntry>
    {
        public GuiSequenceEntryList(Minecraft mc, Consumer<List<SequenceEntry>> callback)
        {
            super(mc, callback);

            this.scroll.scrollItemSize = 16;
        }

        @Override
        protected String elementToString(SequenceEntry element, int i, int x, int y, boolean hover, boolean selected)
        {
            String title = I18n.format("blockbuster.gui.sequencer.no_morph");

            if (element.morph != null)
            {
                title = element.morph.name;
            }

            return title;
        }
    }
}