package mchorse.blockbuster.client.render.tileentity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import mchorse.blockbuster.capabilities.gun.Gun;
import mchorse.blockbuster.capabilities.gun.IGun;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Gun items's TEISR
 * 
 * This class is responsible for rendering gun items
 */
@SideOnly(Side.CLIENT)
public class TileEntityGunItemStackRenderer
{
    /**
     * A cache of model TEs
     */
    public static final Map<ItemStack, GunEntry> models = new HashMap<ItemStack, GunEntry>();

    public void renderByItem(ItemStack stack, float partialTicks)
    {
        /* Removing from the cache unused models */
        Iterator<GunEntry> it = models.values().iterator();

        while (it.hasNext())
        {
            GunEntry model = it.next();

            if (model.timer <= 0)
            {
                it.remove();
            }

            model.timer--;
        }

        GunEntry model = models.get(stack);

        if (model == null)
        {
            IGun gun = Gun.get(stack);

            if (gun != null)
            {
                model = new GunEntry(gun);
                models.put(stack, model);
            }
        }

        if (model != null)
        {
            model.timer = 20;
            model.gun.getInfo().render(partialTicks);
            this.reset();
        }
    }

    public void reset()
    {
        Minecraft mc = Minecraft.getMinecraft();
        TextureManager manager = mc.getTextureManager();

        manager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        mc.getTextureMapBlocks().setBlurMipmapDirect(false, true);
        mc.getTextureMapBlocks().setBlurMipmap(false, false);
    }

    public static class GunEntry
    {
        public int timer = 20;
        public IGun gun;

        public GunEntry(IGun gun)
        {
            this.gun = gun;
        }
    }
}