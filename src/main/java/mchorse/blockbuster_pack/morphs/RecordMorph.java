package mchorse.blockbuster_pack.morphs;

import java.util.ArrayList;
import java.util.Objects;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.recording.PacketRequestRecording;
import mchorse.blockbuster.network.common.recording.actions.PacketRequestAction;
import mchorse.blockbuster.recording.RecordPlayer;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.blockbuster.recording.data.Record;
import mchorse.mclib.client.gui.widgets.GuiInventory;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RecordMorph extends AbstractMorph
{
    /**
     * Record morph's icon in GUI 
     */
    public static final ItemStack ICON = new ItemStack(Items.RECORD_13);

    @SideOnly(Side.CLIENT)
    public EntityActor actor;

    public boolean reload;

    /**
     * Initial morph
     */
    public AbstractMorph initial;

    /**
     * Record which should be played on this morph
     */
    public String record = "";

    private boolean initiate;

    public RecordMorph()
    {
        this.name = "blockbuster.record";
    }

    @Override
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        this.initiateActor(player.world);

        if (this.initial == null)
        {
            /* Render icon when initial morph isn't available */
            scale = (float) Math.ceil(scale / 16);

            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 1, y - 9, 0);
            GlStateManager.scale(scale, scale, 1);
            GuiInventory.drawItemStack(ICON, -8, -8, null);
            GlStateManager.popMatrix();
        }
        else if (this.actor.morph != null)
        {
            this.actor.morph.renderOnScreen(player, x, y, scale, alpha);
        }
    }

    @Override
    public void render(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.initiateActor(entity.world);

        if (this.actor.morph != null)
        {
            if (this.actor.playback.record != null)
            {
                Frame first = this.actor.playback.record.getFrame(0);

                if (first != null)
                {
                    if (!this.initiate)
                    {
                        this.actor.prevPosX = this.actor.posX = first.x;
                        this.actor.prevPosY = this.actor.posY = first.y;
                        this.actor.prevPosZ = this.actor.posZ = first.z;
                        this.initiate = true;
                    }

                    x += (this.actor.prevPosX + (this.actor.posX - this.actor.prevPosX) * partialTicks) - first.x;
                    y += (this.actor.prevPosY + (this.actor.posY - this.actor.prevPosY) * partialTicks) - first.y;
                    z += (this.actor.prevPosZ + (this.actor.posZ - this.actor.prevPosZ) * partialTicks) - first.z;
                }
            }

            this.actor.morph.render(this.actor, x, y, z, entityYaw, partialTicks);
        }
    }

    @SideOnly(Side.CLIENT)
    private void initiateActor(World world)
    {
        if (this.reload)
        {
            this.actor = null;
            this.initiate = false;
            this.reload = false;
        }

        if (this.actor == null)
        {
            this.actor = new EntityActor(world);
            this.actor.morph = this.initial == null ? null : this.initial.clone(world.isRemote);
            this.actor.playback = new RecordPlayer(null, Mode.FRAMES, this.actor);
            this.actor.manual = true;

            Record record = ClientProxy.manager.records.get(this.record);

            if (record == null && !this.record.isEmpty())
            {
                Dispatcher.sendToServer(new PacketRequestRecording(this.record));
            }
        }
    }

    @Override
    public void update(EntityLivingBase target, IMorphing cap)
    {
        super.update(target, cap);

        if (target.world.isRemote)
        {
            this.updateActor();
        }
    }

    @SideOnly(Side.CLIENT)
    private void updateActor()
    {
        if (this.actor != null)
        {
            RecordPlayer player = this.actor.playback;

            if (player.record == null)
            {
                player.record = ClientProxy.manager.records.get(this.record);

                if (player.record != null && player.record.actions.isEmpty())
                {
                    /* Just to prevent it from spamming messages */
                    player.record.actions.add(new ArrayList<Action>());
                    Dispatcher.sendToServer(new PacketRequestAction(this.record));
                }
            }
            else
            {
                this.actor.onUpdate();

                if (this.actor.playback.isFinished())
                {
                    this.actor.playback.record.reset(this.actor);
                    this.actor.playback.tick = 0;
                    this.actor.playback.record.applyAction(0, this.actor, true);
                    this.actor.morph = this.initial;
                }
            }
        }
    }

    @Override
    public AbstractMorph clone(boolean isRemote)
    {
        RecordMorph morph = new RecordMorph();

        morph.name = this.name;
        morph.settings = this.settings;
        morph.record = this.record;

        if (this.initial != null)
        {
            morph.initial = this.initial.clone(isRemote);
        }

        return morph;
    }

    @Override
    public float getWidth(EntityLivingBase target)
    {
        return 0.6F;
    }

    @Override
    public float getHeight(EntityLivingBase target)
    {
        return 1.8F;
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof RecordMorph)
        {
            RecordMorph record = (RecordMorph) obj;

            result = result && Objects.equals(record.record, this.record);
            result = result && Objects.equals(record.initial, this.initial);
        }

        return result;
    }

    @Override
    public void reset()
    {
        super.reset();

        this.initial = null;
        this.record = "";
        this.reload = true;
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        if (tag.hasKey("Initial", NBT.TAG_COMPOUND))
        {
            this.initial = MorphManager.INSTANCE.morphFromNBT(tag.getCompoundTag("Initial"));
        }

        if (tag.hasKey("Record", NBT.TAG_STRING))
        {
            this.record = tag.getString("Record");
        }
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        if (this.initial != null)
        {
            NBTTagCompound morph = new NBTTagCompound();

            this.initial.toNBT(morph);
            tag.setTag("Initial", morph);
        }

        if (!this.record.isEmpty())
        {
            tag.setString("Record", this.record);
        }
    }

    public void setRecord(String record)
    {
        this.record = record;
        this.reload = true;
    }
}