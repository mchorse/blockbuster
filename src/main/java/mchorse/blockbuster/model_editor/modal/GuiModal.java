package mchorse.blockbuster.model_editor.modal;

import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.model_editor.IModalCallback;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

/**
 * Abstract modal
 */
public abstract class GuiModal
{
    /**
     * Label which should be displayed
     */
    public String label;

    public GuiScreen parent;
    public FontRenderer font;

    public List<GuiButton> buttons = new ArrayList<GuiButton>();

    public int buttonWidth = 60;

    public GuiModal(GuiScreen parent, FontRenderer font)
    {
        this.parent = parent;
        this.font = font;
    }

    public abstract void initButtons();

    /**
     * Perform an action based on button's input
     */
    protected void actionPerformed(GuiButton button)
    {
        if (this.parent instanceof IModalCallback)
        {
            ((IModalCallback) this.parent).modalButtonPressed(this, button);
        }
    }

    /**
     * Stole from {@link GuiScreen}. Such thug!
     */
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0)
        {
            for (int i = 0; i < this.buttons.size(); ++i)
            {
                GuiButton button = this.buttons.get(i);

                if (button.mousePressed(this.parent.mc, mouseX, mouseY))
                {
                    button.playPressSound(this.parent.mc.getSoundHandler());
                    this.actionPerformed(button);
                }
            }
        }
    }

    /**
     * If keyboard input is needed
     */
    public void keyTyped(char typedChar, int keyCode)
    {}

    /**
     * Draw the modal
     */
    public void drawModal(int mouseX, int mouseY, float partialTicks)
    {
        int x = this.parent.width / 2;
        int y = this.parent.height / 2;
        int width = 200;
        int height = 90;

        Gui.drawRect(x - width / 2, y - height / 2, x + width / 2, y + height / 2, 0xffcccccc);
        Gui.drawRect(x - width / 2 + 1, y - height / 2 + 1, x + width / 2 - 1, y + height / 2 - 1, 0xff000000);

        float offset = width * 0.5F - 10;

        this.font.drawSplitString(this.label, x - (int) offset, y - height / 2 + 10, (int) (offset * 2), 0xffffff);

        for (GuiButton button : this.buttons)
        {
            button.drawButton(this.parent.mc, mouseX, mouseY);
        }
    }
}