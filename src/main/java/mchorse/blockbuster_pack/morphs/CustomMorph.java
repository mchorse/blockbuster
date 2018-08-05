package mchorse.blockbuster_pack.morphs;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Objects;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.render.RenderCustomModel;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.utils.TextureLocation;
import mchorse.metamorph.api.EntityUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.client.gui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
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

        return model.getPose(poseName);
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

        if (model != null)
        {
            Model data = model.model;

            if (data != null && (data.defaultTexture != null || data.providesMtl || this.skin != null))
            {
                model.materials = this.materials;
                model.pose = this.getPose(player);
                model.swingProgress = 0;

                ResourceLocation texture = this.skin == null ? data.defaultTexture : this.skin;
                RenderCustomModel.bindLastTexture(texture);

                GuiUtils.drawModel(model, player, x, y, scale * data.scaleGui, alpha);
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

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderHand(EntityPlayer player, EnumHand hand)
    {
        if (this.renderer == null || !(this.renderer instanceof RenderCustomModel))
        {
            return false;
        }

        RenderCustomModel renderer = (RenderCustomModel) this.renderer;

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
            RenderCustomModel render = (RenderCustomModel) this.renderer;

            render.current = this;
            render.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
        else
        {
            Minecraft mc = Minecraft.getMinecraft();
            FontRenderer font = mc.fontRenderer;
            RenderManager manager = mc.getRenderManager();

            EntityRenderer.drawNameplate(font, this.name.replace("blockbuster.", ""), (float) x, (float) y + 1, (float) z, 0, manager.playerViewY, manager.playerViewX, mc.gameSettings.thirdPersonView == 2, entity.isSneaking());
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

        super.update(target, cap);
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

            result = result && Objects.equal(this.currentPose, morph.currentPose);
            result = result && this.currentPoseOnSneak == morph.currentPoseOnSneak;
            result = result && this.materials.equals(morph.materials);

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

        if (this.materials.isEmpty())
        {
            morph.materials.clear();
            morph.materials.putAll(this.materials);
        }

        morph.settings = settings;
        morph.model = this.model;

        if (isRemote)
        {
            morph.renderer = this.renderer;
        }

        return morph;
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        if (this.skin != null)
        {
            tag.setString("Skin", this.skin.toString());
        }

        tag.setString("Pose", this.currentPose);
        tag.setBoolean("Sneak", this.currentPoseOnSneak);

        if (this.customPose != null)
        {
            tag.setTag("CustomPose", this.customPose.toNBT(new NBTTagCompound()));
        }

        if (!this.materials.isEmpty())
        {
            NBTTagCompound materials = new NBTTagCompound();

            for (Map.Entry<String, ResourceLocation> entry : this.materials.entrySet())
            {
                materials.setString(entry.getKey(), entry.getValue().toString());
            }

            tag.setTag("Materials", materials);
        }
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        if (tag.hasKey("Skin", 8))
        {
            this.skin = new TextureLocation(tag.getString("Skin"));
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
                this.materials.put(key, new TextureLocation(materials.getString(key)));
            }
        }
    }
}