package mchorse.blockbuster.client.model.parsing;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

/**
 * Model extruded layer class
 * 
 * This baby is responsible for making wonders. Basically, it allows 
 * to render extruded layers based on the texture. 
 * 
 * TODO: Add mirror support 
 * TODO: Clean up and make sure the code isn't confusing (axes and 
 * coordinates)
 */
public class ModelExtrudedLayer
{
    /**
     * Storage for extruded layers 
     */
    protected static Map<ModelCustomRenderer, Map<ResourceLocation, Integer>> layers = new HashMap<>();

    /**
     * Cached textures 
     */
    protected static Map<ResourceLocation, CachedImage> images = new HashMap<>();

    /**
     * Render the extruded 3D layer. If extruded layer wasn't generated 
     * before, it will generate it.
     */
    public static void render3DLayer(ModelCustomRenderer renderer, ResourceLocation texture)
    {
        Map<ResourceLocation, Integer> map = layers.get(renderer);
        int id = -1;

        if (map == null)
        {
            map = new HashMap<>();
            layers.put(renderer, map);
        }

        if (map != null)
        {
            if (!map.containsKey(texture))
            {
                generateLayer(renderer, texture, map);
            }

            Integer callId = map.get(texture);

            if (callId != null)
            {
                id = callId.intValue();
            }
        }

        if (id != -1)
        {
            // GlStateManager.disableTexture2D();
            // GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

            GL11.glCallList(id);

            // GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            // GlStateManager.enableTexture2D();
        }

        /* Clean up cache */
        if (!images.isEmpty())
        {
            Iterator<CachedImage> it = images.values().iterator();

            while (it.hasNext())
            {
                CachedImage image = it.next();

                if (image.timer <= 0)
                {
                    image.image.flush();
                    it.remove();
                }

                image.timer -= 1;
            }
        }
    }

    /**
     * Generate extruded layer call list for given limb renderer and 
     * texture location
     */
    private static void generateLayer(ModelCustomRenderer renderer, ResourceLocation texture, Map<ResourceLocation, Integer> map)
    {
        int id = -1;

        try
        {
            CachedImage image = images.get(texture);

            if (image == null)
            {
                image = new CachedImage(ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(texture).getInputStream()));
                images.put(texture, image);
            }

            ModelLimb limb = renderer.limb;
            Chunk chunk = new Chunk(limb.size[0], limb.size[1], limb.size[2]);

            fillChunk(chunk, image.image, renderer);
            id = generateDisplayList(chunk, renderer);
        }
        catch (Exception e)
        {
            System.err.println("An error occurred during construction of extruded 3D layer for texture " + texture + " and limb " + renderer.limb.name);
            e.printStackTrace();
        }

        map.put(texture, id);
    }

    /**
     * Fill chunk based on given texture
     * 
     * Basically, this method is responsible for filling the chunk data
     * with outer voxels. The voxels are getting filled based on the 
     * limb's offset and Minecraft's cube mapping.
     * 
     * There is a lot of copy-paste code since I'm not sure 
     */
    private static void fillChunk(Chunk chunk, BufferedImage image, ModelCustomRenderer renderer)
    {
        final int threshold = 0x80;

        int stepX = (int) (image.getWidth() / renderer.textureWidth);
        int stepY = (int) (image.getHeight() / renderer.textureHeight);

        int w = renderer.limb.size[0];
        int h = renderer.limb.size[1];
        int d = renderer.limb.size[2];
        int offsetX = renderer.limb.texture[0];
        int offsetY = renderer.limb.texture[1];

        /* Top & bottom */
        int x = (offsetX + d) * stepX;
        int y = offsetY * stepY;

        for (int i = 0; i < w; i++)
        {
            for (int j = 0; j < d; j++)
            {
                int alpha = image.getRGB(x + i * stepX, y + j * stepY) >> 24 & 0xff;

                if (alpha >= threshold)
                {
                    chunk.setBlock(i, h - 1, j, (byte) 1);
                }
            }
        }

        x = (offsetX + d + w) * stepX;
        y = offsetY * stepY;

        for (int i = 0; i < w; i++)
        {
            for (int j = 0; j < d; j++)
            {
                int alpha = image.getRGB(x + i * stepX, y + j * stepY) >> 24 & 0xff;

                if (alpha >= threshold)
                {
                    chunk.setBlock(i, 0, j, (byte) 2);
                }
            }
        }

        /* Front & back */
        x = (offsetX + d) * stepX;
        y = (offsetY + d) * stepY;

        for (int i = 0; i < w; i++)
        {
            for (int j = 0; j < h; j++)
            {
                int alpha = image.getRGB(x + i * stepX, y + j * stepY) >> 24 & 0xff;

                if (alpha >= threshold)
                {
                    chunk.setBlock(i, h - j - 1, d - 1, (byte) 3);
                }
            }
        }

        x = (offsetX + d * 2 + w) * stepX;
        y = (offsetY + d) * stepY;

        for (int i = 0; i < w; i++)
        {
            for (int j = 0; j < h; j++)
            {
                int alpha = image.getRGB(x + i * stepX, y + j * stepY) >> 24 & 0xff;

                if (alpha >= threshold)
                {
                    chunk.setBlock(w - i - 1, h - j - 1, 0, (byte) 4);
                }
            }
        }

        /* Left & right */
        x = offsetX * stepX;
        y = (offsetY + d) * stepY;

        for (int i = 0; i < w; i++)
        {
            for (int j = 0; j < h; j++)
            {
                int alpha = image.getRGB(x + i * stepX, y + j * stepY) >> 24 & 0xff;

                if (alpha >= threshold)
                {
                    chunk.setBlock(0, h - j - 1, i, (byte) 5);
                }
            }
        }

        x = (offsetX + d + w) * stepX;
        y = (offsetY + d) * stepY;

        for (int i = 0; i < w; i++)
        {
            for (int j = 0; j < h; j++)
            {
                int alpha = image.getRGB(x + i * stepX, y + j * stepY) >> 24 & 0xff;

                if (alpha >= threshold)
                {
                    chunk.setBlock(w - 1, h - j - 1, d - i - 1, (byte) 6);
                }
            }
        }
    }

    /**
     * Generate display list out of chunk 
     */
    private static int generateDisplayList(Chunk chunk, ModelCustomRenderer renderer)
    {
        int id = GLAllocation.generateDisplayLists(1);
        GlStateManager.glNewList(id, 4864);
        generateGeometry(chunk, renderer);
        GlStateManager.glEndList();

        return id;
    }

    /**
     * Generate geometry based on given chunk. This method is basically 
     * using stupid (instead of greedy) voxel meshing in order to 
     * compile the geometry.
     * 
     * TODO: Add support for byte flag voxels, i.e. multiside
     * TODO: Fix UV mapping on non primary sides to match the edges (for
     * high-res textures, primarily)
     */
    private static void generateGeometry(Chunk chunk, ModelCustomRenderer renderer)
    {
        VertexBuffer buffer = Tessellator.getInstance().getBuffer();

        int w = renderer.limb.size[0];
        int h = renderer.limb.size[1];
        int d = renderer.limb.size[2];

        float tw = renderer.textureWidth;
        float th = renderer.textureHeight;
        int offsetX = renderer.limb.texture[0];
        int offsetY = renderer.limb.texture[1];

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

        for (int x = 0; x < chunk.w; x++)
        {
            for (int y = 0; y < chunk.h; y++)
            {
                for (int z = 0; z < chunk.d; z++)
                {
                    byte block = chunk.getBlock(x, y, z);

                    if (block == 0)
                    {
                        continue;
                    }

                    float f = 1F / 16F;

                    /* Right */
                    float offX = offsetX + d + w + d - z - 1;
                    float offY = offsetY + d + h - y - 1;

                    /* Top */
                    if (block == 1)
                    {
                        offX = offsetX + d + x;
                        offY = offsetY + z;
                    }
                    /* Bottom */
                    else if (block == 2)
                    {
                        offX = offsetX + d + w + x;
                        offY = offsetY + z;
                    }
                    /* Front */
                    else if (block == 3)
                    {
                        offX = offsetX + d + x;
                        offY = offsetY + d + h - y - 1;
                    }
                    /* Back */
                    else if (block == 4)
                    {
                        offX = offsetX + d * 2 + w * 2 - x - 1;
                        offY = offsetY + d + h - y - 1;
                    }
                    /* Left */
                    else if (block == 5)
                    {
                        offX = offsetX + z;
                        offY = offsetY + d + h - y - 1;
                    }

                    float offMX = (offX + 1) / tw;
                    float offMY = (offY + 1) / th;
                    offX /= tw;
                    offY /= th;

                    float aX = -renderer.limb.anchor[0] * w + w;
                    float aY = -renderer.limb.anchor[1] * h + h;
                    float aZ = -renderer.limb.anchor[2] * d + d;

                    /* Top & Bottom */
                    if (!chunk.hasBlock(x, y + 1, z))
                    {
                        buffer.pos((x - aX) * f, -(y - aY + 1) * f, -(z - aZ) * f).tex(offX, offY).normal(0, -1, 0).endVertex();
                        buffer.pos((x - aX + 1) * f, -(y - aY + 1) * f, -(z - aZ) * f).tex(offMX, offY).normal(0, -1, 0).endVertex();
                        buffer.pos((x - aX + 1) * f, -(y - aY + 1) * f, -(z - aZ + 1) * f).tex(offMX, offMY).normal(0, -1, 0).endVertex();
                        buffer.pos((x - aX) * f, -(y - aY + 1) * f, -(z - aZ + 1) * f).tex(offX, offMY).normal(0, -1, 0).endVertex();
                    }

                    if (!chunk.hasBlock(x, y - 1, z))
                    {
                        buffer.pos((x - aX) * f, -(y - aY) * f, -(z - aZ) * f).tex(offX, offY).normal(0, 1, 0).endVertex();
                        buffer.pos((x - aX + 1) * f, -(y - aY) * f, -(z - aZ) * f).tex(offMX, offY).normal(0, 1, 0).endVertex();
                        buffer.pos((x - aX + 1) * f, -(y - aY) * f, -(z - aZ + 1) * f).tex(offMX, offMY).normal(0, 1, 0).endVertex();
                        buffer.pos((x - aX) * f, -(y - aY) * f, -(z - aZ + 1) * f).tex(offX, offMY).normal(0, 1, 0).endVertex();
                    }

                    /* Front & back */
                    if (!chunk.hasBlock(x, y, z + 1))
                    {
                        buffer.pos((x - aX) * f, -(y - aY + 1) * f, -(z - aZ + 1) * f).tex(offX, offY).normal(0, 0, -1).endVertex();
                        buffer.pos((x - aX + 1) * f, -(y - aY + 1) * f, -(z - aZ + 1) * f).tex(offMX, offY).normal(0, 0, -1).endVertex();
                        buffer.pos((x - aX + 1) * f, -(y - aY) * f, -(z - aZ + 1) * f).tex(offMX, offMY).normal(0, 0, -1).endVertex();
                        buffer.pos((x - aX) * f, -(y - aY) * f, -(z - aZ + 1) * f).tex(offX, offMY).normal(0, 0, -1).endVertex();
                    }

                    if (!chunk.hasBlock(x, y, z - 1))
                    {
                        buffer.pos((x - aX) * f, -(y - aY + 1) * f, -(z - aZ) * f).tex(offMX, offY).normal(0, 0, 1).endVertex();
                        buffer.pos((x - aX + 1) * f, -(y - aY + 1) * f, -(z - aZ) * f).tex(offX, offY).normal(0, 0, 1).endVertex();
                        buffer.pos((x - aX + 1) * f, -(y - aY) * f, -(z - aZ) * f).tex(offX, offMY).normal(0, 0, 1).endVertex();
                        buffer.pos((x - aX) * f, -(y - aY) * f, -(z - aZ) * f).tex(offMX, offMY).normal(0, 0, 1).endVertex();
                    }

                    /* Left & Right */
                    if (!chunk.hasBlock(x + 1, y, z))
                    {
                        buffer.pos((x - aX + 1) * f, -(y - aY + 1) * f, -(z - aZ) * f).tex(offMX, offY).normal(-1, 0, 0).endVertex();
                        buffer.pos((x - aX + 1) * f, -(y - aY + 1) * f, -(z - aZ + 1) * f).tex(offX, offY).normal(-1, 0, 0).endVertex();
                        buffer.pos((x - aX + 1) * f, -(y - aY) * f, -(z - aZ + 1) * f).tex(offX, offMY).normal(-1, 0, 0).endVertex();
                        buffer.pos((x - aX + 1) * f, -(y - aY) * f, -(z - aZ) * f).tex(offMX, offMY).normal(-1, 0, 0).endVertex();
                    }

                    if (!chunk.hasBlock(x - 1, y, z))
                    {
                        buffer.pos((x - aX) * f, -(y - aY + 1) * f, -(z - aZ) * f).tex(offX, offY).normal(-1, 0, 0).endVertex();
                        buffer.pos((x - aX) * f, -(y - aY + 1) * f, -(z - aZ + 1) * f).tex(offMX, offY).normal(-1, 0, 0).endVertex();
                        buffer.pos((x - aX) * f, -(y - aY) * f, -(z - aZ + 1) * f).tex(offMX, offMY).normal(-1, 0, 0).endVertex();
                        buffer.pos((x - aX) * f, -(y - aY) * f, -(z - aZ) * f).tex(offX, offMY).normal(-1, 0, 0).endVertex();
                    }
                }
            }
        }

        Tessellator.getInstance().draw();
    }

    /**
     * Clean everything
     */
    public static void clear()
    {
        for (Map<ResourceLocation, Integer> map : layers.values())
        {
            for (Integer i : map.values())
            {
                GL11.glDeleteLists(i.intValue(), 1);
            }
        }

        layers.clear();
    }

    /**
     * Clean up layers by texture
     */
    public static void clearByTexture(ResourceLocation texture)
    {
        for (Map<ResourceLocation, Integer> map : layers.values())
        {
            if (map.containsKey(texture))
            {
                GL11.glDeleteLists(map.remove(texture).intValue(), 1);
            }
        }
    }

    /**
     * Clean up layers by model
     */
    public static void clearByModel(ModelCustom model)
    {
        if (model == null)
        {
            return;
        }

        for (ModelCustomRenderer renderer : model.limbs)
        {
            if (!renderer.limb.is3D)
            {
                continue;
            }

            Map<ResourceLocation, Integer> map = layers.remove(renderer);

            if (map == null)
            {
                continue;
            }

            for (Integer i : map.values())
            {
                GL11.glDeleteLists(i.intValue(), 1);
            }
        }
    }

    /**
     * Cached image class
     */
    public static class CachedImage
    {
        public BufferedImage image;
        public int timer = 10;

        public CachedImage(BufferedImage image)
        {
            this.image = image;
        }
    }

    /**
     * Chunk class
     * 
     * Another class I extracted from my unfinished voxel video game. 
     * This class represents a voxel chunk.
     */
    public static class Chunk
    {
        /**
         * Array of block data 
         */
        protected byte[] data;

        public final int w;
        public final int h;
        public final int d;

        /**
         * Initialize empty chunk data 
         */
        public Chunk(int w, int h, int d)
        {
            this.w = w;
            this.h = h;
            this.d = d;

            this.data = new byte[w * h * d];
        }

        /**
         * Set block at given coordinates
         */
        public void setBlock(int x, int y, int z, byte block)
        {
            if (x < 0 || y < 0 || z < 0 || x >= this.w || y >= this.h || z >= this.d)
            {
                return;
            }

            this.data[x + y * this.w + z * this.w * this.h] = block;
        }

        /**
         * Is this chunk has a block at given coordinates  
         */
        public boolean hasBlock(int x, int y, int z)
        {
            return this.getBlock(x, y, z) != 0;
        }

        /**
         * Get block at given coordinate 
         */
        public byte getBlock(int x, int y, int z)
        {
            if (x < 0 || y < 0 || z < 0 || x >= this.w || y >= this.h || z >= this.d)
            {
                return 0;
            }

            return this.data[x + y * this.w + z * this.w * this.h];
        }
    }
}