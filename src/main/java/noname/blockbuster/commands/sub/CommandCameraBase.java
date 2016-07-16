package noname.blockbuster.commands.sub;

import net.minecraft.command.CommandBase;
import noname.blockbuster.commands.CommandCamera;

public abstract class CommandCameraBase extends CommandBase
{
    protected CommandCamera parent;

    public CommandCameraBase(CommandCamera parent)
    {
        this.parent = parent;
    }
}
