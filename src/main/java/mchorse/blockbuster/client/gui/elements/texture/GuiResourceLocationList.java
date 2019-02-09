package mchorse.blockbuster.client.gui.elements.texture;

import java.util.Collections;
import java.util.Comparator;
import java.util.function.Consumer;

import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

/**
 * Similar to {@link GuiStringListElement}, but uses {@link ResourceLocation}s 
 */
public class GuiResourceLocationList extends GuiListElement<ResourceLocation>
{
    public GuiResourceLocationList(Minecraft mc, Consumer<ResourceLocation> callback)
    {
        super(mc, callback);

        this.scroll.scrollItemSize = 16;
    }

    @Override
    public void sort()
    {
        Collections.sort(this.list, new Comparator<ResourceLocation>()
        {
            @Override
            public int compare(ResourceLocation o1, ResourceLocation o2)
            {
                return o1.toString().compareToIgnoreCase(o2.toString());
            }
        });
    }

    @Override
    public void drawElement(ResourceLocation element, int i, int x, int y, boolean hover)
    {
        if (this.current == i)
        {
            Gui.drawRect(x, y, x + this.scroll.w, y + this.scroll.scrollItemSize, 0x880088ff);
        }

        this.font.drawStringWithShadow(element.toString(), x + 4, y + 4, hover ? 16777120 : 0xffffff);
    }
}