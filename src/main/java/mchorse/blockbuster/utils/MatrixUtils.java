package mchorse.blockbuster.utils;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix4f;
import java.nio.FloatBuffer;

public class MatrixUtils
{
	/**
	 * Model view matrix buffer
	 */
	public static final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);

	/**
	 * Float array for transferring data from FloatBuffer to the matrix
	 */
	public static final float[] floats = new float[16];

	/**
	 * Read OpenGL's model view matrix
	 */
	public static Matrix4f readModelView(Matrix4f matrix4f)
	{
		buffer.clear();
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buffer);
		buffer.get(floats);

		matrix4f.set(floats);
		matrix4f.transpose();

		return matrix4f;
	}

	/**
	 * Replace model view matrix with given matrix
	 */
	public static void loadModelView(Matrix4f matrix4f)
	{
		matrixToFloat(matrix4f);

		buffer.clear();
		buffer.put(floats);
		buffer.rewind();
		GL11.glLoadMatrix(buffer);
	}

	/**
	 * Private method to fill the float array with values from the matrix
	 */
	private static void matrixToFloat(Matrix4f matrix4f)
	{
		floats[0] = matrix4f.m00;
		floats[1] = matrix4f.m01;
		floats[2] = matrix4f.m02;
		floats[3] = matrix4f.m03;
		floats[4] = matrix4f.m10;
		floats[5] = matrix4f.m11;
		floats[6] = matrix4f.m12;
		floats[7] = matrix4f.m13;
		floats[8] = matrix4f.m20;
		floats[9] = matrix4f.m21;
		floats[10] = matrix4f.m22;
		floats[11] = matrix4f.m23;
		floats[12] = matrix4f.m30;
		floats[13] = matrix4f.m31;
		floats[14] = matrix4f.m32;
		floats[15] = matrix4f.m33;
	}
}