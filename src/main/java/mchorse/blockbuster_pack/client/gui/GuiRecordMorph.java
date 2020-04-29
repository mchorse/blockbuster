package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster_pack.morphs.RecordMorph;
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
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
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
        private GuiButtonElement pick;
        private GuiToggleElement loop;
        private GuiTrackpadElement randomSkip;
        private GuiCreativeMorphs morphPicker;

        public GuiRecordMorphPanel(Minecraft mc, GuiRecordMorph editor)
        {
            super(mc, editor);

            this.records = new GuiStringSearchListElement(mc, (str) -> this.morph.setRecord(str.get(0)));
            this.records.list.background();
            this.pick = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.pick"), (b) ->
            {
                RecordMorph record = this.morph;

                this.editor.morphs.nestEdit(record.initial, (morph) ->
                {
                    record.initial = MorphUtils.copy(morph);
                });
            });
            this.loop = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.loops"), true, (b) ->
            {
                this.morph.loop = this.loop.isToggled();
            });
            this.randomSkip = new GuiTrackpadElement(mc, (value) -> this.morph.randomSkip = value.intValue());
            this.randomSkip.tooltip(IKey.lang("blockbuster.gui.record_morph.random_skip"));
            this.randomSkip.limit(0, Integer.MAX_VALUE, true);

            this.records.flex().relative(this.area).set(10, 25, 105, 20).h(1, -35 - 25 - 16 - 25);
            this.pick.flex().relative(this.records.resizer()).set(0, 0, 105, 20).y(1, 5);
            this.loop.flex().relative(this.pick.resizer()).set(0, 25, 105, 20);
            this.randomSkip.flex().relative(this.loop.resizer()).set(0, 16, 100, 20);

            this.add(this.pick, this.loop, this.randomSkip, this.records);
        }

        @Override
        public void fillData(RecordMorph morph)
        {
            super.fillData(morph);

            this.records.clear();

            if (ClientProxy.panels.recordingEditorPanel != null)
            {
                this.records.list.add(ClientProxy.panels.recordingEditorPanel.records.records.list.getList());
                this.records.filter("", true);
            }

            this.records.list.setCurrent(morph.record);
            this.loop.toggled(morph.loop);
            this.randomSkip.setValue(morph.randomSkip);

            if (this.morphPicker != null)
            {
                this.morphPicker.setSelected(morph.initial);
            }
        }

        @Override
        public void draw(GuiContext context)
        {
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.director.id"), this.records.area.x, this.records.area.y - 12, 0xcccccc);
            super.draw(context);
        }
    }
}