package mchorse.blockbuster.client.model.parsing;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Objects;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.actors.threadpool.Arrays;

/**
 * Model class
 *
 * This class is a domain object that holds inside all information about the
 * model like its name, texture size, limbs and poses.
 */
@SideOnly(Side.CLIENT)
public class Model
{
    public String scheme = "";
    public String name = "";
    public int[] texture = new int[] {64, 32};

    public Map<String, Limb> limbs = new HashMap<String, Limb>();
    public Map<String, Pose> poses = new HashMap<String, Pose>();

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