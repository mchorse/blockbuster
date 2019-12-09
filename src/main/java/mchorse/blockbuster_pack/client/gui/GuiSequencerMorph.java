package mchorse.blockbuster_pack.client.gui;

import java.util.function.Consumer;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster_pack.morphs.SequencerMorph;
import mchorse.blockbuster_pack.morphs.SequencerMorph.SequenceEntry;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphsMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiSequencerMorph extends GuiAbstractMorph<SequencerMorph>
{
    public GuiSequencerMorphPanel general;

    public GuiSequencerMorph(Minecraft mc)
    {
        super(mc);

        this.defaultPanel = this.general = new GuiSequencerMorphPanel(mc, this);
        this.registerPanel(this.general, GuiDashboard.GUI_ICONS, I18n.format("blockbuster.morph.sequencer"), 48, 0, 48, 16);
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof SequencerMorph;
    }

    @Override
    protected void drawMorph(int mouseX, int mouseY, float partialTicks)
    {
        try
        {
            AbstractMorph morph = this.general.entry == null ? null : this.general.entry.morph;

            if (morph != null)
            {
                morph.renderOnScreen(this.mc.player, this.area.getX(0.5F), this.area.getY(0.66F), this.area.h / 3, 1);
            }
        }
        catch (Exception e)
        {}
    }

    /**
     * Sequencer morph panel 
     */
    public static class GuiSequencerMorphPanel extends GuiMorphPanel<SequencerMorph, GuiSequencerMorph>
    {
        public GuiElements<IGuiElement> elements = new GuiElements<IGuiElement>();

        private GuiListElement<SequenceEntry> list;
        private GuiButtonElement<GuiButton> addPart;
        private GuiButtonElement<GuiButton> removePart;

        private GuiButtonElement<GuiButton> pick;
        private GuiTrackpadElement duration;
        private GuiTrackpadElement random;
        private GuiButtonElement<GuiCheckBox> reverse;
        private GuiButtonElement<GuiCheckBox> randomOrder;
        private GuiCreativeMorphs morphPicker;

        public SequenceEntry entry;

        public GuiSequencerMorphPanel(Minecraft mc, GuiSequencerMorph editor)
        {
            super(mc, editor);

            this.list = new GuiSequenceEntryList(mc, (entry) -> this.select(entry));
            this.list.setBackground();
            this.addPart = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.add"), (b) ->
            {
                SequenceEntry current = this.list.getCurrent();
                SequenceEntry entry = new SequenceEntry(current == null ? null : current.morph.clone(true));

                if (current != null)
                {
                    entry.duration = current.duration;
                    entry.random = current.random;
                }

                this.list.getList().add(entry);
                this.list.current = this.list.getList().size() - 1;
                this.select(entry);
                this.list.update();
            });

            this.removePart = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.remove"), (b) ->
            {
                if (this.list.getCurrent() != null)
                {
                    this.list.getList().remove(this.list.current);
                    this.list.current--;

                    this.select(this.list.getCurrent());
                    this.list.update();
                }
            });

            this.pick = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.pick"), (b) ->
            {
                if (this.morphPicker == null)
                {
                    IMorphing morphing = Morphing.get(this.mc.player);

                    this.morphPicker = new GuiCreativeMorphsMenu(mc, 6, null, morphing);
                    this.morphPicker.resizer().parent(this.area).set(0, 0, 0, 0).w(1, 0).h(1, 0);
                    this.morphPicker.callback = (morph) -> this.setMorph(morph);

                    GuiScreen screen = Minecraft.getMinecraft().currentScreen;

                    this.morphPicker.resize(screen.width, screen.height);
                    this.children.add(this.morphPicker);
                }

                this.children.unfocus();
                this.morphPicker.setSelected(this.entry == null ? null : this.entry.morph);
                this.morphPicker.setVisible(true);
            });

            this.duration = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.sequencer.duration"), (value) ->
            {
                if (this.entry != null)
                {
                    this.entry.duration = value;
                }
            });
            this.duration.setLimit(0, Float.MAX_VALUE, false);

            this.random = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.sequencer.random"), (value) ->
            {
                if (this.entry != null)
                {
                    this.entry.random = value;
                }
            });
            this.random.setLimit(0, Float.MAX_VALUE, false);

            this.reverse = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.sequencer.reverse"), false, (b) ->
            {
                this.morph.reverse = b.button.isChecked();
            });

            this.randomOrder = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.sequencer.random_order"), false, (b) ->
            {
                this.morph.random = b.button.isChecked();
            });

            this.pick.resizer().parent(this.area).set(0, 0, 105, 20).x(1, -115);
            this.addPart.resizer().parent(this.area).set(10, 10, 50, 20);
            this.removePart.resizer().relative(this.addPart.resizer()).set(55, 0, 50, 20);
            this.list.resizer().parent(this.area).set(10, 50, 105, 0).h(1, -60);
            this.duration.resizer().relative(this.pick.resizer()).set(0, 25, 105, 20);
            this.random.resizer().relative(this.duration.resizer()).set(0, 25, 105, 20);
            this.reverse.resizer().relative(this.removePart.resizer()).set(55, 4, this.reverse.button.width, 11);
            this.randomOrder.resizer().relative(this.reverse.resizer()).set(0, 0, this.randomOrder.button.width, 11).y(1, 5);

            this.pick.resizer().y(1, -(this.random.resizer().getY() + this.random.resizer().getH() + 10));

            this.elements.add(this.pick, this.duration, this.random);
            this.children.add(this.addPart, this.removePart, this.list, this.reverse, this.randomOrder, this.elements);
        }

        private void setMorph(AbstractMorph morph)
        {
            if (this.entry != null)
            {
                this.entry.morph = morph;
            }
        }

        private void select(SequenceEntry entry)
        {
            this.entry = entry;

            if (entry != null)
            {
                if (this.morphPicker != null)
                {
                    this.morphPicker.setSelected(entry.morph);
                }

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
            this.list.current = -1;
            this.reverse.button.setIsChecked(morph.reverse);
            this.randomOrder.button.setIsChecked(morph.random);

            this.elements.setVisible(false);
        }

        @Override
        public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
        {
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.sequencer.morphs"), this.list.area.x, this.list.area.y - 12, 0xffffff);

            super.draw(tooltip, mouseX, mouseY, partialTicks);
        }
    }

    /**
     * List that shows up the sequencer entries 
     */
    public static class GuiSequenceEntryList extends GuiListElement<SequenceEntry>
    {
        public GuiSequenceEntryList(Minecraft mc, Consumer<SequenceEntry> callback)
        {
            super(mc, callback);

            this.scroll.scrollItemSize = 16;
        }

        @Override
        public void sort()
        {}

        @Override
        public void drawElement(SequenceEntry element, int i, int x, int y, boolean hover)
        {
            if (this.current == i)
            {
                Gui.drawRect(x, y, x + this.scroll.w, y + this.scroll.scrollItemSize, 0x880088ff);
            }

            String title = I18n.format("blockbuster.gui.sequencer.no_morph");

            if (element.morph != null)
            {
                title = element.morph.name;
            }

            this.font.drawStringWithShadow(title, x + 4, y + 4, hover ? 16777120 : 0xffffff);
        }
    }
}