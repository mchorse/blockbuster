package mchorse.blockbuster.client.gui.elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.client.gui.GuiDirector;
import mchorse.blockbuster.client.gui.widgets.GuiScrollPane;
import mchorse.blockbuster.common.tileentity.director.Replay;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Director block's replays GUI
 *
 * This class is responsible for rendering scroll list with director block's
 * replays and upon selection, notify the parent screen with which replay was
 * chosen by the user.
 */
@SideOnly(Side.CLIENT)
public class GuiReplays extends GuiScrollPane
{
    private final int span = 20;
    private int selected = -1;

    private String stringNoCast = I18n.format("blockbuster.director.no_cast");

    /* Input data */
    private GuiDirector parent;

    /**
     * List of entries
     *
     * This list is being compiled in the setCast method, basically this list is
     * used for rendering of the rows and storing the data for edit and remove
     * actions.
     */
    public List<Entry> entries = new ArrayList<Entry>();

    public GuiReplays(GuiDirector parent)
    {
        this.parent = parent;
    }

    /**
     * Compile entries out of passed actors UUIDs
     */
    public void setCast(List<Replay> replays)
    {
        this.scrollY = 0;
        this.scrollHeight = replays.size() * this.span;
        this.entries.clear();

        for (int i = 0; i < replays.size(); i++)
            this.entries.add(new Entry(i, replays.get(i)));
    }

    /* Handling */

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (!this.isInside(mouseX, mouseY) || mouseX > this.x + this.w - 8)
        {
            return;
        }

        int index = (mouseY - this.y + this.scrollY) / this.span;

        if (!this.entries.isEmpty() && index >= 0 && index < this.entries.size())
        {
            this.parent.setSelected(this.entries.get(index).replay, index);
            this.selected = index;
        }
        else
        {
            this.parent.setSelected(null, -1);
            this.selected = -1;
        }
    }

    /* GUI & drawing */

    @Override
    protected void drawBackground()
    {}

    @Override
    protected void drawPane()
    {
        if (this.entries.size() == 0)
        {
            this.fontRendererObj.drawStringWithShadow(this.stringNoCast, this.x + 2, this.y + 8, 0xffcccccc);
            return;
        }

        for (int i = 0, c = this.entries.size(); i < c; i++)
        {
            int x = this.x + 2;
            int y = this.y + i * this.span;
            boolean flag = i == this.selected;

            Entry entry = this.entries.get(i);
            String name = flag ? "> " + entry.name : entry.name;

            this.fontRendererObj.drawStringWithShadow(name, x, y + 8, flag ? 0xffcccccc : 0xffffffff);
        }
    }

    public void reset()
    {
        this.selected = -1;
    }

    /**
     * Entry class
     *
     * Basically this represents a slot in the scroll list
     */
    static class Entry
    {
        public int index;
        public String name;
        public Replay replay;

        public Entry(int index, Replay replay)
        {
            this.index = index;
            this.replay = replay;

            if (!replay.name.isEmpty())
            {
                this.name = replay.name + (replay.id.isEmpty() ? "" : " (" + replay.id + ")");
            }
            else if (!replay.id.isEmpty())
            {
                this.name = replay.id;
            }
            else
            {
                this.name = "Actor";
            }
        }
    }
}