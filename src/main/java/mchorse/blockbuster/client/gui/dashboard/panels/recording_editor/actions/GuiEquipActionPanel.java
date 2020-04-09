package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.recording.actions.EquipAction;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiInventoryElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GuiEquipActionPanel extends GuiActionPanel<EquipAction>
{
    public GuiCirculateElement armor;
    public GuiInventoryElement inventory;
    public GuiSlotElement slot;

    public GuiEquipActionPanel(Minecraft mc)
    {
        super(mc);

        this.armor = new GuiCirculateElement(mc, (b) -> this.action.armorSlot = (byte) (this.armor.getValue() - 1));
        this.armor.addLabel(I18n.format("blockbuster.gui.record_editor.actions.equip.none"));
        this.armor.addLabel(I18n.format("blockbuster.gui.record_editor.actions.equip.main_hand"));
        this.armor.addLabel(I18n.format("blockbuster.gui.record_editor.actions.equip.feet"));
        this.armor.addLabel(I18n.format("blockbuster.gui.record_editor.actions.equip.legs"));
        this.armor.addLabel(I18n.format("blockbuster.gui.record_editor.actions.equip.chest"));
        this.armor.addLabel(I18n.format("blockbuster.gui.record_editor.actions.equip.head"));
        this.armor.addLabel(I18n.format("blockbuster.gui.record_editor.actions.equip.off_hand"));
        this.armor.flex().set(0, 0, 80, 20).relative(this.area).x(0.5F, -40).y(0.5F, -50);

        this.inventory = new GuiInventoryElement(mc, this::pickItem);
        this.slot = new GuiSlotElement(mc,0, this::setSlot);

        this.slot.flex().relative(this.area).xy(0.5F, 0.5F).wh(20, 20).anchor(0.5F, 0.5F);
        this.inventory.flex().under(this.slot.flex(), 10).wh(400, 100);
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
    public void fill(EquipAction action)
    {
        super.fill(action);

        this.armor.setValue(action.armorSlot);
        this.slot.stack = action.itemData == null ? ItemStack.EMPTY : new ItemStack(action.itemData);
        this.inventory.setVisible(false);
    }
}