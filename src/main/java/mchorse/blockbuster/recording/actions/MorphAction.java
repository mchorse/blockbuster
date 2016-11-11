package mchorse.blockbuster.recording.actions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.utils.RLUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

/**
 * Morph action
 *
 * This action is responsible for changing the model and skin of actor during
 * the playback. This action is submitted when player executes /morph command.
 */
public class MorphAction extends Action
{
    public String model;
    public ResourceLocation skin;

    public MorphAction()
    {}

    public MorphAction(String model, ResourceLocation skin)
    {
        this.model = model;
        this.skin = skin;
    }

    @Override
    public byte getType()
    {
        return Action.MORPH;
    }

    @Override
    public void apply(EntityActor actor)
    {
        actor.model = this.model;
        actor.skin = this.skin;
        actor.notifyPlayers();
    }

    @Override
    public void fromBytes(DataInput in) throws IOException
    {
        this.model = in.readUTF();
        this.skin = RLUtils.fromString(in.readUTF(), this.model);
    }

    @Override
    public void toBytes(DataOutput out) throws IOException
    {
        out.writeUTF(this.model);
        out.writeUTF(this.skin == null ? "" : this.skin.toString());
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.model = tag.getString("Model");
        this.skin = RLUtils.fromString(tag.getString("Skin"), this.model);
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        tag.setString("Model", this.model);
        tag.setString("Skin", this.skin == null ? "" : this.skin.toString());
    }
}