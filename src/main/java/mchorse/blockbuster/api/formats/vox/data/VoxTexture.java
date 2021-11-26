package mchorse.blockbuster.api.formats.vox.data;

import java.util.Arrays;

import net.minecraft.client.renderer.texture.DynamicTexture;

public class VoxTexture extends DynamicTexture
{
    private int[] palette;
    private int specular;

    public VoxTexture(int[] palette, int specular)
    {
        super(Math.max(palette.length, 1), 1);

        this.palette = palette;
        this.specular = specular;

        this.updatePalette();
    }

    public void updatePalette()
    {
        int[] tex = this.getTextureData();

        for (int i = 0; i < this.palette.length; i++)
        {
            tex[i] = this.palette[i];
        }

        if (tex.length == 3 * this.palette.length)
        {
            Arrays.fill(tex, 2 * this.palette.length, tex.length, specular);
        }

        this.updateDynamicTexture();
    }
}