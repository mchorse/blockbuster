package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.L10n;
import mchorse.mclib.utils.Interpolations;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Command /record fade
 *
 * This command is responsible for fading out the player recording to
 * the first frame.
 */
public class SubCommandRecordFade extends SubCommandRecordBase
{
    @Override
    public int getRequiredArgs()
    {
        return 2;
    }

    @Override
    public String getName()
    {
        return "fade";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.fade";
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        Record record = CommandRecord.getRecord(args[0]);
        int length = record.getLength();
        int fadeOut = CommandBase.parseInt(args[1], 1, length);

        if (length == 0)
        {
            L10n.error(sender, "record.empty", record.filename);

            return;
        }

        /* Process */
        Frame first = record.getFrame(0);
        Frame last = record.getFrame(length - fadeOut - 1);

        for (int i = 0; i < fadeOut; i ++)
        {
            Frame current = record.getFrame(length - fadeOut + i);
            float x = i / (float) fadeOut;

            current.x = Interpolations.lerp(last.x, first.x, x);
            current.y = Interpolations.lerp(last.y, first.y, x);
            current.z = Interpolations.lerp(last.z, first.z, x);
            current.yaw = Interpolations.lerpYaw(last.yaw, first.yaw, x);
            current.yawHead = Interpolations.lerpYaw(last.yawHead, first.yawHead, x);
            current.mountYaw = Interpolations.lerp(last.mountYaw, first.mountYaw, x);
            current.pitch = Interpolations.lerp(last.pitch, first.pitch, x);
            current.mountPitch = Interpolations.lerp(last.mountPitch, first.mountPitch, x);

            if (current.hasBodyYaw && first.hasBodyYaw)
            {
                current.bodyYaw = Interpolations.lerpYaw(last.bodyYaw, first.bodyYaw, x);
            }
        }

        try
        {
            RecordUtils.saveRecord(record);

            L10n.success(sender, "record.faded", args[0], args[1]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            L10n.error(sender, "record.couldnt_save", args[1]);
        }
    }
}
