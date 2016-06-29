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
import noname.blockbuster.Blockbuster;
import noname.blockbuster.client.gui.GuiRecordingOverlay;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.director.PacketDirectorRemove;
import noname.blockbuster.recording.Mocap;

public class GuiCast extends GuiScrollPane
{
    public BlockPos pos;
    public List<GuiCast.Entry> entries = new ArrayList<GuiCast.Entry>();

    public GuiCast(BlockPos pos, int x, int y, int w, int h)
    {
        super(x, y, w, h);
        this.pos = pos;
    }

    public void setCast(List<String> actors, List<String> cameras)
    {
        World world = Minecraft.getMinecraft().theWorld;

        this.scrollY = 0;
        this.scrollHeight = (actors.size() + cameras.size()) * 24;

        for (int i = 0; i < actors.size(); i++)
        {
            Entity entity = Mocap.entityByUUID(world, actors.get(i));

            this.entries.add(new GuiCast.Entry(entity.getEntityId(), i, entity.getName(), true));
        }

        for (int i = 0; i < cameras.size(); i++)
        {
            Entity entity = Mocap.entityByUUID(world, cameras.get(i));

            this.entries.add(new GuiCast.Entry(entity.getEntityId(), i, entity.getName(), false));
        }

        this.entries.sort(new Comparator<Entry>()
        {
            @Override
            public int compare(Entry a, Entry b)
            {
                return a.name.compareTo(b.name);
            }
        });
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        GuiCustomButton<Entry> buttonIn = (GuiCustomButton<GuiCast.Entry>) button;
        Entry entry = buttonIn.getValue();

        if (buttonIn.id == 0)
        {
            Dispatcher.getInstance().sendToServer(new PacketDirectorRemove(this.pos, entry.index, entry.isActor));
        }
        else if (buttonIn.id == 1)
        {
            this.mc.thePlayer.openGui(Blockbuster.instance, entry.isActor ? 1 : 0, this.mc.theWorld, entry.id, 0, 0);
        }
    }

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
        for (int i = 0, c = this.entries.size(); i < c; i++)
        {
            Entry entry = this.entries.get(i);
            int x = this.x + 24;
            int y = this.y + (i) * 24;

            this.mc.renderEngine.bindTexture(GuiRecordingOverlay.TEXTURE);

            GlStateManager.pushAttrib();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();

            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();

            this.drawTexturedModalRect(x - 20, y + 4, entry.isActor ? 16 : 32, 0, 16, 16);
            this.fontRendererObj.drawStringWithShadow(entry.name, x, y + 8, 0xffffffff);

            GlStateManager.popAttrib();

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

        public Entry(int id, int index, String name, boolean isActor)
        {
            this.id = id;
            this.index = index;
            this.name = name;
            this.isActor = isActor;
        }
    }
}
