package mchorse.blockbuster.client.gui.framework.elements.list;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import mchorse.blockbuster.client.gui.framework.GuiTooltip;
import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiTextElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public abstract class GuiSearchListElement<T> extends GuiElement
{
    public List<T> elements = new ArrayList<T>();
    public GuiTextElement search;
    public GuiListElement<T> list;
    public String label;
    public boolean background;

    public GuiSearchListElement(Minecraft mc, Consumer<T> callback)
    {
        super(mc);

        this.search = new GuiTextElement(mc, 100, (str) -> this.filter(str, false));
        this.search.resizer().parent(this.area).set(0, 0, 0, 20).w(1, 0);

        this.list = this.createList(mc, callback);
        this.list.resizer().parent(this.area).set(0, 20, 0, 0).w(1, 0).h(1, -20);

        this.createChildren().children.add(this.search, this.list);
    }

    protected abstract GuiListElement<T> createList(Minecraft mc, Consumer<T> callback);

    public void filter(String str, boolean fill)
    {
        if (fill) this.search.setText(str);

        this.list.clear();

        if (str == null || str.isEmpty())
        {
            this.list.add(this.elements);
        }
        else
        {
            for (T element : this.elements)
            {
                if (element.toString().toLowerCase().startsWith(str.toLowerCase()))
                {
                    this.list.add(element);
                }
            }
        }

        this.list.sort();
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        return super.mouseClicked(mouseX, mouseY, mouseButton) || this.isVisible() && this.area.isInside(mouseX, mouseY);
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        if (this.background)
        {
            Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.getY(1), 0x88000000);
        }

        super.draw(tooltip, mouseX, mouseY, partialTicks);

        if (!this.search.field.isFocused() && this.search.field.getText().isEmpty())
        {
            this.font.drawStringWithShadow(this.label, this.search.area.x + 4, this.search.area.y + 6, 0x888888);
        }
    }
}