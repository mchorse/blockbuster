package mchorse.blockbuster.api;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.common.base.Objects;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.client.resources.I18n;

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
    protected static final List<String> REQUIRED_POSES = Arrays.<String> asList("standing", "sneaking", "sleeping", "flying");

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
     * Texture size. First element is width, second is height.
     */
    public int[] texture = new int[] {64, 32};

    public Map<String, Limb> limbs = new HashMap<String, Limb>();
    public Map<String, Pose> poses = new HashMap<String, Pose>();

    /**
     * Parse model from input stream
     * @throws Exception
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
        Gson gson = new GsonBuilder().create();
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
        public boolean mirror;

        /* Game play */
        public String holding = "";
        public boolean swiping;
        public boolean looking;
        public boolean swinging;
        public boolean idle;
        public boolean invert;

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

        @Override
        public String toString()
        {
            return Objects.toStringHelper(this).add("translate", this.translate).add("scale", this.scale).add("rotate", this.rotate).toString();
        }
    }
}