package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.modals;

import java.util.Collection;
import java.util.function.Consumer;

import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class GuiListModal extends GuiModal
{
    public Consumer<String> callback;
    public String label;

    private GuiButtonElement<GuiButton> pick;
    private GuiButtonElement<GuiButton> cancel;
    private GuiStringListElement limbs;

    public GuiListModal(Minecraft mc, GuiDelegateElement<IGuiElement> parent, String label, Consumer<String> callback)
    {
        super(mc, parent);

        this.callback = callback;
        this.label = label;

        this.pick = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.me.pick"), (b) -> this.send());
        this.cancel = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.cancel"), (b) -> this.parent.setDelegate(null));
        this.limbs = new GuiStringListElement(mc, null);

        this.pick.resizer().set(0, 0, 50, 20).parent(this.area).x(0.5F, -55).y(0.7F, 10);
        this.cancel.resizer().set(60, 0, 50, 20).relative(this.pick.resizer());

        this.limbs.resizer().set(0, 0, 100, 0).parent(this.area).x(0.5F, -50).y(0.4F, 0).h(0.3F, 0);
        this.limbs.add(I18n.format("blockbuster.gui.me.none"));
        this.limbs.current = 0;

        this.children.add(this.pick, this.cancel, this.limbs);
    }

    public GuiListModal setValue(String parent)
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

    public GuiListModal addValues(Collection<String> values)
    {
        this.limbs.add(values);

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
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(tooltip, mouseX, mouseY, partialTicks);

        this.font.drawSplitString(this.label, this.area.getX(0.15F), this.area.getY(0.1F), (int) (this.area.w * 0.7), 0xffffff);
    }
}