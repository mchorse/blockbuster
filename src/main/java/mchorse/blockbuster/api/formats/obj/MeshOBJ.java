package mchorse.blockbuster.api.formats.obj;

import mchorse.blockbuster.api.formats.Mesh;

public class MeshOBJ extends Mesh
{
	public OBJMaterial material;

	public MeshOBJ(int faces)
	{
		super(faces);
	}
}