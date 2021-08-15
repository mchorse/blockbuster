package mchorse.blockbuster.recording.actions;

import mchorse.blockbuster.common.entity.EntityActor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class CloseContainerAction extends Action
{
    @Override
    public void apply(EntityLivingBase actor)
    {
        EntityPlayer player = actor instanceof EntityActor ? ((EntityActor) actor).fakePlayer : (EntityPlayer) actor;

        if (!player.world.isRemote && player.openContainer != player.inventoryContainer)
        {
            player.closeScreen();
        }
    }
}
