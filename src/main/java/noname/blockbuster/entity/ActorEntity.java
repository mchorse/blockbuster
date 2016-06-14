package noname.blockbuster.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import noname.blockbuster.Blockbuster;
import noname.blockbuster.item.RegisterItem;
import noname.blockbuster.item.SkinManagerItem;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.PacketChangeSkin;
import noname.blockbuster.recording.Action;
import noname.blockbuster.recording.Mocap;
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

    public ActorEntity(World worldIn)
    {
        super(worldIn);
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
     * Process actions
     *
     * Small method to route action execution based on type. Made for organizing
     * the code. Otherwise, this method would be ridiculously long!
     */
    private void processActions(Action action)
    {
        switch (action.type)
        {
            case Action.SWIPE:
                this.swingArm(EnumHand.MAIN_HAND);
                break;

            case Action.EQUIP:
                this.equipAction(action);
                break;

            case Action.DROP:
                this.dropAction(action);
                break;

            case Action.SHOOTARROW:
                this.replayShootArrow(action);
                break;

            case Action.PLACE_BLOCK:
                this.placeBlock(action);
                break;

            case Action.MOUNTING:
                this.mountAction(action);
                break;

            case Action.INTERACT_BLOCK:
                this.interactBlockAction(action);
                break;
        }
    }

    private void placeBlock(Action action)
    {
        ItemStack item = ItemStack.loadItemStackFromNBT(action.itemData);

        if (item.getItem() instanceof ItemBlock)
        {
            ItemBlock block = (ItemBlock) item.getItem();
            BlockPos pos = new BlockPos(action.xCoord, action.yCoord, action.zCoord);
            EnumFacing face = EnumFacing.VALUES[action.armorId];

            block.placeBlockAt(item, null, this.worldObj, pos, face, 0, 0, 0, block.block.getStateFromMeta(action.armorSlot));
        }
    }

    private void replayShootArrow(Action ma)
    {
        float f = ma.arrowCharge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;

        if (f < 0.1D)
            return;
        if (f > 1.0F)
            f = 1.0F;

        EntityArrow entityarrow = new EntityArrow(this.worldObj)
        {
            @Override
            protected ItemStack getArrowStack()
            {
                return new ItemStack(Items.arrow);
            }
        };

        entityarrow.canBePickedUp = PickupStatus.ALLOWED;
        this.worldObj.spawnEntityInWorld(entityarrow);
    }

    private void equipAction(Action action)
    {
        EntityEquipmentSlot slot = Mocap.getSlotByIndex(action.armorSlot);

        if (action.armorId == -1)
        {
            this.setItemStackToSlot(slot, null);
        }
        else
        {
            this.setItemStackToSlot(slot, ItemStack.loadItemStackFromNBT(action.itemData));
        }
    }

    private void dropAction(Action action)
    {
        ItemStack items = ItemStack.loadItemStackFromNBT(action.itemData);

        EntityItem ea = new EntityItem(this.worldObj, this.posX, this.posY - 0.30000001192092896D + this.getEyeHeight(), this.posZ, items);
        Random rand = new Random();

        float f = 0.3F;

        ea.motionX = (-MathHelper.sin(this.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.1415927F) * f);
        ea.motionZ = (MathHelper.cos(this.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.1415927F) * f);
        ea.motionY = (-MathHelper.sin(this.rotationPitch / 180.0F * 3.1415927F) * f + 0.1F);
        ea.setDefaultPickupDelay();

        f = 0.02F;
        float f1 = rand.nextFloat() * 3.1415927F * 2.0F;
        f *= rand.nextFloat();

        ea.motionX += Math.cos(f1) * f;
        ea.motionY += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
        ea.motionZ += Math.sin(f1) * f;

        this.worldObj.spawnEntityInWorld(ea);
    }

    /**
     * Mount or dismount action
     */
    private void mountAction(Action action)
    {
        Entity mount = Mocap.entityByUUID(this.worldObj, action.target);

        if (mount == null)
        {
            return;
        }

        if (action.armorSlot == 1)
        {
            this.startRiding(mount);
        }
        else
        {
            this.dismountRidingEntity();
        }
    }

    /**
     * Interact with block
     */
    private void interactBlockAction(Action action)
    {
        BlockPos pos = new BlockPos(action.xCoord, action.yCoord, action.zCoord);
        IBlockState state = this.worldObj.getBlockState(pos);

        state.getBlock().onBlockActivated(this.worldObj, pos, state, null, EnumHand.MAIN_HAND, null, EnumFacing.UP, pos.getX(), pos.getY(), pos.getZ());
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
            this.processActions(this.eventsList.remove(0));
        }

        this.updateArmSwingProgress();

        /* Taken from the EntityDragon, IDK what it does, but the same code
         * was in Mocap's EntityMocap (which is serves like an actor in
         * Mocap mod)
         *
         * It looks like position and rotation interpolation, though */
        if (this.newPosRotationIncrements > 0)
        {
            double d5 = this.posX + (this.interpTargetX - this.posX) / this.newPosRotationIncrements;
            double d0 = this.posY + (this.interpTargetY - this.posY) / this.newPosRotationIncrements;
            double d1 = this.posZ + (this.interpTargetZ - this.posZ) / this.newPosRotationIncrements;
            double d2 = MathHelper.wrapAngleTo180_double(this.interpTargetYaw - this.rotationYaw);

            this.rotationYaw = (float) (this.rotationYaw + d2 / this.newPosRotationIncrements);
            this.rotationPitch = (float) (this.rotationPitch + (this.newPosX - this.rotationPitch) / this.newPosRotationIncrements);
            this.newPosRotationIncrements -= 1;

            this.setPosition(d5, d0, d1);
            this.setRotation(this.rotationYaw, this.rotationPitch);
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
        {}
        else if (!this.worldObj.isRemote)
        {
            this.startRecording(player);
        }

        return true;
    }

    /**
     * Set actor's id on register item (while using register item on this
     * actor)
     */
    private boolean handleRegisterItem(ItemStack stack)
    {
        if (this.worldObj.isRemote || !(stack.getItem() instanceof RegisterItem))
        {
            return false;
        }

        RegisterItem item = (RegisterItem) stack.getItem();

        item.registerStack(stack, this);

        return true;
    }

    /**
     * Open skin choosing GUI by using skin managing item
     */
    private boolean handleSkinItem(ItemStack stack, EntityPlayer player)
    {
        if (!this.worldObj.isRemote || !(stack.getItem() instanceof SkinManagerItem))
        {
            return false;
        }

        player.openGui(Blockbuster.instance, 1, this.worldObj, this.getEntityId(), 0, 0);

        return true;
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

        if (this.directorBlock != null && !Mocap.records.containsKey(player))
        {
            DirectorTileEntity director = (DirectorTileEntity) player.worldObj.getTileEntity(this.directorBlock);

            director.startPlayback(this);
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
