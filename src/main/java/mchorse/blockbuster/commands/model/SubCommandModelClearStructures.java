package mchorse.blockbuster.commands.model;

import mchorse.blockbuster.commands.BBCommandBase;
import mchorse.blockbuster_pack.morphs.StructureMorph;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Command /model clear_structures
 */
public class SubCommandModelClearStructures extends BBCommandBase
{
    @Override
    public String getName()
    {
        return "clear_structures";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.model.clear_structures";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}model {8}clear_structures{r}";
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        StructureMorph.reloadStructures();
    }
}