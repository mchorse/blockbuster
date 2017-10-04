package mchorse.blockbuster.client.gui.elements;

import java.io.IOException;
import java.util.List;

import mchorse.blockbuster.client.gui.GuiDirector;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiToggle;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.common.tileentity.director.Replay;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.PacketDirectorEdit;
import mchorse.blockbuster.utils.L10n;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs.MorphCell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Subview in director block GUI
 *
 * This GUI is dependent on {@link GuiDirector} UI. This method is responsible
 * for manipulating the selected {@link Replay} from {@link GuiReplays}.
 */
@SideOnly(Side.CLIENT)
public class GuiReplay extends GuiScreen
{
    /* Cached localization strings used in drawScreen method */
    private String stringName = I18n.format("blockbuster.gui.actor.name");
    private String stringFilename = I18n.format("blockbuster.gui.actor.filename");
    private String stringInvincible = I18n.format("blockbuster.gui.actor.invincible");
    private String stringInvisible = I18n.format("blockbuster.gui.actor.invisible");
    private String stringAttached = I18n.format("blockbuster.gui.actor.attached");
    private String stringClickhere = I18n.format("blockbuster.info.recording.clickhere");

    /* Domain objects, they provide data */
    private Replay replay;
    private BlockPos pos;
    private int index;

    /* GUI fields */
    private GuiTextField name;
    private GuiTextField filename;
    private GuiMorphsPopup morphs;

    /* Buttons */
    private GuiButton detach;
    private GuiButton remove;
    private GuiButton record;
    private GuiButton pick;
    private GuiButton duplicate;
    private GuiToggle invincible;
    private GuiToggle invisible;

    /* Widgets and stuff */
    private GuiDirector parent;

    /**
     * Constructor for director map block
     */
    public GuiReplay(GuiDirector parent, BlockPos pos)
    {
        ClientProxy.actorPack.pack.reload();

        this.morphs = new GuiMorphsPopup(6, null, Morphing.get(Minecraft.getMinecraft().player));
        this.parent = parent;
        this.pos = pos;
    }

    /**
     * Select the given replay with index. If replay is null (i.e. replay was
     * deselected). If replay isn't null, then fill the data in GUI fields.
     */
    public void select(Replay replay, int index)
    {
        this.replay = replay;
        this.index = index;

        this.morphs.morphs.setFilter("");
        this.morphs.search.setText("");

        if (replay != null)
        {
            this.fillData();
        }
    }

    /**
     * Is morph picker is active? 
     */
    public boolean isMorphPickerActive()
    {
        return !this.morphs.morphs.getHidden();
    }

    /* Actions */

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 2)
        {
            this.parent.remove(this.index);
        }
        else if (button.id == 3)
        {
            this.replay.actor = null;
            this.parent.detach(this.index);
        }
        else if (button.id == 4)
        {
            this.invincible.toggle();
        }
        else if (button.id == 5)
        {
            this.invisible.toggle();
        }
        else if (button.id == 6)
        {
            this.morphs.morphs.setHidden(false);
        }
        else if (button.id == 7)
        {
            this.parent.duplicate(this.index);
        }
        else if (button.id == 8)
        {
            this.sendRecordMessage();
        }
    }

    /**
     * Send record message to the player
     */
    private void sendRecordMessage()
    {
        EntityPlayer player = this.mc.player;

        if (this.filename.getText().isEmpty())
        {
            L10n.error(player, "recording.fill_filename");

            return;
        }

        String command = "/action record " + this.filename.getText() + " " + this.pos.getX() + " " + this.pos.getY() + " " + this.pos.getZ();

        ITextComponent component = new TextComponentString(stringClickhere);
        component.getStyle().setClickEvent(new ClickEvent(Action.RUN_COMMAND, command));
        component.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(command)));
        component.getStyle().setColor(TextFormatting.GRAY).setUnderlined(true);

        L10n.info(player, "recording.message", this.filename.getText(), component);

        /* Add the command to the history */
        List<String> messages = this.mc.ingameGUI.getChatGUI().getSentMessages();

        boolean empty = messages.isEmpty();
        boolean lastMessageIsntCommand = !empty && !messages.get(messages.size() - 1).equals(command);

        if (lastMessageIsntCommand || empty)
        {
            messages.add(command);
        }
    }

    /**
     * Save the replay
     *
     * Saves the replay, by sending the replay over to the server. Given
     * argument determines if the cast is going to be updated.
     */
    public void save(boolean update)
    {
        MorphCell cell = this.morphs.morphs.getSelected();

        Replay value = new Replay();
        value.id = this.filename.getText();
        value.name = this.name.getText();
        value.invincible = this.invincible.getValue();

        value.morph = cell == null ? this.replay.morph : cell.current().morph.clone(true);
        value.invisible = this.invisible.getValue();

        value.actor = this.replay.actor;

        if (this.replay.equals(value))
        {
            return;
        }

        this.replay.morph = value.morph;

        Dispatcher.sendToServer(new PacketDirectorEdit(this.pos, value, this.index, update));
    }

    /* Setting up child GUI screens */

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        super.setWorldAndResolution(mc, width, height);
        this.morphs.setWorldAndResolution(mc, width, height);
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        this.morphs.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    public void handleKeyboardInput() throws IOException
    {
        this.morphs.handleKeyboardInput();
        super.handleKeyboardInput();
    }

    /* Handling input */

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (this.morphs.isInside(mouseX, mouseY))
        {
            return;
        }

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
        int margin = 8;

        int x = 120 + margin;
        int w = 80;
        int y2 = this.height - margin;
        int x2 = x + w + margin;

        /* Initializing all GUI fields first */
        this.invisible = new GuiToggle(5, x2, y2 - 55, w, 20, I18n.format("blockbuster.no"), I18n.format("blockbuster.yes"));
        this.invincible = new GuiToggle(4, x2, y2 - 20, w, 20, I18n.format("blockbuster.no"), I18n.format("blockbuster.yes"));

        this.name = new GuiTextField(-1, this.fontRenderer, x + 1, y2 - 54, w - 2, 18);
        this.filename = new GuiTextField(-1, this.fontRenderer, x + 1, y2 - 19, w - 2, 18);

        /* Buttons */
        this.detach = new GuiButton(3, this.width - margin - 80, margin, 80, 20, I18n.format("blockbuster.gui.detach"));
        this.remove = new GuiButton(2, this.width - margin - 80, margin + 25, 80, 20, I18n.format("blockbuster.gui.remove"));
        this.pick = new GuiButton(6, x, margin + 25, 80, 20, I18n.format("blockbuster.gui.pick"));
        this.duplicate = new GuiButton(7, x, margin + 50, 80, 20, I18n.format("blockbuster.gui.duplicate"));
        this.record = new GuiButton(8, x, margin, 80, 20, I18n.format("blockbuster.gui.record"));

        /* And then, we're configuring them and injecting input data */
        this.fillData();

        this.buttonList.add(this.remove);
        this.buttonList.add(this.detach);
        this.buttonList.add(this.pick);
        this.buttonList.add(this.duplicate);
        this.buttonList.add(this.record);

        this.buttonList.add(this.invincible);
        this.buttonList.add(this.invisible);

        /* Morph */
        this.morphs.updateRect(x - margin, 0, this.width - x + margin, this.height);
    }

    private void fillData()
    {
        if (this.replay == null)
        {
            return;
        }

        this.name.setMaxStringLength(30);
        this.filename.setMaxStringLength(40);

        this.invisible.setValue(this.replay.invisible);

        this.name.setText(this.replay.name);
        this.filename.setText(this.replay.id);
        this.invincible.setValue(this.replay.invincible);
        this.morphs.morphs.setSelected(this.replay.morph);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (this.replay == null)
        {
            return;
        }

        int x = 128;
        int x2 = 128 + 80 + 8;
        int y = this.height - 8;

        /* Draw labels for visual properties */
        this.drawString(this.fontRenderer, this.stringInvisible, x2, y - 65, 0xffcccccc);
        this.drawString(this.fontRenderer, this.stringInvincible, x2, y - 30, 0xffcccccc);

        /* Draw labels for meta properties */
        this.drawString(this.fontRenderer, this.stringName, x, y - 65, 0xffcccccc);
        this.drawString(this.fontRenderer, this.stringFilename, x, y - 30, this.filename.getText().isEmpty() ? 0xffff3355 : 0xffcccccc);

        if (this.replay.actor != null)
        {
            this.drawCenteredString(this.fontRenderer, this.stringAttached, 120 + (this.width - 120) / 2, 24, 0xffaaaaaa);
        }

        /* Draw entity in the center of the screen */
        int size = this.height / 4;

        y = this.height / 2 + size;
        x = x + (this.width - x) / 2;

        /* Draw morph */
        MorphCell cell = this.morphs.morphs.getSelected();

        if (cell != null)
        {
            int center = 120 + (this.width - 120) / 2;

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, -40);

            cell.current().morph.renderOnScreen(Minecraft.getMinecraft().player, center, this.height / 2 + this.height / 6, this.height / 4, 1.0F);

            GlStateManager.popMatrix();
        }
        else if (this.replay.morph != null)
        {
            int center = 120 + (this.width - 120) / 2;

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, -40);
            this.replay.morph.renderOnScreen(Minecraft.getMinecraft().player, center, this.height / 2 + this.height / 6, this.height / 4, 1.0F);
            GlStateManager.popMatrix();

            this.drawCenteredString(this.fontRenderer, this.replay.morph.name, center, 12, 0x888888);
        }

        /* Draw GUI elements */
        this.name.drawTextBox();
        this.filename.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
        this.morphs.drawScreen(mouseX, mouseY, partialTicks);
    }
}