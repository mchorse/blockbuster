package mchorse.blockbuster.recording.actions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class CloseContainerAction extends Action
{
    @Override
    public void apply(EntityLivingBase actor)
    {
        if (actor instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) actor;
            if (player.openContainer != null)
                player.closeScreen();
        }
    }
    
    @Override
    public boolean isSafe()
    {
        return true;
    }
}
