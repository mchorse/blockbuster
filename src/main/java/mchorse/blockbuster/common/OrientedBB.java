package mchorse.blockbuster.common;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4d;

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

public class OrientedBB
{

	public double yawTMP = 0; //just for testing and fun
	public double[] rotationPoint = {0,0,0}; //testing position
	
	public Matrix4d rotation = new Matrix4d();
	public static Matrix4f modelView = new Matrix4f();
	/* global center point (not the anchor)*/
	public Vector3d center;
	
	/*local basis vectors*/
	private Vector3d w = new Vector3d(1,0,0); //x
	private Vector3d u = new Vector3d(0,1,0); //y
	private Vector3d v = new Vector3d(0,0,1); //z
	
	/*half-width, half-height, half-depth*/
	public float hw;
	public float hu;
	public float hv;
	
	private final double random1 = Math.random();
	private final double random2 = Math.random();
	private final double random3 = Math.random();
	private final double random4 = Math.random();
	
	/* corners - starting from maxXYZ (1,1,1) going clockwise
	 * same thing for bottom - starting at maxXminYmaxZ */
	public Corner[] corners = new Corner[8];
	
	public Vector3d anchorOffset = new Vector3d();
	
	/*offset from main entity*/
	public Vector3d offset = new Vector3d();
	
	public OrientedBB(Vector3d center, float width, float height, float depth) 
	{
		setup(width, height, depth);
		this.center.set(center);
		buildCorners();
	}
	
	public OrientedBB()
	{
		setup(0, 0, 0);
	}
	
	public OrientedBB(float width, float height, float depth) 
	{
		setup(width, height, depth);
		buildCorners();
	}
	
	public void setup(float width, float height, float depth) 
	{
		this.center = new Vector3d();
		this.hw = Math.abs(width)/2;
		this.hu = Math.abs(height)/2;
		this.hv = Math.abs(depth)/2;
		RenderingHandler.obbsToRender.add(this);
		this.rotation.setIdentity();
	}
	
	public void update(EntityLivingBase target, float partialTicks) 
	{
		/*Matrix4f parent = new Matrix4f(MatrixUtils.matrix);
		parent.invert();
		
		Vector4f zero = SnowstormMorph.calculateGlobal(parent, target, 0, 0, 0, partialTicks);

		System.out.println(zero+"\n");
		this.rotation.setIdentity();

		Vector3f ax = new Vector3f(parent.m00, parent.m01, parent.m02);
		Vector3f ay = new Vector3f(parent.m10, parent.m11, parent.m12);
		Vector3f az = new Vector3f(parent.m20, parent.m21, parent.m22);

		ax.normalize();
		ay.normalize();
		az.normalize();

		this.rotation.setRow(0, ax);
		this.rotation.setRow(1, ay);
		this.rotation.setRow(2, az);
		
		//this.center.set(zero.x, zero.y, zero.z);
		rotate(this.rotation);
		buildCorners();*/
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
        
        builder.setTranslation(0, 0, 0);
        tessellator.draw();
        
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.glLineWidth(1F);

        if (shader != 0)
        {
            OpenGlHelper.glUseProgram(shader);
        }
    }
	
	public void buildCorners() 
	{
	    Vector4d width0 = new Vector4d(this.w);
	    width0.scale(this.hw);
	    width0.w = 1;
	    
	    Vector4d height0 = new Vector4d(this.u);
	    height0.scale(this.hu);
	    height0.w = 1;
	    
	    Vector4d depth0 = new Vector4d(this.v);
	    depth0.scale(this.hv);
	    depth0.w = 1;
	    
	    Vector4d anchorOffset = new Vector4d(this.anchorOffset);
	    anchorOffset.w = 1;
	    Vector4d offset = new Vector4d(this.offset);
	    offset.w = 1;
	    
	    //this.rotation.transform(anchorOffset);
	    //this.rotation.transform(width0);
	    //this.rotation.transform(height0);
	    //this.rotation.transform(depth0);
	    
	    Vector4d center0 = new Vector4d(this.center);
	    center0.add(anchorOffset);
	    center0.add(offset);
	    
	    Vector3d center = new Vector3d(center0.x, center0.y, center0.z);
	    Vector3d width = new Vector3d(width0.x, width0.y, width0.z);
	    Vector3d height = new Vector3d(height0.x, height0.y, height0.z);
	    Vector3d depth = new Vector3d(depth0.x, depth0.y, depth0.z);

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
	
	@Override
	public String toString() 
	{
		return "OBB - center: "+this.center;
	}
	
	private class Corner 
	{
		/*global position (maybe local later when I learn more matrix stuff*/
		public Vector3d position;
		
		/*List of corners that should be connected with this corner
		 * corners inside this list also have a connection to this corner*/
		private List<Corner> connections;
		
		public Corner(Vector3d pos, List<Corner> connections)
		{
			this.position = new Vector3d(pos);
			this.connections = new ArrayList<Corner>();
			this.connections.addAll(connections);
		}
		
		public Corner(Vector3d pos)
		{
			this.position = new Vector3d(pos);
			this.connections = new ArrayList<Corner>();
		}
		
		private void addConnection(Corner corner) 
		{
			this.connections.add(corner);
		}
		
		public void connect(Corner corner) 
		{
			corner.addConnection(this);
			addConnection(corner);
		}
	}
}
