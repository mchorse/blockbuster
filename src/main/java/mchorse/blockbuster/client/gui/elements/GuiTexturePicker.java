package mchorse.blockbuster.client.gui.elements;

import java.util.Collections;
import java.util.Comparator;
import java.util.function.Consumer;

import mchorse.blockbuster.utils.TextureLocation;
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

        this.text = new GuiTextElement(mc, 1000, (str) ->
        {
            ResourceLocation rl = str.isEmpty() ? null : new TextureLocation(str);

            if (this.callback != null)
            {
                this.callback.accept(rl);
            }

            this.picker.setCurrent(rl);
        });

        this.pick = GuiButtonElement.button(mc, "X", (b) ->
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
        this.text.resizer().set(5, 5, 0, 20).parent(this.area).w(1, -35);
        this.pick.resizer().set(0, 5, 20, 20).parent(this.area).x(1, -25);
        this.picker.resizer().set(5, 30, 0, 0).parent(this.area).w(1, -10).h(1, -35);

        this.children.add(this.text, this.pick, this.picker);
        this.callback = callback;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        /* Necessary measure to avoid triggering buttons when you press 
         * on a text field, for example */
        return super.mouseClicked(mouseX, mouseY, mouseButton) || (this.isVisible() && this.area.isInside(mouseX, mouseY));
    }

    public void set(ResourceLocation skin)
    {
        this.text.field.setText(skin == null ? "" : skin.toString());
        this.text.field.setCursorPositionZero();
        this.picker.setCurrent(skin);
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.getY(1), 0x88000000);

        if (this.picker.getList().isEmpty())
        {
            this.drawCenteredString(this.font, I18n.format("blockbuster.gui.no_data"), this.area.getX(0.5F), this.area.getY(0.5F), 0xffffff);
        }

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