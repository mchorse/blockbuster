package mchorse.blockbuster_pack.client.render.layers;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import mchorse.blockbuster.api.Model.Limb;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.render.RenderCustomModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Custom head layer
 */
@SideOnly(Side.CLIENT)
public class LayerCustomHead implements LayerRenderer<EntityLivingBase>
{
    public RenderCustomModel render;

    public LayerCustomHead(RenderCustomModel render)
    {
        this.render = render;
    }

    /**
     * Render the layer
     *
     * This method is responsible for rendering either skull with 
     * player's name or an item (i.e. block or something) on custom 
     * model's head.
     */
    @Override
    public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        ModelBase base = this.render.getMainModel();

        if (base instanceof ModelCustom && stack != null && stack.getItem() != null)
        {
            ModelCustom model = (ModelCustom) base;

            for (ModelCustomRenderer limb : model.limbs)
            {
                if (!limb.limb.looking)
                {
                    continue;
                }

                GlStateManager.pushMatrix();

                limb.postRender(scale);

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.renderItem(entity, stack, limb.limb, limbSwing);
                GlStateManager.popMatrix();
            }
        }
    }

    /**
     * This code is taken from
     * {@link net.minecraft.client.renderer.entity.layers.LayerCustomHead} 
     * in order to make blocks rendering available for usage. 
     */
    protected void renderItem(EntityLivingBase entity, ItemStack stack, Limb limb, float limbSwing)
    {
        Item item = stack.getItem();
        Minecraft mc = Minecraft.getMinecraft();

        float w = limb.size[0] / 8F;
        float h = limb.size[1] / 8F;
        float d = limb.size[2] / 8F;

        float offsetX = limb.anchor[0] * w / 2;
        float offsetY = limb.anchor[1] * h / 2;
        float offsetZ = limb.anchor[2] * d / 2;

        /* Player skull rendering */
        if (item == Items.SKULL)
        {
            /* Limb */
            GlStateManager.translate(-w / 4 + offsetX, h / 2 - offsetY, d / 4 - offsetZ);
            GlStateManager.scale(1.1875F * w, -1.1875F * h, -1.1875F * d);

            GameProfile gameprofile = null;

            if (stack.hasTagCompound())
            {
                NBTTagCompound nbttagcompound = stack.getTagCompound();

                if (nbttagcompound.hasKey("SkullOwner", 10))
                {
                    gameprofile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"));
                }
                else if (nbttagcompound.hasKey("SkullOwner", 8))
                {
                    String s = nbttagcompound.getString("SkullOwner");

                    if (!StringUtils.isNullOrEmpty(s))
                    {
                        gameprofile = TileEntitySkull.updateGameprofile(new GameProfile((UUID) null, s));
                        nbttagcompound.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameprofile));
                    }
                }
            }

            TileEntitySkullRenderer.instance.renderSkull(-0.5F, 0.0F, -0.5F, EnumFacing.UP, 180.0F, stack.getMetadata(), gameprofile, -1, limbSwing);
        }
        else if (!(item instanceof ItemArmor) || ((ItemArmor) item).getEquipmentSlot() != EntityEquipmentSlot.HEAD)
        {
            /* Custom block rendering */
            GlStateManager.translate(-w / 4 + offsetX, h / 4 - offsetY, d / 4 - offsetZ);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(0.5625F * w, -0.5625F * h, -0.5625F * d);

            mc.getItemRenderer().renderItem(entity, stack, ItemCameraTransforms.TransformType.HEAD);
        }
    }

    @Override
    public boolean shouldCombineTextures()
    {
        return false;
    }
}