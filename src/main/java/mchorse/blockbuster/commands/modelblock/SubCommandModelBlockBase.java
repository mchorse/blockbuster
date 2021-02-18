package mchorse.blockbuster.commands.modelblock;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.mclib.commands.McCommandBase;
import mchorse.mclib.commands.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public abstract class SubCommandModelBlockBase extends McCommandBase
{
    @Override
    public L10n getL10n()
    {
        return Blockbuster.l10n;
    }

    @Override
    public int getRequiredArgs()
    {
        return 3;
    }

    public TileEntityModel getModelBlock(ICommandSender sender, String[] args) throws CommandException
    {
        int x = CommandBase.parseInt(args[0]);
        int y = CommandBase.parseInt(args[1]);
        int z = CommandBase.parseInt(args[2]);

        TileEntity tile = sender.getEntityWorld().getTileEntity(new BlockPos(x, y, z));

        if (tile instanceof TileEntityModel)
        {
            return (TileEntityModel) tile;
        }

        throw new CommandException("modelblock.missing", x, y, z);
    }
}