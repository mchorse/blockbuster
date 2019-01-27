package mchorse.blockbuster_pack.morphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Objects;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.render.RenderCustomModel;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.utils.RLUtils;
import mchorse.blockbuster_pack.client.render.layers.LayerBodyPart;
import mchorse.blockbuster_pack.client.render.part.IBodyPart;
import mchorse.blockbuster_pack.client.render.part.MorphBodyPart;
import mchorse.metamorph.api.EntityUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Custom morph
 *
 * This is a morph which allows players to use Blockbuster's custom 
 * models as morphs.
 */
public class CustomMorph extends AbstractMorph
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
     * This flag allows to fail {@link #equals(Object)} equality test 
     */
    public boolean notComparible;

    /**
     * List of body parts (on client side only)
     */
    public List<BodyPart> parts = new ArrayList<BodyPart>();

    /**
     * Whether body parts were initiated 
     */
    public boolean initiated;

    /**
     * Cached key value 
     */
    private String key;

    /**
     * Make hands true!
     */
    public CustomMorph()
    {
        this.settings = this.settings.clone();
        this.settings.hands = true;
    }

    /**
     * Get a pose for rendering
     */
    public ModelPose getPose(EntityLivingBase target)
    {
        if (this.customPose != null)
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

    public String getKey()
    {
        if (this.key == null)
        {
            this.key = this.name.replaceAll("^blockbuster\\.", "");
        }

        return this.key;
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
        ModelCustom model = ModelCustom.MODELS.get(this.getKey());

        if (model != null && this.model != null)
        {
            Model data = model.model;

            if (data != null && (data.defaultTexture != null || data.providesMtl || this.skin != null))
            {
                this.initBodyParts();

                model.materials = this.materials;
                model.pose = this.getPose(player);
                model.swingProgress = 0;

                ResourceLocation texture = this.skin == null ? data.defaultTexture : this.skin;
                RenderCustomModel.bindLastTexture(texture);

                this.drawModel(model, player, x, y, scale * data.scaleGui, alpha);
            }
        }
        else
        {
            FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
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
        RenderCustomModel renderer = ClientProxy.actorRenderer;

        /* This */
        renderer.current = this;
        renderer.setupModel(player);

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
        if (this.model != null)
        {
            this.initBodyParts();

            RenderCustomModel render = ClientProxy.actorRenderer;

            render.current = this;
            render.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
        else
        {
            Minecraft mc = Minecraft.getMinecraft();
            FontRenderer font = mc.fontRendererObj;
            RenderManager manager = mc.getRenderManager();

            EntityRenderer.drawNameplate(font, this.getKey(), (float) x, (float) y + 1, (float) z, 0, manager.playerViewY, manager.playerViewX, mc.gameSettings.thirdPersonView == 2, entity.isSneaking());
        }
    }

    @SideOnly(Side.CLIENT)
    public void initBodyParts()
    {
        if (!this.initiated)
        {
            for (BodyPart part : this.parts)
            {
                part.init();
            }

            this.initiated = true;
        }
    }

    /**
     * Update the player based on its morph abilities and properties. This 
     * method also responsible for updating AABB size. 
     */
    @Override
    public void update(EntityLivingBase target, IMorphing cap)
    {
        if (this.model != null)
        {
            this.updateSize(target, cap);
        }

        if (target.worldObj.isRemote)
        {
            this.updateBodyLimbs(target);
        }

        super.update(target, cap);
    }

    /**
     * Update body limbs 
     */
    @SideOnly(Side.CLIENT)
    private void updateBodyLimbs(EntityLivingBase target)
    {
        if (this.parts == null)
        {
            return;
        }

        for (BodyPart part : this.parts)
        {
            part.update(target);
        }
    }

    /**
     * Update size of the player based on the given morph.
     */
    public void updateSize(EntityLivingBase target, IMorphing cap)
    {
        this.pose = this.getPose(target);

        if (this.pose != null)
        {
            float[] pose = this.pose.size;

            this.updateSize(target, pose[0], pose[1]);
        }
    }

    @Override
    public float getWidth(EntityLivingBase target)
    {
        return this.pose != null ? this.pose.size[0] : 0.6F;
    }

    @Override
    public float getHeight(EntityLivingBase target)
    {
        return this.pose != null ? this.pose.size[1] : 1.8F;
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

            if (morph.notComparible || this.notComparible)
            {
                return false;
            }

            result = result && Objects.equal(this.currentPose, morph.currentPose);
            result = result && this.currentPoseOnSneak == morph.currentPoseOnSneak;
            result = result && this.materials.equals(morph.materials);
            result = result && this.parts.equals(morph.parts);

            if (this.customPose != null && morph.customPose != null)
            {
                result = result && this.customPose.equals(morph.customPose);
            }
            /* If one of them isn't a null, then clearly different */
            else if (this.customPose != morph.customPose)
            {
                result = false;
            }

            if (this.skin == null && morph.skin == null)
            {
                return result;
            }
            else if (this.skin != null && morph.skin != null && morph.skin.equals(this.skin))
            {
                return result;
            }
            else
            {
                return false;
            }
        }

        return result;
    }

    @Override
    public void reset()
    {
        super.reset();

        this.key = null;
        this.initiated = false;
    }

    @Override
    public AbstractMorph clone(boolean isRemote)
    {
        CustomMorph morph = new CustomMorph();

        morph.name = this.name;
        morph.skin = this.skin;

        morph.currentPose = this.currentPose;
        morph.currentPoseOnSneak = this.currentPoseOnSneak;

        if (this.customPose != null)
        {
            morph.customPose = this.customPose.clone();
        }

        if (!this.materials.isEmpty())
        {
            morph.materials.clear();
            morph.materials.putAll(this.materials);
        }

        morph.settings = this.settings;
        morph.model = this.model;

        for (BodyPart part : this.parts)
        {
            morph.parts.add(part.clone(isRemote));
        }

        return morph;
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

        if (!this.parts.isEmpty())
        {
            NBTTagList bodyParts = new NBTTagList();

            for (BodyPart part : this.parts)
            {
                NBTTagCompound bodyPart = new NBTTagCompound();

                part.toNBT(bodyPart);

                if (!bodyPart.hasNoTags())
                {
                    bodyParts.appendTag(bodyPart);
                }
            }

            tag.setTag("BodyParts", bodyParts);
        }
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        if (tag.hasKey("Skin"))
        {
            this.skin = RLUtils.create(tag.getTag("Skin"));
        }

        this.currentPose = tag.getString("Pose");
        this.currentPoseOnSneak = tag.getBoolean("Sneak");

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
            this.parts.clear();
            NBTTagList bodyParts = tag.getTagList("BodyParts", 10);

            for (int i = 0, c = bodyParts.tagCount(); i < c; i++)
            {
                NBTTagCompound bodyPart = bodyParts.getCompoundTagAt(i);
                BodyPart part = new BodyPart();

                part.fromNBT(bodyPart);
                this.parts.add(part);
            }
        }
    }

    public static class BodyPart implements IBodyPart
    {
        public String limb = "";
        public MorphBodyPart part;

        @Override
        @SideOnly(Side.CLIENT)
        public void init()
        {
            if (this.part != null) this.part.init();
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void render(EntityLivingBase entity, float partialTicks)
        {
            if (this.part != null) this.part.render(entity, partialTicks);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void update(EntityLivingBase entity)
        {
            if (this.part != null) this.part.update(entity);
        }

        @Override
        public boolean equals(Object obj)
        {
            boolean result = super.equals(obj);

            if (obj instanceof BodyPart)
            {
                BodyPart part = (BodyPart) obj;

                result = result && Objects.equal(this.limb, part.limb);
                result = result && Objects.equal(this.part, part.part);
            }

            return result;
        }

        public BodyPart clone(boolean isRemote)
        {
            BodyPart part = new BodyPart();

            part.limb = this.limb;
            part.part = this.part.clone(isRemote);

            return part;
        }

        @Override
        public void toNBT(NBTTagCompound tag)
        {
            if (this.part == null)
            {
                return;
            }

            if (!this.limb.isEmpty()) tag.setString("Limb", this.limb);
            this.part.toNBT(tag);
        }

        @Override
        public void fromNBT(NBTTagCompound tag)
        {
            this.limb = tag.getString("Limb");
            this.part = new MorphBodyPart();
            this.part.fromNBT(tag);
        }
    }
}