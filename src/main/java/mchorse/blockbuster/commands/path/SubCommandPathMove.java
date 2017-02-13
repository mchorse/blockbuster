package mchorse.blockbuster.commands.path;

import mchorse.blockbuster.camera.fixtures.AbstractFixture;
import mchorse.blockbuster.camera.fixtures.PathFixture;
import mchorse.blockbuster.commands.CommandCamera;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

/**
 * Path's sub-command /camera path move
 *
 * This sub-command is responsible for moving a point to another index in
 * a path fixture.
 */
public class SubCommandPathMove extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "move";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.path.move";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 3)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        int index = CommandBase.parseInt(args[0]);
        int from = CommandBase.parseInt(args[1]);
        int to = CommandBase.parseInt(args[2]);
        AbstractFixture fixture = CommandCamera.getProfile().get(index);

        if (!(fixture instanceof PathFixture))
        {
            L10n.error(sender, "profile.not_path", index);
            return;
        }

        PathFixture path = (PathFixture) fixture;

        path.movePoint(from, to);
    }
}