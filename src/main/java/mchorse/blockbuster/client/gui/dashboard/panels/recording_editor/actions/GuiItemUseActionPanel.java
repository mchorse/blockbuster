package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.recording.actions.ItemUseAction;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumHand;

public class GuiItemUseActionPanel<T extends ItemUseAction> extends GuiActionPanel<T>
{
    public GuiCirculateElement hand;

    public GuiItemUseActionPanel(Minecraft mc)
    {
        super(mc);

        this.hand = new GuiCirculateElement(mc, (b) -> this.action.hand = EnumHand.values()[this.hand.getValue()]);
        this.hand.addLabel(IKey.lang("blockbuster.gui.record_editor.actions.equip.main_hand"));
        this.hand.addLabel(IKey.lang("blockbuster.gui.record_editor.actions.equip.off_hand"));
        this.hand.flex().set(10, 0, 80, 20).relative(this.area).y(1, -30);

        this.add(this.hand);
    }

    @Override
    public void fill(T action)
    {
        super.fill(action);

        this.hand.setValue(action.hand.ordinal());
    }
}