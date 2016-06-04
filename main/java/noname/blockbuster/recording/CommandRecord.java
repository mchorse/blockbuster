package noname.blockbuster.recording;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandRecord extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "record";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender)
    {
        return "Usage: /record <savefile>, eg: /record forestrun";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayer player = getCommandSenderAsPlayer(sender);

        if (args.length < 1)
        {
            sender.addChatMessage(new TextComponentString(this.getCommandUsage(null)));
            return;
        }

        Mocap.startRecording(args[0], player);
    }
}
