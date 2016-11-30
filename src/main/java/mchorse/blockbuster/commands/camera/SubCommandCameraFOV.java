package mchorse.blockbuster.commands.camera;

import mchorse.blockbuster.utils.L10n;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

/**
 * Camera's sub-command /camera fov
 *
 * This command is responsible for setting and getting fov of this client.
 */
public class SubCommandCameraFOV extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "fov";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.fov";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (args.length == 0)
        {
            L10n.info(sender, "camera.fov", mc.gameSettings.fovSetting);
        }
        else
        {
            mc.gameSettings.fovSetting = (float) CommandBase.parseDouble(sender, args[0]);
        }
    }
}