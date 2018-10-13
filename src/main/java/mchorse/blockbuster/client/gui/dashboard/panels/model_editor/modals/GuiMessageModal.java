package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.modals;

import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class GuiMessageModal extends GuiModal
{
    public String label;

    private GuiButtonElement<GuiButton> button;

    public GuiMessageModal(Minecraft mc, GuiDelegateElement<IGuiElement> parent, String label)
    {
        super(mc, parent);

        this.parent = parent;
        this.label = label;

        this.button = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.ok"), (b) -> parent.setDelegate(null));
        this.button.resizer().parent(this.area).set(0, 0, 60, 20).x(0.5F, -30).y(0.5F, 10);

        this.children.add(this.button);
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(tooltip, mouseX, mouseY, partialTicks);

        this.font.drawSplitString(this.label, this.area.getX(0.2F), this.area.getY(0.25F), (int) (this.area.w * 0.6), 0xffffff);
    }
}