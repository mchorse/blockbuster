package noname.blockbuster.recording;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

/**
 * Command play
 *
 * This command is complementary command of Command record. This command plays
 * acted scene with given file name of the record, new displayed name and of
 * course skin.
 *
 * Side note: you can use this command in command block.
 */
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
        return "/play <replay> <entity_name> <skin_name>, e.g.: /play forest_gump_bench_scene Forrest ForrestGump\nSide note: <entity_name> and <skin_name> should be without spaces, sorry, but that's how minecraft parses strings";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 3)
        {
            sender.addChatMessage(new TextComponentString(this.getCommandUsage(null)));
            return;
        }

        Mocap.startPlayback(args[0], args[1], args[2], sender.getEntityWorld(), true);
    }
}
