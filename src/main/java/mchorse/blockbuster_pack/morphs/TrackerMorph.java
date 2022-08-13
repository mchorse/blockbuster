package mchorse.blockbuster_pack.morphs;

import com.google.common.base.Objects;
import mchorse.blockbuster_pack.trackers.ApertureTracker;
import mchorse.blockbuster_pack.trackers.BaseTracker;
import mchorse.blockbuster_pack.trackers.TrackerRegistry;
import mchorse.mclib.client.Draw;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.utils.MatrixUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix4f;
import java.awt.Color;

public class TrackerMorph extends AbstractMorph
{
    public BaseTracker tracker = new ApertureTracker();

    public boolean hidden = false;

    private int renderTimer = 0;

    public TrackerMorph()
    {
        this.name = "tracker";
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getSubclassDisplayName()
    {
        if (this.tracker != null && !this.tracker.name.isEmpty())
        {
            return I18n.format("blockbuster.gui.tracker_morph.type." + TrackerRegistry.CLASS_TO_ID.get(this.tracker.getClass())) + " (" + this.tracker.name + ")";
        }
        else
        {
            return I18n.format("blockbuster.gui.tracker_morph.name");
        }
    }

    @Override
    public void update(EntityLivingBase target)
    {
        super.update(target);

        if (target.world.isRemote)
        {
            this.renderTimer++;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        this.renderTimer++;

        FontRenderer font = Minecraft.getMinecraft().fontRenderer;

        GlStateManager.pushMatrix();

        GlStateManager.disableDepth();
        GlStateManager.translate(x, y - 15, 0);

        GlStateManager.pushMatrix();

        GlStateManager.translate(-5, 0, 0);
        GlStateManager.rotate(-45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-45.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(scale * 2, scale * 2, scale * 2);

        renderPointer();

        GlStateManager.popMatrix();

        String name = I18n.format("blockbuster.gui.tracker_morph.name");

        if (this.tracker != null && this.tracker.name != null && !this.tracker.name.isEmpty())
        {
            name = this.tracker.name;
        }

        font.drawString(name, -font.getStringWidth(name) / 2, 5, 0xFFFFFFFF);

        GlStateManager.enableDepth();

        GlStateManager.popMatrix();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GL11.glTranslated(x, y, z);

        if (Minecraft.isGuiEnabled() && !this.hidden || GuiModelRenderer.isRendering())
        {
            GlStateManager.disableLighting();
            this.renderPointer();
            GlStateManager.enableLighting();

            /* Don't render labels in gui - it clutters the screen */
            if (!this.hidden)
            {
                this.renderLabel();
            }
        }

        if (this.tracker != null)
        {
            this.tracker.track(entity, x, y, z, entityYaw, partialTicks);
        }

        GlStateManager.popMatrix();
    }

    private void renderPointer()
    {
        this.renderTimer %= 50;
        int rgb = Color.HSBtoRGB(this.renderTimer / 50.0F, 1.0F, 1.0F);
        int rgb2 = Color.HSBtoRGB(this.renderTimer / 50.0F + 0.5F, 1.0F, 1.0F);

        GlStateManager.pushMatrix();

        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.disableTexture2D();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.glLineWidth(5.0F);
        buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0.0, 0.0, 0.0).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(0.0, 0.0, 1.0).color(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb >> 0 & 0xFF, 255).endVertex();
        buffer.pos(0.0, 0.0, 0.0).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(0.0, 0.5, 0.0).color(rgb2 >> 16 & 0xFF, rgb2 >> 8 & 0xFF, rgb2 >> 0 & 0xFF, 255).endVertex();
        tessellator.draw();
        GlStateManager.glLineWidth(1.0F);
        Draw.point(0, 0, 0);

        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableTexture2D();

        GlStateManager.popMatrix();
    }

    private void renderLabel()
    {
        if (this.tracker != null && !this.tracker.name.isEmpty())
        {
            Minecraft mc = Minecraft.getMinecraft();
            FontRenderer font = mc.fontRenderer;

            Matrix4f mat = MatrixUtils.readModelView(new Matrix4f());

            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            EntityRenderer.drawNameplate(font, this.tracker.name, mat.m03, mat.m13 + font.FONT_HEIGHT / 48.0F + 0.1F, mat.m23, 0, 180F, 0, mc.gameSettings.thirdPersonView == 2, false);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public AbstractMorph create()
    {
        return new TrackerMorph();
    }

    @Override
    public float getWidth(EntityLivingBase target)
    {
        return 0;
    }

    @Override
    public float getHeight(EntityLivingBase target)
    {
        return 0;
    }

    @Override
    public void copy(AbstractMorph from)
    {
        super.copy(from);

        if (from instanceof TrackerMorph)
        {
            TrackerMorph morph = (TrackerMorph) from;

            this.tracker = null;

            if (morph.tracker != null)
            {
                this.tracker = morph.tracker.copy();
            }

            this.hidden = morph.hidden;
        }
    }

    @Override
    public boolean canMerge(AbstractMorph morph)
    {
        if (morph instanceof TrackerMorph)
        {
            this.mergeBasic(morph);

            TrackerMorph trackerMorph = (TrackerMorph) morph;

            this.hidden = trackerMorph.hidden;

            if (this.tracker != null)
            {
                return this.tracker.canMerge(trackerMorph.tracker);
            }
        }

        return super.canMerge(morph);
    }

    @Override
    public boolean equals(Object object)
    {
        boolean result = super.equals(object);

        if (object instanceof TrackerMorph)
        {
            TrackerMorph morph = (TrackerMorph) object;

            result = result && Objects.equal(this.tracker, morph.tracker);
            result = result && this.hidden == morph.hidden;

            return result;
        }

        return result;
    }

    @Override
    public boolean useTargetDefault()
    {
        return true;
    }

    @Override
    public void reset()
    {
        this.tracker = new ApertureTracker();
        this.hidden = false;
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        if (tag.hasKey("Tracker", NBT.TAG_COMPOUND))
        {
            NBTTagCompound tracker = tag.getCompoundTag("Tracker");
            Class<? extends BaseTracker> clazz = TrackerRegistry.ID_TO_CLASS.get(tracker.getString("Id"));

            if (clazz != null)
            {
                try
                {
                    this.tracker = clazz.newInstance();
                    this.tracker.fromNBT(tracker);
                }
                catch (InstantiationException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }

        if (tag.hasKey("Hidden", NBT.TAG_BYTE))
        {
            this.hidden = tag.getBoolean("Hidden");
        }
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        if (this.tracker != null)
        {
            NBTTagCompound tracker = new NBTTagCompound();

            tracker.setString("Id", TrackerRegistry.CLASS_TO_ID.get(this.tracker.getClass()));
            this.tracker.toNBT(tracker);
            tag.setTag("Tracker", tracker);
        }

        if (this.hidden)
        {
            tag.setBoolean("Hidden", this.hidden);
        }
    }
}
