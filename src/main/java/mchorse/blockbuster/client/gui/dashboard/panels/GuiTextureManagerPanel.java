package mchorse.blockbuster.client.gui.dashboard.panels;

import mchorse.blockbuster.client.gui.dashboard.GuiBlockbusterPanel;
import mchorse.blockbuster.client.gui.dashboard.GuiFirstTime;
import mchorse.blockbuster.client.textures.MipmapTexture;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiResourceLocationListElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.mclib.GuiDashboard;
import mchorse.mclib.client.gui.mclib.GuiDashboardPanel;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import mchorse.mclib.utils.ReflectionUtils;
import mchorse.mclib.utils.resources.RLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.opengl.GL11;

import java.util.Map;

/**
 * Texture manager panel
 * 
 * This is a GUI which allows viewing and managing textures loaded by
 * {@link TextureManager} class.
 * 
 * Besides viewing, it also allows changing filter (linear/nearest), 
 * generating mipmaps and removing (clearing) textures from the manager.
 */
public class GuiTextureManagerPanel extends GuiBlockbusterPanel
{
    public GuiResourceLocationListElement textures;
    public GuiToggleElement linear;
    public GuiToggleElement mipmap;
    public GuiButtonElement remove;
    public GuiButtonElement replace;

    private ResourceLocation rl;
    private String title = I18n.format("blockbuster.gui.texture.title");
    private String subtitle = I18n.format("blockbuster.gui.texture.subtitle");

    public GuiTextureManagerPanel(Minecraft mc, GuiDashboard dashboard)
    {
        super(mc, dashboard);

        this.textures = new GuiResourceLocationListElement(mc, (rl) -> this.pickRL(rl.get(0)));
        this.textures.background();
        this.linear = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.texture.linear"), false, (b) -> this.setLinear(b.isToggled()));
        this.linear.tooltip(IKey.lang("blockbuster.gui.texture.linear_tooltip"), Direction.LEFT);
        this.mipmap = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.texture.mipmap"), false, (b) -> this.setMipmap(b.isToggled()));
        this.mipmap.tooltip(IKey.lang("blockbuster.gui.texture.mipmap_tooltip"), Direction.LEFT);
        this.remove = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.remove"), (b) -> this.remove());
        this.replace = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.texture.replace"), (b) -> this.replace());

        this.textures.flex().relative(this.area).set(10, 50, 0, 0).w(1, -30 - 128).h(1, -60);
        this.remove.flex().relative(this.area).set(0, 0, 128, 20).x(1, -138).y(1, -30);
        this.replace.flex().relative(this.remove.resizer()).set(0, -25, 128, 20);
        this.linear.flex().relative(this.replace.resizer()).set(0, -16, 64, 11);
        this.mipmap.flex().relative(this.linear.resizer()).set(64, 0, 64, 11);

        this.add(this.textures, this.linear, this.mipmap, this.replace, this.remove);
    }

    private void pickRL(ResourceLocation rl)
    {
        if (this.rl == null)
        {
            this.linear.toggled(false);
            this.mipmap.toggled(false);
            this.rl = rl;
        }
        else
        {
            try
            {
                this.mc.renderEngine.bindTexture(rl);

                int filter = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER);

                boolean mipmap = ReflectionUtils.getTextures(this.mc.renderEngine).get(rl) instanceof MipmapTexture;
                boolean linear = filter == GL11.GL_LINEAR || filter == GL11.GL_LINEAR_MIPMAP_LINEAR || filter == GL11.GL_LINEAR_MIPMAP_NEAREST;

                this.linear.toggled(linear);
                this.mipmap.toggled(mipmap);
                this.rl = rl;
            }
            catch (Exception e)
            {}
        }
    }

    private void setLinear(boolean linear)
    {
        if (this.rl == null) return;

        this.mc.renderEngine.bindTexture(this.rl);

        boolean mipmap = this.mipmap.isToggled();

        int mod = linear ? (mipmap ? GL11.GL_LINEAR_MIPMAP_LINEAR : GL11.GL_LINEAR) : (mipmap ? GL11.GL_NEAREST_MIPMAP_LINEAR : GL11.GL_NEAREST);
        int mag = linear ? GL11.GL_LINEAR : GL11.GL_NEAREST;

        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mod);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mag);
    }

    private void setMipmap(boolean mipmap)
    {
        if (this.rl == null) return;

        Map<ResourceLocation, ITextureObject> map = ReflectionUtils.getTextures(this.mc.renderEngine);
        ITextureObject tex = map.get(this.rl);

        boolean mipmapped = tex instanceof MipmapTexture;

        /* Add or remove mipmap */
        if (mipmap && !mipmapped)
        {
            GlStateManager.deleteTexture(map.remove(this.rl).getGlTextureId());

            try
            {
                /* Load texture manually */
                tex = new MipmapTexture(this.rl);
                tex.loadTexture(Minecraft.getMinecraft().getResourceManager());

                map.put(this.rl, tex);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else if (!mipmap && mipmapped)
        {
            GlStateManager.deleteTexture(map.remove(this.rl).getGlTextureId());
        }
    }

    private void remove()
    {
        if (this.rl == null)
        {
            return;
        }

        Map<ResourceLocation, ITextureObject> map = ReflectionUtils.getTextures(this.mc.renderEngine);
        GlStateManager.deleteTexture(map.remove(this.rl).getGlTextureId());

        this.textures.remove(this.rl);
        this.textures.setIndex(this.textures.getIndex() - 1);
        this.pickRL(this.textures.getCurrentFirst());
    }

    private void replace()
    {
        if (this.rl == null || GuiModal.hasModal(this))
        {
            return;
        }

        GuiModal.addModal(this, () ->
        {
            GuiPromptModal modal = new GuiPromptModal(this.mc, IKey.lang("blockbuster.gui.texture.replace_modal"), this::replace);

            modal.text.field.setMaxStringLength(2000);
            modal.setValue(this.rl.toString());
            modal.flex().relative(this.area).set(10, 50, 0, 0).w(1, -30 - 128).h(1, -60);

            return modal;
        });
    }

    private void replace(String string)
    {
        if (this.rl.toString().equals(string))
        {
            return;
        }

        Map<ResourceLocation, ITextureObject> map = ReflectionUtils.getTextures(this.mc.renderEngine);
        ITextureObject texture = map.get(RLUtils.create(string));

        if (texture != null)
        {
            map.put(this.rl, texture);
        }
    }

    @Override
    public void open()
    {
        Map<ResourceLocation, ITextureObject> map = ReflectionUtils.getTextures(this.mc.renderEngine);

        this.textures.clear();
        this.textures.getList().addAll(map.keySet());
        this.textures.sort();
        this.textures.update();

        this.pickRL(this.rl);
        this.textures.setCurrent(this.rl);
    }

    @Override
    public void draw(GuiContext context)
    {
        this.font.drawString(this.title, this.area.x + 10, this.area.y + 10, 0xffffff);
        this.font.drawSplitString(this.subtitle, this.area.x + 10, this.area.y + 26, this.area.w - 158, 0xcccccc);

        /* Draw preview */
        if (this.rl != null)
        {
            this.mc.renderEngine.bindTexture(this.rl);

            int w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
            int h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

            int x = this.area.ex();
            int y = this.area.y + 10;
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

            this.mc.renderEngine.bindTexture(Icons.ICONS);
            GuiUtils.drawContinuousTexturedBox(x, y, 0, 240, fw, fh, 16, 16, 0, 0);
            this.mc.renderEngine.bindTexture(this.rl);

            GlStateManager.enableAlpha();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder vertexbuffer = tessellator.getBuffer();
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            vertexbuffer.pos(x, y + fh, 0.0D).tex(0, 1).endVertex();
            vertexbuffer.pos(x + fw, y + fh, 0.0D).tex(1, 1).endVertex();
            vertexbuffer.pos(x + fw, y, 0.0D).tex(1, 0).endVertex();
            vertexbuffer.pos(x, y, 0.0D).tex(0, 0).endVertex();
            tessellator.draw();
        }

        super.draw(context);
    }
}