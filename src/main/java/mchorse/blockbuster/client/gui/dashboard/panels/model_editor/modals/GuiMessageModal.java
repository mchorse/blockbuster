package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.modals;

import mchorse.blockbuster.client.gui.framework.elements.GuiButtonElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiDelegateElement;
import mchorse.blockbuster.client.gui.utils.Resizer.Measure;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiMessageModal extends GuiModal
{
    public String label;

    private GuiButtonElement<GuiButton> button;

    public GuiMessageModal(Minecraft mc, GuiDelegateElement parent, String label)
    {
        super(mc, parent);

        this.parent = parent;
        this.label = label;

        this.button = GuiButtonElement.button(mc, "Ok", (b) -> parent.setDelegate(null));
        this.button.resizer().parent(this.area).set(0, 0, 60, 20);
        this.button.resizer().x.set(0.5F, Measure.RELATIVE, -30);
        this.button.resizer().y.set(0.5F, Measure.RELATIVE, 10);

        this.children.add(this.button);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        super.draw(mouseX, mouseY, partialTicks);

        this.font.drawSplitString(this.label, this.area.getX(0.2F), this.area.getY(0.25F), (int) (this.area.w * 0.6), 0xffffff);
    }
}