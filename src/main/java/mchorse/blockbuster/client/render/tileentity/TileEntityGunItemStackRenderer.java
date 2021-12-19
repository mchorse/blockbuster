package mchorse.blockbuster.client.render.tileentity;

import mchorse.blockbuster.client.KeyboardHandler;
import mchorse.blockbuster.client.RenderingHandler;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.mclib.math.functions.limit.Min;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Gun items's TEISR
 * 
 * This class is responsible for rendering gun items
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
            ItemStack baseItem = Minecraft.getMinecraft().player.getHeldItemMainhand();
            if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && baseItem.equals(stack)){
                if (RenderingHandler.itemTransformType != ItemCameraTransforms.TransformType.GUI){
                    if (!KeyboardHandler.zoom.isKeyDown()){
                    model.props.renderHands(RenderingHandler.getLastItemHolder(), partialTicks);
                    }
                    if (model.props.enableOverlay && KeyboardHandler.zoom.isKeyDown()){
                        model.props.renderOverlay(RenderingHandler.getLastItemHolder(), partialTicks);
                    }
                }
            }
            if (RenderingHandler.itemTransformType != ItemCameraTransforms.TransformType.GUI){
                if (!(KeyboardHandler.zoom.isKeyDown() && model.props.hideHandOnZoom)){
                    model.props.render(RenderingHandler.getLastItemHolder(), partialTicks);
                }
            }
            if (RenderingHandler.itemTransformType == ItemCameraTransforms.TransformType.GUI){
                model.props.render(RenderingHandler.getLastItemHolder(), partialTicks);
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