package mchorse.blockbuster.recording.scene;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster_pack.MorphUtils;
import mchorse.metamorph.api.MorphAPI;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.vanilla_pack.morphs.PlayerMorph;
import net.minecraft.entity.EntityLivingBase;
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
    public boolean teleportBack = true;

    /* Visual data */
    public AbstractMorph morph;
    public boolean invisible = false;
    public boolean enabled = true;
    public boolean fake = false;
    public float health = 20F;

    public Replay()
    {}

    public Replay(String id)
    {
        this.id = id;
    }

    /**
     * Apply replay on an entity 
     */
    public void apply(EntityLivingBase entity)
    {
        if (entity instanceof EntityActor)
        {
            this.apply((EntityActor) entity);
        }
        else if (entity instanceof EntityPlayer)
        {
            if (!(this.morph instanceof PlayerMorph))
            {
                this.apply((EntityPlayer) entity);
            }
        }
    }

    /**
     * Apply replay on an actor
     */
    public void apply(EntityActor actor)
    {
        boolean remote = actor.world.isRemote;

        actor.setCustomNameTag(this.name);
        actor.setEntityInvulnerable(this.invincible);
        actor.morph.set(mchorse.metamorph.api.MorphUtils.copy(this.morph));
        actor.invisible = this.invisible;
        actor.setHealth(this.health);
        actor.notifyPlayers();
    }

    /**
     * Apply replay on a player 
     */
    public void apply(EntityPlayer player)
    {
        MorphAPI.morph(player, mchorse.metamorph.api.MorphUtils.copy(this.morph), true);
        player.setHealth(this.health);
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
        tag.setBoolean("Fake", this.fake);
        if (!this.teleportBack) tag.setBoolean("TP", this.teleportBack);
        if (this.health != 20) tag.setFloat("Health", this.health);
    }

    public void fromNBT(NBTTagCompound tag)
    {
        this.id = tag.getString("Id");
        this.name = tag.getString("Name");

        this.morph = MorphUtils.morphFromNBT(tag);

        this.invincible = tag.getBoolean("Invincible");
        this.invisible = tag.getBoolean("Invisible");
        this.fake = tag.getBoolean("Fake");

        if (tag.hasKey("Enabled")) this.enabled = tag.getBoolean("Enabled");
        if (tag.hasKey("TP")) this.teleportBack = tag.getBoolean("TP");
        if (tag.hasKey("Health")) this.health = tag.getFloat("Health");
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
        buf.writeBoolean(this.fake);
        buf.writeBoolean(this.teleportBack);
        buf.writeFloat(this.health);
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
        this.fake = buf.readBoolean();
        this.teleportBack = buf.readBoolean();
        this.health = buf.readFloat();
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
        replay.morph = mchorse.metamorph.api.MorphUtils.copy(this.morph);
        replay.invincible = this.invincible;
        replay.invisible = this.invisible;
        replay.enabled = this.enabled;
        replay.fake = this.fake;
        replay.teleportBack = this.teleportBack;
        replay.health = this.health;

        return replay;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this).add("id", this.id).add("name", this.name).add("invincible", this.invincible).add("morph", this.morph).add("invisible", this.invisible).toString();
    }
}