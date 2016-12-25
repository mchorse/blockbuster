package mchorse.blockbuster.client.render;

import java.util.Map;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.capabilities.morphing.IMorphing;
import mchorse.blockbuster.capabilities.morphing.Morphing;
import mchorse.blockbuster.capabilities.morphing.MorphingProvider;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.render.layers.LayerActorArmor;
import mchorse.blockbuster.client.render.layers.LayerElytra;
import mchorse.blockbuster.client.render.layers.LayerHeldItem;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Player renderer
 *
 * Renders player entities with swag
 */
@SideOnly(Side.CLIENT)
public class RenderPlayer extends RenderLivingBase<EntityPlayer>
{
    /**
     * Default texture of the renderer
     */
    private static final ResourceLocation defaultTexture = new ResourceLocation(Blockbuster.MODID, "textures/entity/actor.png");

    public RenderPlayer(RenderManager renderManagerIn, float shadowSize)
    {
        super(renderManagerIn, null, shadowSize);

        this.addLayer(new LayerElytra(this));
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerActorArmor(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityPlayer entity)
    {
        IMorphing capability = entity.getCapability(MorphingProvider.MORPHING, null);
        ResourceLocation skin = capability.getSkin();

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

    @Override
    public void doRender(EntityPlayer entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.setupModel(entity);

        if (this.mainModel != null)
        {
            this.setHands(entity);
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    /**
     * Set hands postures
     */
    private void setHands(EntityPlayer player)
    {
        ItemStack rightItem = player.getHeldItemMainhand();
        ItemStack leftItem = player.getHeldItemOffhand();

        ModelBiped.ArmPose right = ModelBiped.ArmPose.EMPTY;
        ModelBiped.ArmPose left = ModelBiped.ArmPose.EMPTY;
        ModelCustom model = (ModelCustom) this.mainModel;

        if (!rightItem.func_190926_b())
        {
            right = ModelBiped.ArmPose.ITEM;

            if (player.getItemInUseCount() > 0)
            {
                EnumAction enumaction = rightItem.getItemUseAction();

                if (enumaction == EnumAction.BLOCK)
                {
                    right = ModelBiped.ArmPose.BLOCK;
                }
                else if (enumaction == EnumAction.BOW)
                {
                    right = ModelBiped.ArmPose.BOW_AND_ARROW;
                }
            }
        }

        if (!leftItem.func_190926_b())
        {
            left = ModelBiped.ArmPose.ITEM;

            if (player.getItemInUseCount() > 0)
            {
                EnumAction enumaction1 = leftItem.getItemUseAction();

                if (enumaction1 == EnumAction.BLOCK)
                {
                    left = ModelBiped.ArmPose.BLOCK;
                }
            }
        }

        model.rightPose = right;
        model.leftPose = left;
    }

    /**
     * Setup the model for player instance.
     *
     * This method is responsible for picking the right model and pose based
     * on player properties.
     */
    public void setupModel(EntityPlayer entity)
    {
        Map<String, ModelCustom> models = ModelCustom.MODELS;
        IMorphing capability = Morphing.get(entity);

        String key = models.containsKey(capability.getModel()) ? capability.getModel() : "steve";
        String pose = EntityUtils.poseForEntity(entity);

        ModelCustom model = models.get(key);

        if (model != null)
        {
            model.pose = model.model.getPose(pose);

            this.mainModel = model;
        }
    }

    /**
     * Make player a little bit smaller (so he looked like steve, and not like a
     * overgrown rodent).
     */
    @Override
    protected void preRenderCallback(EntityPlayer player, float partialTickTime)
    {
        float f = 0.935F;
        GlStateManager.scale(f, f, f);
    }

    /**
     * Taken from RenderPlayer
     *
     * This code is primarily changes the angle of the player while it's flying
     * an elytra. You know,
     */
    @Override
    protected void rotateCorpse(EntityPlayer player, float pitch, float yaw, float partialTicks)
    {
        super.rotateCorpse(player, pitch, yaw, partialTicks);

        if (player.isElytraFlying())
        {
            float f = player.getTicksElytraFlying() + partialTicks;
            float f1 = MathHelper.clamp_float(f * f / 100.0F, 0.0F, 1.0F);

            Vec3d vec3d = player.getLook(partialTicks);

            double d0 = player.motionX * player.motionX + player.motionZ * player.motionZ;
            double d1 = vec3d.xCoord * vec3d.xCoord + vec3d.zCoord * vec3d.zCoord;

            GlStateManager.rotate(f1 * (-90.0F - player.rotationPitch), 1.0F, 0.0F, 0.0F);

            if (d0 > 0.0D && d1 > 0.0D)
            {
                double d2 = (player.motionX * vec3d.xCoord + player.motionZ * vec3d.zCoord) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = player.motionX * vec3d.zCoord - player.motionZ * vec3d.xCoord;

                GlStateManager.rotate((float) (Math.signum(d3) * Math.acos(d2)) * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
            }
        }
    }

    /**
     * Render right hand
     */
    public void renderRightArm(EntityPlayer player)
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(this.getEntityTexture(player));
        this.mainModel.swingProgress = 0.0F;
        this.mainModel.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);

        GlStateManager.color(1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();

        for (ModelCustomRenderer arm : ((ModelCustom) this.mainModel).right)
        {
            arm.rotateAngleX = 0;
            arm.rotationPointX = -6F;
            arm.rotationPointY = 12.7F - (arm.limb.size[1] > 8 ? arm.limb.size[1] : arm.limb.size[1]);
            arm.rotationPointZ = 0;
            arm.render(0.0625F);
        }

        GlStateManager.disableBlend();
    }

    /**
     * Render left hand
     */
    public void renderLeftArm(EntityPlayer player)
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(this.getEntityTexture(player));
        this.mainModel.swingProgress = 0.0F;
        this.mainModel.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);

        GlStateManager.color(1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();

        for (ModelCustomRenderer arm : ((ModelCustom) this.mainModel).left)
        {
            arm.rotateAngleX = 0;
            arm.rotationPointX = 6F;
            arm.rotationPointY = 12.7F - (arm.limb.size[1] > 8 ? arm.limb.size[1] : arm.limb.size[1]);
            arm.rotationPointZ = 0;
            arm.render(0.0625F);
        }

        GlStateManager.disableBlend();
    }
}