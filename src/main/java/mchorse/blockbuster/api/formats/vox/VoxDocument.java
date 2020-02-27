package mchorse.blockbuster.api.formats.vox;

import mchorse.blockbuster.api.formats.vox.data.Vox;
import mchorse.blockbuster.api.formats.vox.data.VoxBaseNode;
import mchorse.blockbuster.api.formats.vox.data.VoxGroup;
import mchorse.blockbuster.api.formats.vox.data.VoxShape;
import mchorse.blockbuster.api.formats.vox.data.VoxTransform;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class VoxDocument
{
	/**
	 * RGBA palette
	 */
	public int[] palette = VoxReader.DEFAULT_PALETTE;

	/**
	 * List of all chunks
	 */
	public List<Vox> chunks = new ArrayList<Vox>();

	/**
	 * Nodes
	 */
	public List<VoxBaseNode> nodes = new ArrayList<VoxBaseNode>();

	/**
	 * Generate a list of nodes for easier limb generation
	 */
	public List<LimbNode> generate()
	{
		List<LimbNode> nodes = new ArrayList<LimbNode>();
		Stack<Matrix3f> matStack = new Stack<Matrix3f>();
		Stack<Vector3f> vecStack = new Stack<Vector3f>();

		this.generateNodes((VoxTransform) this.nodes.get(0), nodes, matStack, vecStack);

		return nodes;
	}

	private void generateNodes(VoxTransform transform, List<LimbNode> nodes, Stack<Matrix3f> matStack, Stack<Vector3f> vecStack)
	{
		VoxBaseNode child = this.nodes.get(transform.childId);

		Matrix3f parentMat;
		Vector3f parentVec;
		Matrix4f trans = transform.transforms.get(0);

		if (matStack.isEmpty())
		{
			parentMat = new Matrix3f();
			parentVec = new Vector3f(trans.m03, trans.m13, trans.m23);

			parentMat.m00 = trans.m00;
			parentMat.m01 = trans.m01;
			parentMat.m02 = trans.m02;
			parentMat.m10 = trans.m10;
			parentMat.m11 = trans.m11;
			parentMat.m12 = trans.m12;
			parentMat.m20 = trans.m20;
			parentMat.m21 = trans.m21;
			parentMat.m22 = trans.m22;
		}
		else
		{
			parentMat = new Matrix3f(matStack.peek());
			parentVec = new Vector3f(vecStack.peek());

			Matrix3f mat = new Matrix3f();

			mat.m00 = trans.m00;
			mat.m01 = trans.m01;
			mat.m02 = trans.m02;
			mat.m10 = trans.m10;
			mat.m11 = trans.m11;
			mat.m12 = trans.m12;
			mat.m20 = trans.m20;
			mat.m21 = trans.m21;
			mat.m22 = trans.m22;

			parentMat.mul(mat);
			parentVec.add(new Vector3f(trans.m03, trans.m13, trans.m23));
		}

		matStack.push(parentMat);
		vecStack.push(parentVec);

		if (child instanceof VoxGroup)
		{
			matStack.push(new Matrix3f(matStack.peek()));
			vecStack.push(new Vector3f(vecStack.peek()));

			VoxGroup group = (VoxGroup) child;

			for (int id : group.ids)
			{
				this.generateNodes((VoxTransform) this.nodes.get(id), nodes, matStack, vecStack);
			}

			vecStack.pop();
			matStack.pop();
		}
		else if (child instanceof VoxShape)
		{
			Vox chunk = this.chunks.get(((VoxShape) child).modelAttrs.get(0).id);

			nodes.add(new LimbNode(chunk, matStack.pop(), vecStack.pop()));
		}
	}

	public static class LimbNode
	{
		public Vox chunk;
		public Matrix3f rotation;
		public Vector3f translation;

		public LimbNode(Vox chunk, Matrix3f rotation, Vector3f translation)
		{
			this.chunk = chunk;
			this.rotation = rotation;
			this.translation = translation;
		}
	}
}