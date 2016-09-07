package noname.blockbuster.client.model;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noname.blockbuster.client.model.parsing.Model;

/**
 * Custom Model class
 *
 * This class is responsible for managing available custom models that have
 * been loaded from config folder or from server and also render a custom model
 * itself.
 */
@SideOnly(Side.CLIENT)
public class ModelCustom extends ModelBase
{
    /**
     * Repository of custom models that are available for usage
     */
    public static Map<String, ModelCustom> MODELS = new HashMap<String, ModelCustom>();

    /**
     * Model data
     */
    public Model model;

    /**
     * Current pose
     */
    public Model.Pose pose;

    /**
     * Array of all limbs that has been parsed from JSON model
     */
    public ModelCustomRenderer[] limbs;

    /**
     * Array of limbs that has to be rendered (child limbs doesn't have to
     * be rendered, because they're getting render call from parent).
     */
    public ModelCustomRenderer[] renderable;

    public ModelCustomRenderer left;
    public ModelCustomRenderer right;

    /**
     * Initiate the model with the size of the texture
     */
    public ModelCustom(Model model)
    {
        this.model = model;
        this.textureWidth = model.texture[0];
        this.textureHeight = model.texture[1];
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        for (ModelRenderer limb : this.renderable)
        {
            limb.render(scale);
        }
    }

    /**
     * This method is responsible for setting gameplay wise features like head
     * looking, idle rotating (like arm swinging), swinging and swiping
     */
    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        for (ModelCustomRenderer limb : this.limbs)
        {
            boolean mirror = limb.limb.mirror;
            float factor = mirror ? -1 : 1;
            float PI = (float) Math.PI;

            /* Reseting the angles */
            this.applyLimbPose(limb);

            if (limb.limb.looking)
            {
                limb.rotateAngleX = headPitch * 0.017453292F;
                limb.rotateAngleY = netHeadYaw * 0.017453292F;
            }

            if (limb.limb.swinging)
            {
                float f = 0.8F;

                if (limb.limb.mirror)
                {
                    limb.rotateAngleX += MathHelper.cos(limbSwing * 0.6662F + PI) * 2.0F * limbSwingAmount * 0.5F / f;
                }
                else
                {
                    limb.rotateAngleX += MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
                }
            }

            if (limb.limb.idle)
            {
                limb.rotateAngleZ += (MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F) * factor;
                limb.rotateAngleX += (MathHelper.sin(ageInTicks * 0.067F) * 0.05F) * factor;
            }

            if (limb.limb.swiping && this.swingProgress > 0.0F)
            {
                float swing = this.swingProgress;
                float bodyY = MathHelper.sin(MathHelper.sqrt_float(swing) * PI * 2F) * 0.2F;

                if (limb.limb.mirror)
                {
                    bodyY *= -1.0F;
                }

                swing = 1.0F - swing;
                swing = swing * swing * swing;
                swing = 1.0F - swing;

                float sinSwing = MathHelper.sin(swing * PI);
                float sinSwing2 = MathHelper.sin(this.swingProgress * PI) * -(0.0F - 0.7F) * 0.75F;

                limb.rotateAngleX = limb.rotateAngleX - (sinSwing * 1.2F + sinSwing2);
                limb.rotateAngleY += bodyY * 2.0F;
                limb.rotateAngleZ += MathHelper.sin(this.swingProgress * PI) * -0.4F;
            }

            if (!limb.limb.holding.isEmpty())
            {
                EntityLivingBase entity = (EntityLivingBase) entityIn;
                ItemStack stack = limb.limb.holding.equals("right") ? entity.getHeldItemMainhand() : entity.getHeldItemOffhand();

                if (stack != null)
                {
                    limb.rotateAngleX = limb.rotateAngleX * 0.5F - PI / 10F;
                }
            }
        }
    }

    /**
     * Apply transform from current pose on given limb
     */
    private void applyLimbPose(ModelCustomRenderer limb)
    {
        limb.applyTransform(this.pose.limbs.get(limb.limb.name));
    }

    /**
     * Get renderer for an arm
     */
    public ModelCustomRenderer getRenderForArm(EnumHandSide side)
    {
        if (side == EnumHandSide.LEFT)
        {
            return this.left;
        }
        else if (side == EnumHandSide.RIGHT)
        {
            return this.right;
        }

        return null;
    }
}