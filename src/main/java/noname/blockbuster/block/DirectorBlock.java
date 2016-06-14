package noname.blockbuster.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noname.blockbuster.Blockbuster;
import noname.blockbuster.item.PlaybackItem;
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

    /**
     * Power west side of the block while block is playing and power east side
     * of the block while isn't playback actors.
     */
    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return (this.isPlaying && side == EnumFacing.WEST) || (!this.isPlaying && side == EnumFacing.EAST) ? 15 : 0;
    }

    /* Player interaction */

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }

        ItemStack item = playerIn.getHeldItemMainhand();

        if (this.handleRegisterItem(item, worldIn, pos, playerIn) || this.handlePlaybackItem(item, pos, playerIn))
        {
            return true;
        }

        this.outputCast(playerIn, worldIn, pos);

        return true;
    }

    /**
     * Output to chat actors and cameras
     *
     * Temporary solution for browsing registered entities by DirectorTileEntity.
     * Creating GUI for this job is on ToDo list.
     */
    private void outputCast(EntityPlayer playerIn, World worldIn, BlockPos pos)
    {
        DirectorTileEntity tile = (DirectorTileEntity) worldIn.getTileEntity(pos);
        String output = I18n.format("blockbuster.director.cast") + "\n";

        List<String> cast = new ArrayList<String>();
        cast.addAll(tile.actors);
        cast.addAll(tile.cameras);

        for (String id : cast)
        {
            Entity entity = Mocap.entityByUUID(worldIn, id);
            String name = entity != null ? entity.getName() : I18n.format("blockbuster.director.missing_cast", id);

            output += "* " + name + "\n";
        }

        if (cast.isEmpty())
        {
            output = I18n.format("blockbuster.director.no_cast");
        }

        playerIn.addChatComponentMessage(new TextComponentString(output.trim()));
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
     * Attach recording item to current director block
     */
    private boolean handlePlaybackItem(ItemStack item, BlockPos pos, EntityPlayer player)
    {
        if (!(item.getItem() instanceof PlaybackItem))
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

        player.addChatMessage(new TextComponentTranslation("blockbuster.director.attached_device"));
        return true;
    }

    /* ITileEntityProvider implementation */

    /**
     * Create tile entity
     */
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new DirectorTileEntity();
    }
}