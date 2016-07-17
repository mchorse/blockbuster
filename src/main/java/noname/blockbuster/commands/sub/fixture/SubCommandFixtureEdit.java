package noname.blockbuster.commands.sub.fixture;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import noname.blockbuster.commands.CommandCamera;
import noname.blockbuster.commands.sub.SubCommandBase;

public class SubCommandFixtureEdit extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "edit";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.fixture.edit";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        int index = CommandBase.parseInt(args[0]);

        CommandCamera.runner.getProfile().get(index).edit(SubCommandBase.dropFirstArgument(args), (EntityPlayer) sender);
    }
}
