package mchorse.blockbuster.commands.record;

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

public class SubCommandRecordGet extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "get";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.get";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        String filename = args[0];
        int tick = CommandBase.parseInt(args[1], 0);

        Record record = CommonProxy.manager.getRecord(filename);

        if (record == null)
        {
            L10n.error(sender, "record.not_exist", filename);
            return;
        }

        if (tick <= 0 || tick >= record.actions.size())
        {
            L10n.error(sender, "record.tick_out_range", tick);
            return;
        }

        NBTTagCompound tag = new NBTTagCompound();
        Action action = record.actions.get(tick);

        if (action == null)
        {
            L10n.error(sender, "record.no_action", tick);
            return;
        }

        action.toNBT(tag);
        tag.setByte("Type", action.getType());

        L10n.info(sender, "record.action", tick, tag.toString());
    }
}