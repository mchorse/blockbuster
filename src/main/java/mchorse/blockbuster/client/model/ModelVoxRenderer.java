package mchorse.blockbuster.client.model;

import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.api.formats.Mesh;
import mchorse.blockbuster.api.formats.obj.OBJParser;
import mchorse.blockbuster.api.formats.vox.MeshesVOX;
import mchorse.blockbuster.api.formats.vox.data.VoxTexture;
import mchorse.blockbuster.client.render.RenderCustomModel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

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
     * Vox palette texture
     */
    public VoxTexture texture;

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
            BufferBuilder renderer = Tessellator.getInstance().getBuffer();

            /* Generate display list */
            Mesh mesh = this.mesh.mesh;
            {
                int id = GLAllocation.generateDisplayLists(1);

                GlStateManager.glNewList(id, GL11.GL_COMPILE);
                renderer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);

                for (int i = 0, c = mesh.triangles; i < c; i++)
                {
                    float x = (mesh.posData[i * 3] - this.limb.origin[0]) / 16F;
                    float y = -(mesh.posData[i * 3 + 1] - this.limb.origin[1]) / 16F;
                    float z = (mesh.posData[i * 3 + 2] - this.limb.origin[2]) / 16F;

                    float u = mesh.texData[i * 2];
                    float v = mesh.texData[i * 2 + 1];

                    float nx = mesh.normData[i * 3];
                    float ny = mesh.normData[i * 3 + 1];
                    float nz = mesh.normData[i * 3 + 2];

                    renderer.pos(x, y, z).tex(u, v).normal(nx, ny, nz).endVertex();
                }

                Tessellator.getInstance().draw();
                GlStateManager.glEndList();

                this.displayList = id;
            }

            this.texture = new VoxTexture(this.mesh.document.palette);
            this.compiled = true;
            this.mesh = null;
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
        if (this.texture != null)
        {
            GlStateManager.bindTexture(this.texture.getTexture());
            GL11.glCallList(this.displayList);
            RenderCustomModel.bindLastTexture();
        }
    }

    @Override
    public void delete()
    {
        super.delete();

        if (this.texture != null)
        {
            this.texture.deleteTexture();
            this.texture = null;
        }
    }
}