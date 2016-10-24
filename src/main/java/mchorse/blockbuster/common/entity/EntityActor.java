package mchorse.blockbuster.common.entity;

import com.mojang.authlib.GameProfile;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.actor.Model;
import mchorse.blockbuster.actor.ModelHandler;
import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.common.GuiHandler;
import mchorse.blockbuster.common.item.ItemActorConfig;
import mchorse.blockbuster.common.item.ItemRegister;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModifyActor;
import mchorse.blockbuster.recording.RecordPlayer;
import mchorse.blockbuster.recording.Utils;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.blockbuster.utils.NBTUtils;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

/**
 * Actor entity class
 *
 * Actor entity class is responsible for recording player's actions and execute
 * them. I'm also thinking about giving them controllable AI settings so they
 * could be used without recording (like during the battles between two or more
 * actors).
 *
 * Also, it would be cool to add something like simple crowd control for bigger
 * scenes (like one from Van Helsing in beginning with big crowd with torches,
 * fire and stuff).
 */
public class EntityActor extends EntityCreature implements IEntityAdditionalSpawnData
{
    /**
     * Skin used by the actor. If empty - means default skin provided with this
     * mod.
     */
    public ResourceLocation skin;

    /**
     * Model which is used to display. If empty - means default model (steve)
     * provided with this mod.
     */
    public String model = "";

    /**
     * Model instance, used for setting the size of this entity in updateSize
     * method
     */
    private Model modelInstance;

    /**
     * Position of director's block (needed to start the playback of other
     * actors while recording this actor).
     */
    public BlockPos directorBlock;

    /**
     * Temporary solution for disallowing rendering of custom name tag in GUI.
     */
    public boolean renderName = true;

    /**
     * This field is needed to make actors invisible. This is helpful for
     * scenes with different characters, which isn't needed to be seen.
     */
    public boolean invisible = false;

    /**
     * Fake player used in some of methods like onBlockActivated to avoid
     * NullPointerException (and some math like the direction in which to open
     * the fence or something).
     */
    public EntityPlayer fakePlayer;

    /**
     * In Soviet Russia, playback plays you
     */
    public RecordPlayer playback;

    public EntityActor(World worldIn)
    {
        super(worldIn);

        this.fakePlayer = new EntityPlayer(worldIn, new GameProfile(null, "xXx_Fake_Player_420_xXx"))
        {
            @Override
            public boolean isSpectator()
            {
                return false;
            }

            @Override
            public boolean isCreative()
            {
                return false;
            }
        };
    }

    /**
     * Returns the Y Offset of this entity.
     *
     * Taken from EntityPlayer.
     */
    @Override
    public double getYOffset()
    {
        return -0.35D;
    }

    /**
     * Can't despawn an actor
     */
    @Override
    protected boolean canDespawn()
    {
        return false;
    }

    /**
     * Brutally stolen from EntityPlayer class
     */
    public void setElytraFlying(boolean isFlying)
    {
        this.setFlag(7, isFlying);
    }

    /**
     * This is also brutally stolen from EntityPlayer class, by the way, I don't
     * think that changing the height while sneaking can save player's life
     */
    protected void updateSize()
    {
        float width = this.width;
        float height = this.height;

        if (this.isElytraFlying())
        {
            width = 0.6F;
            height = 0.6F;

            if (this.modelInstance != null)
            {
                float[] pose = this.modelInstance.poses.get("flying").size;

                width = pose[0];
                height = pose[1];
            }
        }
        else if (this.isSneaking())
        {
            width = 0.6F;
            height = 1.65F;

            if (this.modelInstance != null)
            {
                float[] pose = this.modelInstance.poses.get("sneaking").size;

                width = pose[0];
                height = pose[1];
            }
        }
        else
        {
            width = 0.6F;
            height = 1.8F;

            if (this.modelInstance == null) this.updateModel();

            if (this.modelInstance != null)
            {
                float[] pose = this.modelInstance.poses.get("standing").size;

                width = pose[0];
                height = pose[1];
            }
        }

        this.setSize(width, height);
    }

    /**
     * Adjust the movement, limb swinging, and process action stuff.
     *
     * See process actions method for more information.
     */
    @Override
    public void onLivingUpdate()
    {
        this.updateSize();
        this.pickUpNearByItems();

        if (this.playback != null && this.playback.playing)
        {
            this.playback.next(this);

            if (this.playback.isFinished() && !this.worldObj.isRemote)
            {
                CommonProxy.manager.stopPlayback(this);
            }
        }

        this.updateArmSwingProgress();

        /*
         * Taken from the EntityLivingBase, IDK what it does, but the same code was
         * in Mocap's EntityMocap (which is serves like an actor in Mocap mod)
         *
         * It looks like position and rotation interpolation, though
         */
        if (this.worldObj.isRemote && this.newPosRotationIncrements > 0)
        {
            double d5 = this.posX + (this.interpTargetX - this.posX) / this.newPosRotationIncrements;
            double d0 = this.posY + (this.interpTargetY - this.posY) / this.newPosRotationIncrements;
            double d1 = this.posZ + (this.interpTargetZ - this.posZ) / this.newPosRotationIncrements;

            this.newPosRotationIncrements -= 1;
            this.setPosition(d5, d0, d1);
        }
        else if (!this.isServerWorld())
        {
            this.motionX *= 0.98D;
            this.motionY *= 0.98D;
            this.motionZ *= 0.98D;
        }

        if (Math.abs(this.motionX) < 0.005D) this.motionX = 0.0D;
        if (Math.abs(this.motionY) < 0.005D) this.motionY = 0.0D;
        if (Math.abs(this.motionZ) < 0.005D) this.motionZ = 0.0D;

        /* Taken from the EntityOtherPlayerMP, I think */
        this.prevLimbSwingAmount = this.limbSwingAmount;

        double d0 = this.posX - this.prevPosX;
        double d1 = this.posZ - this.prevPosZ;
        float f = MathHelper.sqrt_double(d0 * d0 + d1 * d1) * 4.0F;

        if (f > 1.0F) f = 1.0F;

        this.limbSwingAmount += (f - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;
    }

    /**
     * Destroy near by items
     *
     * Taken from super implementation of onLivingUpdate. You can't use
     * super.onLivingUpdate() in onLivingUpdate(), because it will distort
     * actor's movement (make it more laggy)
     */
    private void pickUpNearByItems()
    {
        if (!this.worldObj.isRemote && !this.dead)
        {
            for (EntityItem entityitem : this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().expand(1.0D, 0.0D, 1.0D)))
            {
                if (!entityitem.isDead && entityitem.getEntityItem() != null && !entityitem.cannotPickup())
                {
                    entityitem.setDead();
                }
            }
        }
    }

    /* Processing interaction with player */

    /**
     * Process interact
     *
     * Inject UUID of actor to registering device, open GUI for changing actor's
     * skin, or start recording him
     */
    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand p_184645_2_, ItemStack stack)
    {
        ItemStack item = player.getHeldItemMainhand();

        if (item != null && (this.handleRegisterItem(item, player) || this.handleSkinItem(item, player)))
        {
            return true;
        }
        else if (item == null)
        {
            if (!this.worldObj.isRemote) this.startRecording(player);

            return true;
        }

        return false;
    }

    /**
     * Set actor's id on register item (while using register item on this actor)
     */
    private boolean handleRegisterItem(ItemStack stack, EntityPlayer player)
    {
        boolean holdsRegisterItem = stack.getItem() instanceof ItemRegister;

        if (!this.worldObj.isRemote && holdsRegisterItem)
        {
            ItemRegister item = (ItemRegister) stack.getItem();
            BlockPos pos = item.getBlockPos(stack);

            if (pos == null)
            {
                player.addChatMessage(new TextComponentTranslation("blockbuster.actor.not_attached"));
            }

            TileEntity tile = this.worldObj.getTileEntity(pos);

            if (tile != null && tile instanceof TileEntityDirector)
            {
                TileEntityDirector director = (TileEntityDirector) tile;

                if (!director.add(this))
                {
                    player.addChatMessage(new TextComponentTranslation("blockbuster.director.already_registered"));
                }
                else
                {
                    player.addChatMessage(new TextComponentTranslation("blockbuster.director.was_registered"));
                }
            }
            else
            {
                player.addChatMessage(new TextComponentTranslation("blockbuster.director.missing", pos.getX(), pos.getY(), pos.getZ()));
            }
        }

        return holdsRegisterItem;
    }

    /**
     * Open skin choosing GUI by using skin managing item
     */
    private boolean handleSkinItem(ItemStack stack, EntityPlayer player)
    {
        boolean holdsSkinItem = stack.getItem() instanceof ItemActorConfig;

        if (this.worldObj.isRemote && holdsSkinItem)
        {
            GuiHandler.open(player, GuiHandler.ACTOR, this.getEntityId(), 0, 0);
        }

        return holdsSkinItem;
    }

    /* Public API */

    /**
     * Start the playback, invoked by director block (more specifically by
     * DirectorTileEntity).
     */
    public void startPlaying(String filename, boolean setDead)
    {
        if (CommonProxy.manager.players.containsKey(this))
        {
            Utils.broadcastMessage("blockbuster.actor.playing", new Object[] {});
            return;
        }

        if (filename.isEmpty())
        {
            Utils.broadcastMessage(new TextComponentTranslation("blockbuster.actor.no_name"));
        }
        else
        {
            CommonProxy.manager.startPlayback(filename, this, Mode.BOTH, setDead, true);
        }
    }

    /**
     * Stop playing
     */
    public void stopPlaying()
    {
        if (!CommonProxy.manager.players.containsKey(this))
        {
            return;
        }

        CommonProxy.manager.stopPlayback(this);
    }

    /**
     * Start recording the player's actions for this actor
     */
    private void startRecording(EntityPlayer player)
    {
        if (this.directorBlock == null) return;

        TileEntity tile = player.worldObj.getTileEntity(this.directorBlock);

        if (tile != null && tile instanceof TileEntityDirector)
        {
            TileEntityDirector director = (TileEntityDirector) tile;

            if (!CommonProxy.manager.recorders.containsKey(player))
            {
                director.startPlayback(this);
            }
            else
            {
                director.stopPlayback(this);
            }

            director.startRecording(this, player);
        }
    }

    /**
     * Configure this actor
     *
     * Takes four properties to modify: filename used as id for recording,
     * displayed name, rendering skin and invulnerability flag
     */
    public void modify(String model, ResourceLocation skin, boolean invisible, boolean notify)
    {
        this.model = model;
        this.skin = skin;
        this.invisible = invisible;

        this.updateModel();

        if (!this.worldObj.isRemote && notify) this.notifyPlayers();
    }

    private void updateModel()
    {
        ModelHandler models = Blockbuster.proxy.models;

        if (models.models.containsKey(this.model))
        {
            this.modelInstance = models.models.get(this.model);
        }
    }

    public void notifyPlayers()
    {
        Dispatcher.updateTrackers(this, new PacketModifyActor(this.getEntityId(), this.model, this.skin, this.invisible));
    }

    /* Reading/writing to disk */

    @Override
    public void readEntityFromNBT(NBTTagCompound tag)
    {
        super.readEntityFromNBT(tag);

        ResourceLocation skin = this.skin;
        String model = this.model;

        this.model = tag.getString("Model");
        this.skin = EntityActor.fromString(tag.getString("Skin"), this.model);
        this.invisible = tag.getBoolean("Invisible");

        this.directorBlock = NBTUtils.getBlockPos("Dir", tag);

        if (((skin != null && !skin.equals(this.skin)) || !model.equals(this.model)) && !this.worldObj.isRemote)
        {
            this.notifyPlayers();
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag)
    {
        super.writeEntityToNBT(tag);

        if (this.skin != null)
        {
            tag.setString("Skin", this.skin.toString());
        }

        if (!this.model.isEmpty())
        {
            tag.setString("Model", this.model);
        }

        if (this.directorBlock != null)
        {
            NBTUtils.saveBlockPos("Dir", tag, this.directorBlock);
        }

        tag.setBoolean("Invisible", this.invisible);
    }

    /* IEntityAdditionalSpawnData implementation */

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        ByteBufUtils.writeUTF8String(buffer, this.model);
        ByteBufUtils.writeUTF8String(buffer, this.skin == null ? "" : this.skin.toString());
        buffer.writeBoolean(this.invisible);

        /* What a shame, Mojang, why do I need to synchronize your shit?! */
        buffer.writeBoolean(this.isEntityInvulnerable(DamageSource.anvil));
    }

    @Override
    public void readSpawnData(ByteBuf buffer)
    {
        this.model = ByteBufUtils.readUTF8String(buffer);
        this.skin = EntityActor.fromString(ByteBufUtils.readUTF8String(buffer), this.model);
        this.invisible = buffer.readBoolean();

        this.setEntityInvulnerable(buffer.readBoolean());
    }

    public static ResourceLocation fromString(String skin, String model)
    {
        if (skin.isEmpty())
        {
            return null;
        }

        if (skin.indexOf(":") == -1)
        {
            String suffix = (skin.indexOf("/") == -1 ? model + "/" : "");

            return new ResourceLocation("blockbuster.actors", suffix + skin);
        }

        return new ResourceLocation(skin);
    }

    public static String fromResource(ResourceLocation skin)
    {
        if (skin == null)
        {
            return "";
        }

        if (skin.getResourceDomain().equals("blockbuster.actors"))
        {
            String[] splits = skin.getResourcePath().split("/");

            return splits[splits.length - 1];
        }

        return skin.toString();
    }
}