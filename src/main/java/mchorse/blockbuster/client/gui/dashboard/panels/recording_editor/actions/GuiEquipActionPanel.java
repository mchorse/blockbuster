package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.recording.actions.EquipAction;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.widgets.GuiInventory;
import mchorse.mclib.client.gui.widgets.GuiInventory.IInventoryPicker;
import mchorse.mclib.client.gui.widgets.GuiSlot;
import mchorse.mclib.client.gui.widgets.buttons.GuiCirculate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GuiEquipActionPanel extends GuiActionPanel<EquipAction> implements IInventoryPicker
{
    public GuiButtonElement<GuiCirculate> armor;
    public GuiInventory inventory;
    public GuiSlot slot;

    public GuiEquipActionPanel(Minecraft mc)
    {
        super(mc);

        this.inventory = new GuiInventory(this, mc.player);
        this.slot = new GuiSlot(0);
        this.armor = new GuiButtonElement<GuiCirculate>(mc, new GuiCirculate(0, 0, 0, 0, 0), (b) -> this.action.armorSlot = (byte) (b.button.getValue() - 1));
        this.armor.button.addLabel(I18n.format("blockbuster.gui.record_editor.actions.equip.none"));
        this.armor.button.addLabel(I18n.format("blockbuster.gui.record_editor.actions.equip.main_hand"));
        this.armor.button.addLabel(I18n.format("blockbuster.gui.record_editor.actions.equip.feet"));
        this.armor.button.addLabel(I18n.format("blockbuster.gui.record_editor.actions.equip.legs"));
        this.armor.button.addLabel(I18n.format("blockbuster.gui.record_editor.actions.equip.chest"));
        this.armor.button.addLabel(I18n.format("blockbuster.gui.record_editor.actions.equip.head"));
        this.armor.button.addLabel(I18n.format("blockbuster.gui.record_editor.actions.equip.off_hand"));
        this.armor.resizer().set(0, 0, 80, 20).parent(this.area).x(0.5F, -40).y(0.5F, -50);

        this.children.add(this.armor);
    }

    @Override
    public void pickItem(GuiInventory inventory, ItemStack stack)
    {
        this.action.itemData = stack.isEmpty() ? null : stack.writeToNBT(new NBTTagCompound());
        this.slot.stack = stack;

        inventory.visible = false;
    }

    @Override
    public void fill(EquipAction action)
    {
        super.fill(action);

        this.slot.stack = action.itemData == null ? ItemStack.EMPTY : new ItemStack(action.itemData);
        this.armor.button.setValue(action.armorSlot + 1);
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