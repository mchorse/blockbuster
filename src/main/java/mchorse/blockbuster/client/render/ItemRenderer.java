package mchorse.blockbuster.client.render;

import javax.annotation.Nullable;

import com.google.common.base.Objects;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Item renderer class.
 * 
 * This class is responsible for rendering in first-person arms. Most of the 
 * code has been taken from {@link ItemRenderer}. I integrated my morphed 
 * player rendered, added comments for methods and renamed the variables.
 * 
 * Basically made it more readable and support for morphed hands.
 */
@SideOnly(Side.CLIENT)
public class ItemRenderer
{
    /**
     * Resource location for map background.  
     */
    private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");

    /** 
     * A reference to the Minecraft object. 
     */
    private final Minecraft mc;
    private final RenderPlayer render;

    /* Item stacks for both hands */
    private ItemStack itemStackMainHand;
    private ItemStack itemStackOffHand;

    /* Progress variables for hand equipment animation */
    private float equippedProgressMainHand;
    private float prevEquippedProgressMainHand;
    private float equippedProgressOffHand;
    private float prevEquippedProgressOffHand;

    private final RenderItem itemRenderer;

    /**
     * Construct ItemRenderer with Minecraft instance and morphed player 
     * renderer instance.
     */
    public ItemRenderer(Minecraft mc, RenderPlayer player)
    {
        this.mc = mc;
        this.itemRenderer = mc.getRenderItem();
        this.render = player;
    }

    /**
     * Render the item in the first-person. 
     */
    public void renderItemSide(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean p_187462_4_)
    {
        if (heldStack != null)
        {
            Item item = heldStack.getItem();
            Block block = Block.getBlockFromItem(item);
            GlStateManager.pushMatrix();
            boolean flag = this.itemRenderer.shouldRenderItemIn3D(heldStack) && this.isBlockTranslucent(block);

            if (flag)
            {
                GlStateManager.depthMask(false);
            }

            this.itemRenderer.renderItem(heldStack, entitylivingbaseIn, transform, p_187462_4_);

            if (flag)
            {
                GlStateManager.depthMask(true);
            }

            GlStateManager.popMatrix();
        }
    }

    /**
     * Returns true if given block is translucent
     */
    private boolean isBlockTranslucent(@Nullable Block blockIn)
    {
        return blockIn != null && blockIn.getBlockLayer() == BlockRenderLayer.TRANSLUCENT;
    }

    /**
     * Rotate the render around X and Y.
     */
    private void rotateArroundXAndY(float angle, float angleY)
    {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(angle, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(angleY, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    /**
     * Setup the light map. 
     */
    private void setLightmap()
    {
        AbstractClientPlayer abstractclientplayer = this.mc.thePlayer;
        int i = this.mc.theWorld.getCombinedLight(new BlockPos(abstractclientplayer.posX, abstractclientplayer.posY + (double) abstractclientplayer.getEyeHeight(), abstractclientplayer.posZ), 0);
        float f = (float) (i & 65535);
        float f1 = (float) (i >> 16);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);
    }

    /**
     * Setup rotation transformation for the hand
     */
    private void rotateArm(float ticks)
    {
        EntityPlayerSP player = this.mc.thePlayer;
        float armPitch = player.prevRenderArmPitch + (player.renderArmPitch - player.prevRenderArmPitch) * ticks;
        float armYaw = player.prevRenderArmYaw + (player.renderArmYaw - player.prevRenderArmYaw) * ticks;

        GlStateManager.rotate((player.rotationPitch - armPitch) * 0.1F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((player.rotationYaw - armYaw) * 0.1F, 0.0F, 1.0F, 0.0F);
    }

    /**
     * Return the angle to render the Map
     */
    private float getMapAngleFromPitch(float pitch)
    {
        float angle = 1.0F - pitch / 45.0F + 0.1F;

        angle = MathHelper.clamp_float(angle, 0.0F, 1.0F);
        angle = -MathHelper.cos(angle * (float) Math.PI) * 0.5F + 0.5F;

        return angle;
    }

    /**
     * Render both arms (used for rendering map arms)
     */
    private void renderArms()
    {
        if (!this.mc.thePlayer.isInvisible())
        {
            GlStateManager.disableCull();
            GlStateManager.pushMatrix();
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);

            this.renderArm(EnumHandSide.RIGHT);
            this.renderArm(EnumHandSide.LEFT);

            GlStateManager.popMatrix();
            GlStateManager.enableCull();
        }
    }

    /**
     * Render given arm in first-person. 
     */
    private void renderArm(EnumHandSide hand)
    {
        GlStateManager.pushMatrix();
        float f = hand == EnumHandSide.RIGHT ? 1.0F : -1.0F;
        GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * -41.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(f * 0.3F, -1.1F, 0.45F);

        if (hand == EnumHandSide.RIGHT)
        {
            this.render.renderRightArm(this.mc.thePlayer);
        }
        else
        {
            this.render.renderLeftArm(this.mc.thePlayer);
        }

        GlStateManager.popMatrix();
    }

    /**
     * Render the map and the hands in first-person.
     */
    private void renderMapFirstPersonSide(float equipProgress, EnumHandSide hand, float swing, ItemStack item)
    {
        float f = hand == EnumHandSide.RIGHT ? 1.0F : -1.0F;
        GlStateManager.translate(f * 0.125F, -0.125F, 0.0F);

        if (!this.mc.thePlayer.isInvisible())
        {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(f * 10.0F, 0.0F, 0.0F, 1.0F);
            this.renderArmFirstPerson(equipProgress, swing, hand);
            GlStateManager.popMatrix();
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(f * 0.51F, -0.08F + equipProgress * -1.2F, -0.75F);

        float f1 = MathHelper.sqrt_float(swing);
        float f2 = MathHelper.sin(f1 * (float) Math.PI);
        float f3 = -0.5F * f2;
        float f4 = 0.4F * MathHelper.sin(f1 * ((float) Math.PI * 2F));
        float f5 = -0.3F * MathHelper.sin(swing * (float) Math.PI);

        GlStateManager.translate(f * f3, f4 - 0.3F * f2, f5);
        GlStateManager.rotate(f2 * -45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * f2 * -30.0F, 0.0F, 1.0F, 0.0F);

        this.renderMapFirstPerson(item);

        GlStateManager.popMatrix();
    }

    /**
     * Render the map and arms in first-person.
     */
    private void renderMapFirstPerson(float pitch, float progress, float swing)
    {
        float f = MathHelper.sqrt_float(swing);
        float f1 = -0.2F * MathHelper.sin(swing * (float) Math.PI);
        float f2 = -0.4F * MathHelper.sin(f * (float) Math.PI);

        GlStateManager.translate(0.0F, -f1 / 2.0F, f2);

        float f3 = this.getMapAngleFromPitch(pitch);

        GlStateManager.translate(0.0F, 0.04F + progress * -1.2F + f3 * -0.5F, -0.72F);
        GlStateManager.rotate(f3 * -85.0F, 1.0F, 0.0F, 0.0F);

        this.renderArms();

        float f4 = MathHelper.sin(f * (float) Math.PI);

        GlStateManager.rotate(f4 * 20.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(2.0F, 2.0F, 2.0F);

        this.renderMapFirstPerson(this.itemStackMainHand);
    }

    /**
     * Render the map itself in the first person. 
     */
    private void renderMapFirstPerson(ItemStack stack)
    {
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.scale(0.38F, 0.38F, 0.38F);
        GlStateManager.disableLighting();

        this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        GlStateManager.translate(-0.5F, -0.5F, 0.0F);
        GlStateManager.scale(0.0078125F, 0.0078125F, 0.0078125F);

        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(-7.0D, 135.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
        vertexbuffer.pos(135.0D, 135.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
        vertexbuffer.pos(135.0D, -7.0D, 0.0D).tex(1.0D, 0.0D).endVertex();
        vertexbuffer.pos(-7.0D, -7.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();

        MapData mapdata = Items.FILLED_MAP.getMapData(stack, this.mc.theWorld);

        if (mapdata != null)
        {
            this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, false);
        }

        GlStateManager.enableLighting();
    }

    /**
     * Render an arm in the first-person
     */
    private void renderArmFirstPerson(float progress, float swing, EnumHandSide side)
    {
        boolean flag = side != EnumHandSide.LEFT;
        float f = flag ? 1.0F : -1.0F;
        float f1 = MathHelper.sqrt_float(swing);
        float f2 = -0.3F * MathHelper.sin(f1 * (float) Math.PI);
        float f3 = 0.4F * MathHelper.sin(f1 * ((float) Math.PI * 2F));
        float f4 = -0.4F * MathHelper.sin(swing * (float) Math.PI);

        GlStateManager.translate(f * (f2 + 0.64000005F), f3 + -0.6F + progress * -0.6F, f4 + -0.71999997F);
        GlStateManager.rotate(f * 45.0F, 0.0F, 1.0F, 0.0F);

        float f5 = MathHelper.sin(swing * swing * (float) Math.PI);
        float f6 = MathHelper.sin(f1 * (float) Math.PI);

        GlStateManager.rotate(f * f6 * 70.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f * f5 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(f * -1.0F, 3.6F, 3.5F);
        GlStateManager.rotate(f * 120.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * -135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(f * 5.6F, 0.0F, 0.0F);
        GlStateManager.disableCull();

        if (flag)
        {
            this.render.renderRightArm(this.mc.thePlayer);
        }
        else
        {
            this.render.renderLeftArm(this.mc.thePlayer);
        }

        GlStateManager.enableCull();
    }

    /**
     * Transform the food in the first-person based on the animation. 
     */
    private void transformEatFirstPerson(float ticks, EnumHandSide side, ItemStack item)
    {
        float f = (float) this.mc.thePlayer.getItemInUseCount() - ticks + 1.0F;
        float f1 = f / (float) item.getMaxItemUseDuration();

        if (f1 < 0.8F)
        {
            float f2 = MathHelper.abs(MathHelper.cos(f / 4.0F * (float) Math.PI) * 0.1F);
            GlStateManager.translate(0.0F, f2, 0.0F);
        }

        float f3 = 1.0F - (float) Math.pow((double) f1, 27.0D);
        int i = side == EnumHandSide.RIGHT ? 1 : -1;

        GlStateManager.translate(f3 * 0.6F * (float) i, f3 * -0.5F, f3 * 0.0F);
        GlStateManager.rotate((float) i * f3 * 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f3 * 10.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((float) i * f3 * 30.0F, 0.0F, 0.0F, 1.0F);
    }

    /**
     * Transform the hand, in first-person, based on the swing.
     */
    private void transformFirstPerson(EnumHandSide side, float swing)
    {
        int i = side == EnumHandSide.RIGHT ? 1 : -1;
        float f = MathHelper.sin(swing * swing * (float) Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swing) * (float) Math.PI);

        GlStateManager.rotate((float) i * (45.0F + f * -20.0F), 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) i * f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((float) i * -45.0F, 0.0F, 1.0F, 0.0F);
    }

    /**
     * Another transform. 
     */
    private void transformSideFirstPerson(EnumHandSide p_187459_1_, float p_187459_2_)
    {
        int i = p_187459_1_ == EnumHandSide.RIGHT ? 1 : -1;
        GlStateManager.translate((float) i * 0.56F, -0.52F + p_187459_2_ * -0.6F, -0.72F);
    }

    /**
     * Renders the active item in the player's hand when in first person mode.
     */
    public void renderItemInFirstPerson(float partialTicks)
    {
        AbstractClientPlayer player = this.mc.thePlayer;

        float swing = player.getSwingProgress(partialTicks);
        EnumHand enumhand = (EnumHand) Objects.firstNonNull(player.swingingHand, EnumHand.MAIN_HAND);
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
        float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;
        boolean mainHand = true;
        boolean offHand = true;

        if (player.isHandActive())
        {
            ItemStack itemstack = player.getActiveItemStack();

            if (itemstack != null && itemstack.getItem() == Items.BOW) //Forge: Data watcher can desync and cause this to NPE...
            {
                EnumHand enumhand1 = player.getActiveHand();
                mainHand = enumhand1 == EnumHand.MAIN_HAND;
                offHand = !mainHand;
            }
        }

        this.rotateArroundXAndY(pitch, yaw);
        this.setLightmap();
        this.rotateArm(partialTicks);
        GlStateManager.enableRescaleNormal();

        if (mainHand)
        {
            float mainHandSwing = enumhand == EnumHand.MAIN_HAND ? swing : 0.0F;
            float progress = 1.0F - (this.prevEquippedProgressMainHand + (this.equippedProgressMainHand - this.prevEquippedProgressMainHand) * partialTicks);

            this.renderItemInFirstPerson(player, partialTicks, pitch, EnumHand.MAIN_HAND, mainHandSwing, this.itemStackMainHand, progress);
        }

        if (offHand)
        {
            float offHandSwing = enumhand == EnumHand.OFF_HAND ? swing : 0.0F;
            float progress = 1.0F - (this.prevEquippedProgressOffHand + (this.equippedProgressOffHand - this.prevEquippedProgressOffHand) * partialTicks);

            this.renderItemInFirstPerson(player, partialTicks, pitch, EnumHand.OFF_HAND, offHandSwing, this.itemStackOffHand, progress);
        }

        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
    }

    /**
     * Render the given arm based on the given attributes. 
     */
    public void renderItemInFirstPerson(AbstractClientPlayer player, float ticks, float pitch, EnumHand hand, float swing, @Nullable ItemStack item, float equipProgress)
    {
        boolean mainHand = hand == EnumHand.MAIN_HAND;
        EnumHandSide side = mainHand ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
        GlStateManager.pushMatrix();

        if (item == null)
        {
            if (mainHand && !player.isInvisible())
            {
                this.renderArmFirstPerson(equipProgress, swing, side);
            }
        }
        else if (item.getItem() instanceof net.minecraft.item.ItemMap)
        {
            if (mainHand && this.itemStackOffHand == null)
            {
                this.renderMapFirstPerson(pitch, equipProgress, swing);
            }
            else
            {
                this.renderMapFirstPersonSide(equipProgress, side, swing, item);
            }
        }
        else
        {
            boolean rightHand = side == EnumHandSide.RIGHT;

            if (player.isHandActive() && player.getItemInUseCount() > 0 && player.getActiveHand() == hand)
            {
                int j = rightHand ? 1 : -1;

                switch (item.getItemUseAction())
                {
                    case NONE:
                        this.transformSideFirstPerson(side, equipProgress);
                        break;
                    case EAT:
                    case DRINK:
                        this.transformEatFirstPerson(ticks, side, item);
                        this.transformSideFirstPerson(side, equipProgress);
                        break;
                    case BLOCK:
                        this.transformSideFirstPerson(side, equipProgress);
                        break;
                    case BOW:
                        this.transformSideFirstPerson(side, equipProgress);
                        GlStateManager.translate((float) j * -0.2785682F, 0.18344387F, 0.15731531F);
                        GlStateManager.rotate(-13.935F, 1.0F, 0.0F, 0.0F);
                        GlStateManager.rotate((float) j * 35.3F, 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate((float) j * -9.785F, 0.0F, 0.0F, 1.0F);
                        float f5 = (float) item.getMaxItemUseDuration() - ((float) this.mc.thePlayer.getItemInUseCount() - ticks + 1.0F);
                        float f6 = f5 / 20.0F;
                        f6 = (f6 * f6 + f6 * 2.0F) / 3.0F;

                        if (f6 > 1.0F)
                        {
                            f6 = 1.0F;
                        }

                        if (f6 > 0.1F)
                        {
                            float f7 = MathHelper.sin((f5 - 0.1F) * 1.3F);
                            float f3 = f6 - 0.1F;
                            float f4 = f7 * f3;
                            GlStateManager.translate(f4 * 0.0F, f4 * 0.004F, f4 * 0.0F);
                        }

                        GlStateManager.translate(f6 * 0.0F, f6 * 0.0F, f6 * 0.04F);
                        GlStateManager.scale(1.0F, 1.0F, 1.0F + f6 * 0.2F);
                        GlStateManager.rotate((float) j * 45.0F, 0.0F, -1.0F, 0.0F);
                }
            }
            else
            {
                float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(swing) * (float) Math.PI);
                float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(swing) * ((float) Math.PI * 2F));
                float f2 = -0.2F * MathHelper.sin(swing * (float) Math.PI);
                int i = rightHand ? 1 : -1;
                GlStateManager.translate((float) i * f, f1, f2);
                this.transformSideFirstPerson(side, equipProgress);
                this.transformFirstPerson(side, swing);
            }

            this.renderItemSide(player, item, rightHand ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !rightHand);
        }

        GlStateManager.popMatrix();
    }

    /**
     * Update equipped items.
     */
    public void updateEquippedItem()
    {
        if (this.mc.theWorld == null || this.mc.thePlayer == null || this.mc.isGamePaused())
        {
            return;
        }

        this.prevEquippedProgressMainHand = this.equippedProgressMainHand;
        this.prevEquippedProgressOffHand = this.equippedProgressOffHand;
        EntityPlayerSP entityplayersp = this.mc.thePlayer;
        ItemStack mainHand = entityplayersp.getHeldItemMainhand();
        ItemStack offHand = entityplayersp.getHeldItemOffhand();

        if (entityplayersp.isRowingBoat())
        {
            this.equippedProgressMainHand = MathHelper.clamp_float(this.equippedProgressMainHand - 0.4F, 0.0F, 1.0F);
            this.equippedProgressOffHand = MathHelper.clamp_float(this.equippedProgressOffHand - 0.4F, 0.0F, 1.0F);
        }
        else
        {
            float f = entityplayersp.getCooledAttackStrength(1.0F);
            this.equippedProgressMainHand += MathHelper.clamp_float((!net.minecraftforge.client.ForgeHooksClient.shouldCauseReequipAnimation(this.itemStackMainHand, mainHand, entityplayersp.inventory.currentItem) ? f * f * f : 0.0F) - this.equippedProgressMainHand, -0.4F, 0.4F);
            this.equippedProgressOffHand += MathHelper.clamp_float((float) (!net.minecraftforge.client.ForgeHooksClient.shouldCauseReequipAnimation(this.itemStackOffHand, offHand, -1) ? 1 : 0) - this.equippedProgressOffHand, -0.4F, 0.4F);
        }

        if (this.equippedProgressMainHand < 0.1F)
        {
            this.itemStackMainHand = mainHand;
        }

        if (this.equippedProgressOffHand < 0.1F)
        {
            this.itemStackOffHand = offHand;
        }
    }
}