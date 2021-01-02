package mchorse.blockbuster.commands.model;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.utils.L10n;
import mchorse.blockbuster.utils.TextureUtils;
import mchorse.mclib.utils.files.GlobalTree;
import mchorse.mclib.utils.files.entries.AbstractEntry;
import mchorse.mclib.utils.files.entries.FileEntry;
import mchorse.mclib.utils.files.entries.FolderEntry;
import mchorse.mclib.utils.resources.FilteredResourceLocation;
import mchorse.mclib.utils.resources.MultiResourceLocation;
import mchorse.mclib.utils.resources.RLUtils;
import mchorse.mclib.utils.resources.TextureProcessor;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SubCommandModelCombine extends CommandBase
{
    @Override
    public String getName()
    {
        return "combine";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.model.combine";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getUsage(sender));
        }

        List<MultiResourceLocation> toExport = new ArrayList<MultiResourceLocation>();
        List<FolderEntry> entries = new ArrayList<FolderEntry>();

        for (String path : args)
        {
            FolderEntry entry = GlobalTree.TREE.getByPath("b.a/" + path, null);

            if (entry != null)
            {
                entries.add(entry);
            }
        }

        if (entries.isEmpty())
        {
            L10n.error(sender, "commands.combining_empty", toExport.size());
            return;
        }

        this.generate(toExport, entries);

        if (toExport.isEmpty())
        {
            L10n.error(sender, "commands.combining_folders_empty", toExport.size());
            return;
        }

        L10n.info(sender, "commands.started_combining", toExport.size());

        try
        {
            new Thread(new CombineThread(sender, toExport)).start();
        }
        catch (Exception e)
        {}
    }

    private void generate(List<MultiResourceLocation> toExport, List<FolderEntry> entries)
    {
        this.generateRLs(entries, entries.get(0), 0, "", (string) ->
        {
            String[] splits = string.substring(1).split("!");
            MultiResourceLocation location = new MultiResourceLocation();

            for (String split : splits)
            {
                location.children.add(new FilteredResourceLocation(RLUtils.create(split)));
            }

            toExport.add(location);
        });
    }

    private void generateRLs(List<FolderEntry> entries, FolderEntry folder, int index, String prefix, Consumer<String> callback)
    {
        for (AbstractEntry entry : folder.getEntries())
        {
            if (entry instanceof FileEntry)
            {
                FileEntry file = (FileEntry) entry;

                if (index == entries.size() - 1)
                {
                    callback.accept(prefix + "!" + file.resource);
                }
                else
                {
                    this.generateRLs(entries, entries.get(index + 1), index + 1, prefix + "!" + file.resource, callback);
                }
            }
        }
    }

    /**
     * Local thread that combines all the skins instead of
     * hanging the game until it's done...
     */
    public static class CombineThread implements Runnable
    {
        public ICommandSender sender;
        public List<MultiResourceLocation> locations;

        public CombineThread(ICommandSender sender, List<MultiResourceLocation> locations)
        {
            this.sender = sender;
            this.locations = locations;
        }

        @Override
        public void run()
        {
            int i = 0;

            for (MultiResourceLocation location : this.locations)
            {
                try
                {
                    BufferedImage image = TextureProcessor.process(location);
                    File folder = new File(ClientProxy.configFile, "export");
                    File file = TextureUtils.getFirstAvailableFile(folder, "combined_" + i);

                    ImageIO.write(image, "png", file);

                    L10n.info(this.sender, "commands.combined", i);

                    Thread.sleep(50);
                }
                catch (Exception e)
                {}

                i += 1;
            }

            L10n.info(this.sender, "commands.finished_combining");
        }
    }
}