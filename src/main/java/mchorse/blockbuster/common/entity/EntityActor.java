package mchorse.blockbuster.common.entity;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelHandler;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.common.GuiHandler;
import mchorse.blockbuster.common.item.ItemActorConfig;
import mchorse.blockbuster.common.item.ItemRegister;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModifyActor;
import mchorse.blockbuster.network.common.recording.PacketSyncTick;
import mchorse.blockbuster.recording.RecordPlayer;
import mchorse.blockbuster.recording.Utils;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.blockbuster.utils.BlockPos;
import mchorse.blockbuster.utils.EntityUtils;
import mchorse.blockbuster.utils.L10n;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.blockbuster.utils.RLUtils;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

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
public class EntityActor extends EntityLiving implements IEntityAdditionalSpawnData
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

    /* Default pose sizes */
    private float[] flying = {0.6F, 0.6F};
    private float[] sneaking = {0.6F, 1.65F};
    private float[] standing = {0.6F, 1.80F};

    public EntityActor(World worldIn)
    {
        super(worldIn);

        this.fakePlayer = new EntityPlayer(worldIn, new GameProfile(null, "xXx_Fake_Gamer_420_xXx"))
        {
            @Override
            public ChunkCoordinates getPlayerCoordinates()
            {
                return null;
            }

            @Override
            public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_)
            {
                return false;
            }

            @Override
            public void addChatMessage(IChatComponent p_145747_1_)
            {}
        };
    }

    /**
     * Check whether this actor is playing
     */
    public boolean isPlaying()
    {
        return this.playback != null && !this.playback.isFinished();
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
        float[] pose;

        if (this.modelInstance != null)
        {
            pose = this.modelInstance.getPose(EntityUtils.poseForEntity(this)).size;
        }
        else
        {
            pose = this.isSneaking() ? this.sneaking : this.standing;
        }

        this.setSize(pose[0], pose[1]);
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

            if (!this.worldObj.isRemote)
            {
                int tick = this.playback.tick;

                if (this.playback.isFinished())
                {
                    CommonProxy.manager.stopPlayback(this);
                }
                else if (tick != 0 && tick % Blockbuster.proxy.config.record_sync_rate == 0)
                {
                    Dispatcher.sendToTracked(this, new PacketSyncTick(this.getEntityId(), tick));
                }
            }
        }

        /* Copy paste of onLivingUpdate from EntityLivingBase, I believe */
        this.updateArmSwingProgress();

        if (this.worldObj.isRemote && this.newPosRotationIncrements > 0)
        {
            double d0 = this.posX + (this.newPosX - this.posX) / this.newPosRotationIncrements;
            double d1 = this.posY + (this.newPosY - this.posY) / this.newPosRotationIncrements;
            double d2 = this.posZ + (this.newPosZ - this.posZ) / this.newPosRotationIncrements;
            double d3 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - this.rotationYaw);

            this.rotationYaw = (float) (this.rotationYaw + d3 / this.newPosRotationIncrements);
            this.rotationPitch = (float) (this.rotationPitch + (this.newRotationPitch - this.rotationPitch) / this.newPosRotationIncrements);

            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);

            --this.newPosRotationIncrements;
        }
        else if (this.worldObj.isRemote)
        {
            this.motionX *= 0.98D;
            this.motionY *= 0.98D;
            this.motionZ *= 0.98D;
        }

        if (Math.abs(this.motionX) < 0.005D) this.motionX = 0.0D;
        if (Math.abs(this.motionY) < 0.005D) this.motionY = 0.0D;
        if (Math.abs(this.motionZ) < 0.005D) this.motionZ = 0.0D;

        this.prevLimbSwingAmount = this.limbSwingAmount;

        double d0 = this.posX - this.prevPosX;
        double d1 = this.posZ - this.prevPosZ;
        float f = MathHelper.sqrt_double(d0 * d0 + d1 * d1) * 4.0F;

        if (f > 1.0F)
        {
            f = 1.0F;
        }

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
            for (Object object : this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(1.0D, 0.0D, 1.0D)))
            {
                EntityItem item = (EntityItem) object;

                if (!item.isDead && item.getEntityItem() != null && item.delayBeforeCanPickup == 0)
                {
                    item.setDead();
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
    protected boolean interact(EntityPlayer player)
    {
        ItemStack item = player.getHeldItem();

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
                L10n.error(player, "actor.not_attached");

                return false;
            }

            TileEntity tile = this.worldObj.getTileEntity(pos.x, pos.y, pos.z);

            if (tile != null && tile instanceof TileEntityDirector)
            {
                TileEntityDirector director = (TileEntityDirector) tile;

                if (!director.add(this))
                {
                    L10n.info(player, "director.already_registered");
                }
                else
                {
                    L10n.success(player, "director.was_registered");
                }
            }
            else
            {
                L10n.error(player, "director.missing", pos.getX(), pos.getY(), pos.getZ());
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
    public void startPlaying(String filename, boolean kill)
    {
        if (CommonProxy.manager.players.containsKey(this))
        {
            Utils.broadcastMessage("blockbuster.info.actor.playing", new Object[] {});

            return;
        }

        CommonProxy.manager.startPlayback(filename, this, Mode.BOTH, kill, true);
    }

    /**
     * Stop playing
     */
    public void stopPlaying()
    {
        CommonProxy.manager.stopPlayback(this);
    }

    /**
     * Start recording the player's actions for this actor
     */
    private void startRecording(EntityPlayer player)
    {
        if (this.directorBlock == null) return;

        TileEntity tile = player.worldObj.getTileEntity(this.directorBlock.x, this.directorBlock.y, this.directorBlock.z);

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

        if (!this.worldObj.isRemote && notify)
        {
            this.notifyPlayers();
        }
    }

    /**
     * Update the data model
     */
    private void updateModel()
    {
        ModelHandler models = Blockbuster.proxy.models;

        if (models.models.containsKey(this.model))
        {
            this.modelInstance = models.models.get(this.model);
        }
    }

    /**
     * Notify trackers of data changes happened in this actor
     */
    public void notifyPlayers()
    {
        Dispatcher.sendToTracked(this, new PacketModifyActor(this.getEntityId(), this.model, this.skin, this.invisible));
    }

    /* Reading/writing to disk */

    @Override
    public void readEntityFromNBT(NBTTagCompound tag)
    {
        super.readEntityFromNBT(tag);

        this.model = tag.getString("Model");
        this.skin = RLUtils.fromString(tag.getString("Skin"), this.model);
        this.invisible = tag.getBoolean("Invisible");

        this.directorBlock = NBTUtils.getBlockPos("Dir", tag);

        if (!this.worldObj.isRemote)
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
        buffer.writeBoolean(this.isPlaying());

        if (this.isPlaying())
        {
            buffer.writeInt(this.playback.tick);
            buffer.writeByte(this.playback.recordDelay);
            ByteBufUtils.writeUTF8String(buffer, this.playback.record.filename);
        }
    }

    @Override
    public void readSpawnData(ByteBuf buffer)
    {
        this.model = ByteBufUtils.readUTF8String(buffer);
        this.skin = RLUtils.fromString(ByteBufUtils.readUTF8String(buffer), this.model);
        this.invisible = buffer.readBoolean();

        if (buffer.readBoolean())
        {
            int tick = buffer.readInt();
            int delay = buffer.readByte();
            String filename = ByteBufUtils.readUTF8String(buffer);

            if (this.playback == null)
            {
                if (ClientProxy.manager.records.containsKey(filename))
                {
                    this.playback = new RecordPlayer(ClientProxy.manager.records.get(filename), Mode.FRAMES);
                }
                else
                {
                    this.playback = new RecordPlayer(null, Mode.FRAMES);
                }
            }

            if (this.playback != null)
            {
                this.playback.tick = tick;
                this.playback.recordDelay = delay;
            }
        }
    }
}