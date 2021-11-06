package mchorse.blockbuster.api.formats.obj;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mesh from OBJ file
 *
 * It holds faces for every object found in OBJ file
 */
public class OBJDataMesh
{
    public String name;
    public Map<OBJMaterial, List<OBJFace>> groups = new LinkedHashMap<OBJMaterial, List<OBJFace>>();
}
