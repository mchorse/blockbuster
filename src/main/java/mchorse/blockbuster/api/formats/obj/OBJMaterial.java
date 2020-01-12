package mchorse.blockbuster.api.formats.obj;

import net.minecraft.util.ResourceLocation;

/**
 * OBJ material
 * 
 * This class stores information about OBJ material from MTL file
 */
public class OBJMaterial
{
    public String name = "";

    public float r = 1;
    public float g = 1;
    public float b = 1;

    public boolean useTexture;
    public boolean linear = false;
    public ResourceLocation texture;

    public OBJMaterial(String name)
    {
        this.name = name;
    }
}