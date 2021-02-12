package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster_pack.morphs.RecordMorph;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringSearchListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphs;
import mchorse.metamorph.client.gui.creative.GuiMorphRenderer;
import mchorse.metamorph.client.gui.creative.GuiNestedEdit;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiRecordMorph extends GuiAbstractMorph<RecordMorph>
{
    public GuiRecordMorphPanel general;

    public GuiRecordMorph(Minecraft mc)
    {
        super(mc);

        this.defaultPanel = this.general = new GuiRecordMorphPanel(mc, this);
        this.registerPanel(this.general, IKey.lang("blockbuster.morph.record"), Icons.GEAR);
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof RecordMorph;
    }

    @SideOnly(Side.CLIENT)
    public static class GuiRecordMorphPanel extends GuiMorphPanel<RecordMorph, GuiRecordMorph>
    {
        private GuiStringSearchListElement records;
        private GuiNestedEdit pick;
        private GuiToggleElement loop;
        private GuiTrackpadElement randomSkip;

        public GuiRecordMorphPanel(Minecraft mc, GuiRecordMorph editor)
        {
            super(mc, editor);

            this.records = new GuiStringSearchListElement(mc, (str) -> this.morph.setRecord(str.get(0)));
            this.records.list.background();
            this.pick = new GuiNestedEdit(mc, (editing) ->
            {
                RecordMorph record = this.morph;

                this.editor.morphs.nestEdit(record.initial, editing, (morph) ->
                {
                    record.initial = MorphUtils.copy(morph);
                    ((GuiMorphRenderer) this.editor.renderer).morph = record.initial;
                });
            });
            this.loop = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.loops"), true, (b) ->
            {
                this.morph.loop = this.loop.isToggled();
            });
            this.randomSkip = new GuiTrackpadElement(mc, (value) -> this.morph.randomSkip = value.intValue());
            this.randomSkip.tooltip(IKey.lang("blockbuster.gui.record_morph.random_skip"));
            this.randomSkip.limit(0, Integer.MAX_VALUE, true);

            GuiElement element = new GuiElement(mc);

            element.flex().relative(this).y(1F).w(130).anchorY(1F).column(5).stretch().vertical().height(20).padding(10);
            element.add(this.pick, this.loop, this.randomSkip);

            this.records.flex().relative(this).set(10, 25, 110, 20).hTo(element.flex());

            this.add(element, this.records);
        }

        @Override
        public void fillData(RecordMorph morph)
        {
            super.fillData(morph);

            this.records.list.clear();

            if (ClientProxy.panels.recordingEditorPanel != null)
            {
                this.records.list.add(ClientProxy.panels.recordingEditorPanel.records.records.list.getList());
                this.records.filter("", true);
            }

            this.records.list.setCurrent(morph.record);
            this.loop.toggled(morph.loop);
            this.randomSkip.setValue(morph.randomSkip);

            ((GuiMorphRenderer) this.editor.renderer).morph = morph.initial;

            this.records.resize();
        }

        @Override
        public void draw(GuiContext context)
        {
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.id"), this.records.area.x, this.records.area.y - 12, 0xcccccc);
            super.draw(context);
        }
    }
}