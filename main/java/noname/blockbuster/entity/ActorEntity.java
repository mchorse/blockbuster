package noname.blockbuster.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Actor entity class
 * 
 * Actor entity class is responsible for recording player's actions and 
 * execute them. I'm also thinking about giving them controllable AI settings
 * so they could be used without recording (like during the battles between two or more actors).
 */
public class ActorEntity extends EntityCreature
{
	protected boolean isRecording = false;
	protected double prePosX;
	protected double prePosY;
	protected double prePosZ;
	
	protected boolean isPlaying = false;
	protected List<Vec3d> recording = new ArrayList<Vec3d>();
	protected Entity recordingTarget;
	protected int recordIndex = 0;
	
	public ActorEntity(World worldIn) 
	{
		super(worldIn);
	}
	
	public boolean isRecording()
	{
		return isRecording;
	}
	
	public void setRecording(boolean recording)
	{
		isRecording = recording;
		noClip = recording;
		
		if (recording)
		{
			prePosX = posX;
			prePosY = posY;
			prePosZ = posZ;
			
			this.recording.clear();
		}
		else 
		{
			setPosition(prePosX, prePosY, prePosZ);
		}
	}
	
	@Override
	public void onUpdate() 
	{
		if (isPlaying)
		{
			motionY = 0.0D;
			noClip = true;
			Vec3d pos = recording.get(recordIndex);
			setPosition(pos.xCoord, pos.yCoord, pos.zCoord);
			
			recordIndex ++;
			
			if (recordIndex >= recording.size())
			{
				isPlaying = false;
				setPosition(prePosX, prePosY, prePosZ);
				recordIndex = 0;
				noClip = false;
			}
		}
		
		super.onUpdate();
		
		if (isRecording)
		{
			motionY = 0.0D;
			
			recording.add(new Vec3d(recordingTarget.posX, recordingTarget.posY, recordingTarget.posZ));
		}
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand p_184645_2_, ItemStack stack)
    {
		if (!isRecording && !isPlaying && player.getHeldItemMainhand().getItem() instanceof ItemSword) 
		{
			isPlaying = true;

			return true;
		}

		if (!isPlaying) 
		{
			if (!isRecording) 
			{
				setRecording(true);
				recordingTarget = player;
				player.setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);

				return true;
			} 
			else 
			{
				setRecording(false);
				recordingTarget = null;

				return true;
			}
		}
		
		return false;
    }
}
