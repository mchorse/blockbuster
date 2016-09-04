package noname.blockbuster.client.model.parsing;

import java.util.HashMap;
import java.util.Map;

/**
 * Model class
 *
 * This class is a domain object that holds inside all information about the
 * model like its name, texture size, limbs and poses.
 */
public class Model
{
    public String scheme;
    public String name;
    public int[] texture;

    public Map<String, Limb> limbs = new HashMap<String, Limb>();
    public Map<String, Object> poses = new HashMap<String, Object>();

    /**
     * Limb class
     *
     * This class is responsible for holding data that describing the limb.
     * It contains meta data and data about visuals and game play.
     */
    public static class Limb
    {
        /* Meta data */
        public String id;
        public String parent;

        /* Visuals */
        public int[] size;
        public int[] texture;
        public float[] anchor;
        public boolean mirror;

        /* Game play */
        public String holding;
        public boolean swiping;
        public boolean looking;
        public boolean swinging;
        public boolean idle;
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
        public float[] size;
        public Map<String, Transform> limbs = new HashMap<String, Transform>();
    }

    /**
     * Transform class
     *
     * This class simply holds basic transformation data for every limb.
     */
    public static class Transform
    {
        public float[] translate;
        public float[] scale;
        public float[] rotate;
    }
}