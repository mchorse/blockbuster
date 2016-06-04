package noname.blockbuster.recording;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandPlay extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "play";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender)
    {
        return "/play <replay> <entityname> <skin_name>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            sender.addChatMessage(new TextComponentString(this.getCommandUsage(null)));
            return;
        }

        Mocap.startPlayback(args[0], sender.getEntityWorld(), true);
    }
}
