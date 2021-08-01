package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;

/**
 * Command /record create
 *
 * This command is responsible for generating an empty player recording based on
 * current player's position or arbitrary position and rotation
 */
public class SubCommandRecordCreate extends SubCommandRecordBase
{
    @Override
    public String getName()
    {
        return "create";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.create";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}record {8}create{r} {7}<filename> <duration> [x] [y] [z] [yaw] [pitch]{r}";
    }

    @Override
    public int getRequiredArgs()
    {
        return 2;
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        String filename = args[0];

        try
        {
            Vec3d pos = sender.getPositionVector();
            EntityPlayer player = sender instanceof EntityPlayer ? (EntityPlayer) sender : null;

            int duration = CommandBase.parseInt(args[1], 1, 100000);
            float yaw = player == null ? 0 : player.rotationYaw;
            float pitch = player == null ? 0 : player.rotationPitch;

            double x = args.length >= 3 ? CommandBase.parseDouble(pos.x, args[2], false) : pos.x;
            double y = args.length >= 4 ? CommandBase.parseDouble(pos.y, args[3], false) : pos.y;
            double z = args.length >= 5 ? CommandBase.parseDouble(pos.z, args[4], false) : pos.z;
            yaw = args.length >= 6 ? (float) CommandBase.parseDouble(yaw, args[5], false) : yaw;
            pitch = args.length >= 7 ? (float) CommandBase.parseDouble(pitch, args[6], false) : pitch;

            Record record = this.generateProfile(player, filename, duration, x, y, z, yaw, pitch);

            CommonProxy.manager.records.put(filename, record);
            RecordUtils.saveRecord(record);

            Blockbuster.l10n.success(sender, "record.create", filename, duration);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Blockbuster.l10n.error(sender, "record.couldnt_save", filename);
        }
    }

    private Record generateProfile(EntityPlayer player, String filename, int duration, double x, double y, double z, float yaw, float pitch)
    {
        Record record = new Record(filename);
        Frame original = new Frame();

        if (player != null)
        {
            original.fromPlayer(player);
        }

        original.x = x;
        original.y = y;
        original.z = z;
        original.yaw = original.yawHead = original.bodyYaw = yaw;
        original.pitch = pitch;
        original.hasBodyYaw = true;

        for (int i = 0; i <= duration; i++)
        {
            record.frames.add(original.copy());
            record.actions.add(null);
        }

        return record;
    }
}