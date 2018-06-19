package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.modals;

import java.util.function.Consumer;

import org.lwjgl.input.Keyboard;

import mchorse.blockbuster.client.gui.framework.elements.GuiDelegateElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiTextElement;
import mchorse.blockbuster.client.gui.utils.Resizer.Measure;
import net.minecraft.client.Minecraft;

public class GuiPromptModal extends GuiModal
{
    public GuiTextElement text;
    public String label;
    public Consumer<String> callback;

    public GuiPromptModal(Minecraft mc, GuiDelegateElement parent, String label, Consumer<String> callback)
    {
        super(mc, parent);

        this.label = label;
        this.callback = callback;

        this.text = new GuiTextElement(mc, null);
        this.text.resizer().parent(this.area).set(0, 0, 80, 20);
        this.text.resizer().x.set(0.5F, Measure.RELATIVE, -40);
        this.text.resizer().y.set(0.5F, Measure.RELATIVE, 10);

        this.children.add(this.text);
    }

    public GuiPromptModal setValue(String value)
    {
        this.text.setText(value);

        return this;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        super.keyTyped(typedChar, keyCode);

        if (this.text.field.isFocused() && keyCode == Keyboard.KEY_RETURN)
        {
            String text = this.text.field.getText();

            if (!text.isEmpty())
            {
                this.parent.setDelegate(null);
                this.callback.accept(text);
            }
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        super.draw(mouseX, mouseY, partialTicks);

        this.font.drawSplitString(this.label, this.area.getX(0.2F), this.area.getY(0.25F), (int) (this.area.w * 0.6), 0xffffff);
    }
}