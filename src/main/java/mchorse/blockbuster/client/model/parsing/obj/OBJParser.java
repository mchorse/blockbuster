package mchorse.blockbuster.client.model.parsing.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mchorse.blockbuster.commands.SubCommandBase;
import net.minecraft.util.ResourceLocation;

/**
 * OBJ and MTL parser and loader
 */
public class OBJParser
{
    /* Input files */
    public File objFile;
    public File mtlFile;

    /* Collected data */
    public List<Vector3f> vertices = new ArrayList<Vector3f>();
    public List<Vector2f> textures = new ArrayList<Vector2f>();
    public List<Vector3f> normals = new ArrayList<Vector3f>();
    public List<OBJDataMesh> objects = new ArrayList<OBJDataMesh>();
    public Map<String, OBJMaterial> materials = new HashMap<String, OBJMaterial>();

    /**
     * Read all lines from a file (needs a text file) 
     */
    public static List<String> readAllLines(File file) throws Exception
    {
        List<String> list = new ArrayList<String>();

        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            String line;
            while ((line = br.readLine()) != null)
            {
                list.add(line);
            }

            br.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Construct OBJ parser with OBJ and MTL file references
     * 
     * TODO: rewrite to using {@link InputStream}
     */
    public OBJParser(File objFile, File mtlFile)
    {
        this.objFile = objFile;
        this.mtlFile = mtlFile;
    }

    /**
     * Setup material textures 
     */
    public void setupTextures(String key, File folder)
    {
        /* Create a texture location for materials */
        for (OBJMaterial material : this.materials.values())
        {
            if (material.useTexture)
            {
                material.texture = new ResourceLocation("blockbuster.actors", key + "/skins/" + material.name + "/default.png");

                /* Create folder for every material */
                new File(folder, "skins/" + material.name + "/").mkdirs();
            }
        }
    }

    /**
     * Read the data first 
     */
    public void read() throws Exception
    {
        this.vertices.clear();
        this.textures.clear();
        this.normals.clear();
        this.objects.clear();
        this.materials.clear();

        this.readMTL();
        this.readOBJ();
    }

    /**
     * Read materials from MTL file. This method isn't necessarily will 
     * read any materials because MTL file is optional
     */
    public void readMTL() throws Exception
    {
        if (this.mtlFile == null)
        {
            return;
        }

        List<String> lines = readAllLines(this.mtlFile);
        OBJMaterial material = null;

        for (String line : lines)
        {
            if (line.isEmpty())
            {
                continue;
            }

            String[] tokens = line.split("\\s+");
            String first = tokens[0];

            if (first.equals("newmtl"))
            {
                material = new OBJMaterial();
                material.name = tokens[1];

                this.materials.put(material.name, material);
            }
            /* Read diffuse color */
            else if (first.equals("Kd") && tokens.length == 4)
            {
                material.r = Float.parseFloat(tokens[1]);
                material.g = Float.parseFloat(tokens[2]);
                material.b = Float.parseFloat(tokens[3]);
            }
            /* Read texture */
            else if (first.equals("map_Kd"))
            {
                material.useTexture = true;
            }
            else if (first.equals("map_Kd_linear"))
            {
                material.linear = true;
            }
        }
    }

    /**
     * Read objects from OBJ file 
     */
    public void readOBJ() throws Exception
    {
        List<String> lines = readAllLines(this.objFile);

        OBJDataMesh mesh = null;
        OBJDataGroup group = null;
        boolean firstUse = false;

        for (String line : lines)
        {
            String[] tokens = line.split("\\s+");
            String first = tokens[0];

            /* Blender uses "o" for objects, while C4D uses "g" */
            if (first.equals("o") || first.equals("g"))
            {
                mesh = new OBJDataMesh();
                mesh.name = tokens[1];

                group = new OBJDataGroup();
                mesh.groups.add(group);
                firstUse = true;

                this.objects.add(mesh);
            }

            /* Vertices */
            if (first.equals("v"))
            {
                this.vertices.add(new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
            }
            /* Texture coordinates (UV) */
            else if (first.equals("vt"))
            {
                this.textures.add(new Vector2f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2])));
            }
            /* Who needs normals? */
            else if (first.equals("vn"))
            {
                this.normals.add(new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
            }
            /* Material group */
            else if (first.equals("usemtl"))
            {
                OBJMaterial material = this.materials.get(tokens[1]);

                group = firstUse ? group : new OBJDataGroup();
                group.material = material;

                if (!firstUse)
                {
                    mesh.groups.add(group);
                }

                firstUse = false;
            }
            /* Collect faces */
            else if (first.equals("f"))
            {
                String[] faces = SubCommandBase.dropFirstArgument(tokens);

                if (faces.length == 4)
                {
                    /* Support for quads, yay! */
                    group.faces.add(new OBJFace(new String[] {faces[0], faces[1], faces[2]}));
                    group.faces.add(new OBJFace(new String[] {faces[0], faces[2], faces[3]}));
                }
                else
                {
                    group.faces.add(new OBJFace(faces));
                }
            }
        }
    }

    /**
     * From collected information, form mesh data
     */
    public Map<String, MeshObject> compile()
    {
        Map<String, MeshObject> meshes = new HashMap<String, MeshObject>();

        for (OBJDataMesh obj : this.objects)
        {
            MeshObject meshObject = new MeshObject();

            for (OBJDataGroup group : obj.groups)
            {
                List<OBJFace> faces = group.faces;
                Mesh mesh = new Mesh(faces.size());

                int i = 0;

                for (OBJFace face : faces)
                {
                    for (OBJIndexGroup indexGroup : face.idxGroups)
                    {
                        processFaceVertex(i, indexGroup, mesh);

                        i++;
                    }
                }

                mesh.material = group.material;
                meshObject.meshes.add(mesh);
            }

            meshes.put(obj.name, meshObject);
        }

        return meshes;
    }

    /**
     * Place all the data to complementary arrays 
     */
    private void processFaceVertex(int i, OBJIndexGroup indices, Mesh mesh)
    {
        if (indices.idxPos >= 0)
        {
            Vector3f vertex = this.vertices.get(indices.idxPos);

            mesh.posData[i * 3] = vertex.x;
            mesh.posData[i * 3 + 1] = vertex.y;
            mesh.posData[i * 3 + 2] = vertex.z;
        }

        if (indices.idxTextCoord >= 0)
        {
            Vector2f coord = this.textures.get(indices.idxTextCoord);

            mesh.texData[i * 2] = coord.x;
            mesh.texData[i * 2 + 1] = 1 - coord.y;
        }

        if (indices.idxVecNormal >= 0)
        {
            Vector3f normal = this.normals.get(indices.idxVecNormal);

            mesh.normData[i * 3] = normal.x;
            mesh.normData[i * 3 + 1] = normal.y;
            mesh.normData[i * 3 + 2] = normal.z;
        }
    }

    /**
     * Mesh from OBJ file
     * 
     * It holds faces for every object found in OBJ file
     */
    protected static class OBJDataMesh
    {
        public String name;
        public List<OBJDataGroup> groups = new ArrayList<OBJDataGroup>();
    }

    public static class OBJDataGroup
    {
        public List<OBJFace> faces = new ArrayList<OBJFace>();
        public OBJMaterial material;
    }

    /**
     * Substitute class for a 2d vector which comes with joml library 
     */
    protected static class Vector2f
    {
        public float x;
        public float y;

        public Vector2f(float x, float y)
        {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Substitute class for a 3d vector which comes with joml library 
     */
    protected static class Vector3f
    {
        public float x;
        public float y;
        public float z;

        public Vector3f(float x, float y, float z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static class MeshObject
    {
        public List<Mesh> meshes = new ArrayList<Mesh>();
    }

    /**
     * Holds the mesh data 
     */
    public static class Mesh
    {
        public float[] posData;
        public float[] texData;
        public float[] normData;
        public OBJMaterial material;

        public Mesh(int faces)
        {
            this(new float[faces * 9], new float[faces * 6], new float[faces * 9]);
        }

        public Mesh(float[] posData, float[] texData, float[] normData)
        {
            this.posData = posData;
            this.texData = texData;
            this.normData = normData;
        }
    }
}