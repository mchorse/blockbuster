package mchorse.blockbuster.client.render.tileentity;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.mclib.utils.MatrixUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Matrix4f;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Model block's TEISR
 * 
 * This class is responsible for rendering model blocks in inventory
 */
@SideOnly(Side.CLIENT)
public class TileEntityModelItemStackRenderer extends TileEntityItemStackRenderer
{
    /**
     * Default tile entity model
     */
    public TileEntityModel def;

    /**
     * A cache of model TEs
     */
    public static final Map<NBTTagCompound, TEModel> models = new HashMap<NBTTagCompound, TEModel>();

    private static boolean isRendering;

    public static boolean isRendering()
    {
        return isRendering;
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks)
    {
        /* Thank you Mojang, very cool! */
        partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();

        if (this.def == null)
        {
            this.def = new TileEntityModel();
        }

        NBTTagCompound tag = stack.getTagCompound();

        if (tag != null)
        {
            TEModel model = models.get(tag);

            if (model == null)
            {
                TileEntityModel te = new TileEntityModel();
                te.readFromNBT(tag.getCompoundTag("BlockEntityTag"));

                model = new TEModel(te);
                models.put(tag, model);
            }
            
            /*
             * timer in ticks when to remove items that are not rendered anymore
             * 5 should be enough to ensure that even with very low fps the model doesn't get removed unnecessarily
             */
            model.timer = 5;
            this.renderModel(model.model, partialTicks);

            return;
        }

        this.renderModel(this.def, partialTicks);
    }

    public void renderModel(TileEntityModel model, float partialTicks)
    {
        isRendering = true;

        ClientProxy.modelRenderer.render(model, 0, 0, 0, partialTicks, 0, 0);

        Minecraft mc = Minecraft.getMinecraft();
        TextureManager manager = mc.getTextureManager();

        manager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        mc.getTextureMapBlocks().setBlurMipmapDirect(false, true);
        mc.getTextureMapBlocks().setBlurMipmap(false, false);

        isRendering = false;
    }

    /**
     * {@link TileEntityModel} wrapper class
     * 
     * This class allows to hold timer for unloading purpose (so objects 
     * won't get stuck in the map forever, which might cause memory 
     * leak) 
     */
    public static class TEModel
    {
        public int timer = 20;
        public TileEntityModel model;

        public TEModel(TileEntityModel model)
        {
            this.model = model;
        }
    }
}