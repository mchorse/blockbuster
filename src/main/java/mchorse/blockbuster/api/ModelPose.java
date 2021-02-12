package mchorse.blockbuster.api;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.MoreObjects;

import mchorse.mclib.utils.NBTUtils;
import net.minecraft.nbt.NBTTagCompound;
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

    public void copy(ModelPose pose)
    {
        for (int i = 0, c = Math.min(pose.size.length, this.size.length); i < c; i++)
        {
            this.size[i] = pose.size[i];
        }

        for (Map.Entry<String, ModelTransform> entry : this.limbs.entrySet())
        {
            ModelTransform otherTransform = pose.limbs.get(entry.getKey());

            if (otherTransform != null)
            {
                entry.getValue().copy(otherTransform);
            }
        }
    }

    /**
     * Fill in missing transforms 
     */
    public void fillInMissing(ModelPose pose)
    {
        for (Map.Entry<String, ModelTransform> entry : pose.limbs.entrySet())
        {
            String key = entry.getKey();

            if (!this.limbs.containsKey(key))
            {
                this.limbs.put(key, entry.getValue().clone());
            }
        }
    }

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
    @Override
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
                NBTUtils.readFloatList(list, this.size);
            }
        }

        if (tag.hasKey("Poses", 10))
        {
            this.limbs.clear();

            NBTTagCompound poses = tag.getCompoundTag("Poses");

            for (String key : poses.getKeySet())
            {
                ModelTransform trans = new ModelTransform();

                trans.fromNBT(poses.getCompoundTag(key));
                this.limbs.put(key, trans);
            }
        }
    }

    public NBTTagCompound toNBT(NBTTagCompound tag)
    {
        NBTTagCompound poses = new NBTTagCompound();

        tag.setTag("Size", NBTUtils.writeFloatList(new NBTTagList(), this.size));
        tag.setTag("Poses", poses);

        for (Map.Entry<String, ModelTransform> entry : this.limbs.entrySet())
        {
            poses.setTag(entry.getKey(), entry.getValue().toNBT());
        }

        return tag;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this).add("size", this.size).add("limbs", this.limbs).toString();
    }

    public void setSize(float w, float h, float d)
    {
        this.size[0] = w;
        this.size[1] = h;
        this.size[2] = d;
    }
}