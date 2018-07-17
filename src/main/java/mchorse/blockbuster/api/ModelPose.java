package mchorse.blockbuster.api;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.MoreObjects;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;

/**
 * Pose class
 *
 * This class is responsible for holding transformation about every limb
 * available in the main model. Model parser should put default transforms
 * for limbs that don't have transformations.
 */
public class ModelPose
{
    public float[] size = new float[] {1, 1, 1};
    public Map<String, ModelTransform> limbs = new HashMap<String, ModelTransform>();

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ModelPose)
        {
            ModelPose pose = (ModelPose) obj;

            return ModelTransform.equalFloatArray(this.size, pose.size) && this.limbs.equals(pose.limbs);
        }

        return super.equals(obj);
    }

    /**
     * Clone a model pose
     */
    public ModelPose clone()
    {
        ModelPose b = new ModelPose();

        b.size = new float[] {this.size[0], this.size[1], this.size[2]};

        for (Map.Entry<String, ModelTransform> entry : this.limbs.entrySet())
        {
            b.limbs.put(entry.getKey(), entry.getValue().clone());
        }

        return b;
    }

    public void fromNBT(NBTTagCompound tag)
    {
        if (tag.hasKey("Size", 9))
        {
            NBTTagList list = tag.getTagList("Size", 5);

            if (list.tagCount() >= 3)
            {
                this.readFloatList(list, this.size);
            }
        }

        if (tag.hasKey("Poses", 10))
        {
            this.limbs.clear();

            NBTTagCompound poses = tag.getCompoundTag("Poses");

            for (String key : poses.getKeySet())
            {
                NBTTagCompound pose = poses.getCompoundTag(key);
                ModelTransform trans = new ModelTransform();

                this.readFloatList(pose.getTagList("P", 5), trans.translate);
                this.readFloatList(pose.getTagList("S", 5), trans.scale);
                this.readFloatList(pose.getTagList("R", 5), trans.rotate);

                this.limbs.put(key, trans);
            }
        }
    }

    public NBTTagCompound toNBT(NBTTagCompound tag)
    {
        NBTTagCompound poses = new NBTTagCompound();

        tag.setTag("Size", this.writeFloatList(new NBTTagList(), this.size));
        tag.setTag("Poses", poses);

        for (Map.Entry<String, ModelTransform> entry : this.limbs.entrySet())
        {
            NBTTagCompound pose = new NBTTagCompound();
            ModelTransform trans = entry.getValue();

            pose.setTag("P", this.writeFloatList(new NBTTagList(), trans.translate));
            pose.setTag("S", this.writeFloatList(new NBTTagList(), trans.scale));
            pose.setTag("R", this.writeFloatList(new NBTTagList(), trans.rotate));

            poses.setTag(entry.getKey(), pose);
        }

        return tag;
    }

    public void readFloatList(NBTTagList list, float[] array)
    {
        int count = Math.min(array.length, list.tagCount());

        for (int i = 0; i < count; i++)
        {
            array[i] = list.getFloatAt(i);
        }
    }

    public NBTTagList writeFloatList(NBTTagList list, float[] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            list.appendTag(new NBTTagFloat(array[i]));
        }

        return list;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this).add("size", this.size).add("limbs", this.limbs).toString();
    }
}