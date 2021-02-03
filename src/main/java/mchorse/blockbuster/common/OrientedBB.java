package mchorse.blockbuster.common;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4d;
import javax.vecmath.Vector4f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import mchorse.blockbuster.client.RenderingHandler;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.MathUtils;
import mchorse.mclib.utils.MatrixUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLEvent;
/**
 * This is an implentation of an oriented bounding box.
 * 
 * The implementation is specifically designed for the Minecraft Blockbuster mod, created by McHorse.
 * Special attributes or functions that are used for the Blockbuster mod are for example:
 * 		- an initial rotation, which can be changed in the GUI of the model editor.
 * 		- limb and anchor offset: limb offset defines the offset from a selected object part.
 * 		  The anchor offset is used for the initial rotation, to rotate around a certain point.
 * <p>
 * How it works (currently):
 * Inside the render() method of ModelCustomRenderer the modelview of the limb is computated 
 * and calculates the relative offset to the main morph center. This data is passed to the offset attribute in OBB.
 * In the applyRotations() method of RenderCustomModel the center of the entity is calculated which gets passed
 * to the center variable here in the OBB. Then the method buildCorners() is being called, which uses offset
 * and center to calculate the real centerpoint of this OBB.
 * 
 * buildCorners() is the most important method, as it computes all the corners according to
 * the offsets, rotations and other transformations. 
 * This should be called at least once somewhere before the collision is tested.
 * <p>
 * Some of the learning sources that were used for the general concept of an OBB implementation:
 * @see http://www.cie.bgu.tum.de/publications/bachelorthesis/2014_Engeser.pdf
 * @see https://www.sciencedirect.com/topics/computer-science/oriented-bounding-box
 * <p>
 * @author Christian F. (known as Chryfi)
 * @see https://github.com/Chryfi
 * @see https://www.youtube.com/Chryfi
 */
public class OrientedBB
{	
	private final double random1 = Math.random();
	private final double random2 = Math.random();
	private final double random3 = Math.random();
	private final double random4 = Math.random();
	
	/*local basis vectors*/
	private Vector3d w = new Vector3d(1,0,0); //x
	private Vector3d u = new Vector3d(0,1,0); //y
	private Vector3d v = new Vector3d(0,0,1); //z
	
	public static Matrix4f modelView = new Matrix4f();
	
	public Matrix4d rotation = new Matrix4d();
	
	/*initial rotation*/
	public Matrix4d rotation0 = new Matrix4d();
	
	/*global center point (not the anchor)*/
	public Vector3d center = new Vector3d();
	
	/*global anchor point - mostly for rendering anchorpoints*/
	private Vector3d anchorPoint = new Vector3d();
	
	/*half-width, half-height, half-depth*/
	public double hw;
	public double hu;
	public double hv;
	
	/*corners - starting from maxXYZ (1,1,1) going clockwise
	 *same thing for bottom - starting at maxXminYmaxZ */
	public Corner[] corners = new Corner[8];
	
	/*offset from limb (calculated through modelview)*/
	public Vector3d limbOffset = new Vector3d();
	
	/*anchor of the obb - for initialrotation*/
	public Vector3d anchorOffset = new Vector3d();
	
	/*offset from main entity*/
	public Vector3d offset = new Vector3d();
	
	public OrientedBB(@Nullable Vector3d center, @Nullable Matrix4d rotation, float width, float height, float depth) 
	{
		if(center==null)
		{
			center = new Vector3d();
		}
		if(rotation==null)
		{
			rotation = new Matrix4d();
			rotation.setIdentity();
		}
		setup(rotation, width, height, depth);
		this.center.set(center);
	}
	
	public OrientedBB()
	{
		Matrix4d rotation = new Matrix4d();
		rotation.setIdentity();
		setup(rotation, 0, 0, 0);
		this.anchorOffset.set(0, (Math.random()*2-1)/2, 0);
		this.rotation0.set(anglesToMatrix(0, (Math.random()*2-1)*1, (Math.random()*2-1)*1));
		buildCorners();
	}
	
	public void setup(Matrix4d rotation0, float width, float height, float depth) 
	{
		this.center = new Vector3d();
		this.hw = Math.abs(width)/2;
		this.hu = Math.abs(height)/2;
		this.hv = Math.abs(depth)/2;
		RenderingHandler.obbsToRender.add(this);
		this.rotation.setIdentity();
		this.rotation0.set(rotation0);
	}
	
	public void render(RenderWorldLastEvent event)
    { 
		buildCorners();
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
        
        color.set((float)random1+0.25F,(float)random2+0.25F,(float)random3+0.25F,1F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();

        builder.setTranslation(-playerX, -playerY, -playerZ);
        builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        
        Corner[] startCorners = {this.corners[0], this.corners[2], this.corners[5], this.corners[7]};
        for(Corner start : startCorners) 
        {
        	for(Corner end : start.connections)
        	{
        		builder.pos(start.position.x, start.position.y, start.position.z).color(color.r, color.g, color.b, color.a).endVertex();
    	        builder.pos(end.position.x, end.position.y, end.position.z).color(color.r, color.g, color.b, color.a).endVertex();
        	}
        }
        drawPlainAxis(builder, color, anchorPoint, 6, true);
		
		drawPlainAxis(builder, color, center, 11, false);
		
        builder.setTranslation(0, 0, 0);
        tessellator.draw();
        
        
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.glLineWidth(2F);

        if (shader != 0)
        {
            OpenGlHelper.glUseProgram(shader);
        }
    }
	
	/**
	 * this draws a plainaxis - three lines in total
	 * @param center0 the center of the plain Axis
	 * @param length the half-length of the axis measured in Minecraft pixels
	 * @param rotate - if true the plain axis will be rotated by rotation0 and rotaion
	 */
	public void drawPlainAxis(BufferBuilder builder, Color color, Vector3d center0, double length, boolean rotate)
	{
		length /= 32; //1 <=> 1 pixel => divide by 16 and by 2 as it's half length
		Vector3d axisX1 = new Vector3d(length, 0, 0);
		Vector3d axisX2 = new Vector3d(0, length, 0);
		Vector3d axisX3 = new Vector3d(0, 0, length);
		if(rotate)
		{
			this.rotation0.transform(axisX1);
			this.rotation.transform(axisX1);
			this.rotation0.transform(axisX2);
			this.rotation.transform(axisX2);
			this.rotation0.transform(axisX3);
			this.rotation.transform(axisX3);
		}
		builder.pos(center0.x+axisX1.x, center0.y+axisX1.y, center0.z+axisX1.z).color(color.r, color.g, color.b, color.a).endVertex();
		builder.pos(center0.x-axisX1.x,center0.y-axisX1.y, center0.z-axisX1.z).color(color.r, color.g, color.b, color.a).endVertex();
		
		builder.pos(center0.x+axisX2.x, center0.y+axisX2.y, center0.z+axisX2.z).color(color.r, color.g, color.b, color.a).endVertex();
		builder.pos(center0.x-axisX2.x,center0.y-axisX2.y, center0.z-axisX2.z).color(color.r, color.g, color.b, color.a).endVertex();
		
		builder.pos(center0.x+axisX3.x, center0.y+axisX3.y, center0.z+axisX3.z).color(color.r, color.g, color.b, color.a).endVertex();
		builder.pos(center0.x-axisX3.x,center0.y-axisX3.y, center0.z-axisX3.z).color(color.r, color.g, color.b, color.a).endVertex();
	}
	
	/**
	 * This method calculates all the corners of the OBB according to rotation, anchor and other transformation.
	 * The corners are saved inside the attribute corners.
	 */
	public void buildCorners() 
	{
		if(!RenderingHandler.obbsToRender.contains(this)) RenderingHandler.obbsToRender.add(this);
		
	    Vector4d width0 = new Vector4d(this.w);
	    width0.scale(this.hw);
	    width0.w = 1;
	    
	    Vector4d height0 = new Vector4d(this.u);
	    height0.scale(this.hu);
	    height0.w = 1;
	    
	    Vector4d depth0 = new Vector4d(this.v);
	    depth0.scale(this.hv);
	    depth0.w = 1;

	    Vector4d limbOffset0 = new Vector4d(this.limbOffset);
	    limbOffset0.w = 1;
	    
	    Vector4d anchorOffset0 = new Vector4d(this.anchorOffset);
	    anchorOffset0.w = 1;
	    
	    Vector4d offset = new Vector4d(this.offset);
	    offset.w = 1;
	    
	    Matrix4d rotation1 = new Matrix4d(rotation0);
	    rotation1.mul(this.rotation);
	    
	    this.rotation.transform(limbOffset0);
	    rotation1.transform(anchorOffset0); //not entirely sure if that is correct - testing later in gui
	    rotation1.transform(width0);
	    rotation1.transform(height0);
	    rotation1.transform(depth0);
	    
	    Vector4d center0 = new Vector4d(this.center);
	    center0.add(limbOffset0);
	    center0.add(anchorOffset0);
	    center0.add(offset);
	    
	    Vector3d center = new Vector3d(center0.x, center0.y, center0.z);
	    Vector3d width = new Vector3d(width0.x, width0.y, width0.z);
	    Vector3d height = new Vector3d(height0.x, height0.y, height0.z);
	    Vector3d depth = new Vector3d(depth0.x, depth0.y, depth0.z);
	    this.anchorPoint.set(center);
	    
	    /*calculate the corners*/
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
        
        /*connect the corners*/
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
	 * This method converts the given angles into one single rotation 4x4 rotation matrix
	 * @param angleX rotation around X
	 * @param angleY rotation around Y (Minecraft height axis)
	 * @param angleZ rotation around Z
	 * @return Matrix4d the complete rotation
	 */
	public Matrix4d anglesToMatrix(double angleX, double angleY, double angleZ) 
	{
		angleX = Math.toRadians(angleX);
        angleY = Math.toRadians(angleY);
        angleZ = Math.toRadians(angleZ);
        double sinX = Math.sin(angleX);
        double cosX = Math.cos(angleX);
        double sinY = Math.sin(angleY);
        double cosY = Math.cos(angleY);
        double sinZ = Math.sin(angleZ);
        double cosZ = Math.cos(angleZ);
        Matrix4d rotateX = new Matrix4d( 1,   0 ,    0 , 0,
				   						 0, cosX, -sinX, 0,
				   						 0, sinX,  cosX, 0,
				   						 0,   0 ,    0 , 1);
		Matrix4d rotateY = new Matrix4d( cosY, 0, sinY, 0,
									       0 , 1,   0 , 0,
									    -sinY, 0, cosY, 0,
									       0 , 0,   0 , 1);
		Matrix4d rotateZ = new Matrix4d( cosZ, -sinZ, 0, 0,
				   						 sinZ,  cosZ, 0, 0,
				   						   0 ,    0 , 1, 0,
				   						   0 ,    0 , 0, 1);
		Matrix4d rotation = new Matrix4d(rotateX);
		rotation.mul(rotateY);
		rotation.mul(rotateZ);
		return rotation;
	}
	
	@Override
	public String toString() 
	{
		return "OBB - center: "+this.center;
	}
	
	private class Corner 
	{
		/*global position (could it also be local???)*/
		public Vector3d position;
		
		/*List of corners that should be connected with this corner
		 * corners inside this list also have a connection to this corner*/
		private List<Corner> connections;
		
		public Corner(Vector3d pos)
		{
			this.position = new Vector3d(pos);
			this.connections = new ArrayList<Corner>();
		}
		
		/**
		 * method connects corner with this corner. It adds both elements to both connections lists
		 * @return boolean true if connection was established. False means that no connection was made as both lists contain already the corners
		 */
		public boolean connect(Corner corner) 
		{
			if(!corner.connections.contains(this) && !this.connections.contains(corner))
			{
				corner.connections.add(this);
				this.connections.add(corner);
				return true;
			}
			else if(!corner.connections.contains(this))
			{
				corner.connections.add(this);
				return true;
			}
			else if(!this.connections.contains(corner))
			{
				this.connections.add(corner);
				return true;
			}
			return false;
		}
		
		public void disconnect(Corner corner)
		{
			corner.connections.remove(this);
			this.connections.remove(corner);
		}
	}
}
