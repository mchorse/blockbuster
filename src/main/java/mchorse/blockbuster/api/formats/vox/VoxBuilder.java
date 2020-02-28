package mchorse.blockbuster.api.formats.vox;

import mchorse.blockbuster.api.formats.Mesh;
import mchorse.blockbuster.api.formats.vox.data.Vox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

@SideOnly(Side.CLIENT)
public class VoxBuilder
{
    public Matrix3f transform;
    public Vector3f vector = new Vector3f();

    private Vector3f n1;
    private Vector3f n2;
    private Vector3f n3;
    private Vector3f n4;
    private Vector3f n5;
    private Vector3f n6;

    public VoxBuilder(Matrix3f transform)
    {
        this.transform = transform;

        this.n1 = this.processNormal(new Vector3f(1, 0, 0));
        this.n2 = this.processNormal(new Vector3f(-1, 0, 0));
        this.n3 = this.processNormal(new Vector3f(0, 0, -1));
        this.n4 = this.processNormal(new Vector3f(0, 0, 1));
        this.n5 = this.processNormal(new Vector3f(0, 1, 0));
        this.n6 = this.processNormal(new Vector3f(0, -1, 0));
    }

    private Vector3f processNormal(Vector3f normal)
    {
        /* Transform the normal */
        normal.set(normal.x, normal.z, normal.y);
        this.transform.transform(normal);
        normal.set(normal.x, normal.z, normal.y);
        normal.normalize();

        return normal;
    }

    public Mesh build(Vox vox)
    {
        /* Worst case scenario */
        Mesh mesh = new Mesh(vox.blocks * 12);

        mesh.triangles = 0;

        for (int x = 0; x < vox.x; x++)
        {
            for (int y = 0; y < vox.y; y++)
            {
                for (int z = 0; z < vox.z; z++)
                {
                    int voxel = vox.voxels[vox.toIndex(x, y, z)];

                    if (voxel != 0)
                    {
                        this.buildVertex(mesh, x, y, z, voxel, vox);
                    }
                }
            }
        }

        return mesh;
    }

    private void buildVertex(Mesh mesh, int x, int y, int z, int voxel, Vox vox)
    {
        boolean top = vox.has(x, y + 1, z);
        boolean bottom = vox.has(x, y - 1, z);
        boolean left = vox.has(x + 1, y, z);
        boolean right = vox.has(x - 1, y, z);
        boolean front = vox.has(x, y, z + 1);
        boolean back = vox.has(x, y, z - 1);

        if (!top)
        {
            Vector3f normal = this.n6;

            this.add(mesh, vox, x, y + 1, z, voxel, normal);
            this.add(mesh, vox, x + 1, y + 1, z, voxel, normal);
            this.add(mesh, vox, x, y + 1, z + 1, voxel, normal);
            this.add(mesh, vox, x, y + 1, z + 1, voxel, normal);
            this.add(mesh, vox, x + 1, y + 1, z, voxel, normal);
            this.add(mesh, vox, x + 1, y + 1, z + 1, voxel, normal);
        }

        if (!bottom)
        {
            Vector3f normal = this.n5;

            this.add(mesh, vox, x, y, z, voxel, normal);
            this.add(mesh, vox, x, y, z + 1, voxel, normal);
            this.add(mesh, vox, x + 1, y, z, voxel, normal);
            this.add(mesh, vox, x, y, z + 1, voxel, normal);
            this.add(mesh, vox, x + 1, y, z + 1, voxel, normal);
            this.add(mesh, vox, x + 1, y, z, voxel, normal);
        }

        if (!left)
        {
            Vector3f normal = this.n2;

            this.add(mesh, vox, x + 1, y, z, voxel, normal);
            this.add(mesh, vox, x + 1, y, z + 1, voxel, normal);
            this.add(mesh, vox, x + 1, y + 1, z, voxel, normal);
            this.add(mesh, vox, x + 1, y + 1, z, voxel, normal);
            this.add(mesh, vox, x + 1, y, z + 1, voxel, normal);
            this.add(mesh, vox, x + 1, y + 1, z + 1, voxel, normal);
        }

        if (!right)
        {
            Vector3f normal = this.n1;

            this.add(mesh, vox, x, y, z, voxel, normal);
            this.add(mesh, vox, x, y + 1, z, voxel, normal);
            this.add(mesh, vox, x, y, z + 1, voxel, normal);
            this.add(mesh, vox, x, y + 1, z, voxel, normal);
            this.add(mesh, vox, x, y + 1, z + 1, voxel, normal);
            this.add(mesh, vox, x, y, z + 1, voxel, normal);
        }

        if (!front)
        {
            Vector3f normal = this.n3;

            this.add(mesh, vox, x, y, z + 1, voxel, normal);
            this.add(mesh, vox, x, y  + 1, z + 1, voxel, normal);
            this.add(mesh, vox, x + 1, y, z + 1, voxel, normal);
            this.add(mesh, vox, x, y + 1, z + 1, voxel, normal);
            this.add(mesh, vox, x + 1, y + 1, z + 1, voxel, normal);
            this.add(mesh, vox, x + 1, y, z + 1, voxel, normal);
        }

        if (!back)
        {
            Vector3f normal = this.n4;

            this.add(mesh, vox, x, y, z, voxel, normal);
            this.add(mesh, vox, x + 1, y, z, voxel, normal);
            this.add(mesh, vox, x, y + 1, z, voxel, normal);
            this.add(mesh, vox, x, y + 1, z, voxel, normal);
            this.add(mesh, vox, x + 1, y, z, voxel, normal);
            this.add(mesh, vox, x + 1, y + 1, z, voxel, normal);
        }
    }

    private void add(Mesh mesh, Vox vox, int x, int y, int z, int voxel, Vector3f normal)
    {
        int tris = mesh.triangles;
        float u = (voxel + 0.5F) / 256F;
        float v = 0.5F;

        Vector3f vertex = this.process(x, y, z, vox);
        mesh.posData[tris * 3] = vertex.x;
        mesh.posData[tris * 3 + 1] = vertex.y;
        mesh.posData[tris * 3 + 2] = vertex.z;

        mesh.normData[tris * 3] = normal.x;
        mesh.normData[tris * 3 + 1] = normal.y;
        mesh.normData[tris * 3 + 2] = normal.z;

        mesh.texData[tris * 2] = u;
        mesh.texData[tris * 2 + 1] = v;

        mesh.triangles += 1;
    }

    private Vector3f process(int x, int y, int z, Vox vox)
    {
        int w = (int) (vox.x / 2F);
        int h = (int) (vox.y / 2F);
        int d = (int) (vox.z / 2F);

        this.vector.set(x - w, z - d, y - h);
        this.transform.transform(this.vector);
        this.vector.set(this.vector.x, this.vector.z, this.vector.y);

        return this.vector;
    }
}