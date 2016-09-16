package mchorse.blockbuster.client.gui;

import java.io.IOException;
import java.util.List;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.actor.ModelPack;
import mchorse.blockbuster.client.gui.elements.GuiChildScreen;
import mchorse.blockbuster.client.gui.elements.GuiCompleterViewer;
import mchorse.blockbuster.client.gui.elements.GuiParentScreen;
import mchorse.blockbuster.client.gui.elements.GuiToggle;
import mchorse.blockbuster.client.gui.utils.GuiUtils;
import mchorse.blockbuster.client.gui.utils.TabCompleter;
import mchorse.blockbuster.entity.EntityActor;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModifyActor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Actor configuration GUI
 *
 * This GUI is opened via player.openGui and has an id of 1. Most of the code
 * below is easy to understand, so no comments are needed.
 */
@SideOnly(Side.CLIENT)
public class GuiActor extends GuiChildScreen
{
    /* Cached localization strings */
    private String stringTitle = I18n.format("blockbuster.gui.actor.title");
    private String stringName = I18n.format("blockbuster.gui.actor.name");
    private String stringModel = I18n.format("blockbuster.gui.actor.model");
    private String stringFilename = I18n.format("blockbuster.gui.actor.filename");
    private String stringSkin = I18n.format("blockbuster.gui.actor.skin");
    private String stringInvulnerability = I18n.format("blockbuster.gui.actor.invulnerability");

    /* Domain objects, they're provide data */
    private EntityActor actor;
    private BlockPos pos;
    private int id;

    private ModelPack pack;
    private List<String> skins;

    /* GUI fields */
    private GuiTextField name;
    private GuiTextField model;
    private GuiTextField filename;
    private GuiTextField skin;
    private GuiCompleterViewer skinViewer;

    private GuiButton done;
    private GuiButton restore;
    private GuiToggle invincibility;

    private TabCompleter completer;

    /**
     * Constructor for director map block
     */
    public GuiActor(GuiParentScreen parent, EntityActor actor, BlockPos pos, int id)
    {
        this(parent, actor);
        this.pos = pos;
        this.id = id;
    }

    /**
     * Constructor for director block and skin manager item
     */
    public GuiActor(GuiParentScreen parent, EntityActor actor)
    {
        super(parent);

        this.pack = ClientProxy.actorPack.pack;
        this.pack.reload();

        this.actor = actor;
        this.skins = this.pack.getSkins(actor.model);
    }

    /* Actions */

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.saveAndQuit();
        }
        else if (button.id == 3)
        {
            this.skin.setText("");
        }
        else if (button.id == 4)
        {
            this.invincibility.toggle();
        }
    }

    /**
     * Save and quit this screen
     *
     * Depends on the fact where does this GUI was opened from, it either sends
     * modify actor packet, which modifies entity's properties directly, or
     * sends edit action to director map block
     */
    private void saveAndQuit()
    {
        String filename = this.filename.getText();
        String name = this.name.getText();
        String skin = this.skin.getText();
        String model = this.model.getText();
        boolean invulnerability = this.invincibility.getValue();

        if (this.pos == null)
        {
            Dispatcher.sendToServer(new PacketModifyActor(this.actor.getEntityId(), filename, name, skin, model, invulnerability));
        }

        this.close();
    }

    /* Setting up child GUI screens */

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        super.setWorldAndResolution(mc, width, height);

        this.skinViewer.setWorldAndResolution(mc, width, height);
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.skinViewer.handleMouseInput();
    }

    /* Handling input */

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (this.skinViewer.isInside(mouseX, mouseY)) return;

        super.mouseClicked(mouseX, mouseY, mouseButton);

        boolean modelUsedFocused = this.model.isFocused();
        boolean skinUsedFocused = this.skin.isFocused();

        this.name.mouseClicked(mouseX, mouseY, mouseButton);
        this.model.mouseClicked(mouseX, mouseY, mouseButton);
        this.filename.mouseClicked(mouseX, mouseY, mouseButton);
        this.skin.mouseClicked(mouseX, mouseY, mouseButton);

        /* Populate the tab completer */
        if (!modelUsedFocused && this.model.isFocused())
        {
            List<String> models = this.pack.getModels();

            models.add(0, "steve");
            models.add(0, "alex");

            this.completer.setAllCompletions(models);
            this.completer.setField(this.model);
            this.skinViewer.updateRect(10, 50 + 20 - 1, 120, 100);
        }
        else if (!skinUsedFocused && this.skin.isFocused())
        {
            this.skins = this.pack.getSkins(this.model.getText());
            this.completer.setAllCompletions(this.skins);
            this.completer.setField(this.skin);
            this.skinViewer.updateRect(10, 50 + 60 - 1, 120, 100);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);

        /* 15 = tab */
        if (keyCode == 15 && (this.skin.isFocused() || this.model.isFocused()))
        {
            this.completer.complete();

            int size = this.completer.getCompletions().size();
            this.skinViewer.setHeight(size * 20);
            this.skinViewer.setHidden(size == 0);
        }
        else
        {
            this.completer.resetDidComplete();
            this.skinViewer.setHidden(true);
        }

        this.name.textboxKeyTyped(typedChar, keyCode);
        this.model.textboxKeyTyped(typedChar, keyCode);
        this.filename.textboxKeyTyped(typedChar, keyCode);
        this.skin.textboxKeyTyped(typedChar, keyCode);
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
        int x = 10;
        int w = 120;
        int y = 50;
        int x2 = this.width - x - w;
        int y2 = this.height - 25;

        /* Initializing all GUI fields first */
        this.model = new GuiTextField(7, this.fontRendererObj, x + 1, y + 1, w - 2, 18);
        this.skin = new GuiTextField(1, this.fontRendererObj, x + 1, y + 41, w - 2, 18);

        this.name = new GuiTextField(5, this.fontRendererObj, x2 + 1, y + 1, w - 2, 18);
        this.filename = new GuiTextField(6, this.fontRendererObj, x2 + 1, y + 41, w - 2, 18);
        this.invincibility = new GuiToggle(4, x2, y + 80, w, 20, I18n.format("blockbuster.no"), I18n.format("blockbuster.yes"));

        /* Buttons */
        this.done = new GuiButton(0, x2, y2, w, 20, I18n.format("blockbuster.gui.done"));
        this.restore = new GuiButton(3, x, y2, w, 20, I18n.format("blockbuster.gui.restore"));

        /* And then, we're configuring them and injecting input data */
        this.model.setText(this.actor.model);
        this.skin.setText(this.actor.skin);

        this.name.setText(this.actor.hasCustomName() ? this.actor.getCustomNameTag() : "");
        this.name.setMaxStringLength(30);
        this.filename.setText(this.actor.filename);
        this.filename.setMaxStringLength(40);
        this.invincibility.setValue(this.actor.isEntityInvulnerable(DamageSource.anvil));

        this.buttonList.add(this.done);
        this.buttonList.add(this.restore);
        this.buttonList.add(this.invincibility);

        /* Additional utilities */
        this.completer = new TabCompleter(this.skin);
        this.completer.setAllCompletions(this.skins);

        this.skinViewer = new GuiCompleterViewer(this.completer);
        this.skinViewer.updateRect(x, y + 60 - 1, w, 100);
        this.skinViewer.setHidden(true);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        int x = 10;
        int y = 10;

        int x2 = this.width - x - 120;

        /* Draw background */
        this.drawDefaultBackground();
        this.drawGradientRect(0, 30, this.width, this.height - 30, -1072689136, -804253680);

        /* Draw labels: title */
        this.drawString(this.fontRendererObj, this.stringTitle, x, y, 0xffffffff);

        /* Draw labels for visual properties */
        this.drawString(this.fontRendererObj, this.stringModel, x, y + 30, 0xffcccccc);
        this.drawString(this.fontRendererObj, this.stringSkin, x, y + 70, 0xffcccccc);

        /* Draw labels for meta properties */
        this.drawString(this.fontRendererObj, this.stringName, x2, y + 30, 0xffcccccc);
        this.drawString(this.fontRendererObj, this.stringFilename, x2, y + 70, 0xffcccccc);
        this.drawString(this.fontRendererObj, this.stringInvulnerability, x2, y + 110, 0xffcccccc);

        /* Draw entity in the center of the screen */
        int size = this.height / 4;

        y = this.height / 2 + this.height / 4;
        x = this.width / 2;

        String skin = this.actor.skin;
        String model = this.actor.model;

        this.actor.skin = this.skin.getText();
        this.actor.model = this.model.getText();
        this.actor.renderName = false;
        GuiUtils.drawEntityOnScreen(x, y, size, x - mouseX, (y - size) - mouseY, this.actor);
        this.actor.renderName = true;
        this.actor.model = model;
        this.actor.skin = skin;

        /* Draw GUI elements */
        this.name.drawTextBox();
        this.model.drawTextBox();
        this.filename.drawTextBox();
        this.skin.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);

        this.skinViewer.drawScreen(mouseX, mouseY, partialTicks);
    }
}