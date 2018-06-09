package mchorse.blockbuster.common.tileentity.director;

import com.google.common.base.Objects;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster_pack.MorphUtils;
import mchorse.metamorph.api.MorphAPI;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * Replay domain object
 *
 * This class is responsible for storing, and persisting to different sources
 * (to NBT and ByteBuf) its content.
 */
public class Replay
{
    /* Meta data */
    public String id = "";
    public String name = "";
    public boolean invincible = false;

    /* Visual data */
    public AbstractMorph morph;
    public boolean invisible = false;
    public boolean enabled = true;
    public boolean fakePlayer = false;
    public int health = 20;

    public Replay()
    {}

    public Replay(String id)
    {
        this.id = id;
    }

    /**
     * Apply replay on an actor
     */
    public void apply(EntityActor actor)
    {
        actor.setCustomNameTag(this.name);
        actor.setEntityInvulnerable(this.invincible);
        actor.morph = this.morph == null ? null : this.morph.clone(actor.worldObj.isRemote);
        actor.invisible = this.invisible;
        actor.setHealth(this.health);
        actor.notifyPlayers();
    }

    /**
     * Apply replay on a player 
     */
    public void apply(EntityPlayer player)
    {
        MorphAPI.morph(player, this.morph, true);
    }

    /* to / from NBT */

    public void toNBT(NBTTagCompound tag)
    {
        tag.setString("Id", this.id);
        tag.setString("Name", this.name);

        MorphUtils.morphToNBT(tag, this.morph);

        tag.setBoolean("Invincible", this.invincible);
        tag.setBoolean("Invisible", this.invisible);
        tag.setBoolean("Enabled", this.enabled);
    }

    public void fromNBT(NBTTagCompound tag)
    {
        this.id = tag.getString("Id");
        this.name = tag.getString("Name");

        this.morph = MorphUtils.morphFromNBT(tag);

        this.invincible = tag.getBoolean("Invincible");
        this.invisible = tag.getBoolean("Invisible");

        if (tag.hasKey("Enabled"))
        {
            this.enabled = tag.getBoolean("Enabled");
        }
    }

    /* to / from ByteBuf */

    public void toBuf(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.id);
        ByteBufUtils.writeUTF8String(buf, this.name);
        buf.writeBoolean(this.morph != null);

        if (this.morph != null)
        {
            NBTTagCompound tag = new NBTTagCompound();
            this.morph.toNBT(tag);

            ByteBufUtils.writeTag(buf, tag);
        }

        buf.writeBoolean(this.invincible);
        buf.writeBoolean(this.invisible);
        buf.writeBoolean(this.enabled);
    }

    public void fromBuf(ByteBuf buf)
    {
        this.id = ByteBufUtils.readUTF8String(buf);
        this.name = ByteBufUtils.readUTF8String(buf);

        if (buf.readBoolean())
        {
            this.morph = MorphManager.INSTANCE.morphFromNBT(ByteBufUtils.readTag(buf));
        }

        this.invincible = buf.readBoolean();
        this.invisible = buf.readBoolean();
        this.enabled = buf.readBoolean();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Replay)
        {
            Replay replay = (Replay) obj;

            return Objects.equal(replay.id, this.id) && Objects.equal(replay.name, this.name) && replay.invincible == this.invincible && replay.invisible == this.invisible && Objects.equal(replay.morph, this.morph);
        }

        return super.equals(obj);
    }

    public Replay clone(boolean isRemote)
    {
        Replay replay = new Replay();

        replay.id = this.id;
        replay.name = this.name;
        replay.invincible = this.invincible;

        replay.invisible = this.invisible;

        if (this.morph != null)
        {
            replay.morph = this.morph.clone(isRemote);
        }

        return replay;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).add("id", this.id).add("name", this.name).add("invincible", this.invincible).add("morph", this.morph).add("invisible", this.invisible).toString();
    }
}