package noname.blockbuster.block;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import noname.blockbuster.item.ItemRegister;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.director.PacketDirectorCast;
import noname.blockbuster.tileentity.TileEntityDirector;

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
        tooltip.add(I18n.format("blockbuster.info.director"));
    }

    @Override
    protected boolean handleItem(ItemStack item, World world, BlockPos pos, EntityPlayer player)
    {
        return this.handlePlaybackItem(item, world, pos, player) || this.handleRegisterItem(item, world, pos, player);
    }

    /**
     * Attach an entity (actor) to director block
     */
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
        player.addChatMessage(new TextComponentTranslation("blockbuster.director.attached_device"));

        return true;
    }

    @Override
    protected void displayCast(EntityPlayer player, World worldIn, BlockPos pos)
    {
        TileEntityDirector tile = (TileEntityDirector) worldIn.getTileEntity(pos);
        Dispatcher.sendTo(new PacketDirectorCast(tile.getPos(), tile.actors), (EntityPlayerMP) player);
    }

    /**
     * Create tile entity
     */
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityDirector();
    }
}