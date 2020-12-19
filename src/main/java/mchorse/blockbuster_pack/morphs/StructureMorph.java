package mchorse.blockbuster_pack.morphs;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.client.ClientHandlerStructure;
import mchorse.blockbuster.network.common.structure.PacketStructure;
import mchorse.blockbuster.network.common.structure.PacketStructureRequest;
import mchorse.blockbuster.network.server.ServerHandlerStructureRequest;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StructureMorph extends AbstractMorph
{
    /**
     * Map of baked structures 
     */
    @SideOnly(Side.CLIENT)
    public static Map<String, StructureRenderer> STRUCTURES;

    /**
     * Cache of structures 
     */
    public static final Map<String, Long> STRUCTURE_CACHE = new HashMap<String, Long>();

    /**
     * The name of the structure which should be rendered 
     */
    public String structure = "";

    @SideOnly(Side.CLIENT)
    public static void request()
    {
        if (STRUCTURES.isEmpty())
        {
            Dispatcher.sendToServer(new PacketStructureRequest());
        }
    }

    @SideOnly(Side.CLIENT)
    public static void reloadStructures()
    {
        cleanUp();
        request();
    }

    /**
     * Update structures 
     */
    public static void checkStructures()
    {
        for (String name : ServerHandlerStructureRequest.getAllStructures())
        {
            File file = ServerHandlerStructureRequest.getStructureFolder(name);
            Long modified = STRUCTURE_CACHE.get(name);

            if (modified == null)
            {
                modified = file.lastModified();
                STRUCTURE_CACHE.put(name, modified);
            }

            if (modified != null && modified.longValue() < file.lastModified())
            {
                STRUCTURE_CACHE.put(name, file.lastModified());

                IMessage packet = new PacketStructure(name, null);
                PlayerList players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();

                for (String username : players.getOnlinePlayerNames())
                {
                    EntityPlayerMP player = players.getPlayerByUsername(username);

                    if (player != null)
                    {
                        Dispatcher.sendTo(packet, player);
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void cleanUp()
    {
        for (StructureRenderer renderer : STRUCTURES.values())
        {
            renderer.delete();
        }

        STRUCTURES.clear();
    }

    public StructureMorph()
    {
        super();

        this.name = "structure";
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getSubclassDisplayName()
    {
        return I18n.format("blockbuster.morph.structure");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        StructureRenderer renderer = STRUCTURES.get(this.structure);

        if (renderer != null)
        {
            if (renderer.vbo < 0)
            {
                if (renderer.vbo == -1)
                {
                    renderer.vbo = -2;
                    Dispatcher.sendToServer(new PacketStructureRequest(this.structure));
                }

                return;
            }

            int max = Math.max(renderer.size.getX(), Math.max(renderer.size.getY(), renderer.size.getZ()));

            scale /= 0.65F * max;

            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            GlStateManager.enableDepth();
            GlStateManager.enableAlpha();
            GlStateManager.disableCull();
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, 0);
            GlStateManager.scale(scale, scale, scale);
            GlStateManager.rotate(45.0F, -1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(45.0F, 0.0F, -1.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            renderer.render();
            renderer.renderTEs();
            GlStateManager.disableLighting();
            GlStateManager.popMatrix();
            GlStateManager.enableCull();
            GlStateManager.disableAlpha();
            GlStateManager.disableDepth();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        StructureRenderer renderer = STRUCTURES.get(this.structure);

        if (renderer != null)
        {
            if (renderer.vbo < 0)
            {
                if (renderer.vbo == -1)
                {
                    renderer.vbo = -2;
                    Dispatcher.sendToServer(new PacketStructureRequest(this.structure));
                }

                return;
            }

            float lastX = OpenGlHelper.lastBrightnessX;
            float lastY = OpenGlHelper.lastBrightnessY;

            renderer.world.setLightLevel(lastX, lastY);

            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            /* These states are important to enable */
            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();
            GlStateManager.translate(x, y, z);

            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            renderer.render();

            GlStateManager.disableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.shadeModel(GL11.GL_FLAT);

            GL11.glColor4f(1, 1, 1, 1);
            renderer.renderTEs();

            GlStateManager.popMatrix();
        }
    }

    @Override
    public AbstractMorph create()
    {
        return new StructureMorph();
    }

    @Override
    public void copy(AbstractMorph from)
    {
        super.copy(from);

        if (from instanceof StructureMorph)
        {
            StructureMorph morph = (StructureMorph) from;

            this.structure = morph.structure;
        }
    }

    @Override
    public float getWidth(EntityLivingBase target)
    {
        return 0.6F;
    }

    @Override
    public float getHeight(EntityLivingBase target)
    {
        return 1.8F;
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof StructureMorph)
        {
            StructureMorph morph = (StructureMorph) obj;

            result = result && Objects.equals(this.structure, morph.structure);
        }

        return result;
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        this.structure = tag.getString("Structure");
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        if (!this.structure.isEmpty())
        {
            tag.setString("Structure", this.structure);
        }
    }

    /**
     * Structure renderer
     * 
     * All it does is renders compiled display list and also has the 
     * method {@link #delete()} to clean up GL memory. 
     */
    @SideOnly(Side.CLIENT)
    public static class StructureRenderer
    {
        public int vbo = -1;
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
            /* Create VBO */
            int vbo = GL15.glGenBuffers();

            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buffer = tess.getBuffer();

            this.render(buffer);

            int count = buffer.getVertexCount();

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.getByteBuffer(), GL15.GL_STATIC_DRAW);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

            buffer.finishDrawing();

            this.count = count;
            this.vbo = vbo;
        }

        public void render()
        {
            GL11.glNormal3f(0, 0.6F, 0);

            if (Blockbuster.cachedStructureRendering.get())
            {
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);

                GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
                GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

                GlStateManager.glVertexPointer(3, 5126, 28, 0);
                GlStateManager.glColorPointer(4, 5121, 28, 12);
                GlStateManager.glTexCoordPointer(2, 5126, 28, 16);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                GlStateManager.glTexCoordPointer(2, 5122, 28, 24);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);

                GlStateManager.glDrawArrays(GL11.GL_QUADS, 0, this.count);

                GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
                GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
                GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
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
            if (this.vbo > 0)
            {
                GL15.glDeleteBuffers(this.vbo);
                this.vbo = -1;
                this.count = 0;
            }
        }
    }
}