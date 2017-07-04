package mchorse.blockbuster.commands.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import mchorse.blockbuster.client.model.parsing.ModelExporter;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.command.server.CommandSummon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;

/**
 * Command /model export
 *
 * This command is responsible for converting (i.e. exporting) in-game Minecraft
 * models (ModelBase or his children) to JSON scheme that supports my custom
 * models.
 *
 * This is attempt number two, and it's a successful attempt!
 */
public class SubCommandModelExport extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "export";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.model.export";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        /* Gather needed elements for exporter class */
        String type = args[0];
        Entity entity = EntityList.createEntityByName(type, sender.getEntityWorld());
        Render render = RenderManager.instance.getEntityRenderObject(entity);

        if (render == null || !(render instanceof RendererLivingEntity) || !(entity instanceof EntityLivingBase))
        {
            L10n.error(sender, "model.export.wrong_type", type);
            return;
        }

        /* Export the model */
        ModelExporter exporter = new ModelExporter((EntityLivingBase) entity, (RendererLivingEntity) render);

        String output = exporter.export(type);
        File exportFolder = new File(ClientProxy.config.getAbsolutePath() + "/export");

        exportFolder.mkdirs();

        /* Save exported model */
        try
        {
            File destination = new File(ClientProxy.config.getAbsolutePath() + "/export/" + type + ".json");
            PrintWriter writer = new PrintWriter(destination);

            writer.print(output);
            writer.close();

            L10n.success(sender, "model.export.saved", type, destination.getName());
        }
        catch (FileNotFoundException e)
        {
            L10n.error(sender, "model.export.error_save");
        }
    }

    /**
     * Auto-complete entity type list
     *
     * Brutally ripped from {@link CommandSummon} class
     */
    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return args.length == 1 ? getListOfStringsFromIterableMatchingLastWord(args, EntityList.func_151515_b()) : Collections.<String> emptyList();
    }
}
