package mchorse.blockbuster.commands.record;

import java.util.List;

import mchorse.blockbuster.commands.McCommandBase;
import mchorse.blockbuster.recording.RecordUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public abstract class SubCommandRecordBase extends McCommandBase
{
    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, RecordUtils.getReplays());
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
    }
}