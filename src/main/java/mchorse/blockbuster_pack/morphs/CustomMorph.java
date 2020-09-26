package mchorse.blockbuster_pack.morphs;

import com.google.common.base.Objects;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelHandler;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.render.RenderCustomModel;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster_pack.client.render.layers.LayerBodyPart;
import mchorse.mclib.utils.resources.RLUtils;
import mchorse.metamorph.api.EntityUtils;
import mchorse.metamorph.api.models.IMorphProvider;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.utils.Animation;
import mchorse.metamorph.api.morphs.utils.IAnimationProvider;
import mchorse.metamorph.api.morphs.utils.ISyncableMorph;
import mchorse.metamorph.api.morphs.utils.PausedMorph;
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
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom morph
 *
 * This is a morph which allows players to use Blockbuster's custom 
 * models as morphs.
 */
public class CustomMorph extends AbstractMorph implements IBodyPartProvider, ISyncableMorph, IAnimationProvider
{
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
    public ModelPose customPose = null;

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

    private PausedMorph pause = new PausedMorph();

    /**
     * Make hands true!
     */
    public CustomMorph()
    {
        super();

        this.settings = this.settings.copy();
        this.settings.hands = true;
    }

    @Override
    public void pauseMorph(AbstractMorph previous, int offset)
    {
        this.pauseRecursive(this, previous, offset);

        this.pause.set(previous, offset);
        // this.pause.recursivePausing(this);
        this.applyAnimation(this.animation);
    }

    private void pauseRecursive(IBodyPartProvider provider, AbstractMorph previous, int offset)
    {
        BodyPartManager manager = provider.getBodyPart();

        for (int i = 0; i < manager.parts.size(); i++)
        {
            BodyPart part = manager.parts.get(i);
            AbstractMorph morph = part.morph.get();

            if (morph instanceof SequencerMorph)
            {
                SequencerMorph sequencer = (SequencerMorph) morph;
                SequencerMorph.FoundMorph foundMorph = sequencer.getMorphAt(offset);

                if (foundMorph != null && foundMorph.morph != null)
                {
                    AbstractMorph copy = foundMorph.morph.copy();
                    int localOffset = offset - foundMorph.lastDuration;

                    part.morph.setDirect(copy);

                    if (previous instanceof IBodyPartProvider)
                    {
                        BodyPartManager previousManager = ((IBodyPartProvider) previous).getBodyPart();
                        BodyPart previousPart = i < previousManager.parts.size() ? previousManager.parts.get(i) : null;

                        previous = previousPart == null ? null : previousPart.morph.get();
                    }
                    else
                    {
                        previous = null;
                    }

                    if (previous != null)
                    {
                        previous.copy();
                    }

                    if (copy instanceof ISyncableMorph)
                    {
                        ((ISyncableMorph) copy).pauseMorph(foundMorph.previous == null ? previous : foundMorph.previous, localOffset);
                    }

                    if (copy instanceof IBodyPartProvider)
                    {
                        this.pauseRecursive((IBodyPartProvider) copy, previous, localOffset);
                    }
                }
            }
            else if (morph instanceof ISyncableMorph)
            {
                ((ISyncableMorph) morph).pauseMorph(previous, offset);
            }
        }
    }

    public void applyAnimation(CustomMorph.PoseAnimation animation)
    {
        if (this.isPaused())
        {
            animation.pause();
            animation.progress = this.pause.offset;

            AbstractMorph previous = this.pause.previous;

            if (previous instanceof CustomMorph)
            {
                animation.last = ((CustomMorph) previous).getCurrentPose();
            }
        }
    }

    @Override
    public boolean isPaused()
    {
        return this.pause.isPaused();
    }

    @Override
    public Animation getAnimation()
    {
        return this.animation;
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

        if (this.animation.isInProgress())
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
        this.updateModel();

        if (this.model != null)
        {
            this.parts.initBodyParts();

            RenderCustomModel render = ClientProxy.actorRenderer;

            render.current = this;
            render.doRender(entity, x, y, z, entityYaw, partialTicks);

            if (this.animation.isInProgress() && entity instanceof EntityPlayer)
            {
                ((EntityPlayer) entity).eyeHeight = this.animation.pose.size[1] * 0.9F;
            }
        }
        else
        {
            Minecraft mc = Minecraft.getMinecraft();
            FontRenderer font = mc.fontRenderer;
            RenderManager manager = mc.getRenderManager();

            EntityRenderer.drawNameplate(font, this.getKey(), (float) x, (float) y + 1, (float) z, 0, manager.playerViewY, manager.playerViewX, mc.gameSettings.thirdPersonView == 2, entity.isSneaking());
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

        if (this.model != null)
        {
            this.updateSize(target);
        }

        this.parts.updateBodyLimbs(target);
        super.update(target);
    }

    /**
     * Update size of the player based on the given morph.
     */
    public void updateSize(EntityLivingBase target)
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

            this.pause.reset();

            /* Don't suddenly end the animation in progress, interpolate */
            if (!custom.animation.ignored)
            {
                /* If the last pose is null, it might case a first cycle freeze.
                 * this should fix it. */
                ModelPose pose = this.getCurrentPose();

                if (this.animation.isInProgress() && pose != null)
                {
                    this.animation.last = this.animation.calculatePose(pose, 1).clone();
                }
                else
                {
                    this.animation.last = pose;
                }

                this.currentPose = custom.currentPose;
                this.customPose = custom.customPose == null ? null : custom.customPose.clone();
                this.animation.merge(custom.animation);
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
            target.animation.last = destination.animation.calculatePose(pose, 1).clone();
        }
        else
        {
            target.animation.last = pose;
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
                this.customPose = morph.customPose.clone();
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
            this.pause.copy(morph.pause);
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

        this.pause.toNBT(tag);
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
            this.customPose = new ModelPose();
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

        this.pause.fromNBT(tag);
        this.applyAnimation(this.animation);
    }

    /**
     * Animation details 
     */
    public static class PoseAnimation extends Animation
    {
        public ModelPose last;
        public ModelPose pose = new ModelPose();

        @Override
        public void merge(Animation animation)
        {
            super.merge(animation);
            this.pose.limbs.clear();
        }

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
                    trans = new ModelTransform();
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
}