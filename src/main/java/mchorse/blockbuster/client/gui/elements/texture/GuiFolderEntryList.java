package mchorse.blockbuster.client.gui.elements.texture;

import java.util.function.Consumer;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.elements.texture.AbstractEntry.FileEntry;
import mchorse.blockbuster.client.gui.elements.texture.AbstractEntry.FolderEntry;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * Folder entry list GUI
 * 
 * This GUI list element allows to navigate through the file tree 
 * entries. 
 */
public class GuiFolderEntryList extends GuiListElement<AbstractEntry>
{
    public Consumer<FileEntry> fileCallback;

    public GuiFolderEntryList(Minecraft mc, Consumer<FileEntry> fileCallback)
    {
        super(mc, null);

        this.callback = (entry) ->
        {
            if (entry instanceof FileEntry)
            {
                if (this.fileCallback != null)
                {
                    this.fileCallback.accept(((FileEntry) entry));
                }
            }
            else if (entry instanceof FolderEntry)
            {
                this.setList(((FolderEntry) entry).entries);
                this.update();
                this.current = -1;
            }
        };
        this.fileCallback = fileCallback;
        this.scroll.scrollItemSize = 16;
    }

    public ResourceLocation getCurrentResource()
    {
        AbstractEntry entry = this.getCurrent();

        if (entry != null && entry instanceof FileEntry)
        {
            return ((FileEntry) entry).resource;
        }

        return null;
    }

    @Override
    public void sort()
    {}

    @Override
    public void drawElement(AbstractEntry element, int i, int x, int y, boolean hover)
    {
        if (this.current == i)
        {
            Gui.drawRect(x, y, x + this.scroll.w, y + this.scroll.scrollItemSize, 0x880088ff);
        }

        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        this.mc.renderEngine.bindTexture(GuiDashboard.ICONS);

        GlStateManager.color(1, 1, 1, hover ? 0.8F : 0.6F);
        if (element instanceof FolderEntry)
        {
            Gui.drawScaledCustomSizeModalRect(x + 2, y, 112, 64, 16, 16, 16, 16, 256, 256);
        }
        else
        {
            Gui.drawScaledCustomSizeModalRect(x + 2, y, 96, 64, 16, 16, 16, 16, 256, 256);
        }

        this.font.drawStringWithShadow(element.title, x + 20, y + 4, hover ? 16777120 : 0xffffff);
    }
}