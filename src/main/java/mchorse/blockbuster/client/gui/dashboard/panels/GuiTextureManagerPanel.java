package mchorse.blockbuster.client.gui.dashboard.panels;

import java.util.Map;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.client.MipmapTexture;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.commands.model.SubCommandModelClear;
import mchorse.blockbuster.commands.model.SubCommandModelTexture;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.GuiTooltip.TooltipDirection;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiResourceLocationList;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.utils.resources.RLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiUtils;

/**
 * Texture manager panel
 * 
 * This is a GUI version of {@link SubCommandModelTexture} which allows 
 * viewing and managing textures loaded by {@link TextureManager} class.
 * 
 * Besides viewing, it also allows changing filter (linear/nearest), 
 * generating mipmaps and removing (clearing) textures from the manager.
 */
public class GuiTextureManagerPanel extends GuiDashboardPanel
{
    public GuiResourceLocationList textures;
    public GuiButtonElement<GuiCheckBox> linear;
    public GuiButtonElement<GuiCheckBox> mipmap;
    public GuiButtonElement<GuiButton> remove;
    public GuiButtonElement<GuiButton> replace;
    public GuiDelegateElement<IGuiElement> modal;

    private ResourceLocation rl;
    private String title = I18n.format("blockbuster.gui.texture.title");
    private String subtitle = I18n.format("blockbuster.gui.texture.subtitle");

    public GuiTextureManagerPanel(Minecraft mc, GuiDashboard dashboard)
    {
        super(mc, dashboard);

        this.textures = new GuiResourceLocationList(mc, (rl) -> this.pickRL(rl));
        this.linear = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.texture.linear"), false, (b) -> this.setLinear(b.button.isChecked()));
        this.linear.tooltip(I18n.format("blockbuster.gui.texture.linear_tooltip"), TooltipDirection.LEFT);
        this.mipmap = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.texture.mipmap"), false, (b) -> this.setMipmap(b.button.isChecked()));
        this.mipmap.tooltip(I18n.format("blockbuster.gui.texture.mipmap_tooltip"), TooltipDirection.LEFT);
        this.remove = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.remove"), (b) -> this.remove());
        this.replace = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.texture.replace"), (b) -> this.replace());
        this.modal = new GuiDelegateElement<IGuiElement>(mc, null);

        this.textures.resizer().parent(this.area).set(10, 50, 0, 0).w(1, -30 - 128).h(1, -60);
        this.remove.resizer().parent(this.area).set(0, 0, 128, 20).x(1, -138).y(1, -30);
        this.replace.resizer().relative(this.remove.resizer()).set(0, -25, 128, 20);
        this.linear.resizer().relative(this.replace.resizer()).set(0, -16, 64, 11);
        this.mipmap.resizer().relative(this.linear.resizer()).set(64, 0, 64, 11);
        this.modal.resizer().parent(this.area).set(10, 50, 0, 0).w(1, -30 - 128).h(1, -60);

        this.children.add(this.textures, this.linear, this.mipmap, this.replace, this.remove, this.modal);
    }

    private void pickRL(ResourceLocation rl)
    {
        this.rl = rl;

        if (this.rl == null)
        {
            this.linear.button.setIsChecked(false);
            this.mipmap.button.setIsChecked(false);
        }
        else
        {
            this.mc.renderEngine.bindTexture(rl);

            int filter = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER);

            boolean mipmap = SubCommandModelClear.getTextures(this.mc.renderEngine).get(rl) instanceof MipmapTexture;
            boolean linear = filter == GL11.GL_LINEAR || filter == GL11.GL_LINEAR_MIPMAP_LINEAR || filter == GL11.GL_LINEAR_MIPMAP_NEAREST;

            this.linear.button.setIsChecked(linear);
            this.mipmap.button.setIsChecked(mipmap);
        }
    }

    private void setLinear(boolean linear)
    {
        if (this.rl == null) return;

        this.mc.renderEngine.bindTexture(this.rl);

        boolean mipmap = this.mipmap.button.isChecked();

        int mod = linear ? (mipmap ? GL11.GL_LINEAR_MIPMAP_LINEAR : GL11.GL_LINEAR) : (mipmap ? GL11.GL_NEAREST_MIPMAP_LINEAR : GL11.GL_NEAREST);
        int mag = linear ? GL11.GL_LINEAR : GL11.GL_NEAREST;

        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mod);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mag);
    }

    private void setMipmap(boolean mipmap)
    {
        if (this.rl == null) return;

        Map<ResourceLocation, ITextureObject> map = SubCommandModelClear.getTextures(this.mc.renderEngine);
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
        if (this.rl == null) return;

        Map<ResourceLocation, ITextureObject> map = SubCommandModelClear.getTextures(this.mc.renderEngine);
        GlStateManager.deleteTexture(map.remove(this.rl).getGlTextureId());

        this.textures.remove(this.rl);
        this.textures.current--;
        this.pickRL(this.textures.getCurrent());
    }

    private void replace()
    {
        if (this.rl == null) return;

        GuiPromptModal modal = new GuiPromptModal(this.mc, this.modal, I18n.format("blockbuster.gui.texture.replace_modal"), (string) -> this.replace(string));
        modal.text.field.setMaxStringLength(2000);
        modal.setValue(this.rl.toString());

        this.modal.setDelegate(modal);
    }

    private void replace(String string)
    {
        if (this.rl.toString().equals(string))
        {
            return;
        }

        Map<ResourceLocation, ITextureObject> map = SubCommandModelClear.getTextures(this.mc.renderEngine);
        ITextureObject texture = map.get(RLUtils.create(string));

        if (texture != null)
        {
            map.put(this.rl, texture);
        }
    }

    @Override
    public void open()
    {
        Map<ResourceLocation, ITextureObject> map = SubCommandModelClear.getTextures(this.mc.renderEngine);

        this.textures.clear();
        this.textures.getList().addAll(map.keySet());
        this.textures.sort();
        this.textures.update();

        this.pickRL(this.rl);
        this.textures.setCurrent(this.rl);
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        this.textures.area.draw(0x88000000);
        this.font.drawString(this.title, this.area.x + 10, this.area.y + 10, 0xffffff);
        this.font.drawSplitString(this.subtitle, this.area.x + 10, this.area.y + 26, this.area.w - 158, 0xcccccc);

        /* Draw preview */
        if (this.rl != null)
        {
            this.mc.renderEngine.bindTexture(this.rl);

            int w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
            int h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

            int x = this.area.getX(1);
            int y = this.area.getY(0) + 10;
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

            this.mc.renderEngine.bindTexture(GuiBase.ICONS);
            GuiUtils.drawContinuousTexturedBox(x, y, 0, 96, fw, fh, 32, 32, 0, 0);
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

        super.draw(tooltip, mouseX, mouseY, partialTicks);
    }
}