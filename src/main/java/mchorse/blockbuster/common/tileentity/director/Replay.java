package mchorse.blockbuster.common.tileentity.director;

import java.util.UUID;

import com.google.common.base.Objects;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster_pack.MorphUtils;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
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

    /* UUID */
    public UUID actor;

    public Replay()
    {}

    public Replay(EntityActor actor)
    {
        this.copy(actor);
    }

    /**
     * Apply replay on an actor
     */
    public void apply(EntityActor actor)
    {
        actor.setCustomNameTag(this.name);
        actor.setEntityInvulnerable(this.invincible);

        if (this.morph != null)
        {
            actor.morph = this.morph.clone(actor.world.isRemote);
        }

        actor.invisible = this.invisible;
    }

    /**
     * Copy possible properties from entity actor
     */
    public void copy(EntityActor actor)
    {
        this.name = actor.getCustomNameTag();
        this.invincible = actor.isEntityInvulnerable(DamageSource.ANVIL);

        if (actor.morph != null && this.morph == null)
        {
            this.morph = actor.getMorph().clone(actor.world.isRemote);
        }

        this.invisible = actor.invisible;
        this.actor = actor.getUniqueID();
    }

    /* to / from NBT */

    public void toNBT(NBTTagCompound tag)
    {
        tag.setString("Id", this.id);
        tag.setString("Name", this.name);
        tag.setBoolean("Invincible", this.invincible);

        MorphUtils.morphToNBT(tag, this.morph);

        tag.setBoolean("Invisible", this.invisible);

        if (this.actor != null)
        {
            tag.setString("UUID", this.actor.toString());
        }
    }

    public void fromNBT(NBTTagCompound tag)
    {
        this.id = tag.getString("Id");
        this.name = tag.getString("Name");
        this.invincible = tag.getBoolean("Invincible");

        this.morph = MorphUtils.morphFromNBT(tag);

        this.invisible = tag.getBoolean("Invisible");

        String uuid = tag.getString("UUID");

        if (!uuid.isEmpty())
        {
            this.actor = UUID.fromString(uuid);
        }
    }

    /* to / from ByteBuf */

    public void toBuf(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.id);
        ByteBufUtils.writeUTF8String(buf, this.name);
        buf.writeBoolean(this.invincible);
        buf.writeBoolean(this.morph != null);

        if (this.morph != null)
        {
            NBTTagCompound tag = new NBTTagCompound();
            this.morph.toNBT(tag);

            ByteBufUtils.writeTag(buf, tag);
        }

        buf.writeBoolean(this.invisible);
        buf.writeBoolean(this.actor != null);

        if (this.actor != null)
        {
            ByteBufUtils.writeUTF8String(buf, this.actor.toString());
        }
    }

    public void fromBuf(ByteBuf buf)
    {
        this.id = ByteBufUtils.readUTF8String(buf);
        this.name = ByteBufUtils.readUTF8String(buf);
        this.invincible = buf.readBoolean();

        if (buf.readBoolean())
        {
            this.morph = MorphManager.INSTANCE.morphFromNBT(ByteBufUtils.readTag(buf));
        }

        this.invisible = buf.readBoolean();

        if (buf.readBoolean())
        {
            this.actor = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        }
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