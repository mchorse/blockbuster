package mchorse.blockbuster.client.model;

import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.api.formats.Mesh;
import mchorse.blockbuster.api.formats.obj.OBJParser;
import mchorse.blockbuster.api.formats.vox.MeshesVOX;
import mchorse.blockbuster.client.render.RenderCustomModel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

/**
 * Like {@link ModelCustomRenderer}, this model renders 
 */
@SideOnly(Side.CLIENT)
public class ModelVoxRenderer extends ModelCustomRenderer
{
    /**
     * Mesh containing the data about the model
     */
    public MeshesVOX mesh;

    /**
     * Palette texture ID
     */
    protected int paletteTexture = -1;

    public ModelVoxRenderer(ModelBase model, ModelLimb limb, ModelTransform transform, MeshesVOX mesh)
    {
        super(model, limb, transform);

        this.mesh = mesh;
    }

    /**
     * Instead of generating plain cube, this model renderer will 
     * generate mesh which was read by the {@link OBJParser}.
     */
    @Override
    protected void compileDisplayList(float scale)
    {
        if (this.mesh != null)
        {
            VertexBuffer renderer = Tessellator.getInstance().getBuffer();

            int texture = 0;
            int count = this.mesh.vox.palette.length;

            if (count > 0)
            {
                ByteBuffer buffer = GLAllocation.createDirectByteBuffer(count * 4);
                texture = GL11.glGenTextures();

                for (int color : this.mesh.vox.palette)
                {
                    int r = color >> 16 & 255;
                    int g = color >> 8 & 255;
                    int b = color & 255;
                    int a = color >> 24 & 255;

                    buffer.put((byte) r);
                    buffer.put((byte) g);
                    buffer.put((byte) b);
                    buffer.put((byte) a);
                }

                buffer.flip();

                /* For some reason, if there is no glTexParameter calls
                 * the texture becomes pure white */
                GlStateManager.bindTexture(texture);
                GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, count, 1, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
            }

            /* Generate display list */
            Mesh mesh = this.mesh.mesh;
            {
                int id = GLAllocation.generateDisplayLists(1);

                GlStateManager.glNewList(id, GL11.GL_COMPILE);
                renderer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);

                for (int i = 0, c = mesh.posData.length / 3; i < c; i++)
                {
                    float x = mesh.posData[i * 3] - this.limb.origin[0];
                    float y = -mesh.posData[i * 3 + 1] + this.limb.origin[1];
                    float z = mesh.posData[i * 3 + 2] - this.limb.origin[2];

                    float u = mesh.texData[i * 2];
                    float v = mesh.texData[i * 2 + 1];

                    float nx = mesh.normData[i * 3];
                    float ny = -mesh.normData[i * 3 + 1];
                    float nz = mesh.normData[i * 3 + 2];

                    renderer.pos(x, y, z).tex(u, v).normal(nx, ny, nz).endVertex();
                }

                Tessellator.getInstance().draw();
                GlStateManager.glEndList();

                this.displayList = id;
            }

            /* I hope this will get garbage collected xD */
            this.compiled = true;
            this.mesh = null;
            this.paletteTexture = texture;
        }
        else
        {
            super.compileDisplayList(scale);
        }
    }

    /**
     * Instead of rendering one default display list, this method 
     * renders the meshes  
     */
    @Override
    protected void renderDisplayList()
    {
        GlStateManager.enableCull();
        GlStateManager.bindTexture(this.paletteTexture);
        GL11.glCallList(this.displayList);
        RenderCustomModel.bindLastTexture();
        GlStateManager.disableCull();
    }

    @Override
    public void delete()
    {
        super.delete();

        if (this.paletteTexture != -1)
        {
            GL11.glDeleteTextures(this.paletteTexture);
        }
    }
}