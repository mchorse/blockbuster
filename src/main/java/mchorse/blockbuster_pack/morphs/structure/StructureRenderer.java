package mchorse.blockbuster_pack.morphs.structure;

import mchorse.blockbuster.network.client.ClientHandlerStructure;
import mchorse.blockbuster_pack.morphs.StructureMorph;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Structure renderer
 *
 * All it does is renders compiled display list and also has the
 * method {@link #delete()} to clean up memory.
 */
@SideOnly(Side.CLIENT)
public class StructureRenderer
{
    public static int renderTimes = 0;

    public StructureStatus status = StructureStatus.UNLOADED;
    public Map<Biome, int[]> solidBuffers = new HashMap<Biome, int[]>();
    public Map<Biome, int[]> cutoutBuffers = new HashMap<Biome, int[]>();
    public Map<Biome, int[]> translucentBuffers = new HashMap<Biome, int[]>();
    public BlockPos size;
    public ClientHandlerStructure.FakeWorld world;
    public VertexFormat worldLighting;
    public VertexFormat structureLighting;
    public Map<TileEntity, Integer> tileEntityLighting = new LinkedHashMap<TileEntity, Integer>();

    public StructureRenderer()
    {}

    public StructureRenderer(BlockPos size, ClientHandlerStructure.FakeWorld world)
    {
        this.size = size;
        this.world = world;

        this.setupFormat();
        this.status = StructureStatus.LOADED;
    }

    public void setupFormat()
    {
        /* Check if optifine changed format. */
        if (!DefaultVertexFormats.BLOCK.equals(this.structureLighting))
        {
            this.solidBuffers.clear();
            this.structureLighting = DefaultVertexFormats.BLOCK;
            this.worldLighting = new VertexFormat();

            for (VertexFormatElement element : this.structureLighting.getElements())
            {
                if (DefaultVertexFormats.TEX_2S.equals(element))
                {
                    this.worldLighting.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUsage.PADDING, 2));
                }
                else
                {
                    this.worldLighting.addElement(element);
                }
            }
        }
    }

    public void rebuild(Biome biome)
    {
        this.world.biome = biome;

        int ao = Minecraft.getMinecraft().gameSettings.ambientOcclusion;

        Minecraft.getMinecraft().gameSettings.ambientOcclusion = 0;

        this.solidBuffers.put(biome, this.render(0));
        this.cutoutBuffers.put(biome, this.render(1));
        this.translucentBuffers.put(biome, this.render(2));

        Minecraft.getMinecraft().gameSettings.ambientOcclusion = ao;

        if (this.tileEntityLighting.isEmpty())
        {
            for (TileEntity te : this.world.loadedTileEntityList)
            {
                this.tileEntityLighting.put(te, this.world.getCombinedLight(te.getPos(), 0));
            }
        }
    }

    public void render(StructureMorph morph)
    {
        GL11.glNormal3f(0, 0.6F, 0);

        Biome biome = morph.getBiome();

        if (this.solidBuffers.get(biome) == null)
        {
            this.setupFormat();
            this.rebuild(biome);
        }

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        if (this.solidBuffers.get(biome) != null && this.solidBuffers.get(biome).length > 0)
        {
            buffer.begin(GL11.GL_QUADS, morph.lighting ? this.worldLighting : this.structureLighting);
            buffer.addVertexData(this.solidBuffers.get(biome));
            tess.draw();
        }

        GlStateManager.enableAlpha();

        if (this.cutoutBuffers.get(biome) != null && this.cutoutBuffers.get(biome).length > 0)
        {
            buffer.begin(GL11.GL_QUADS, morph.lighting ? this.worldLighting : this.structureLighting);
            buffer.addVertexData(this.cutoutBuffers.get(biome));
            tess.draw();
        }

        GlStateManager.enableBlend();

        if (this.translucentBuffers.get(biome) != null && this.translucentBuffers.get(biome).length > 0)
        {
            buffer.begin(GL11.GL_QUADS, morph.lighting ? this.worldLighting : this.structureLighting);
            buffer.addVertexData(this.translucentBuffers.get(biome));
            tess.draw();
        }
    }

    public int[] render(int type)
    {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();

        buffer.begin(GL11.GL_QUADS, this.structureLighting);

        BlockPos origin = new BlockPos(1, 1, 1);
        int w = this.size.getX();
        int h = this.size.getY();
        int d = this.size.getZ();

        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

        /* Centerize the geometry */
        buffer.setTranslation(-w / 2F - origin.getX(), -origin.getY(), -d / 2F - origin.getZ());

        for (BlockPos.MutableBlockPos pos : BlockPos.getAllInBoxMutable(origin, origin.add(w, h, d)))
        {
            IBlockState state = this.world.getBlockState(pos);
            Block block = state.getBlock();

            if (block.getDefaultState().getRenderType() != EnumBlockRenderType.INVISIBLE)
            {
                if (type == 0 && block.getBlockLayer() == BlockRenderLayer.SOLID
                    || type == 1 && (block.getBlockLayer() == BlockRenderLayer.CUTOUT || block.getBlockLayer() == BlockRenderLayer.CUTOUT_MIPPED)
                    || type == 2 && block.getBlockLayer() == BlockRenderLayer.TRANSLUCENT)
                {
                    dispatcher.renderBlock(state, pos, this.world, buffer);
                }
            }
        }

        buffer.setTranslation(0, 0, 0);
        buffer.finishDrawing();

        int count = buffer.getVertexCount();
        int[] vertexData = new int[count * this.structureLighting.getIntegerSize()];
        buffer.getByteBuffer().asIntBuffer().get(vertexData);

        return vertexData;
    }

    public void renderTEs(StructureMorph morph)
    {
        if (renderTimes >= 10 || GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH) >= GL11.glGetInteger(GL11.GL_MAX_MODELVIEW_STACK_DEPTH) - 4)
        {
            return;
        }
        
        if (this.world == null)
        {
            return;
        }

        renderTimes++;
        
        for (Entry<TileEntity, Integer> entry : this.tileEntityLighting.entrySet())
        {
            if (!morph.lighting)
            {
                int block = entry.getValue() % 65536;
                int sky = entry.getValue() / 65536;
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, block, sky);
            }
            
            TileEntity te = entry.getKey();
            BlockPos pos = te.getPos();
            
            TileEntityRendererDispatcher.instance.render(te, pos.getX() - this.size.getX() / 2D - 1, pos.getY() - 1, pos.getZ() - this.size.getZ() / 2D - 1, 0);
            /* For Beacon & End Gateway */
            GlStateManager.disableFog();
        }
            
        renderTimes--;
    }

    public void delete()
    {
        if (!this.solidBuffers.isEmpty())
        {
            this.solidBuffers.clear();
            this.tileEntityLighting.clear();
            this.status = StructureStatus.UNLOADED;
        }
    }
}