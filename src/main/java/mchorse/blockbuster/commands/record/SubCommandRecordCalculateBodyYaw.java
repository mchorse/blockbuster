package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.actions.SwipeAction;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class SubCommandRecordCalculateBodyYaw extends SubCommandRecordBase
{
	@Override
	public int getRequiredArgs()
	{
		return 1;
	}

	@Override
	public String getName()
	{
		return "calculate_body_yaw";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "blockbuster.commands.record.calculate_body_yaw";
	}

	@Override
	public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		String filename = args[0];
		Record record = CommandRecord.getRecord(filename);
		Frame prev = null;
		float renderYawOffset = 0;
		int swingProgress = 0;

		for (int i = 0, c = record.frames.size(); i < c; i++)
		{
			Frame frame = record.frames.get(i);
			List<Action> actions = record.getActions(i);

			if (actions != null)
			{
				for (Action action : actions)
				{
					if (action instanceof SwipeAction)
					{
						swingProgress = 6;
					}
				}
			}

			if (prev == null)
			{
				prev = frame;
				renderYawOffset = prev.yaw;
			}

			double dx = frame.x - prev.x;
			double dz = frame.z - prev.z;
			float distSq = (float)(dx * dx + dz * dz);
			float tempRenderyawOffset = renderYawOffset;

			if (distSq > 0.0025000002F)
			{
				float f1 = (float) MathHelper.atan2(dz, dx) * (180F / (float)Math.PI) - 90.0F;
				float f2 = MathHelper.abs(MathHelper.wrapDegrees(frame.yaw) - f1);

				if (95.0F < f2 && f2 < 265.0F)
				{
					tempRenderyawOffset = f1 - 180.0F;
				}
				else
				{
					tempRenderyawOffset = f1;
				}
			}

			if (swingProgress > 0)
			{
				renderYawOffset = frame.yaw;
			}

			float coolBob = MathHelper.wrapDegrees(tempRenderyawOffset - renderYawOffset);
			renderYawOffset += coolBob * 0.3F;
			float anotherCoolBob = MathHelper.wrapDegrees(frame.yaw - renderYawOffset);

			if (anotherCoolBob < -75.0F)
			{
				anotherCoolBob = -75.0F;
			}

			if (anotherCoolBob >= 75.0F)
			{
				anotherCoolBob = 75.0F;
			}

			renderYawOffset = frame.yaw - anotherCoolBob;

			if (anotherCoolBob * anotherCoolBob > 2500.0F)
			{
				renderYawOffset += anotherCoolBob * 0.2F;
			}

			frame.hasBodyYaw = true;
			frame.bodyYaw = renderYawOffset;

			prev = frame;
			swingProgress--;
		}

		record.dirty = true;

		RecordUtils.unloadRecord(record);
		L10n.success(sender, "record.calculate_body_yaw", filename);
	}
}
