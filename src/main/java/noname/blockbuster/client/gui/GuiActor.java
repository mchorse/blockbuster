package noname.blockbuster.client.gui;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import noname.blockbuster.ClientProxy;
import noname.blockbuster.client.gui.elements.GuiToggle;
import noname.blockbuster.entity.EntityActor;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.PacketChangeSkin;
import noname.blockbuster.network.common.PacketModifyActor;

/**
 * Actor skin picker
 *
 * This GUI is opened via player.openGui and has an id of 1. Most of the code
 * below is easy to understand, so no comments are needed.
 */
public class GuiActor extends GuiScreen
{
    private EntityActor actor;

    private GuiTextField name;

    private GuiButton done;
    private GuiButton next;
    private GuiButton prev;
    private GuiButton restore;
    private GuiToggle invincibility;

    private List<String> skins;
    private int skinIndex;

    public GuiActor(EntityActor actor)
    {
        this.actor = actor;
        this.skins = ClientProxy.actorPack.getReloadedSkins();
        this.skinIndex = this.skins.indexOf(actor.skin);
    }

    @Override
    public void initGui()
    {
        int x = 30;
        int w = 120;

        this.buttonList.add(this.done = new GuiButton(0, x, 200, w, 20, I18n.format("blockbuster.gui.done")));
        this.buttonList.add(this.next = new GuiButton(1, x, 80, w / 2 - 4, 20, I18n.format("blockbuster.gui.next")));
        this.buttonList.add(this.prev = new GuiButton(2, x + w / 2 + 4, 80, w / 2 - 4, 20, I18n.format("blockbuster.gui.previous")));
        this.buttonList.add(this.restore = new GuiButton(3, x, 105, w, 20, I18n.format("blockbuster.gui.restore")));
        this.buttonList.add(this.invincibility = new GuiToggle(4, x, 155, w, 20, "No", "Yes"));

        this.name = new GuiTextField(5, this.fontRendererObj, x + 1, 41, w - 2, 18);

        this.next.enabled = this.prev.enabled = this.skins.size() != 0;
        this.invincibility.setValue(this.actor.isEntityInvulnerable(DamageSource.anvil));
        this.name.setText(this.actor.hasCustomName() ? this.actor.getCustomNameTag() : "");
    }

    /* Actions */

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            Dispatcher.getInstance().sendToServer(new PacketModifyActor(this.actor.getEntityId(), this.invincibility.getValue(), this.name.getText()));
            this.mc.displayGuiScreen(null);
        }
        else if (button.id == 1)
        {
            this.updateSkin(this.skinIndex + 1);
        }
        else if (button.id == 2)
        {
            this.updateSkin(this.skinIndex - 1);
        }
        else if (button.id == 3)
        {
            this.skinIndex = -1;
            this.updateSkin();
        }
        else if (button.id == 4)
        {
            this.invincibility.toggle();
            this.actor.setEntityInvulnerable(this.invincibility.getValue());
        }
    }

    private void updateSkin(int index)
    {
        this.skinIndex = MathHelper.clamp_int(index, 0, this.skins.size() - 1);
        this.updateSkin();
    }

    private void updateSkin()
    {
        Dispatcher.getInstance().sendToServer(new PacketChangeSkin(this.actor.getEntityId(), this.skinIndex >= 0 ? this.skins.get(this.skinIndex) : ""));
    }

    /* Handling input */

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.name.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);
        this.name.textboxKeyTyped(typedChar, keyCode);
    }

    /* Drawing */

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        int centerX = this.width / 2;
        String title = I18n.format("blockbuster.gui.skin.title");
        String skin = I18n.format("blockbuster.gui.skin.default");

        if (this.skinIndex != -1)
        {
            skin = this.skins.get(this.skinIndex);
        }

        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, title, centerX, 15, 0xffffffff);
        this.drawCenteredString(this.fontRendererObj, skin, centerX + 40, 208, 0xffffffff);
        this.drawString(this.fontRendererObj, "Name", 30, 30, 0xffcccccc);
        this.drawString(this.fontRendererObj, "Skin", 30, 70, 0xffcccccc);
        this.drawString(this.fontRendererObj, "Can be killed?", 30, 145, 0xffcccccc);

        this.actor.renderName = false;
        drawEntityOnScreen(centerX + 40, 190, 75, centerX - mouseX, 70 - mouseY, this.actor);
        this.actor.renderName = true;

        this.name.drawTextBox();
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