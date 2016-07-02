package noname.blockbuster.client.gui.elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import noname.blockbuster.client.gui.GuiActor;
import noname.blockbuster.entity.EntityActor;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.director.PacketDirectorMapRemove;
import noname.blockbuster.recording.Mocap;

/**
 * Director map block cast view
 *
 * This view is responsible for viewing director map block's cast, and providing
 * actions to edit or remove cast members.
 */
public class GuiReplays extends GuiScrollPane
{
    /* Strings */
    private String noCast = I18n.format("blockbuster.director_map.no_cast");

    /* GUI */
    private GuiParentScreen parent;

    /* Input data */
    private BlockPos pos;

    /* Entries */
    private List<Entry> entries = new ArrayList<Entry>();

    public GuiReplays(GuiParentScreen parent, BlockPos pos)
    {
        this.parent = parent;
        this.pos = pos;
    }

    public void setCast(List<String> cast)
    {
        System.out.println(cast);

        this.setHeight(cast.size() * 24);
        this.entries.clear();

        for (int i = 0; i < cast.size(); i++)
            this.entries.add(new Entry(i, cast.get(i)));

        this.buttonList.clear();
        this.initGui();
    }

    /* Action handling */

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        Entry entry = ((GuiCustomButton<Entry>) button).getValue();

        if (button.id == 0)
        {
            Dispatcher.getInstance().sendToServer(new PacketDirectorMapRemove(this.pos, entry.index));
        }
        else if (button.id == 1)
        {
            EntityActor actor = Mocap.actorFromArgs(entry.replay.split(":"), this.mc.theWorld);

            this.mc.displayGuiScreen(new GuiActor(this.parent, actor, this.pos, entry.index));
        }
    }

    /* GUI and drawing */

    @Override
    public void initGui()
    {
        for (int i = 0; i < this.entries.size(); i++)
        {
            Entry entry = this.entries.get(i);
            int x = this.x + this.w - 64;
            int y = this.y + i * 24 + 3;

            GuiCustomButton<Entry> remove = new GuiCustomButton<Entry>(0, x, y, 54, 20, I18n.format("blockbuster.gui.remove"));
            GuiCustomButton<Entry> edit = new GuiCustomButton<Entry>(1, x - 60, y, 54, 20, I18n.format("blockbuster.gui.edit"));

            remove.setValue(entry);
            edit.setValue(entry);

            this.buttonList.add(remove);
            this.buttonList.add(edit);
        }
    }

    @Override
    protected void drawPane()
    {
        if (this.entries.size() == 0)
        {
            this.drawCenteredString(this.fontRendererObj, this.noCast, this.width / 2, this.y + 8, 0xffffff);
            return;
        }

        for (int i = 0, c = this.entries.size(); i < c; i++)
        {
            Entry entry = this.entries.get(i);
            int x = this.x + 6;
            int y = this.y + i * 24;

            /* Label */
            this.fontRendererObj.drawStringWithShadow(entry.caption, x, y + 8, 0xffffffff);

            /* Separator */
            if (i != c - 1)
            {
                this.drawRect(x - 5, y + 24, this.x + this.w - 1, y + 25, 0xff181818);
            }
        }
    }

    static class Entry
    {
        public int index;
        public String replay;
        public String caption;

        public Entry(int index, String replay)
        {
            this.index = index;
            this.replay = replay;
            this.caption = replay.split(":")[0];
        }
    }
}
