package noname.blockbuster.commands.fixture;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
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
        return "blockbuster.commands.camera.fixture.add";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        try
        {
            EntityPlayerMP player = getCommandSenderAsPlayer(sender);

            CommandCamera.getProfile().add(AbstractFixture.fromCommand(args, player));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
