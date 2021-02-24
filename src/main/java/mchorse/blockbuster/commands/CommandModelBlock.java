package mchorse.blockbuster.commands;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.commands.modelblock.SubCommandModelBlockMorph;
import mchorse.blockbuster.commands.modelblock.SubCommandModelBlockProperty;
import mchorse.mclib.commands.SubCommandBase;
import mchorse.mclib.commands.utils.L10n;
import net.minecraft.command.ICommandSender;

public class CommandModelBlock extends SubCommandBase
{
    public CommandModelBlock()
    {
        this.add(new SubCommandModelBlockMorph());
        this.add(new SubCommandModelBlockProperty());
    }

    @Override
    public L10n getL10n()
    {
        return Blockbuster.l10n;
    }

    @Override
    public String getName()
    {
        return "modelblock";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.modelblock.help";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }
}