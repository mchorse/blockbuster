package mchorse.blockbuster.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.MoreObjects;

import mchorse.blockbuster.api.formats.obj.ShapeKey;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

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

    /**
     * Shapes configurations
     */
    public final List<ShapeKey> shapes = new ArrayList<ShapeKey>();

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

            return ModelTransform.equalFloatArray(this.size, pose.size) && this.limbs.equals(pose.limbs) && this.shapes.equals(pose.shapes);
        }

        return super.equals(obj);
    }

    /**
     * Clone a model pose
     */
    public ModelPose copy()
    {
        ModelPose b = new ModelPose();

        b.size = new float[] {this.size[0], this.size[1], this.size[2]};

        for (Map.Entry<String, ModelTransform> entry : this.limbs.entrySet())
        {
            b.limbs.put(entry.getKey(), entry.getValue().clone());
        }

        for (ShapeKey key : this.shapes)
        {
            b.shapes.add(key.copy());
        }

        return b;
    }

    public void fromNBT(NBTTagCompound tag)
    {
        if (tag.hasKey("Size", Constants.NBT.TAG_LIST))
        {
            NBTTagList list = tag.getTagList("Size", 5);

            if (list.tagCount() >= 3)
            {
                NBTUtils.readFloatList(list, this.size);
            }
        }

        if (tag.hasKey("Poses", Constants.NBT.TAG_COMPOUND))
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

        if (tag.hasKey("Shapes"))
        {
            NBTTagList shapes = tag.getTagList("Shapes", Constants.NBT.TAG_COMPOUND);

            this.shapes.clear();

            for (int i = 0; i < shapes.tagCount(); i++)
            {
                NBTTagCompound key = shapes.getCompoundTagAt(i);

                if (key.hasKey("Name") && key.hasKey("Value"))
                {
                    ShapeKey shapeKey = new ShapeKey();

                    shapeKey.fromNBT(key);
                    this.shapes.add(shapeKey);
                }
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

        if (!this.shapes.isEmpty())
        {
            NBTTagList shapes = new NBTTagList();

            for (ShapeKey shape : this.shapes)
            {
                if (shape.value != 0)
                {
                    shapes.appendTag(shape.toNBT());
                }
            }

            tag.setTag("Shapes", shapes);
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