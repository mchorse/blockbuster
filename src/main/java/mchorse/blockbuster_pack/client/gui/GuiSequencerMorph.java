package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster_pack.morphs.SequencerMorph;
import mchorse.blockbuster_pack.morphs.SequencerMorph.SequenceEntry;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.framework.tooltips.LabelTooltip;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import mchorse.mclib.utils.DummyEntity;
import mchorse.mclib.utils.MathUtils;
import mchorse.metamorph.api.Morph;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.utils.IAnimationProvider;
import mchorse.metamorph.client.gui.creative.GuiMorphRenderer;
import mchorse.metamorph.client.gui.creative.GuiNestedEdit;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public class GuiSequencerMorph extends GuiAbstractMorph<SequencerMorph>
{
    public GuiSequencerMorphPanel general;

    public GuiSequencerMorph(Minecraft mc)
    {
        super(mc);

        this.defaultPanel = this.general = new GuiSequencerMorphPanel(mc, this);
        this.registerPanel(this.general, IKey.lang("blockbuster.morph.sequencer"), Icons.GEAR);
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof SequencerMorph;
    }

    /**
     * Sequencer morph panel 
     */
    public static class GuiSequencerMorphPanel extends GuiMorphPanel<SequencerMorph, GuiSequencerMorph>
    {
        public GuiElement elements;

        private GuiListElement<SequenceEntry> list;
        private GuiButtonElement addPart;
        private GuiButtonElement removePart;

        private GuiNestedEdit pick;
        private GuiTrackpadElement duration;
        private GuiTrackpadElement random;
        private GuiToggleElement setDuration;
        private GuiToggleElement reverse;
        private GuiToggleElement randomOrder;

        public SequenceEntry entry;

        /* Playback preview */
        public GuiTrackpadElement preview;
        public GuiIconElement plause;
        public GuiIconElement stop;

        private Morph previewMorph = new Morph();
        private DummyEntity dummy = new DummyEntity(Minecraft.getMinecraft().world);
        private boolean playing;
        private int tick;
        private long lastTick;

        public GuiSequencerMorphPanel(Minecraft mc, GuiSequencerMorph editor)
        {
            super(mc, editor);

            this.elements = new GuiElement(mc);
            this.elements.flex().relative(this).xy(1F, 1F).w(130).anchor(1F, 1F).column(5).vertical().stretch().padding(10);

            this.list = new GuiSequenceEntryList(mc, (entry) -> this.select(entry.get(0)));
            this.list.sorting().background();
            this.addPart = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.add"), (b) ->
            {
                SequenceEntry current = this.list.getCurrentFirst();
                SequenceEntry entry = new SequenceEntry(current == null ? null : MorphUtils.copy(current.morph));

                if (current != null)
                {
                    entry.duration = current.duration;
                    entry.random = current.random;
                }

                this.list.getList().add(entry);
                this.list.setIndex(this.list.getList().size() - 1);
                this.select(entry);
                this.list.update();

                this.stopPlayback();
            });

            this.removePart = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.remove"), (b) ->
            {
                if (!this.list.isDeselected())
                {
                    int index = this.list.getIndex();

                    this.list.getList().remove(index);
                    this.list.setIndex(index - 1);

                    this.select(this.list.getCurrentFirst());
                    this.list.update();

                    this.stopPlayback();
                }
            });

            this.pick = new GuiNestedEdit(mc, (editing) ->
            {
                if (this.entry == null)
                {
                    return;
                }

                SequenceEntry entry = this.entry;

                this.editor.morphs.nestEdit(entry.morph, editing, (morph) ->
                {
                    entry.morph = MorphUtils.copy(morph);
                });
            });

            this.duration = new GuiTrackpadElement(mc, (value) ->
            {
                if (this.entry != null)
                {
                    this.entry.duration = value.floatValue();
                    this.stopPlayback();
                }
            });
            this.duration.tooltip(IKey.lang("blockbuster.gui.sequencer.duration"));
            this.duration.limit(0, Float.MAX_VALUE);

            this.random = new GuiTrackpadElement(mc, (value) ->
            {
                if (this.entry != null)
                {
                    this.entry.random = value.floatValue();
                    this.stopPlayback();
                }
            });
            this.random.tooltip(IKey.lang("blockbuster.gui.sequencer.random"));
            this.random.limit(0, Float.MAX_VALUE);
            this.setDuration = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.sequencer.set_duration"), (b) -> this.entry.setDuration = b.isToggled());
            this.setDuration.tooltip(IKey.lang("blockbuster.gui.sequencer.set_duration_tooltip"), Direction.TOP);

            this.reverse = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.sequencer.reverse"), false, (b) ->
            {
                this.morph.reverse = b.isToggled();
                this.stopPlayback();
            });

            this.randomOrder = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.sequencer.random_order"), false, (b) ->
            {
                this.morph.isRandom = b.isToggled();
                this.stopPlayback();
            });

            this.addPart.flex().relative(this.area).set(10, 10, 50, 20);
            this.removePart.flex().relative(this.addPart.resizer()).set(55, 0, 50, 20);
            this.list.flex().relative(this.area).set(10, 50, 105, 0).hTo(this.reverse.area, -5);
            this.randomOrder.flex().relative(this).x(10).y(1F, -24).w(105);
            this.reverse.flex().relative(this.randomOrder).y(-1F, -5).w(1F);

            /* Playback preview code */
            this.preview = new GuiTrackpadElement(mc, (value) -> this.previewTick(value.intValue()));
            this.preview.limit(0).integer().tooltip(IKey.lang("blockbuster.gui.sequencer.preview_tick"));
            this.plause = new GuiIconElement(mc, Icons.PLAY, (b) -> this.togglePlay());
            this.plause.tooltip(IKey.lang("blockbuster.gui.sequencer.keys.toggle")).flex().wh(16, 20);
            this.stop = new GuiIconElement(mc, Icons.STOP, (b) -> this.stopPlayback());
            this.stop.tooltip(IKey.lang("blockbuster.gui.sequencer.keys.stop")).flex().wh(16, 20);

            GuiElement previewBar = new GuiElement(mc);

            previewBar.flex().relative(this).x(130).y(10).wTo(this.elements.flex()).h(20).row(5).preferred(1);
            previewBar.add(this.plause, this.preview, this.stop);

            this.elements.add(this.pick, this.duration, this.random, this.setDuration);
            this.add(this.addPart, this.removePart, this.randomOrder, this.reverse, this.list, this.elements, previewBar);

            this.keys().register(((LabelTooltip) this.plause.tooltip).label, Keyboard.KEY_SPACE, () -> this.plause.clickItself(GuiBase.getCurrent()))
                .held(Keyboard.KEY_LSHIFT)
                .category(GuiAbstractMorph.KEY_CATEGORY);
            this.keys().register(((LabelTooltip) this.stop.tooltip).label, Keyboard.KEY_SPACE, () -> this.stop.clickItself(GuiBase.getCurrent()))
                .held(Keyboard.KEY_LMENU)
                .category(GuiAbstractMorph.KEY_CATEGORY);
        }

        private void select(SequenceEntry entry)
        {
            this.entry = entry;

            if (entry != null)
            {
                this.pick.setMorph(entry.morph);
                this.duration.setValue(entry.duration);
                this.random.setValue(entry.random);
                this.setDuration.toggled(entry.setDuration);

                ((GuiMorphRenderer) this.editor.renderer).morph = entry.morph;

                if (entry.morph instanceof IAnimationProvider)
                {
                    ((IAnimationProvider) entry.morph).getAnimation().reset();
                }
            }
            else
            {
                ((GuiMorphRenderer) this.editor.renderer).morph = null;
            }

            this.elements.setVisible(entry != null);
        }

        /* Playback preview */

        private void previewTick(int tick)
        {
            this.tick = tick;

            if (this.playing)
            {
                this.togglePlay();
            }

            this.updatePreviewMorph();
        }

        private void togglePlay()
        {
            this.playing = !this.playing;
            this.updatePlauseButton();
        }

        private void updatePlauseButton()
        {
            this.plause.both(this.playing ? Icons.PAUSE : Icons.PLAY);
        }

        private void stopPlayback()
        {
            this.tick = 0;
            this.playing = false;
            this.updatePlauseButton();
            this.preview.setValue(0);

            if (this.entry != null)
            {
                ((GuiMorphRenderer) this.editor.renderer).morph = this.entry.morph;

                if (entry.morph instanceof IAnimationProvider)
                {
                    ((IAnimationProvider) entry.morph).getAnimation().reset();
                }
            }
        }

        private void updatePreviewMorph()
        {
            SequencerMorph.FoundMorph morph = this.morph.getMorphAt(this.tick);

            if (morph != null)
            {
                AbstractMorph current = morph.getCurrentMorph();
                AbstractMorph previous = morph.getPreviousMorph();

                current = MorphUtils.copy(current);
                previous = MorphUtils.copy(previous);

                int prevDuration = (int) morph.getPreviousDuration();

                MorphUtils.pause(previous, null, prevDuration);
                MorphUtils.pause(current, previous, this.tick - (int) (morph.totalDuration - morph.getCurrentDuration()));

                this.previewMorph.setDirect(current);
                ((GuiMorphRenderer) this.editor.renderer).morph = this.previewMorph.get();
            }
        }

        private void resetPlayback()
        {
            this.stopPlayback();
            this.updatePreviewMorph();
        }

        @Override
        public void fillData(SequencerMorph morph)
        {
            super.fillData(morph);

            this.resetPlayback();

            this.list.setList(morph.morphs);
            this.list.setIndex(0);
            this.select(this.list.getCurrentFirst());

            this.reverse.toggled(morph.reverse);
            this.randomOrder.toggled(morph.isRandom);
        }

        @Override
        public void draw(GuiContext context)
        {
            this.updateLogic(context);
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.sequencer.morphs"), this.list.area.x, this.list.area.y - 12, 0xffffff);

            super.draw(context);
        }

        private void updateLogic(GuiContext context)
        {
            if (this.playing)
            {
                long i = MathUtils.clamp(context.tick - this.lastTick, 0, 10);

                while (i > 0L)
                {
                    this.updatePreviewMorph();

                    AbstractMorph morph = this.previewMorph.get();

                    if (morph != null)
                    {
                        this.dummy.ticksExisted = (int) context.tick;
                    }

                    this.tick += 1;
                    this.preview.setValue(this.tick);

                    --i;
                }
            }

            this.lastTick = context.tick;
        }

        @Override
        public void fromNBT(NBTTagCompound tag)
        {
            super.fromNBT(tag);

            this.list.setIndex(tag.getInteger("Index"));

            SequenceEntry entry = this.list.getCurrentFirst();

            if (entry != null)
            {
                this.select(entry);
            }
        }

        @Override
        public NBTTagCompound toNBT()
        {
            NBTTagCompound tag = super.toNBT();

            tag.setInteger("Index", this.list.getIndex());

            return tag;
        }
    }

    /**
     * List that shows up the sequencer entries 
     */
    public static class GuiSequenceEntryList extends GuiListElement<SequenceEntry>
    {
        public static IKey ticks = IKey.lang("blockbuster.gui.sequencer.ticks");

        public GuiSequenceEntryList(Minecraft mc, Consumer<List<SequenceEntry>> callback)
        {
            super(mc, callback);

            this.scroll.scrollItemSize = 24;
        }

        @Override
        protected void drawElementPart(SequenceEntry element, int i, int x, int y, boolean hover, boolean selected)
        {
            GuiContext context = GuiBase.getCurrent();

            if (element.morph != null)
            {
                GuiDraw.scissor(x, y, this.scroll.w, this.scroll.scrollItemSize, context);
                element.morph.renderOnScreen(this.mc.player, x + this.scroll.w - 16, y + 30, 20, 1);
                GuiDraw.unscissor(context);
            }

            super.drawElementPart(element, i, x, y, hover, selected);
        }

        @Override
        protected String elementToString(SequenceEntry element)
        {
            String title = element.duration + " " + ticks.get();

            if (element.morph == null)
            {
                title += " " + I18n.format("blockbuster.gui.sequencer.no_morph");
            }

            return title;
        }
    }
}