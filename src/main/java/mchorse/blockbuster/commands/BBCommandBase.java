package mchorse.blockbuster.commands;

import mchorse.blockbuster.Blockbuster;
import mchorse.mclib.commands.McCommandBase;
import mchorse.mclib.commands.utils.L10n;

public abstract class BBCommandBase extends McCommandBase
{
    @Override
    public L10n getL10n()
    {
        return Blockbuster.l10n;
    }
}
