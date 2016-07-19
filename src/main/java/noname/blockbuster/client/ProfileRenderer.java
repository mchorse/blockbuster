package noname.blockbuster.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noname.blockbuster.camera.CameraProfile;
import noname.blockbuster.camera.Point;
import noname.blockbuster.camera.Position;

/**
 * Profile path renderer
 *
 * This class is responsible for rendering current loaded profile.
 */
@SideOnly(Side.CLIENT)
public class ProfileRenderer
{
    protected Minecraft mc = Minecraft.getMinecraft();
    protected boolean render = true;
    protected CameraProfile profile;

    public void setProfile(CameraProfile profile)
    {
        this.profile = profile;
    }

    @SubscribeEvent
    public void onLastRender(RenderWorldLastEvent event)
    {
        if (this.profile == null || this.profile.getCount() < 2) return;

        Position prev = new Position(0, 0, 0, 0, 0);
        Position next = new Position(0, 0, 0, 0, 0);

        EntityPlayer player = this.mc.thePlayer;
        float ticks = event.getPartialTicks();

        double playerX = player.prevPosX + (player.posX - player.prevPosX) * ticks;
        double playerY = player.prevPosY + (player.posY - player.prevPosY) * ticks;
        double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * ticks;

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL11.glLineWidth(3);
        GL11.glTranslated(-playerX, -playerY, -playerZ);
        GL11.glColor3ub((byte) 0, (byte) 230, (byte) 0);

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

        for (int i = 0, count = (int) (this.profile.getDuration() / 50) - 1; i < count; i++)
        {
            this.profile.applyProfile(i * 50, prev);
            this.profile.applyProfile((i + 1) * 50, next);

            this.drawLine(prev.point, next.point);
        }

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    private void drawLine(Point prev, Point next)
    {
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3f(prev.x, prev.y, prev.z);
        GL11.glVertex3f(next.x, next.y, next.z);
        GL11.glEnd();
    }
}