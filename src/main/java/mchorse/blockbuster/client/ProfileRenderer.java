package mchorse.blockbuster.client;

import java.util.List;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.camera.CameraProfile;
import mchorse.blockbuster.camera.Position;
import mchorse.blockbuster.camera.fixtures.AbstractFixture;
import mchorse.blockbuster.camera.fixtures.CircularFixture;
import mchorse.blockbuster.camera.fixtures.FollowFixture;
import mchorse.blockbuster.camera.fixtures.LookFixture;
import mchorse.blockbuster.camera.fixtures.PathFixture;
import mchorse.blockbuster.commands.CommandCamera;
import mchorse.blockbuster.common.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    public void onCameraOrient(EntityViewRenderEvent.CameraSetup event)
    {
        float roll = CommandCamera.getControl().roll;

        if (roll == 0)
        {
            return;
        }

        event.setRoll(roll);
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
        float ticks = event.getPartialTicks();

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
        PathFixture path = (PathFixture) fixture;
        List<Position> points = path.getPoints();

        final int p = 15;

        for (int i = 0, size = points.size() - 1; i < size; i++)
        {
            for (int j = 0; j < p; j++)
            {
                path.applyFixture((float) (j + i * p) / (float) (size * p - 1), 0, prev);
                path.applyFixture((float) (j + i * p + 1) / (float) (size * p - 1), 0, next);

                this.drawLine(color, this.playerX, this.playerY, this.playerZ, prev, next);
            }

            if (i != 0)
            {
                path.applyFixture((float) i / (float) size, 0, prev);

                this.drawPathPoint(color, prev, i);
            }
        }
    }

    /**
     * Draw the path point
     *
     * This method is responsible for drawing a square with a label which shows
     * the index of that point. This is very useful for path point management.
     */
    private void drawPathPoint(Color color, Position position, int index)
    {
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.enableBlend();
        GlStateManager.color(color.red, color.green, color.blue, 1.0F);

        this.mc.renderEngine.bindTexture(TEXTURE);

        double x = position.point.x - this.playerX;
        double y = position.point.y - this.playerY;
        double z = position.point.z - this.playerZ;

        GL11.glNormal3f(0, 1, 0);
        GlStateManager.translate(x, y + this.mc.thePlayer.eyeHeight, z);
        GlStateManager.rotate(-this.mc.getRenderManager().playerViewY, 0, 1, 0);
        GlStateManager.rotate(this.mc.getRenderManager().playerViewX, 1, 0, 0);

        float factor = 0.1F;
        float minX = -factor;
        float minY = -factor;
        float maxX = factor;
        float maxY = factor;

        int tw = 34;

        int tx = 32;
        int tx2 = 34;

        float texX = (float) tx / (float) tw;
        float texY = 0;
        float texRX = (float) tx2 / (float) tw;
        float texRY = 2.0F / 16.0F;

        VertexBuffer vb = Tessellator.getInstance().getBuffer();

        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(minX, minY, 0).tex(texRX, texRY).endVertex();
        vb.pos(minX, maxY, 0).tex(texRX, texY).endVertex();
        vb.pos(maxX, maxY, 0).tex(texX, texY).endVertex();
        vb.pos(maxX, minY, 0).tex(texX, texRY).endVertex();

        Tessellator.getInstance().draw();
        GlStateManager.disableBlend();

        String indexString = String.valueOf(index);
        int indexWidth = this.mc.fontRendererObj.getStringWidth(indexString) / 2;

        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.scale(0.03f, 0.03f, 0.03f);
        GlStateManager.translate(0, -3.5, -0.1);
        GlStateManager.translate(0, -12, 0);

        this.mc.fontRendererObj.drawString(indexString, -indexWidth, 0, -1);

        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1, 1);
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

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.enableBlend();
        GlStateManager.color(color.red, color.green, color.blue, selected ? 1.0F : 0.75F);

        this.mc.renderEngine.bindTexture(TEXTURE);

        double x = pos.point.x - this.playerX;
        double y = pos.point.y - this.playerY;
        double z = pos.point.z - this.playerZ;

        GL11.glNormal3f(0, 1, 0);
        GlStateManager.translate(x, y + this.mc.thePlayer.eyeHeight, z);
        GlStateManager.rotate(-this.mc.getRenderManager().playerViewY, 0, 1, 0);
        GlStateManager.rotate(this.mc.getRenderManager().playerViewX, 1, 0, 0);

        float factor = selected ? 0.65F : 0.5F;

        float minX = -factor;
        float minY = -factor;
        float maxX = factor;
        float maxY = factor;

        int tw = 34;
        int i = selected ? 1 : 0;

        int tx = i * 16;
        int tx2 = tx + 16;

        float texX = (float) tx / (float) tw;
        float texY = 0;
        float texRX = (float) tx2 / (float) tw;
        float texRY = 1;

        VertexBuffer vb = Tessellator.getInstance().getBuffer();

        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(minX, minY, 0).tex(texRX, texRY).endVertex();
        vb.pos(minX, maxY, 0).tex(texRX, texY).endVertex();
        vb.pos(maxX, maxY, 0).tex(texX, texY).endVertex();
        vb.pos(maxX, minY, 0).tex(texX, texRY).endVertex();

        Tessellator.getInstance().draw();
        GlStateManager.disableBlend();

        String indexString = String.valueOf(index);
        String durationString = duration + "t";
        int indexWidth = this.mc.fontRendererObj.getStringWidth(indexString) / 2;
        int durationWidth = this.mc.fontRendererObj.getStringWidth(durationString) / 2;

        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.scale(0.05f, 0.05f, 0.05f);
        GlStateManager.translate(0, -3.5, -0.1);

        this.mc.fontRendererObj.drawString(indexString, -indexWidth, 0, -1);

        GlStateManager.translate(0, -13, 0);
        GlStateManager.scale(0.5f, 0.5f, 0.5f);

        this.mc.fontRendererObj.drawString(durationString, -durationWidth, 0, -1);

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

        VertexBuffer vb = Tessellator.getInstance().getBuffer();

        vb.setTranslation(x, y + this.mc.thePlayer.eyeHeight, z);
        vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        vb.pos(next.point.x - prev.point.x, next.point.y - prev.point.y, next.point.z - prev.point.z).endVertex();
        vb.pos(0, 0, 0).endVertex();

        Tessellator.getInstance().draw();

        vb.setTranslation(0, 0, 0);

        GlStateManager.enableTexture2D();
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();

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

    /**
     * Colors for fixture types.
     */
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