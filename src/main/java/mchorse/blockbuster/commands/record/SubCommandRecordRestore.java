package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.util.List;

public class SubCommandRecordRestore extends SubCommandRecordBase
{
    @Override
    public String getName()
    {
        return "restore";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.restore";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}record {8}restore{r} {7}<filename> <iteration>{r}";
    }

    @Override
    public int getRequiredArgs() {
        return 2;
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        String name = args[0];
        String iteration = args[1];
        Record record = CommandRecord.getRecord(name);

        if (RecordUtils.getReplayIterations(record.filename).contains(iteration))
        {
            File current = RecordUtils.replayFile(name);
            File toRestore = new File(RecordUtils.replayFile(name).getAbsolutePath() + "~" + iteration);
            File temporary = RecordUtils.replayFile("mchorse is the coolest");

            if (temporary.exists())
            {
                Blockbuster.l10n.error(sender, "record.cant_restore", name);
            }
            else
            {
                current.renameTo(temporary);
                toRestore.renameTo(current);
                temporary.renameTo(toRestore);

                CommonProxy.manager.records.remove(record.filename);
                RecordUtils.unloadRecord(record);

                Blockbuster.l10n.success(sender, "record.restored", name, iteration);
            }
        }
        else
        {
            Blockbuster.l10n.error(sender, "record.not_exist_iteration", name, iteration);
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 2)
        {
            List<String> iterations = RecordUtils.getReplayIterations(args[0]);

            if (!iterations.isEmpty())
            {
                return getListOfStringsMatchingLastWord(args, iterations);
            }
        }

        return super.getTabCompletions(server, sender, args, pos);
    }
}