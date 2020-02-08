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
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
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
    public static final byte TOP_BIT = 0b1;
    public static final byte BOTTOM_BIT = 0b10;
    public static final byte FRONT_BIT = 0b100;
    public static final byte BACK_BIT = 0b1000;
    public static final byte LEFT_BIT = 0b10000;
    public static final byte RIGHT_BIT = 0b100000;

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
            // GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            GL11.glCallList(id);
            // GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
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

            if (chunk.stats > 0)
            {
                id = generateDisplayList(chunk, renderer);
            }
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
                    chunk.setBlockBit(i, h - 1, j, TOP_BIT);
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
                    chunk.setBlockBit(i, 0, j, BOTTOM_BIT);
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
                    chunk.setBlockBit(i, h - j - 1, d - 1, FRONT_BIT);
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
                    chunk.setBlockBit(w - i - 1, h - j - 1, 0, BACK_BIT);
                }
            }
        }

        /* Left & right */
        x = offsetX * stepX;
        y = (offsetY + d) * stepY;

        for (int i = 0; i < d; i++)
        {
            for (int j = 0; j < h; j++)
            {
                int alpha = image.getRGB(x + i * stepX, y + j * stepY) >> 24 & 0xff;

                if (alpha >= threshold)
                {
                    chunk.setBlockBit(0, h - j - 1, i, LEFT_BIT);
                }
            }
        }

        x = (offsetX + d + w) * stepX;
        y = (offsetY + d) * stepY;

        for (int i = 0; i < d; i++)
        {
            for (int j = 0; j < h; j++)
            {
                int xx = x + i * stepX;
                int yy = y + j * stepY;

                int color = image.getRGB(xx, yy);
                int alpha = color >> 24 & 0xff;
                String c = Integer.toHexString(color);

                if (alpha >= threshold)
                {
                    chunk.setBlockBit(w - 1, h - j - 1, d - i - 1, RIGHT_BIT);
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
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        int w = renderer.limb.size[0];
        int h = renderer.limb.size[1];
        int d = renderer.limb.size[2];

        float tw = renderer.textureWidth;
        float th = renderer.textureHeight;
        int offsetX = renderer.limb.texture[0];
        int offsetY = renderer.limb.texture[1];
        boolean mirror = renderer.limb.mirror;
        Offset off = new Offset(0, 0);
        Offset offmax = new Offset(0, 0);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

        for (int x = 0; x < chunk.w; x++)
        {
            for (int y = 0; y < chunk.h; y++)
            {
                for (int z = 0; z < chunk.d; z++)
                {
                    int blockX = mirror ? w - x - 1 : x;
                    byte block = chunk.getBlock(blockX, y, z);

                    if (block == 0)
                    {
                        continue;
                    }

                    float f = 1F / 16F;

                    float aX = -renderer.limb.anchor[0] * w + w;
                    float aY = -renderer.limb.anchor[1] * h + h;
                    float aZ = -renderer.limb.anchor[2] * d + d;

                    /* Minimum and maximum */
                    float mnx = (x - aX + (mirror ? 1 : 0)) * f;
                    float mmx = (x - aX + (mirror ? 0 : 1)) * f;

                    /* Top & Bottom */
                    if (!chunk.hasBlock(blockX, y + 1, z))
                    {
                        if (!calculateOffset(off, offmax, (byte) (block & TOP_BIT), offsetX, offsetY, w, h, d, blockX, y, z, tw, th))
                        {
                            calculateOffset(off, offmax, block, offsetX, offsetY, w, h, d, blockX, y, z, tw, th);
                        }

                        buffer.pos(mnx, -(y - aY + 1) * f, -(z - aZ) * f).tex(off.x, off.y).normal(0, -1, 0).endVertex();
                        buffer.pos(mmx, -(y - aY + 1) * f, -(z - aZ) * f).tex(offmax.x, off.y).normal(0, -1, 0).endVertex();
                        buffer.pos(mmx, -(y - aY + 1) * f, -(z - aZ + 1) * f).tex(offmax.x, offmax.y).normal(0, -1, 0).endVertex();
                        buffer.pos(mnx, -(y - aY + 1) * f, -(z - aZ + 1) * f).tex(off.x, offmax.y).normal(0, -1, 0).endVertex();
                    }

                    if (!chunk.hasBlock(blockX, y - 1, z))
                    {
                        if (!calculateOffset(off, offmax, (byte) (block & BOTTOM_BIT), offsetX, offsetY, w, h, d, blockX, y, z, tw, th))
                        {
                            calculateOffset(off, offmax, block, offsetX, offsetY, w, h, d, blockX, y, z, tw, th);
                        }

                        buffer.pos(mnx, -(y - aY) * f, -(z - aZ) * f).tex(off.x, off.y).normal(0, 1, 0).endVertex();
                        buffer.pos(mmx, -(y - aY) * f, -(z - aZ) * f).tex(offmax.x, off.y).normal(0, 1, 0).endVertex();
                        buffer.pos(mmx, -(y - aY) * f, -(z - aZ + 1) * f).tex(offmax.x, offmax.y).normal(0, 1, 0).endVertex();
                        buffer.pos(mnx, -(y - aY) * f, -(z - aZ + 1) * f).tex(off.x, offmax.y).normal(0, 1, 0).endVertex();
                    }

                    /* Front & back */
                    if (!chunk.hasBlock(blockX, y, z + 1))
                    {
                        if (!calculateOffset(off, offmax, (byte) (block & FRONT_BIT), offsetX, offsetY, w, h, d, blockX, y, z, tw, th))
                        {
                            calculateOffset(off, offmax, block, offsetX, offsetY, w, h, d, blockX, y, z, tw, th);
                        }

                        buffer.pos(mnx, -(y - aY + 1) * f, -(z - aZ + 1) * f).tex(off.x, off.y).normal(0, 0, -1).endVertex();
                        buffer.pos(mmx, -(y - aY + 1) * f, -(z - aZ + 1) * f).tex(offmax.x, off.y).normal(0, 0, -1).endVertex();
                        buffer.pos(mmx, -(y - aY) * f, -(z - aZ + 1) * f).tex(offmax.x, offmax.y).normal(0, 0, -1).endVertex();
                        buffer.pos(mnx, -(y - aY) * f, -(z - aZ + 1) * f).tex(off.x, offmax.y).normal(0, 0, -1).endVertex();
                    }

                    if (!chunk.hasBlock(blockX, y, z - 1))
                    {
                        if (!calculateOffset(off, offmax, (byte) (block & BACK_BIT), offsetX, offsetY, w, h, d, blockX, y, z, tw, th))
                        {
                            calculateOffset(off, offmax, block, offsetX, offsetY, w, h, d, blockX, y, z, tw, th);
                        }

                        buffer.pos(mnx, -(y - aY + 1) * f, -(z - aZ) * f).tex(offmax.x, off.y).normal(0, 0, 1).endVertex();
                        buffer.pos(mmx, -(y - aY + 1) * f, -(z - aZ) * f).tex(off.x, off.y).normal(0, 0, 1).endVertex();
                        buffer.pos(mmx, -(y - aY) * f, -(z - aZ) * f).tex(off.x, offmax.y).normal(0, 0, 1).endVertex();
                        buffer.pos(mnx, -(y - aY) * f, -(z - aZ) * f).tex(offmax.x, offmax.y).normal(0, 0, 1).endVertex();
                    }

                    /* Left & Right */
                    if (!chunk.hasBlock(blockX + 1, y, z))
                    {
                        if (!calculateOffset(off, offmax, (byte) (block & RIGHT_BIT), offsetX, offsetY, w, h, d, blockX, y, z, tw, th))
                        {
                            calculateOffset(off, offmax, block, offsetX, offsetY, w, h, d, blockX, y, z, tw, th);
                        }

                        buffer.pos(mmx, -(y - aY + 1) * f, -(z - aZ) * f).tex(offmax.x, off.y).normal(1, 0, 0).endVertex();
                        buffer.pos(mmx, -(y - aY + 1) * f, -(z - aZ + 1) * f).tex(off.x, off.y).normal(1, 0, 0).endVertex();
                        buffer.pos(mmx, -(y - aY) * f, -(z - aZ + 1) * f).tex(off.x, offmax.y).normal(1, 0, 0).endVertex();
                        buffer.pos(mmx, -(y - aY) * f, -(z - aZ) * f).tex(offmax.x, offmax.y).normal(1, 0, 0).endVertex();
                    }

                    if (!chunk.hasBlock(blockX - 1, y, z))
                    {
                        if (!calculateOffset(off, offmax, (byte) (block & LEFT_BIT), offsetX, offsetY, w, h, d, blockX, y, z, tw, th))
                        {
                            calculateOffset(off, offmax, block, offsetX, offsetY, w, h, d, blockX, y, z, tw, th);
                        }

                        buffer.pos(mnx, -(y - aY + 1) * f, -(z - aZ) * f).tex(off.x, off.y).normal(-1, 0, 0).endVertex();
                        buffer.pos(mnx, -(y - aY + 1) * f, -(z - aZ + 1) * f).tex(offmax.x, off.y).normal(-1, 0, 0).endVertex();
                        buffer.pos(mnx, -(y - aY) * f, -(z - aZ + 1) * f).tex(offmax.x, offmax.y).normal(-1, 0, 0).endVertex();
                        buffer.pos(mnx, -(y - aY) * f, -(z - aZ) * f).tex(off.x, offmax.y).normal(-1, 0, 0).endVertex();
                    }
                }
            }
        }

        Tessellator.getInstance().draw();
    }

    private static boolean calculateOffset(Offset offset, Offset max, byte block, int offsetX, int offsetY, int w, int h, int d, int x, int y, int z, float tw, float th)
    {
        /* Right */
        float offX = -1;
        float offY = -1;

        /* Top */
        if ((block & TOP_BIT) != 0)
        {
            offX = offsetX + d + x;
            offY = offsetY + z;
        }
        /* Bottom */
        else if ((block & BOTTOM_BIT) != 0)
        {
            offX = offsetX + d + w + x;
            offY = offsetY + z;
        }
        /* Front */
        else if ((block & FRONT_BIT) != 0)
        {
            offX = offsetX + d + x;
            offY = offsetY + d + h - y - 1;
        }
        /* Back */
        else if ((block & BACK_BIT) != 0)
        {
            offX = offsetX + d * 2 + w * 2 - x - 1;
            offY = offsetY + d + h - y - 1;
        }
        /* Left */
        else if ((block & LEFT_BIT) != 0)
        {
            offX = offsetX + z;
            offY = offsetY + d + h - y - 1;
        }
        else if ((block & RIGHT_BIT) != 0)
        {
            offX = offsetX + d + w + d - z - 1;
            offY = offsetY + d + h - y - 1;
        }

        if (offX == -1 && offY == -1)
        {
            return false;
        }

        float offMX = (offX + 1) / tw;
        float offMY = (offY + 1) / th;
        offX /= tw;
        offY /= th;

        offset.set(offX, offY);
        max.set(offMX, offMY);

        return true;
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

        public int stats;

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

            byte old = this.data[x + y * this.w + z * this.w * this.h];

            this.data[x + y * this.w + z * this.w * this.h] = block;

            if (block != old)
            {
                this.stats += (block == 0) ? -1 : 1;
            }
        }

        /**
         * Set block at given coordinates
         */
        public void setBlockBit(int x, int y, int z, byte bit)
        {
            if (x < 0 || y < 0 || z < 0 || x >= this.w || y >= this.h || z >= this.d)
            {
                return;
            }

            byte old = this.data[x + y * this.w + z * this.w * this.h];
            byte block = (byte) (old | bit);

            this.data[x + y * this.w + z * this.w * this.h] = block;

            if (block != old)
            {
                this.stats += (block == 0) ? -1 : 1;
            }
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

    public static class Offset {
        public float x;
        public float y;

        public Offset(float x, float y)
        {
            this.set(x, y);
        }

        public void set(float x, float y)
        {
            this.x = x;
            this.y = y;
        }
    }
}