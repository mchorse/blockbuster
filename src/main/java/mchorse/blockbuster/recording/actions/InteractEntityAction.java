package mchorse.blockbuster.recording.actions;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.utils.EntityUtils;
import mchorse.mclib.utils.RayTracing;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

public class InteractEntityAction extends ItemUseAction
{
	public InteractEntityAction()
	{}

	public InteractEntityAction(EnumHand hand)
	{
		super(hand);
	}

	@Override
	public void apply(EntityLivingBase actor)
	{
		Frame frame = EntityUtils.getRecordPlayer(actor).getCurrentFrame();
		EntityPlayer player = actor instanceof EntityPlayer ? (EntityPlayer) actor : ((EntityActor) actor).fakePlayer;

		if (frame == null) return;

		float yaw = actor.rotationYaw;
		float pitch = actor.rotationPitch;
		float yawHead = actor.rotationYawHead;

		actor.rotationYaw = frame.yaw;
		actor.rotationPitch = frame.pitch;
		actor.rotationYawHead = frame.yawHead;

		Entity target = RayTracing.getTargetEntity(actor, 5.0);

		actor.rotationYaw = yaw;
		actor.rotationPitch = pitch;
		actor.rotationYawHead = yawHead;

		if (player != actor)
		{
			this.copyActor(actor, player, frame);
		}

		if (target != null)
		{
			player.interactOn(target, this.hand);
		}
	}
}