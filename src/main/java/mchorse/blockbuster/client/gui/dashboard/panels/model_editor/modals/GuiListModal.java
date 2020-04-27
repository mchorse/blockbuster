package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.modals;

import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiModal;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import java.util.Collection;
import java.util.function.Consumer;

public class GuiListModal extends GuiModal
{
    public Consumer<String> callback;
    public String label;

    private GuiButtonElement pick;
    private GuiButtonElement cancel;
    private GuiStringListElement limbs;

    public GuiListModal(Minecraft mc, IKey label, Consumer<String> callback)
    {
        super(mc, label);

        this.callback = callback;

        this.pick = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.me.pick"), (b) -> this.send());
        this.cancel = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.cancel"), (b) -> this.removeFromParent());
        this.limbs = new GuiStringListElement(mc, null);

        this.pick.flex().relative(this.area).set(10, 0, 0, 20).y(1, -30).w(0.5F, -15);
        this.cancel.flex().relative(this.area).set(10, 0, 0, 20).y(1, -30).x(0.5F, 5).w(0.5F, -15);

        this.limbs.flex().set(10, 0, 0, 0).relative(this.area).y(0.4F, 0).w(1, -20).h(0.6F, -35);
        this.limbs.add(I18n.format("blockbuster.gui.me.none"));
        this.limbs.setIndex(0);

        this.add(this.pick, this.cancel, this.limbs);
    }

    public GuiListModal setValue(String parent)
    {
        if (parent.isEmpty())
        {
            this.limbs.setIndex(0);
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
        if (this.limbs.isDeselected())
        {
            return;
        }

        this.parent.removeFromParent();

        if (this.callback != null)
        {
            this.callback.accept(this.limbs.getIndex() == 0 ? "" : this.limbs.getCurrentFirst());
        }
    }
}