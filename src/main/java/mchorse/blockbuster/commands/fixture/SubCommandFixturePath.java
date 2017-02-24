package mchorse.blockbuster.commands.fixture;

import mchorse.blockbuster.commands.SubCommandBase;
import mchorse.blockbuster.commands.path.SubCommandPathAdd;
import mchorse.blockbuster.commands.path.SubCommandPathEdit;
import mchorse.blockbuster.commands.path.SubCommandPathGoto;
import mchorse.blockbuster.commands.path.SubCommandPathMove;
import mchorse.blockbuster.commands.path.SubCommandPathRemove;

/**
 * Camera's sub-command /camera path
 *
 * This sub-command is responsible for manipulating path fixtures. This
 * sub-command provides sub-commands for adding, editing, removing, moving and
 * going to points in the path fixture.
 */
public class SubCommandFixturePath extends SubCommandBase
{
    public SubCommandFixturePath()
    {
        this.add(new SubCommandPathAdd());
        this.add(new SubCommandPathEdit());
        this.add(new SubCommandPathGoto());
        this.add(new SubCommandPathMove());
        this.add(new SubCommandPathRemove());
    }

    @Override
    public String getName()
    {
        return "path";
    }

    @Override
    protected String getHelp()
    {
        return "blockbuster.commands.camera.path.help";
    }
}