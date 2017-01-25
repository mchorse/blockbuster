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
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.server.MinecraftServer;

/**
 * Command /record set
 *
 * This command is responsible for replacing action in given player record
 * at given tick with given data.
 */
public class SubCommandRecordSet extends CommandBase
{
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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 3)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        String filename = args[0];
        int tick = CommandBase.parseInt(args[1], 0);

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

        if (tick <= 0 || tick >= record.actions.size())
        {
            L10n.error(sender, "record.tick_out_range", tick);
            return;
        }

        if (!Action.TYPES.containsKey(args[2]))
        {
            L10n.error(sender, "record.wrong_action", args[2]);
            return;
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