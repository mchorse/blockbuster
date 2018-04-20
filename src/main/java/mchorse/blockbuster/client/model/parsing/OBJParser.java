package mchorse.blockbuster.client.model.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mchorse.blockbuster.commands.SubCommandBase;

/**
 * OBJ file parser and loader
 * 
 * Taken code from my unpolished 3d video game.
 */
public class OBJParser
{
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
     * Load separate meshes from OBJ file
     */
    public static Map<String, Mesh> loadMeshes(File file) throws Exception
    {
        List<String> lines = readAllLines(file);

        List<Vector3f> vertices = new ArrayList<Vector3f>();
        List<Vector2f> textures = new ArrayList<Vector2f>();
        List<Vector3f> normals = new ArrayList<Vector3f>();
        List<OBJMesh> objects = new ArrayList<OBJMesh>();

        OBJMesh mesh = null;

        for (String line : lines)
        {
            String[] tokens = line.split("\\s+");

            if (tokens[0].equals("o"))
            {
                if (mesh != null)
                {
                    objects.add(mesh);
                }

                mesh = new OBJMesh();
                mesh.name = tokens[1];
            }

            String first = tokens[0];

            /* Vertices */
            if (first.equals("v"))
            {
                vertices.add(new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
            }
            /* Texture coordinates (UV) */
            else if (first.equals("vt"))
            {
                textures.add(new Vector2f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2])));
            }
            /* Who needs normals? */
            else if (first.equals("vn"))
            {
                normals.add(new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
            }
            /* Collect faces */
            else if (first.equals("f"))
            {
                String[] faces = SubCommandBase.dropFirstArgument(tokens);

                if (faces.length == 4)
                {
                    /* Support for quads, yay! */
                    mesh.faces.add(new Face(new String[] {faces[0], faces[1], faces[2]}));
                    mesh.faces.add(new Face(new String[] {faces[0], faces[2], faces[3]}));
                }
                else
                {
                    mesh.faces.add(new Face(faces));
                }
            }
        }

        objects.add(mesh);

        return reorderLists(vertices, textures, normals, objects, false);
    }

    /**
     * From collected information, form mesh data
     */
    private static Map<String, Mesh> reorderLists(List<Vector3f> posList, List<Vector2f> textCoordList, List<Vector3f> normList, List<OBJMesh> objectsList, boolean unused)
    {
        Map<String, Mesh> meshes = new HashMap<String, Mesh>();

        for (OBJMesh obj : objectsList)
        {
            List<Face> facesList = obj.faces;

            /* Initiate arrays for mesh data */
            float[] posArr = new float[facesList.size() * 3 * 3];
            float[] textCoordArr = new float[facesList.size() * 3 * 2];
            float[] normArr = new float[facesList.size() * 3 * 3];

            int i = 0;

            for (Face face : facesList)
            {
                /* Quad support */
                for (IdxGroup indValue : face.idxGroups)
                {
                    processFaceVertex(i, indValue, posList, textCoordList, normList, posArr, textCoordArr, normArr);

                    i++;
                }
            }

            meshes.put(obj.name, new Mesh(posArr, textCoordArr, normArr));
        }

        return meshes;
    }

    /**
     * Place all the data to complementary arrays 
     */
    private static void processFaceVertex(int i, IdxGroup indices, List<Vector3f> posList, List<Vector2f> textCoordList, List<Vector3f> normList, float[] posArr, float[] texCoordArr, float[] normArr)
    {
        if (indices.idxPos >= 0)
        {
            Vector3f vec = posList.get(indices.idxPos);

            posArr[i * 3] = vec.x;
            posArr[i * 3 + 1] = vec.y;
            posArr[i * 3 + 2] = vec.z;
        }

        if (indices.idxTextCoord >= 0)
        {
            Vector2f textCoord = textCoordList.get(indices.idxTextCoord);

            texCoordArr[i * 2] = textCoord.x;
            texCoordArr[i * 2 + 1] = 1 - textCoord.y;
        }

        if (indices.idxVecNormal >= 0)
        {
            Vector3f vecNorm = normList.get(indices.idxVecNormal);

            normArr[i * 3] = vecNorm.x;
            normArr[i * 3 + 1] = vecNorm.y;
            normArr[i * 3 + 2] = vecNorm.z;
        }
    }

    /**
     * Face that holds indices for loading data
     */
    protected static class Face
    {
        /**
         * List of idxGroup groups for a face triangle (3 vertices per face).
         */
        public IdxGroup[] idxGroups = new IdxGroup[3];

        public Face(String[] lines)
        {
            for (int i = 0; i < 3; i++)
            {
                this.idxGroups[i] = this.parseLine(lines[i]);
            }
        }

        /**
         * Parse index group from a string in format of "1/2/3". It can be also 
         * "1//3" if the model doesn't provides texture coordinates.
         */
        private IdxGroup parseLine(String line)
        {
            IdxGroup idxGroup = new IdxGroup();
            String[] lineTokens = line.split("/");
            int length = lineTokens.length;

            idxGroup.idxPos = Integer.parseInt(lineTokens[0]) - 1;

            if (length > 1)
            {
                /* It can be empty if the obj does not define text coords */
                String textCoord = lineTokens[1];

                if (!textCoord.isEmpty())
                {
                    idxGroup.idxTextCoord = Integer.parseInt(textCoord) - 1;
                }

                if (length > 2)
                {
                    idxGroup.idxVecNormal = Integer.parseInt(lineTokens[2]) - 1;
                }
            }

            return idxGroup;
        }
    }

    /**
     * Index group class
     * 
     * This class represents an index group. Used in {@link Face} class to 
     * represent an index group for looking up vertices from the collected 
     * arrays. 
     */
    protected static class IdxGroup
    {
        public static final int NO_VALUE = -1;

        public int idxPos = NO_VALUE;
        public int idxTextCoord = NO_VALUE;
        public int idxVecNormal = NO_VALUE;
    }

    /**
     * Mesh from OBJ file
     * 
     * It holds faces for every object found in OBJ file
     */
    protected static class OBJMesh
    {
        public String name;
        public List<Face> faces = new ArrayList<Face>();
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

    /**
     * Holds the mesh data 
     */
    public static class Mesh
    {
        public float[] posData;
        public float[] texData;
        public float[] normData;

        public Mesh(float[] posData, float[] texData, float[] normData)
        {
            this.posData = posData;
            this.texData = texData;
            this.normData = normData;
        }
    }
}