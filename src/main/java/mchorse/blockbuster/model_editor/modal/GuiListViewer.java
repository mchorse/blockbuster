package mchorse.blockbuster.model_editor.modal;

import java.io.IOException;
import java.util.List;

import mchorse.metamorph.client.gui.utils.GuiScrollPane;
import net.minecraft.client.gui.Gui;

/**
 * Gui tab completer viewer
 *
 * This class is responsible for viewing the completions from
 * {@link net.minecraft.util.TabCompleter} and allowing to insert the value from
 * TabCompleter by clicking on one of the elements
 *
 * @author mchorse
 */
public class GuiListViewer extends GuiScrollPane
{
    private final int span = 20;

    private List<String> strings;
    private IListResponder responder;

    public GuiListViewer(List<String> strings, IListResponder responder)
    {
        this.strings = strings;
        this.responder = responder;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (!this.isInside(mouseX, mouseY) || mouseX > this.x + this.w - 8)
        {
            return;
        }

        List<String> completions = this.strings;
        int index = (mouseY - this.y + this.scrollY) / this.span;

        if (completions.isEmpty() || index < 0 || index > completions.size() - 1)
        {
            return;
        }

        this.responder.pickedValue(completions.get(index));
        this.setHidden(true);
    }

    public void setStrings(List<String> strings)
    {
        this.strings = strings;
        this.scrollHeight = strings.size() * this.span;
    }

    @Override
    protected void drawPane(int mouseX, int mouseY, float partialTicks)
    {
        List<String> completions = this.strings;

        for (int i = 0, c = completions.size(); i < c; i++)
        {
            String entry = completions.get(i);
            int x = this.x + 6;
            int y = this.y + i * this.span;

            int color = 0xffffffff;

            if (mouseX >= this.x && mouseX <= this.x + this.w && mouseY + this.scrollY >= y && mouseY + this.scrollY < y + this.span)
            {
                color = 0xff999999;
            }

            /* Label */
            this.fontRendererObj.drawStringWithShadow(entry, x, y + 6, color);

            /* Separator */
            if (i != c - 1)
            {
                Gui.drawRect(x - 5, y + this.span - 1, this.x + this.w - 1, y + this.span, 0xff181818);
            }
        }
    }

    /**
     * Don't draw the screen in case if there's no completions to draw
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (this.strings.isEmpty())
        {
            return;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public static interface IListResponder
    {
        public void pickedValue(String value);
    }
}
