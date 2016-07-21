package noname.blockbuster.block;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.director.PacketDirectorMapCast;
import noname.blockbuster.tileentity.AbstractTileEntityDirector;
import noname.blockbuster.tileentity.TileEntityDirectorMap;

/**
 * Descendant of abstract director block
 *
 * This block is the one that responsible for adventure maps scenes.
 */
public class BlockDirectorMap extends AbstractBlockDirector
{
    public BlockDirectorMap()
    {
        super();
        this.setRegistryName("director_map");
        this.setUnlocalizedName("blockbuster.director_map");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        tooltip.add(I18n.format("blockbuster.info.director_map"));
    }

    @Override
    protected void displayCast(EntityPlayer player, World worldIn, BlockPos pos)
    {
        AbstractTileEntityDirector tile = (AbstractTileEntityDirector) worldIn.getTileEntity(pos);

        Dispatcher.sendTo(new PacketDirectorMapCast(tile.getCast(), tile.getPos()), (EntityPlayerMP) player);
    }

    /**
     * Create tile entity
     */
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityDirectorMap();
    }
}
