package noname.blockbuster.commands.camera;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import noname.blockbuster.camera.Angle;
import noname.blockbuster.camera.CameraProfile;
import noname.blockbuster.camera.Point;
import noname.blockbuster.camera.Position;
import noname.blockbuster.commands.CommandCamera;

/**
 * Camera's sub-command /camera goto
 *
 * Teleports player to specific camera fixture with specified progress.
 */
public class SubCommandCameraGoto extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "goto";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.goto";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        EntityPlayer player = (EntityPlayer) sender;
        CameraProfile profile = CommandCamera.getProfile();
        Position pos = new Position(player);

        int index = CommandBase.parseInt(args[0]);
        float progress = 0;

        if (args.length > 1)
        {
            progress = (float) CommandBase.parseDouble(args[1], 0, 1);
        }

        if (!profile.has(index))
        {
            throw new CommandException("blockbuster.profile.not_exists", index);
        }

        Point point = pos.point;
        Angle angle = pos.angle;

        profile.get(index).applyFixture(progress, pos);
        player.setPositionAndRotation(point.x, point.y, point.z, angle.yaw, angle.pitch);
    }
}
