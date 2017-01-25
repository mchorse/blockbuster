package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.commands.McCommandBase;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.server.MinecraftServer;

/**
 * Command /record set
 *
 * This command is responsible for replacing action in given player record
 * at given tick with given data.
 */
public class SubCommandRecordSet extends McCommandBase
{
    @Override
    public int getRequiredArgs()
    {
        return 3;
    }

    @Override
    public String getCommandName()
    {
        return "set";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.set";
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        String filename = args[0];
        int tick = CommandBase.parseInt(args[1], 0);
        Record record = CommandRecord.getRecord(filename);

        if (tick <= 0 || tick >= record.actions.size())
        {
            throw new CommandException("record.tick_out_range", tick);
        }

        if (!Action.TYPES.containsKey(args[2]) && !args[2].equals("none"))
        {
            throw new CommandException("record.wrong_action", args[2]);
        }

        if (args[2].equals("none"))
        {
            record.actions.set(tick, null);
            record.dirty = true;

            return;
        }

        try
        {
            Action action = Action.fromType(Action.TYPES.get(args[2]).byteValue());

            if (args.length > 3)
            {
                action.fromNBT(JsonToNBT.getTagFromJson(args[3]));
            }

            record.actions.set(tick, action);
            record.dirty = true;
        }
        catch (Exception e)
        {
            /* This shouldn't */
            e.printStackTrace();
        }
    }
}