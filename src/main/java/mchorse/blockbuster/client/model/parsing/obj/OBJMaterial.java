package mchorse.blockbuster.client.model.parsing.obj;

import net.minecraft.util.ResourceLocation;

/**
 * OBJ material
 * 
 * This class stores information about OBJ material from MTL file
 */
public class OBJMaterial
{
    public String name = "";

    public float r;
    public float g;
    public float b;

    public boolean useTexture;
    public ResourceLocation texture;
}