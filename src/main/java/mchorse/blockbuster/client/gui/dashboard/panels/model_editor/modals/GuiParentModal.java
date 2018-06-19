package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.modals;

import java.util.function.Consumer;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.gui.framework.elements.GuiButtonElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiDelegateElement;
import mchorse.blockbuster.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.blockbuster.client.gui.utils.Resizer.Measure;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiParentModal extends GuiModal
{
    public Consumer<String> callback;
    public String label;

    private GuiButtonElement<GuiButton> pick;
    private GuiStringListElement limbs;

    public GuiParentModal(Minecraft mc, GuiDelegateElement parent, Model model, String label, Consumer<String> callback)
    {
        super(mc, parent);

        this.callback = callback;
        this.label = label;

        this.pick = GuiButtonElement.button(mc, "Pick", (b) -> this.send());
        this.limbs = new GuiStringListElement(mc, null);

        this.pick.resizer().set(0, 0, 60, 20).parent(this.area);
        this.pick.resizer().x.set(0.5F, Measure.RELATIVE, -30);
        this.pick.resizer().y.set(0.7F, Measure.RELATIVE, 10);

        this.limbs.resizer().set(0, 0, 80, 0).parent(this.area);
        this.limbs.resizer().x.set(0.5F, Measure.RELATIVE, -40);
        this.limbs.resizer().y.set(0.4F, Measure.RELATIVE);
        this.limbs.resizer().h.set(0.3F, Measure.RELATIVE);
        this.limbs.add("(none)");
        this.limbs.add(model.limbs.keySet());

        this.children.add(this.pick, this.limbs);
    }

    public GuiParentModal setValue(String parent)
    {
        if (parent.isEmpty())
        {
            this.limbs.current = 0;
        }
        else
        {
            this.limbs.setCurrent(parent);
        }

        return this;
    }

    private void send()
    {
        String parent = this.limbs.getCurrent();

        if (this.limbs.current == -1)
        {
            return;
        }

        this.parent.setDelegate(null);

        if (this.callback != null)
        {
            this.callback.accept(this.limbs.current == 0 ? "" : parent);
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        super.draw(mouseX, mouseY, partialTicks);

        this.font.drawSplitString(this.label, this.area.getX(0.2F), this.area.getY(0.1F), (int) (this.area.w * 0.6), 0xffffff);
    }
}