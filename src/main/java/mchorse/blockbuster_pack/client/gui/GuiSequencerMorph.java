package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster_pack.morphs.SequencerMorph;
import mchorse.blockbuster_pack.morphs.SequencerMorph.SequenceEntry;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.framework.tooltips.LabelTooltip;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import mchorse.mclib.utils.DummyEntity;
import mchorse.mclib.utils.MathUtils;
import mchorse.metamorph.api.Morph;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.utils.Animation;
import mchorse.metamorph.api.morphs.utils.IAnimationProvider;
import mchorse.metamorph.api.morphs.utils.IMorphGenerator;
import mchorse.metamorph.bodypart.BodyPart;
import mchorse.metamorph.bodypart.IBodyPartProvider;
import mchorse.metamorph.client.gui.creative.GuiMorphRenderer;
import mchorse.metamorph.client.gui.creative.GuiNestedEdit;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
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

    @Override
    protected GuiModelRenderer createMorphRenderer(Minecraft mc)
    {
        return new GuiSequencerMorphRenderer(mc);
    }

    @Override
    public int getCurrentTick()
    {
        return this.general.getCurrentTick();
    }

    /**
     * Sequencer morph panel 
     */
    public static class GuiSequencerMorphPanel extends GuiMorphPanel<SequencerMorph, GuiSequencerMorph>
    {
        public GuiElement elements;
        public GuiElement elementsTop;

        private GuiListElement<SequenceEntry> list;
        private GuiButtonElement addPart;
        private GuiButtonElement removePart;

        private GuiTrackpadElement loop;
        private GuiTrackpadElement offsetX;
        private GuiTrackpadElement offsetY;
        private GuiTrackpadElement offsetZ;
        private GuiTrackpadElement offsetCount;
        
        private GuiNestedEdit pick;
        private GuiTrackpadElement duration;
        private GuiTrackpadElement random;
        private GuiToggleElement setDuration;
        private GuiToggleElement endPoint;
        private GuiToggleElement reverse;
        private GuiToggleElement randomOrder;
        private GuiToggleElement trulyRandomOrder;

        public SequenceEntry entry;

        /* Playback preview */
        public GuiTrackpadElement preview;
        public GuiIconElement plause;
        public GuiIconElement stop;
        public GuiElement previewBar;
        
        public GuiButtonElement generateMorph;

        public GuiSequencerMorphRenderer previewRenderer;

        public GuiSequencerMorphPanel(Minecraft mc, GuiSequencerMorph editor)
        {
            super(mc, editor);

            this.elements = new GuiElement(mc);
            this.elements.flex().relative(this).xy(1F, 1F).w(130).anchor(1F, 1F).column(5).vertical().stretch().padding(10);
            
            this.elementsTop = new GuiElement(mc);
            this.elementsTop.flex().relative(this).xy(1F, 0F).w(130).anchor(1F, 0F).column(5).vertical().stretch().padding(10);

            this.list = new GuiSequenceEntryList(mc, (entry) -> 
            {
                this.select(entry.get(0));
                
                this.stopPlayback();
            });
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

                if (GuiScreen.isCtrlKeyDown())
                {
                    this.list.getList().add(this.list.getIndex() + 1, entry);
                    this.list.setIndex(this.list.getIndex() + 1);
                }
                else
                {
                    this.list.getList().add(entry);
                    this.list.setIndex(this.list.getList().size() - 1);
                }

                this.select(entry);
                this.list.update();

                this.stopPlayback();
            });
            this.addPart.tooltip(IKey.lang("blockbuster.gui.sequencer.add_part_tooltip"));

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
            
            this.loop = new GuiTrackpadElement(mc, (value) -> 
            {
                this.morph.loop = value.intValue();
                this.stopPlayback();
            });
            this.loop.tooltip(IKey.lang("blockbuster.gui.sequencer.loop_tooltip"));
            this.loop.integer().limit(0);

            this.offsetX = new GuiTrackpadElement(mc, (value) -> 
            {
                this.morph.offset[0] = value.floatValue();
                this.stopPlayback();
            });
            this.offsetX.tooltip(IKey.lang("mclib.gui.transforms.x"));

            this.offsetY = new GuiTrackpadElement(mc, (value) -> 
            {
                this.morph.offset[1] = value.floatValue();
                this.stopPlayback();
            });
            this.offsetY.tooltip(IKey.lang("mclib.gui.transforms.y"));

            this.offsetZ = new GuiTrackpadElement(mc, (value) -> 
            {
                this.morph.offset[2] = value.floatValue();
                this.stopPlayback();
            });
            this.offsetZ.tooltip(IKey.lang("mclib.gui.transforms.z"));

            this.offsetCount = new GuiTrackpadElement(mc, (value) -> 
            {
                this.morph.offsetCount = value.intValue();
                this.stopPlayback();
            });
            this.offsetCount.tooltip(IKey.lang("blockbuster.gui.sequencer.loop_offset_count"));
            this.offsetCount.integer().limit(0);

            this.pick = new GuiNestedEdit(mc, (editing) ->
            {
                if (this.entry == null)
                {
                    return;
                }

                SequenceEntry entry = this.entry;

                this.editor.morphs.nestEdit(entry.morph, editing, true, (morph) ->
                {
                    entry.morph = MorphUtils.copy(morph);
                });
            });

            this.duration = new GuiTrackpadElement(mc, (value) ->
            {
                if (this.entry != null)
                {
                    this.morph.current = -1;
                    this.morph.timer = 0;
                    this.morph.duration = 0;
                    this.morph.loopCount = 0;
                    this.morph.lastUpdate = 0;

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
                    this.morph.current = -1;
                    this.morph.timer = 0;
                    this.morph.duration = 0;
                    this.morph.loopCount = 0;
                    this.morph.lastUpdate = 0;

                    this.entry.random = value.floatValue();
                    this.stopPlayback();
                }
            });
            this.random.tooltip(IKey.lang("blockbuster.gui.sequencer.random"));
            this.random.limit(0, Float.MAX_VALUE);
            this.setDuration = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.sequencer.set_duration"), (b) -> this.entry.setDuration = b.isToggled());
            this.setDuration.tooltip(IKey.lang("blockbuster.gui.sequencer.set_duration_tooltip"), Direction.TOP);

            this.endPoint = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.sequencer.end_point"), (b) -> this.entry.endPoint = b.isToggled());
            this.endPoint.tooltip(IKey.lang("blockbuster.gui.sequencer.end_point_tooltip"), Direction.TOP);

            this.reverse = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.sequencer.reverse"), false, (b) ->
            {
                this.morph.reverse = b.isToggled();
                this.stopPlayback();
            });

            this.randomOrder = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.sequencer.random_order"), false, (b) ->
            {
                this.morph.isRandom = b.isToggled();
                this.stopPlayback();
                this.updatePreviewBar();
            });

            this.trulyRandomOrder = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.sequencer.truly_random_order"), false, (b) ->
            {
                this.morph.isTrulyRandom = b.isToggled();
                this.stopPlayback();
                this.updatePreviewBar();
            });

            this.addPart.flex().relative(this.area).set(10, 10, 50, 20);
            this.removePart.flex().relative(this.addPart.resizer()).set(55, 0, 50, 20);
            this.list.flex().relative(this.area).set(10, 50, 105, 0).hTo(this.reverse.area, -5);
            this.randomOrder.flex().relative(this).x(10).y(1F, -24).w(105);
            this.trulyRandomOrder.flex().relative(this.randomOrder).y(-1F, -5).w(1F);
            this.reverse.flex().relative(this.trulyRandomOrder).y(-1F, -5).w(1F);

            /* Playback preview code */
            this.preview = new GuiTrackpadElement(mc, (value) -> this.previewTick(value.floatValue()));
            this.preview.limit(0).metric().tooltip(IKey.lang("blockbuster.gui.sequencer.preview_tick"));
            this.plause = new GuiIconElement(mc, Icons.PLAY, (b) -> this.togglePlay());
            this.plause.tooltip(IKey.lang("blockbuster.gui.sequencer.keys.toggle")).flex().wh(16, 20);
            this.stop = new GuiIconElement(mc, Icons.STOP, (b) -> this.stopPlayback());
            this.stop.tooltip(IKey.lang("blockbuster.gui.sequencer.keys.stop")).flex().wh(16, 20);

            this.previewBar = new GuiElement(mc);

            this.previewBar.flex().relative(this).x(130).y(10).wTo(this.elementsTop.flex()).h(20).row(5).preferred(1);
            this.previewBar.add(this.plause, this.preview, this.stop);

            this.generateMorph = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.sequencer.generate_morph"), (element) ->
            {
                SequencerMorph previewer = GuiSequencerMorphRenderer.PREVIEWER;
                AbstractMorph morph = previewer.getMorph();
                
                if (morph instanceof IMorphGenerator)
                {
                    float progress = this.previewRenderer.partialTicks + previewer.timer - previewer.lastDuration;
                    float duration = previewer.duration - previewer.lastDuration;
                    float partialTicks;

                    if (previewer.morphSetDuration)
                    {
                        if (duration > 0.0001)
                        {
                            partialTicks = progress / duration;
                        }
                        else
                        {
                            partialTicks = 1;
                        }
                    }
                    else
                    {
                        partialTicks = progress - (int) progress;
                    }
                    
                    morph = ((IMorphGenerator) morph).genCurrentMorph(partialTicks);
                    this.setMorphDuration(morph, (int) Math.min(progress, duration));
                    
                    SequenceEntry entry = new SequenceEntry(morph);
                    entry.duration = Math.min(progress, duration);
                    entry.setDuration = true;

                    this.list.getList().add(entry);
                    this.list.setIndex(this.list.getList().size() - 1);

                    this.select(entry);
                    this.list.update();

                    this.stopPlayback();
                }
            });
            this.generateMorph.flex().relative(this).x(0.5F).y(1.0F, -20).wh(100, 30).anchor(0.5F, 1.0F);
            this.generateMorph.tooltip(IKey.lang("blockbuster.gui.sequencer.generate_morph_tooltip"));

            this.elements.add(this.pick, this.duration, this.random, this.setDuration, this.endPoint);
            this.elementsTop.add(Elements.label(IKey.lang("blockbuster.gui.sequencer.loop")), this.loop, Elements.label(IKey.lang("blockbuster.gui.sequencer.loop_offset")), this.offsetX, this.offsetY, this.offsetZ, this.offsetCount);
            this.add(this.addPart, this.removePart, this.randomOrder, this.trulyRandomOrder, this.reverse, this.list, this.elements, this.elementsTop, this.previewBar, this.generateMorph);

            this.keys().register(((LabelTooltip) this.plause.tooltip).label, Keyboard.KEY_SPACE, () -> this.plause.clickItself(GuiBase.getCurrent()))
                .held(Keyboard.KEY_LSHIFT)
                .category(GuiAbstractMorph.KEY_CATEGORY);
            this.keys().register(((LabelTooltip) this.stop.tooltip).label, Keyboard.KEY_SPACE, () -> this.stop.clickItself(GuiBase.getCurrent()))
                .held(Keyboard.KEY_LMENU)
                .category(GuiAbstractMorph.KEY_CATEGORY);
            
            this.previewRenderer = (GuiSequencerMorphRenderer) editor.renderer; 
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
                this.endPoint.toggled(entry.endPoint);

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

        private void previewTick(float ticks)
        {
            this.previewRenderer.tick = (int) ticks;
            this.previewRenderer.partialTicks = ticks - this.previewRenderer.tick;

            if (this.previewRenderer.playing)
            {
                this.togglePlay();
            }

            this.updatePreviewMorph();
        }

        private void togglePlay()
        {
            this.previewRenderer.playing = !this.previewRenderer.playing;
            this.updatePlauseButton();
            this.updatePreviewMorph();
        }

        private void updatePlauseButton()
        {
            this.plause.both(this.previewRenderer.playing ? Icons.PAUSE : Icons.PLAY);
        }

        private void stopPlayback()
        {
            this.previewRenderer.tick = 0;
            this.previewRenderer.partialTicks = 0F;
            this.previewRenderer.playing = false;
            this.updatePlauseButton();
            this.preview.setValue(0);

            if (this.previewRenderer.morph == GuiSequencerMorphRenderer.PREVIEWER)
            {
                this.list.setIndex(0);
                this.select(this.list.getCurrentFirst());
            }
        }

        private void updatePreviewMorph()
        {
            if (this.entry != null)
            {
                this.list.setIndex(-1);
                this.select(null);
            }
            
            if (this.previewRenderer.morph != GuiSequencerMorphRenderer.PREVIEWER)
            {
                GuiSequencerMorphRenderer.PREVIEWER.reset();
                GuiSequencerMorphRenderer.PREVIEWER.copy(this.morph);
                this.previewRenderer.morph = GuiSequencerMorphRenderer.PREVIEWER;
            }

            GuiSequencerMorphRenderer.PREVIEWER.pause(null, this.previewRenderer.tick);
            GuiSequencerMorphRenderer.PREVIEWER.resume();
        }
        
        private void updatePreviewBar()
        {
            boolean visible = !this.morph.isRandom || !this.morph.isTrulyRandom;
            
            this.previewBar.setVisible(visible);
            this.plause.setEnabled(visible);
            this.stop.setEnabled(visible);
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
            this.trulyRandomOrder.toggled(morph.isTrulyRandom);

            this.loop.setValue(morph.loop);
            this.offsetX.setValue(morph.offset[0]);
            this.offsetY.setValue(morph.offset[1]);
            this.offsetZ.setValue(morph.offset[2]);
            this.offsetCount.setValue(morph.offsetCount);
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
            this.preview.setValue(this.previewRenderer.tick + this.previewRenderer.partialTicks);

            boolean canGenerate = false;
            
            if (this.previewRenderer.morph == GuiSequencerMorphRenderer.PREVIEWER && !this.previewRenderer.playing)
            {
                AbstractMorph morph = GuiSequencerMorphRenderer.PREVIEWER.getMorph();

                if (morph instanceof IMorphGenerator)
                {
                    canGenerate = ((IMorphGenerator) morph).canGenerate();
                }
            }

            this.generateMorph.setVisible(canGenerate);
        }

        public void setMorphDuration(AbstractMorph morph, int duration)
        {
            if (morph instanceof IAnimationProvider)
            {
                Animation animation = ((IAnimationProvider) morph).getAnimation();

                animation.duration = duration;
                animation.reset();
            }
            
            if (morph instanceof IBodyPartProvider)
            {
                for (BodyPart part : ((IBodyPartProvider) morph).getBodyPart().parts)
                {
                    this.setMorphDuration(part.morph.get(), duration);
                }
            }
        }

        public int getCurrentTick()
        {
            if (this.previewRenderer.morph == GuiSequencerMorphRenderer.PREVIEWER)
            {
                return this.previewRenderer.tick;
            }
            else
            {
                return this.morph.getTickAt(this.list.getIndex());
            }
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

    /**
     * Sequencer Morph Renderer
     */
    public static class GuiSequencerMorphRenderer extends GuiMorphRenderer
    {
        public static final SequencerMorph PREVIEWER = new SequencerMorph();

        public boolean playing;

        public int tick;
        public float partialTicks;
        public long lastTick;

        public GuiSequencerMorphRenderer(Minecraft mc)
        {
            super(mc);
        }

        @Override
        protected void drawUserModel(GuiContext context)
        {
            this.doRender(context, this.entity, 0.0D, 0.0D, 0.0D);
        }

        public void doRender(GuiContext context, EntityLivingBase entity, double x, double y, double z)
        {
            if (this.morph == null)
            {
                return;
            }

            if (this.morph == PREVIEWER && this.playing)
            {
                long delta = MathUtils.clamp(context.tick - this.lastTick, 0, 10);

                if (delta > 0)
                {
                    this.tick += delta;
                    this.partialTicks = context.partialTicks;

                    PREVIEWER.pause(null, this.tick);
                    PREVIEWER.resume();
                }
            }

            MorphUtils.render(this.morph, entity, x, y, z, this.yaw, this.partialTicks);

            this.lastTick = context.tick;
        }
    }
}