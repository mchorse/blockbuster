package mchorse.blockbuster.common;

import javax.vecmath.Matrix4f;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import mchorse.blockbuster.client.RenderingHandler;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;

/**
 * This is an implentation of an oriented bounding box.
 * 
 * The implementation is specifically designed for the Minecraft Blockbuster
 * mod, created by McHorse. Special attributes or functions that are used for
 * the Blockbuster mod are for example: initial rotation (which can be
 * changed in the GUI of the model editor), limb and anchor offsets. 
 * Limb offset defines the offset from a selected object part. 
 * The anchor offset is used for the initial rotation, to rotate around a certain point.
 * <p>
 * How it works (currently): Inside the render() method of ModelCustomRenderer
 * the modelview of the limb is computated and calculates the relative offset to
 * the main morph center. This data is passed to the offset attribute in OBB. In
 * the applyRotations() method of RenderCustomModel the center of the entity is
 * calculated which gets passed to the center variable here in the OBB. Then the
 * method buildCorners() is being called, which uses offset and center to
 * calculate the real centerpoint of this OBB.
 * 
 * buildCorners() is the most important method, as it computes all the corners
 * according to the offsets, rotations and other transformations. This should be
 * called at least once somewhere before the collision is tested.
 * <p>
 * The learning sources that were used for the general concept 
 * of an OBB implementation:
 * 
 *      http://www.cie.bgu.tum.de/publications/bachelorthesis/2014_Engeser.pdf
 *      https://www.sciencedirect.com/topics/computer-science/oriented-bounding-box
 * <p>
 * @author Christian F. (known as Chryfi)
 * @see https://github.com/Chryfi
 * @see https://www.youtube.com/Chryfi
 * @see https://twitter.com/Chryfi
 */
public class OrientedBB
{
    /** local basis vector x */
    private Vector3d w = new Vector3d(1, 0, 0);
    /** local basis vector y */
    private Vector3d u = new Vector3d(0, 1, 0);
    /** local basis vector z */
    private Vector3d v = new Vector3d(0, 0, 1);

    /** global anchor point - mostly for rendering anchorpoints */
    private Vector3d anchorPoint = new Vector3d();
    
    public static Matrix4f modelView = new Matrix4f();

    /** scale factor determined by modelView and other scaling factors */
    public Matrix3d scale = new Matrix3d();
    
    public Matrix3d rotation = new Matrix3d();

    /** initial rotation defined at the beginning of model creation */
    public double[] rotation0 = { 0, 0, 0 };

    /** global center point (not the anchor) */
    public Vector3d center = new Vector3d();

    /** half-width */
    public double hw;
    /** half-height */
    public double hu;
    /** half-depth */
    public double hv;

    /** corners - starting from maxXYZ (1,1,1) going clockwise same thing for bottom - starting at maxXminYmaxZ */
    public Corner[] corners = new Corner[8];

    /** offset from limb (calculated through modelview) */
    public Vector3d limbOffset = new Vector3d();

    /** anchor of the obb - for initial rotation */
    public Vector3d anchorOffset = new Vector3d();

    /** offset from main entity */
    public Vector3d offset = new Vector3d();

    public OrientedBB(@Nullable Vector3d center, @Nullable double[] rotation0, float width, float height, float depth)
    {
        if (center == null)
        {
            center = new Vector3d();
        }
        
        if (rotation0 == null)
        {
            rotation0 = new double[3];
        }
        
        setup(rotation0, width, height, depth);
        this.center.set(center);
    }

    public OrientedBB()
    {
        double[] rotation0 = new double[3];

        rotation.setIdentity();
        setup(rotation0, 0, 0, 0);
        buildCorners();
    }

    public void setup(double[] rotation0, float width, float height, float depth)
    {
        this.center = new Vector3d();
        this.hw = Math.abs(width) / 2;
        this.hu = Math.abs(height) / 2;
        this.hv = Math.abs(depth) / 2;
        this.rotation0 = rotation0;
        
        RenderingHandler.obbsToRender.add(this);
        this.rotation.setIdentity();
        this.scale.setIdentity();
    }

    public void render(RenderWorldLastEvent event)
    {
        int shader = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

        if (shader != 0)
        {
            OpenGlHelper.glUseProgram(0);
        }

        Color color = ColorUtils.COLOR;
        Entity player = Minecraft.getMinecraft().getRenderViewEntity();

        double playerX = player.prevPosX + (player.posX - player.prevPosX) * event.getPartialTicks();
        double playerY = player.prevPosY + (player.posY - player.prevPosY) * event.getPartialTicks();
        double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * event.getPartialTicks();

        GlStateManager.glLineWidth(3F);
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        
        color.set(1F, 1F, 1F, 1F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();

        builder.setTranslation(-playerX, -playerY, -playerZ);
        builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        Corner[] startCorners = { this.corners[0], this.corners[2], this.corners[5], this.corners[7] };
        
        for (Corner start : startCorners)
        {
            for (Corner end : start.connections)
            {
                builder.pos(start.position.x, start.position.y, start.position.z).color(color.r, color.g, color.b, color.a).endVertex();
                builder.pos(end.position.x, end.position.y, end.position.z).color(color.r, color.g, color.b, color.a).endVertex();
            }
        }
        
        builder.setTranslation(0, 0, 0);
        tessellator.draw();
        
        renderAxes(new double[]{-playerX, -playerY, -playerZ}, color, this.anchorPoint, 4, true, false);
        
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.glLineWidth(1F);

        if (shader != 0)
        {
            OpenGlHelper.glUseProgram(shader);
        }
    }

    /**
     * This method renders 3 axes. If wanted, it can rotate according to the rotation matrices in the OBB.
     * 
     * @param translation for OpenGL translation (usually player position)
     * @param color the color of the axes
     * @param center0 the center of the plain Axis
     * @param length  the half-length of the axis measured in Minecraft pixels
     * @param rotate  if true the plain axis will be rotated by rotation0 and rotation
     * @param depth control GlStateManager depth
     */
    public void renderAxes(double[] translation, Color color, Vector3d center0, double length, boolean rotate, boolean depth)
    {
        GlStateManager.glLineWidth(2F);
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        
        if(!depth)
        {
            GlStateManager.disableDepth();
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();

        builder.setTranslation(translation[0], translation[1], translation[2]);
        builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        Matrix3d rotation0 = anglesToMatrix(this.rotation0[0], this.rotation0[1], this.rotation0[2]);
        length /= 32; // 1 <=> 1 pixel => divide by 16 and by 2 as it's half length
        Vector3d axisX1 = new Vector3d(length, 0, 0);
        Vector3d axisX2 = new Vector3d(0, length, 0);
        Vector3d axisX3 = new Vector3d(0, 0, length);
        
        if (rotate)
        {
            this.rotation.transform(axisX1);
            rotation0.transform(axisX1);
            
            this.rotation.transform(axisX2);
            rotation0.transform(axisX2);
            
            this.rotation.transform(axisX3);
            rotation0.transform(axisX3);
        }
        
        builder.pos(center0.x + axisX1.x, center0.y + axisX1.y, center0.z + axisX1.z).color(color.r, color.g, color.b, color.a).endVertex();
        builder.pos(center0.x - axisX1.x, center0.y - axisX1.y, center0.z - axisX1.z).color(color.r, color.g, color.b, color.a).endVertex();

        builder.pos(center0.x + axisX2.x, center0.y + axisX2.y, center0.z + axisX2.z).color(color.r, color.g, color.b, color.a).endVertex();
        builder.pos(center0.x - axisX2.x, center0.y - axisX2.y, center0.z - axisX2.z).color(color.r, color.g, color.b, color.a).endVertex();

        builder.pos(center0.x + axisX3.x, center0.y + axisX3.y, center0.z + axisX3.z).color(color.r, color.g, color.b, color.a).endVertex();
        builder.pos(center0.x - axisX3.x, center0.y - axisX3.y, center0.z - axisX3.z).color(color.r, color.g, color.b, color.a).endVertex();
        
        builder.setTranslation(0, 0, 0);
        tessellator.draw();
        
        if(!depth)
        {
            GlStateManager.enableDepth();
        }
    }

    /**
     * This method calculates all the corners of the OBB according to rotation,
     * anchor and other transformation. The corners are saved inside the attribute
     * corners.
     */
    public void buildCorners()
    {
        if (!RenderingHandler.obbsToRender.contains(this))
        {
            RenderingHandler.obbsToRender.add(this);
        }

        Vector3d width = new Vector3d(this.w);
        Vector3d height = new Vector3d(this.u);
        Vector3d depth = new Vector3d(this.v);
        Matrix3d rotation0 = anglesToMatrix(this.rotation0[0], this.rotation0[1], this.rotation0[2]);
        
        width.scale(this.hw);
        height.scale(this.hu);
        depth.scale(this.hv);
        
        Vector3d limbOffset0 = new Vector3d(this.limbOffset);

        Vector3d anchorOffset0 = new Vector3d(this.anchorOffset);

        Vector3d offset0 = new Vector3d(this.offset);

        Matrix3d rotscale = new Matrix3d(this.scale);
        
        rotscale.mul(this.rotation);
        rotscale.mul(rotation0);
        
        this.rotation.transform(limbOffset0);
        this.scale.transform(limbOffset0);
        
        rotscale.transform(anchorOffset0); // not entirely sure if that is correct - testing later in gui
        rotscale.transform(width);
        rotscale.transform(height);
        rotscale.transform(depth);

        Vector3d center = new Vector3d(this.center);
        
        center.add(offset0);
        this.anchorPoint.set(center);
        center.add(anchorOffset0);
        center.add(limbOffset0);
        
        
        /* calculate the corners */
        Vector3d pos = new Vector3d(center);
        pos.add(width);
        pos.add(height);
        pos.add(depth);
        
        Corner maxXYZ = new Corner(pos);
        this.corners[0] = maxXYZ;

        pos.set(center);
        pos.sub(width);
        pos.add(height);
        pos.add(depth);
        
        Corner minXmaxYZ = new Corner(pos);
        this.corners[1] = minXmaxYZ;

        pos.set(center);
        pos.sub(width);
        pos.add(height);
        pos.sub(depth);
        
        Corner minXmaxYminZ = new Corner(pos);
        this.corners[2] = minXmaxYminZ;

        pos.set(center);
        pos.add(width);
        pos.add(height);
        pos.sub(depth);
        
        Corner maxXYminZ = new Corner(pos);
        this.corners[3] = maxXYminZ;

        pos.set(center);
        pos.add(width);
        pos.sub(height);
        pos.add(depth);
        
        Corner maxXminYmaxZ = new Corner(pos);
        this.corners[4] = maxXminYmaxZ;

        pos.set(center);
        pos.sub(width);
        pos.sub(height);
        pos.add(depth);
        
        Corner minXYmaxZ = new Corner(pos);
        this.corners[5] = minXYmaxZ;

        pos.set(center);
        pos.sub(width);
        pos.sub(height);
        pos.sub(depth);
        
        Corner minXYZ = new Corner(pos);
        this.corners[6] = minXYZ;

        pos.set(center);
        pos.add(width);
        pos.sub(height);
        pos.sub(depth);
        
        Corner maxXminYZ = new Corner(pos);
        this.corners[7] = maxXminYZ;

        /* connect the corners */
        maxXYZ.connect(maxXYminZ);
        maxXYZ.connect(minXmaxYZ);
        maxXYZ.connect(maxXminYmaxZ);

        minXmaxYminZ.connect(maxXYminZ);
        minXmaxYminZ.connect(minXYZ);
        minXmaxYminZ.connect(minXmaxYZ);

        minXYmaxZ.connect(maxXminYmaxZ);
        minXYmaxZ.connect(minXYZ);
        minXYmaxZ.connect(minXmaxYZ);

        maxXminYZ.connect(maxXYminZ);
        maxXminYZ.connect(minXYZ);
        maxXminYZ.connect(maxXminYmaxZ);
    }

    /**
     * This method converts the given angles into one single 3x3 rotation matrix.
     * The rotation mode is XYZ
     * 
     * @param angleX rotation around X
     * @param angleY rotation around Y (Minecraft height axis)
     * @param angleZ rotation around Z
     * @return the complete rotation Matrix3d
     */
    public static Matrix3d anglesToMatrix(double angleX, double angleY, double angleZ)
    {
        double radX = Math.toRadians(angleX);
        double radY = Math.toRadians(angleY);
        double radZ = Math.toRadians(angleZ);
        Matrix3d rotation = new Matrix3d();
        Matrix3d rot = new Matrix3d();

        rotation.setIdentity();
        rot.rotX(radX);
        rotation.mul(rot);
        rot.rotY(radY);
        rotation.mul(rot);
        rot.rotZ(radZ);
        rotation.mul(rot);
        
        return rotation;
    }
    
    public OrientedBB clone()
    {
        OrientedBB d = new OrientedBB();
        d.hu = this.hu;
        d.hw = this.hw;
        d.hv = this.hv;
        d.anchorOffset.set(this.anchorOffset);
        d.offset.set(this.offset);
        d.center.set(this.center);
        d.limbOffset.set(this.limbOffset);
        d.rotation.set(this.rotation);
        d.rotation0 = this.rotation0;
        d.scale.set(this.scale);
        return d;
    }

    @Override
    public String toString()
    {
        return "OBB - center: " + this.center;
    }

    private class Corner
    {
        /** global position (could it be also local???) */
        public Vector3d position;

        /**
         * List of corners that should be connected with this corner. 
         * Corners inside this list should also have a connection to this corner
         */
        private List<Corner> connections;

        public Corner(Vector3d pos)
        {
            this.position = new Vector3d(pos);
            this.connections = new ArrayList<>();
        }

        /**
         * This method connects the given corner with this corner. 
         * It adds given corner to this connection list and this corner to given corner's connection list.
         * 
         * @param corner
         * @return true if connection was established. False means that no connection was made as both lists contain already the corners
         */
        public boolean connect(Corner corner)
        {
            if (!corner.connections.contains(this) && !this.connections.contains(corner))
            {
                corner.connections.add(this);
                this.connections.add(corner);
                
                return true;
            } 
            else if (!corner.connections.contains(this))
            {
                corner.connections.add(this);
                
                return true;
            } 
            else if (!this.connections.contains(corner))
            {
                this.connections.add(corner);
                
                return true;
            }
            
            return false;
        }

        /**
         * This method removes the given corner and this corner from both connection lists.
         * 
         * @param corner
         * @return false if both connection lists don't have the corners.
         */
        public boolean disconnect(Corner corner)
        {
            if (corner.connections.contains(this) && this.connections.contains(corner))
            {
                corner.connections.remove(this);
                this.connections.remove(corner);
                
                return true;
            } 
            else if (corner.connections.contains(this))
            {
                corner.connections.remove(this);
                
                return true;
            } 
            else if (this.connections.contains(corner))
            {
                this.connections.remove(corner);
                
                return true;
            }
            
            return false;
        }
    }
}
