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
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * Texture picker GUI
 *
 * This GUI is responsible for allowing user to pick a texture from default one
 * and also from {@link ModelPack}.
 */
public class GuiTexturePicker extends GuiScrollPane
{
    private ModelPack pack;
    private List<TextureInfo> textures = new ArrayList<TextureInfo>();
    private ITexturePicker picker;

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

    /**
     * Recalculate the scroll height of texture picker
     */
    @Override
    public void initGui()
    {
        this.scrollHeight = (this.textures.size() / 6 + 1) * (this.w / 6);
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

        if (this.getHidden() || this.dragging)
        {
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
        }
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

            this.mc.renderEngine.bindTexture(texture.path);
            GlStateManager.color(hover ? 0.0F : 1.0F, 1.0F, 1.0F, 1.0F);
            Gui.drawScaledCustomSizeModalRect(x, y, 8, 8, 8, 8, width, width, texture.w, texture.h);

            i++;
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