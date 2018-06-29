package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.client.gui.framework.elements.GuiButtonElement;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiCirculate;
import mchorse.blockbuster.recording.actions.ItemUseAction;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumHand;

public class GuiItemUseActionPanel<T extends ItemUseAction> extends GuiActionPanel<T>
{
    public GuiButtonElement<GuiCirculate> hand;

    public GuiItemUseActionPanel(Minecraft mc)
    {
        super(mc);

        this.title = "Item use action";

        this.hand = new GuiButtonElement<GuiCirculate>(mc, new GuiCirculate(0, 0, 0, 0, 0), (b) -> this.action.hand = EnumHand.values()[b.button.getValue()]);
        this.hand.button.addLabel("Main hand");
        this.hand.button.addLabel("Off hand");
        this.hand.resizer().set(10, 0, 80, 20).parent(this.area).y(1, -30);

        this.children.add(this.hand);
    }

    @Override
    public void fill(T action)
    {
        super.fill(action);

        this.hand.button.setValue(action.hand.ordinal());
    }
}