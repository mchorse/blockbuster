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
    public Map<Biome, int[]> buffers = new HashMap<Biome, int[]>();
    public int count = 0;
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
            this.buffers.clear();
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
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();

        this.world.biome = biome;
        buffer.begin(GL11.GL_QUADS, this.structureLighting);
        this.render(buffer);
        buffer.finishDrawing();

        int count = buffer.getVertexCount();
        int[] vertexData = new int[count * this.structureLighting.getIntegerSize()];
        buffer.getByteBuffer().asIntBuffer().get(vertexData);

        this.buffers.put(biome, vertexData);
        this.count = count;
        
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

        if (this.buffers.get(biome) == null)
        {
            this.setupFormat();
            this.rebuild(biome);
        }

        if (this.buffers.get(biome) != null && this.buffers.get(biome).length > 0)
        {
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buffer = tess.getBuffer();
            buffer.begin(GL11.GL_QUADS, morph.lighting ? this.worldLighting : this.structureLighting);
            buffer.addVertexData(this.buffers.get(biome));
            tess.draw();
        }
    }

    public void render(BufferBuilder buffer)
    {
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
                dispatcher.renderBlock(state, pos, this.world, buffer);
            }
        }

        buffer.setTranslation(0, 0, 0);
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
        if (!this.buffers.isEmpty())
        {
            this.buffers.clear();
            this.tileEntityLighting.clear();
            this.count = 0;
            this.status = StructureStatus.UNLOADED;
        }
    }
}