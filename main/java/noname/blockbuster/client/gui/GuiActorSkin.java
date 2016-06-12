package noname.blockbuster.client.gui;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import noname.blockbuster.ClientProxy;
import noname.blockbuster.entity.ActorEntity;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.ChangeSkin;

public class GuiActorSkin extends GuiScreen
{
    private ActorEntity actor;
    private GuiButton done;
    private GuiButton next;
    private GuiButton prev;
    private GuiButton restore;
    private List<String> skins;
    private int skinIndex;

    public GuiActorSkin(ActorEntity actor)
    {
        this.actor = actor;
        this.skins = ClientProxy.actorPack.getReloadedSkins();
        this.skinIndex = this.skins.indexOf(actor.skin);
    }

    @Override
    public void initGui()
    {
        int centerX = this.width / 2;

        this.buttonList.clear();
        this.buttonList.add(this.done = new GuiButton(0, centerX - 100, 210, 200, 20, I18n.format("blockbuster.gui.done", new Object[] {})));
        this.buttonList.add(this.next = new GuiButton(1, centerX - 100, 18, 20, 20, I18n.format("blockbuster.gui.next", new Object[] {})));
        this.buttonList.add(this.prev = new GuiButton(2, centerX + 80, 18, 20, 20, I18n.format("blockbuster.gui.previous", new Object[] {})));
        this.buttonList.add(this.restore = new GuiButton(3, centerX - 100, 185, 200, 20, I18n.format("blockbuster.gui.restore", new Object[] {})));

        this.next.enabled = this.prev.enabled = this.skins.size() != 0;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        switch (button.id)
        {
            case 0:
                this.mc.displayGuiScreen(null);
                break;

            case 1:
                this.nextSkin();
                break;

            case 2:
                this.prevSkin();
                break;

            case 3:
                this.skinIndex = -1;
                this.updateSkin();
                break;
        }
    }

    private void nextSkin()
    {
        this.skinIndex++;

        if (this.skinIndex >= this.skins.size())
        {
            this.skinIndex = 0;
        }

        this.updateSkin();
    }

    private void prevSkin()
    {
        this.skinIndex--;

        if (this.skinIndex < 0)
        {
            this.skinIndex = this.skins.size() - 1;
        }

        this.updateSkin();
    }

    private void updateSkin()
    {
        IMessage message = new ChangeSkin(this.actor.getEntityId(), this.skinIndex >= 0 ? this.skins.get(this.skinIndex) : "");

        Dispatcher.getInstance().sendToServer(message);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        int centerX = this.width / 2;
        String title = I18n.format("blockbuster.gui.skin.title", new Object[] {});
        String skin = I18n.format("blockbuster.gui.skin.default", new Object[] {});

        if (this.skinIndex != -1)
        {
            skin = this.skins.get(this.skinIndex);
        }

        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, title, centerX, 25, 0xffffffff);
        this.drawCenteredString(this.fontRendererObj, skin, centerX, 170, 0xffffffff);

        drawEntityOnScreen(this.width / 2, 155, 50, centerX - mouseX, 120 - mouseY, this.actor);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Draw an entity on the screen
     *
     * Taken from minecraft's class GuiInventory
     */
    public static void drawEntityOnScreen(int posX, int posY, int scale, int mouseX, int mouseY, EntityLivingBase ent)
    {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, 50.0F);
        GlStateManager.scale((-scale), scale, scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);

        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;

        ent.renderYawOffset = (float) Math.atan(mouseX / 40.0F) * 20.0F;
        ent.rotationYaw = (float) Math.atan(mouseX / 40.0F) * 40.0F;
        ent.rotationPitch = -((float) Math.atan(mouseY / 40.0F)) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;

        GlStateManager.translate(0.0F, 0.0F, 0.0F);

        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.doRenderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);

        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}
