package noname.blockbuster.commands.sub.fixture;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import noname.blockbuster.camera.fixtures.AbstractFixture;
import noname.blockbuster.commands.CommandCamera;

public class SubCommandFixtureAdd extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "add";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.command.camera.fixture.add";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        CommandCamera.runner.getProfile().add(AbstractFixture.fromCommand(args, (EntityPlayer) sender));
    }
}
