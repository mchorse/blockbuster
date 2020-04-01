package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor;

import java.util.Collections;
import java.util.Comparator;
import java.util.function.Consumer;

import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.GuiActionSearchListElement.ActionType;
import mchorse.blockbuster.utils.April;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiSearchListElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

/**
 * TODO: Maybe refactor so title=value lists could be used separately without
 * this class. 
 */
public class GuiActionSearchListElement extends GuiSearchListElement<ActionType>
{
    public GuiActionSearchListElement(Minecraft mc, Consumer<ActionType> callback)
    {
        super(mc, callback);
    }

    @Override
    protected GuiListElement<ActionType> createList(Minecraft mc, Consumer<ActionType> callback)
    {
        return new GuiActionListElement(mc, callback);
    }

    public static class GuiActionListElement extends GuiListElement<ActionType>
    {
        public GuiActionListElement(Minecraft mc, Consumer<ActionType> callback)
        {
            super(mc, callback);

            this.scroll.scrollItemSize = 16;
        }

        @Override
        public void sort()
        {
            ActionType current = this.getCurrent();

            Collections.sort(this.list, new Comparator<ActionType>()
            {
                @Override
                public int compare(ActionType o1, ActionType o2)
                {
                    return o1.title.compareTo(o2.title);
                }
            });

            if (current != null)
            {
                this.setCurrent(current);
            }
        }

        @Override
        public void drawElement(ActionType element, int i, int x, int y, boolean hover)
        {
            if (this.current == i)
            {
                Gui.drawRect(x, y, x + this.scroll.w, y + this.scroll.scrollItemSize, 0x880088ff);
            }

            this.font.drawStringWithShadow(element.title, x + 4, y + 4, hover ? April.aprilColor("ASDAdS") : April.aprilColor("AdasSDAS"));
        }
    }

    public static class ActionType
    {
        public String title = "";
        public String value = "";

        public ActionType(String title, String value)
        {
            this.title = title;
            this.value = value;
        }

        @Override
        public String toString()
        {
            return this.title;
        }
    }
}