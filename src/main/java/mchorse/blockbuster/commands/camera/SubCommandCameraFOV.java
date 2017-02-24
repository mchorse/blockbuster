package mchorse.blockbuster.commands.camera;

import mchorse.blockbuster.utils.L10n;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Camera's sub-command /camera fov
 *
 * This command is responsible for setting and getting fov of this client.
 */
public class SubCommandCameraFOV extends CommandBase
{
    @Override
    public String getName()
    {
        return "fov";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.fov";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (args.length == 0)
        {
            L10n.info(sender, "camera.fov", mc.gameSettings.fovSetting);
        }
        else
        {
            mc.gameSettings.fovSetting = (float) CommandBase.parseDouble(args[0]);
        }
    }
}