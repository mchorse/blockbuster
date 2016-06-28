package noname.blockbuster.client.gui.elements;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.director.PacketDirectorMapRemove;

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
            int y = this.y + i * 24;

            this.buttonList.add(new GuiButton(i, x, y + 3, 54, 18, I18n.format("blockbuster.gui.remove")));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (this.cast.get(button.id) != null)
        {
            Dispatcher.getInstance().sendToServer(new PacketDirectorMapRemove(this.pos, this.cast.get(button.id)));
        }
    }

    @Override
    protected void drawPane()
    {
        for (int i = 0, c = this.cast.size(); i < c; i++)
        {
            String member = this.cast.get(i);
            int x = this.x + 6;
            int y = this.y + i * 24;

            this.fontRendererObj.drawStringWithShadow(member, x, y + 8, 0xffffff);
        }
    }
}
