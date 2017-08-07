package mchorse.blockbuster.model_editor.elements;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import mchorse.blockbuster.api.ModelPack;
import mchorse.metamorph.client.gui.utils.GuiScrollPane;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

/**
 * Texture picker GUI
 *
 * This GUI is responsible for allowing user to pick a texture from default one
 * and also from {@link ModelPack}.
 */
public class GuiTexturePicker extends GuiScrollPane
{
    private final String strSearch = I18n.format("blockbuster.gui.me.search_texture");

    private ModelPack pack;
    private List<TextureInfo> textures = new ArrayList<TextureInfo>();
    private ITexturePicker picker;
    private GuiTextField search;
    private ResourceLocation name;

    public GuiTexturePicker(ITexturePicker picker, ModelPack pack)
    {
        this.picker = picker;
        this.pack = pack;
        this.pack.reload();

        /* Adding factory textures */
        this.textures.add(new TextureInfo(64, 32, new ResourceLocation("blockbuster:textures/entity/actor.png")));
        this.textures.add(new TextureInfo(64, 64, new ResourceLocation("minecraft:textures/entity/steve.png")));
        this.textures.add(new TextureInfo(64, 64, new ResourceLocation("minecraft:textures/entity/alex.png")));

        for (Map.Entry<String, Map<String, File>> skins : pack.skins.entrySet())
        {
            for (Map.Entry<String, File> skin : skins.getValue().entrySet())
            {
                try
                {
                    ResourceLocation path = new ResourceLocation("blockbuster.actors", skins.getKey() + "/" + skin.getKey());
                    BufferedImage img = ImageIO.read(skin.getValue());

                    this.textures.add(new TextureInfo(img.getWidth(), img.getHeight(), path));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void search(String search)
    {
        int i = 0;
        int index = 0;

        for (TextureInfo texture : this.textures)
        {
            if (search.isEmpty())
            {
                texture.highlight = true;
            }
            else
            {
                texture.highlight = texture.path.toString().toLowerCase().indexOf(search.toLowerCase()) != -1;

                if (texture.highlight && index == 0)
                {
                    index = i;
                }

                i++;
            }
        }

        this.scrollTo(index / 6 * this.w / 6);
    }

    /**
     * Recalculate the scroll height of texture picker
     */
    @Override
    public void initGui()
    {
        int w = this.w - 40;

        this.scrollHeight = (this.textures.size() / 6 + 1) * (this.w / 6);
        this.search = new GuiTextField(0, this.fontRenderer, this.width / 2 - w / 2, this.height - 25, w, 18);
        this.search.setFocused(false);
        this.name = null;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (!this.hidden)
        {
            this.search.textboxKeyTyped(typedChar, keyCode);

            if (this.search.isFocused())
            {
                this.search(this.search.getText());
            }
        }
    }

    /**
     * Mouse click event
     *
     * This method is responsible for picking the texture. It uses simple
     * arithmetic algorithm to calculate the index of the texture based on
     * mouse's X and Y coordinates.
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.hidden || this.dragging)
        {
            return;
        }

        if (mouseX >= this.search.x && mouseX <= this.search.x + this.search.width && mouseY >= this.search.y && mouseY <= this.search.y + this.search.height)
        {
            this.search.mouseClicked(mouseX, mouseY, mouseButton);

            return;
        }

        if (!this.isInside(mouseX, mouseY))
        {
            this.setHidden(true);
            return;
        }

        int x = mouseX - this.x;
        int y = mouseY - this.y + this.scrollY;

        int width = this.w / 6;
        int index = x / width + y / width * 6;

        if (this.picker != null && index >= 0 && index < this.textures.size())
        {
            this.picker.pickTexture(this.textures.get(index).path.toString());
            this.search("");
            this.search.setText("");
            this.search.setFocused(false);
        }
    }

    /**
     * Draw solid black background
     */
    @Override
    protected void drawBackground()
    {
        Gui.drawRect(this.x + 1, this.y + 1, this.x + this.w - 1, this.y + this.h - 1, 0xff000000);
    }

    /**
     * Draw textures on the screen
     */
    @Override
    protected void drawPane(int mouseX, int mouseY, float partialTicks)
    {
        int i = 0;
        int width = this.w / 6;

        for (TextureInfo texture : this.textures)
        {
            int x = this.x + i % 6 * width;
            int y = this.y + i / 6 * width;

            boolean hover = mouseX >= x && mouseX < x + width && mouseY + this.scrollY >= y && mouseY + this.scrollY < y + width;

            GlStateManager.enableBlend();
            this.mc.renderEngine.bindTexture(texture.path);
            GlStateManager.color(hover ? 0.0F : 1.0F, 1.0F, 1.0F, texture.highlight ? 1.0F : 0.25F);
            Gui.drawScaledCustomSizeModalRect(x, y, 8, 8, 8, 8, width, width, texture.w, texture.h);

            if (hover && this.name == null)
            {
                this.name = texture.path;
            }

            i++;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (!this.hidden)
        {
            if (this.name != null)
            {
                String path = this.name.getResourcePath();
                String domain = this.name.getResourceDomain();

                int w = Math.max(this.fontRenderer.getStringWidth(path), this.fontRenderer.getStringWidth(domain)) + 4;
                int x = this.x + this.w / 2;

                Gui.drawRect(x - w / 2, 3, x + w / 2, 25, 0x88000000);
                this.drawCenteredString(this.fontRenderer, path, this.width / 2, 5, 0xffffff);
                this.drawCenteredString(this.fontRenderer, domain, this.width / 2, 15, 0xffffff);
            }

            this.search.drawTextBox();
            this.name = null;

            if (!this.search.isFocused() && this.search.getText().isEmpty())
            {
                this.fontRenderer.drawStringWithShadow(this.strSearch, this.search.x + 4, this.search.y + 5, 0xaaaaaa);
            }
        }
    }

    /**
     * Texture info class
     *
     * This class is responsible for holding information about texture in the
     * texture picker. Used mostly for caching purposes and the ability to
     * correctly size texture's width and height.
     */
    public static class TextureInfo
    {
        public int w;
        public int h;
        public ResourceLocation path;
        public boolean highlight = true;

        public TextureInfo(int w, int h, ResourceLocation path)
        {
            this.w = w;
            this.h = h;
            this.path = path;
        }
    }

    /**
     * Pick texture interface
     *
     * Basically {@link GuiTexturePicker} needs a callback for notifying some
     * object that user clicked some texture.
     */
    public static interface ITexturePicker
    {
        public void pickTexture(String texture);
    }
}