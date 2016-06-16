package noname.blockbuster.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import noname.blockbuster.item.RegisterItem;
import noname.blockbuster.recording.Mocap;
import noname.blockbuster.tileentity.DirectorTileEntity;

public class DirectorBlock extends AbstractDirectorBlock
{
    public DirectorBlock()
    {
        super();
        this.setRegistryName("directorBlock");
        this.setUnlocalizedName("directorBlock");
    }

    @Override
    protected boolean handleItem(ItemStack item, World world, BlockPos pos, EntityPlayer player)
    {
        return this.handleRegisterItem(item, world, pos, player) || this.handlePlaybackItem(item, pos, player);
    }

    /**
     * Attach an entity (actor or camera) to director block
     */
    private boolean handleRegisterItem(ItemStack item, World world, BlockPos pos, EntityPlayer player)
    {
        if (!(item.getItem() instanceof RegisterItem) && item.getTagCompound() == null)
        {
            return false;
        }

        DirectorTileEntity tile = (DirectorTileEntity) world.getTileEntity(pos);
        NBTTagCompound tag = item.getTagCompound();

        if (!tag.hasKey("EntityID"))
        {
            return false;
        }

        String id = tag.getString("EntityID");
        Entity entity = Mocap.entityByUUID(world, id);

        if (entity == null)
        {
            player.addChatMessage(new TextComponentTranslation("blockbuster.director.not_exist"));
            return true;
        }

        if (!tile.add(entity))
        {
            player.addChatMessage(new TextComponentTranslation("blockbuster.director.already_registered"));
            return true;
        }

        player.addChatMessage(new TextComponentTranslation("blockbuster.director.was_registered"));
        return true;
    }

    /**
     * Create tile entity
     */
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new DirectorTileEntity();
    }
}