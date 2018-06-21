package mchorse.blockbuster.client.model;

import java.util.Map;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.MipmapTexture;
import mchorse.blockbuster.client.model.parsing.obj.OBJMaterial;
import mchorse.blockbuster.client.model.parsing.obj.OBJParser;
import mchorse.blockbuster.client.model.parsing.obj.OBJParser.Mesh;
import mchorse.blockbuster.client.render.RenderCustomModel;
import mchorse.blockbuster.commands.model.SubCommandModelClear;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

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
            BufferBuilder renderer = Tessellator.getInstance().getBuffer();

            this.displayLists = new OBJDisplayList[this.mesh.meshes.size()];
            int index = 0;

            for (Mesh mesh : this.mesh.meshes)
            {
                OBJMaterial material = mesh.material;

                if (material != null && material.useTexture && material.texture != null)
                {
                    this.setupTexture(material);
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
     * Manually replace/setup a mipmapped texture 
     */
    private void setupTexture(OBJMaterial material)
    {
        TextureManager manager = Minecraft.getMinecraft().renderEngine;
        ITextureObject texture = manager.getTexture(material.texture);
        Map<ResourceLocation, ITextureObject> map = SubCommandModelClear.getTextures(manager);

        if (texture != null && !(texture instanceof MipmapTexture))
        {
            GlStateManager.deleteTexture(map.remove(material.texture).getGlTextureId());
            texture = null;
        }

        if (texture == null)
        {
            try
            {
                /* Load texture manually */
                texture = new MipmapTexture(material.texture);
                texture.loadTexture(Minecraft.getMinecraft().getResourceManager());

                map.put(material.texture, texture);
            }
            catch (Exception e)
            {
                System.err.println("An error occurred during loading manually a mipmap'd texture '" + material.texture + "'");
                e.printStackTrace();
            }
        }

        boolean loaded = texture instanceof MipmapTexture;
        manager.bindTexture(material.texture);

        int mod = material.linear ? (loaded ? GL11.GL_LINEAR_MIPMAP_LINEAR : GL11.GL_LINEAR) : (loaded ? GL11.GL_NEAREST_MIPMAP_LINEAR : GL11.GL_NEAREST);
        int mag = material.linear ? GL11.GL_LINEAR : GL11.GL_NEAREST;

        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mod);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mag);
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

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

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
                try
                {
                    Minecraft.getMinecraft().renderEngine.bindTexture(RenderCustomModel.lastTexture);
                }
                catch (Exception e)
                {
                    System.out.println(RenderCustomModel.lastTexture);
                }
            }

            if (hasColor)
            {
                GlStateManager.enableTexture2D();
            }

            GlStateManager.disableBlend();
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