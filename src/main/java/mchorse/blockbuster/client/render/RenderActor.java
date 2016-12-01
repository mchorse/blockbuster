package mchorse.blockbuster.client.render;

import java.util.Map;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.gui.utils.GlStateManager;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.utils.EntityUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Render actor class
 *
 * Render actor entities with custom swaggalicious models ?8|
 */
public class RenderActor extends RenderLiving
{
    /**
     * Default texture of the renderer
     */
    private static final ResourceLocation defaultTexture = new ResourceLocation(Blockbuster.MODID, "textures/entity/actor.png");
    private static final String defaultModel = "steve";

    /**
     * Initiate render actor with set of layers.
     *
     * - Layer elytra is responsible for rendering elytra on the back of the
     *   custom model
     * - Layer held item is responsible for rendering item that selected in
     *   hot bar and located in off hand slot for every limb that
     *   has "holding" property
     * - Layer biped armor is responsible for rendering armor on every limb
     *   that has "armor" property
     */
    public RenderActor(float f)
    {
        super(ModelCustom.MODELS.get(defaultModel), f);
    }

    /**
     * Use skin from resource pack or default one (if skin is empty or just
     * wasn't found by actor pack)
     */
    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        EntityActor actor = (EntityActor) entity;
        ResourceLocation skin = actor.skin;

        if (skin != null)
        {
            boolean actors = skin.getResourceDomain().equals("blockbuster.actors");

            if (!actors || (actors && ClientProxy.actorPack.resourceExists(skin)))
            {
                return skin;
            }
        }

        return defaultTexture;
    }

    /**
     * Another important extension. Assign sneaking property, without it, actor
     * would look like an idiot who's clipping through the ground for a minute.
     *
     * Also, head rotation is interpolated inside of this method, another thing,
     * yeah previousYaw thing is pretty stupid (the renderer is one for all),
     * but it works...
     */
    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        EntityActor actor = (EntityActor) entity;
        this.shadowOpaque = actor.invisible ? 0.0F : 1.0F;

        if (actor.invisible)
        {
            return;
        }

        this.setupModel(actor);

        if (this.mainModel != null)
        {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    @Override
    protected void renderEquippedItems(EntityLivingBase p_77029_1_, float p_77029_2_)
    {
        if (p_77029_1_ instanceof EntityActor)
        {
            this.renderFuckingEquippedItemsAlready((EntityActor) p_77029_1_, p_77029_2_);
        }
    }

    protected void renderFuckingEquippedItemsAlready(EntityActor actor, float abc)
    {
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        super.renderEquippedItems(actor, abc);
        super.renderArrowsStuckInEntity(actor, abc);

        float f2;

        ItemStack itemstack1 = actor.getHeldItem();

        if (itemstack1 != null)
        {
            GL11.glPushMatrix();
            ((ModelCustom) this.mainModel).right[0].postRender(0.0625F);
            GL11.glTranslatef(-0.0625F / 4, 0.4375F * 8 / 7, 0.0625F);

            net.minecraftforge.client.IItemRenderer customRenderer = net.minecraftforge.client.MinecraftForgeClient.getItemRenderer(itemstack1, net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED);
            boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED, itemstack1, net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D));

            if (is3D || itemstack1.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack1.getItem()).getRenderType()))
            {
                f2 = 0.5F;
                GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
                f2 *= 0.75F;
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(-f2, -f2, f2);
            }
            else if (itemstack1.getItem() == Items.bow)
            {
                f2 = 0.625F;
                GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
                GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(f2, -f2, f2);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else if (itemstack1.getItem().isFull3D())
            {
                f2 = 0.625F;

                if (itemstack1.getItem().shouldRotateAroundWhenRendering())
                {
                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                }

                GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
                GL11.glScalef(f2, -f2, f2);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else
            {
                f2 = 0.375F;
                GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
                GL11.glScalef(f2, f2, f2);
                GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
            }

            float f3, f4;
            int k;
            float f12;

            if (itemstack1.getItem().requiresMultipleRenderPasses())
            {
                for (k = 0; k < itemstack1.getItem().getRenderPasses(itemstack1.getItemDamage()); ++k)
                {
                    int i = itemstack1.getItem().getColorFromItemStack(itemstack1, k);
                    f12 = (i >> 16 & 255) / 255.0F;
                    f3 = (i >> 8 & 255) / 255.0F;
                    f4 = (i & 255) / 255.0F;
                    GL11.glColor4f(f12, f3, f4, 1.0F);
                    this.renderManager.itemRenderer.renderItem(actor, itemstack1, k);
                }
            }
            else
            {
                k = itemstack1.getItem().getColorFromItemStack(itemstack1, 0);
                float f11 = (k >> 16 & 255) / 255.0F;
                f12 = (k >> 8 & 255) / 255.0F;
                f3 = (k & 255) / 255.0F;
                GL11.glColor4f(f11, f12, f3, 1.0F);
                this.renderManager.itemRenderer.renderItem(actor, itemstack1, 0);
            }

            GL11.glPopMatrix();
        }
    }

    /**
     * Setup the model for actor instance.
     *
     * This method is responsible for picking the right model and pose based
     * on actor properties.
     */
    protected void setupModel(EntityActor entity)
    {
        Map<String, ModelCustom> models = ModelCustom.MODELS;

        String key = models.containsKey(entity.model) ? entity.model : defaultModel;
        String pose = EntityUtils.poseForEntity(entity);

        ModelCustom model = models.get(key);

        if (model != null)
        {
            model.pose = model.model.getPose(pose);

            this.mainModel = model;
        }
    }

    /**
     * Make actor a little bit smaller (so he looked like steve, and not like a
     * overgrown rodent).
     */
    @Override
    protected void preRenderCallback(EntityLivingBase actor, float partialTickTime)
    {
        float f = 0.935F;
        GlStateManager.scale(f, f, f);
    }
}