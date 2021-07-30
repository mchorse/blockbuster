package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiPoseTransformations;
import mchorse.blockbuster_pack.morphs.StructureMorph;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiSearchListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAnimation;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class GuiStructureMorph extends GuiAbstractMorph<StructureMorph>
{
    private static IKey ANCHOR_POINT = IKey.lang("blockbuster.gui.structure_morph.anchor");

    public GuiStructureMorphPanel general;

    public GuiStructureMorph(Minecraft mc)
    {
        super(mc);

        this.defaultPanel = this.general = new GuiStructureMorphPanel(mc, this);
        this.registerPanel(this.general, IKey.lang("blockbuster.morph.structure"), Icons.GEAR);
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof StructureMorph;
    }

    public static class GuiStructureMorphPanel extends GuiMorphPanel<StructureMorph, GuiStructureMorph>
    {
        public GuiPoseTransformations pose;
        public GuiAnimation animation;
        public GuiToggleElement lighting;
        public GuiSearchBiomeList biomes;
        public GuiTrackpadElement anchorX;
        public GuiTrackpadElement anchorY;
        public GuiTrackpadElement anchorZ;

        public GuiStructureMorphPanel(Minecraft mc, GuiStructureMorph editor)
        {
            super(mc, editor);

            this.pose = new GuiPoseTransformations(mc);
            this.pose.flex().relative(this.area).set(0, 0, 190, 70).x(0.5F, -95).y(1, -75);

            this.animation = new GuiAnimation(mc, true);
            this.animation.flex().relative(this).x(1F, -130).w(130);

            this.lighting = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.structure_morph.lighting"), (b) -> this.morph.lighting = b.isToggled());
            this.lighting.flex().relative(this).x(1F, -10).y(1F, -10).w(110).anchor(1F, 1F);
            this.anchorX = new GuiTrackpadElement(mc, (v) -> this.morph.anchorX = v.floatValue());
            this.anchorY = new GuiTrackpadElement(mc, (v) -> this.morph.anchorY = v.floatValue());
            this.anchorZ = new GuiTrackpadElement(mc, (v) -> this.morph.anchorZ = v.floatValue());
            this.anchorX.flex().relative(this.anchorY).y(-5).w(1F).anchorY(1);
            this.anchorY.flex().relative(this.anchorZ).y(-5).w(1F).anchorY(1);
            this.anchorZ.flex().relative(this.lighting).y(-5).w(1F).anchorY(1);
            
            this.biomes = new GuiSearchBiomeList(mc, this::accept);
            this.biomes.list.sort();
            this.biomes.flex().relative(this).x(0F).w(150).h(1F).anchorX(0F);
            this.biomes.list.background(0x80000000);
            this.biomes.resize();
            this.biomes.list.scroll.scrollSpeed = 15;

            this.add(this.pose, this.animation, this.lighting, this.anchorZ, this.anchorY, this.anchorX, this.biomes);
        }

        @Override
        public void fillData(StructureMorph morph)
        {
            super.fillData(morph);

            this.pose.set(morph.pose);
            this.animation.fill(morph.animation);
            this.lighting.toggled(morph.lighting);
            this.biomes.filter("", true);
            this.biomes.list.setCurrent(morph.biome);
            this.anchorX.setValue(morph.anchorX);
            this.anchorY.setValue(morph.anchorY);
            this.anchorZ.setValue(morph.anchorZ);
        }

        private void accept(List<ResourceLocation> sel)
        {
            this.morph.biome = sel.get(0);
        }

        @Override
        public void draw(GuiContext context)
        {
            this.font.drawStringWithShadow(ANCHOR_POINT.get(), this.anchorX.area.x, this.anchorX.area.y - 12, 0xffffff);

            super.draw(context);
        }
    }
    
    public static class GuiSearchBiomeList extends GuiSearchListElement<ResourceLocation>
    {
        public GuiSearchBiomeList(Minecraft mc, Consumer<List<ResourceLocation>> callback)
        {
            super(mc, callback);
        }

        @Override
        protected GuiListElement<ResourceLocation> createList(Minecraft mc, Consumer<List<ResourceLocation>> callback)
        {
            return new GuiBiomeList(mc, callback);
        }
    }
    
    public static class GuiBiomeList extends GuiListElement<ResourceLocation>
    {

        public GuiBiomeList(Minecraft mc, Consumer<List<ResourceLocation>> callback)
        {
            super(mc, callback);
            for (ResourceLocation location : Biome.REGISTRY.getKeys())
            {
                this.add(location);
            }
        }
        
        @Override
        protected boolean sortElements()
        {
            Collections.<ResourceLocation>sort(this.list, Comparator.comparing(this::elementToString));

            return true;
        }

        @Override
        protected String elementToString(ResourceLocation element)
        {
            return Biome.REGISTRY.getObject(element).getBiomeName();
        }
    }
}
