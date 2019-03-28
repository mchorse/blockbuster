package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster_pack.morphs.RecordMorph;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringSearchListElement;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.elements.GuiAbstractMorph;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphsMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiRecordMorph extends GuiAbstractMorph
{
    private GuiDelegateElement<IGuiElement> view;
    public GuiButtonElement<GuiButton> toggleNbt;

    private GuiElements<IGuiElement> general = new GuiElements<IGuiElement>();
    private GuiElements<IGuiElement> elements = new GuiElements<IGuiElement>();

    private GuiStringSearchListElement records;
    private GuiButtonElement<GuiButton> pick;
    private GuiCreativeMorphs morphPicker;

    public GuiRecordMorph(Minecraft mc)
    {
        super(mc);

        this.view = new GuiDelegateElement<IGuiElement>(mc, this.general);
        this.records = new GuiStringSearchListElement(mc, (str) -> this.getMorph().setRecord(str));
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
            this.morphPicker.setSelected(this.getMorph().initial);
            this.morphPicker.setVisible(true);
        });

        this.records.resizer().parent(this.area).set(10, 25, 105, 20).h(1, -85);
        this.pick.resizer().parent(this.area).set(10, 0, 105, 20).y(1, -55);

        this.toggleNbt = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.builder.nbt"), (b) -> this.toggleNbt());
        this.toggleNbt.resizer().parent(this.area).set(0, 10, 40, 20).x(1, -50).y(1, -25);

        this.elements.add(this.pick, this.records);
        this.general.add(this.elements);
        this.children.add(this.view, this.toggleNbt);

        this.data.setVisible(false);
        this.data.resizer().y(1, -55);

        this.finish.resizer().parent(this.area).set(10, 0, 105, 20).y(1, -25);
    }

    private void setMorph(AbstractMorph morph)
    {
        this.getMorph().initial = morph;
    }

    public RecordMorph getMorph()
    {
        return (RecordMorph) this.morph;
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

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof RecordMorph;
    }

    @Override
    public void startEdit(AbstractMorph morph)
    {
        super.startEdit(morph);

        RecordMorph record = this.getMorph();

        this.records.elements.clear();

        if (ClientProxy.dashboard != null && ClientProxy.dashboard.recordingEditorPanel != null)
        {
            this.records.elements.addAll(ClientProxy.dashboard.recordingEditorPanel.records.records.elements);
            this.records.filter("", true);
        }

        this.records.list.setCurrent(record.record);

        if (this.morphPicker != null)
        {
            this.morphPicker.setSelected(record.initial);
        }
    }

    @Override
    protected void drawMorph(int mouseX, int mouseY, float partialTicks)
    {
        try
        {
            this.getMorph().initial.renderOnScreen(this.mc.player, this.area.getX(0.5F), this.area.getY(0.66F), this.area.h / 3, 1);
        }
        catch (Exception e)
        {}
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        this.records.area.draw(0x88000000);
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.id"), this.records.area.x, this.records.area.y - 12, error ? 0xffff3355 : 0xcccccc);
        super.draw(tooltip, mouseX, mouseY, partialTicks);
    }
}