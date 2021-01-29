package mchorse.blockbuster.api;

import java.util.Arrays;

import com.google.common.base.MoreObjects;

import mchorse.blockbuster.common.OrientedBB;
import net.minecraft.inventory.EntityEquipmentSlot;

/**
 * Limb class
 *
 * This class is responsible for holding data that describing the limb.
 * It contains meta data and data about visuals and game play.
 */
public class ModelLimb
{	
	/*OrientedBoundingBox*/
	public OrientedBB obb = new OrientedBB();
    /* Meta data */
    public String name = "";
    public String parent = "";

    /* Visuals */
    public int[] size = new int[] {4, 4, 4};
    public float sizeOffset = 0;
    public int[] texture = new int[] {0, 0};
    public float[] anchor = new float[] {0.5F, 0.5F, 0.5F};
    public float[] color = new float[] {1.0F, 1.0F, 1.0F};
    public float opacity = 1.0F;
    public boolean mirror;
    public boolean lighting = true;
    public boolean shading = true;
    public boolean smooth = false;
    public boolean is3D = false;

    /* Game play */
    public Holding holding = Holding.NONE;
    public ArmorSlot slot = ArmorSlot.NONE;
    public boolean hold = true;
    public boolean swiping;
    public boolean lookX;
    public boolean lookY;
    public boolean swinging;
    public boolean idle;
    public boolean invert;
    public boolean wheel;
    public boolean wing;
    public boolean roll;

    /* OBJ */
    public float[] origin = new float[] {0F, 0F, 0F};

    public ModelLimb()
    {}

    public ModelLimb(String name)
    {
        this.name = name;
    }

    /**
     * Clone a model limb
     */
    @Override
    public ModelLimb clone()
    {
        ModelLimb b = new ModelLimb();

        b.name = this.name;
        b.parent = this.parent;

        b.size = new int[] {this.size[0], this.size[1], this.size[2]};
        b.sizeOffset = this.sizeOffset;
        b.texture = new int[] {this.texture[0], this.texture[1]};
        b.anchor = new float[] {this.anchor[0], this.anchor[1], this.anchor[2]};
        b.color = new float[] {this.color[0], this.color[1], this.color[2]};
        b.opacity = this.opacity;
        b.mirror = this.mirror;
        b.lighting = this.lighting;
        b.shading = this.shading;
        b.smooth = this.smooth;
        b.is3D = this.is3D;

        b.holding = this.holding;
        b.slot = this.slot;
        b.hold = this.hold;
        b.swiping = this.swiping;
        b.lookX = this.lookX;
        b.lookY = this.lookY;
        b.swinging = this.swinging;
        b.idle = this.idle;
        b.invert = this.invert;
        b.wheel = this.wheel;
        b.wing = this.wing;
        b.roll = this.roll;

        b.origin = new float[] {this.origin[0], this.origin[1], this.origin[2]};

        return b;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this).add("parent", this.parent).add("size", Arrays.toString(this.size)).add("texture", Arrays.toString(this.texture)).add("anchor", Arrays.toString(this.anchor)).add("mirror", this.mirror).toString();
    }

    public static enum Holding
    {
        NONE, RIGHT, LEFT;
    }

    /**
     * Armor slots
     */
    public static enum ArmorSlot
    {
        NONE(null, "none"), HEAD(EntityEquipmentSlot.HEAD, "head"), CHEST(EntityEquipmentSlot.CHEST, "chest"), LEFT_SHOULDER(EntityEquipmentSlot.CHEST, "left_shoulder"), RIGHT_SHOULDER(EntityEquipmentSlot.CHEST, "right_shoulder"), LEGGINGS(EntityEquipmentSlot.LEGS, "leggings"), LEFT_LEG(EntityEquipmentSlot.LEGS, "left_leg"), RIGHT_LEG(EntityEquipmentSlot.LEGS, "right_leg"), LEFT_FOOT(EntityEquipmentSlot.FEET, "left_foot"), RIGHT_FOOT(EntityEquipmentSlot.FEET, "right_foot");

        public final EntityEquipmentSlot slot;
        public final String name;

        public static ArmorSlot fromName(String str)
        {
            for (ArmorSlot slot : values())
            {
                if (slot.name.equals(str)) return slot;
            }

            return NONE;
        }

        private ArmorSlot(EntityEquipmentSlot slot, String name)
        {
            this.slot = slot;
            this.name = name;
        }
    }
}