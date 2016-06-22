package noname.blockbuster.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import noname.blockbuster.Blockbuster;
import noname.blockbuster.item.RegisterItem;
import noname.blockbuster.item.SkinManagerItem;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.PacketChangeSkin;
import noname.blockbuster.recording.Mocap;
import noname.blockbuster.recording.actions.Action;
import noname.blockbuster.tileentity.DirectorTileEntity;

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
public class ActorEntity extends EntityCreature implements IEntityAdditionalSpawnData
{
    /**
     * Event list. Each tick there's might be added an event action which
     * should be performed by this actor. The events are injected by PlayThread.
     */
    public List<Action> eventsList = Collections.synchronizedList(new ArrayList());

    /**
     * Skin used by the actor. If empty – means default skin provided with
     * this mod.
     */
    public String skin = "";

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
     * Fake player used in some of methods like onBlockActivated to solve
     * NullPointerException
     */
    public EntityPlayer fakePlayer;

    public ActorEntity(World worldIn)
    {
        super(worldIn);

        if (!worldIn.isRemote)
        {
            this.fakePlayer = FakePlayerFactory.getMinecraft((WorldServer) worldIn);
        }
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
     * Adjust the movement, limb swinging, and process action stuff.
     *
     * See process actions method for more information.
     */
    @Override
    public void onLivingUpdate()
    {
        this.pickUpNearByItems();

        if (this.eventsList.size() > 0)
        {
            this.eventsList.remove(0).apply(this);
        }

        if (!this.worldObj.isRemote)
        {
            this.fakePlayer.rotationYaw = this.rotationYaw;
            this.fakePlayer.rotationPitch = this.rotationPitch;
        }

        this.updateArmSwingProgress();

        /* Taken from the EntityDragon, IDK what it does, but the same code
         * was in Mocap's EntityMocap (which is serves like an actor in
         * Mocap mod)
         *
         * It looks like position and rotation interpolation, though */
        if (this.worldObj.isRemote && this.newPosRotationIncrements > 0)
        {
            double d5 = this.posX + (this.interpTargetX - this.posX) / this.newPosRotationIncrements;
            double d0 = this.posY + (this.interpTargetY - this.posY) / this.newPosRotationIncrements;
            double d1 = this.posZ + (this.interpTargetZ - this.posZ) / this.newPosRotationIncrements;
            double d2 = MathHelper.wrapAngleTo180_double(this.interpTargetYaw - this.rotationYaw);

            this.rotationYaw = 360 + (float) (this.rotationYaw + d2 / this.newPosRotationIncrements);
            this.rotationPitch = (float) (this.rotationPitch + (this.newPosX - this.rotationPitch) / this.newPosRotationIncrements);
            this.newPosRotationIncrements -= 1;

            this.setPosition(d5, d0, d1);
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.prevRotationYaw = this.rotationYaw;
        }
        else if (!this.isServerWorld())
        {
            this.motionX *= 0.98D;
            this.motionY *= 0.98D;
            this.motionZ *= 0.98D;
        }

        if (Math.abs(this.motionX) < 0.005D)
            this.motionX = 0.0D;
        if (Math.abs(this.motionY) < 0.005D)
            this.motionY = 0.0D;
        if (Math.abs(this.motionZ) < 0.005D)
            this.motionZ = 0.0D;

        if (!this.isServerWorld())
        {
            this.rotationYawHead = this.rotationYaw;
        }

        /* Taken from the EntityOtherPlayerMP, I think */
        this.prevLimbSwingAmount = this.limbSwingAmount;

        double d0 = this.posX - this.prevPosX;
        double d1 = this.posZ - this.prevPosZ;
        float f = MathHelper.sqrt_double(d0 * d0 + d1 * d1) * 4.0F;

        if (f > 1.0F)
            f = 1.0F;

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
     * Inject UUID of actor to registering device, open GUI for changing
     * actor's skin, or start recording him
     */
    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand p_184645_2_, ItemStack stack)
    {
        ItemStack item = player.getHeldItemMainhand();

        if (item != null && (this.handleRegisterItem(item) || this.handleSkinItem(item, player)))
        {
            return true;
        }
        else if (item == null)
        {
            if (!this.worldObj.isRemote)
                this.startRecording(player);

            return true;
        }

        return false;
    }

    /**
     * Set actor's id on register item (while using register item on this
     * actor)
     */
    private boolean handleRegisterItem(ItemStack stack)
    {
        boolean holdsRegisterItem = stack.getItem() instanceof RegisterItem;

        if (!this.worldObj.isRemote && holdsRegisterItem)
        {
            RegisterItem item = (RegisterItem) stack.getItem();
            item.registerStack(stack, this);
        }

        return holdsRegisterItem;
    }

    /**
     * Open skin choosing GUI by using skin managing item
     */
    private boolean handleSkinItem(ItemStack stack, EntityPlayer player)
    {
        boolean holdsSkinItem = stack.getItem() instanceof SkinManagerItem;

        if (this.worldObj.isRemote && holdsSkinItem)
        {
            player.openGui(Blockbuster.instance, 1, this.worldObj, this.getEntityId(), 0, 0);
        }

        return holdsSkinItem;
    }

    /* Public API */

    /**
     * Set actor's skin, and possibly notify the clients about the change
     */
    public void setSkin(String skin, boolean notify)
    {
        this.skin = skin;

        if (!this.worldObj.isRemote && notify)
        {
            Dispatcher.updateTrackers(this, new PacketChangeSkin(this.getEntityId(), this.skin));
        }
    }

    /**
     * Start the playback, invoked by director block (more specifically by
     * DirectorTileEntity).
     */
    public void startPlaying()
    {
        if (Mocap.playbacks.containsKey(this))
        {
            Mocap.broadcastMessage(I18n.format("blockbuster.actor.playing"));
            return;
        }

        if (!this.hasCustomName())
        {
            Mocap.broadcastMessage(I18n.format("blockbuster.actor.no_name"));
        }
        else
        {
            Mocap.startPlayback(this.getCustomNameTag(), this, false);
        }
    }

    /**
     * Stop playing
     */
    public void stopPlaying()
    {
        if (!Mocap.playbacks.containsKey(this))
        {
            Mocap.broadcastMessage(I18n.format("blockbuster.actor.playing"));
            return;
        }

        if (!this.hasCustomName())
        {
            Mocap.broadcastMessage(I18n.format("blockbuster.actor.no_name"));
        }
        else
        {
            Mocap.playbacks.get(this).playing = false;
        }
    }

    /**
     * Start recording the player's actions for this actor
     *
     * Few notes:
     * - Actor performs a playback specified by his name tag, so if you'll kill
     *   actor by accident, you can create new actor and name him as the old one.
     *   He'll still be able to playback the same recording.
     * - Don't use the same name for actors in different scenes. It will cause
     *   total overwrite of your previous recording, be careful.
     */
    private void startRecording(EntityPlayer player)
    {
        if (!this.hasCustomName())
        {
            Mocap.broadcastMessage(I18n.format("blockbuster.actor.noname"));
            return;
        }

        if (this.directorBlock != null)
        {
            DirectorTileEntity director = (DirectorTileEntity) player.worldObj.getTileEntity(this.directorBlock);

            if (!Mocap.records.containsKey(player))
            {
                director.startPlayback(this);
            }
            else
            {
                director.stopPlayback(this);
            }
        }

        Mocap.startRecording(this.getCustomNameTag(), player);
    }

    /* Reading/writing to disk */

    @Override
    public void readEntityFromNBT(NBTTagCompound tag)
    {
        this.skin = tag.getString("Skin");

        if (tag.hasKey("DirX") && tag.hasKey("DirY") && tag.hasKey("DirZ"))
        {
            this.directorBlock = new BlockPos(tag.getInteger("DirX"), tag.getInteger("DirY"), tag.getInteger("DirZ"));
        }

        super.readEntityFromNBT(tag);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag)
    {
        if (this.skin != "")
        {
            tag.setString("Skin", this.skin);
        }

        if (this.directorBlock != null)
        {
            tag.setInteger("DirX", this.directorBlock.getX());
            tag.setInteger("DirY", this.directorBlock.getY());
            tag.setInteger("DirZ", this.directorBlock.getZ());
        }

        super.writeEntityToNBT(tag);
    }

    /* IEntityAdditionalSpawnData implementation */

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        ByteBufUtils.writeUTF8String(buffer, this.skin);
    }

    @Override
    public void readSpawnData(ByteBuf buffer)
    {
        this.skin = ByteBufUtils.readUTF8String(buffer);
    }
}