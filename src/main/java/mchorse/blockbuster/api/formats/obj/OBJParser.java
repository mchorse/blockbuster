package mchorse.blockbuster.api.formats.obj;

import mchorse.blockbuster.api.formats.IMeshes;
import mchorse.blockbuster.api.formats.Mesh;
import mchorse.mclib.commands.SubCommandBase;
import mchorse.mclib.utils.resources.RLUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OBJ and MTL parser and loader
 */
public class OBJParser
{
    /* Input files */
    public InputStream objFile;
    public InputStream mtlFile;

    /* Collected data */
    public List<Vector3f> vertices = new ArrayList<Vector3f>();
    public List<Vector2f> textures = new ArrayList<Vector2f>();
    public List<Vector3f> normals = new ArrayList<Vector3f>();
    public List<OBJDataMesh> objects = new ArrayList<OBJDataMesh>();
    public Map<String, OBJMaterial> materials = new HashMap<String, OBJMaterial>();

    public static String processMaterialName(String name)
    {
        /* Apparently material name can have slashes and backslashes, so
         * they must be replaced to avoid messing up texture paths...
         */
        return name.replaceAll("[/|\\\\]+", "-");
    }

    /**
     * Read all lines from a file (needs a text file)
     */
    public static List<String> readAllLines(InputStream stream) throws Exception
    {
        List<String> list = new ArrayList<String>();

        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

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
     */
    public OBJParser(InputStream objFile, InputStream mtlFile)
    {
        this.objFile = objFile;
        this.mtlFile = mtlFile;
    }

    public boolean equalData(OBJParser parser)
    {
        boolean result = this.vertices.size() == parser.vertices.size();

        result = result && this.textures.size() == parser.textures.size();
        result = result && this.normals.size() == parser.normals.size();
        result = result && this.objects.size() == parser.objects.size();

        here:
        for (OBJDataMesh mesh : this.objects)
        {
            for (OBJDataMesh dataMesh : parser.objects)
            {
                if (mesh.name.equals(dataMesh.name))
                {
                    result = result && mesh.groups.size() == dataMesh.groups.size();

                    continue here;
                }
            }

            return false;
        }

        return result;
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
     * Setup material textures
     */
    public void setupTextures(String key, File folder)
    {
        /* Create a texture location for materials */
        for (OBJMaterial material : this.materials.values())
        {
            if (material.useTexture && material.texture == null)
            {
                material.texture = RLUtils.create("b.a", key + "/skins/" + material.name + "/default.png");

                /* Create folder for every material */
                new File(folder, "skins/" + material.name + "/").mkdirs();
            }
        }
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
                material = new OBJMaterial(processMaterialName(tokens[1]));

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
            else if (first.equals("map_Kd_path"))
            {
                String texture = String.join(" ", SubCommandBase.dropFirstArgument(tokens));

                material.texture = RLUtils.create(texture);
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
        OBJMaterial material = null;

        for (String line : lines)
        {
            String[] tokens = line.split("\\s+");
            String first = tokens[0];

            /* Blender uses "o" for objects, while C4D uses "g" */
            if ((first.equals("o") || first.equals("g")) && tokens.length >= 2)
            {
                String name = tokens[1];

                mesh = null;

                for (OBJDataMesh data : this.objects)
                {
                    if (data.name.equals(name))
                    {
                        mesh = data;

                        break;
                    }
                }

                if (mesh == null)
                {
                    mesh = new OBJDataMesh();
                    mesh.name = name;
                    this.objects.add(mesh);
                }

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
                material = this.materials.get(processMaterialName(tokens[1]));
            }
            /* Collect faces */
            else if (first.equals("f"))
            {
                List<OBJFace> faceList = mesh.groups.get(material);
                if (faceList == null) {
                    faceList = new ArrayList<OBJFace>();
                    mesh.groups.put(material, faceList);
                }
                
                String[] faces = SubCommandBase.dropFirstArgument(tokens);

                if (faces.length == 4)
                {
                    /* Support for quads, yay! */
                    faceList.add(new OBJFace(new String[] {faces[0], faces[1], faces[2]}));
                    faceList.add(new OBJFace(new String[] {faces[0], faces[2], faces[3]}));
                }
                else if (faces.length == 3)
                {
                    faceList.add(new OBJFace(faces));
                }
                else if (faces.length > 4)
                {
                    for (int i = 0, c = faces.length - 2; i < c; i++)
                    {
                        faceList.add(new OBJFace(new String[] {faces[0], faces[i + 1], faces[i + 2]}));
                    }
                }
            }
        }
    }

    /**
     * From collected information, form mesh data
     */
    public Map<String, IMeshes> compile()
    {
        Map<String, IMeshes> meshes = new HashMap<String, IMeshes>();

        for (OBJDataMesh obj : this.objects)
        {
            MeshesOBJ meshObject = new MeshesOBJ();

            for (Map.Entry<OBJMaterial, List<OBJFace>> group : obj.groups.entrySet())
            {
                List<OBJFace> faces = group.getValue();
                MeshOBJ mesh = new MeshOBJ(faces.size());

                int i = 0;

                for (OBJFace face : faces)
                {
                    for (OBJIndexGroup indexGroup : face.idxGroups)
                    {
                        processFaceVertex(i, indexGroup, mesh);

                        i++;
                    }
                }

                mesh.material = group.getKey();
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
}