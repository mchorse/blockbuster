package mchorse.blockbuster.common.tileentity;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.RenderingHandler;
import mchorse.blockbuster.client.render.IRenderLast;
import mchorse.blockbuster.common.block.BlockModel;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModifyModelBlock;
import mchorse.blockbuster.recording.scene.Scene;
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
public class TileEntityModel extends TileEntity implements ITickable, IRenderLast
{
    private static AbstractMorph DEFAULT_MORPH;

    public int lightValue;

    public boolean renderLast;

    public Morph morph = new Morph();
    public EntityLivingBase entity;
    public ItemStack[] slots = new ItemStack[] {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};

    /* Entity rotations */
    public RotationOrder order = RotationOrder.ZYX;
    public float rotateYawHead;
    public float rotatePitch;
    public float rotateBody;

    /* Translation */
    public float x;
    public float y;
    public float z;

    /* Rotation */
    public float rx;
    public float ry;
    public float rz;

    /* Scale */
    public boolean one = false;
    public float sx = 1;
    public float sy = 1;
    public float sz = 1;

    public boolean shadow = true;
    public boolean global = false;
    public boolean enabled = true;

    private long lastModelUpdate;

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

    public TileEntityModel()
    {
        this.morph.setDirect(MorphUtils.copy(getDefaultMorph()));
        this.lastModelUpdate = Scene.lastUpdate;
    }

    public TileEntityModel(float yaw)
    {
        this();
        this.ry = yaw;
    }

    @Override
    public Vector3d getRenderLastPos()
    {
        BlockPos blockPos = this.getPos();

        return new Vector3d(blockPos.getX() + this.x, blockPos.getY() + this.y, blockPos.getZ() + this.z);
    }

    @Override
    public boolean shouldRenderInPass(int pass)
    {
        return super.shouldRenderInPass(pass) && !(this.renderLast && RenderingHandler.addRenderLast(this));
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

        for (int i = 0; i < this.slots.length; i++)
        {
            this.entity.setItemStackToSlot(EntityEquipmentSlot.values()[i], this.slots[i]);
        }

        this.entity.posX = this.pos.getX() + this.x + 0.5;
        this.entity.posY = this.pos.getY() + this.y;
        this.entity.posZ = this.pos.getZ() + this.z + 0.5;
    }

    @Override
    public void update()
    {
        if (this.entity == null)
        {
            this.createEntity(this.world);
        }

        if (this.entity != null && this.enabled)
        {
            this.entity.ticksExisted++;
            this.entity.posX = this.pos.getX() + this.x + 0.5;
            this.entity.posY = this.pos.getY() + this.y;
            this.entity.posZ = this.pos.getZ() + this.z + 0.5;

            if (!this.morph.isEmpty())
            {
                this.morph.get().update(this.entity);
            }
        }

        if (this.lastModelUpdate < Scene.lastUpdate)
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

    public void copyData(TileEntityModel model, boolean merge)
    {
        this.lightValue = model.lightValue;
        this.renderLast = model.renderLast;
        this.order = model.order;
        this.rotateYawHead = model.rotateYawHead;
        this.rotatePitch = model.rotatePitch;
        this.rotateBody = model.rotateBody;
        this.x = model.x;
        this.y = model.y;
        this.z = model.z;
        this.rx = model.rx;
        this.ry = model.ry;
        this.rz = model.rz;
        this.one = model.one;
        this.sx = model.sx;
        this.sy = model.sy;
        this.sz = model.sz;
        this.shadow = model.shadow;
        this.global = model.global;
        this.enabled = model.enabled;

        if (merge)
        {
            this.morph.set(model.morph.get());
        }
        else
        {
            this.morph.setDirect(model.morph.get());
        }

        for (int i = 0; i < model.slots.length; i++)
        {
            ItemStack stack = model.slots[i];

            this.slots[i] = stack.copy();
        }

        this.updateEntity();
        this.markDirty();
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

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        if (this.order != RotationOrder.ZYX) compound.setByte("Order", (byte) this.order.ordinal());

        if (this.lightValue != 0) compound.setInteger("LightValue", this.lightValue);
        if (this.renderLast) compound.setBoolean("RenderLast", this.renderLast);
        if (this.rotateYawHead != 0) compound.setFloat("Yaw", this.rotateYawHead);
        if (this.rotatePitch != 0) compound.setFloat("Pitch", this.rotatePitch);
        if (this.rotateBody != 0) compound.setFloat("Body", this.rotateBody);
        if (this.x != 0) compound.setFloat("ShiftX", this.x);
        if (this.y != 0) compound.setFloat("ShiftY", this.y);
        if (this.z != 0) compound.setFloat("ShiftZ", this.z);
        if (this.rx != 0) compound.setFloat("RotateX", this.rx);
        if (this.ry != 0) compound.setFloat("RotateY", this.ry);
        if (this.rz != 0) compound.setFloat("RotateZ", this.rz);
        if (this.one == true) compound.setBoolean("Scale", this.one);

        if (this.sx != 1) compound.setFloat("ScaleX", this.sx);
        if (this.sy != 1) compound.setFloat("ScaleY", this.sy);
        if (this.sz != 1) compound.setFloat("ScaleZ", this.sz);

        if (!this.shadow) compound.setBoolean("Shadow", this.shadow);
        if (this.global) compound.setBoolean("Global", this.global);
        if (!this.enabled) compound.setBoolean("Enabled", this.enabled);

        NBTTagList list = new NBTTagList();
        int empty = 0;

        for (int i = 0; i < this.slots.length; i++)
        {
            NBTTagCompound tag = new NBTTagCompound();
            ItemStack stack = this.slots[i];

            if (!stack.isEmpty())
            {
                stack.writeToNBT(tag);
            }
            else
            {
                empty += 1;
            }

            list.appendTag(tag);
        }

        if (empty != this.slots.length)
        {
            compound.setTag("Items", list);
        }

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

        if (compound.hasKey("Order"))
        {
            this.order = RotationOrder.values()[compound.getByte("Order")];
        }

        if (compound.hasKey("LightValue")) this.lightValue = compound.getInteger("LightValue");
        if (compound.hasKey("RenderLast")) this.renderLast = compound.getBoolean("RenderLast");
        if (compound.hasKey("Yaw")) this.rotateYawHead = compound.getFloat("Yaw");
        if (compound.hasKey("Pitch")) this.rotatePitch = compound.getFloat("Pitch");
        if (compound.hasKey("Body")) this.rotateBody = compound.getFloat("Body");
        if (compound.hasKey("ShiftX")) this.x = compound.getFloat("ShiftX");
        if (compound.hasKey("ShiftY")) this.y = compound.getFloat("ShiftY");
        if (compound.hasKey("ShiftZ")) this.z = compound.getFloat("ShiftZ");
        if (compound.hasKey("RotateX")) this.rx = compound.getFloat("RotateX");
        if (compound.hasKey("RotateY")) this.ry = compound.getFloat("RotateY");
        if (compound.hasKey("RotateZ")) this.rz = compound.getFloat("RotateZ");
        if (compound.hasKey("Scale")) this.one = compound.getBoolean("Scale");

        if (compound.hasKey("ScaleX")) this.sx = compound.getFloat("ScaleX");
        if (compound.hasKey("ScaleY")) this.sy = compound.getFloat("ScaleY");
        if (compound.hasKey("ScaleZ")) this.sz = compound.getFloat("ScaleZ");

        if (compound.hasKey("Shadow")) this.shadow = compound.getBoolean("Shadow");
        if (compound.hasKey("Global")) this.global = compound.getBoolean("Global");
        if (compound.hasKey("Enabled")) this.enabled = compound.getBoolean("Enabled");

        if (compound.hasKey("Items", 9))
        {
            NBTTagList items = compound.getTagList("Items", 10);

            for (int i = 0, c = items.tagCount(); i < c; i++)
            {
                NBTTagCompound tag = items.getCompoundTagAt(i);
                ItemStack stack = new ItemStack(tag);

                this.slots[i] = stack;
            }
        }

        if (compound.hasKey("Morph", 10))
        {
            this.morph.setDirect(MorphManager.INSTANCE.morphFromNBT(compound.getCompoundTag("Morph")));
        }
    }

    public void fromBytes(ByteBuf buf)
    {
        this.order = RotationOrder.values()[buf.readByte()];

        this.lightValue = buf.readInt();
        this.renderLast = buf.readBoolean();
        this.rotateYawHead = buf.readFloat();
        this.rotatePitch = buf.readFloat();
        this.rotateBody = buf.readFloat();

        this.x = buf.readFloat();
        this.y = buf.readFloat();
        this.z = buf.readFloat();
        this.rx = buf.readFloat();
        this.ry = buf.readFloat();
        this.rz = buf.readFloat();
        this.one = buf.readBoolean();
        this.sx = buf.readFloat();
        this.sy = buf.readFloat();
        this.sz = buf.readFloat();

        this.shadow = buf.readBoolean();
        this.global = buf.readBoolean();
        this.enabled = buf.readBoolean();

        for (int i = 0; i < 6; i++)
        {
            this.slots[i] = buf.readBoolean() ? ByteBufUtils.readItemStack(buf) : null;
        }

        this.morph.setDirect(MorphUtils.morphFromBuf(buf));
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeByte(this.order.ordinal());

        buf.writeInt(this.lightValue);
        buf.writeBoolean(this.renderLast);
        buf.writeFloat(this.rotateYawHead);
        buf.writeFloat(this.rotatePitch);
        buf.writeFloat(this.rotateBody);

        buf.writeFloat(this.x);
        buf.writeFloat(this.y);
        buf.writeFloat(this.z);
        buf.writeFloat(this.rx);
        buf.writeFloat(this.ry);
        buf.writeFloat(this.rz);
        buf.writeBoolean(this.one);
        buf.writeFloat(this.sx);
        buf.writeFloat(this.sy);
        buf.writeFloat(this.sz);

        buf.writeBoolean(this.shadow);
        buf.writeBoolean(this.global);
        buf.writeBoolean(this.enabled);

        for (int i = 0; i < 6; i++)
        {
            ItemStack stack = this.slots[i];

            buf.writeBoolean(stack != null);

            if (stack != null)
            {
                ByteBufUtils.writeItemStack(buf, stack);
            }
        }

        MorphUtils.morphToBuf(buf, this.morph.get());
    }

    /**
     * Rotation order
     */
    public static enum RotationOrder
    {
        ZYX, XYZ;
    }
}