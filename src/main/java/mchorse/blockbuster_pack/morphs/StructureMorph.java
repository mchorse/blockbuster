package mchorse.blockbuster_pack.morphs;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketStructureRequest;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.renderer.ChunkRenderContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
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
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, 0);
            GlStateManager.scale(scale, scale, scale);
            renderer.render();
            GlStateManager.popMatrix();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        StructureRenderer renderer = STRUCTURES.get(this.structure);

        if (renderer != null)
        {
            GlStateManager.color(1, 1, 1);

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            // GlStateManager.scale(16, 16, 16);
            renderer.render();
            GlStateManager.popMatrix();
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
        public ChunkRenderContainer renderer;
        public RenderChunk[] chunks;
        public BlockPos size;

        public StructureRenderer(ChunkRenderContainer renderer, RenderChunk[] chunks, BlockPos size)
        {
            this.renderer = renderer;
            this.chunks = chunks;
            this.size = size;
        }

        public void render()
        {
            for (RenderChunk chunk : this.chunks)
            {
                this.renderer.addRenderChunk(chunk, BlockRenderLayer.SOLID);
            }

            this.renderer.renderChunkLayer(BlockRenderLayer.SOLID);
        }

        public void delete()
        {
            for (RenderChunk chunk : this.chunks)
            {
                chunk.deleteGlResources();
            }
        }
    }
}