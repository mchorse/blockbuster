package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster_pack.morphs.RecordMorph;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringSearchListElement;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphsMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiRecordMorph extends GuiAbstractMorph<RecordMorph>
{
    public GuiRecordMorphPanel general;

    public GuiRecordMorph(Minecraft mc)
    {
        super(mc);

        this.defaultPanel = this.general = new GuiRecordMorphPanel(mc, this);
        this.registerPanel(this.general, GuiDashboard.GUI_ICONS, I18n.format("blockbuster.morph.record"), 48, 0, 48, 16);
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof RecordMorph;
    }

    @Override
    protected void drawMorph(int mouseX, int mouseY, float partialTicks)
    {
        try
        {
            this.morph.initial.renderOnScreen(this.mc.player, this.area.getX(0.5F), this.area.getY(0.66F), this.area.h / 3, 1);
        }
        catch (Exception e)
        {}
    }

    @SideOnly(Side.CLIENT)
    public static class GuiRecordMorphPanel extends GuiMorphPanel<RecordMorph, GuiRecordMorph>
    {
        private GuiStringSearchListElement records;
        private GuiButtonElement<GuiButton> pick;
        private GuiCreativeMorphs morphPicker;

        public GuiRecordMorphPanel(Minecraft mc, GuiRecordMorph editor)
        {
            super(mc, editor);

            this.records = new GuiStringSearchListElement(mc, (str) -> this.morph.setRecord(str));
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
                this.morphPicker.setSelected(this.morph.initial);
                this.morphPicker.setVisible(true);
            });

            this.records.resizer().parent(this.area).set(10, 25, 105, 20).h(1, -60);
            this.pick.resizer().parent(this.area).set(10, 0, 105, 20).y(1, -30);

            this.children.add(this.pick, this.records);
        }

        private void setMorph(AbstractMorph morph)
        {
            this.morph.initial = morph;
        }

        @Override
        public void fillData(RecordMorph morph)
        {
            super.fillData(morph);

            this.records.elements.clear();

            if (ClientProxy.dashboard != null && ClientProxy.dashboard.recordingEditorPanel != null)
            {
                this.records.elements.addAll(ClientProxy.dashboard.recordingEditorPanel.records.records.elements);
                this.records.filter("", true);
            }

            this.records.list.setCurrent(morph.record);

            if (this.morphPicker != null)
            {
                this.morphPicker.setSelected(morph.initial);
            }
        }

        @Override
        public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
        {
            this.records.area.draw(0x88000000);
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.id"), this.records.area.x, this.records.area.y - 12, 0xcccccc);
            super.draw(tooltip, mouseX, mouseY, partialTicks);
        }
    }
}