package mchorse.blockbuster.common.entity;

import com.google.common.collect.Queues;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.common.GuiHandler;
import mchorse.blockbuster.common.item.ItemActorConfig;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModifyActor;
import mchorse.blockbuster.network.common.recording.PacketRequestFrames;
import mchorse.blockbuster.network.common.recording.PacketSyncTick;
import mchorse.blockbuster.network.common.recording.actions.PacketRequestAction;
import mchorse.blockbuster.recording.RecordPlayer;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.recording.data.Record.MorphType;
import mchorse.blockbuster.recording.scene.Replay;
import mchorse.metamorph.api.Morph;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.models.IMorphProvider;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityBodyHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Stack;

import javax.annotation.Nullable;

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
public class EntityActor extends EntityCreature implements IEntityAdditionalSpawnData, IMorphProvider
{
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
    public EntityFakePlayer fakePlayer;

    /**
     * In Soviet Russia, playback plays you
     */
    public RecordPlayer playback;

    /**
     * Metamorph's morph for this actor
     */
    public Morph morph = new Morph();

    /* Elytra interpolated animated properties */
    public float rotateElytraX = 0.0F;
    public float rotateElytraY = 0.0F;
    public float rotateElytraZ = 0.0F;

    /**
     * Whether this actor is mounted (used for hacking riding pose for 
     * 3rd party sit-able mods, such as CFM or Quark) 
     */
    public boolean isMounted;

    /**
     * Whether this actor was attached to director block 
     */
    public boolean wasAttached;

    /**
     * Whether the control of playback should be manual 
     */
    public boolean manual = false;

    public int pauseOffset = -1;
    public AbstractMorph pausePreviousMorph;
    public int pausePreviousOffset = -1;
    public boolean forceMorph = false;

    public float prevRoll;
    public float roll;

    public boolean renderLast;

    public Queue<PacketModifyActor> modify = Queues.<PacketModifyActor>newArrayDeque();

    public EntityActor(World worldIn)
    {
        super(worldIn);

        this.fakePlayer = new EntityFakePlayer(worldIn, this, new GameProfile(null, "xXx_Fake_Player_420_xXx"));
        this.fakePlayer.capabilities.isCreativeMode = true;
    }

    @Override
    public AbstractMorph getMorph()
    {
        return this.morph.get();
    }

    /**
     * Check whether this actor is playing
     */
    public boolean isPlaying()
    {
        return this.playback != null && this.playback.playing && !this.playback.isFinished();
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
     * Yes, this boy can be ridden!
     */
    @Override
    protected boolean canBeRidden(Entity entityIn)
    {
        return true;
    }

    /**
     * No, this boy can't be steered!
     */
    @Override
    public boolean canBeSteered()
    {
        return false;
    }

    @Override
    public boolean canPassengerSteer()
    {
        return false;
    }

    /**
     * For vehicles, the first passenger is generally considered the controller and "drives" the vehicle. For example,
     * Pigs, Horses, and Boats are generally "steered" by the controlling passenger.
     */
    @Override
    @Nullable
    public Entity getControllingPassenger()
    {
        return this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);
    }

    /**
     * Give a morph to an actor
     */
    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
    {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setString("Name", "blockbuster.steve");
        this.morph.fromNBT(tag);

        return super.onInitialSpawn(difficulty, livingdata);
    }

    /**
     * Apply first frame for new actor.
     */
    @Override
    public void onEntityUpdate()
    {
        boolean spawn = this.world.isRemote && this.ticksExisted < 2;
        Record record = this.playback == null ? null : this.playback.record;

        spawn &= record != null && record.getFrame(0) != null;

        if (spawn)
        {
            this.playback.applyFrame(this.playback.tick - 1, this, true);

            Frame frame = record.getFrameSafe(this.playback.tick - record.preDelay - 1);

            if (frame.hasBodyYaw)
            {
                this.renderYawOffset = frame.bodyYaw;
            }
        }

        super.onEntityUpdate();

        if (spawn && this.playback.playing)
        {
            playback.applyFrame(this.playback.tick, this, true);

            Frame frame = record.getFrameSafe(this.playback.tick - record.preDelay);

            if (frame.hasBodyYaw)
            {
                this.renderYawOffset = frame.bodyYaw;
            }
        }
    }

    /**
     * Adjust the movement, limb swinging, and process action stuff.
     *
     * See process actions method for more information.
     */
    @Override
    public void onLivingUpdate()
    {
        if (!this.world.isRemote && this.playback != null && this.playback.playing && !this.manual)
        {
            int tick = this.playback.tick;

            if (this.playback.isFinished() && !this.noClip)
            {
                this.playback.stopPlaying();
            }
            else if (tick != 0 && tick % Blockbuster.recordSyncRate.get() == 0)
            {
                Dispatcher.sendToTracked(this, new PacketSyncTick(this.getEntityId(), tick));
            }
        }

        if (this.noClip && !this.world.isRemote)
        {
            if (this.playback != null)
            {
                this.playback.next();
            }

            return;
        }

        this.pickUpNearByItems();

        if (this.playback != null)
        {
            if (this.manual)
            {
                this.playback.applyFrame(this.playback.tick, this, true);
                this.playback.applyAction(this.playback.tick, this, true);
                this.playback.tick++;
            }
            else
            {
                this.playback.next();
            }
        }

        if (this.world.isRemote && this.newPosRotationIncrements > 0)
        {
            double d0 = this.posX + (this.interpTargetX - this.posX) / this.newPosRotationIncrements;
            double d1 = this.posY + (this.interpTargetY - this.posY) / this.newPosRotationIncrements;
            double d2 = this.posZ + (this.interpTargetZ - this.posZ) / this.newPosRotationIncrements;

            this.newPosRotationIncrements--;
            this.setPosition(d0, d1, d2);
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

        this.updateArmSwingProgress();

        /* Make foot steps sound more player-like */
        if (!this.world.isRemote && this.isPlaying() && this.playback.tick < this.playback.record.frames.size() - 1 && !this.isSneaking() && this.onGround)
        {
            Frame current = this.playback.record.frames.get(this.playback.tick);
            Frame next = this.playback.record.frames.get(this.playback.tick + 1);

            double dx = next.x - current.x;
            double dy = next.y - current.y;
            double dz = next.z - current.z;

            this.distanceWalkedModified = this.distanceWalkedModified + MathHelper.sqrt(dx * dx + dz * dz) * 0.32F;
            this.distanceWalkedOnStepModified = this.distanceWalkedOnStepModified + MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 0.32F;
        }

        if (this.playback != null)
        {
            double posX = this.posX;
            double posY = this.posY;
            double posZ = this.posZ;
            double prevPosX = this.prevPosX;
            double prevPosY = this.prevPosY;
            double prevPosZ = this.prevPosZ;

            /* Trigger pressure playback */
            this.travel(this.moveStrafing, this.moveVertical, this.moveForward);

            /* Restore the position from the playback which fixes weird sliding */
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.prevPosX = prevPosX;
            this.prevPosY = prevPosY;
            this.prevPosZ = prevPosZ;
        }
        else
        {
            /* Trigger pressure playback */
            this.travel(this.moveStrafing, this.moveVertical, this.moveForward);
        }
    }

    /**
     * Update fall state.
     *
     * This override is responsible for applying fall damage on the actor.
     * {@link #move(MoverType, double, double, double)} seem to override onGround
     * property wrongly on the server.
     */
    @Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos)
    {
        if (!this.world.isRemote && Blockbuster.actorFallDamage.get() && this.playback != null)
        {
            int tick = this.playback.getTick();

            /* Override onGround field */
            if (tick >= 1 && tick < this.playback.record.frames.size())
            {
                this.onGround = onGroundIn = this.playback.record.frames.get(tick - 1).onGround;
            }
        }

        super.updateFallState(y, onGroundIn, state, pos);
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
        if (!this.world.isRemote && !this.dead)
        {
            for (EntityItem entityitem : this.world.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().expand(1.0D, 0.0D, 1.0D)))
            {
                if (!entityitem.isDead && entityitem.getItem() != null && !entityitem.cannotPickup())
                {
                    this.onItemPickup(entityitem, 1);
                    entityitem.setDead();
                }
            }
        }
    }

    /**
     * Roll back to {@link EntityLivingBase}'s updateDistance methods.
     *
     * Its implementation supports much superior renderYawOffset animation.
     * Well, at least that's what I think. I should check out
     * {@link EntityBodyHelper} before making final decision.
    */
    @Override
    protected float updateDistance(float renderYawOffset, float distance)
    {
        boolean shouldAutoAlign = true;

        if (Blockbuster.actorPlaybackBodyYaw.get() && this.playback != null && this.playback.record != null)
        {
            Frame previous = this.playback.record.getFrame(this.playback.getTick() - 1);
            Frame frame = this.playback.getCurrentFrame();

            if (frame != null && frame.hasBodyYaw)
            {
                this.renderYawOffset = frame.bodyYaw;
                this.prevRenderYawOffset = previous == null || !this.playback.playing ? frame.bodyYaw : previous.bodyYaw;

                shouldAutoAlign = false;
            }
        }

        if (shouldAutoAlign)
        {
            float tempRenderYawOffset = MathHelper.wrapDegrees(renderYawOffset - this.renderYawOffset);
            this.renderYawOffset += tempRenderYawOffset * 0.3F;
            tempRenderYawOffset = MathHelper.wrapDegrees(this.rotationYaw - this.renderYawOffset);
            boolean isBackwards = tempRenderYawOffset < -90.0F || tempRenderYawOffset >= 90.0F;

            if (tempRenderYawOffset < -75.0F)
            {
                tempRenderYawOffset = -75.0F;
            }

            if (tempRenderYawOffset >= 75.0F)
            {
                tempRenderYawOffset = 75.0F;
            }

            this.renderYawOffset = this.rotationYaw - tempRenderYawOffset;

            if (tempRenderYawOffset * tempRenderYawOffset > 2500.0F)
            {
                this.renderYawOffset += tempRenderYawOffset * 0.2F;
            }

            if (isBackwards)
            {
                distance *= -1.0F;
            }
        }

        /* Explanation: Why do we update morph here? Because for some reason
         * the EntityMorph morphs don't turn smoothly in onLivingUpdate method */
        AbstractMorph morph = this.morph.get();

        if (morph != null)
        {
            morph.update(this);
        }

        while (!this.modify.isEmpty())
        {
            this.applyModifyPacket(this.modify.poll());
        }

        return distance;
    }

    /* Processing interaction with player */

    /**
     * Process interact
     *
     * Inject UUID of actor to registering device, open GUI for changing actor's
     * skin, or start recording him
     */
    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand)
    {
        ItemStack item = player.getHeldItem(hand);
        boolean empty = item.isEmpty();

        if (empty)
        {
            if (!this.world.isRemote && !Blockbuster.actorDisableRiding.get())
            {
                if (!player.isSneaking())
                {
                    player.startRiding(this);
                }
            }

            return true;
        }
        else if (item.getItem() instanceof ItemActorConfig)
        {
            player.openGui(Blockbuster.instance, GuiHandler.ACTOR, player.world, this.getEntityId(), 0, 0);

            return true;
        }

        return false;
    }

    /* Public API */

    public void applyModifyPacket(PacketModifyActor message)
    {
        this.forceMorph = message.forceMorph;

        if (message.offset >= 0)
        {
            this.invisible = message.invisible;
            this.applyPause(message.morph, message.offset, message.previous, message.previousOffset, this.forceMorph);

            if (this.forceMorph)
            {
                this.pauseOffset = -1;
                this.pausePreviousMorph = null;
                this.pausePreviousOffset = -1;
                this.forceMorph = false;
            }
        }
        else
        {
            this.modify(message.morph, message.invisible, false);
        }
    }

    /**
     * Configure this actor
     *
     * Takes four properties to modify: filename used as id for recording,
     * displayed name, rendering skin and invulnerability flag
     */
    public void modify(AbstractMorph morph, boolean invisible, boolean notify)
    {
        if (this.forceMorph)
        {
            this.morph.setDirect(morph);
        }
        else
        {
            this.morph.set(morph);
        }

        this.invisible = invisible;

        if (!this.world.isRemote && notify)
        {
            this.notifyPlayers();
        }
    }

    /**
     * Morph this actor into given morph (used on the server side)
     */
    public void morph(AbstractMorph morph, boolean force)
    {
        this.pauseOffset = -1;
        this.pausePreviousMorph = null;
        this.pausePreviousOffset = -1;
        this.forceMorph = force;

        this.morph.set(morph);
    }

    /**
     * Stores the data for paused morph
     */
    public void morphPause(AbstractMorph morph, int offset, AbstractMorph previous, int previousOffset, boolean resume)
    {
        this.pauseOffset = offset;
        this.pausePreviousMorph = previous;
        this.pausePreviousOffset = previousOffset;
        this.forceMorph = resume;

        this.morph.setDirect(morph);
    }

    /**
     * Apply pause on the morph
     */
    public void applyPause(AbstractMorph morph, int offset, AbstractMorph previous, int previousOffset, boolean resume)
    {
        this.morphPause(morph, offset, previous, previousOffset, resume);

        MorphUtils.pause(previous, null, previousOffset);
        MorphUtils.pause(morph, previous, offset);
        
        if (resume)
        {
            MorphUtils.resume(morph);
        }
    }

    /**
     * Notify trackers of data changes happened in this actor
     */
    public void notifyPlayers()
    {
        if (!this.manual)
        {
            this.playback.sendToTracked(new PacketModifyActor(this));
        }
    }

    /* Reading/writing to disk */

    @Override
    public void readEntityFromNBT(NBTTagCompound tag)
    {
        super.readEntityFromNBT(tag);

        this.morph.setDirect(MorphManager.INSTANCE.morphFromNBT(tag.getCompoundTag("Morph")));
        this.invisible = tag.getBoolean("Invisible");
        this.wasAttached = tag.getBoolean("WasAttached");

        if (!this.world.isRemote)
        {
            this.notifyPlayers();
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag)
    {
        super.writeEntityToNBT(tag);

        if (!this.morph.isEmpty())
        {
            tag.setTag("Morph", this.morph.get().toNBT());
        }

        tag.setBoolean("Invisible", this.invisible);
        tag.setBoolean("WasAttached", this.wasAttached);
    }

    /* IEntityAdditionalSpawnData implementation */

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        if (this.wasAttached && this.playback == null)
        {
            this.setDead();
        }

        MorphUtils.morphToBuf(buffer, this.morph.get());

        buffer.writeBoolean(this.invisible);
        buffer.writeBoolean(this.noClip);
        buffer.writeBoolean(this.playback != null);

        if (this.playback != null)
        {
            buffer.writeBoolean(this.playback.playing);
            buffer.writeInt(this.playback.tick);
            ByteBufUtils.writeUTF8String(buffer, this.playback.record.filename);

            buffer.writeBoolean(this.playback.replay != null && this.playback.replay.morph != null);

            if (this.playback.replay != null && this.playback.replay.morph != null)
            {
                MorphUtils.morphToBuf(buffer, this.playback.replay.morph);
            }
        }

        buffer.writeBoolean(this.isEntityInvulnerable(DamageSource.ANVIL));
        buffer.writeBoolean(this.renderLast);
    }

    @Override
    public void readSpawnData(ByteBuf buffer)
    {
        this.morph.setDirect(MorphUtils.morphFromBuf(buffer));
        this.invisible = buffer.readBoolean();
        this.noClip = buffer.readBoolean();

        if (buffer.readBoolean())
        {
            boolean playing = buffer.readBoolean();
            int tick = buffer.readInt();
            String filename = ByteBufUtils.readUTF8String(buffer);
            Replay replay = null;

            if (buffer.readBoolean())
            {
                replay = new Replay();
                replay.morph = MorphUtils.morphFromBuf(buffer);
            }

            if (this.playback == null)
            {
                Record record = ClientProxy.manager.getClient(filename);

                if (record != null)
                {
                    this.playback = new RecordPlayer(record, Mode.FRAMES, this);

                    record.applyPreviousMorph(this, replay, tick - record.preDelay, playing ? MorphType.FORCE : MorphType.PAUSE);
                }
                else
                {
                    this.playback = new RecordPlayer(null, Mode.FRAMES, this);

                    Dispatcher.sendToServer(new PacketRequestAction(filename, false));
                    Dispatcher.sendToServer(new PacketRequestFrames(this.getEntityId(), filename));
                }
            }

            if (this.playback != null)
            {
                this.playback.tick = tick;
                this.playback.playing = playing;
            }
        }

        this.setEntityInvulnerable(buffer.readBoolean());
        this.renderLast = buffer.readBoolean();
    }

    @Override
    public boolean canBeLeashedTo(EntityPlayer player) {
        return !this.getLeashed();
    }

    /**
     * Used by playback code to set item animation thingy
     */
    public void setItemStackInUse(int activeCount)
    {
        this.activeItemStackUseCount = activeCount;
    }

    /**
     * Is actor in range in render distance
     *
     * This method is responsible for checking if this entity is 
     * available for rendering. Rendering range is configurable.
     */
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInRangeToRenderDist(double distance)
    {
        double d0 = this.getEntityBoundingBox().getAverageEdgeLength();

        if (Double.isNaN(d0))
        {
            d0 = 1.0D;
        }

        d0 = d0 * Blockbuster.actorRenderingRange.get();
        return distance < d0 * d0;
    }

    public static class EntityFakePlayer extends EntityPlayer
    {
        public EntityActor actor;

        public EntityFakePlayer(World world, EntityActor actor, GameProfile profile)
        {
            super(world, profile);

            this.actor = actor;
        }

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

        @Override
        public void onUpdate()
        {
            if (this.actor.isDead)
            {
                this.setDead();

                return;
            }

            this.width = actor.width;
            this.height = actor.height;
            this.eyeHeight = actor.getEyeHeight();
            this.setEntityBoundingBox(actor.getEntityBoundingBox());

            this.posX = actor.posX;
            this.posY = actor.posY;
            this.posZ = actor.posZ;
            this.rotationYaw = actor.rotationYaw;
            this.rotationPitch = actor.rotationPitch;

            if (!Objects.equals(this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), actor.getHeldItemMainhand()))
            {
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, actor.getHeldItemMainhand());
            }

            if (!Objects.equals(this.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND), actor.getHeldItemOffhand()))
            {
                this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, actor.getHeldItemOffhand());
            }
        }

        @Override
        public void displayGUIChest(IInventory chestInventory)
        {
            if (this.openContainer != this.inventoryContainer)
            {
                this.closeScreen();
            }

            if (chestInventory instanceof IInteractionObject)
            {
                this.openContainer = ((IInteractionObject)chestInventory).createContainer(this.inventory, this);
            }
            else
            {
                this.openContainer = new ContainerChest(this.inventory, chestInventory, this);
            }
        }

        @Override
        public void closeScreen()
        {
            this.openContainer.onContainerClosed(this);

            super.closeScreen();
        }

        @Override
        public void readFromNBT(NBTTagCompound compound)
        {
            this.setDead();
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound)
        {
            return compound;
        }
    }
}