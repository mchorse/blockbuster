package mchorse.blockbuster.client.gui;

import java.io.IOException;
import java.util.List;

import mchorse.blockbuster.api.ModelPack;
import mchorse.blockbuster.client.gui.utils.GuiUtils;
import mchorse.blockbuster.client.gui.utils.TabCompleter;
import mchorse.blockbuster.client.gui.widgets.GuiCompleterViewer;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiToggle;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModifyActor;
import mchorse.blockbuster.utils.RLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Actor configuration GUI
 *
 * This GUI is opened via player.openGui and has an id of 1. Most of the code
 * below is easy to understand, so no comments are needed.
 */
@SideOnly(Side.CLIENT)
public class GuiActor extends GuiScreen
{
    /* Cached localization strings */
    private String stringTitle = I18n.format("blockbuster.gui.actor.title");
    private String stringModel = I18n.format("blockbuster.gui.actor.model");
    private String stringSkin = I18n.format("blockbuster.gui.actor.skin");
    private String stringInvisible = I18n.format("blockbuster.gui.actor.invisible");

    /* Domain objects, they're provide data */
    private EntityActor actor;

    private ModelPack pack;
    private List<String> skins;

    /* GUI fields */
    private GuiTextField model;
    private GuiTextField skin;
    private GuiCompleterViewer skinViewer;

    private GuiButton done;
    private GuiButton restore;
    private GuiToggle invisible;

    private TabCompleter completer;

    /**
     * Constructor for director block and skin manager item
     */
    public GuiActor(EntityActor actor)
    {
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
        else if (button.id == 1)
        {
            this.model.setText("");
            this.skin.setText("");
        }
        else if (button.id == 2)
        {
            this.invisible.toggle();
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
        String model = this.model.getText();
        ResourceLocation skin = RLUtils.fromString(this.skin.getText(), model);
        boolean invisible = this.invisible.getValue();

        Dispatcher.sendToServer(new PacketModifyActor(this.actor.getEntityId(), model, skin, invisible));
        Minecraft.getMinecraft().displayGuiScreen(null);
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

        this.model.mouseClicked(mouseX, mouseY, mouseButton);
        this.skin.mouseClicked(mouseX, mouseY, mouseButton);

        /* Populate the tab completer */
        if (!modelUsedFocused && this.model.isFocused())
        {
            List<String> models = this.pack.getModels();

            models.add(0, "steve");
            models.add(0, "alex");

            this.completer.setAllCompletions(models);
            this.completer.setField(this.model);
            this.skinViewer.updateRect(10, 30 + 20 - 1, 120, 100);
        }
        else if (!skinUsedFocused && this.skin.isFocused())
        {
            this.skins = this.pack.getSkins(this.model.getText());
            this.completer.setAllCompletions(this.skins);
            this.completer.setField(this.skin);
            this.skinViewer.updateRect(10, 30 + 60 - 1, 120, 100);
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

        this.model.textboxKeyTyped(typedChar, keyCode);
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
        int y = 30;
        int y2 = this.height - 30;

        /* Initializing all GUI fields first */
        this.model = new GuiTextField(4, this.fontRendererObj, x + 1, y + 1, w - 2, 18);
        this.skin = new GuiTextField(3, this.fontRendererObj, x + 1, y + 41, w - 2, 18);
        this.invisible = new GuiToggle(2, x, y + 80, w, 20, I18n.format("blockbuster.no"), I18n.format("blockbuster.yes"));

        /* Buttons */
        this.done = new GuiButton(0, x, y2, w, 20, I18n.format("blockbuster.gui.done"));
        this.restore = new GuiButton(1, x, y2 - 25, w, 20, I18n.format("blockbuster.gui.restore"));

        /* And then, we're configuring them and injecting input data */
        this.fillData();

        this.buttonList.add(this.done);
        this.buttonList.add(this.restore);
        this.buttonList.add(this.invisible);

        /* Additional utilities */
        this.completer = new TabCompleter(this.skin);
        this.completer.setAllCompletions(this.skins);

        this.skinViewer = new GuiCompleterViewer(this.completer);
        this.skinViewer.updateRect(x, y + 60 - 1, w, 100);
        this.skinViewer.setHidden(true);
    }

    private void fillData()
    {
        this.skin.setMaxStringLength(120);

        this.model.setText(this.actor.model);
        this.skin.setText(RLUtils.fromResource(this.actor.skin));
        this.invisible.setValue(this.actor.invisible);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        int x = 10;
        int y = 10;

        /* Draw background */
        this.drawDefaultBackground();

        /* Draw labels: title */
        this.drawCenteredString(this.fontRendererObj, this.stringTitle, this.width / 2, y, 0xffffffff);

        /* Draw labels for visual properties */
        this.drawString(this.fontRendererObj, this.stringModel, x, y + 10, 0xffcccccc);
        this.drawString(this.fontRendererObj, this.stringSkin, x, y + 50, 0xffcccccc);
        this.drawString(this.fontRendererObj, this.stringInvisible, x, y + 90, 0xffcccccc);

        /* Draw entity in the center of the screen */
        int size = this.height / 3;

        y = this.height / 2 + (int) (size * 1.2);
        x = this.width / 2;

        String model = this.actor.model;
        ResourceLocation skin = this.actor.skin;
        boolean invisible = this.actor.invisible;

        this.actor.model = this.model.getText();
        this.actor.skin = RLUtils.fromString(this.skin.getText(), this.actor.model);
        this.actor.invisible = this.invisible.getValue();
        this.actor.renderName = false;
        GuiUtils.drawEntityOnScreen(x, y, size, x - mouseX, (y - size) - mouseY, this.actor);
        this.actor.renderName = true;
        this.actor.model = model;
        this.actor.skin = skin;
        this.actor.invisible = invisible;

        /* Draw GUI elements */
        this.model.drawTextBox();
        this.skin.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);

        this.skinViewer.drawScreen(mouseX, mouseY, partialTicks);
    }
}