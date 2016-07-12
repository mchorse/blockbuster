package noname.blockbuster.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import noname.blockbuster.tileentity.AbstractTileEntityDirector;

/**
 * Command play director
 *
 * This command is triggering playback in a director block which is located
 * in passed coordinates. Makes a nice addition to adventure maps and command
 * blocks.
 *
 * Side note: you can use this command in command block.
 */
public class CommandPlayDirector extends CommandDirector
{
    @Override
    public String getCommandName()
    {
        return "play-director";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender)
    {
        return "blockbuster.commands.play_director";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 3)
        {
            throw new WrongUsageException(this.getCommandUsage(null));
        }

        AbstractTileEntityDirector director = this.getDirector(server, args[0], args[1], args[2]);

        if (director == null)
        {
            throw new CommandException("blockbuster.commands.no_director", args[0], args[1], args[2]);
        }

        director.startPlayback();
    }
}