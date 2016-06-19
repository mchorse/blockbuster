package noname.blockbuster.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import noname.blockbuster.tileentity.AbstractDirectorTileEntity;

/**
 * Command play director
 *
 * This command is triggering playback in a director block which is located
 * in passed coordinates. Makes a nice addition to adventure maps and command
 * blocks.
 *
 * Side note: you can use this command in command block.
 */
public class CommandPlayDirector extends CommandBase
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
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 3)
        {
            throw new WrongUsageException(this.getCommandUsage(null));
        }

        CommandBase.CoordinateArg x = parseCoordinate(0, args[0], false);
        CommandBase.CoordinateArg y = parseCoordinate(0, args[1], false);
        CommandBase.CoordinateArg z = parseCoordinate(0, args[2], false);

        BlockPos pos = new BlockPos(x.func_179628_a(), y.func_179628_a(), z.func_179628_a());

        ((AbstractDirectorTileEntity) server.getEntityWorld().getTileEntity(pos)).startPlayback();
    }
}