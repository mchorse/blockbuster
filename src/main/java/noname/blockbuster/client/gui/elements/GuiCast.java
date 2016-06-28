package noname.blockbuster.client.gui.elements;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import noname.blockbuster.client.gui.GuiActor;
import noname.blockbuster.entity.EntityActor;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.director.PacketDirectorMapRemove;

/**
 * Director map block cast view
 *
 * This view is responsible for viewing director map block's cast, and providing
 * actions to edit or remove cast members.
 */
public class GuiCast extends GuiScrollPane
{
    public List<String> cast;
    public BlockPos pos;

    public GuiCast(int x, int y, int w, int h, BlockPos pos)
    {
        super(x, y, w, h);
        this.pos = pos;
    }

    public void setCast(List<String> cast)
    {
        this.cast = cast;

        this.scrollHeight = cast.size() * 24;
        this.scrollY = 0;
    }

    @Override
    public void initGui()
    {
        for (int i = 0; i < this.cast.size(); i++)
        {
            int x = this.x + this.w - 64;
            int y = this.y + i * 24 + 3;

            this.buttonList.add(new GuiButton(i * 2, x, y, 54, 18, I18n.format("blockbuster.gui.remove")));
            this.buttonList.add(new GuiButton(i * 2 + 1, x - 60, y, 54, 18, I18n.format("blockbuster.gui.edit")));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        int index = Math.floorDiv(button.id, 2);
        String member = this.cast.get(index);

        if (member != null)
        {
            if (button.id % 2 == 0)
            {
                Dispatcher.getInstance().sendToServer(new PacketDirectorMapRemove(this.pos, member));
            }
            else
            {
                String[] args = member.split(":");

                String name = args.length >= 2 ? args[1] : "";
                String skin = args.length >= 3 ? args[2] : "";
                boolean isInvulnerable = args.length >= 4 && args[3].equals("1");

                EntityActor actor = new EntityActor(this.mc.theWorld);
                actor.modify(isInvulnerable, name, skin, false);

                this.mc.displayGuiScreen(new GuiActor(actor, this.pos, index));
            }
        }
    }

    @Override
    protected void drawPane()
    {
        for (int i = 0, c = this.cast.size(); i < c; i++)
        {
            String member = this.cast.get(i).split(":")[0];
            int x = this.x + 6;
            int y = this.y + i * 24;

            this.fontRendererObj.drawStringWithShadow(member, x, y + 8, 0xffffff);
        }
    }
}
