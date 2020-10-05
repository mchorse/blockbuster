package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class SubCommandRecordRemoveBodyYaw extends SubCommandRecordBase
{
	@Override
	public int getRequiredArgs()
	{
		return 1;
	}

	@Override
	public String getName()
	{
		return "remove_body_yaw";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "blockbuster.commands.record.remove_body_yaw";
	}

	@Override
	public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		String filename = args[0];
		Record record = CommandRecord.getRecord(filename);

		for (int i = 0, c = record.frames.size(); i < c; i++)
		{
			Frame frame = record.frames.get(i);

			frame.hasBodyYaw = false;
			frame.bodyYaw = 0F;
		}

		record.dirty = true;

		RecordUtils.unloadRecord(record);
		L10n.success(sender, "record.remove_body_yaw", filename);
	}
}
