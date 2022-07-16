package mchorse.blockbuster.common.tileentity;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.RenderingHandler;
import mchorse.blockbuster.client.render.IRenderLast;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModifyModelBlock;
import mchorse.blockbuster.recording.scene.Scene;
import mchorse.mclib.network.IByteBufSerializable;
import mchorse.mclib.utils.ValueSerializer;
import mchorse.metamorph.api.Morph;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector3d;

/**
 * Model tile entity
 * 
 * This little guy is responsible for storing visual data about model's 
 * rendering.
 */
public class TileEntityModel extends TileEntity implements ITickable, IRenderLast, IByteBufSerializable
{
    private static AbstractMorph DEFAULT_MORPH;

    public Morph morph = new Morph();
    public EntityLivingBase entity;

    private long lastModelUpdate;
    private TileEntityModelSettings settings = new TileEntityModelSettings();

    public TileEntityModel()
    {
        this.morph.setDirect(MorphUtils.copy(getDefaultMorph()));

        this.lastModelUpdate = Scene.lastUpdate;
    }

    public TileEntityModel(float yaw)
    {
        this();

        this.settings.setRy(yaw);
    }

    /**
     * @return reference to this {@link #settings} object.
     */
    public TileEntityModelSettings getSettings()
    {
        return this.settings;
    }

    public static AbstractMorph getDefaultMorph()
    {
        if (DEFAULT_MORPH == null)
        {
            NBTTagCompound tag = new NBTTagCompound();

            tag.setString("Name", "blockbuster.fred");

            DEFAULT_MORPH = MorphManager.INSTANCE.morphFromNBT(tag);
        }

        return DEFAULT_MORPH;
    }

    @Override
    public Vector3d getRenderLastPos()
    {
        BlockPos blockPos = this.getPos();

        return new Vector3d(blockPos.getX() + this.settings.getX(),
                         blockPos.getY() + this.settings.getY(),
                         blockPos.getZ() + this.settings.getZ());
    }

    @Override
    public boolean shouldRenderInPass(int pass)
    {
        return super.shouldRenderInPass(pass) && !(this.settings.isRenderLast() && RenderingHandler.addRenderLast(this));
    }

    public void setMorph(AbstractMorph morph)
    {
        this.morph.set(morph);
        this.markDirty();
    }

    public void createEntity(World world)
    {
        if (world == null)
        {
            return;
        }

        this.entity = new EntityActor(world);
        this.entity.onGround = true;
        this.updateEntity();
    }

    public void updateEntity()
    {
        if (this.entity == null)
        {
            return;
        }

        for (int i = 0; i < this.settings.getSlots().length; i++)
        {
            this.entity.setItemStackToSlot(EntityEquipmentSlot.values()[i], this.settings.getSlots()[i]);
        }

        this.entity.posX = this.pos.getX() + this.settings.getX() + 0.5;
        this.entity.posY = this.pos.getY() + this.settings.getY();
        this.entity.posZ = this.pos.getZ() + this.settings.getZ() + 0.5;
    }

    @Override
    public void update()
    {
        if (this.entity == null)
        {
            this.createEntity(this.world);
        }

        if (this.entity != null && this.settings.isEnabled())
        {
            this.entity.ticksExisted++;
            this.entity.posX = this.pos.getX() + this.settings.getX() + 0.5;
            this.entity.posY = this.pos.getY() + this.settings.getY();
            this.entity.posZ = this.pos.getZ() + this.settings.getZ() + 0.5;

            if (!this.morph.isEmpty())
            {
                this.morph.get().update(this.entity);
            }
        }

        if (this.lastModelUpdate < Scene.lastUpdate && !this.settings.isExcludeResetPlayback())
        {
            if (this.world != null && !this.world.isRemote)
            {
                BlockPos pos = this.pos;
                PacketModifyModelBlock message = new PacketModifyModelBlock(pos, this);
                NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64);

                Dispatcher.DISPATCHER.get().sendToAllAround(message, point);
            }

            this.lastModelUpdate = Scene.lastUpdate;
        }
    }

    /**
     * Infinite extend AABB allows to avoid frustum culling which can be 
     * used for some interesting things (like placing a whole OBJ level 
     * in the game)
     */
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return TileEntity.INFINITE_EXTENT_AABB;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        float range = Blockbuster.actorRenderingRange.get();

        return range * range;
    }

    /**
     * Dont refresh tile entity when blockstate changes - only when block changes
     * @param world
     * @param pos
     * @param oldState
     * @param newSate
     * @return
     */
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return (oldState.getBlock() != newSate.getBlock());
    }

    /* NBT methods */

    /**
     * That's important too for 
     * {@link #onDataPacket(NetworkManager, SPacketUpdateTileEntity)} to 
     * fix the flower pot thing. 
     */
    @Override
    public NBTTagCompound getUpdateTag()
    {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), this.getUpdateTag());
    }

    /**
     * This method fixes the old thing with flower pot thanks to asie!
     * 
     * @link https://www.reddit.com/r/feedthebeast/comments/b7h6fb/modders_what_embarrassingdirty_trick_did_you_do/ejtdydo/?context=3
     */
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        this.readFromNBT(pkt.getNbtCompound());
    }

    public void copyData(TileEntityModel model, boolean merge)
    {
        this.settings.copy(model.settings);

        if (merge)
        {
            this.morph.set(model.morph.get());
        }
        else
        {
            this.morph.setDirect(model.morph.get());
        }

        this.updateEntity();
        this.markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        this.settings.toNBT(compound);

        if (!this.morph.isEmpty())
        {
            compound.setTag("Morph", this.morph.toNBT());
        }

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        this.settings.fromNBT(compound);

        if (compound.hasKey("Morph", 10))
        {
            this.morph.setDirect(MorphManager.INSTANCE.morphFromNBT(compound.getCompoundTag("Morph")));
        }
    }

    public void fromBytes(ByteBuf buf)
    {
        this.settings.fromBytes(buf);

        this.morph.setDirect(MorphUtils.morphFromBuf(buf));
    }

    public void toBytes(ByteBuf buf)
    {
        this.settings.toBytes(buf);

        MorphUtils.morphToBuf(buf, this.morph.get());
    }
}