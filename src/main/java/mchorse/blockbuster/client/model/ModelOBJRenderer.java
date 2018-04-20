package mchorse.blockbuster.client.model;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.model.parsing.OBJParser;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

/**
 * Like {@link ModelCustomRenderer}, this model renders 
 */
public class ModelOBJRenderer extends ModelCustomRenderer
{
    /**
     * Mesh containing the data about the model
     */
    public OBJParser.Mesh mesh;

    public ModelOBJRenderer(ModelBase model, Model.Limb limb, Model.Transform transform, OBJParser.Mesh mesh)
    {
        super(model, limb, transform);

        this.mesh = mesh;
    }

    /**
     * Instead of generating plain cube, this model renderer will generate mesh which was read by the 
     * {@link OBJParser}.
     */
    @Override
    protected void compileDisplayList(float scale)
    {
        if (this.mesh != null)
        {
            this.displayList = GLAllocation.generateDisplayLists(1);
            GlStateManager.glNewList(this.displayList, 4864);
            BufferBuilder renderer = Tessellator.getInstance().getBuffer();

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
            this.compiled = true;

            /* I hope this will get garbage collected xD */
            this.mesh = null;
        }
        else
        {
            super.compileDisplayList(scale);
        }
    }
}