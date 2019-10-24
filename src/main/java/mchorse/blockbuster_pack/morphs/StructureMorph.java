package mchorse.blockbuster_pack.morphs;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.minecraft.client.renderer.entity.Render;
import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.structure.PacketStructure;
import mchorse.blockbuster.network.common.structure.PacketStructureRequest;
import mchorse.blockbuster.network.server.ServerHandlerStructureRequest;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    /**
     * Whether the structure applies lightmap 
     */
    public boolean lighting;

    @SideOnly(Side.CLIENT)
    public static void request()
    {
        if (STRUCTURES.isEmpty())
        {
            Dispatcher.sendToServer(new PacketStructureRequest());
        }
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
        this.name = "structure";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        StructureRenderer renderer = STRUCTURES.get(this.structure);

        if (renderer != null)
        {
            if (renderer.list < 0)
            {
                if (renderer.list == -1)
                {
                    renderer.list = -2;
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
            if (renderer.list < 0)
            {
                if (renderer.list == -1)
                {
                    renderer.list = -2;
                    Dispatcher.sendToServer(new PacketStructureRequest(this.structure));
                }

                return;
            }

            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            /* These states are important to enable */
            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();
            GlStateManager.translate(x, y, z);

            if (!this.lighting)
            {
                RenderHelper.disableStandardItemLighting();
            }

            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            renderer.render();

            GlStateManager.disableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.shadeModel(GL11.GL_FLAT);

            if (!this.lighting)
            {
                GlStateManager.enableLighting();
                GlStateManager.enableLight(0);
                GlStateManager.enableLight(1);
                GlStateManager.enableColorMaterial();
            }

            GL11.glColor4f(1, 1, 1, 1);
            renderer.renderTEs();

            GlStateManager.popMatrix();
        }
    }

    @Override
    public AbstractMorph clone(boolean isRemote)
    {
        StructureMorph morph = new StructureMorph();

        morph.name = this.name;
        morph.settings = this.settings;
        morph.structure = this.structure;
        morph.lighting = this.lighting;

        return morph;
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
            result = result && this.lighting == morph.lighting;
        }

        return result;
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);
        this.structure = tag.getString("Structure");
        this.lighting = tag.getBoolean("Lighting");
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        if (!this.structure.isEmpty()) tag.setString("Structure", this.structure);
        if (this.lighting) tag.setBoolean("Lighting", this.lighting);
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
        public int list = -1;
        public BlockPos size = BlockPos.ORIGIN;
        public List<TileEntity> tes;

        public StructureRenderer()
        {}

        public StructureRenderer(int list, BlockPos size, List<TileEntity> tes)
        {
            this.list = list;
            this.size = size;
            this.tes = tes;
        }

        public void render()
        {
            GL11.glNormal3f(0, 1, 0);
            GL11.glCallList(this.list);
        }

        public void renderTEs()
        {
            if (this.tes == null)
            {
                return;
            }

            for (TileEntity te : this.tes)
            {
                BlockPos pos = te.getPos();
                TileEntityRendererDispatcher.instance.render(te, pos.getX() - this.size.getX() / 2D - 1, pos.getY() - 1, pos.getZ() - this.size.getZ() / 2D - 1, 0);
            }
        }

        public void delete()
        {
            if (this.list > 0)
            {
                GL11.glDeleteLists(this.list, 1);
                this.list = -1;
                this.tes = null;
            }
        }
    }
}