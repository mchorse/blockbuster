package mchorse.blockbuster.client.render.tileentity;

import mchorse.blockbuster.client.KeyboardHandler;
import mchorse.blockbuster.client.RenderingHandler;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.mclib.math.functions.limit.Min;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Gun items's TEISR
 *
 * <p>This class is responsible for rendering gun items</p>
 */
@SideOnly(Side.CLIENT)
public class TileEntityGunItemStackRenderer extends TileEntityItemStackRenderer
{
    /**
     * A cache of model TEs
     */
    public static final Map<ItemStack, GunEntry> models = new HashMap<ItemStack, GunEntry>();

    @Override
    public void renderByItem(ItemStack stack, float partialTicks)
    {
        /* Thank you Mojang, very cool! */
        partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();

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
            GunProps props = NBTUtils.getGunProps(stack);

            if (props != null)
            {
                model = new GunEntry(props);
                models.put(stack, model);
            }
        }

        if (model != null)
        {
            model.timer = 20;

            if (
                RenderingHandler.itemTransformType != ItemCameraTransforms.TransformType.GUI &&
                RenderingHandler.itemTransformType != ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND &&
                RenderingHandler.itemTransformType != ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND &&
                RenderingHandler.itemTransformType != ItemCameraTransforms.TransformType.FIXED &&
                RenderingHandler.itemTransformType != ItemCameraTransforms.TransformType.GROUND
            ) {
                if (!(KeyboardHandler.zoom.isKeyDown() && model.props.hideHandsOnZoom))
                {
                    model.props.renderHands(RenderingHandler.getLastItemHolder(), partialTicks);
                }

                if (model.props.useZoomOverlayMorph && KeyboardHandler.zoom.isKeyDown())
                {
                    model.props.renderZoomOverlay(RenderingHandler.getLastItemHolder(), partialTicks);
                }
            }
            
            if (RenderingHandler.itemTransformType != ItemCameraTransforms.TransformType.GUI)
            {
                if (KeyboardHandler.zoom.isKeyDown() && model.props.hideHandsOnZoom)
                {
                    if
                    (
                     RenderingHandler.itemTransformType != ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND &&
                     RenderingHandler.itemTransformType != ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND
                    ) {
                        model.props.render(RenderingHandler.getLastItemHolder(), partialTicks);
                    }
                }
                else
                {
                    model.props.render(RenderingHandler.getLastItemHolder(), partialTicks);
                }
            }
            
            if (RenderingHandler.itemTransformType == ItemCameraTransforms.TransformType.GUI)
            {
                if (model.props.useInventoryMorph && model.props.inventoryMorph != null)
                {
                    model.props.renderInventoryMorph(RenderingHandler.getLastItemHolder(), partialTicks);
                }
                else
                {
                    model.props.render(RenderingHandler.getLastItemHolder(), partialTicks);
                }
            }

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
        public GunProps props;

        public GunEntry(GunProps props)
        {
            this.props = props;
        }
    }
}