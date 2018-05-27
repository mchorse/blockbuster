package mchorse.blockbuster.client.model;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.model.parsing.obj.OBJMaterial;
import mchorse.blockbuster.client.model.parsing.obj.OBJParser;
import mchorse.blockbuster.client.model.parsing.obj.OBJParser.Mesh;
import mchorse.blockbuster.client.render.RenderCustomModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;

/**
 * Like {@link ModelCustomRenderer}, this model renders 
 */
public class ModelOBJRenderer extends ModelCustomRenderer
{
    public static final VertexFormat POSITION_COLOR_NORMAL = new VertexFormat();

    /**
     * Mesh containing the data about the model
     */
    public OBJParser.MeshObject mesh;

    public OBJDisplayList[] displayLists;

    static
    {
        POSITION_COLOR_NORMAL.addElement(DefaultVertexFormats.POSITION_3F);
        POSITION_COLOR_NORMAL.addElement(DefaultVertexFormats.COLOR_4UB);
        POSITION_COLOR_NORMAL.addElement(DefaultVertexFormats.NORMAL_3B);
        POSITION_COLOR_NORMAL.addElement(DefaultVertexFormats.PADDING_1B);
    }

    public ModelOBJRenderer(ModelBase model, Model.Limb limb, Model.Transform transform, OBJParser.MeshObject mesh)
    {
        super(model, limb, transform);

        this.mesh = mesh;
        this.compiled = false;
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

            this.displayLists = new OBJDisplayList[this.mesh.meshes.size()];
            int index = 0;

            for (Mesh mesh : this.mesh.meshes)
            {
                OBJMaterial material = mesh.material;

                if (material != null && material.useTexture && material.texture != null)
                {
                    Minecraft.getMinecraft().renderEngine.bindTexture(material.texture);

                    int mod = material.linear ? GL11.GL_LINEAR_MIPMAP_LINEAR : GL11.GL_NEAREST;

                    GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mod);
                    GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mod);
                }

                int id = GLAllocation.generateDisplayLists(1);
                boolean hasColor = material != null && !mesh.material.useTexture;

                VertexFormat format = hasColor ? POSITION_COLOR_NORMAL : DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL;

                GlStateManager.glNewList(id, 4864);
                renderer.begin(GL11.GL_TRIANGLES, format);

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

                    if (hasColor)
                    {
                        renderer.pos(x, y, z).color(material.r, material.g, material.b, 1).normal(nx, ny, nz).endVertex();
                    }
                    else
                    {
                        renderer.pos(x, y, z).tex(u, v).normal(nx, ny, nz).endVertex();
                    }
                }

                Tessellator.getInstance().draw();
                GlStateManager.glEndList();

                this.displayLists[index++] = new OBJDisplayList(id, mesh.material);
            }

            /* I hope this will get garbage collected xD */
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
        for (OBJDisplayList list : this.displayLists)
        {
            boolean hasColor = list.material != null && !list.material.useTexture;
            boolean hasTexture = list.material != null && list.material.useTexture;

            if (hasColor)
            {
                GlStateManager.disableTexture2D();
            }

            if (hasTexture && list.material.texture != null)
            {
                Minecraft.getMinecraft().renderEngine.bindTexture(list.material.texture);
            }

            GL11.glCallList(list.id);

            if (hasTexture && RenderCustomModel.lastTexture != null)
            {
                Minecraft.getMinecraft().renderEngine.bindTexture(RenderCustomModel.lastTexture);
            }

            if (hasColor)
            {
                GlStateManager.enableTexture2D();
            }
        }
    }

    public static class OBJDisplayList
    {
        public int id;
        public OBJMaterial material;

        public OBJDisplayList(int id, OBJMaterial material)
        {
            this.id = id;
            this.material = material;
        }
    }
}