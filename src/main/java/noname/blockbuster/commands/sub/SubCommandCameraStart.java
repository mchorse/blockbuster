package noname.blockbuster.commands.sub;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noname.blockbuster.commands.CommandCamera;

public class SubCommandCameraStart extends CommandCameraBase
{
    public SubCommandCameraStart(CommandCamera parent)
    {
        super(parent);
    }

    @Override
    public String getCommandName()
    {
        return "start";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.play";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        this.parent.runner.start();
    }
}
