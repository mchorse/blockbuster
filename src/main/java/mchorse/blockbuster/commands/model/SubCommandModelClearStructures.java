package mchorse.blockbuster.commands.model;

import mchorse.blockbuster_pack.morphs.StructureMorph;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Command /model clear_structures
 */
public class SubCommandModelClearStructures extends CommandBase
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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        StructureMorph.reloadStructures();
    }
}