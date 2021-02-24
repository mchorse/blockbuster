package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.commands.BBCommandBase;
import mchorse.blockbuster.recording.RecordUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public abstract class SubCommandRecordBase extends BBCommandBase
{
    @Override
    public int getRequiredArgs()
    {
        return 1;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, RecordUtils.getReplays());
        }

        return super.getTabCompletions(server, sender, args, pos);
    }
}