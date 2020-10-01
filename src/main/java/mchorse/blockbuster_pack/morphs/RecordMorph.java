package mchorse.blockbuster_pack.morphs;

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
import mchorse.blockbuster.recording.scene.Replay;
import mchorse.mclib.client.gui.framework.elements.utils.GuiInventoryElement;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.utils.Animation;
import mchorse.metamorph.api.morphs.utils.ISyncableMorph;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Objects;

public class RecordMorph extends AbstractMorph implements ISyncableMorph
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

    /**
     * Loop the actor 
     */
    public boolean loop = true;

    /**
     * Random skip factor
     */
    public int randomSkip;

    private boolean initiate;
    private Animation animation = new Animation();
    private Replay replay = new Replay();

    public RecordMorph()
    {
        super();

        this.name = "blockbuster.record";
    }

    public void setRecord(String record)
    {
        this.record = record;
        this.reload = true;
    }

    @Override
    public void pause(AbstractMorph previous, int offset)
    {
        this.animation.pause(offset);
        this.replay.morph = this.initial;
    }

    @Override
    public boolean isPaused()
    {
        return this.animation.paused;
}

    @SideOnly(Side.CLIENT)
    private void previewActor(Record record)
    {
        int tick = this.animation.progress % record.getLength();
        Frame frame = record.getFrame(tick);

        if (frame == null)
        {
            return;
        }

        frame.apply(this.actor, true);

        if (frame.hasBodyYaw)
        {
            this.actor.renderYawOffset = frame.bodyYaw;
        }

        this.actor.prevPosX = this.actor.posX;
        this.actor.prevPosY = this.actor.posY;
        this.actor.prevPosZ = this.actor.posZ;

        this.actor.prevRotationYaw = this.actor.rotationYaw;
        this.actor.prevRotationPitch = this.actor.rotationPitch;
        this.actor.prevRotationYawHead = this.actor.rotationYawHead;
        this.actor.prevRenderYawOffset = this.actor.renderYawOffset;
        this.actor.playback.tick = tick;
        this.actor.playback.playing = false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getSubclassDisplayName()
    {
        return I18n.format("blockbuster.morph.record");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        this.initiateActor(player.world);

        /* Render icon when initial morph isn't available */
        float record = (float) Math.ceil(scale / 16);

        GlStateManager.disableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 1, y - 9, 0);
        GlStateManager.scale(record, record, 1);
        GuiInventoryElement.drawItemStack(ICON, -8, -8, 0, null);
        GlStateManager.popMatrix();
        GlStateManager.enableDepth();

        if (this.initial != null)
        {
            this.initial.renderOnScreen(player, x, y, scale, alpha);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.initiateActor(entity.world);

        AbstractMorph morph = this.actor.getMorph();

        if (morph != null)
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

            morph.render(this.actor, x, y, z, entityYaw, partialTicks);
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
            this.actor.morph.setDirect(MorphUtils.copy(this.initial));
            this.actor.playback = new RecordPlayer(null, Mode.FRAMES, this.actor);
            this.actor.playback.tick = (int) (this.randomSkip * Math.random());
            this.actor.manual = true;

            Record record = ClientProxy.manager.records.get(this.record);

            if (record == null && !this.record.isEmpty())
            {
                Dispatcher.sendToServer(new PacketRequestRecording(this.record));
            }
            else if (this.isPaused() && record != null)
            {
                this.previewActor(record);
                record.applyPreviousMorph(this.actor, this.replay, this.animation.progress, Record.MorphType.PAUSE);
            }
        }
    }

    @Override
    public void update(EntityLivingBase target)
    {
        super.update(target);

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

                if (player.record != null)
                {
                    this.previewActor(player.record);
                }

                if (player.record != null && player.record.actions.isEmpty())
                {
                    /* Just to prevent it from spamming messages */
                    player.record.actions.add(new ArrayList<Action>());
                    Dispatcher.sendToServer(new PacketRequestAction(this.record, false));
                }
            }
            else
            {
                if (this.isPaused() && this.actor.playback.record != null)
                {
                    this.previewActor(this.actor.playback.record);
                    this.actor.playback.record.applyPreviousMorph(this.actor, this.replay, this.animation.progress, Record.MorphType.PAUSE);
                }
                else
                {
                    this.actor.onUpdate();
                }

                if (!this.isPaused() && this.actor.playback.isFinished() && this.loop)
                {
                    this.actor.playback.record.reset(this.actor);
                    this.actor.playback.tick = (int) (this.randomSkip * Math.random());
                    this.actor.playback.record.applyAction(0, this.actor, true);
                    this.actor.morph.setDirect(MorphUtils.copy(this.initial));
                }
            }
        }
    }

    @Override
    public AbstractMorph create()
    {
        return new RecordMorph();
    }

    @Override
    public void copy(AbstractMorph from)
    {
        super.copy(from);

        if (from instanceof RecordMorph)
        {
            RecordMorph morph = (RecordMorph) from;

            this.record = morph.record;
            this.loop = morph.loop;
            this.randomSkip = morph.randomSkip;
            this.initial = MorphUtils.copy(morph.initial);
        }
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
            result = result && record.loop == this.loop;
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
        this.loop = true;
        this.randomSkip = 0;
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

        if (tag.hasKey("Loop", NBT.TAG_ANY_NUMERIC))
        {
            this.loop = tag.getBoolean("Loop");
        }

        if (tag.hasKey("RandomDelay", NBT.TAG_ANY_NUMERIC))
        {
            this.randomSkip = tag.getInteger("RandomDelay");
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

        if (!this.loop)
        {
            tag.setBoolean("Loop", this.loop);
        }

        if (this.randomSkip != 0)
        {
            tag.setInteger("RandomDelay", this.randomSkip);
        }
    }
}