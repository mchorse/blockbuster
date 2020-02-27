package mchorse.blockbuster.api.formats.vox;

import mchorse.blockbuster.api.formats.Mesh;
import mchorse.blockbuster.api.formats.vox.data.Vox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class VoxBuilder
{
    public List<Vertex> vertices = new ArrayList<Vertex>();
    public Matrix3f transform;
    public Vector3f vector = new Vector3f();

    public VoxBuilder(Matrix3f transform)
    {
        this.transform = transform;
    }

    public Mesh build(Vox vox)
    {
        this.vertices.clear();

        for (int x = 0; x < vox.x; x++)
        {
            for (int y = 0; y < vox.y; y++)
            {
                for (int z = 0; z < vox.z; z++)
                {
                    int voxel = vox.voxels[vox.toIndex(x, y, z)];

                    if (voxel != 0)
                    {
                        this.buildVertex(x, y, z, voxel, vox);
                    }
                }
            }
        }

        Mesh mesh = new Mesh(this.vertices.size() / 4 * 2);

        for (int i = 0, c = this.vertices.size() / 6; i < c; i++)
        {
            Vertex first = this.vertices.get(i * 6);
            Vector3f normal = null;

            if (first.normal == 0) normal = new Vector3f(-1, 0, 0);
            else if (first.normal == 1) normal = new Vector3f(1, 0, 0);
            else if (first.normal == 2) normal = new Vector3f(0, 0, 1);
            else if (first.normal == 3) normal = new Vector3f(0, 0, -1);
            else if (first.normal == 4) normal = new Vector3f(0, 1, 0);
            else if (first.normal == 5) normal = new Vector3f(0, -1, 0);

            /* Transform the normal */
            normal.set(normal.x, normal.z, normal.y);
            this.transform.transform(normal);
            normal.set(normal.x, normal.z, normal.y);

            float u = (first.color + 0.5F) / 256F;
            float v = 0.5F;

            Vector3f v1 = this.process(this.vertices.get(i * 6), vox);
            mesh.posData[i * 18] = v1.x;
            mesh.posData[i * 18 + 1] = v1.y;
            mesh.posData[i * 18 + 2] = v1.z;

            Vector3f v2 = this.process(this.vertices.get(i * 6 + 1), vox);
            mesh.posData[i * 18 + 3] = v2.x;
            mesh.posData[i * 18 + 4] = v2.y;
            mesh.posData[i * 18 + 5] = v2.z;

            Vector3f v3 = this.process(this.vertices.get(i * 6 + 2), vox);
            mesh.posData[i * 18 + 6] = v3.x;
            mesh.posData[i * 18 + 7] = v3.y;
            mesh.posData[i * 18 + 8] = v3.z;

            Vector3f v4 = this.process(this.vertices.get(i * 6 + 3), vox);
            mesh.posData[i * 18 + 9] = v4.x;
            mesh.posData[i * 18 + 10] = v4.y;
            mesh.posData[i * 18 + 11] = v4.z;

            Vector3f v5 = this.process(this.vertices.get(i * 6 + 4), vox);
            mesh.posData[i * 18 + 12] = v5.x;
            mesh.posData[i * 18 + 13] = v5.y;
            mesh.posData[i * 18 + 14] = v5.z;

            Vector3f v6 = this.process(this.vertices.get(i * 6 + 5), vox);
            mesh.posData[i * 18 + 15] = v6.x;
            mesh.posData[i * 18 + 16] = v6.y;
            mesh.posData[i * 18 + 17] = v6.z;

            for (int j = 0; j < 6; j ++)
            {
                mesh.normData[i * 18 + j * 3] = normal.x;
                mesh.normData[i * 18 + j * 3 + 1] = normal.y;
                mesh.normData[i * 18 + j * 3 + 2] = normal.z;
            }

            mesh.texData[i * 12] = mesh.texData[i * 12 + 2] = mesh.texData[i * 12 + 4] = mesh.texData[i * 12 + 6] = mesh.texData[i * 12 + 8] = mesh.texData[i * 12 + 10] = u;
            mesh.texData[i * 12 + 1] = mesh.texData[i * 12 + 3] = mesh.texData[i * 12 + 5] = mesh.texData[i * 12 + 7] = mesh.texData[i * 12 + 9] = mesh.texData[i * 12 + 11] = v;
        }

        return mesh;
    }

    private void buildVertex(int x, int y, int z, int voxel, Vox vox)
    {
        boolean top = vox.has(x, y + 1, z);
        boolean bottom = vox.has(x, y - 1, z);
        boolean left = vox.has(x + 1, y, z);
        boolean right = vox.has(x - 1, y, z);
        boolean front = vox.has(x, y, z + 1);
        boolean back = vox.has(x, y, z - 1);

        if (!top)
        {
            this.vertices.add(new Vertex(x, y + 1, z, voxel, 5));
            this.vertices.add(new Vertex(x + 1, y + 1, z, voxel, 5));
            this.vertices.add(new Vertex(x, y + 1, z + 1, voxel, 5));
            this.vertices.add(new Vertex(x, y + 1, z + 1, voxel, 5));
            this.vertices.add(new Vertex(x + 1, y + 1, z, voxel, 5));
            this.vertices.add(new Vertex(x + 1, y + 1, z + 1, voxel, 5));
        }

        if (!bottom)
        {
            this.vertices.add(new Vertex(x, y, z, voxel, 4));
            this.vertices.add(new Vertex(x, y, z + 1, voxel, 4));
            this.vertices.add(new Vertex(x + 1, y, z, voxel, 4));
            this.vertices.add(new Vertex(x, y, z + 1, voxel, 4));
            this.vertices.add(new Vertex(x + 1, y, z + 1, voxel, 4));
            this.vertices.add(new Vertex(x + 1, y, z, voxel, 4));
        }

        if (!left)
        {
            this.vertices.add(new Vertex(x + 1, y, z, voxel, 1));
            this.vertices.add(new Vertex(x + 1, y, z + 1, voxel, 1));
            this.vertices.add(new Vertex(x + 1, y + 1, z, voxel, 1));
            this.vertices.add(new Vertex(x + 1, y + 1, z, voxel, 1));
            this.vertices.add(new Vertex(x + 1, y, z + 1, voxel, 1));
            this.vertices.add(new Vertex(x + 1, y + 1, z + 1, voxel, 1));
        }

        if (!right)
        {
            this.vertices.add(new Vertex(x, y, z, voxel, 0));
            this.vertices.add(new Vertex(x, y + 1, z, voxel, 0));
            this.vertices.add(new Vertex(x, y, z + 1, voxel, 0));
            this.vertices.add(new Vertex(x, y + 1, z, voxel, 0));
            this.vertices.add(new Vertex(x, y + 1, z + 1, voxel, 0));
            this.vertices.add(new Vertex(x, y, z + 1, voxel, 0));
        }

        if (!front)
        {
            this.vertices.add(new Vertex(x, y, z + 1, voxel, 2));
            this.vertices.add(new Vertex(x, y  + 1, z + 1, voxel, 2));
            this.vertices.add(new Vertex(x + 1, y, z + 1, voxel, 2));
            this.vertices.add(new Vertex(x, y + 1, z + 1, voxel, 2));
            this.vertices.add(new Vertex(x + 1, y + 1, z + 1, voxel, 2));
            this.vertices.add(new Vertex(x + 1, y, z + 1, voxel, 2));
        }

        if (!back)
        {
            this.vertices.add(new Vertex(x, y, z, voxel, 3));
            this.vertices.add(new Vertex(x + 1, y, z, voxel, 3));
            this.vertices.add(new Vertex(x, y + 1, z, voxel, 3));
            this.vertices.add(new Vertex(x, y + 1, z, voxel, 3));
            this.vertices.add(new Vertex(x + 1, y, z, voxel, 3));
            this.vertices.add(new Vertex(x + 1, y + 1, z, voxel, 3));
        }
    }

    private Vector3f process(Vertex vertex, Vox vox)
    {
        int w = (int) (vox.x / 2F);
        int h = (int) (vox.y / 2F);
        int d = (int) (vox.z / 2F);

        this.vector.set(vertex.x - w, vertex.z - d, vertex.y - h);
        this.transform.transform(this.vector);
        this.vector.set(this.vector.x, this.vector.z, this.vector.y);

        return this.vector;
    }

    public static class Vertex
    {
        public int x;
        public int y;
        public int z;
        public int color;
        public int normal;

        public Vertex(int x, int y, int z, int color, int normal)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.color = color;
            this.normal = normal;
        }
    }
}