package noname.blockbuster.block;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noname.blockbuster.Blockbuster;
import noname.blockbuster.item.RecordItem;
import noname.blockbuster.item.RegisterItem;
import noname.blockbuster.recording.Mocap;
import noname.blockbuster.tileentity.DirectorTileEntity;

/**
 * <p>
 * This block is responsible for launching and stopping scene recording/play.
 * It's basically the block that ties everything together.
 * </p>
 *
 * <p>
 * It also has two hooks:
 * </p>
 *
 * <ul>
 * <li>Start hook – director block sends redstone signal on east side when the
 * scene starts playing</li>
 * <li>Stop hook – director block sends redstone signal on west side when the
 * scene stops playing</li>
 * </ul>
 *
 * <p>
 * Stop hook is very useful when you need to reset the scene, like you want to
 * use TNT in your scene, but don't want to rebuild the same house over and over
 * again, you can use this redstone hook to /clone already built environment
 * after the scene was stopped.
 * </p>
 *
 * <p>
 * I don't really know a good use for the start hook, maybe start playing one of
 * these sick minecraft crafted tunes or do something else. I added just to
 * complement the stop hook.
 * </p>
 */
public class DirectorBlock extends Block implements ITileEntityProvider
{
    public boolean isPlaying = false;

    public DirectorBlock()
    {
        super(Material.rock);
        this.setCreativeTab(Blockbuster.blockbusterTab);
        this.setRegistryName("directorBlock");
        this.setUnlocalizedName("directorBlock");
    }

    /* Redstone */

    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean canProvidePower(IBlockState state)
    {
        return true;
    }

    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return (this.isPlaying && side == EnumFacing.WEST) || (!this.isPlaying && side == EnumFacing.EAST) ? 15 : 0;
    }

    /* Player interaction */

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            ItemStack item = playerIn.getHeldItemMainhand();

            if (item != null)
            {
                if (this.handleRegisterItem(item, worldIn, pos, playerIn) || this.handleRecordItem(item, pos, playerIn))
                {
                    return true;
                }
            }

            DirectorTileEntity tile = (DirectorTileEntity) worldIn.getTileEntity(pos);
            String actors = "Registered actors: \n";

            for (String id : tile.actors)
            {
                Entity entity = Mocap.entityByUUID(worldIn, UUID.fromString(id));

                if (entity != null)
                {
                    actors += entity.getName() + "\n";
                }
                else
                {
                    actors += "Missing actor with UUID: " + id + "\n";
                }
            }

            playerIn.addChatComponentMessage(new TextComponentString(actors.trim()));
        }

        return true;
    }

    /**
     * Attach actor to director block
     */
    private boolean handleRegisterItem(ItemStack item, World world, BlockPos pos, EntityPlayer player)
    {
        if (!(item.getItem() instanceof RegisterItem))
        {
            return false;
        }

        if (item.getTagCompound() == null)
        {
            return false;
        }

        String id = item.getTagCompound().getString("ActorID");
        DirectorTileEntity tile = (DirectorTileEntity) world.getTileEntity(pos);

        if (!tile.addActor(id))
        {
            player.addChatMessage(new TextComponentString("This actor is already registered by director block!"));
            return false;
        }

        player.addChatMessage(new TextComponentString("This actor was succesfully attached to director block!"));
        return true;
    }

    /**
     * Attach recording item to current director block
     */
    private boolean handleRecordItem(ItemStack item, BlockPos pos, EntityPlayer player)
    {
        if (!(item.getItem() instanceof RecordItem))
        {
            return false;
        }

        NBTTagCompound tag = item.getTagCompound();

        if (tag == null)
        {
            item.setTagCompound(tag = new NBTTagCompound());
        }

        tag.setInteger("DirX", pos.getX());
        tag.setInteger("DirY", pos.getY());
        tag.setInteger("DirZ", pos.getZ());

        player.addChatMessage(new TextComponentString("This recording device was succesfully attached to director block!"));
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
