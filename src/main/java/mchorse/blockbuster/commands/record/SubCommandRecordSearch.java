package mchorse.blockbuster.commands.record;

import java.util.List;

import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class SubCommandRecordSearch extends SubCommandRecordBase
{
    @Override
    public int getRequiredArgs()
    {
        return 2;
    }

    @Override
    public String getName()
    {
        return "search";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.search";
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (!Action.TYPES.containsKey(args[1]))
        {
            throw new CommandException("record.wrong_action", args[1]);
        }

        String filename = args[0];
        byte type = Action.TYPES.get(args[1]).byteValue();
        Record record = CommandRecord.getRecord(filename);

        int i = 0;
        int tick = -1;

        int limit = record.actions.size() + 1;
        boolean outputData = args.length >= 4 ? CommandBase.parseBoolean(args[3]) : false;

        if (args.length >= 3)
        {
            int temp = CommandBase.parseInt(args[2], -1);

            if (temp >= 0)
            {
                limit = temp;
            }
        }

        L10n.info(sender, "record.search_type", args[1]);

        for (List<Action> actions : record.actions)
        {
            tick++;

            if (actions == null)
            {
                continue;
            }

            if (i >= limit)
            {
                break;
            }

            int j = -1;

            for (Action action : actions)
            {
                j++;

                if (action.getType() != type)
                {
                    continue;
                }

                if (outputData)
                {
                    NBTTagCompound tag = new NBTTagCompound();
                    action.toNBT(tag);

                    L10n.info(sender, "record.search_action_data", tick, j, tag.toString());
                }
                else
                {
                    L10n.info(sender, "record.search_action", tick, j);
                }
            }

            i++;
        }

    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, Action.TYPES.keySet());
        }

        return super.getTabCompletions(server, sender, args, pos);
    }
}