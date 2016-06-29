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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import noname.blockbuster.ClientProxy;
import noname.blockbuster.client.gui.elements.GuiToggle;
import noname.blockbuster.entity.EntityActor;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.PacketModifyActor;
import noname.blockbuster.network.common.director.PacketDirectorMapEdit;

/**
 * Actor skin picker
 *
 * This GUI is opened via player.openGui and has an id of 1. Most of the code
 * below is easy to understand, so no comments are needed.
 */
public class GuiActor extends GuiScreen
{
    /* Cached localization strings */
    private String stringTitle = I18n.format("blockbuster.gui.actor.title");
    private String stringDefault = I18n.format("blockbuster.gui.actor.default");
    private String stringName = I18n.format("blockbuster.gui.actor.name");
    private String stringFilename = I18n.format("blockbuster.gui.actor.filename");
    private String stringSkin = I18n.format("blockbuster.gui.actor.skin");
    private String stringInvulnerability = I18n.format("blockbuster.gui.actor.invulnerability");

    /* Domain objects, they're provide data */
    private EntityActor actor;
    private BlockPos pos;
    private int id;

    private List<String> skins;
    private int skinIndex;

    /* GUI fields */
    private GuiScreen parent;

    private GuiTextField name;
    private GuiTextField filename;

    private GuiButton done;
    private GuiButton next;
    private GuiButton prev;
    private GuiButton restore;
    private GuiToggle invincibility;

    /**
     * Constructor for director map block
     */
    public GuiActor(GuiScreen parent, EntityActor actor, BlockPos pos, int id)
    {
        this(parent, actor);
        this.pos = pos;
        this.id = id;
    }

    /**
     * Constructor for director block and skin manager item
     */
    public GuiActor(GuiScreen parent, EntityActor actor)
    {
        this.parent = parent;
        this.actor = actor;
        this.skins = ClientProxy.actorPack.getReloadedSkins();
        this.skinIndex = this.skins.indexOf(actor.skin);
    }

    /* Actions */

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.saveAndQuit();
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
        }
    }

    /**
     * Save and quit this screen
     *
     * Depends on the fact where does this GUI was opened from, it either
     * sends modify actor packet, which modifies entity's properties directly,
     * or sends edit action to director map block
     */
    private void saveAndQuit()
    {
        SimpleNetworkWrapper dispatcher = Dispatcher.getInstance();

        String filename = this.filename.getText();
        String name = this.name.getText();
        String skin = this.getSkin();
        boolean invulnerability = this.invincibility.getValue();

        if (this.pos == null)
        {
            dispatcher.sendToServer(new PacketModifyActor(this.actor.getEntityId(), filename, name, skin, invulnerability));
        }
        else
        {
            this.actor.modify(filename, name, skin, invulnerability, false);

            dispatcher.sendToServer(new PacketDirectorMapEdit(this.pos, this.id, this.actor.toReplayString()));
        }

        this.mc.displayGuiScreen(this.parent);
    }

    private void updateSkin(int index)
    {
        this.skinIndex = MathHelper.clamp_int(index, 0, this.skins.size() - 1);
        this.updateSkin();
    }

    private void updateSkin()
    {
        this.actor.skin = this.getSkin();
    }

    private String getSkin()
    {
        return this.skinIndex >= 0 ? this.skins.get(this.skinIndex) : "";
    }

    /* Handling input */

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.name.mouseClicked(mouseX, mouseY, mouseButton);
        this.filename.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);
        this.name.textboxKeyTyped(typedChar, keyCode);
        this.filename.textboxKeyTyped(typedChar, keyCode);
    }

    /* Initiating GUI and drawing */

    /**
     * I think Mojang should come up with something better than hardcoded
     * positions and sizes for buttons. Something like HTML. Maybe I should
     * write this library (for constructing minecraft GUIs). Hm...
     */
    @Override
    public void initGui()
    {
        int x = 30;
        int w = 120;

        /* Initializing all GUI fields first */
        this.done = new GuiButton(0, x, 210, w, 20, I18n.format("blockbuster.gui.done"));
        this.next = new GuiButton(1, x, 120, w / 2 - 4, 20, I18n.format("blockbuster.gui.next"));
        this.prev = new GuiButton(2, x + w / 2 + 4, 120, w / 2 - 4, 20, I18n.format("blockbuster.gui.previous"));
        this.restore = new GuiButton(3, x, 145, w, 20, I18n.format("blockbuster.gui.restore"));
        this.invincibility = new GuiToggle(4, x, 185, w, 20, I18n.format("blockbuster.no"), I18n.format("blockbuster.yes"));

        this.name = new GuiTextField(5, this.fontRendererObj, x + 1, 41, w - 2, 18);
        this.filename = new GuiTextField(6, this.fontRendererObj, x + 1, 81, w - 2, 18);

        /* And then, we're configuring them and injecting input data */
        this.buttonList.add(this.done);
        this.buttonList.add(this.next);
        this.buttonList.add(this.prev);
        this.buttonList.add(this.restore);
        this.buttonList.add(this.invincibility);

        this.next.enabled = this.prev.enabled = this.skins.size() != 0;
        this.invincibility.setValue(this.actor.isEntityInvulnerable(DamageSource.anvil));

        this.name.setText(this.actor.hasCustomName() ? this.actor.getCustomNameTag() : "");
        this.name.setMaxStringLength(30);
        this.filename.setText(this.actor.filename);
        this.filename.setMaxStringLength(40);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        int centerX = this.width / 2;

        String skin = this.stringDefault;

        if (this.skinIndex != -1)
        {
            skin = this.skins.get(this.skinIndex);
        }

        this.drawDefaultBackground();

        this.drawCenteredString(this.fontRendererObj, this.stringTitle, centerX, 15, 0xffffffff);
        this.drawCenteredString(this.fontRendererObj, skin, centerX + 50, 214, 0xffffffff);

        this.drawString(this.fontRendererObj, this.stringName, 30, 30, 0xffcccccc);
        this.drawString(this.fontRendererObj, this.stringFilename, 30, 70, 0xffcccccc);
        this.drawString(this.fontRendererObj, this.stringSkin, 30, 110, 0xffcccccc);
        this.drawString(this.fontRendererObj, this.stringInvulnerability, 30, 175, 0xffcccccc);

        this.actor.renderName = false;
        drawEntityOnScreen(centerX + 50, 200, 75, centerX - mouseX, 70 - mouseY, this.actor);
        this.actor.renderName = true;

        this.name.drawTextBox();
        this.filename.drawTextBox();
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