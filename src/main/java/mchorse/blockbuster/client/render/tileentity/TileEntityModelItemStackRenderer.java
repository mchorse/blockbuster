package mchorse.blockbuster.client.render.tileentity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Model block's TEISR
 * 
 * This class is responsible for rendering model blocks in inventory
 */
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

    @Override
    public void renderByItem(ItemStack stack, float partialTicks)
    {
        if (stack.getItem() == Blockbuster.modelBlockItem)
        {
            if (this.def == null)
            {
                this.def = new TileEntityModel();
            }

            /* Removing from the cache unused models */
            Iterator<TEModel> it = models.values().iterator();

            while (it.hasNext())
            {
                TEModel model = it.next();

                if (model.timer <= 0)
                {
                    it.remove();
                }

                model.timer--;
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

                if (model != null)
                {
                    model.timer = 20;

                    ClientProxy.modelRenderer.render(model.model, 0, 0, 0, partialTicks, 0, 0);

                    return;
                }
            }

            ClientProxy.modelRenderer.render(this.def, 0, 0, 0, partialTicks, 0, 0);

            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, true);
            Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        }
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