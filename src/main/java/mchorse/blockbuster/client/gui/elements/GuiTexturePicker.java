package mchorse.blockbuster.client.gui.elements;

import java.util.Collections;
import java.util.Comparator;
import java.util.function.Consumer;

import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiTexturePicker extends GuiElement
{
    public GuiTextElement text;
    public GuiButtonElement<GuiButton> pick;
    public GuiResourceLocationList picker;
    public Consumer<ResourceLocation> callback;

    public GuiTexturePicker(Minecraft mc, Consumer<ResourceLocation> callback)
    {
        super(mc);

        this.text = new GuiTextElement(mc, (str) ->
        {
            ResourceLocation rl = str.isEmpty() ? null : new ResourceLocation(str);

            if (this.callback != null)
            {
                this.callback.accept(rl);
            }

            this.picker.setCurrent(rl);
        });

        this.pick = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.picked"), (b) ->
        {
            this.setVisible(false);
        });

        this.picker = new GuiResourceLocationList(mc, (rl) ->
        {
            if (this.callback != null)
            {
                this.callback.accept(rl);
            }
            this.text.setText(rl == null ? "" : rl.toString());
        });

        this.createChildren();
        this.text.resizer().set(5, 5, 0, 20).parent(this.area).w(1, -75);
        this.pick.resizer().set(0, 5, 60, 20).parent(this.area).x(1, -65);
        this.picker.resizer().set(5, 30, 0, 0).parent(this.area).w(1, -10).h(1, -35);

        this.children.add(this.text, this.pick, this.picker);
        this.callback = callback;
    }

    public void set(ResourceLocation skin)
    {
        this.text.field.setText(skin == null ? "" : skin.toString());
        this.picker.setCurrent(skin);
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.getY(1), 0x88000000);

        super.draw(tooltip, mouseX, mouseY, partialTicks);
    }

    public static class GuiResourceLocationList extends GuiListElement<ResourceLocation>
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
}