package noname.blockbuster.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import noname.blockbuster.tileentity.AbstractTileEntityDirector;

/**
 * Base command class for commands that need director
 *
 * There's only one method that is useful {@link CommandDirector#getDirector}
 */
public abstract class CommandDirector extends CommandBase
{
    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    /**
     * Get abstract director from argument strings
     */
    protected AbstractTileEntityDirector getDirector(MinecraftServer server, String x, String y, String z) throws CommandException
    {
        CommandBase.CoordinateArg cX = parseCoordinate(0, x, false);
        CommandBase.CoordinateArg cY = parseCoordinate(0, y, false);
        CommandBase.CoordinateArg cZ = parseCoordinate(0, z, false);

        BlockPos pos = new BlockPos(cX.getResult(), cY.getResult(), cZ.getResult());

        return (AbstractTileEntityDirector) server.getEntityWorld().getTileEntity(pos);
    }
}
