package mchorse.blockbuster.commands;

import mchorse.blockbuster.commands.modelblock.SubCommandModelBlockMorph;
import mchorse.blockbuster.commands.modelblock.SubCommandModelBlockProperty;
import mchorse.mclib.commands.SubCommandBase;

public class CommandModelBlock extends SubCommandBase
{
    public CommandModelBlock()
    {
        this.add(new SubCommandModelBlockMorph());
        this.add(new SubCommandModelBlockProperty());
    }

    @Override
    public String getName()
    {
        return "modelblock";
    }

    @Override
    protected String getHelp()
    {
        return "blockbuster.commands.modelblock.help";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }
}