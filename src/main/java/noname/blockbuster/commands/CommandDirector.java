package noname.blockbuster.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import noname.blockbuster.tileentity.AbstractTileEntityDirector;

/**
 * Command /director
 *
 * This command is responsible for playing or stopping director block.
 *
 * This command is unified version of previous existed commands CommandStopDirector
 * and CommandPlayDirector. These unifications were made to avoid duplicate
 * code, and less command memorizing.
 */
public class CommandDirector extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "director";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender)
    {
        return "blockbuster.commands.director";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 4)
        {
            throw new WrongUsageException(this.getCommandUsage(null));
        }

        String action = args[0];
        AbstractTileEntityDirector director = this.getDirector(server, args[1], args[2], args[3]);

        if (action.equals("play"))
        {
            director.startPlayback();
        }
        else if (action.equals("stop"))
        {
            director.stopPlayback();
        }
    }

    /**
     * Get abstract director from argument strings
     */
    protected AbstractTileEntityDirector getDirector(MinecraftServer server, String x, String y, String z) throws CommandException
    {
        CommandBase.CoordinateArg cX = CommandBase.parseCoordinate(0, x, false);
        CommandBase.CoordinateArg cY = CommandBase.parseCoordinate(0, y, false);
        CommandBase.CoordinateArg cZ = CommandBase.parseCoordinate(0, z, false);

        BlockPos pos = new BlockPos(cX.getResult(), cY.getResult(), cZ.getResult());
        TileEntity entity = server.getEntityWorld().getTileEntity(pos);

        if (entity instanceof AbstractTileEntityDirector)
        {
            return (AbstractTileEntityDirector) entity;
        }

        throw new CommandException("blockbuster.commands.no_director", x, y, z);
    }
}