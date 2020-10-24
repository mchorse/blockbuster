package mchorse.blockbuster.client.model;

import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.api.formats.obj.MeshOBJ;
import mchorse.blockbuster.api.formats.obj.MeshesOBJ;
import mchorse.blockbuster.api.formats.obj.OBJMaterial;
import mchorse.blockbuster.api.formats.obj.OBJParser;
import mchorse.blockbuster.api.formats.obj.ShapeKey;
import mchorse.blockbuster.client.render.RenderCustomModel;
import mchorse.blockbuster.client.textures.GifTexture;
import mchorse.blockbuster.client.textures.MipmapTexture;
import mchorse.mclib.utils.Interpolations;
import mchorse.mclib.utils.ReflectionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * Like {@link ModelCustomRenderer}, this model renders 
 */
public class ModelOBJRenderer extends ModelCustomRenderer
{
    /**
     * Mesh containing the data about the model
     */
    public MeshesOBJ mesh;

    /**
     * Display lists 
     */
    public OBJDisplayList[] displayLists;

    /**
     * Custom materials texture 
     */
    public Map<String, ResourceLocation> materials;

    /**
     * Shape configurations
     */
    public List<ShapeKey> shapes;

    /**
     * Solid colored texture ID 
     */
    protected int solidColorTex = -1;

    public ModelOBJRenderer(ModelCustom model, ModelLimb limb, ModelTransform transform, MeshesOBJ mesh)
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
            this.displayLists = new OBJDisplayList[this.mesh.meshes.size()];
            int index = 0;
            int texture = 0;
            int count = 0;

            /* Generate a texture based on solid colored materials */
            for (MeshOBJ mesh : this.mesh.meshes)
            {
                count += mesh.material != null && !mesh.material.useTexture ? 1 : 0;
            }

            if (count > 0)
            {
                ByteBuffer buffer = GLAllocation.createDirectByteBuffer(count * 4);
                texture = GL11.glGenTextures();

                for (MeshOBJ mesh : this.mesh.meshes)
                {
                    if (mesh.material != null && !mesh.material.useTexture)
                    {
                        buffer.put((byte) (mesh.material.r * 255));
                        buffer.put((byte) (mesh.material.g * 255));
                        buffer.put((byte) (mesh.material.b * 255));
                        buffer.put((byte) 255);
                    }
                }

                buffer.flip();

                /* For some reason, if there is no glTexParameter calls
                 * the texture becomes pure white */
                GlStateManager.bindTexture(texture);
                GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, count, 1, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
            }

            /* Generate display lists */
            int j = 0;
            int k = 0;

            for (MeshOBJ mesh : this.mesh.meshes)
            {
                OBJMaterial material = mesh.material;

                if (material != null && material.useTexture && material.texture != null)
                {
                    this.setupTexture(material);
                }

                int id = GLAllocation.generateDisplayLists(1);
                boolean hasColor = material != null && !mesh.material.useTexture;
                BufferBuilder renderer = Tessellator.getInstance().getBuffer();

                GlStateManager.glNewList(id, GL11.GL_COMPILE);
                renderer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
                float texF = (j + 0.5F) / count;

                for (int i = 0, c = mesh.triangles; i < c; i++)
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
                        renderer.pos(x, y, z).tex(texF, 0.5F).normal(nx, ny, nz).endVertex();
                    }
                    else
                    {
                        renderer.pos(x, y, z).tex(u, v).normal(nx, ny, nz).endVertex();
                    }
                }

                Tessellator.getInstance().draw();
                GlStateManager.glEndList();

                this.displayLists[index++] = new OBJDisplayList(k, id, texture, mesh, this.mesh.shapes == null);
                j += hasColor ? 1 : 0;
                k += 1;
            }

            this.compiled = true;
            this.solidColorTex = texture;

            /* Discard the mesh ONLY if there are no shapes */
            if (this.mesh.shapes == null)
            {
                this.mesh = null;
            }
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
        Map<ResourceLocation, ITextureObject> map = ReflectionUtils.getTextures(manager);

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

            if (hasColor)
            {
                GlStateManager.bindTexture(list.texId);
            }

            if (hasTexture && list.material.texture != null)
            {
                ResourceLocation texture = list.material.texture;

                if (this.materials != null && this.materials.containsKey(list.material.name))
                {
                    texture = this.materials.get(list.material.name);
                }

                GifTexture.bindTexture(texture, RenderCustomModel.tick);
            }

            list.render(this);

            if (hasColor || (hasTexture && list.material.texture != null))
            {
                RenderCustomModel.bindLastTexture();
            }
        }
    }

    @Override
    public void delete()
    {
        super.delete();

        if (this.displayLists != null)
        {
            for (OBJDisplayList list : this.displayLists)
            {
                if (list.id != -1)
                {
                    GL11.glDeleteLists(list.id, 1);
                }
            }
        }

        if (this.solidColorTex != -1)
        {
            GL11.glDeleteTextures(this.solidColorTex);
        }
    }

    public static class OBJDisplayList
    {
        public int index;
        public int id;
        public int texId;
        public OBJMaterial material;

        private MeshOBJ mesh;
        private MeshOBJ temporary;

        public OBJDisplayList(int index, int id, int texId, MeshOBJ mesh, boolean discard)
        {
            this.index = index;
            this.id = id;
            this.texId = texId;
            this.material = mesh.material;

            if (!discard)
            {
                this.mesh = mesh;
                this.temporary = new MeshOBJ(new float[mesh.posData.length], new float[mesh.texData.length], new float[mesh.normData.length]);
            }
        }

        public void render(ModelOBJRenderer renderer)
        {
            if (renderer.shapes != null && this.mesh != null)
            {
                BufferBuilder builder = Tessellator.getInstance().getBuffer();

                builder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
                // float texF = (j + 0.5F) / count;

                for (int i = 0, c = this.mesh.triangles; i < c; i++)
                {
                    this.temporary.posData[i * 3] = this.mesh.posData[i * 3];
                    this.temporary.posData[i * 3 + 1] = this.mesh.posData[i * 3 + 1];
                    this.temporary.posData[i * 3 + 2] = this.mesh.posData[i * 3 + 2];
                    this.temporary.texData[i * 2] = this.mesh.texData[i * 2];
                    this.temporary.texData[i * 2 + 1] = this.mesh.texData[i * 2 + 1];
                    this.temporary.normData[i * 3] = this.mesh.normData[i * 3];
                    this.temporary.normData[i * 3 + 1] = this.mesh.normData[i * 3 + 1];
                    this.temporary.normData[i * 3 + 2] = this.mesh.normData[i * 3 + 2];
                }

                for (ShapeKey key : renderer.shapes)
                {
                    List<MeshOBJ> list = renderer.mesh.shapes.get(key.name);

                    if (list == null)
                    {
                        continue;
                    }

                    MeshOBJ mesh = list.get(this.index);
                    float factor = key.value;

                    if (mesh == null || this.temporary.triangles != mesh.triangles)
                    {
                        continue;
                    }

                    for (int i = 0, c = this.temporary.triangles; i < c; i++)
                    {
                        this.temporary.posData[i * 3] = Interpolations.lerp(this.temporary.posData[i * 3], mesh.posData[i * 3], factor);
                        this.temporary.posData[i * 3 + 1] = Interpolations.lerp(this.temporary.posData[i * 3 + 1], mesh.posData[i * 3 + 1], factor);
                        this.temporary.posData[i * 3 + 2] = Interpolations.lerp(this.temporary.posData[i * 3 + 2], mesh.posData[i * 3 + 2], factor);
                        this.temporary.texData[i * 2] = Interpolations.lerp(this.temporary.texData[i * 2], mesh.texData[i * 2], factor);
                        this.temporary.texData[i * 2 + 1] = Interpolations.lerp(this.temporary.texData[i * 2 + 1], mesh.texData[i * 2 + 1], factor);
                        this.temporary.normData[i * 3] = Interpolations.lerp(this.temporary.normData[i * 3], mesh.normData[i * 3], factor);
                        this.temporary.normData[i * 3 + 1] = Interpolations.lerp(this.temporary.normData[i * 3 + 1], mesh.normData[i * 3 + 1], factor);
                        this.temporary.normData[i * 3 + 2] = Interpolations.lerp(this.temporary.normData[i * 3 + 2], mesh.normData[i * 3 + 2], factor);
                    }
                }

                for (int i = 0, c = this.temporary.triangles; i < c; i++)
                {
                    float x = this.temporary.posData[i * 3] - renderer.limb.origin[0];
                    float y = -this.temporary.posData[i * 3 + 1] + renderer.limb.origin[1];
                    float z = this.temporary.posData[i * 3 + 2] - renderer.limb.origin[2];

                    float u = this.temporary.texData[i * 2];
                    float v = this.temporary.texData[i * 2 + 1];

                    float nx = this.temporary.normData[i * 3];
                    float ny = -this.temporary.normData[i * 3 + 1];
                    float nz = this.temporary.normData[i * 3 + 2];

                    if (false)
                    {
                        // renderer.pos(x, y, z).tex(texF, 0.5F).normal(nx, ny, nz).endVertex();
                    }
                    else
                    {
                        builder.pos(x, y, z).tex(u, v).normal(nx, ny, nz).endVertex();
                    }
                }

                Tessellator.getInstance().draw();
            }
            else
            {
                GL11.glCallList(this.id);
            }
        }
    }
}