package mchorse.blockbuster_pack.morphs;

import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.structure.PacketStructure;
import mchorse.blockbuster.network.common.structure.PacketStructureRequest;
import mchorse.blockbuster.network.server.ServerHandlerStructureRequest;
import mchorse.blockbuster_pack.morphs.structure.StructureAnimation;
import mchorse.blockbuster_pack.morphs.structure.StructureRenderer;
import mchorse.blockbuster_pack.morphs.structure.StructureStatus;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.utils.Animation;
import mchorse.metamorph.api.morphs.utils.IAnimationProvider;
import mchorse.metamorph.api.morphs.utils.ISyncableMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StructureMorph extends AbstractMorph implements IAnimationProvider, ISyncableMorph
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
     * TSR for structure morph
     */
    public ModelTransform pose = new ModelTransform();

    public StructureAnimation animation = new StructureAnimation();

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
    public Animation getAnimation()
    {
        return this.animation;
    }

    @Override
    public void pause(AbstractMorph previous, int offset)
    {
        this.animation.pause(offset);

        if (previous instanceof StructureMorph)
        {
            StructureMorph structure = (StructureMorph) previous;

            this.animation.last = new ModelTransform();
            this.animation.last.copy(structure.pose);
        }
    }

    @Override
    public boolean isPaused()
    {
        return this.animation.paused;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getSubclassDisplayName()
    {
        String suffix = this.structure != null && !this.structure.isEmpty() ? " (" + this.structure + ")" : "";

        return I18n.format("blockbuster.morph.structure") + suffix;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        StructureRenderer renderer = STRUCTURES.get(this.structure);

        if (renderer != null)
        {
            if (renderer.status != StructureStatus.LOADED)
            {
                if (renderer.status == StructureStatus.UNLOADED)
                {
                    renderer.status = StructureStatus.LOADING;
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
            if (renderer.status != StructureStatus.LOADED)
            {
                if (renderer.status == StructureStatus.UNLOADED)
                {
                    renderer.status = StructureStatus.LOADING;
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

            ModelTransform transform = this.pose;

            if (this.animation.isInProgress())
            {
                transform = new ModelTransform();
                transform.copy(this.pose);

                this.animation.apply(transform, partialTicks);
            }

            transform.transform();

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
    public void update(EntityLivingBase target)
    {
        super.update(target);

        this.animation.update();
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
            this.pose.copy(morph.pose);
            this.animation.copy(morph.animation);
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
            result = result && Objects.equals(this.pose, morph.pose);
        }

        return result;
    }

    @Override
    public boolean canMerge(AbstractMorph morph)
    {
        if (morph instanceof StructureMorph)
        {
            StructureMorph structure = (StructureMorph) morph;

            this.mergeBasic(morph);

            if (!structure.animation.ignored)
            {
                this.animation.merge(this, structure);
                this.copy(structure);
                this.animation.progress = 0;
            }

            return true;
        }

        return super.canMerge(morph);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        if (tag.hasKey("Structure")) this.structure = tag.getString("Structure");
        if (tag.hasKey("Pose")) this.pose.fromNBT(tag.getCompoundTag("Pose"));
        if (tag.hasKey("Animation")) this.animation.fromNBT(tag.getCompoundTag("Animation"));
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        if (!this.structure.isEmpty())
        {
            tag.setString("Structure", this.structure);
        }

        if (!this.pose.isDefault())
        {
            tag.setTag("Pose", this.pose.toNBT());
        }

        NBTTagCompound animation = this.animation.toNBT();

        if (!animation.hasNoTags())
        {
            tag.setTag("Animation", animation);
        }
    }
}