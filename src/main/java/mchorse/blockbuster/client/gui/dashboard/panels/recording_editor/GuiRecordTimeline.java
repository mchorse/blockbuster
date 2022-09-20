package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor;

import mchorse.blockbuster.network.server.recording.actions.ServerHandlerActionsChange;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.actions.ActionRegistry;
import mchorse.blockbuster.recording.actions.MorphAction;
import mchorse.blockbuster_pack.morphs.SequencerMorph;
import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.ScrollArea;
import mchorse.mclib.client.gui.utils.ScrollDirection;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.ICopy;
import mchorse.mclib.utils.MathUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.utils.Animation;
import mchorse.metamorph.api.morphs.utils.IAnimationProvider;
import mchorse.metamorph.bodypart.BodyPart;
import mchorse.metamorph.bodypart.BodyPartManager;
import mchorse.metamorph.bodypart.IBodyPartProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class GuiRecordTimeline extends GuiElement
{
    public GuiRecordingEditorPanel panel;
    public ScrollArea scroll;
    public ScrollArea vertical;
    public Consumer<Action> callback;
    /**
     * Pointer to the current selected action.
     * Can be null
     */
    private Action current;
    /**
     * A frame in the list can be null if it does not contain any actions
     */
    private final List<List<Action>> selection = new ArrayList<>();
    /**
     * The tick where the selection begins
     */
    private int fromTick = -1;
    /**
     * The current selected tick. It should always be greater or equal to {@link #fromTick}
     */
    private Selection currentTick = new Selection(-1, -1);
    /**
     * What has been last left clicked
     */
    private Selection lastClicked = new Selection(-1, -1);

    public boolean lastDragging = false;
    public int lastX;
    public int lastY;
    private int lastLeftX;
    private int lastLeftY;
    public int lastH;
    public int lastV;
    /**
     * To render the actions at the mouse position correctly when dragging.
     * The default value is -1 when this has not been set
     */
    private int movingDx = -1;
    private int movingDy = -1;
    /**
     * The first scroll when drawing the selection area.
     * This is used to ensure continuity of the selection area when scrolling around.
     */
    private int areaScrollDx = -1;
    private int areaScrollDy = -1;

    /**
     * If this is true moving is possible
     */
    public boolean canMove;
    public boolean moving;
    /**
     * Whether the user is ready to select an area
     */
    private boolean canSelectArea;
    private boolean selectingArea;
    public int cursor = -1;
    private boolean preventMouseReleaseSelect = false;

    private int adaptiveMaxIndex;
    private final int itemHeight = 20;

    public GuiRecordTimeline(Minecraft mc, GuiRecordingEditorPanel panel)
    {
        super(mc);

        this.scroll = new ScrollArea(34);
        this.scroll.direction = ScrollDirection.HORIZONTAL;
        this.scroll.scrollSpeed = 34 * 2;
        this.vertical = new ScrollArea(this.itemHeight);
        this.vertical.direction = ScrollDirection.VERTICAL;
        this.panel = panel;
    }

    /**
     * @return the tick of the current selected action / frame
     */
    public int getCurrentTick()
    {
        return this.currentTick.tick;
    }

    public int getCurrentIndex()
    {
        return this.currentTick.index;
    }

    /**
     * This is used to determine whether you can escape out of the gui
     * @return true if this selection is not empty and if it does not contain only one empty frame.
     */
    public boolean isActive()
    {
        return !(this.selection.isEmpty() || (this.selection.size() == 1 && this.isFrameEmpty(this.selection.get(0))));
    }

    //public void reset()
    //{}

    @Override
    public void resize()
    {
        super.resize();

        this.scroll.copy(this.area);
        this.vertical.copy(this.area);
    }

    /**
     * Update with a new recording
     */
    public void reset()
    {
        if (this.panel.record != null)
        {
            this.fromTick = MathUtils.clamp(this.fromTick, 0, this.panel.record.actions.size() - 1);
            this.currentTick.tick = -1;
            this.currentTick.index = -1;
            this.current = null;
            this.selection.clear();

            this.scroll.setSize(this.panel.record.actions.size());
            this.scroll.clamp();

            this.recalculateVertical();
        }
    }

    public void recalculateVertical()
    {
        int max = 0;

        if (this.panel.record != null)
        {
            for (List<Action> actions : this.panel.record.actions)
            {
                if (actions != null && actions.size() > max)
                {
                    max = actions.size();
                }
            }

            max += 1;
        }

        this.vertical.setSize(max);
        this.vertical.clamp();
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        this.lastX = context.mouseX;
        this.lastY = context.mouseY;

        //TODO getIndex returns -2 when beyond end but returns -1 when beyond max index, but also -1 when behind beginning...
        //this brings issues with interpreting!
        int tick = this.scroll.getIndex(context.mouseX, context.mouseY);
        int index = this.vertical.getIndex(context.mouseX, context.mouseY);

        if (context.mouseButton == 0)
        {
            this.lastClicked = new Selection(tick, index);
            this.lastLeftX = this.lastX;
            this.lastLeftY = this.lastY;
        }

        if (context.mouseButton == 1)
        {
            if (this.moving || this.selectingArea)
            {
                this.preventMouseReleaseSelect = true;
            }

            this.moving = false;
            this.selectingArea = false;
        }

        if (context.mouseButton == 2 && this.area.isInside(context))
        {
            this.lastDragging = true;
            this.lastH = this.scroll.scroll;
            this.lastV = this.vertical.scroll;

            return true;
        }

        if (super.mouseClicked(context) || this.scroll.mouseClicked(context) || this.vertical.mouseClicked(context))
        {
            this.preventMouseReleaseSelect = true;

            return true;
        }

        if (this.scroll.isInside(context) && !this.moving && context.mouseButton == 0)
        {
            if (tick >= 0 && tick < this.panel.record.actions.size())
            {
                this.selectMouseClicked(tick, index);
            }
        }

        return false;
    }

    private void selectMouseClicked(int tick, int index)
    {
        if (this.isInSelection(tick, index))
        {
            if (GuiScreen.isCtrlKeyDown())
            {
                this.removeFromSelection(tick, index);
            }
            else if (GuiScreen.isShiftKeyDown())
            {
                this.canSelectArea = true;
            }
            else
            {
                this.awaitDragging();
            }
        }
        else
        {
            if (GuiScreen.isShiftKeyDown())
            {
                if (this.currentTick.tick != -1 && this.currentTick.index != -1)
                {
                    /* select a range from current tick to clicked tick */
                    List<List<Action>> actionRange = this.panel.record.getActions(tick, this.currentTick.tick, index, this.currentTick.index);

                    this.selectTick(tick);

                    int start = tick < this.currentTick.tick ? tick - this.fromTick : this.currentTick.tick - this.fromTick;

                    if (start >= 0)
                    {
                        for (int i = start, c = 0; i < this.selection.size() && c < actionRange.size(); i++, c++)
                        {
                            if (this.selection.get(i) == null && actionRange.get(c) != null)
                            {
                                this.selection.set(i, actionRange.get(c));
                            }
                            else if (this.selection.get(i) != null && actionRange.get(c) != null)
                            {
                                this.selection.get(i).removeAll(actionRange.get(c));

                                this.selection.get(i).addAll(actionRange.get(c));
                            }
                        }
                    }
                }

                this.selectCurrent(tick, index);
            }
            else if (GuiBase.isCtrlKeyDown())
            {
                if (this.panel.record.getAction(tick, index) != null)
                {
                    this.selectCurrent(tick, index);
                }
            }
            else if (this.panel.record.getAction(tick, index) != null)
            {
                this.selection.clear();
                this.fromTick = tick;

                this.selectCurrent(tick, index);
                this.awaitDragging();
            }
            else
            {
                this.canSelectArea = true;
            }
        }
    }

    private void awaitDragging()
    {
        this.canMove = true;
        this.moving = false;
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        super.mouseReleased(context);

        if (context.mouseButton == 0)
        {
            if (this.moving)
            {
                this.moveSelectionTo(this.scroll.getIndex(context.mouseX, context.mouseY));
            }
            else if (this.selectingArea)
            {
                if (!GuiScreen.isShiftKeyDown())
                {
                    this.selection.clear();
                }

                int fromSt = Math.min(this.lastClicked.tick, this.scroll.getIndex(context.mouseX, context.mouseY));
                int toSt = Math.max(this.lastClicked.tick, this.scroll.getIndex(context.mouseX, context.mouseY));
                int fromSi = MathUtils.clamp(Math.min(this.lastClicked.index, this.vertical.getIndex(context.mouseX, context.mouseY)), 0, Integer.MAX_VALUE);
                int toSi = MathUtils.clamp(Math.max(this.lastClicked.index, this.vertical.getIndex(context.mouseX, context.mouseY)), 0, Integer.MAX_VALUE);

                if (0 <= fromSt && fromSt < this.panel.record.actions.size())
                {
                    /* select a range from current tick to clicked tick */
                    List<List<Action>> actionRange = this.panel.record.getActions(fromSt, toSt, fromSi, toSi);

                    int startShift = this.trimSelectionBeginning(actionRange);
                    int endShift = this.trimSelectionEnd(actionRange);

                    this.selectTick(fromSt + startShift);
                    this.selectTick(toSt - endShift);

                    //TODO copy
                    int start = (fromSt + startShift) - this.fromTick;

                    for (int i = start, c = 0; c < actionRange.size(); i++, c++)
                    {
                        if (this.selection.get(i) == null && actionRange.get(c) != null)
                        {
                            this.selection.set(i, actionRange.get(c));
                        }
                        else if (this.selection.get(i) != null && actionRange.get(c) != null)
                        {
                            this.selection.get(i).removeAll(actionRange.get(c));

                            this.selection.get(i).addAll(actionRange.get(c));
                        }
                    }
                }
            }
            else if (!this.preventMouseReleaseSelect)
            {
                int tick = this.scroll.getIndex(context.mouseX, context.mouseY);
                int index = this.vertical.getIndex(context.mouseX, context.mouseY);

                if (index != -1 && 0 <= tick && tick < this.panel.record.actions.size())
                {
                    if (!GuiScreen.isShiftKeyDown() && !GuiScreen.isCtrlKeyDown()
                            && (this.isInSelection(tick, index) || this.panel.record.getAction(tick, index) == null))
                    {
                        this.selection.clear();
                        this.selectCurrent(tick, index);
                    }
                }
            }

            this.preventMouseReleaseSelect = false;

            this.canSelectArea = false;
            this.selectingArea = false;
            this.canMove = false;
            this.moving = false;
        }

        this.lastDragging = false;
        this.scroll.mouseReleased(context);
        this.vertical.mouseReleased(context);
    }

    @Override
    public boolean mouseScrolled(GuiContext context)
    {
        if (super.mouseScrolled(context))
        {
            return true;
        }

        boolean shift = GuiScreen.isShiftKeyDown();
        boolean alt = GuiScreen.isAltKeyDown();

        if (shift && !alt)
        {
            return this.vertical.mouseScroll(context);
        }
        else if (alt && !shift)
        {
            int scale = this.scroll.scrollItemSize;

            this.scroll.scrollItemSize = MathUtils.clamp(this.scroll.scrollItemSize + (int) Math.copySign(2, context.mouseWheel), 6, 50);
            this.scroll.setSize(this.panel.record.actions.size());
            this.scroll.clamp();

            if (this.scroll.scrollItemSize != scale)
            {
                int value = this.scroll.scroll + (context.mouseX - this.area.x);

                this.scroll.scroll = (int) ((value - (value - this.scroll.scroll) * (scale / (float) this.scroll.scrollItemSize)) * (this.scroll.scrollItemSize / (float) scale));
            }

            return true;
        }

        return this.scroll.mouseScroll(context);
    }


    /**
     * @param tick
     * @param index
     * @return true if the action at the tick and index is selected.
     *         False if tick is outside of selection, if there is no action at the tick and index
     *         or if the action is not in the selection.
     */
    private boolean isInSelection(int tick, int index)
    {
        if (tick < this.fromTick || this.selection.isEmpty() || this.fromTick == -1)
        {
            return false;
        }

        Action action = this.panel.record.getAction(tick, index);
        int t = tick - this.fromTick;

        if (action == null || t >= this.selection.size())
        {
            return false;
        }

        return this.selection.get(t) != null ? this.selection.get(t).contains(action) : false;
    }

    /**
     * Remove the given tick and index from the selection
     * and update {@link #currentTick} and {@link #fromTick} if necessary.
     * @param tick
     * @param index
     */
    private void removeFromSelection(int tick, int index)
    {
        if (tick < this.fromTick)
        {
            return;
        }

        int t = tick - fromTick;

        Action remove = this.panel.record.getAction(tick, index);

        if (remove == null || t >= this.selection.size())
        {
            return;
        }

        if (this.selection.get(t) != null)
        {
            this.selection.get(t).remove(remove);
        }

        if (t == this.selection.size() - 1)
        {
            this.trimSelectionEnd(this.selection);
        }
        else if (t == 0)
        {
            this.fromTick += this.trimSelectionBeginning(this.selection);
        }
        else if (this.selection.get(t).isEmpty())
        {
            this.selection.set(t, null);
        }

        if (this.current == remove)
        {
            this.selectCurrent(-1, -1);
        }
    }

    /**
     * Clear the selection, but keep current tick (without selecting a current action)
     */
    public void deselect()
    {
        this.selection.clear();
        this.selectCurrent(this.currentTick.tick, -1);
    }

    /**
     * Trims the selection so that the first start frame is not empty or null.
     * This method will remove entry from the given list, but it will not remove entries from the list entries.
     * @param selection
     * @return the shift from the beginning
     */
    private int trimSelectionBeginning(List<List<Action>> selection)
    {
        int shift = 0;

        for (int start = 0; start < selection.size(); start++)
        {
            if (this.isFrameEmpty(selection.get(start)))
            {
                selection.remove(start);

                start--;
                shift++;
            }
            else
            {
                break;
            }
        }

        return shift;
    }

    private boolean isFrameEmpty(List<Action> frame)
    {
        boolean empty = true;

        if (frame != null && !frame.isEmpty())
        {
            for (int a = 0; a < frame.size(); a++)
            {
                if (frame.get(a) != null)
                {
                    empty = false;
                }
            }
        }

        return empty;
    }

    /**
     * Trims the selection so that the first end frame that is not empty or null.
     * This method will remove entry from the given list, but it will not remove entries from the list entries.
     * @param selection
     * @return the shift from the end
     */
    private int trimSelectionEnd(List<List<Action>> selection)
    {
        int shift = 0;

        for (int end = selection.size() - 1; end >= 0; end--)
        {
            if (this.isFrameEmpty(selection.get(end)))
            {
                selection.remove(end);

                shift++;
            }
            else
            {
                break;
            }
        }

        return shift;
    }


    /**
     * Sets current action and {@link #currentTick} and updates the selection.
     * This method also updates the GUI action panel of the recording editor panel.
     * If the selection was empty or if {@link #fromTick} was -1 before: {@link #fromTick} will be set to the provided tick
     * @param tick
     * @param index
     */
    public void selectCurrent(int tick, int index)
    {
        this.panel.save();

        Action selected = this.panel.record.getAction(tick, index);

        if (selected != null)
        {
            if (this.fromTick == -1 || this.selection.isEmpty())
            {
                this.selection.clear();

                this.fromTick = tick;
                this.selection.add(null);
            }
            else
            {
                this.selectTick(tick);
            }

            int t = tick - this.fromTick;

            if (this.selection.get(t) != null)
            {
                if (!this.selection.get(t).contains(selected))
                {
                    this.selection.get(t).add(selected);
                }
            }
            else
            {
                this.selection.set(t, new ArrayList<>(Arrays.asList(selected)));
            }
        }
        else if (this.fromTick == -1 || this.selection.isEmpty())
        {
            this.selection.clear();

            this.fromTick = tick;
            this.selection.add(null);
        }

        this.current = selected;
        this.currentTick.tick = tick;
        this.currentTick.index = index;

        this.panel.setDelegate(this.panel.getActionPanel(this.current));
    }

    /**
     * Add the given tick to the selection and close gaps to previous selection.
     * The provided tick will be greater or equal to {@link #fromTick} after this operation.
     * @param tick
     */
    private void selectTick(int tick)
    {
        int t = tick - this.fromTick;
        int start = (tick < this.fromTick) ? 0 : ((t >= this.selection.size()) ? this.selection.size() : 0);
        int end = (tick < this.fromTick) ? this.fromTick - tick : ((t >= this.selection.size()) ? t + 1: 0);

        for (int i = start; i < end; i++)
        {
            this.selection.add(i, null);
        }

        if (tick < this.fromTick)
        {
            this.fromTick = tick;
        }
    }

    private void moveSelectionTo(int tick)
    {
        if (tick >= this.panel.record.actions.size())
        {
            return;
        }

        tick = tick + this.fromTick - this.lastClicked.tick;

        List<List<Action>> selectionCopy = new ArrayList<>(this.selection);

        int start = this.trimSelectionBeginning(selectionCopy);
        this.trimSelectionEnd(selectionCopy);

        if (selectionCopy.isEmpty())
        {
            return;
        }

        Action current = this.current;
        int dT = this.currentTick.tick - this.fromTick;
        this.removeActions();
        this.selection.clear();
        this.selection.addAll(selectionCopy);

        tick += start;

        this.panel.record.addActionCollection(tick, this.selection);
        this.recalculateVertical();

        this.fromTick = tick;
        this.currentTick.tick = tick + dT - start;
        this.currentTick.index = this.panel.record.getActionIndex(this.currentTick.tick, current);
        this.preventMouseReleaseSelect = true;

        this.selectCurrent(this.currentTick.tick, this.currentTick.index);
        ServerHandlerActionsChange.addActions(this.selection, this.panel.record.filename, tick);
    }

    public void cutAction()
    {
        if (this.panel.actionEditor.delegate == null)
        {
            return;
        }

        this.copy();
        this.removeActions();
    }

    public void copy()
    {
        if (this.panel.actionEditor.delegate == null)
        {
            return;
        }

        Action action = this.panel.actionEditor.delegate.action;
        NBTTagCompound tag = new NBTTagCompound();

        tag.setString("ActionType", ActionRegistry.NAME_TO_CLASS.inverse().get(action.getClass()));
        action.toNBT(tag);
        this.panel.buffer = tag;
    }

    public void pasteAction()
    {
        if (this.panel.buffer == null)
        {
            return;
        }

        int tick = this.fromTick;
        int index = this.fromTick;

        Action action = null;

        try
        {
            action = ActionRegistry.fromName(this.panel.buffer.getString("ActionType"));
            action.fromNBT(this.panel.buffer);
        }
        catch (Exception e)
        {}

        if (action == null)
        {
            return;
        }

        this.panel.record.addAction(tick, index, action);
        this.recalculateVertical();
        this.fromTick = this.panel.record.actions.get(tick).size() - 1;
        //this.panel.selectAction(action);
        //Dispatcher.sendToServer(new PacketActionsOverwrite(this.panel.record.filename, tick, index, action, true));
    }

    public void createAction(String str)
    {
        if (!ActionRegistry.NAME_TO_CLASS.containsKey(str))
        {
            return;
        }

        try
        {
            Action action = ActionRegistry.fromName(str);
            int tick = this.currentTick.tick;
            int index = this.currentTick.index;

            this.panel.record.addAction(tick, index, action);
            this.panel.list.setVisible(false);
            this.selection.clear();
            this.selectCurrent(tick, this.panel.record.getActionIndex(tick, action));
            this.recalculateVertical();

            ServerHandlerActionsChange.addAction(action, this.panel.record.filename, tick, index);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void dupeAction()
    {
        int tick = this.fromTick;
        int index = this.fromTick;

        Action action = this.panel.record.getAction(tick, index);

        if (action == null)
        {
            return;
        }

        try
        {
            Action newAction = ActionRegistry.fromType(ActionRegistry.getType(action));
            NBTTagCompound tag = new NBTTagCompound();
            action.toNBT(tag);
            newAction.fromNBT(tag);
            action = newAction;
        }
        catch (Exception e)
        {}

        this.panel.record.addAction(tick, index, action);
        this.recalculateVertical();
        //Dispatcher.sendToServer(new PacketActionsOverwrite(this.panel.record.filename, tick, index, action, true));
    }

    /**
     * Remove selected actions, send to server, clear selection and set current to none
     */
    public void removeActions()
    {
        if (this.fromTick == -1 || this.selection.isEmpty())
        {
            return;
        }

        List<List<Boolean>> deletionMask = this.panel.record.getActionsMask(this.fromTick, this.selection);
        this.panel.record.removeActions(this.fromTick, this.selection);
        ServerHandlerActionsChange.deleteActions(this.panel.record.filename, this.fromTick, deletionMask);

        this.deselect();
        this.recalculateVertical();
    }

    @Override
    public void draw(GuiContext context)
    {
        if (this.panel.record == null)
        {
            return;
        }

        int mouseX = context.mouseX;
        int mouseY = context.mouseY;
        int count = this.panel.record.actions.size();

        if (this.lastDragging)
        {
            this.scroll.scroll = this.lastH + (this.lastX - mouseX);
            this.scroll.clamp();
            this.vertical.scroll = this.lastV + (this.lastY - mouseY);
            this.vertical.clamp();
        }

        if (!this.moving && (Math.abs(mouseX - this.lastX) > 2 || Math.abs(mouseY - this.lastY) > 2))
        {
            if (this.canMove)
            {
                this.moving = true;
            }

            if (this.canSelectArea)
            {
                this.selectingArea = true;
            }
        }

        this.scroll.drag(mouseX, mouseY);
        this.vertical.drag(mouseX, mouseY);
        this.scroll.draw(ColorUtils.HALF_BLACK);

        Gui.drawRect(this.area.ex(), this.area.y, this.area.ex() + 20, this.area.ey(), 0xff222222);
        Gui.drawRect(this.area.x - 20, this.area.y, this.area.x, this.area.ey(), 0xff222222);
        GuiDraw.drawHorizontalGradientRect(this.area.ex() - 8, this.area.y, this.area.ex(), this.area.ey(), 0, ColorUtils.HALF_BLACK, 0);
        GuiDraw.drawHorizontalGradientRect(this.area.x, this.area.y, this.area.x + 8, this.area.ey(), ColorUtils.HALF_BLACK, 0, 0);

        int max = this.area.x + this.scroll.scrollItemSize * count;

        if (max < this.area.ex())
        {
            Gui.drawRect(max, this.area.y, this.area.ex(), this.area.ey(), 0xaa000000);
        }

        GuiDraw.scissor(this.area.x, this.area.y, this.area.w, this.area.h, context);

        int w = this.scroll.scrollItemSize;
        int index = this.scroll.scroll / w;
        int diff = index;

        index -= this.adaptiveMaxIndex;
        index = index < 0 ? 0 : index;
        diff = diff - index;

        this.adaptiveMaxIndex = 0;

        for (int i = index, c = i + this.area.w / w + 2 + diff; i < c; i++)
        {
            int x = this.scroll.x - this.scroll.scroll + i * w;

            if (i < count)
            {
                Gui.drawRect(x, this.scroll.y, x + 1, this.scroll.ey(), 0x22ffffff);
            }

            int toTick = this.selection.isEmpty() ? this.fromTick : this.fromTick + this.selection.size() - 1;

            if (this.fromTick <= i && i <= toTick)
            {
                Gui.drawRect(x, this.scroll.y, x + w + 1, this.scroll.ey(), 0x440088ff);
            }

            if (i >= 0 && i < count)
            {
                List<Action> actions = this.panel.record.actions.get(i);

                if (actions != null)
                {
                    int j = 0;

                    for (Action action : actions)
                    {
                        if (this.moving && this.isInSelection(i, j))
                        {
                            j++;

                            continue;
                        }

                        int y = this.scroll.y + j * this.itemHeight - this.vertical.scroll;

                        int fromSt = Math.min(this.lastClicked.tick, this.scroll.getIndex(mouseX, mouseY));
                        int toSt = Math.max(this.lastClicked.tick, this.scroll.getIndex(mouseX, mouseY));
                        int fromSi = Math.min(this.lastClicked.index, this.vertical.getIndex(mouseX, mouseY));
                        int toSi = Math.max(this.lastClicked.index, this.vertical.getIndex(mouseX, mouseY));

                        boolean selected;

                        if (this.selectingArea)
                        {
                            selected = fromSt <= i && i <= toSt && fromSi <= j && j <= toSi;

                            if (GuiScreen.isShiftKeyDown())
                            {
                                selected = selected || this.isInSelection(i, j);
                            }
                        }
                        else
                        {
                            selected = this.isInSelection(i, j);
                        }

                        this.drawAction(action, String.valueOf(j), x, y, selected);

                        j++;
                    }
                }
            }
        }

        for (int i = index, c = i + this.area.w / w + 2 + diff; i < c; i++)
        {
            if (i % 5 == 0 && i < count && i != this.cursor)
            {
                int x = this.scroll.x - this.scroll.scroll + i * w;
                int y = this.scroll.ey() - 12;

                String str = String.valueOf(i);

                this.drawGradientRect(x + 1, y - 6, x + w, y + 12, 0, ColorUtils.HALF_BLACK);
                this.font.drawStringWithShadow(str, x + (this.scroll.scrollItemSize - this.font.getStringWidth(str) + 2) / 2, y, 0xffffff);
            }
        }

        this.scroll.drawScrollbar();
        this.vertical.drawScrollbar();

        /* Draw cursor (tick indicator) */
        if (this.cursor >= 0 && this.cursor < this.panel.record.actions.size())
        {
            int x = this.scroll.x - this.scroll.scroll + this.cursor * w;
            int cursorX = x + 2;

            String label = this.cursor + "/" + this.panel.record.actions.size();
            int width = this.font.getStringWidth(label);
            int height = 2 + this.font.FONT_HEIGHT;
            int offsetY = this.scroll.ey() - height;

            if (cursorX + width + 4 > this.scroll.ex())
            {
                cursorX -= width + 4 + 2;
            }

            Gui.drawRect(x, this.scroll.y, x + 2, this.scroll.ey(), 0xff57f52a);
            Gui.drawRect(cursorX, offsetY, cursorX + width + 4, offsetY + height, 0xaa57f52a);

            this.font.drawStringWithShadow(label, cursorX + 2, offsetY + 2, 0xffffff);
        }

        String label = this.panel.record.filename;

        GuiDraw.drawTextBackground(this.font, label, this.area.ex() - this.font.getStringWidth(label) - 5, this.area.ey() - 13, 0xffffff, 0xaa000000 + McLib.primaryColor.get());

        GuiDraw.unscissor(context);

        if (this.moving)
        {
            int x = mouseX;
            int y = mouseY;

            int posX = w * (this.lastClicked.tick) + this.scroll.x - this.scroll.scroll;
            int posY = this.itemHeight * this.lastClicked.index + this.scroll.y - this.vertical.scroll;

            if (this.movingDx == -1)
            {
                this.movingDx = mouseX - posX;
            }

            if (this.movingDy == -1)
            {
                this.movingDy = mouseY - posY;
            }

            x -= this.movingDx - w * (this.fromTick - this.lastClicked.tick);
            y -= this.movingDy + this.itemHeight * this.lastClicked.index;

            int y0 = y;

            for (int tick = this.fromTick; tick < this.panel.record.actions.size(); tick++)
            {
                List<Action> frame = this.panel.record.actions.get(tick);

                if (frame != null)
                {
                    for (int i = 0; i < frame.size(); i++)
                    {
                        if (this.isInSelection(tick, i))
                        {
                            this.drawAction(frame.get(i), String.valueOf(i), x, y, true);
                        }

                        y += this.itemHeight;
                    }
                }

                y = y0;
                x += w;
            }
        }
        else
        {
            this.movingDx = -1;
            this.movingDy = -1;
        }

        if (this.selectingArea)
        {
            if (this.areaScrollDx == -1)
            {
                this.areaScrollDx = this.scroll.scroll;
            }

            if (this.areaScrollDy == -1)
            {
                this.areaScrollDy = this.vertical.scroll;
            }

            Gui.drawRect(this.lastLeftX - (this.scroll.scroll - this.areaScrollDx), this.lastLeftY - (this.vertical.scroll - this.areaScrollDy), mouseX, mouseY, 0x440088FF);
        }
        else
        {
            this.areaScrollDy = -1;
            this.areaScrollDx = -1;
        }

        super.draw(context);

        this.cursor = -1;
    }

    private void drawAction(Action action, String label, int x, int y, boolean selected)
    {
        int w = this.scroll.scrollItemSize;
        float hue = ((ActionRegistry.getType(action) - 1) / ((float) ActionRegistry.getMaxID()));
        int color = MathHelper.hsvToRGB(hue, 1F, 1F);
        int offset = this.scroll.scrollItemSize < 18 ? (this.scroll.scrollItemSize - this.font.getStringWidth(label)) / 2 : 6;

        this.drawAnimationLength(action, x, y, color, selected);

        Gui.drawRect(x, y, x + w, y + this.itemHeight, color + ColorUtils.HALF_BLACK);
        this.font.drawStringWithShadow(label, x + offset, y + 6, 0xffffff);

        if (selected)
        {
            /* get complementary color, but ignore purple and blue colors - they don't pop enough */
            float hueSelected = MathUtils.clamp((hue - 0.5F) < 0 ? 0.5F + hue : hue - 0.5F, 0, 0.5F);
            int c = action == this.current ? (new Color(MathHelper.hsvToRGB(hueSelected, 0.5F, 1F), false)).getRGBAColor() : 0xffffffff;
            int border = action == this.current ? 2 : 1;
            GuiDraw.drawOutline(x, y, x + w, y + this.itemHeight, c, border);
        }
    }

    private void drawAnimationLength(Action action, int x, int y, int color, boolean selected)
    {
        if (action instanceof MorphAction)
        {
            MorphAction morphAction = (MorphAction) action;
            int ticks = this.getLength(morphAction.morph);

            if (ticks > 1)
            {
                ticks -= 1;

                int offset = x + this.scroll.scrollItemSize;

                Gui.drawRect(offset, y + 8, offset + ticks * this.scroll.scrollItemSize, y + 12, selected ? 0xffffffff : color + 0x33000000);
                Gui.drawRect(offset + ticks * this.scroll.scrollItemSize - 1, y, offset + ticks * this.scroll.scrollItemSize, y + this.itemHeight, selected ? 0xffffffff : 0xff000000 + color);
            }

            this.adaptiveMaxIndex = Math.max(ticks, this.adaptiveMaxIndex);
        }
    }

    private int getLength(AbstractMorph morph)
    {
        int ticks = 0;

        if (morph instanceof IAnimationProvider)
        {
            Animation animation = ((IAnimationProvider) morph).getAnimation();

            if (animation.animates)
            {
                ticks = animation.duration;
            }
        }
        else if (morph instanceof SequencerMorph)
        {
            SequencerMorph sequencerMorph = (SequencerMorph) morph;

            ticks = (int) sequencerMorph.getDuration();
        }

        if (morph instanceof IBodyPartProvider)
        {
            BodyPartManager manager = ((IBodyPartProvider) morph).getBodyPart();

            for (BodyPart part : manager.parts)
            {
                if (!part.morph.isEmpty() && part.limb != null && !part.limb.isEmpty())
                {
                    ticks = Math.max(ticks, this.getLength(part.morph.get()));
                }
            }
        }

        return ticks;
    }

    public static class Selection implements ICopy<Selection>
    {
        private int tick;
        private int index;

        public Selection(int tick, int index)
        {
            this.set(tick, index);
        }

        public void set(int tick, int index)
        {
            this.tick = tick;
            this.index = index;
        }

        public int getTick()
        {
            return this.tick;
        }

        public int getIndex()
        {
            return this.index;
        }

        @Override
        public Selection copy()
        {
            Selection copy = new Selection(this.tick, this.index);

            return copy;
        }
    }
}