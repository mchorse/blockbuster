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

    public Map<String, Limb> limbs = new HashMap<String, Limb>();
    public Map<String, Pose> poses = new HashMap<String, Pose>();

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
        Gson gson = new GsonBuilder().registerTypeAdapter(Model.class, new ModelAdapter()).create();
        Model data = gson.fromJson(json, Model.class);

        for (String key : REQUIRED_POSES)
        {
            if (!data.poses.containsKey(key))
            {
                throw new Exception(I18n.format("blockbuster.parsing.lacks_pose", data.name, key));
            }
        }

        data.fillInMissing();

        return data;
    }

    /**
     * Add a limb into a model
     */
    public Model.Limb addLimb(String name)
    {
        Model.Limb limb = new Model.Limb();

        limb.name = name;
        this.limbs.put(name, limb);

        for (Model.Pose pose : this.poses.values())
        {
            pose.limbs.put(name, new Model.Transform());
        }

        return limb;
    }

    /**
     * Remove limb from a model
     *
     * If given any limb in the model is child of this limb, then they're
     * also getting removed.
     */
    public void removeLimb(Model.Limb limb)
    {
        this.limbs.remove(limb.name);

        List<Model.Limb> limbsToRemove = new ArrayList<Model.Limb>();

        for (Model.Limb child : this.limbs.values())
        {
            if (child.parent.equals(limb.name))
            {
                limbsToRemove.add(child);
            }
        }

        for (Model.Pose pose : this.poses.values())
        {
            pose.limbs.remove(limb.name);
        }

        for (Model.Limb limbToRemove : limbsToRemove)
        {
            this.removeLimb(limbToRemove);
        }
    }

    /**
     * Fill in missing transforms and assign name to every limb
     */
    public void fillInMissing()
    {
        for (Map.Entry<String, Limb> entry : this.limbs.entrySet())
        {
            String key = entry.getKey();

            for (Pose pose : this.poses.values())
            {
                if (!pose.limbs.containsKey(key))
                {
                    pose.limbs.put(key, new Transform());
                }
            }

            entry.getValue().name = key;
        }
    }

    /**
     * Get pose, or return default pose (which is the "standing" pose)
     */
    public Pose getPose(String key)
    {
        Pose pose = this.poses.get(key);

        return pose == null ? this.poses.get("standing") : pose;
    }

    /**
     * Clone a model
     */
    public Model clone()
    {
        Model b = new Model();

        b.texture = new int[] {this.texture[0], this.texture[1]};
        b.scale = new float[] {this.scale[0], this.scale[1], this.scale[2]};
        b.scaleGui = this.scaleGui;

        b.name = this.name;
        b.scheme = this.scheme;
        b.model = this.model;

        b.defaultTexture = this.defaultTexture == null ? null : new ResourceLocation(this.defaultTexture.toString());
        b.providesObj = this.providesObj;
        b.providesMtl = this.providesMtl;

        for (Map.Entry<String, Model.Limb> entry : this.limbs.entrySet())
        {
            b.limbs.put(entry.getKey(), entry.getValue().clone());
        }

        for (Map.Entry<String, Model.Pose> entry : this.poses.entrySet())
        {
            b.poses.put(entry.getKey(), entry.getValue().clone());
        }

        return b;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).add("scheme", this.scheme).add("name", this.name).add("texture", Arrays.toString(this.texture)).add("limbs", this.limbs).add("poses", this.poses).toString();
    }

    /**
     * Limb class
     *
     * This class is responsible for holding data that describing the limb.
     * It contains meta data and data about visuals and game play.
     */
    public static class Limb
    {
        /* Meta data */

        /**
         * Currently not used. Provide the name for your models still, it's
         * going to be used later when I going to implement GUI for managing
         * models and skins between server and client.
         */
        public String name = "";
        public String parent = "";

        /* Visuals */
        public int[] size = new int[] {4, 4, 4};
        public int[] texture = new int[] {0, 0};
        public float[] anchor = new float[] {0.5F, 0.5F, 0.5F};
        public float[] color = new float[] {1.0F, 1.0F, 1.0F};
        public float opacity = 1.0F;
        public boolean mirror;
        public boolean lighting = true;
        public boolean shading = true;

        /* Game play */
        public String holding = "";
        public boolean swiping;
        public boolean looking;
        public boolean swinging;
        public boolean idle;
        public boolean invert;

        /* OBJ */
        public float[] origin = new float[] {0F, 0F, 0F};

        /**
         * Clone a model limb
         */
        public Model.Limb clone()
        {
            Model.Limb b = new Model.Limb();

            b.name = this.name;
            b.parent = this.parent;

            b.size = new int[] {this.size[0], this.size[1], this.size[2]};
            b.texture = new int[] {this.texture[0], this.texture[1]};
            b.anchor = new float[] {this.anchor[0], this.anchor[1], this.anchor[2]};
            b.color = new float[] {this.color[0], this.color[1], this.color[2]};
            b.opacity = this.opacity;
            b.mirror = this.mirror;
            b.lighting = this.lighting;
            b.shading = this.shading;

            b.holding = this.holding;
            b.swiping = this.swiping;
            b.looking = this.looking;
            b.swinging = this.swinging;
            b.idle = this.idle;
            b.invert = this.invert;

            return b;
        }

        @Override
        public String toString()
        {
            return Objects.toStringHelper(this).add("parent", this.parent).add("size", Arrays.toString(this.size)).add("texture", Arrays.toString(this.texture)).add("anchor", Arrays.toString(this.anchor)).add("mirror", this.mirror).toString();
        }
    }

    /**
     * Pose class
     *
     * This class is responsible for holding transformation about every limb
     * available in the main model. Model parser should put default transforms
     * for limbs that don't have transformations.
     */
    public static class Pose
    {
        public float[] size = new float[] {1, 1, 1};
        public Map<String, Transform> limbs = new HashMap<String, Transform>();

        /**
         * Clone a model pose
         */
        public Model.Pose clone()
        {
            Model.Pose b = new Model.Pose();

            b.size = new float[] {this.size[0], this.size[1], this.size[2]};

            for (Map.Entry<String, Model.Transform> entry : this.limbs.entrySet())
            {
                b.limbs.put(entry.getKey(), entry.getValue().clone());
            }

            return b;
        }

        @Override
        public String toString()
        {
            return Objects.toStringHelper(this).add("size", this.size).add("limbs", this.limbs).toString();
        }
    }

    /**
     * Transform class
     *
     * This class simply holds basic transformation data for every limb.
     */
    public static class Transform
    {
        public float[] translate = new float[] {0, 0, 0};
        public float[] scale = new float[] {1, 1, 1};
        public float[] rotate = new float[] {0, 0, 0};

        /**
         * Clone a model transform
         */
        public Model.Transform clone()
        {
            Model.Transform b = new Model.Transform();

            b.translate = new float[] {this.translate[0], this.translate[1], this.translate[2]};
            b.rotate = new float[] {this.rotate[0], this.rotate[1], this.rotate[2]};
            b.scale = new float[] {this.scale[0], this.scale[1], this.scale[2]};

            return b;
        }

        @Override
        public String toString()
        {
            return Objects.toStringHelper(this).add("translate", this.translate).add("scale", this.scale).add("rotate", this.rotate).toString();
        }
    }
}