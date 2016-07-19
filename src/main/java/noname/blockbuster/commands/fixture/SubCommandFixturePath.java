package noname.blockbuster.commands.fixture;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import noname.blockbuster.camera.Position;
import noname.blockbuster.camera.fixtures.AbstractFixture;
import noname.blockbuster.camera.fixtures.PathFixture;
import noname.blockbuster.commands.CommandCamera;

public class SubCommandFixturePath extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "path";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.fixture.path";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        int index = CommandBase.parseInt(args[0]);

        AbstractFixture fixture = CommandCamera.runner.getProfile().get(index);

        if (!(fixture instanceof PathFixture))
        {
            throw new CommandException("Fixture at index %s isn't a path fixture!", args[0]);
        }

        PathFixture path = (PathFixture) fixture;

        if (args.length < 2)
        {
            path.addPoint(new Position((EntityPlayer) sender));
        }
        else
        {
            path.removePoint(CommandBase.parseInt(args[1]));
        }
    }
}
