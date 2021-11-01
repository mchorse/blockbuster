package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.tabs;

import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiThreeElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiModal;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import javax.vecmath.Vector3f;
import java.util.function.Consumer;

public class GuiAnchorModal extends GuiModal
{
    public Consumer<Vector3f> callback;

    public GuiThreeElement vector;
    public GuiButtonElement confirm;

    public GuiAnchorModal(Minecraft mc, IKey label, Consumer<Vector3f> callback)
    {
        super(mc, label);

        this.callback = callback;
        this.vector = new GuiThreeElement(mc, null);
        this.vector.setLimit(0, 1, false);
        this.vector.a.increment(0.1).values(0.05, 0.01, 0.1);
        this.vector.b.increment(0.1).values(0.05, 0.01, 0.1);
        this.vector.c.increment(0.1).values(0.05, 0.01, 0.1);
        this.vector.flex().relative(this).set(10, 0, 0, 20).y(1, -55).w(1, -20);

        this.confirm = new GuiButtonElement(mc, IKey.lang("mclib.gui.ok"), (b) -> this.send());

        this.bar.add(this.confirm);
        this.add(this.vector);
    }

    public void send()
    {
        this.removeFromParent();

        if (this.callback != null)
        {
            this.callback.accept(new Vector3f((float) this.vector.a.value, (float) this.vector.b.value, (float) this.vector.c.value));
        }
    }

    @Override
    public boolean keyTyped(GuiContext context)
    {
        if (super.keyTyped(context))
        {
            return true;
        }

        if (context.keyCode == Keyboard.KEY_RETURN)
        {
            this.confirm.clickItself(context);

            return true;
        }

        return false;
    }
}