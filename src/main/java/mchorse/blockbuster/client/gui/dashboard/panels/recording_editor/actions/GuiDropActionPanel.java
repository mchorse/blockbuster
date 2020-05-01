package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.recording.actions.DropAction;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiInventoryElement;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GuiDropActionPanel extends GuiActionPanel<DropAction>
{
    public GuiInventoryElement inventory;
    public GuiSlotElement slot;

    public GuiDropActionPanel(Minecraft mc)
    {
        super(mc);

        this.inventory = new GuiInventoryElement(mc, this::pickItem);
        this.slot = new GuiSlotElement(mc,0, this::setSlot);

        this.slot.flex().relative(this.area).xy(0.5F, 0.5F).wh(24, 24).anchor(0.5F, 0.5F);
        this.inventory.flex().under(this.slot.flex(), 10).x(0.5F).anchorX(0.5F).wh(200, 100);
        this.add(this.slot, this.inventory);
    }

    private void setSlot(GuiSlotElement slot)
    {
        this.inventory.linked = slot;
        this.inventory.setVisible(true);
    }

    public void pickItem(ItemStack stack)
    {
        this.action.itemData = stack.isEmpty() ? null : stack.writeToNBT(new NBTTagCompound());
        this.slot.stack = stack;

        this.inventory.linked = null;
        this.inventory.setVisible(false);
    }

    @Override
    public void fill(DropAction action)
    {
        super.fill(action);

        this.slot.stack = action.itemData == null ? ItemStack.EMPTY : new ItemStack(action.itemData);
        this.inventory.setVisible(false);
    }
}