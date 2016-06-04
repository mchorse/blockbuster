package noname.blockbuster.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import noname.blockbuster.entity.ActorEntity;
import noname.blockbuster.recording.Mocap;

public class DirectorTileEntity extends TileEntity
{
    public List<String> actors = new ArrayList<String>();

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        NBTTagList list = compound.getTagList("Actors", 8);

        this.actors.clear();

        for (int i = 0; i < list.tagCount(); i++)
        {
            this.actors.add(list.getStringTagAt(i));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);

        NBTTagList list = new NBTTagList();

        if (!this.actors.isEmpty())
        {
            for (int i = 0; i < this.actors.size(); i++)
            {
                list.appendTag(new NBTTagString(this.actors.get(i)));
            }
        }

        compound.setTag("Actors", list);
    }

    public boolean addActor(String id)
    {
        if (!this.actors.contains(id))
        {
            this.actors.add(id);
            this.markDirty();

            return true;
        }

        return false;
    }

    /**
     * Start recording (actually a playback :D)
     */
    public void startRecording()
    {
        if (this.worldObj.isRemote)
        {
            return;
        }

        for (String id : this.actors)
        {
            ActorEntity actor = (ActorEntity) Mocap.entityByUUID(this.worldObj, UUID.fromString(id));

            if (actor == null)
            {
                continue;
            }

            actor.startPlaying();
        }
    }
}
