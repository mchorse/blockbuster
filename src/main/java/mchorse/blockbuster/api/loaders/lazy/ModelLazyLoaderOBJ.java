package mchorse.blockbuster.api.loaders.lazy;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.api.formats.IMeshes;
import mchorse.blockbuster.api.formats.obj.MeshesOBJ;
import mchorse.blockbuster.api.formats.obj.OBJDataMesh;
import mchorse.blockbuster.api.formats.obj.OBJParser;
import mchorse.blockbuster.api.resource.FileEntry;
import mchorse.blockbuster.api.resource.IResourceEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelLazyLoaderOBJ extends ModelLazyLoaderJSON
{
    public IResourceEntry obj;
    public IResourceEntry mtl;

    public List<IResourceEntry> shapes = new ArrayList<IResourceEntry>();

    private OBJParser parser;
    private long lastModified;

    public ModelLazyLoaderOBJ(IResourceEntry model, IResourceEntry obj, IResourceEntry mtl, File shapes)
    {
        super(model);

        this.obj = obj;
        this.mtl = mtl;

        this.setupShapes(shapes);
    }

    private void setupShapes(File shapes)
    {
        if (shapes == null)
        {
            return;
        }

        File[] files = shapes.listFiles();

        if (files == null)
        {
            return;
        }

        for (File file : files)
        {
            if (file.isFile() && file.getName().endsWith(".obj"))
            {
                this.shapes.add(new FileEntry(file));
            }
        }
    }

    @Override
    public int count()
    {
        int count = super.count() + (this.obj.exists() ? 2 : 0) + (this.mtl.exists() ? 4 : 0);
        int bit = 3;

        for (IResourceEntry shape : this.shapes)
        {
            if (shape.exists())
            {
                count += shape.exists() ? 1 << bit : 0;
            }

            bit++;
        }

        return count;
    }

    @Override
    public boolean hasChanged()
    {
        boolean hasChanged = super.hasChanged() || this.obj.hasChanged() || this.mtl.hasChanged();

        for (IResourceEntry shape : this.shapes)
        {
            hasChanged = hasChanged || shape.hasChanged();
        }

        return hasChanged;
    }

    @Override
    public Model loadModel(String key) throws Exception
    {
        Model model = null;

        try
        {
            model = super.loadModel(key);
        }
        catch (Exception e) {}

        if (model == null)
        {
            model = this.generateOBJModel(key);
        }

        for (IResourceEntry entry : this.shapes)
        {
            if (!entry.exists())
            {
                continue;
            }

            String name = entry.getName();

            name = name.substring(0, name.lastIndexOf("."));
            model.shapes.add(name);
        }

        return model;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected Map<String, IMeshes> getMeshes(String key, Model model) throws Exception
    {
        try
        {
            OBJParser parser = this.getOBJParser(key, model);
            Map<String, IMeshes> meshes = parser.compile();

            for (IResourceEntry shape : this.shapes)
            {
                try
                {
                    OBJParser shapeParser = new OBJParser(shape.getStream(), null);

                    shapeParser.read();
                    this.mergeParsers(shape.getName(), meshes, shapeParser);
                }
                catch (Exception e)
                {}
            }

            this.parser = null;
            this.lastModified = 0;

            return meshes;
        }
        catch (Exception e) {}

        return null;
    }

    /**
     * Merges data of shape parsers
     */
    private void mergeParsers(String name, Map<String, IMeshes> meshes, OBJParser shapeParser)
    {
        name = name.substring(0, name.lastIndexOf("."));

        Map<String, IMeshes> shapeMeshes = shapeParser.compile();

        for (Map.Entry<String, IMeshes> entry : meshes.entrySet())
        {
            IMeshes shapeMesh = shapeMeshes.get(entry.getKey());

            if (shapeMesh != null)
            {
                ((MeshesOBJ) entry.getValue()).mergeShape(name, (MeshesOBJ) shapeMesh);
            }
        }
    }

    /**
     * Create an OBJ parser
     */
    public OBJParser getOBJParser(String key, Model model)
    {
        if (!model.providesObj)
        {
            return null;
        }

        long lastModified = Math.max(this.model.lastModified(), Math.max(this.obj.lastModified(), this.mtl.lastModified()));

        if (this.lastModified < lastModified)
        {
            this.lastModified = lastModified;
        }
        else
        {
            return this.parser;
        }

        try
        {
            InputStream obj = this.obj.getStream();
            InputStream mtl = model.providesMtl ? this.mtl.getStream() : null;

            this.parser = new OBJParser(obj, mtl);
            this.parser.read();

            if (this.mtl instanceof FileEntry)
            {
                this.parser.setupTextures(key, ((FileEntry) this.mtl).file.getParentFile());
            }

            model.materials.putAll(this.parser.materials);
        }
        catch (Exception e)
        {
            return null;
        }

        return this.parser;
    }

    /**
     * Generate custom model based on given OBJ
     */
    private Model generateOBJModel(String model)
    {
        /* Generate custom model for an OBJ model */
        Model data = new Model();
        ModelPose blocky = new ModelPose();

        blocky.setSize(1, 1, 1);
        data.poses.put("flying", blocky.clone());
        data.poses.put("standing", blocky.clone());
        data.poses.put("sneaking", blocky.clone());
        data.poses.put("sleeping", blocky.clone());
        data.poses.put("riding", blocky.clone());
        data.name = model;

        data.providesObj = true;
        data.providesMtl = this.mtl.exists();

        /* Generate limbs */
        OBJParser parser = this.getOBJParser(model, data);

        if (parser != null)
        {
            for (OBJDataMesh mesh : parser.objects)
            {
                data.addLimb(mesh.name);
            }
        }

        if (data.limbs.isEmpty())
        {
            data.addLimb("body");
        }

        data.legacyObj = false;

        return data;
    }

    @Override
    public boolean copyFiles(File folder)
    {
        boolean result = super.copyFiles(folder);

        result = this.obj.copyTo(new File(folder, this.obj.getName())) || result;
        result = this.mtl.copyTo(new File(folder, this.mtl.getName())) || result;

        for (IResourceEntry shape : this.shapes)
        {
            result = shape.copyTo(new File(folder, "shapes/" + shape.getName())) || result;
        }

        return result;
    }
}