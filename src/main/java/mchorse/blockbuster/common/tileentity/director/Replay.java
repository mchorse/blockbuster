package mchorse.blockbuster.common.tileentity.director;

import java.util.UUID;

import com.google.common.base.Objects;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.common.entity.EntityActor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
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
    public String model = "";
    public ResourceLocation skin;
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

        actor.model = this.model;
        actor.skin = this.skin;
        actor.invisible = this.invisible;
    }

    /**
     * Copy possible properties from entity actor
     */
    public void copy(EntityActor actor)
    {
        this.name = actor.getCustomNameTag();
        this.invincible = actor.isEntityInvulnerable(DamageSource.anvil);

        this.model = actor.model;
        this.skin = actor.skin;
        this.invisible = actor.invisible;

        this.actor = actor.getUniqueID();
    }

    /* to / from NBT */

    public void toNBT(NBTTagCompound tag)
    {
        tag.setString("Id", this.id);
        tag.setString("Name", this.name);
        tag.setBoolean("Invincible", this.invincible);

        tag.setString("Model", this.model);
        tag.setString("Skin", this.skin == null ? "" : this.skin.toString());
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

        this.model = tag.getString("Model");
        this.skin = EntityActor.fromString(tag.getString("Skin"), this.model);
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

        ByteBufUtils.writeUTF8String(buf, this.model);
        ByteBufUtils.writeUTF8String(buf, this.skin == null ? "" : this.skin.toString());
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

        this.model = ByteBufUtils.readUTF8String(buf);
        this.skin = EntityActor.fromString(ByteBufUtils.readUTF8String(buf), this.model);
        this.invisible = buf.readBoolean();

        if (buf.readBoolean())
        {
            this.actor = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).add("id", this.id).add("name", this.name).add("invincible", this.invincible).add("model", this.model).add("skin", this.skin).add("invisible", this.invisible).toString();
    }
}