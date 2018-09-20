package mchorse.blockbuster.commands.record;

import java.util.List;

import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.ActionRegistry;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.data.Record;
import mchorse.metamorph.commands.CommandMorph;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

/**
 * Command /record add
 *
 * This command is responsible for adding a desired action to the given player
 * recording.
 */
public class SubCommandRecordAdd extends SubCommandRecordBase
{
    @Override
    public int getRequiredArgs()
    {
        return 3;
    }

    @Override
    public String getName()
    {
        return "add";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.add";
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

        if (!ActionRegistry.NAME_TO_CLASS.containsKey(args[2]))
        {
            throw new CommandException("record.wrong_action", args[2]);
        }

        try
        {
            Action action = ActionRegistry.fromName(args[2]);

            if (args.length > 3)
            {
                action.fromNBT(JsonToNBT.getTagFromJson(CommandMorph.mergeArgs(args, 3)));
            }

            record.addAction(tick, action);
            record.dirty = true;
        }
        catch (Exception e)
        {
            throw new CommandException("record.add", args[2], e.getMessage());
        }
    }

    /**
     * Tab complete action
     */
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 3)
        {
            return getListOfStringsMatchingLastWord(args, ActionRegistry.NAME_TO_ID.keySet());
        }

        return super.getTabCompletions(server, sender, args, pos);
    }
}