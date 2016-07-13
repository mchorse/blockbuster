package noname.blockbuster.client.render;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noname.blockbuster.Blockbuster;
import noname.blockbuster.ClientProxy;
import noname.blockbuster.entity.EntityActor;

/**
 * Actor renderer
 *
 * Renders actor entities with swag
 */
@SideOnly(Side.CLIENT)
public class RenderActor extends RenderBiped<EntityActor>
{
    private static final ResourceLocation defaultTexture = new ResourceLocation(Blockbuster.MODID, "textures/entity/actor.png");

    private float previousYaw;

    /**
     * Add armor layer to my biped texture
     */
    public RenderActor(RenderManager renderManagerIn, ModelBiped modelBipedIn, float shadowSize)
    {
        super(renderManagerIn, modelBipedIn, shadowSize);

        this.addLayer(new LayerBipedArmor(this));
        this.addLayer(new LayerElytra(this));
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
    public void doRender(EntityActor entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.modelBipedMain.isSneak = entity.isSneaking();
        this.setArmsPoses(entity);

        if (!entity.renderName)
        {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
        else
        {
            float lastYaw = entity.rotationYawHead;
            float lastPrevYaw = entity.prevRotationYawHead;

            if (this.previousYaw != entityYaw)
            {
                this.previousYaw = entityYaw;
            }

            entity.prevRotationYawHead = entityYaw;
            entityYaw = this.interpolateRotation(entityYaw, this.previousYaw, partialTicks);
            entity.rotationYawHead = entityYaw;

            super.doRender(entity, x, y, z, entityYaw, partialTicks * 0.5F);

            entity.rotationYawHead = lastYaw;
            entity.prevRotationYawHead = lastPrevYaw;
        }
    }

    /**
     * Sets arms poses
     */
    private void setArmsPoses(EntityActor entity)
    {
        ItemStack itemstack = entity.getHeldItemMainhand();
        ItemStack itemstack1 = entity.getHeldItemOffhand();
        ModelBiped.ArmPose modelbiped$armpose = ModelBiped.ArmPose.EMPTY;
        ModelBiped.ArmPose modelbiped$armpose1 = ModelBiped.ArmPose.EMPTY;

        if (itemstack != null)
        {
            modelbiped$armpose = ModelBiped.ArmPose.ITEM;

            if (entity.getItemInUseCount() > 0)
            {
                EnumAction enumaction = itemstack.getItemUseAction();

                if (enumaction == EnumAction.BLOCK)
                {
                    modelbiped$armpose = ModelBiped.ArmPose.BLOCK;
                }
                else if (enumaction == EnumAction.BOW)
                {
                    modelbiped$armpose = ModelBiped.ArmPose.BOW_AND_ARROW;
                }
            }
        }

        if (itemstack1 != null)
        {
            modelbiped$armpose1 = ModelBiped.ArmPose.ITEM;

            if (entity.getItemInUseCount() > 0)
            {
                EnumAction enumaction1 = itemstack1.getItemUseAction();

                if (enumaction1 == EnumAction.BLOCK)
                {
                    modelbiped$armpose1 = ModelBiped.ArmPose.BLOCK;
                }
            }
        }

        this.modelBipedMain.rightArmPose = modelbiped$armpose;
        this.modelBipedMain.leftArmPose = modelbiped$armpose1;
    }

    /**
     * Most important extension! Don't render the name in GUI, that looks
     * irritating. actor.renderName is switched for awhile to false during GUI
     * rendering.
     *
     * See GuiActorSkin for a reference.
     */
    @Override
    protected boolean canRenderName(EntityActor entity)
    {
        return super.canRenderName(entity) && entity.renderName;
    }

    /**
     * Make actor a little bit smaller (so he looked like steve, and not like a
     * giant weirdo).
     */
    protected void preRenderCallback(AbstractClientPlayer entitylivingbaseIn, float partialTickTime)
    {
        float f = 0.920F;
        GlStateManager.scale(f, f, f);
    }

    /**
     * Use skin from resource pack or default one (if skin is empty or just
     * wasn't found by actor pack)
     */
    @Override
    protected ResourceLocation getEntityTexture(EntityActor entity)
    {
        ResourceLocation location = new ResourceLocation("blockbuster.actors", entity.skin);

        if (ClientProxy.actorPack.resourceExists(location))
        {
            return location;
        }

        return defaultTexture;
    }

    /**
     * Taken from RenderPlayer
     */
    @Override
    protected void rotateCorpse(EntityActor actor, float p_77043_2_, float p_77043_3_, float partialTicks)
    {
        if (actor.isElytraFlying())
        {
            super.rotateCorpse(actor, p_77043_2_, p_77043_3_, partialTicks);

            float f = actor.getTicksElytraFlying() + partialTicks;
            float f1 = MathHelper.clamp_float(f * f / 100.0F, 0.0F, 1.0F);
            Vec3d vec3d = actor.getLook(partialTicks);
            double d0 = actor.motionX * actor.motionX + actor.motionZ * actor.motionZ;
            double d1 = vec3d.xCoord * vec3d.xCoord + vec3d.zCoord * vec3d.zCoord;

            GlStateManager.rotate(f1 * (-90.0F - actor.rotationPitch), 1.0F, 0.0F, 0.0F);

            if (d0 > 0.0D && d1 > 0.0D)
            {
                double d2 = (actor.motionX * vec3d.xCoord + actor.motionZ * vec3d.zCoord) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = actor.motionX * vec3d.zCoord - actor.motionZ * vec3d.xCoord;

                GlStateManager.rotate((float) (Math.signum(d3) * Math.acos(d2)) * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
            }
        }
        else
        {
            super.rotateCorpse(actor, p_77043_2_, p_77043_3_, partialTicks);
        }
    }

    /**
     * Renderer factory
     */
    public static class FactoryActor implements IRenderFactory<EntityActor>
    {
        @Override
        public RenderActor createRenderFor(RenderManager manager)
        {
            return new RenderActor(manager, new ModelBiped(), 0.5F);
        }
    }
}
