package mchorse.blockbuster.client.textures;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import mchorse.blockbuster.utils.mclib.GifFolder;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;

public class GifFrameTexture extends AbstractTexture
{
    public GifFolder file;
    public int index;

    public GifFrameTexture(GifFolder file, int index)
    {
        this.file = file;
        this.index = index;
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException
    {
        this.deleteGlTexture();

        if (!this.file.exists())
        {
            throw new FileNotFoundException(this.file.getFilePath());
        }

        if (!tryLoadMultiTex())
        {
            TextureUtil.uploadTextureImage(this.getGlTextureId(), this.file.gif.getFrame(this.index));
        }
    }

    private boolean tryLoadMultiTex()
    {
        try
        {
            Class<?> config = Class.forName("Config");
            Method isShaders = config.getMethod("isShaders");

            if (!Boolean.TRUE.equals(isShaders.invoke(null)))
            {
                return false;
            }
        }
        catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            return false;
        }

        BufferedImage frame = this.file.gif.getFrame(this.index);
        int width = frame.getWidth();
        int height = frame.getHeight();
        int[] aint = new int[width * height * 3];

        frame.getRGB(0, 0, width, height, aint, 0, width);

        String path = this.file.getFilePath();

        path = path.substring(0, path.length() - 4);

        GifFolder normal = new GifFolder(path + "_n.gif");
        GifFolder specular = new GifFolder(path + "_s.gif");

        if (normal.exists())
        {
            frame = normal.gif.getFrame(this.index);
            frame.getRGB(0, 0, width, height, aint, width * height, width);
        }
        else
        {
            Arrays.fill(aint, width * height, width * height * 2, 0xFF7F7FFF);
        }

        if (specular.exists())
        {
            frame = specular.gif.getFrame(this.index);
            frame.getRGB(0, 0, width, height, aint, width * height * 2, width);
        }
        else
        {
            Arrays.fill(aint, width * height * 2, width * height * 3, 0);
        }

        try
        {
            Class<?> shadersTex = Class.forName("net.optifine.shaders.ShadersTex");

            Method getMultiTexID = null;
            Method setupTexture = null;

            for (Method method : shadersTex.getMethods())
            {
                if ("getMultiTexID".equals(method.getName()))
                {
                    getMultiTexID = method;
                }
                else if ("setupTexture".equals(method.getName()))
                {
                    setupTexture = method;
                }
            }

            if (getMultiTexID == null || setupTexture == null)
            {
                return false;
            }

            Object multiTex = getMultiTexID.invoke(null, this);

            setupTexture.invoke(null, multiTex, aint, width, height, false, false);
        }
        catch (ClassNotFoundException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            return false;
        }

        return true;
    }
}
