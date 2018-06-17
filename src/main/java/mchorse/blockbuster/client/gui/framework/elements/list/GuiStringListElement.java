package mchorse.blockbuster.client.gui.framework.elements.list;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class GuiStringListElement extends GuiListElement<String>
{
    public GuiStringListElement(Minecraft mc, Consumer<String> callback)
    {
        super(mc, callback);

        this.scroll.scrollItemSize = 16;
    }

    @Override
    public void drawElement(String element, int i, int x, int y, boolean hover)
    {
        if (this.current == i)
        {
            Gui.drawRect(x, y, x + this.scroll.w, y + this.scroll.scrollItemSize, 0x880088ff);
        }

        this.font.drawStringWithShadow(element, x + 4, y + 4, hover ? 16777120 : 0xffffff);
    }
}