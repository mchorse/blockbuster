package mchorse.blockbuster.recording.scene;

import com.google.common.base.Objects;
import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.mclib.utils.TextUtils;
import mchorse.metamorph.api.MorphAPI;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.vanilla_pack.morphs.PlayerMorph;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
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
    public String target = "";
    public boolean invincible = false;
    public boolean enableBurning = true;
    public boolean teleportBack = true;
    /**
     * Whether the food and XP recording should be played back
     */
    public boolean playBackXPFood = false;

    /* Visual data */
    public AbstractMorph morph;
    public boolean invisible = false;
    public boolean enabled = true;
    public boolean fake = false;
    public float health = 20F;
    public boolean renderLast = false;
    public int foodLevel = 20;
    public int totalExperience = 0;

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
        String name = TextUtils.processColoredText(this.name);

        actor.setCustomNameTag(name);
        actor.setEntityInvulnerable(this.invincible);
        actor.morph(mchorse.metamorph.api.MorphUtils.copy(this.morph), false);
        actor.invisible = this.invisible;
        actor.enableBurning = this.enableBurning;

        if (this.health > 20)
        {
            actor.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(this.health);
        }

        actor.setHealth(this.health);
        actor.renderLast = this.renderLast;
        actor.notifyPlayers();
    }

    /**
     * Apply replay on a player 
     */
    public void apply(EntityPlayer player)
    {
        MorphAPI.morph(player, mchorse.metamorph.api.MorphUtils.copy(this.morph), true);
        player.setHealth(this.health);
        player.getFoodStats().setFoodLevel(this.foodLevel);
        player.experienceTotal = 0;
        player.experience = 0;
        player.experienceLevel = 0;
        this.setExperienceWithoutSound(player, this.totalExperience);
    }

    private void setExperienceWithoutSound(EntityPlayer player, int amount)
    {
        /* copied from EntityPlayer.addExperience(int) and modified */
        player.addScore(amount);
        int i = Integer.MAX_VALUE - player.experienceTotal;

        if (amount > i)
        {
            amount = i;
        }

        player.experience += (float)amount / (float)player.xpBarCap();

        for (player.experienceTotal += amount; player.experience >= 1.0F; player.experience /= (float)player.xpBarCap())
        {
            player.experience = (player.experience - 1.0F) * (float)player.xpBarCap();

            player.experienceLevel += 1;
        }
    }

    /* to / from NBT */

    public void toNBT(NBTTagCompound tag)
    {
        tag.setString("Id", this.id);
        tag.setString("Name", this.name);
        tag.setString("Target", this.target);

        if (this.morph != null)
        {
            tag.setTag("Morph", this.morph.toNBT());
        }

        tag.setBoolean("Invincible", this.invincible);
        tag.setBoolean("Invisible", this.invisible);
        tag.setBoolean("EnableBurning", this.enableBurning);
        tag.setBoolean("Enabled", this.enabled);
        tag.setBoolean("Fake", this.fake);
        if (!this.teleportBack) tag.setBoolean("TP", this.teleportBack);
        if (this.health != 20) tag.setFloat("Health", this.health);
        if (this.foodLevel != 20) tag.setInteger("FoodLevel", this.foodLevel);
        if (this.totalExperience != 0) tag.setInteger("TotalExperience", this.totalExperience);
        if (this.renderLast) tag.setBoolean("RenderLast", this.renderLast);
        if (this.playBackXPFood) tag.setBoolean("PlaybackXPFoodLevel", this.playBackXPFood);
    }

    public void fromNBT(NBTTagCompound tag)
    {
        this.id = tag.getString("Id");
        this.name = tag.getString("Name");
        this.target = tag.getString("Target");
        this.morph = MorphManager.INSTANCE.morphFromNBT(tag.getCompoundTag("Morph"));
        this.invincible = tag.getBoolean("Invincible");
        this.invisible = tag.getBoolean("Invisible");
        this.enableBurning = tag.getBoolean("EnableBurning");
        this.fake = tag.getBoolean("Fake");
        this.foodLevel = tag.hasKey("FoodLevel") ? tag.getInteger("FoodLevel") : this.foodLevel;
        this.totalExperience = tag.hasKey("TotalExperience") ? tag.getInteger("TotalExperience") : this.totalExperience;

        if (tag.hasKey("Enabled")) this.enabled = tag.getBoolean("Enabled");
        if (tag.hasKey("TP")) this.teleportBack = tag.getBoolean("TP");
        if (tag.hasKey("Health")) this.health = tag.getFloat("Health");
        if (tag.hasKey("RenderLast")) this.renderLast = tag.getBoolean("RenderLast");
        if (tag.hasKey("PlaybackXPFoodLevel")) this.playBackXPFood = tag.getBoolean("PlaybackXPFoodLevel");
    }

    /* to / from ByteBuf */

    public void toBuf(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.id);
        ByteBufUtils.writeUTF8String(buf, this.name);
        ByteBufUtils.writeUTF8String(buf, this.target);
        MorphUtils.morphToBuf(buf, this.morph);

        buf.writeBoolean(this.invincible);
        buf.writeBoolean(this.invisible);
        buf.writeBoolean(this.enableBurning);
        buf.writeBoolean(this.enabled);
        buf.writeBoolean(this.fake);
        buf.writeBoolean(this.teleportBack);
        buf.writeBoolean(this.renderLast);
        buf.writeFloat(this.health);
        buf.writeInt(this.foodLevel);
        buf.writeInt(this.totalExperience);
        buf.writeBoolean(this.playBackXPFood);
    }

    public void fromBuf(ByteBuf buf)
    {
        this.id = ByteBufUtils.readUTF8String(buf);
        this.name = ByteBufUtils.readUTF8String(buf);
        this.target = ByteBufUtils.readUTF8String(buf);
        this.morph = MorphUtils.morphFromBuf(buf);

        this.invincible = buf.readBoolean();
        this.invisible = buf.readBoolean();
        this.enableBurning = buf.readBoolean();
        this.enabled = buf.readBoolean();
        this.fake = buf.readBoolean();
        this.teleportBack = buf.readBoolean();
        this.renderLast = buf.readBoolean();
        this.health = buf.readFloat();
        this.foodLevel = buf.readInt();
        this.totalExperience = buf.readInt();
        this.playBackXPFood = buf.readBoolean();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Replay)
        {
            Replay replay = (Replay) obj;

            return Objects.equal(replay.id, this.id)
                && Objects.equal(replay.name, this.name)
                && Objects.equal(replay.target, this.target)
                && replay.invincible == this.invincible
                && replay.invisible == this.invisible
                && replay.enableBurning == this.enableBurning
                && replay.renderLast == this.renderLast
                && Objects.equal(replay.morph, this.morph);
        }

        return super.equals(obj);
    }

    public Replay copy()
    {
        Replay replay = new Replay();

        replay.id = this.id;
        replay.name = this.name;
        replay.target = this.target;
        replay.morph = mchorse.metamorph.api.MorphUtils.copy(this.morph);

        replay.invincible = this.invincible;
        replay.invisible = this.invisible;
        replay.enableBurning = this.enableBurning;
        replay.enabled = this.enabled;
        replay.fake = this.fake;
        replay.teleportBack = this.teleportBack;
        replay.renderLast = this.renderLast;
        replay.health = this.health;
        replay.foodLevel = this.foodLevel;
        replay.totalExperience = this.totalExperience;
        replay.playBackXPFood = this.playBackXPFood;

        return replay;
    }
}