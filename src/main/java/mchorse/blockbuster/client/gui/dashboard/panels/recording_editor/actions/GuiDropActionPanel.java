package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.recording.actions.DropAction;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.widgets.GuiInventory;
import mchorse.mclib.client.gui.widgets.GuiInventory.IInventoryPicker;
import mchorse.mclib.client.gui.widgets.GuiSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GuiDropActionPanel extends GuiActionPanel<DropAction> implements IInventoryPicker
{
    public GuiInventory inventory;
    public GuiSlot slot;

    public GuiDropActionPanel(Minecraft mc)
    {
        super(mc);

        this.inventory = new GuiInventory(this, mc.thePlayer);
        this.slot = new GuiSlot(0);
    }

    @Override
    public void pickItem(GuiInventory inventory, ItemStack stack)
    {
        this.action.itemData = stack == null ? null : stack.writeToNBT(new NBTTagCompound());
        this.slot.stack = stack;

        inventory.visible = false;
    }

    @Override
    public void fill(DropAction action)
    {
        super.fill(action);

        this.slot.stack = action.itemData == null ? null : ItemStack.loadItemStackFromNBT(action.itemData);
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);

        this.slot.update(this.area.getX(0.5F) - 10, this.area.getY(0.5F) - 10);
        this.inventory.update(this.area.getX(0.5F), this.area.getY(0.75F));
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (super.mouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        this.inventory.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.slot.area.isInside(mouseX, mouseY))
        {
            this.inventory.visible = true;
        }

        return false;
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(tooltip, mouseX, mouseY, partialTicks);

        this.slot.draw(mouseX, mouseY, partialTicks);
        this.inventory.draw(mouseX, mouseY, partialTicks);
    }
}