package mchorse.blockbuster.api.formats.obj;

import java.util.ArrayList;
import java.util.List;

/**
 * Mesh from OBJ file
 *
 * It holds faces for every object found in OBJ file
 */
public class OBJDataMesh
{
    public String name;
    public List<OBJDataGroup> groups = new ArrayList<OBJDataGroup>();
}
