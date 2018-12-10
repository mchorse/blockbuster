package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.modals;

import java.util.Collection;
import java.util.function.Consumer;

import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiModal;
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
        super(mc, parent, label);

        this.callback = callback;

        this.pick = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.me.pick"), (b) -> this.send());
        this.cancel = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.cancel"), (b) -> this.parent.setDelegate(null));
        this.limbs = new GuiStringListElement(mc, null);

        this.pick.resizer().parent(this.area).set(10, 0, 0, 20).y(1, -30).w(0.5F, -15);
        this.cancel.resizer().parent(this.area).set(10, 0, 0, 20).y(1, -30).x(0.5F, 5).w(0.5F, -15);

        this.limbs.resizer().set(10, 0, 0, 0).parent(this.area).y(0.4F, 0).w(1, -20).h(0.6F, -35);
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
}