
package mchorse.blockbuster.client;

import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.camera.CameraProfile;
import mchorse.blockbuster.camera.Position;
import mchorse.blockbuster.camera.fixtures.AbstractFixture;
import mchorse.blockbuster.camera.fixtures.CircularFixture;
import mchorse.blockbuster.camera.fixtures.FollowFixture;
import mchorse.blockbuster.camera.fixtures.LookFixture;
import mchorse.blockbuster.camera.fixtures.PathFixture;
import mchorse.blockbuster.client.gui.utils.GlStateManager;
import mchorse.blockbuster.commands.CommandCamera;
import mchorse.blockbuster.common.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;

/**
 * Profile path renderer
 *
 * This class is responsible for rendering current loaded profile.
 */
@SideOnly(Side.CLIENT)
public class ProfileRenderer
{
    /**
     * Background texture for a fixture rendering.
     */
    public static final ResourceLocation TEXTURE = new ResourceLocation(Blockbuster.MODID, "textures/gui/fixture.png");

    protected Minecraft mc = Minecraft.getMinecraft();
    protected boolean render = true;
    protected CameraProfile profile;

    protected double playerX;
    protected double playerY;
    protected double playerZ;

    public void setProfile(CameraProfile profile)
    {
        this.profile = profile;
    }

    public void toggleRender()
    {
        this.render = !this.render;
    }

    @SubscribeEvent
    public void onLastRender(RenderWorldLastEvent event)
    {
        boolean badProfile = this.profile == null || this.profile.getCount() < 1;

        if (!this.render) return;
        if (ClientProxy.profileRunner.isRunning()) return;
        if (badProfile) return;

        Position prev = new Position(0, 0, 0, 0, 0);
        Position next = new Position(0, 0, 0, 0, 0);

        EntityPlayer player = this.mc.thePlayer;
        float ticks = event.partialTicks;

        this.playerX = player.prevPosX + (player.posX - player.prevPosX) * ticks;
        this.playerY = player.prevPosY + (player.posY - player.prevPosY) * ticks;
        this.playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * ticks;

        GlStateManager.pushAttrib();
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();

        int i = 0;

        for (AbstractFixture fixture : this.profile.getAll())
        {
            fixture.applyFixture(0, 0.0F, prev);
            fixture.applyFixture(1, 0.0F, next);

            long duration = fixture.getDuration();

            float distX = Math.abs(next.point.x - prev.point.x);
            float distY = Math.abs(next.point.y - prev.point.y);
            float distZ = Math.abs(next.point.z - prev.point.z);

            Color color = this.fromFixture(fixture);

            if (distX + distY + distZ >= 0.5) this.drawCard(color, i, duration, next);

            this.drawCard(color, i++, duration, prev);
            this.drawFixture(0.0F, color, fixture, prev, next);
        }

        GlStateManager.disableBlend();
        GlStateManager.popAttrib();
        GL11.glLineWidth(2);
    }

    /**
     * Draw a fixture's fixture
     */
    private void drawFixture(float partialTicks, Color color, AbstractFixture fixture, Position prev, Position next)
    {
        if (fixture instanceof PathFixture)
        {
            this.drawPathFixture(color, fixture, prev, next);
        }
        else if (fixture instanceof CircularFixture)
        {
            this.drawCircularFixture(partialTicks, color, fixture, prev, next);
        }
    }

    /**
     * Draw the passed path fixture
     */
    private void drawPathFixture(Color color, AbstractFixture fixture, Position prev, Position next)
    {
        List<Position> points = ((PathFixture) fixture).getPoints();

        for (int i = 0, size = points.size() - 1; i < size; i++)
        {
            prev.copy(points.get(i));
            next.copy(points.get(i + 1));

            this.drawLine(color, this.playerX, this.playerY, this.playerZ, prev, next);
        }
    }

    /**
     * Draw the passed circular fixture
     */
    private void drawCircularFixture(float partialTicks, Color color, AbstractFixture fixture, Position prev, Position next)
    {
        float circles = ((CircularFixture) fixture).getCircles();

        for (int i = 0; i < circles; i += 3)
        {
            fixture.applyFixture(i / circles, partialTicks, prev);
            fixture.applyFixture((i + 2) / circles, partialTicks, next);

            this.drawLine(color, this.playerX, this.playerY, this.playerZ, prev, next);
        }
    }

    /**
     * Draw the card of the fixture with the information about this fixture,
     * like duration and stuff.
     */
    private void drawCard(Color color, int index, long duration, Position pos)
    {
        boolean selected = index == CommandCamera.getControl().index;

        GL11.glPushMatrix();
        GlStateManager.pushAttrib();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(color.red, color.green, color.blue, selected ? 1.0F : 0.75F);

        this.mc.renderEngine.bindTexture(TEXTURE);

        double x = pos.point.x - this.playerX;
        double y = pos.point.y - this.playerY;
        double z = pos.point.z - this.playerZ;

        GL11.glNormal3f(0, 1, 0);
        GL11.glTranslated(x, y + this.mc.thePlayer.eyeHeight, z);
        GL11.glRotatef(-RenderManager.instance.playerViewY, 0, 1, 0);
        GL11.glRotatef(RenderManager.instance.playerViewX, 1, 0, 0);

        float factor = selected ? 0.65F : 0.5F;

        float minX = -factor;
        float minY = -factor;
        float maxX = factor;
        float maxY = factor;

        int i = selected ? 1 : 0;

        float texX = i * 0.5F;
        float texY = 0;
        float texRX = i * 0.5F + 0.5F;
        float texRY = 1;

        Tessellator vb = Tessellator.instance;

        vb.startDrawingQuads();

        vb.addVertexWithUV(minX, minY, 0, texRX, texRY);
        vb.addVertexWithUV(minX, maxY, 0, texRX, texY);
        vb.addVertexWithUV(maxX, maxY, 0, texX, texY);
        vb.addVertexWithUV(maxX, minY, 0, texX, texRY);

        vb.draw();

        GlStateManager.disableBlend();

        String indexString = String.valueOf(index);
        String durationString = duration + "t";
        int indexWidth = this.mc.fontRenderer.getStringWidth(indexString) / 2;
        int durationWidth = this.mc.fontRenderer.getStringWidth(durationString) / 2;

        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.scale(0.05f, 0.05f, 0.05f);
        GlStateManager.translate(0, -3.5, -0.1);

        this.mc.fontRenderer.drawString(indexString, -indexWidth, 0, -1);

        GlStateManager.translate(0, -13, 0);
        GlStateManager.scale(0.5f, 0.5f, 0.5f);

        this.mc.fontRenderer.drawString(durationString, -durationWidth, 0, -1);

        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1, 1);
    }

    /**
     * Draw a line between two positions
     */
    private void drawLine(Color color, double playerX, double playerY, double playerZ, Position prev, Position next)
    {
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        double x = prev.point.x - playerX;
        double y = prev.point.y - playerY;
        double z = prev.point.z - playerZ;

        GL11.glLineWidth(4);
        GlStateManager.disableTexture2D();
        GlStateManager.color(color.red, color.green, color.blue, 0.5F);

        Tessellator vb = Tessellator.instance;

        vb.startDrawing(GL11.GL_LINES);
        vb.setTranslation(x, y + this.mc.thePlayer.eyeHeight, z);

        vb.addVertex(next.point.x - prev.point.x, next.point.y - prev.point.y, next.point.z - prev.point.z);
        vb.addVertex(0, 0, 0);

        vb.draw();
        vb.setTranslation(0, 0, 0);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GlStateManager.popAttrib();
        GL11.glPopMatrix();
    }

    /**
     * Get color for a fixture
     *
     * Don't forget about instanceof thing with order and extend.
     */
    private ProfileRenderer.Color fromFixture(AbstractFixture fixture)
    {
        if (fixture instanceof PathFixture) return ProfileRenderer.Color.PATH;
        else if (fixture instanceof FollowFixture) return ProfileRenderer.Color.LOOK;
        else if (fixture instanceof LookFixture) return ProfileRenderer.Color.FOLLOW;
        else if (fixture instanceof CircularFixture) return ProfileRenderer.Color.CIRCULAR;

        return ProfileRenderer.Color.IDLE;
    }

    public static enum Color
    {
        IDLE(0.085F, 0.62F, 0.395F), PATH(0.408F, 0.128F, 0.681F), LOOK(0.85F, 0.137F, 0.329F), FOLLOW(0.298F, 0.690F, 0.972F), CIRCULAR(0.298F, 0.631F, 0.247F);

        public float red;
        public float green;
        public float blue;

        Color(float red, float green, float blue)
        {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }
}