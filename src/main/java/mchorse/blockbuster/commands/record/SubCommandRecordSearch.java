package mchorse.blockbuster.commands.record;

import java.io.FileNotFoundException;

import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class SubCommandRecordSearch extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "search";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.search";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        String filename = args[0];
        byte type = Action.TYPES.get(args[1]).byteValue();

        Record record;

        try
        {
            record = CommonProxy.manager.getRecord(filename);
        }
        catch (FileNotFoundException e)
        {
            L10n.error(sender, "record.not_exist", filename);
            return;
        }
        catch (Exception e)
        {
            L10n.error(sender, "recording.read", filename);
            return;
        }

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

        for (Action action : record.actions)
        {
            tick++;

            if (action == null || action.getType() != type)
            {
                continue;
            }

            if (i >= limit)
            {
                break;
            }

            if (outputData)
            {
                NBTTagCompound tag = new NBTTagCompound();
                action.toNBT(tag);

                L10n.info(sender, "record.search_action_data", tick, tag.toString());
            }
            else
            {
                L10n.info(sender, "record.search_action", tick);
            }

            i++;
        }
    }
}