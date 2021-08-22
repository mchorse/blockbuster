package mchorse.blockbuster_pack.morphs;

import com.google.common.base.Objects;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelHandler;
import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.api.formats.obj.ShapeKey;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.render.RenderCustomModel;
import mchorse.blockbuster.common.OrientedBB;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster_pack.client.render.layers.LayerBodyPart;
import mchorse.mclib.utils.MatrixUtils;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.Interpolation;
import mchorse.mclib.utils.NBTUtils;
import mchorse.mclib.utils.resources.RLUtils;
import mchorse.metamorph.api.EntityUtils;
import mchorse.metamorph.api.models.IMorphProvider;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.utils.Animation;
import mchorse.metamorph.api.morphs.utils.IAnimationProvider;
import mchorse.metamorph.api.morphs.utils.IMorphGenerator;
import mchorse.metamorph.api.morphs.utils.ISyncableMorph;
import mchorse.metamorph.bodypart.BodyPart;
import mchorse.metamorph.bodypart.BodyPartManager;
import mchorse.metamorph.bodypart.IBodyPartProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom morph
 *
 * This is a morph which allows players to use Blockbuster's custom 
 * models as morphs.
 */
public class CustomMorph extends AbstractMorph implements IBodyPartProvider, IAnimationProvider, ISyncableMorph, IMorphGenerator
{
    /**
     * OrientedBoundingBoxes List by limbs
     */
    public Map<ModelLimb, List<OrientedBB>> orientedBBlimbs;
    
    /**
     * Morph's model
     */
    public Model model;

    /**
     * Current pose 
     */
    protected ModelPose pose;

    /**
     * Current custom pose
     */
    public String currentPose = "";

    /**
     * Apply current pose on sneaking
     */
    public boolean currentPoseOnSneak = false;

    /**
     * Skin for custom morph
     */
    public ResourceLocation skin;

    /**
     * Custom pose 
     */
    public ModelProperties customPose = null;

    /**
     * Map of textures designated to specific OBJ materials 
     */
    public Map<String, ResourceLocation> materials = new HashMap<String, ResourceLocation>();

    /**
     * Scale of this model
     */
    public float scale = 1F;

    /**
     * Scale of this model in morph GUIs
     */
    public float scaleGui = 1F;

    /**
     * Whether this image morph should cut out background color
     */
    public boolean keying;

    /**
     * Animation details
     */
    public PoseAnimation animation = new PoseAnimation();

    /**
     * Body part manager 
     */
    public BodyPartManager parts = new BodyPartManager();

    /**
     * Cached key value 
     */
    private String key;

    private long lastUpdate;

    /* Cape variables */
    public double prevCapeX;
    public double prevCapeY;
    public double prevCapeZ;
    public double capeX;
    public double capeY;
    public double capeZ;

    /**
     * Make hands true!
     */
    public CustomMorph()
    {
        super();

        this.settings = this.settings.copy();
        this.settings.hands = true;
    }
    
    /**
     * This method fills the obbsLimb Map with data from the model blueprint.
     * @param force if true it ignores that orientedBBlimbs might already be filled
     */
    public void fillObbs(boolean force)
    {
        if(this.orientedBBlimbs == null || force)
        {
            this.orientedBBlimbs = new HashMap<>();

            if (this.model != null)
            {
                for(ModelLimb limb : this.model.limbs.values())
                {
                    List<OrientedBB> newObbs = new ArrayList<>();

                    for(OrientedBB obb : limb.obbs)
                    {
                        newObbs.add(obb.clone());
                    }

                    this.orientedBBlimbs.put(limb, newObbs);
                }
            }
        }
    }

    @Override
    public void pause(AbstractMorph previous, int offset)
    {
        this.animation.pause(offset);

        if (previous instanceof IMorphProvider)
        {
            previous = ((IMorphProvider) previous).getMorph();
        }

        if (previous instanceof CustomMorph)
        {
            CustomMorph custom = (CustomMorph) previous;
            ModelPose pose = custom.getCurrentPose();

            if (!this.animation.ignored)
            {
                if (custom.animation.isInProgress() && pose != null)
                {
                    this.animation.last = this.convertProp(custom.animation.calculatePose(pose, 1).copy());
                }
                else
                {
                    this.animation.last = this.convertProp(pose);
                }
            }
            else if (custom.customPose != null)
            {
                this.customPose = custom.customPose;
            }
            else if (!custom.currentPose.isEmpty())
            {
                this.customPose = null;
                this.currentPose = custom.currentPose;
            }

            if (pose != null)
            {
                this.animation.mergeShape(pose.shapes);
            }
        }

        this.parts.pause(previous, offset);
    }

    @Override
    public boolean isPaused()
    {
        return this.animation.paused;
    }

    @Override
    public Animation getAnimation()
    {
        return this.animation;
    }

    @Override
    public boolean canGenerate()
    {
        return this.animation.isInProgress();
    }

    @Override
    public AbstractMorph genCurrentMorph(float partialTicks)
    {
        CustomMorph morph = (CustomMorph) this.copy();

        if (this.getCurrentPose() != null)
        {
            morph.customPose = this.convertProp(this.animation.calculatePose(this.getCurrentPose(), partialTicks));
            morph.customPose.shapes.clear();
            morph.customPose.shapes.addAll(this.getShapesForRendering(partialTicks));
        }

        morph.animation.duration = this.animation.progress;

        morph.parts.parts.clear();

        for (BodyPart part : this.parts.parts)
        {
            morph.parts.parts.add(part.genCurrentBodyPart(this, partialTicks));
        }

        return morph;
    }

    public List<ShapeKey> getShapesForRendering(float partialTick)
    {
        if (this.model.shapes.isEmpty())
        {
            return this.getCurrentPose().shapes;
        }

        if (this.animation.isInProgress())
        {
            return this.animation.calculateShapes(this, partialTick);
        }

        return this.getCurrentPose().shapes;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getSubclassDisplayName()
    {
        if (this.model != null)
        {
            return this.model.name;
        }

        return super.getSubclassDisplayName();
    }

    public void changeModel(String model)
    {
        if (Blockbuster.proxy.models.models.get(model) == null)
        {
            return;
        }

        this.name = "blockbuster." + model;
        this.key = null;
        this.model = Blockbuster.proxy.models.models.get(model);
        
        fillObbs(true);

        if (this.customPose != null)
        {
            this.customPose.updateLimbs(this.model, false);
        }
    }

    @Override
    public BodyPartManager getBodyPart()
    {
        return this.parts;
    }

    /**
     * Get a pose for rendering
     */
    public ModelPose getPose(EntityLivingBase target, float partialTicks)
    {
        return this.getPose(target, false, partialTicks);
    }

    /**
     * Get a pose for rendering
     */
    public ModelPose getPose(EntityLivingBase target, boolean ignoreCustom, float partialTicks)
    {
        ModelPose pose = this.getCurrentPose(target, ignoreCustom);

        if (this.animation.isInProgress() && pose != null)
        {
            return this.animation.calculatePose(pose, partialTicks);
        }

        return pose;
    }

    private ModelPose getCurrentPose(EntityLivingBase target, boolean ignoreCustom)
    {
        if (this.customPose != null && !ignoreCustom)
        {
            if (this.currentPoseOnSneak && target.isSneaking() || !this.currentPoseOnSneak)
            {
                return this.customPose;
            }
        }

        String poseName = EntityUtils.getPose(target, this.currentPose, this.currentPoseOnSneak);

        if (target instanceof EntityActor)
        {
            poseName = ((EntityActor) target).isMounted ? "riding" : poseName;
        }

        return this.model == null ? null : this.model.getPose(poseName);
    }

    public ModelPose getCurrentPose()
    {
        return this.customPose != null ? this.customPose : (this.model == null ? null : this.model.getPose(this.currentPose));
    }

    public String getKey()
    {
        if (this.key == null)
        {
            this.key = this.name.replaceAll("^blockbuster\\.", "");
        }

        return this.key;
    }

    public void updateModel()
    {
        this.updateModel(false);
    }

    public void updateModel(boolean force)
    {
        if (this.lastUpdate < ModelHandler.lastUpdate || force)
        {
            this.lastUpdate = ModelHandler.lastUpdate;
            this.model = Blockbuster.proxy.models.models.get(this.getKey());

            fillObbs(true);

            if (this.customPose != null)
            {
                this.customPose.updateLimbs(this.model, false);
            }
        }
    }

    /**
     * Render actor morph on the screen
     *
     * This method overrides parent class method to take in account current
     * morph's skin.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        if(this.model != null)
        {
            fillObbs(false);
        }
        
        this.updateModel();

        ModelCustom model = ModelCustom.MODELS.get(this.getKey());

        if (model != null && this.model != null)
        {
            Model data = model.model;

            if (data != null && (data.defaultTexture != null || data.providesMtl || this.skin != null))
            {
                this.parts.initBodyParts();

                model.materials = this.materials;
                model.pose = this.getPose(player, Minecraft.getMinecraft().getRenderPartialTicks());
                model.swingProgress = 0;

                ResourceLocation texture = this.skin == null ? data.defaultTexture : this.skin;
                RenderCustomModel.bindLastTexture(texture);

                this.drawModel(model, player, x, y, scale * data.scaleGui * this.scaleGui, alpha);
            }
        }
        else
        {
            FontRenderer font = Minecraft.getMinecraft().fontRenderer;
            int width = font.getStringWidth(this.name);
            String error = I18n.format("blockbuster.morph_error");

            font.drawStringWithShadow(error, x - font.getStringWidth(error) / 2, y - (int) (font.FONT_HEIGHT * 2.5), 0xff2222);
            font.drawStringWithShadow(this.name, x - width / 2, y - font.FONT_HEIGHT, 0xffffff);
        }
    }

    /**
     * Draw a {@link ModelBase} without using the {@link RenderManager} (which 
     * adds a lot of useless transformations and stuff to the screen rendering).
     */
    @SideOnly(Side.CLIENT)
    private void drawModel(ModelCustom model, EntityPlayer player, int x, int y, float scale, float alpha)
    {
        float factor = 0.0625F;

        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 50.0F);
        GlStateManager.scale((-scale), scale, scale);
        GlStateManager.rotate(45.0F, -1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, -1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

        RenderHelper.enableStandardItemLighting();

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();

        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.translate(0.0F, -1.501F, 0.0F);

        GlStateManager.enableAlpha();

        model.setLivingAnimations(player, 0, 0, 0);
        model.setRotationAngles(0, 0, player.ticksExisted, 0, 0, factor, player);

        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);

        model.render(player, 0, 0, 0, 0, 0, factor);
        LayerBodyPart.renderBodyParts(player, this, model, 0F, factor);

        GlStateManager.disableDepth();

        GlStateManager.disableRescaleNormal();
        GlStateManager.disableAlpha();
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();

        RenderHelper.disableStandardItemLighting();

        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderHand(EntityPlayer player, EnumHand hand)
    {
        this.updateModel();

        RenderCustomModel renderer = ClientProxy.actorRenderer;

        /* This */
        renderer.current = this;
        renderer.setupModel(player, Minecraft.getMinecraft().getRenderPartialTicks());

        if (renderer.getMainModel() == null)
        {
            return false;
        }

        ResourceLocation location = this.skin != null ? this.skin : (this.model != null ? this.model.defaultTexture : null);

        if (location != null)
        {
            renderer.bindTexture(location);
        }

        if (hand.equals(EnumHand.MAIN_HAND))
        {
            renderer.renderRightArm(player);
        }
        else
        {
            renderer.renderLeftArm(player);
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if(this.model != null)
        {
            fillObbs(false);
        }
        
        this.updateModel();

        if (this.model != null)
        {
            this.parts.initBodyParts();

            RenderCustomModel render = ClientProxy.actorRenderer;

            render.current = this;
            render.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
        else
        {
            Minecraft mc = Minecraft.getMinecraft();
            FontRenderer font = mc.fontRenderer;
            RenderManager manager = mc.getRenderManager();

            if (Blockbuster.modelBlockRenderMissingName.get() || mc.gameSettings.showDebugInfo)
            {
                MatrixUtils.Transformation transformation = MatrixUtils.extractTransformations(MatrixUtils.matrix, MatrixUtils.readModelView(), MatrixUtils.MatrixMajor.COLUMN);

                float invSx = (transformation.scale.m00 != 0) ? 1 / transformation.scale.m00 : 0;
                float invSy = (transformation.scale.m11 != 0) ? 1 / transformation.scale.m11 : 0;
                float invSz = (transformation.scale.m22 != 0) ? 1 / transformation.scale.m22 : 0;

                try
                {
                    transformation.rotation.invert();
                }
                catch (Exception e)
                { }

                Vector3f rot = transformation.getRotation(MatrixUtils.Transformation.RotationOrder.XYZ);

                GlStateManager.scale(invSx, invSy, invSz);
                GlStateManager.rotate(rot.z, 0, 0, 1);
                GlStateManager.rotate(rot.y, 0, 1, 0);
                GlStateManager.rotate(rot.x, 1, 0, 0);

                EntityRenderer.drawNameplate(font, this.getKey(), (float) x, (float) y + 1, (float) z, 0, manager.playerViewY, manager.playerViewX, mc.gameSettings.thirdPersonView == 2, entity.isSneaking());
            }
        }
    }

    /**
     * Update the player based on its morph abilities and properties. This 
     * method also responsible for updating AABB size. 
     */
    @Override
    public void update(EntityLivingBase target)
    {
        this.updateModel();
        this.animation.update();
        this.parts.updateBodyLimbs(this, target);

        super.update(target);

        if (target.world.isRemote)
        {
            this.updateCapeVariables(target);
        }
    }

    @SideOnly(Side.CLIENT)
    private void updateCapeVariables(EntityLivingBase target)
    {
        this.prevCapeX = this.capeX;
        this.prevCapeY = this.capeY;
        this.prevCapeZ = this.capeZ;

        double dX = target.posX - this.capeX;
        double dY = target.posY - this.capeY;
        double dZ = target.posZ - this.capeZ;
        double multiplier = 0.25D;

        if (Math.abs(dX) > 10)
        {
            this.capeX = target.posX;
            this.prevCapeX = this.capeX;
        }

        if (Math.abs(dY) > 10)
        {
            this.capeY = target.posY;
            this.prevCapeY = this.capeY;
        }

        if (Math.abs(dZ) > 10)
        {
            this.capeZ = target.posZ;
            this.prevCapeZ = this.capeZ;
        }

        this.capeX += dX * multiplier;
        this.capeY += dY * multiplier;
        this.capeZ += dZ * multiplier;
    }

    @Override
    protected void updateUserHitbox(EntityLivingBase target)
    {
        this.pose = this.getPose(target, 0);

        if (this.pose != null)
        {
            float[] pose = this.pose.size;

            this.updateSize(target, pose[0] * this.scale, pose[1] * this.scale);
        }
    }

    @Override
    public float getWidth(EntityLivingBase target)
    {
        return (this.pose != null ? this.pose.size[0] : 0.6F) * this.scale;
    }

    @Override
    public float getHeight(EntityLivingBase target)
    {
        return (this.pose != null ? this.pose.size[1] : 1.8F) * this.scale;
    }

    /**
     * Check whether given object equals to this object
     *
     * This method is responsible for checking whether other {@link CustomMorph}
     * has the same skin as this morph. This method plays very big role in
     * morphing and morph acquiring.
     */
    @Override
    public boolean equals(Object object)
    {
        boolean result = super.equals(object);

        if (object instanceof CustomMorph)
        {
            CustomMorph morph = (CustomMorph) object;

            result = result && Objects.equal(this.currentPose, morph.currentPose);
            result = result && Objects.equal(this.skin, morph.skin);
            result = result && Objects.equal(this.customPose, morph.customPose);
            result = result && this.currentPoseOnSneak == morph.currentPoseOnSneak;
            result = result && this.scale == morph.scale;
            result = result && this.scaleGui == morph.scaleGui;
            result = result && this.materials.equals(morph.materials);
            result = result && this.parts.equals(morph.parts);
            result = result && this.keying == morph.keying;
            result = result && this.animation.equals(morph.animation);

            return result;
        }

        return result;
    }

    @Override
    public boolean canMerge(AbstractMorph morph)
    {
        if (morph instanceof CustomMorph)
        {
            CustomMorph custom = (CustomMorph) morph;

            this.mergeBasic(morph);

            /* Don't suddenly end the animation in progress, interpolate */
            if (!custom.animation.ignored)
            {
                /* If the last pose is null, it might case a first cycle freeze.
                 * this should fix it. */
                ModelPose pose = this.getCurrentPose();

                if (this.animation.isInProgress() && pose != null)
                {
                    this.animation.last = this.convertProp(this.animation.calculatePose(pose, 1).copy());
                }
                else
                {
                    this.animation.last = this.convertProp(pose);
                }
                
                this.currentPose = custom.currentPose;
                this.customPose = custom.customPose == null ? null : custom.customPose.copy();
                this.animation.merge(custom.animation);

                if (pose != null)
                {
                    this.animation.mergeShape(pose.shapes);
                }
            }
            else
            {
                this.animation.ignored = true;
            }

            this.key = null;
            this.name = custom.name;
            this.skin = RLUtils.clone(custom.skin);
            this.currentPoseOnSneak = custom.currentPoseOnSneak;
            this.scale = custom.scale;
            this.scaleGui = custom.scaleGui;
            this.materials.clear();

            for (Map.Entry<String, ResourceLocation> entry : custom.materials.entrySet())
            {
                this.materials.put(entry.getKey(), RLUtils.clone(entry.getValue()));
            }

            this.parts.merge(custom.parts);
            this.model = custom.model;

            return true;
        }

        return super.canMerge(morph);
    }

    @Override
    public void afterMerge(AbstractMorph morph)
    {
        super.afterMerge(morph);

        if (!(morph instanceof IMorphProvider))
        {
            return;
        }

        AbstractMorph destination = ((IMorphProvider) morph).getMorph();

        if (destination instanceof IBodyPartProvider)
        {
            this.recursiveAfterMerge(this, (IBodyPartProvider) destination);
        }

        if (destination instanceof CustomMorph)
        {
            this.copyPoseForAnimation(this, (CustomMorph) destination);
        }
    }

    private void recursiveAfterMerge(IBodyPartProvider target, IBodyPartProvider destination)
    {
        for (int i = 0, c = target.getBodyPart().parts.size(); i < c; i++)
        {
            if (i >= destination.getBodyPart().parts.size())
            {
                break;
            }

            AbstractMorph a = target.getBodyPart().parts.get(i).morph.get();
            AbstractMorph b = destination.getBodyPart().parts.get(i).morph.get();

            if (a instanceof IBodyPartProvider && b instanceof IBodyPartProvider)
            {
                this.recursiveAfterMerge((IBodyPartProvider) a, (IBodyPartProvider) b);
            }

            if (a instanceof CustomMorph && b instanceof CustomMorph)
            {
                this.copyPoseForAnimation((CustomMorph) a, (CustomMorph) b);
            }
        }
    }

    private void copyPoseForAnimation(CustomMorph target, CustomMorph destination)
    {
        /* If the last pose is null, it might case a first cycle freeze.
         * this should fix it. */
        ModelPose pose = destination.getCurrentPose();

        target.animation.progress = 0;

        if (destination.animation.isInProgress() && pose != null)
        {
            target.animation.last = this.convertProp(destination.animation.calculatePose(pose, 1));
        }
        else
        {
            target.animation.last = this.convertProp(pose);
        }
    }

    @Override
    public void reset()
    {
        super.reset();

        this.key = null;
        this.parts.reset();
        this.animation.reset();
        this.scale = this.scaleGui = 1F;
    }

    @Override
    public AbstractMorph create()
    {
        return new CustomMorph();
    }

    @Override
    public void copy(AbstractMorph from)
    {
        super.copy(from);

        if (from instanceof CustomMorph)
        {
            CustomMorph morph = (CustomMorph) from;

            this.skin = RLUtils.clone(morph.skin);

            this.currentPose = morph.currentPose;
            this.currentPoseOnSneak = morph.currentPoseOnSneak;
            this.scale = morph.scale;
            this.scaleGui = morph.scaleGui;
            this.keying = morph.keying;

            if (morph.customPose != null)
            {
                this.customPose = morph.customPose.copy();
            }

            if (!morph.materials.isEmpty())
            {
                this.materials.clear();

                for (Map.Entry<String, ResourceLocation> entry : morph.materials.entrySet())
                {
                    this.materials.put(entry.getKey(), RLUtils.clone(entry.getValue()));
                }
            }

            this.model = morph.model;
            this.parts.copy(morph.parts);
            this.animation.copy(morph.animation);
        }
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        if (this.skin != null)
        {
            tag.setTag("Skin", RLUtils.writeNbt(this.skin));
        }

        if (!this.currentPose.isEmpty()) tag.setString("Pose", this.currentPose);
        if (this.currentPoseOnSneak) tag.setBoolean("Sneak", this.currentPoseOnSneak);
        if (this.scale != 1F) tag.setFloat("Scale", this.scale);
        if (this.scaleGui != 1F) tag.setFloat("ScaleGUI", this.scaleGui);
        if (this.keying) tag.setBoolean("Keying", this.keying);

        if (this.customPose != null)
        {
            tag.setTag("CustomPose", this.customPose.toNBT(new NBTTagCompound()));
        }

        if (!this.materials.isEmpty())
        {
            NBTTagCompound materials = new NBTTagCompound();

            for (Map.Entry<String, ResourceLocation> entry : this.materials.entrySet())
            {
                materials.setTag(entry.getKey(), RLUtils.writeNbt(entry.getValue()));
            }

            tag.setTag("Materials", materials);
        }

        NBTTagList bodyParts = this.parts.toNBT();

        if (bodyParts != null)
        {
            tag.setTag("BodyParts", bodyParts);
        }

        NBTTagCompound animation = this.animation.toNBT();

        if (!animation.hasNoTags())
        {
            tag.setTag("Animation", animation);
        }
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        String name = this.name;

        super.fromNBT(tag);

        /* Replace the current model */
        if (!name.equals(this.name))
        {
            Model model = Blockbuster.proxy.models.models.get(this.getKey());

            this.model = model == null ? this.model : model;
        }

        if (tag.hasKey("Skin"))
        {
            this.skin = RLUtils.create(tag.getTag("Skin"));
        }

        this.currentPose = tag.getString("Pose");
        this.currentPoseOnSneak = tag.getBoolean("Sneak");
        if (tag.hasKey("Scale", NBT.TAG_ANY_NUMERIC)) this.scale = tag.getFloat("Scale");
        if (tag.hasKey("ScaleGUI", NBT.TAG_ANY_NUMERIC)) this.scaleGui = tag.getFloat("ScaleGUI");
        if (tag.hasKey("Keying")) this.keying = tag.getBoolean("Keying");

        if (tag.hasKey("CustomPose", 10))
        {
            this.customPose = new ModelProperties();
            this.customPose.fromNBT(tag.getCompoundTag("CustomPose"));
        }

        if (tag.hasKey("Materials", 10))
        {
            NBTTagCompound materials = tag.getCompoundTag("Materials");

            this.materials.clear();

            for (String key : materials.getKeySet())
            {
                this.materials.put(key, RLUtils.create(materials.getTag(key)));
            }
        }

        if (tag.hasKey("BodyParts", 9))
        {
            this.parts.fromNBT(tag.getTagList("BodyParts", 10));
        }

        if (tag.hasKey("Animation"))
        {
            this.animation.fromNBT(tag.getCompoundTag("Animation"));
        }
    }

    public ModelProperties convertProp(ModelPose pose)
    {
        if (pose instanceof ModelProperties)
        {
            return (ModelProperties) pose;
        }

        NBTTagCompound tag = pose.toNBT(new NBTTagCompound());

        ModelProperties props = new ModelProperties();

        props.fromNBT(tag);

        if (this.model != null)
        {
            props.updateLimbs(this.model, true);
        }

        return props;
    }

    /**
     * Animation details 
     */
    public static class PoseAnimation extends Animation
    {
        public Map<String, LimbProperties> lastProps;
        public List<ShapeKey> lastShapes = new ArrayList<ShapeKey>();
        public ModelProperties last;
        public ModelProperties pose = new ModelProperties();

        private List<ShapeKey> temporaryShapes = new ArrayList<ShapeKey>();

        @Override
        public void merge(Animation animation)
        {
            super.merge(animation);
            this.pose.limbs.clear();
        }

        public void mergeShape(List<ShapeKey> shapes)
        {
            this.lastShapes.clear();
            this.lastShapes.addAll(shapes);
        }

        public List<ShapeKey> calculateShapes(CustomMorph morph, float partialTicks)
        {
            float factor = this.getFactor(partialTicks);

            this.temporaryShapes.clear();

            for (ShapeKey key : morph.getCurrentPose().shapes)
            {
                ShapeKey last = null;

                for (ShapeKey previous : this.lastShapes)
                {
                    if (previous.name.equals(key.name))
                    {
                        last = previous;

                        break;
                    }
                }

                this.temporaryShapes.add(new ShapeKey(key.name, this.interp.interpolate(last == null ? 0 : last.value, key.value, factor), key.relative));
            }

            for (ShapeKey key : this.lastShapes)
            {
                ShapeKey last = null;

                for (ShapeKey previous : this.temporaryShapes)
                {
                    if (previous.name.equals(key.name))
                    {
                        last = previous;

                        break;
                    }
                }

                if (last == null)
                {
                    this.temporaryShapes.add(new ShapeKey(key.name, this.interp.interpolate(key.value, 0, factor), key.relative));
                }
            }

            return this.temporaryShapes;
        }

        @Override
        public boolean isInProgress()
        {
            return super.isInProgress() && this.last != null;
        }

        public ModelPose calculatePose(ModelPose current, float partialTicks)
        {
            float factor = this.getFactor(partialTicks);

            for (Map.Entry<String, ModelTransform> entry : current.limbs.entrySet())
            {
                String key = entry.getKey();
                ModelTransform trans = this.pose.limbs.get(key);
                ModelTransform last = this.last.limbs.get(key);

                if (last == null)
                {
                    continue;
                }

                if (trans == null)
                {
                    trans = new LimbProperties();
                    this.pose.limbs.put(key, trans);
                }

                trans.interpolate(last, entry.getValue(), factor, this.interp);
            }

            for (int i = 0; i < this.pose.size.length; i++)
            {
                this.pose.size[i] = this.interp.interpolate(this.last.size[i], current.size[i], factor);
            }

            return this.pose;
        }
    }

    public static class LimbProperties extends ModelTransform
    {
        public boolean fixed = false;
        public float glow = 0.0f;
        public Color color = new Color(1f, 1f, 1f, 1f);

        @Override
        public boolean isDefault()
        {
            return false;
        }

        @Override
        public void copy(ModelTransform transform)
        {
            super.copy(transform);
            if (transform instanceof LimbProperties)
            {
                LimbProperties prop = (LimbProperties) transform;
                this.fixed = prop.fixed;
                this.glow = prop.glow;
                this.color.copy(prop.color);
            }
        }
        
        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof LimbProperties)
            {
                LimbProperties prop = (LimbProperties) obj;
                return super.equals(obj) && this.fixed == prop.fixed && Math.abs(this.glow - prop.glow) < 0.0001f && this.color.equals(prop.color);
            }

            return false;
        }
        
        @Override
        public LimbProperties clone()
        {
            LimbProperties b = new LimbProperties();
            b.copy(this);
            return b;
        }
        
        @Override
        public void fromNBT(NBTTagCompound tag)
        {
            super.fromNBT(tag);

            if (tag.hasKey("F", NBT.TAG_BYTE)) this.fixed = tag.getBoolean("F");
            if (tag.hasKey("G", NBT.TAG_FLOAT)) this.glow = tag.getFloat("G");
            if (tag.hasKey("C", NBT.TAG_INT)) this.color.set(tag.getInteger("C"));
        }
        
        @Override
        public NBTTagCompound toNBT()
        {
            NBTTagCompound tag = new NBTTagCompound();

            if (!this.isDefault())
            {
                if (!equalFloatArray(DEFAULT.translate, this.translate)) tag.setTag("P", NBTUtils.writeFloatList(new NBTTagList(), this.translate));
                if (!equalFloatArray(DEFAULT.scale, this.scale)) tag.setTag("S", NBTUtils.writeFloatList(new NBTTagList(), this.scale));
                if (!equalFloatArray(DEFAULT.rotate, this.rotate)) tag.setTag("R", NBTUtils.writeFloatList(new NBTTagList(), this.rotate));
                if (this.fixed) tag.setBoolean("F", this.fixed);
                if (this.glow > 0.0001f) tag.setFloat("G", this.glow);
                if (this.color.getRGBAColor() != 0xFFFFFFFF) tag.setInteger("C", this.color.getRGBAColor());
            }

            return tag;
        }
        
        @Override
        public void interpolate(ModelTransform a, ModelTransform b, float x, Interpolation interp)
        {
            super.interpolate(a, b, x, interp);
            
            boolean fixed = false;
            float glow = 0.0f;
            float cr, cg, cb, ca;
            cr = cg = cb = ca = 1.0f;
            
            if (a instanceof LimbProperties)
            {
                LimbProperties l = (LimbProperties) a;
                fixed = l.fixed;
                glow = l.glow;
                cr = l.color.r;
                cg = l.color.g;
                cb = l.color.b;
                ca = l.color.a;
            }
            
            if (b instanceof LimbProperties)
            {
                LimbProperties l = (LimbProperties) b;
                fixed = l.fixed;
                glow = interp.interpolate(glow, l.glow, x);
                cr = interp.interpolate(cr, l.color.r, x);
                cg = interp.interpolate(cg, l.color.g, x);
                cb = interp.interpolate(cb, l.color.b, x);
                ca = interp.interpolate(ca, l.color.a, x);
            }
            else
            {
                fixed = false;
                glow = interp.interpolate(glow, 0.0f, x);
                cr = interp.interpolate(cr, 1.0f, x);
                cg = interp.interpolate(cg, 1.0f, x);
                cb = interp.interpolate(cb, 1.0f, x);
                ca = interp.interpolate(ca, 1.0f, x);
            }
            
            this.fixed = fixed;
            this.glow = glow;
            this.color.set(cr, cg, cb, ca);
        }

        public void applyGlow(float lastX, float lastY)
        {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, Interpolation.LINEAR.interpolate(lastX, 240, this.glow), Interpolation.LINEAR.interpolate(lastY, 240, this.glow));
        }
    }
    
    public static class ModelProperties extends ModelPose
    {
        @Override
        public ModelProperties copy()
        {
            ModelProperties b = new ModelProperties();

            b.size = new float[] {this.size[0], this.size[1], this.size[2]};

            for (Map.Entry<String, ModelTransform> entry : this.limbs.entrySet())
            {
                b.limbs.put(entry.getKey(), entry.getValue().clone());
            }

            for (ShapeKey key : this.shapes)
            {
                b.shapes.add(key.copy());
            }

            return b;
        }

        public void updateLimbs(Model model, boolean override)
        {
            if (model == null)
            {
                return;
            }

            for (Map.Entry<String, ModelLimb> entry : model.limbs.entrySet())
            {
                ModelLimb limb = entry.getValue();
                LimbProperties prop = (LimbProperties) this.limbs.get(entry.getKey());
                boolean newProp = false;

                if (prop == null)
                {
                    prop = new LimbProperties();
                    newProp = true;

                    this.limbs.put(entry.getKey(), prop);
                }

                if (newProp || override)
                {
                    prop.color.set(limb.color[0], limb.color[1], limb.color[2], limb.opacity);
                    prop.glow = limb.lighting ? 0.0f : 1.0f;
                }
            }
        }

        @Override
        public void fillInMissing(ModelPose pose)
        {
            for (Map.Entry<String, ModelTransform> entry : pose.limbs.entrySet())
            {
                String key = entry.getKey();

                if (!this.limbs.containsKey(key))
                {
                    LimbProperties limb = new LimbProperties();
                    limb.copy(entry.getValue());
                    this.limbs.put(key, limb);
                }
            }
        }

        @Override
        public void fromNBT(NBTTagCompound tag)
        {
            if (tag.hasKey("Size", Constants.NBT.TAG_LIST))
            {
                NBTTagList list = tag.getTagList("Size", 5);

                if (list.tagCount() >= 3)
                {
                    NBTUtils.readFloatList(list, this.size);
                }
            }

            if (tag.hasKey("Poses", Constants.NBT.TAG_COMPOUND))
            {
                this.limbs.clear();

                NBTTagCompound poses = tag.getCompoundTag("Poses");

                for (String key : poses.getKeySet())
                {
                    ModelTransform trans = new LimbProperties();

                    trans.fromNBT(poses.getCompoundTag(key));
                    this.limbs.put(key, trans);
                }
            }

            if (tag.hasKey("Shapes"))
            {
                NBTTagList shapes = tag.getTagList("Shapes", Constants.NBT.TAG_COMPOUND);

                this.shapes.clear();

                for (int i = 0; i < shapes.tagCount(); i++)
                {
                    NBTTagCompound key = shapes.getCompoundTagAt(i);

                    if (key.hasKey("Name") && key.hasKey("Value"))
                    {
                        ShapeKey shapeKey = new ShapeKey();

                        shapeKey.fromNBT(key);
                        this.shapes.add(shapeKey);
                    }
                }
            }
        }
    }
}