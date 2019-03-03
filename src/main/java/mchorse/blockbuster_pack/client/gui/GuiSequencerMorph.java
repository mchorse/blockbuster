package mchorse.blockbuster_pack.client.gui;

import java.util.function.Consumer;

import mchorse.blockbuster.client.gui.elements.GuiCreativeMorphsMenu;
import mchorse.blockbuster_pack.morphs.SequencerMorph;
import mchorse.blockbuster_pack.morphs.SequencerMorph.SequenceEntry;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.elements.GuiAbstractMorph;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiSequencerMorph extends GuiAbstractMorph
{
    private GuiDelegateElement<IGuiElement> view;
    public GuiButtonElement<GuiButton> toggleNbt;

    private GuiElements<IGuiElement> general = new GuiElements<IGuiElement>();
    private GuiElements<IGuiElement> elements = new GuiElements<IGuiElement>();

    private GuiListElement<SequenceEntry> list;
    private GuiButtonElement<GuiButton> addPart;
    private GuiButtonElement<GuiButton> removePart;

    private GuiButtonElement<GuiButton> pick;
    private GuiTrackpadElement duration;
    private GuiButtonElement<GuiCheckBox> reverse;
    private GuiCreativeMorphs morphPicker;

    private SequenceEntry entry;

    public GuiSequencerMorph(Minecraft mc)
    {
        super(mc);

        this.view = new GuiDelegateElement<IGuiElement>(mc, this.general);
        this.list = new GuiSequenceEntryList(mc, (entry) -> this.select(entry));
        this.addPart = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.add"), (b) ->
        {
            SequenceEntry entry = new SequenceEntry(null);

            this.list.getList().add(entry);
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
                IMorphing morphing = Morphing.get(this.mc.thePlayer);

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

        this.reverse = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.sequencer.reverse"), false, (b) ->
        {
            this.getMorph().reverse = b.button.isChecked();
        });

        this.pick.resizer().parent(this.area).set(0, 10, 105, 20).x(1, -115);
        this.addPart.resizer().parent(this.area).set(10, 10, 50, 20);
        this.removePart.resizer().relative(this.addPart.resizer()).set(55, 0, 50, 20);
        this.list.resizer().parent(this.area).set(10, 50, 105, 0).h(1, -85);
        this.duration.resizer().relative(this.pick.resizer()).set(0, 25, 105, 20);
        this.reverse.resizer().relative(this.removePart.resizer()).set(55, 4, this.reverse.button.width, 11);

        this.toggleNbt = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.builder.nbt"), (b) -> this.toggleNbt());
        this.toggleNbt.resizer().parent(this.area).set(0, 10, 40, 20).x(1, -50).y(1, -25);

        this.elements.add(this.pick, this.duration);
        this.general.add(this.addPart, this.removePart, this.list, this.reverse, this.elements);
        this.children.add(this.view, this.toggleNbt);

        this.data.setVisible(false);
        this.data.resizer().y(1, -55);

        this.finish.resizer().parent(this.area).set(10, 0, 105, 20).y(1, -25);
    }

    private void setMorph(AbstractMorph morph)
    {
        if (this.entry != null)
        {
            this.entry.morph = morph;
            this.getMorph().currentMorph = morph;
        }
    }

    private void toggleNbt()
    {
        if (this.view.delegate == null)
        {
            this.startEdit(this.morph);
            this.view.setDelegate(this.general);
            this.data.setVisible(false);
        }
        else
        {
            this.view.setDelegate(null);
            this.updateNBT();
            this.data.setVisible(true);
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
            this.getMorph().currentMorph = entry.morph;
        }

        this.elements.setVisible(entry != null);
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof SequencerMorph;
    }

    @Override
    public void startEdit(AbstractMorph morph)
    {
        super.startEdit(morph);

        SequencerMorph seq = this.getMorph();

        this.list.setList(seq.morphs);
        this.list.current = -1;

        this.elements.setVisible(false);
    }

    public SequencerMorph getMorph()
    {
        return (SequencerMorph) this.morph;
    }

    @Override
    protected void drawMorph(int mouseX, int mouseY, float partialTicks)
    {
        try
        {
            AbstractMorph morph = this.getMorph().currentMorph;

            if (morph != null)
            {
                morph.renderOnScreen(this.mc.thePlayer, this.area.getX(0.5F), this.area.getY(0.66F), this.area.h / 3, 1);
            }
        }
        catch (Exception e)
        {}
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        if (this.view.delegate == this.general)
        {
            this.list.area.draw(0x88000000);
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.sequencer.morphs"), this.list.area.x, this.list.area.y - 12, 0xffffff);
        }

        super.draw(tooltip, mouseX, mouseY, partialTicks);
    }

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