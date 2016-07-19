package noname.blockbuster.client;

import java.util.List;

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
import noname.blockbuster.camera.fixtures.AbstractFixture;
import noname.blockbuster.camera.fixtures.CircularFixture;
import noname.blockbuster.camera.fixtures.FollowFixture;
import noname.blockbuster.camera.fixtures.IdleFixture;
import noname.blockbuster.camera.fixtures.LookFixture;
import noname.blockbuster.camera.fixtures.PathFixture;

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

    /**
     * Wurst code ever! lol
     */
    @SubscribeEvent
    public void onLastRender(RenderWorldLastEvent event)
    {
        if (this.profile == null || this.profile.getCount() < 1) return;

        Position prev = new Position(0, 0, 0, 0, 0);
        Position next = new Position(0, 0, 0, 0, 0);

        EntityPlayer player = this.mc.thePlayer;
        float ticks = event.getPartialTicks();

        double playerX = player.prevPosX + (player.posX - player.prevPosX) * ticks;
        double playerY = player.prevPosY + (player.posY - player.prevPosY) * ticks;
        double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * ticks;

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glLineWidth(2);
        GL11.glPointSize(10);
        GL11.glTranslated(-playerX, -playerY + player.eyeHeight, -playerZ);

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

        for (AbstractFixture fixture : this.profile.getAll())
        {
            if (fixture instanceof IdleFixture || fixture instanceof LookFixture || fixture instanceof FollowFixture)
            {
                this.drawStaticFixture(fixture, prev);
            }
            else if (fixture instanceof PathFixture)
            {
                this.drawPathFixture(fixture);
            }
            else if (fixture instanceof CircularFixture)
            {
                this.drawCircularFixture(fixture, prev, next);
            }
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    private void drawStaticFixture(AbstractFixture fixture, Position prev)
    {
        fixture.applyFixture(0, prev);

        GL11.glColor3ub((byte) 240, (byte) 0, (byte) 0);
        GL11.glBegin(GL11.GL_POINTS);
        GL11.glVertex3f(prev.point.x, prev.point.y, prev.point.z);
        GL11.glEnd();
    }

    private void drawPathFixture(AbstractFixture fixture)
    {
        GL11.glColor3ub((byte) 0, (byte) 0, (byte) 250);
        List<Position> points = ((PathFixture) fixture).getPoints();

        for (int i = 0, count = points.size() - 1; i < count; i++)
        {
            this.drawLine(points.get(i).point, points.get(i + 1).point);
        }
    }

    private void drawCircularFixture(AbstractFixture fixture, Position prev, Position next)
    {
        GL11.glColor3ub((byte) 0, (byte) 230, (byte) 0);
        CircularFixture circle = (CircularFixture) fixture;
        float circles = circle.getCircles();

        for (int i = 0; i < (int) circles; i++)
        {
            fixture.applyFixture(i / circles, prev);
            fixture.applyFixture((i + 1) / circles, next);

            this.drawLine(prev.point, next.point);
        }
    }

    private void drawLine(Point prev, Point next)
    {
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3f(prev.x, prev.y, prev.z);
        GL11.glVertex3f(next.x, next.y, next.z);
        GL11.glEnd();
    }
}