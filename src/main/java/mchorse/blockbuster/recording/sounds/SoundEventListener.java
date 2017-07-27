package mchorse.blockbuster.recording.sounds;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class SoundEventListener implements IWorldEventListener
{
    public static SoundEventListener INSTANCE = null;

    public SoundSession session;
    public Minecraft mc;
    public World world;

    public int frame;

    public SoundEventListener(World world)
    {
        this.mc = Minecraft.getMinecraft();
        this.world = world;
    }

    @Override
    public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags)
    {}

    @Override
    public void notifyLightSet(BlockPos pos)
    {}

    @Override
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2)
    {}

    @Override
    public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch)
    {
        if (this.session != null)
        {
            EntityPlayer thePlayer = this.mc.player;

            double dx = thePlayer.posX - x;
            double dy = thePlayer.posY - y;
            double dz = thePlayer.posZ - z;
            double distance = dx * dx + dy * dy + dz * dz;

            distance = MathHelper.clamp(distance, 0, 30 * 30) / (30 * 30);
            volume = volume * (float) (1 - distance);

            if (volume > 0.00001F)
            {
                this.session.record(this.frame, soundIn.getSoundName().toString(), volume, pitch);
            }
        }
    }

    @Override
    public void playRecord(SoundEvent soundIn, BlockPos pos)
    {}

    @Override
    public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {}

    @Override
    public void spawnParticle(int p_190570_1_, boolean p_190570_2_, boolean p_190570_3_, double p_190570_4_, double p_190570_6_, double p_190570_8_, double p_190570_10_, double p_190570_12_, double p_190570_14_, int... p_190570_16_)
    {}

    @Override
    public void onEntityAdded(Entity entityIn)
    {}

    @Override
    public void onEntityRemoved(Entity entityIn)
    {}

    @Override
    public void broadcastSound(int soundID, BlockPos pos, int data)
    {}

    @Override
    public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data)
    {}

    @Override
    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress)
    {}

    /**
     * This is going to count ticks (used for camera synchronization)
     */
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        if (event.side == Side.CLIENT && event.player == this.mc.player && event.phase == Phase.START)
        {
            this.frame++;
        }
    }
}