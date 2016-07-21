package noname.blockbuster.block;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import noname.blockbuster.item.ItemRegister;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.director.PacketDirectorCast;
import noname.blockbuster.recording.Mocap;
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
        return this.handleRegisterItem(item, world, pos, player) || this.handlePlaybackItem(item, pos, player);
    }

    /**
     * Attach an entity (actor) to director block
     */
    private boolean handleRegisterItem(ItemStack item, World world, BlockPos pos, EntityPlayer player)
    {
        if (!(item.getItem() instanceof ItemRegister) && item.getTagCompound() == null)
        {
            return false;
        }

        TileEntityDirector tile = (TileEntityDirector) world.getTileEntity(pos);

        NBTTagCompound tag = item.getTagCompound();

        if (tag == null || !tag.hasKey("EntityID"))
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