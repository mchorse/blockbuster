package noname.blockbuster.client.gui.elements;

import net.minecraft.client.gui.GuiScreen;

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
        this.parent.appear(this);
    }
}
