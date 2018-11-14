package mchorse.blockbuster.api;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.common.base.Objects;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mchorse.blockbuster.api.json.ModelAdapter;
import mchorse.blockbuster.api.json.ModelLimbAdapter;
import mchorse.blockbuster.utils.TextureLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

/**
 * Model class
 *
 * This class is a domain object that holds all information about the model like
 * its name, texture size, limbs, poses, interests and stuff.
 */
public class Model
{
    /**
     * Poses that are required by custom models
     */
    public static final List<String> REQUIRED_POSES = Arrays.<String>asList("standing", "sneaking", "sleeping", "flying");

    /**
     * Scheme version. Would be used in future versions for extracting and
     * exporting purposes.
     */
    public String scheme = "1.3";

    /**
     * Not really sure what to do with this one.
     */
    public String name = "";

    /**
     * Default texture for this model
     */
    public ResourceLocation defaultTexture;

    /**
     * Texture size. First element is width, second is height.
     */
    public int[] texture = new int[] {64, 32};

    /**
     * Scale of the model 
     */
    public float[] scale = new float[] {1, 1, 1};

    /**
     * Scale to be displayed in GUI 
     */
    public float scaleGui = 1;

    /**
     * Class for the custom model 
     */
    public String model = "";

    /**
     * Does this model provides OBJ model
     */
    public boolean providesObj = false;

    /**
     * Does this model provides MTL file
     */
    public boolean providesMtl = false;

    /**
     * Skins folder 
     */
    public String skins = "";

    public Map<String, ModelLimb> limbs = new HashMap<String, ModelLimb>();
    public Map<String, ModelPose> poses = new HashMap<String, ModelPose>();

    /**
     * Parse model from input stream
     */
    public static Model parse(InputStream stream) throws Exception
    {
        Scanner scanner = new Scanner(stream, "UTF-8");

        Model model = parse(scanner.useDelimiter("\\A").next());
        scanner.close();

        return model;
    }

    /**
     * This method parses an instance of Model class from provided JSON string.
     * This method also checks if model has all required poses for playing.
     */
    public static Model parse(String json) throws Exception
    {
        Gson gson = new GsonBuilder().registerTypeAdapter(Model.class, new ModelAdapter()).registerTypeAdapter(ModelLimb.class, new ModelLimbAdapter()).create();
        Model data = gson.fromJson(json, Model.class);

        for (String key : REQUIRED_POSES)
        {
            if (!data.poses.containsKey(key))
            {
                throw new Exception(I18n.format("blockbuster.parsing.lacks_pose", data.name, key));
            }
        }

        if (data.limbs.isEmpty())
        {
            throw new Exception(I18n.format("blockbuster.parsing.lacks_limbs", data.name));
        }

        data.fillInMissing();

        return data;
    }

    /**
     * Add a limb into a model
     */
    public ModelLimb addLimb(String name)
    {
        ModelLimb limb = new ModelLimb();

        limb.name = name;

        return this.addLimb(limb);
    }

    /**
     * Add a limb into a model
     */
    public ModelLimb addLimb(ModelLimb limb)
    {
        this.limbs.put(limb.name, limb);

        for (ModelPose pose : this.poses.values())
        {
            pose.limbs.put(limb.name, new ModelTransform());
        }

        return limb;
    }

    /**
     * Remove limb from a model
     *
     * If given any limb in the model is child of this limb, then they're
     * also getting removed.
     */
    public void removeLimb(ModelLimb limb)
    {
        this.limbs.remove(limb.name);

        List<ModelLimb> limbsToRemove = new ArrayList<ModelLimb>();

        for (ModelLimb child : this.limbs.values())
        {
            if (child.parent.equals(limb.name))
            {
                limbsToRemove.add(child);
            }
        }

        for (ModelPose pose : this.poses.values())
        {
            pose.limbs.remove(limb.name);
        }

        for (ModelLimb limbToRemove : limbsToRemove)
        {
            this.removeLimb(limbToRemove);
        }
    }

    /**
     * Rename given limb (this limb should already exist in this model)
     * @return 
     */
    public boolean renameLimb(ModelLimb limb, String newName)
    {
        if (this.limbs.containsKey(newName) || !this.limbs.containsValue(limb))
        {
            return false;
        }

        /* Rename limb name in poses */
        for (ModelPose pose : this.poses.values())
        {
            ModelTransform transform = pose.limbs.remove(limb.name);

            pose.limbs.put(newName, transform);
        }

        /* Rename all children limbs */
        for (ModelLimb child : this.limbs.values())
        {
            if (child.parent.equals(limb.name))
            {
                child.parent = newName;
            }
        }

        /* And finally remap the limb name to the new name */
        this.limbs.remove(limb.name);
        this.limbs.put(newName, limb);
        limb.name = newName;

        return true;
    }

    /**
     * Returns amount of limbs given limb hosts
     */
    public int getLimbCount(ModelLimb parent)
    {
        int count = 1;

        for (ModelLimb child : this.limbs.values())
        {
            if (child.parent.equals(parent.name))
            {
                count += this.getLimbCount(child);
            }
        }

        return count;
    }

    /**
     * Fill in missing transforms and assign name to every limb
     */
    public void fillInMissing()
    {
        for (Map.Entry<String, ModelLimb> entry : this.limbs.entrySet())
        {
            String key = entry.getKey();

            for (ModelPose pose : this.poses.values())
            {
                if (!pose.limbs.containsKey(key))
                {
                    pose.limbs.put(key, new ModelTransform());
                }
            }

            entry.getValue().name = key;
        }
    }

    /**
     * Get pose, or return default pose (which is the "standing" pose)
     */
    public ModelPose getPose(String key)
    {
        ModelPose pose = this.poses.get(key);

        return pose == null ? this.poses.get("standing") : pose;
    }

    /**
     * Clone a model
     */
    @Override
    public Model clone()
    {
        Model b = new Model();

        b.texture = new int[] {this.texture[0], this.texture[1]};
        b.scale = new float[] {this.scale[0], this.scale[1], this.scale[2]};
        b.scaleGui = this.scaleGui;

        b.name = this.name;
        b.scheme = this.scheme;
        b.model = this.model;

        b.defaultTexture = this.defaultTexture == null ? null : new TextureLocation(this.defaultTexture.toString());
        b.providesObj = this.providesObj;
        b.providesMtl = this.providesMtl;
        b.skins = this.skins;

        for (Map.Entry<String, ModelLimb> entry : this.limbs.entrySet())
        {
            b.limbs.put(entry.getKey(), entry.getValue().clone());
        }

        for (Map.Entry<String, ModelPose> entry : this.poses.entrySet())
        {
            b.poses.put(entry.getKey(), entry.getValue().clone());
        }

        return b;
    }

    /**
     * Copy model from model
     */
    public void copy(Model from)
    {
        this.texture = from.texture;
        this.scale = from.scale;
        this.scaleGui = from.scaleGui;

        this.name = from.name;
        this.scheme = from.scheme;
        this.model = from.model;

        this.defaultTexture = from.defaultTexture;
        this.providesObj = from.providesObj;
        this.providesMtl = from.providesMtl;
        this.skins = from.skins;

        this.poses = from.poses;
        this.limbs = from.limbs;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).add("scheme", this.scheme).add("name", this.name).add("texture", Arrays.toString(this.texture)).add("limbs", this.limbs).add("poses", this.poses).toString();
    }
}