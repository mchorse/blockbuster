package noname.blockbuster.client.gui.elements;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This GuiChildScreen
 *
 * It's like iOS's table views (you know these table views when you press on
 * the table row and its WHOOSHs on the right and other screen appears). You
 * need to pass to your screen a parent screen so it could invoke appear.
 */
@SideOnly(Side.CLIENT)
public abstract class GuiChildScreen extends GuiScreen
{
    protected GuiParentScreen parent;

    public GuiChildScreen(GuiParentScreen parent)
    {
        this.parent = parent;
    }

    public void close()
    {
        this.mc.displayGuiScreen(this.parent);

        if (this.parent != null)
        {
            this.parent.appear(this);
        }
    }
}
