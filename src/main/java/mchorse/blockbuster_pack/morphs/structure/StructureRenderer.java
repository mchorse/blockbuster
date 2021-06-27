package mchorse.blockbuster_pack.morphs.structure;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.network.client.ClientHandlerStructure;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

/**
 * Structure renderer
 *
 * All it does is renders compiled display list and also has the
 * method {@link #delete()} to clean up memory.
 */
@SideOnly(Side.CLIENT)
public class StructureRenderer
{
    public StructureStatus status = StructureStatus.UNLOADED;
    public ByteBuffer buffer;
    public int count = 0;
    public BlockPos size;
    public ClientHandlerStructure.FakeWorld world;

    public StructureRenderer()
    {}

    public StructureRenderer(BlockPos size, ClientHandlerStructure.FakeWorld world)
    {
        this.size = size;
        this.world = world;

        this.rebuild();
    }

    public void rebuild()
    {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();

        this.render(buffer);
        buffer.finishDrawing();

        int count = buffer.getVertexCount();
        ByteBuffer byteBuffer = buffer.getByteBuffer();

        this.buffer = GLAllocation.createDirectByteBuffer(byteBuffer.capacity());
        this.buffer.put(byteBuffer);
        this.count = count;
        this.status = StructureStatus.LOADED;
    }

    public void render()
    {
        GL11.glNormal3f(0, 0.6F, 0);

        if (Blockbuster.cachedStructureRendering.get())
        {
            if (this.buffer == null)
            {
                this.rebuild();
            }

            if (this.buffer != null)
            {
                int stride = 28;

                GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
                GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

                this.buffer.position(0);
                GlStateManager.glVertexPointer(3, GL11.GL_FLOAT, stride, this.buffer);
                this.buffer.position(12);
                GlStateManager.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, stride, this.buffer);
                this.buffer.position(16);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                GlStateManager.glTexCoordPointer(2, GL11.GL_FLOAT, stride, this.buffer);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                this.buffer.position(24);
                GlStateManager.glTexCoordPointer(2, GL11.GL_SHORT, stride, this.buffer);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);

                GlStateManager.glDrawArrays(GL11.GL_QUADS, 0, this.count);

                GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
                GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
                GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            }
        }
        else
        {
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buffer = tess.getBuffer();

            this.render(buffer);

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
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
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

    public void renderTEs()
    {
        if (this.world == null)
        {
            return;
        }

        for (TileEntity te : this.world.loadedTileEntityList)
        {
            BlockPos pos = te.getPos();
            TileEntityRendererDispatcher.instance.render(te, pos.getX() - this.size.getX() / 2D - 1, pos.getY() - 1, pos.getZ() - this.size.getZ() / 2D - 1, 0);
        }
    }

    public void delete()
    {
        if (this.buffer != null)
        {
            this.buffer = null;
            this.count = 0;
            this.status = StructureStatus.UNLOADED;
        }
    }
}