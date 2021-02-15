package mchorse.blockbuster.commands.modelblock;

import mchorse.blockbuster.commands.SubCommandBase;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModifyModelBlock;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class SubCommandModelBlockMorph extends SubCommandModelBlockBase
{
    @Override
    public String getName()
    {
        return "morph";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.modelblock.morph";
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        TileEntityModel model = this.getModelBlock(sender, args);
        String morphData = args.length >= 4 ? String.join(" ", SubCommandBase.dropFirstArguments(args, 3)) : null;
        AbstractMorph morph = null;

        if (morphData != null)
        {
            try
            {
                morph = MorphManager.INSTANCE.morphFromNBT(JsonToNBT.getTagFromJson(morphData));
            }
            catch (Exception e)
            {}
        }

        model.setMorph(morph);

        int x = model.getPos().getX();
        int y = model.getPos().getY();
        int z = model.getPos().getZ();

        PacketModifyModelBlock message = new PacketModifyModelBlock(model.getPos(), model, true);
        Dispatcher.DISPATCHER.get().sendToAllAround(message, new NetworkRegistry.TargetPoint(sender.getEntityWorld().provider.getDimension(), x, y, z, 64));
    }
}