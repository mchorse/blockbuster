package mchorse.blockbuster.client.render;

import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.textures.GifTexture;
import mchorse.blockbuster.common.OrientedBB;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.blockbuster_pack.morphs.SnowstormMorph;
import mchorse.mclib.utils.MatrixUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCustomModel extends RenderLivingBase<EntityLivingBase>
{
    /**
     * Last bind texture
     */
    public static ResourceLocation lastTexture;

    public static int tick;

    /**
     * Currently used morph 
     */
    public CustomMorph current;

    private int captured;

    public static void bindLastTexture(ResourceLocation location)
    {
        lastTexture = location;
        bindLastTexture();
    }

    public static void bindLastTexture()
    {
        if (lastTexture != null)
        {
            GifTexture.bindTexture(lastTexture, tick);
        }
    }

    public RenderCustomModel(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn)
    {
        super(renderManagerIn, null, shadowSizeIn);
    }

    /**
     * Get default texture for entity 
     */
    @Override
    protected ResourceLocation getEntityTexture(EntityLivingBase entity)
    {
        return this.mainModel == null ? null : ((ModelCustom) this.mainModel).model.defaultTexture;
    }

    protected boolean bindEntityTexture(EntityLivingBase entity)
    {
        if (this.mainModel != null && ((ModelCustom) this.mainModel).model.providesMtl)
        {
            ResourceLocation texture = this.getEntityTexture(entity);

            if (texture == null)
            {
                return true;
            }
        }

        return super.bindEntityTexture(entity);
    }

    /**
     * Override method in order to save the last texture. Used by OBJ 
     * renderer with materials to bind texture back 
     */
    @Override
    public void bindTexture(ResourceLocation location)
    {
        lastTexture = location;

        GifTexture.bindTexture(location, tick);
    }

    /**
     * Render morph's name only if the player is pointed at the entity
     */
    @Override
    protected boolean canRenderName(EntityLivingBase entity)
    {
        return super.canRenderName(entity) && entity.hasCustomName() && entity == this.renderManager.pointedEntity;
    }

    @Override
    public void doRender(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        tick = entity.ticksExisted;

        this.setupModel(entity, partialTicks);

        if (this.mainModel != null)
        {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }

        this.captured --;

        if (this.captured <= 0)
        {
            this.captured = 0;
            MatrixUtils.releaseMatrix();
        }
    }

    @Override
    protected void renderLivingAt(EntityLivingBase entityLivingBaseIn, double x, double y, double z)
    {
        super.renderLivingAt(entityLivingBaseIn, x, y, z);

        if (this.captured == 0)
        {
            MatrixUtils.captureMatrix();
        }

        this.captured ++;
    }

    /**
     * Setup the model for player instance.
     *
     * This method is responsible for picking the right model and pose based
     * on player properties.
     */
    public void setupModel(EntityLivingBase entity, float partialTicks)
    {
        Map<String, ModelCustom> models = ModelCustom.MODELS;
        ModelCustom model = null;
        ModelPose pose = null;

        if (this.current != null)
        {
            model = models.get(this.current.getKey());
            pose = this.current.getPose(entity, partialTicks);
        }

        if (model != null)
        {
            if (pose == null)
            {
                pose = model.model.getPose("standing");
            }

            model.materials = this.current.materials;
            model.shapes = this.current.getShapesForRendering(partialTicks);
            model.pose = pose;
            model.current = this.current;
            this.mainModel = model;
        }
    }

    /**
     * Make player a little bit smaller (so he looked like steve, and not like an 
     * overgrown rodent).
     */
    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTickTime)
    {
        Model model = ((ModelCustom) this.mainModel).model;
        float scale = this.current == null ? 1.0F : this.current.scale;

        GlStateManager.scale(model.scale[0] * scale, model.scale[1] * scale, model.scale[2] * scale);
    }

    /**
     * Taken from RenderPlayer
     *
     * This code is primarily changes the angle of the player while it's flying
     * an elytra. You know?
     */
    @Override
    protected void applyRotations(EntityLivingBase entity, float pitch, float yaw, float partialTicks)
    {
        Matrix4f parent = new Matrix4f(MatrixUtils.matrix);
        Matrix4f matrix4f = MatrixUtils.readModelView(SnowstormMorph.getMatrix());

        parent.invert();
        parent.mul(matrix4f);

        Vector4f zero = SnowstormMorph.calculateGlobal(parent, entity, 0, 0, 0, partialTicks);

        for (List<OrientedBB> obbs : this.current.orientedBBlimbs.values())
        {
            for(OrientedBB obb : obbs)
            {
                obb.center.x = zero.x;
                obb.center.y = zero.y;
                obb.center.z = zero.z;
                
                obb.buildCorners();
            }
        }

        if (entity.isEntityAlive() && entity.isPlayerSleeping())
        {
            /* Nap time! */
            GlStateManager.rotate(((EntityPlayer) entity).getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(this.getDeathMaxRotation(entity), 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
        }
        else if (entity.isElytraFlying())
        {
            /* Elytra rotation */
            super.applyRotations(entity, pitch, yaw, partialTicks);

            float f = entity.getTicksElytraFlying() + partialTicks;
            float f1 = MathHelper.clamp(f * f / 100.0F, 0.0F, 1.0F);

            Vec3d vec3d = entity.getLook(partialTicks);

            double d0 = entity.motionX * entity.motionX + entity.motionZ * entity.motionZ;
            double d1 = vec3d.x * vec3d.x + vec3d.z * vec3d.z;

            GlStateManager.rotate(f1 * (-90.0F - entity.rotationPitch), 1.0F, 0.0F, 0.0F);

            if (d0 > 0.0D && d1 > 0.0D)
            {
                double d2 = (entity.motionX * vec3d.x + entity.motionZ * vec3d.z) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = entity.motionX * vec3d.z - entity.motionZ * vec3d.x;

                GlStateManager.rotate((float) (Math.signum(d3) * Math.acos(d2)) * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
            }
        }
        else
        {
            super.applyRotations(entity, pitch, yaw, partialTicks);
        }
    }

    /**
     * Render right hand 
     */
    public void renderRightArm(EntityPlayer player)
    {
        ResourceLocation texture = this.getEntityTexture(player);

        if (texture != null)
        {
            GifTexture.bindTexture(texture, player.ticksExisted);
        }

        this.mainModel.swingProgress = 0.0F;
        this.mainModel.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);

        GlStateManager.color(1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();

        for (ModelCustomRenderer arm : ((ModelCustom) this.mainModel).right)
        {
            arm.rotateAngleX = 0;
            arm.rotationPointX = -6;
            arm.rotationPointY = 13.8F - (arm.limb.size[1] > 8 ? arm.limb.size[1] : arm.limb.size[1] + 2);
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
        ResourceLocation texture = this.getEntityTexture(player);

        if (texture != null)
        {
            GifTexture.bindTexture(texture, player.ticksExisted);
        }

        this.mainModel.swingProgress = 0.0F;
        this.mainModel.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);

        GlStateManager.color(1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();

        for (ModelCustomRenderer arm : ((ModelCustom) this.mainModel).left)
        {
            arm.rotateAngleX = 0;
            arm.rotationPointX = 6;
            arm.rotationPointY = 13.8F - (arm.limb.size[1] > 8 ? arm.limb.size[1] : arm.limb.size[1] + 2F);
            arm.rotationPointZ = 0;
            arm.render(0.0625F);
        }

        GlStateManager.disableBlend();
    }
}