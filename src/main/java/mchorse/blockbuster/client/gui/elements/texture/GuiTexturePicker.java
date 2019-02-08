package mchorse.blockbuster.client.gui.elements.texture;

import java.util.Collections;
import java.util.Comparator;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.elements.texture.FileTree.AbstractEntry;
import mchorse.blockbuster.client.gui.elements.texture.FileTree.FileEntry;
import mchorse.blockbuster.client.gui.elements.texture.FileTree.FolderEntry;
import mchorse.blockbuster.utils.MultiResourceLocation;
import mchorse.blockbuster.utils.RLUtils;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.widgets.buttons.GuiTextureButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiTexturePicker extends GuiElement
{
    public GuiTextElement text;
    public GuiButtonElement<GuiButton> pick;
    public GuiFolderEntryList picker;

    public GuiButtonElement<GuiButton> multi;
    public GuiButtonElement<GuiTextureButton> add;
    public GuiButtonElement<GuiTextureButton> remove;
    public GuiResourceLocationList multiList;

    public Consumer<ResourceLocation> callback;
    public MultiResourceLocation current;

    public GuiTexturePicker(Minecraft mc, Consumer<ResourceLocation> callback)
    {
        super(mc);

        this.text = new GuiTextElement(mc, 1000, (str) -> this.setCurrent(str.isEmpty() ? null : RLUtils.create(str)));
        this.pick = GuiButtonElement.button(mc, "X", (b) -> this.setVisible(false));
        this.picker = new GuiFolderEntryList(mc, (entry) ->
        {
            ResourceLocation rl = entry.resource;

            this.setCurrent(rl);
            this.text.setText(rl == null ? "" : rl.toString());
        });

        this.multi = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.multi_skin"), (b) -> this.toggleMulti());
        this.multiList = new GuiResourceLocationList(mc, (rl) -> this.displayCurrent(this.multiList.getCurrent()));
        this.add = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 32, 32, 32, 48, (b) -> this.addMultiSkin());
        this.remove = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 64, 32, 64, 48, (b) -> this.removeMultiSkin());

        this.createChildren();
        this.text.resizer().set(115, 5, 0, 20).parent(this.area).w(1, -145);
        this.pick.resizer().set(0, 5, 20, 20).parent(this.area).x(1, -25);
        this.picker.resizer().set(115, 30, 0, 0).parent(this.area).w(1, -120).h(1, -30);

        this.multi.resizer().parent(this.area).set(5, 5, 100, 20);
        this.add.resizer().parent(this.area).set(67, 7, 16, 16);
        this.remove.resizer().relative(this.add.resizer()).set(20, 0, 16, 16);
        this.multiList.resizer().set(5, 35, 100, 0).parent(this.area).h(1, -40);

        this.children.add(this.text, this.pick, this.picker, this.multi, this.multiList, this.add, this.remove);
        this.callback = callback;
    }

    private void addMultiSkin()
    {
        ResourceLocation rl = this.picker.getCurrentResource();

        if (rl == null && !this.text.field.getText().isEmpty())
        {
            rl = RLUtils.create(this.text.field.getText());
        }

        this.multiList.add(rl);
        this.multiList.current = this.multiList.getList().indexOf(rl);

        if (this.multiList.current >= 0)
        {
            this.displayCurrent(this.multiList.getCurrent());
        }
    }

    private void removeMultiSkin()
    {
        if (this.multiList.current >= 0)
        {
            this.multiList.getList().remove(this.multiList.current);
            this.multiList.update();
            this.multiList.current--;

            if (this.multiList.current >= 0)
            {
                this.displayCurrent(this.multiList.getCurrent());
            }
        }
    }

    private void setCurrent(ResourceLocation rl)
    {
        if (this.current != null)
        {
            if (this.multiList.current != -1 && rl != null)
            {
                this.multiList.getList().set(this.multiList.current, rl);
            }

            rl = this.current;
        }

        if (this.callback != null)
        {
            this.callback.accept(rl);
        }
    }

    private void displayCurrent(ResourceLocation rl)
    {
        // this.picker.setCurrent(rl);
        this.text.setText(rl == null ? "" : rl.toString());
    }

    private void toggleMulti()
    {
        this.setMultiSkin(this.current == null ? new MultiResourceLocation(this.text.field.getText()) : null);

        if (this.picker.getCurrent() != null)
        {
            this.setCurrent(this.picker.getCurrentResource());
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        /* Necessary measure to avoid triggering buttons when you press 
         * on a text field, for example */
        return super.mouseClicked(mouseX, mouseY, mouseButton) || (this.isVisible() && this.area.isInside(mouseX, mouseY));
    }

    public void set(ResourceLocation skin)
    {
        this.text.field.setText(skin == null ? "" : skin.toString());
        this.text.field.setCursorPositionZero();
        // this.picker.setCurrent(skin);

        this.setMultiSkin(skin);
    }

    private void setMultiSkin(ResourceLocation skin)
    {
        boolean show = skin instanceof MultiResourceLocation;

        if (show)
        {
            this.current = (MultiResourceLocation) skin;

            this.multiList.current = this.current.children.isEmpty() ? -1 : 0;
            this.multiList.setList(this.current.children);
            this.multiList.update();

            this.picker.resizer().set(115, 30, 0, 0).parent(this.area).w(1, -120).h(1, -30);
            this.multi.resizer().set(5, 5, 60, 20).parent(this.area);
        }
        else
        {
            this.current = null;
            this.multiList.setList(null);

            this.picker.resizer().set(5, 30, 0, 0).parent(this.area).w(1, -10).h(1, -30);
            this.multi.resizer().set(5, 5, 100, 20).parent(this.area);
        }

        this.multiList.setVisible(show);
        this.add.setVisible(show);
        this.remove.setVisible(show);

        GuiScreen screen = this.mc.currentScreen;

        this.picker.resize(screen.width, screen.height);
        this.multi.resize(screen.width, screen.height);
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        drawGradientRect(this.area.x, this.area.y, this.area.getX(1), this.area.getY(1), 0x88000000, 0xff000000);

        if (this.multiList.isVisible())
        {
            this.mc.renderEngine.bindTexture(GuiDashboard.ICONS);
            GuiUtils.drawContinuousTexturedBox(this.area.x, this.area.y, 0, 32, 110, this.area.h, 32, 32, 0, 0);
            drawRect(this.area.x, this.area.y, this.area.x + 110, this.area.y + 30, 0x44000000);
            drawGradientRect(this.area.x, this.area.getY(1) - 20, this.area.x + 110, this.area.getY(1), 0x00, 0x44000000);
        }

        if (this.picker.getList().isEmpty())
        {
            this.drawCenteredString(this.font, I18n.format("blockbuster.gui.no_data"), this.area.getX(0.5F), this.area.getY(0.5F), 0xffffff);
        }

        super.draw(tooltip, mouseX, mouseY, partialTicks);

        ResourceLocation loc = this.picker.getCurrentResource();

        /* Draw preview */
        if (loc != null)
        {
            this.mc.renderEngine.bindTexture(loc);

            int w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
            int h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

            int x = this.area.getX(1);
            int y = this.area.getY(1);
            int fw = w;
            int fh = h;

            if (fw > 128 || fh > 128)
            {
                fw = fh = 128;

                if (w > h)
                {
                    fh = (int) ((h / (float) w) * fw);
                }
                else if (h > w)
                {
                    fw = (int) ((w / (float) h) * fh);
                }
            }

            x -= fw + 10;
            y -= fh + 10;

            this.mc.renderEngine.bindTexture(GuiDashboard.ICONS);
            GuiUtils.drawContinuousTexturedBox(x, y, 0, 96, fw, fh, 32, 32, 0, 0);
            this.mc.renderEngine.bindTexture(loc);

            GlStateManager.enableAlpha();
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer vertexbuffer = tessellator.getBuffer();
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            vertexbuffer.pos(x, y + fh, 0.0D).tex(0, 1).endVertex();
            vertexbuffer.pos(x + fw, y + fh, 0.0D).tex(1, 1).endVertex();
            vertexbuffer.pos(x + fw, y, 0.0D).tex(1, 0).endVertex();
            vertexbuffer.pos(x, y, 0.0D).tex(0, 0).endVertex();
            tessellator.draw();
        }
    }

    public static class GuiResourceLocationList extends GuiListElement<ResourceLocation>
    {
        public GuiResourceLocationList(Minecraft mc, Consumer<ResourceLocation> callback)
        {
            super(mc, callback);

            this.scroll.scrollItemSize = 16;
        }

        @Override
        public void sort()
        {
            Collections.sort(this.list, new Comparator<ResourceLocation>()
            {
                @Override
                public int compare(ResourceLocation o1, ResourceLocation o2)
                {
                    return o1.toString().compareToIgnoreCase(o2.toString());
                }
            });
        }

        @Override
        public void drawElement(ResourceLocation element, int i, int x, int y, boolean hover)
        {
            if (this.current == i)
            {
                Gui.drawRect(x, y, x + this.scroll.w, y + this.scroll.scrollItemSize, 0x880088ff);
            }

            this.font.drawStringWithShadow(element.toString(), x + 4, y + 4, hover ? 16777120 : 0xffffff);
        }
    }

    public static class GuiFolderEntryList extends GuiListElement<AbstractEntry>
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

            this.mc.renderEngine.bindTexture(GuiDashboard.ICONS);

            GlStateManager.color(1, 1, 1, hover ? 0.8F : 0.6F);
            if (element instanceof FolderEntry)
            {
                Gui.drawScaledCustomSizeModalRect(x + 2, y + 2, 112, 64, 16, 16, 16, 16, 256, 256);
            }
            else
            {
                Gui.drawScaledCustomSizeModalRect(x + 2, y + 2, 96, 64, 16, 16, 16, 16, 256, 256);
            }

            this.font.drawStringWithShadow(element.title, x + 20, y + 6, hover ? 16777120 : 0xffffff);
        }
    }
}