package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.GuiRecordingEditorPanel;
import mchorse.blockbuster.recording.actions.HotbarChangeAction;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class GuiHotbarChangeActionPanel extends GuiActionPanel<HotbarChangeAction>
{
    public GuiSlotElement item;
    public GuiTrackpadElement slot;
    public GuiTrackpadElement durability;

    public GuiHotbarChangeActionPanel(Minecraft mc, GuiRecordingEditorPanel panel)
    {
        super(mc, panel);

        this.slot = new GuiTrackpadElement(mc, (b) -> this.action.setSlot(b.intValue()));
        this.slot.integer().limit(0,8);
        this.slot.flex().relative(this).xy(0.5F, 0.65F).anchor(0.5F, 0.5F).w(75);
        this.slot.tooltip(IKey.lang("blockbuster.gui.record_editor.actions.hotbar_change.slot_tooltip"));

        this.durability = new GuiTrackpadElement(mc, (b) ->
        {
            if (this.item.getStack().getMaxDamage() != 0)
            {
                ItemStack newItemStack = this.item.getStack().copy();

                newItemStack.setItemDamage((int) ((1 - b.floatValue() / 100F) * this.item.getStack().getMaxDamage()));

                this.action.setItemStack(newItemStack);
                this.item.setStack(newItemStack);
            }
        });
        this.durability.limit(0,100);
        this.durability.flex().relative(this).xy(0.5F, 0.75F).anchor(0.5F, 0.5F).w(75);
        this.durability.tooltip(IKey.lang("blockbuster.gui.record_editor.actions.hotbar_change.durability_tooltip"));

        this.item = new GuiSlotElement(mc,0, this::pickItem);
        this.item.flex().relative(this).xy(0.5F, 0.5F).anchor(0.5F, 0.5F);

        this.add(this.item, this.slot);
    }

    public void pickItem(ItemStack stack)
    {
        this.action.setItemStack(stack);
        this.item.setStack(stack);

        this.updateFields();
    }

    protected void updateFields()
    {
        this.durability.removeFromParent();

        if (this.item.getStack().getMaxDamage() != 0)
        {
            this.add(this.durability);
        }

        double durability = (this.item.getStack().getMaxDamage() == 0) ? 1 : (1 - (double) this.item.getStack().getItemDamage() / (double) this.item.getStack().getMaxDamage());

        this.durability.setValue(durability * 100D);

        if (this.parent != null)
        {
            this.parent.resize();
        }
    }

    @Override
    public void fill(HotbarChangeAction action)
    {
        super.fill(action);

        this.slot.setValue(action.getSlot());
        this.item.setStack(action.getItemStack() == null ? ItemStack.EMPTY : action.getItemStack().copy());

        this.updateFields();
    }
}
