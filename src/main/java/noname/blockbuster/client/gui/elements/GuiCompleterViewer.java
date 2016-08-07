package noname.blockbuster.client.gui.elements;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.gui.Gui;
import noname.blockbuster.client.gui.utils.TabCompleter;

/**
 * Gui tab completer viewer
 */
public class GuiCompleterViewer extends GuiScrollPane
{
    private TabCompleter completer;

    public GuiCompleterViewer(TabCompleter completer)
    {
        this.completer = completer;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (!this.isInside(mouseX, mouseY))
        {
            return;
        }

        List<String> completions = this.completer.getCompletions();
        int index = (mouseY - this.y + this.scrollY) / 20;

        if (completions.isEmpty() || index < 0 || index > completions.size() - 1)
        {
            return;
        }

        this.completer.getField().setText(completions.get(index));
        this.completer.resetDidComplete();
    }

    @Override
    protected void drawPane()
    {
        List<String> completions = this.completer.getCompletions();
        this.scrollHeight = completions.size() * 20;

        for (int i = 0, c = completions.size(); i < c; i++)
        {
            String entry = completions.get(i);
            int x = this.x + 6;
            int y = this.y + i * 20;

            /* Label */
            this.fontRendererObj.drawStringWithShadow(entry, x, y + 8, 0xffffffff);

            /* Separator */
            if (i != c - 1)
            {
                Gui.drawRect(x - 5, y + 19, this.x + this.w - 1, y + 20, 0xff181818);
            }
        }
    }

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
