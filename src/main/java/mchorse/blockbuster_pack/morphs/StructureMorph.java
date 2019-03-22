package mchorse.blockbuster_pack.morphs;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketStructureRequest;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class StructureMorph extends AbstractMorph
{
    @SideOnly(Side.CLIENT)
    public static final Map<String, StructureRenderer> STRUCTURES = new HashMap<String, StructureRenderer>();

    public String structure = "";

    @SideOnly(Side.CLIENT)
    public static void request()
    {
        if (STRUCTURES.isEmpty())
        {
            Dispatcher.sendToServer(new PacketStructureRequest());
        }
    }

    @SideOnly(Side.CLIENT)
    public static void cleanStructures()
    {
        for (StructureRenderer renderer : STRUCTURES.values())
        {
            renderer.delete();
        }

        STRUCTURES.clear();
    }

    public StructureMorph()
    {
        this.name = "blockbuster.structure";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        StructureRenderer renderer = STRUCTURES.get(this.structure);

        if (renderer != null)
        {
            int max = Math.max(renderer.size.getX(), Math.max(renderer.size.getY(), renderer.size.getZ()));

            scale /= 0.65F * max;

            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            GlStateManager.enableDepth();
            GlStateManager.enableAlpha();
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, 0);
            GlStateManager.scale(scale, scale, scale);
            GlStateManager.rotate(45.0F, -1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(45.0F, 0.0F, -1.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            renderer.render();
            GlStateManager.popMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.disableDepth();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        StructureRenderer renderer = STRUCTURES.get(this.structure);

        if (renderer != null)
        {
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            RenderHelper.disableStandardItemLighting();
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            renderer.render();
            GlStateManager.popMatrix();

            GlStateManager.disableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.shadeModel(GL11.GL_FLAT);
            RenderHelper.enableStandardItemLighting();
        }
    }

    @Override
    public AbstractMorph clone(boolean isRemote)
    {
        StructureMorph morph = new StructureMorph();

        morph.name = this.name;
        morph.settings = this.settings;
        morph.structure = this.structure;

        return null;
    }

    @Override
    public float getWidth(EntityLivingBase target)
    {
        return 0.6F;
    }

    @Override
    public float getHeight(EntityLivingBase target)
    {
        return 1.8F;
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof StructureMorph)
        {
            StructureMorph morph = (StructureMorph) obj;

            result = result && Objects.equals(this.structure, morph.structure);
        }

        return result;
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);
        this.structure = tag.getString("Structure");
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);
        tag.setString("Structure", this.structure);
    }

    @SideOnly(Side.CLIENT)
    public static class StructureRenderer
    {
        public int list;
        public BlockPos size;

        public StructureRenderer(int list, BlockPos size)
        {
            this.list = list;
            this.size = size;
        }

        public void render()
        {
            GL11.glCallList(this.list);
        }

        public void delete()
        {
            if (this.list != -1)
            {
                GL11.glDeleteLists(this.list, 1);
            }
        }
    }
}