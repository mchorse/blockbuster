package mchorse.blockbuster.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;

public class CommandMount extends BBCommandBase
{
    @Override
    public String getName()
    {
        return "mount";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.mount.help";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}mount {8}<target>{r} {7}[destination]{r}";
    }

    @Override
    public int getRequiredArgs()
    {
        return 1;
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        Entity target = getEntity(server, sender, args[0]);

        if (args.length > 1)
        {
            target.startRiding(getEntity(server, sender, args[1]));
        }
        else
        {
            target.dismountRidingEntity();
        }
    }
}
