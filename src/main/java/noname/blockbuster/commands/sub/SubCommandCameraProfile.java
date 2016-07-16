package noname.blockbuster.commands.sub;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import noname.blockbuster.commands.CommandCamera;

public class SubCommandCameraProfile extends CommandCameraBase
{
    public SubCommandCameraProfile(CommandCamera parent)
    {
        super(parent);
    }

    @Override
    public String getCommandName()
    {
        return "profile";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.profile";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        if (args[0].equals("remove"))
        {
            this.parent.runner.getProfile().remove(CommandBase.parseInt(args[1]));
        }
    }
}
