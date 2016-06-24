package noname.blockbuster.item;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noname.blockbuster.Blockbuster;
import noname.blockbuster.entity.EntityCamera;

/**
 * Camera item
 *
 * Spawns a rideable camera entity (for controlling the camera)
 */
public class ItemCamera extends Item
{
    public ItemCamera()
    {
        this.setMaxStackSize(1);
        this.setRegistryName("camera");
        this.setUnlocalizedName("blockbuster.camera");
        this.setCreativeTab(Blockbuster.blockbusterTab);
    }

    /**
     * Some of the code is lookup/copied from the ItemMonsterPlacer (which is a
     * bad name, because it can spawn not only monsters, but also passive
     * entities, so basically it's item spawn egg).
     */
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            EntityLiving camera = new EntityCamera(worldIn);

            pos = pos.offset(facing);

            camera.setLocationAndAngles(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, playerIn.rotationYaw, playerIn.rotationPitch);
            camera.rotationYawHead = camera.rotationYaw;
            camera.renderYawOffset = camera.rotationYaw;

            worldIn.spawnEntityInWorld(camera);
        }

        return EnumActionResult.SUCCESS;
    }
}