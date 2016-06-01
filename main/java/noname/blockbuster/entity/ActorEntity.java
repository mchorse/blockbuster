package noname.blockbuster.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import noname.blockbuster.item.RegisterItem;
import noname.blockbuster.recording.Action;
import noname.blockbuster.recording.Mocap;

/**
 * Actor entity class
 *
 * Actor entity class is responsible for recording player's actions and execute
 * them. I'm also thinking about giving them controllable AI settings so they
 * could be used without recording (like during the battles between two or more
 * actors).
 */
public class ActorEntity extends EntityCreature
{
    private static final DataParameter<String> RECORDING_ID = EntityDataManager.<String> createKey(ActorEntity.class, DataSerializers.STRING);

    public List<Action> eventsList = Collections.synchronizedList(new ArrayList());

    public ActorEntity(World worldIn)
    {
        super(worldIn);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();

        this.dataWatcher.register(RECORDING_ID, "");
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

    /**
     * Process the actions
     */
    private void processActions(Action action)
    {
        ItemStack foo = null;

        switch (action.type)
        {
            case Action.SWIPE:
                this.swingArm(EnumHand.MAIN_HAND);
                break;

            case Action.EQUIP:
                EntityEquipmentSlot slot = Mocap.getSlotByIndex(action.armorSlot);

                if (action.armorId == -1)
                {
                    this.setItemStackToSlot(slot, null);
                }
                else
                {
                    this.setItemStackToSlot(slot, ItemStack.loadItemStackFromNBT(action.itemData));
                }
                break;

            case Action.DROP:
                foo = ItemStack.loadItemStackFromNBT(action.itemData);

                EntityItem ea = new EntityItem(this.worldObj, this.posX, this.posY - 0.30000001192092896D + this.getEyeHeight(), this.posZ, foo);
                Random rand = new Random();

                float f = 0.3F;

                ea.motionX = (-MathHelper.sin(this.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.1415927F) * f);
                ea.motionZ = (MathHelper.cos(this.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.1415927F) * f);
                ea.motionY = (-MathHelper.sin(this.rotationPitch / 180.0F * 3.1415927F) * f + 0.1F);

                f = 0.02F;
                float f1 = rand.nextFloat() * 3.1415927F * 2.0F;
                f *= rand.nextFloat();

                ea.motionX += Math.cos(f1) * f;
                ea.motionY += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                ea.motionZ += Math.sin(f1) * f;

                this.worldObj.spawnEntityInWorld(ea);
                break;

            case Action.SHOOTARROW:
                this.replayShootArrow(action);
                break;
        }
    }

    /**
     * Adjust the movement and limb swinging action stuff
     */
    @Override
    public void onLivingUpdate()
    {
        if (this.eventsList.size() > 0)
        {
            this.processActions(this.eventsList.remove(0));
        }

        this.updateArmSwingProgress();

        /* Taken from the EntityDragon, IDK what it does */
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

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand p_184645_2_, ItemStack stack)
    {
        if (!this.worldObj.isRemote)
        {
            ItemStack item = player.getHeldItemMainhand();

            if (!(item.getItem() instanceof RegisterItem))
            {
                return false;
            }

            if (item.getTagCompound() == null)
            {
                item.setTagCompound(new NBTTagCompound());
            }

            NBTTagCompound tag = item.getTagCompound();

            if (!tag.hasKey("ActorID") || tag.getString("ActorID") != this.getUniqueID().toString())
            {
                tag.setString("ActorID", this.getUniqueID().toString());
            }
        }

        return false;
    }
}
