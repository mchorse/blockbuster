package mchorse.blockbuster.commands.record;

import java.util.List;

import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Command /record remove
 *
 * This command is responsible for removing action(s) from given player
 * recording.
 */
public class SubCommandRecordRemove extends SubCommandRecordBase
{
    @Override
    public int getRequiredArgs()
    {
        return 2;
    }

    @Override
    public String getName()
    {
        return "remove";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.remove";
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        String filename = args[0];
        int tick = CommandBase.parseInt(args[1], 0);
        Record record = CommandRecord.getRecord(filename);

        if (tick < 0 || tick >= record.actions.size())
        {
            throw new CommandException("record.tick_out_range", tick, record.actions.size() - 1);
        }

        this.removeActions(args, record, tick);
    }

    /**
     * Remove action(s) from given record at given tick
     */
    private void removeActions(String[] args, Record record, int tick) throws CommandException
    {
        if (args.length > 2)
        {
            int index = CommandBase.parseInt(args[2]);
            List<Action> actions = record.actions.get(tick);

            if (actions == null)
            {
                throw new CommandException("record.already_empty", args[1], args[0]);
            }

            if (index < 0 && index >= actions.size())
            {
                throw new CommandException("record.index_out_range", index, actions.size() - 1);
            }

            /* Remove action at given tick */
            if (actions.size() <= 1)
            {
                record.actions.set(tick, null);
            }
            else
            {
                actions.remove(index);
            }
        }
        else
        {
            /* Remove all actions at tick */
            record.actions.set(tick, null);
        }

        record.dirty = true;
    }
}