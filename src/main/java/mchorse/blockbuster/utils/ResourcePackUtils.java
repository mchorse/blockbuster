package mchorse.blockbuster.utils;

import mchorse.blockbuster.Blockbuster;
import mchorse.mclib.utils.resources.RLUtils;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.LegacyV2Adapter;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.FMLFolderResourcePack;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourcePackUtils
{
    public static List<ResourceLocation> getAllPictures(IResourceManager resourceManager)
    {
        List<IResourcePack> packs = getPacks();

        if (packs == null)
        {
            return Collections.emptyList();
        }

        List<ResourceLocation> locations = new ArrayList<ResourceLocation>();

        for (IResourcePack pack : packs)
        {
            if (!pack.getResourceDomains().contains(Blockbuster.MOD_ID))
            {
                continue;
            }

            locations.addAll(getLocations(pack, "", fileName -> fileName.endsWith(".png")));

            break;
        }

        return locations;
    }

    private static List<IResourcePack> getPacks()
    {
        try
        {
            Field field = FMLClientHandler.class.getDeclaredField("resourcePackList");
            field.setAccessible(true);

            return (List<IResourcePack>) field.get(FMLClientHandler.instance());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private static List<ResourceLocation> getLocations(IResourcePack pack, String folder, Predicate<String> predicate)
    {
        if (pack instanceof LegacyV2Adapter)
        {
            LegacyV2Adapter adapter = (LegacyV2Adapter) pack;
            Field packField = null;

            for (Field field : adapter.getClass().getDeclaredFields())
            {
                if (field.getType() == IResourcePack.class)
                {
                    packField = field;

                    break;
                }
            }

            if (packField != null)
            {
                packField.setAccessible(true);

                try
                {
                    return getLocations((IResourcePack) packField.get(adapter), folder, predicate);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        List<ResourceLocation> locations = new ArrayList<ResourceLocation>();

        if (pack instanceof FolderResourcePack)
        {
            handleFolderResourcePack((FolderResourcePack) pack, folder, predicate, locations);
        }
        else if (pack instanceof FileResourcePack)
        {
            handleZipResourcePack((FileResourcePack) pack, folder, predicate, locations);
        }

        return locations;
    }

    /* Folder handling */

    private static void handleFolderResourcePack(FolderResourcePack folderPack, String folder, Predicate<String> predicate, List<ResourceLocation> locations)
    {
        Field fileField = null;

        for (Field field : AbstractResourcePack.class.getDeclaredFields())
        {
            if (field.getType() == File.class)
            {
                fileField = field;

                break;
            }
        }

        if (fileField != null)
        {
            fileField.setAccessible(true);

            try
            {
                File file = (File) fileField.get(folderPack);
                Set<String> domains = folderPack.getResourceDomains();

                if (folderPack instanceof FMLFolderResourcePack)
                {
                    domains.add(((FMLFolderResourcePack) folderPack).getFMLContainer().getModId());
                }

                if (!folder.isEmpty())
                {
                    folder += "/";
                }

                for (String domain : domains)
                {
                    String prefix = "assets/" + domain + "/" + folder;
                    File pathFile = new File(file, prefix);

                    enumerateFiles(folderPack, pathFile, predicate, locations, domain, folder);
                }
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static void enumerateFiles(FolderResourcePack folderPack, File parent, Predicate<String> predicate, List<ResourceLocation> locations, String domain, String prefix)
    {
        File[] files = parent.listFiles();

        if (files == null)
        {
            return;
        }

        for (File file : files)
        {
            if (file.isFile() && predicate.test(file.getName()))
            {
                locations.add(RLUtils.create(domain, prefix + file.getName()));
            }
            else if (file.isDirectory())
            {
                enumerateFiles(folderPack, file, predicate, locations, domain, prefix + file.getName() + "/");
            }
        }
    }

    /* Zip handling */

    private static void handleZipResourcePack(FileResourcePack filePack, String folder, Predicate<String> predicate, List<ResourceLocation> locations)
    {
        Field zipField = null;

        for (Field field : FileResourcePack.class.getDeclaredFields())
        {
            if (field.getType() == ZipFile.class)
            {
                zipField = field;

                break;
            }
        }

        if (zipField != null)
        {
            zipField.setAccessible(true);

            try
            {
                enumerateZipFile(filePack, folder, (ZipFile) zipField.get(filePack), predicate, locations);
            }
            catch (IllegalAccessException e)
            {}
        }
    }

    private static void enumerateZipFile(FileResourcePack filePack, String folder, ZipFile file, Predicate<String> predicate, List<ResourceLocation> locations)
    {
        Set<String> domains = filePack.getResourceDomains();
        Enumeration<? extends ZipEntry> it = file.entries();

        while (it.hasMoreElements())
        {
            String name = it.nextElement().getName();

            for (String domain : domains)
            {
                String assets = "assets/" + domain + "/";
                String path = assets + (folder.isEmpty() ? "" : folder + "/");

                if (name.startsWith(path) && predicate.test(name))
                {
                    locations.add(RLUtils.create(domain, name.substring(assets.length())));
                }
            }
        }
    }
}