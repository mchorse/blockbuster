package mchorse.blockbuster.common.block;

import java.util.List;

import mchorse.blockbuster.common.item.ItemRegister;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.PacketDirectorCast;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Descendant of abstract director block
 *
 * This block is the one that responsible for machinimas creation.
 */
public class BlockDirector extends AbstractBlockDirector
{
    public BlockDirector()
    {
        super();
        this.setRegistryName("director");
        this.setUnlocalizedName("blockbuster.director");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        tooltip.add(I18n.format("blockbuster.info.director_block"));
    }

    @Override
    protected boolean handleItem(ItemStack item, World world, BlockPos pos, EntityPlayer player)
    {
        return this.handlePlaybackItem(item, world, pos, player) || this.handleRegisterItem(item, world, pos, player);
    }

    private boolean handleRegisterItem(ItemStack item, World world, BlockPos pos, EntityPlayer player)
    {
        if (!(item.getItem() instanceof ItemRegister))
        {
            return false;
        }

        if (world.isRemote)
        {
            return true;
        }

        ((ItemRegister) item.getItem()).registerStack(item, pos);
        L10n.success(player, "director.attached_device");

        return true;
    }

    @Override
    protected void displayCast(EntityPlayer player, World worldIn, BlockPos pos)
    {
        TileEntityDirector tile = (TileEntityDirector) worldIn.getTileEntity(pos);
        Dispatcher.sendTo(new PacketDirectorCast(tile.getPos(), tile.replays), (EntityPlayerMP) player);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityDirector();
    }
}