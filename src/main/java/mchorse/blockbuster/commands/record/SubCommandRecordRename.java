package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class SubCommandRecordRename extends SubCommandRecordBase
{
	@Override
	public int getRequiredArgs()
	{
		return 2;
	}

	@Override
	public String getName()
	{
		return "rename";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "blockbuster.commands.record.rename";
	}

	@Override
	public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (RecordUtils.isReplayExists(args[1]))
		{
			throw new CommandException("record.already_exists", args[1]);
		}

		try
		{
			Record record = CommandRecord.getRecord(args[0]).clone();

			record.filename = args[1];
			record.save(RecordUtils.replayFile(record.filename));

			CommonProxy.manager.rename(args[0], record);

			L10n.success(sender, "record.renamed", args[0], args[1]);
		}
		catch (Exception e)
		{
			L10n.error(sender, "record.couldnt_save", args[1]);
		}
	}
}