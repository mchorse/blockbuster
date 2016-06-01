package noname.blockbuster.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;

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

        System.out.println(this.actors);
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

        System.out.println(this.actors);
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

    public void startRecording()
    {
        System.out.println("Start recording, bitches!");
    }
}
