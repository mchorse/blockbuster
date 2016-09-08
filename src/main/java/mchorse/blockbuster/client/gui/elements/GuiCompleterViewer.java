package mchorse.blockbuster.client.gui.elements;

import java.io.IOException;
import java.util.List;

import mchorse.blockbuster.client.gui.utils.TabCompleter;
import net.minecraft.client.gui.Gui;

/**
 * Gui tab completer viewer
 *
 * This class is responsible for viewing the completions from
 * {@link net.minecraft.util.TabCompleter} and allowing to insert the value from
 * TabCompleter by clicking on one of the elements
 */
public class GuiCompleterViewer extends GuiScrollPane
{
    private final int span = 20;
    private TabCompleter completer;

    public GuiCompleterViewer(TabCompleter completer)
    {
        this.completer = completer;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (!this.isInside(mouseX, mouseY) || mouseX > this.x + this.w - 8)
        {
            return;
        }

        List<String> completions = this.completer.getCompletions();
        int index = (mouseY - this.y + this.scrollY) / this.span;

        if (completions.isEmpty() || index < 0 || index > completions.size() - 1)
        {
            return;
        }

        this.completer.getField().setText(completions.get(index));
        this.completer.resetDidComplete();
        this.setHidden(true);
    }

    @Override
    protected void drawPane()
    {
        List<String> completions = this.completer.getCompletions();

        for (int i = 0, c = completions.size(); i < c; i++)
        {
            String entry = completions.get(i);
            int x = this.x + 6;
            int y = this.y + i * this.span;

            /* Label */
            this.fontRendererObj.drawStringWithShadow(entry, x, y + 4, 0xffffffff);

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
        if (this.completer.getCompletions().isEmpty())
        {
            return;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
