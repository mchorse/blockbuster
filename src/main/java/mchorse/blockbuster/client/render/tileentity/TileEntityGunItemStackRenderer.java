package mchorse.blockbuster.client.render.tileentity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import mchorse.blockbuster.Blockbuster;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Model block's TEISR
 * 
 * This class is responsible for rendering model blocks in inventory
 */
public class TileEntityGunItemStackRenderer
{
    /**
     * A cache of model TEs
     */
    public static final Map<NBTTagCompound, GunRenderer> models = new HashMap<NBTTagCompound, GunRenderer>();

    public void renderByItem(ItemStack stack, float partialTicks)
    {
        if (stack.getItem() == Blockbuster.gunItem)
        {
            /* Removing from the cache unused models */
            Iterator<GunRenderer> it = models.values().iterator();

            while (it.hasNext())
            {
                GunRenderer model = it.next();

                if (model.timer <= 0)
                {
                    it.remove();
                }

                model.timer--;
            }

            NBTTagCompound tag = stack.getTagCompound();

            if (tag != null)
            {
                GunRenderer model = models.get(tag);

                if (model == null)
                {
                    model = new GunRenderer(tag);
                    models.put(tag, model);
                }

                if (model != null)
                {
                    model.timer = 20;
                    this.renderModel(model, partialTicks);
                }
            }
        }
    }

    public void renderModel(GunRenderer model, float partialTicks)
    {
        model.render(partialTicks);

        Minecraft mc = Minecraft.getMinecraft();
        TextureManager manager = mc.getTextureManager();

        manager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        mc.getTextureMapBlocks().setBlurMipmapDirect(false, true);
        mc.getTextureMapBlocks().setBlurMipmap(false, false);
    }

    public static class GunRenderer
    {
        /* Logic fields */
        public int timer = 20;
        public AbstractMorph current;

        /* Rendering data */
        public AbstractMorph defaultMorph;
        public AbstractMorph shootingMorph;
        public int shootingDelay;

        public GunRenderer(NBTTagCompound tag)
        {

        }

        public void render(float partialTicks)
        {
            if (this.current != null)
            {
                this.current.render(Minecraft.getMinecraft().thePlayer, 0, 0, 0, 0, partialTicks);
            }
        }
    }
}