package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Command /record reverse
 *
 * This command is responsible for reversing all frames and actions in
 * the player recording
 */
public class SubCommandRecordReverse extends SubCommandRecordBase
{
	@Override
	public int getRequiredArgs()
	{
		return 1;
	}

	@Override
	public String getName()
	{
		return "reverse";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "blockbuster.commands.record.reverse";
	}

	@Override
	public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		String filename = args[0];

		Record record = CommandRecord.getRecord(filename);

		record.reverse();
		record.dirty = true;

		RecordUtils.unloadRecord(record);
		L10n.success(sender, "record.reversed", args[0]);
	}
}