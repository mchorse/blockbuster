package noname.blockbuster.client.gui.elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noname.blockbuster.client.gui.GuiActor;
import noname.blockbuster.client.gui.GuiCamera;
import noname.blockbuster.client.gui.GuiRecordingOverlay;
import noname.blockbuster.entity.EntityActor;
import noname.blockbuster.entity.EntityCamera;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.director.PacketDirectorRemove;
import noname.blockbuster.recording.Mocap;

/**
 * Director block's cast GUI
 *
 * This class is responsible for rendering scroll list with director block's
 * cast and execute different actions (edit, remove) when player hits the
 * button.
 */
public class GuiCast extends GuiScrollPane
{
    /**
     * Comparator, sorts by alphabet in ascending order
     */
    public static final Comparator<Entry> ALPHA = new Comparator<Entry>()
    {
        @Override
        public int compare(Entry a, Entry b)
        {
            return a.name.compareTo(b.name);
        }
    };

    private String noCast = I18n.format("blockbuster.director.no_cast");

    /* Input data */
    private BlockPos pos;
    private GuiParentScreen parent;

    /**
     * List of entries
     *
     * This list is being compiled in the setCast method, basically this list
     * is used for rendering of the rows and storing the data for edit and
     * remove actions.
     */
    public List<Entry> entries = new ArrayList<Entry>();

    public GuiCast(GuiParentScreen parent, BlockPos pos)
    {
        this.parent = parent;
        this.pos = pos;
    }

    /**
     * Compile entries out of passed actors and cameras UUIDs
     */
    public void setCast(List<String> actors, List<String> cameras)
    {
        World world = Minecraft.getMinecraft().theWorld;

        this.scrollY = 0;
        this.scrollHeight = (actors.size() + cameras.size()) * 24;
        this.entries.clear();

        for (int i = 0; i < actors.size(); i++)
            this.addEntry(i, Mocap.entityByUUID(world, actors.get(i)), true);

        for (int i = 0; i < cameras.size(); i++)
            this.addEntry(i, Mocap.entityByUUID(world, cameras.get(i)), false);

        this.entries.sort(ALPHA);

        this.buttonList.clear();
        this.initGui();
    }

    /**
     * Add entry to the entry list
     */
    private void addEntry(int index, Entity entity, boolean isActor)
    {
        int id = entity != null ? entity.getEntityId() : -1;
        String name = entity != null ? entity.getName() : (isActor ? "Actor" : "Camera");

        this.entries.add(new Entry(id, index, name, isActor, entity == null));
    }

    /* Handling */

    /**
     * Performs an action.
     *
     * Remove action is triggered when this method is invoked with button which
     * has id of 0, however edit action is triggered when this method invoked
     * with button of id 1.
     *
     * Edit action only can be performed when the entity is in reach (i.e. loaded
     * by the client), else, pal, you're on your own.
     */
    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        GuiCustomButton<Entry> buttonIn = (GuiCustomButton<Entry>) button;
        Entry entry = buttonIn.getValue();

        if (buttonIn.id == 0)
        {
            Dispatcher.getInstance().sendToServer(new PacketDirectorRemove(this.pos, entry.index, entry.isActor));
        }
        else if (buttonIn.id == 1 && !entry.outOfReach)
        {
            Entity entity = this.mc.theWorld.getEntityByID(entry.id);

            if (entry.isActor)
            {
                this.mc.displayGuiScreen(new GuiActor(this.parent, (EntityActor) entity));
            }
            else
            {
                this.mc.displayGuiScreen(new GuiCamera(this.parent, (EntityCamera) entity));
            }
        }
    }

    /* GUI & drawing */

    /**
     * Initiate GUI, this gets invoked after the setCast method get invoke
     */
    @Override
    public void initGui()
    {
        for (int i = 0; i < this.entries.size(); i++)
        {
            int x = this.x + this.w - 64;
            int y = this.y + i * 24 + 3;

            GuiCustomButton<Entry> remove = new GuiCustomButton<Entry>(0, x, y, 54, 20, I18n.format("blockbuster.gui.remove"));
            GuiCustomButton<Entry> edit = new GuiCustomButton<Entry>(1, x - 60, y, 54, 20, I18n.format("blockbuster.gui.edit"));

            remove.setValue(this.entries.get(i));
            edit.setValue(this.entries.get(i));

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
            int x = this.x + 24;
            int y = this.y + (i) * 24;

            this.mc.renderEngine.bindTexture(GuiRecordingOverlay.TEXTURE);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();

            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();

            /* Icon of the entry */
            this.drawTexturedModalRect(x - 20, y + 4, entry.isActor ? 16 : 32, 0, 16, 16);

            /* X over the actor/camera icon */
            if (entry.outOfReach)
            {
                this.drawTexturedModalRect(x - 20, y + 4, 48, 0, 16, 16);
            }

            this.fontRendererObj.drawStringWithShadow(entry.name, x, y + 8, 0xffffffff);

            /* Border under the row */
            if (i != c - 1)
            {
                this.drawRect(x - 5, y + 24, this.x + this.w - 1, y + 25, 0xff181818);
            }
        }
    }

    /**
     * Entry class
     *
     * Basically this represents a slot in the scroll pane
     */
    static class Entry
    {
        public int id;
        public int index;
        public String name;
        public boolean isActor;
        public boolean outOfReach;

        public Entry(int id, int index, String name, boolean isActor, boolean outOfReach)
        {
            this.id = id;
            this.index = index;
            this.name = name;
            this.isActor = isActor;
            this.outOfReach = outOfReach;
        }
    }
}
