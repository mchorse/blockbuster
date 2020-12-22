package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.List;

public class SubCommandRecordFlip extends SubCommandRecordBase
{
	public static List<String> ALLOWED_AXES = Arrays.asList("x", "z");

	@Override
	public int getRequiredArgs()
	{
		return 3;
	}

	@Override
	public String getName()
	{
		return "flip";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "blockbuster.commands.record.flip";
	}

	@Override
	public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		Record record = CommandRecord.getRecord(args[0]);
		String axis = args[1].toLowerCase();
		boolean center = args.length < 4 ? true : CommandBase.parseBoolean(args[3]);
		double coordinate = CommandBase.parseInt(args[2]) + (center ? 0.5 : 0);

		if (!ALLOWED_AXES.contains(axis))
		{
			L10n.error(sender, "record.wrong_axis", args[1]);

			return;
		}

		for (Frame frame : record.frames)
		{
			if (axis.equals("x"))
			{
				double diff = coordinate - frame.x;

				frame.x = coordinate + diff;
				frame.yaw *= -1;
				frame.yawHead *= -1;
				frame.mountYaw *= -1;
			}
			else
			{
				double diff = coordinate - frame.z;

				frame.z = coordinate + diff;
				frame.yaw = -frame.yaw + 180;
				frame.yawHead = -frame.yawHead + 180;
				frame.mountYaw = -frame.mountYaw + 180;
			}
		}

		for (List<Action> actions : record.actions)
		{
			if (actions == null)
			{
				continue;
			}

			for (Action action : actions)
			{
				action.flip(axis, center ? Math.floor(coordinate) : coordinate - 0.5);
			}
		}

		try
		{
			RecordUtils.saveRecord(record);

			L10n.success(sender, "record.flipped", args[0], args[1], args[2]);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			L10n.error(sender, "record.couldnt_save", args[1]);
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
	{
		if (args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, ALLOWED_AXES);
		}
		else if (args.length == 3 && ALLOWED_AXES.contains(args[1]))
		{
			Vec3d vec3d = sender.getPositionVector();
			int coordinate = (int) Math.floor(args[1].equals("x") ? vec3d.x : vec3d.z);

			return getListOfStringsMatchingLastWord(args, Arrays.asList(coordinate));
		}
		else if (args.length == 4)
		{
			return getListOfStringsMatchingLastWord(args, new String[] {"true", "false"});
		}

		return super.getTabCompletions(server, sender, args, pos);
	}
}