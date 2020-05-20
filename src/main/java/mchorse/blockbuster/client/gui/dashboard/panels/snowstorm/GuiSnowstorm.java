package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm;

import jdk.nashorn.internal.ir.Block;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.gui.dashboard.GuiBlockbusterPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections.GuiSnowstormAppearanceSection;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections.GuiSnowstormCollisionSection;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections.GuiSnowstormExpirationSection;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections.GuiSnowstormGeneralSection;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections.GuiSnowstormInitializationSection;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections.GuiSnowstormLifetimeSection;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections.GuiSnowstormLightingSection;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections.GuiSnowstormMotionSection;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections.GuiSnowstormRateSection;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections.GuiSnowstormSection;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections.GuiSnowstormShapeSection;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections.GuiSnowstormSpaceSection;
import mchorse.blockbuster.client.particles.BedrockLibrary;
import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringSearchListElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiConfirmModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDrawable;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.mclib.GuiDashboard;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class GuiSnowstorm extends GuiBlockbusterPanel
{
	public static final String DEFAULT_PARTICLE = "default_snow";

	public GuiSnowstormRenderer renderer;
	public GuiScrollElement editor;

	public GuiIconElement open;
	public GuiIconElement save;

	public GuiElement modal;
	public GuiIconElement add;
	public GuiIconElement dupe;
	public GuiIconElement remove;
	public GuiIconElement folder;
	public GuiStringSearchListElement particles;

	public List<GuiSnowstormSection> sections = new ArrayList<GuiSnowstormSection>();

	private BedrockLibrary library;

	private String filename;
	private BedrockScheme scheme;
	private boolean dirty;

	public GuiSnowstorm(Minecraft mc, GuiDashboard dashboard)
	{
		super(mc, dashboard);

		/* TODO: Add link to snowstorm web editor */
		this.library = Blockbuster.proxy.particles;

		this.renderer = new GuiSnowstormRenderer(mc);
		this.renderer.flex().relative(this).wh(1F, 1F);

		this.editor = new GuiScrollElement(mc);
		this.editor.flex().relative(this).x(1F).w(200).h(1F).anchorX(1F).column(20).vertical().stretch().scroll().padding(10);

		this.open = new GuiIconElement(mc, Icons.MORE, (b) -> this.modal.toggleVisible());
		this.open.flex().relative(this).wh(20, 20);
		this.save = new GuiIconElement(mc, Icons.SAVE, (b) -> this.save());
		this.save.flex().relative(this.open).x(20).wh(20, 20);

		/* Modal */
		this.modal = new GuiElement(mc);
		this.modal.flex().relative(this).y(20).w(160).hTo(this.area, 1F, -16);

		GuiLabel label = Elements.label(IKey.lang("blockbuster.gui.snowstorm.title"), 20)
			.anchor(0, 0.5F);
		label.flex().relative(this.modal).xy(10, 10).w(1F, -20);

		this.add = new GuiIconElement(mc, Icons.ADD, (b) -> this.addEffect());
		this.dupe = new GuiIconElement(mc, Icons.DUPE, (b) -> this.dupeEffect());
		this.remove = new GuiIconElement(mc, Icons.REMOVE, (b) -> this.removeEffect());
		this.folder = new GuiIconElement(mc, Icons.FOLDER, (b) -> GuiUtils.openWebLink(this.library.folder.toURI()));

		this.particles = new GuiStringSearchListElement(mc, (list) -> this.setScheme(list.get(0)));
		this.particles.flex().relative(this.modal).xy(10, 35).w(1F, -20).h(1F, -45);

		GuiElement icons = new GuiElement(mc);
		icons.flex().relative(this.modal).x(1F, -10).y(10).h(20).anchorX(1F).row(0).resize().width(20).height(20);
		icons.add(this.add, this.dupe, this.remove, this.folder);

		this.modal.add(label, icons, this.particles);
		this.modal.setVisible(false);
		this.add(this.renderer, new GuiDrawable(this::drawOverlay), this.editor, this.modal, this.open, this.save);

		this.addSection(new GuiSnowstormGeneralSection(mc, this));
		this.addSection(new GuiSnowstormSpaceSection(mc, this));
		this.addSection(new GuiSnowstormInitializationSection(mc, this));
		this.addSection(new GuiSnowstormRateSection(mc, this));
		this.addSection(new GuiSnowstormLifetimeSection(mc, this));
		this.addSection(new GuiSnowstormShapeSection(mc, this));
		this.addSection(new GuiSnowstormMotionSection(mc, this));
		this.addSection(new GuiSnowstormExpirationSection(mc, this));
		this.addSection(new GuiSnowstormAppearanceSection(mc, this));
		this.addSection(new GuiSnowstormLightingSection(mc, this));
		this.addSection(new GuiSnowstormCollisionSection(mc, this));

		this.keys()
			.register(IKey.lang("blockbuster.gui.snowstorm.keys.save"), Keyboard.KEY_S, () -> this.save.clickItself(GuiBase.getCurrent()))
			.held(Keyboard.KEY_LCONTROL).category(IKey.lang("blockbuster.gui.snowstorm.keys.category"));
	}

	private void addEffect()
	{
		GuiModal.addFullModal(this.modal, () -> new GuiPromptModal(this.mc, IKey.lang("blockbuster.gui.snowstorm.add_modal"), (name) ->
		{
			if (this.library.hasEffect(name) || name.isEmpty())
			{
				return;
			}

			BedrockScheme scheme = this.library.load(DEFAULT_PARTICLE);

			scheme.identifier = name;
			this.setScheme(name, scheme);
			this.particles.list.setCurrent("");
			this.dirty();
		}));
	}

	private void dupeEffect()
	{
		GuiModal.addFullModal(this.modal, () -> new GuiPromptModal(this.mc, IKey.lang("blockbuster.gui.snowstorm.dupe_modal"), (name) ->
		{
			if (this.library.hasEffect(name) || name.isEmpty())
			{
				return;
			}

			BedrockScheme scheme = BedrockScheme.dupe(this.scheme);

			scheme.identifier = name;
			this.setScheme(name, scheme);
			this.particles.list.add(name);
			this.particles.list.sort();
			this.particles.list.setCurrent(name);
			this.dirty();
		}).setValue(this.filename));
	}

	private void removeEffect()
	{
		if (this.scheme.isFactory())
		{
			return;
		}

		GuiModal.addFullModal(this.modal, () -> new GuiConfirmModal(this.mc, IKey.lang("blockbuster.gui.snowstorm.remove_modal"), (confirm) ->
		{
			if (!confirm || !this.library.hasEffect(this.filename))
			{
				return;
			}

			int index = this.particles.list.getIndex();

			if (this.library.file(this.filename).delete())
			{
				if (!this.library.factory.containsKey(this.filename))
				{
					this.particles.list.remove(this.filename);
				}

				index = MathUtils.clamp(index, 0, this.particles.list.getList().size() - 1);

				this.particles.list.setIndex(index);
				this.setScheme(this.particles.list.getCurrentFirst());
			}
		}));
	}

	public void dirty()
	{
		this.dirty = true;
		this.updateSaveButton();
	}

	private void updateSaveButton()
	{
		this.save.both(this.dirty ? Icons.SAVE : Icons.SAVED);
	}

	private void updateRemoveButton()
	{
		this.remove.setEnabled(!this.scheme.isFactory());
	}

	private void save()
	{
		for (GuiSnowstormSection section : this.sections)
		{
			section.beforeSave(this.scheme);
		}

		this.library.save(this.filename, this.scheme);

		if (!this.particles.list.getList().contains(this.filename))
		{
			this.particles.list.add(this.filename);
			this.particles.list.sort();
			this.particles.list.setCurrent(this.filename);
		}

		this.dirty = false;
		this.scheme.factory(false);
		this.updateSaveButton();
		this.updateRemoveButton();
	}

	private void addSection(GuiSnowstormSection section)
	{
		this.sections.add(section);
		this.editor.add(section);
	}

	private void setScheme(String scheme)
	{
		this.setScheme(scheme, this.library.load(scheme));
	}

	private void setScheme(String name, BedrockScheme scheme)
	{
		this.filename = name;
		this.scheme = scheme;
		this.renderer.setScheme(this.scheme);

		this.dirty = false;
		this.updateSaveButton();
		this.updateRemoveButton();

		for (GuiSnowstormSection section : this.sections)
		{
			section.setScheme(this.scheme);
		}

		this.editor.resize();
	}

	@Override
	public void appear()
	{
		super.appear();

		String current = this.particles.list.getCurrentFirst();

		this.particles.filter("", true);

		this.particles.list.clear();
		this.particles.list.add(this.library.presets.keySet());
		this.particles.list.sort();

		if (this.scheme == null)
		{
			this.setScheme(DEFAULT_PARTICLE);
			this.particles.list.setCurrent(DEFAULT_PARTICLE);
		}
		else
		{
			this.particles.list.setCurrent(current);
		}
	}

	@Override
	public void close()
	{
		if (this.renderer.emitter != null)
		{
			this.renderer.emitter.particles.clear();
		}
	}

	private void drawOverlay(GuiContext context)
	{
		/* Draw debug info */
		this.editor.area.draw(0x88000000);

		BedrockEmitter emitter = this.renderer.emitter;
		String label = emitter.particles.size() + "P - " + emitter.age + "A";

		this.font.drawStringWithShadow(label, this.area.x + 4, this.area.ey() - 12, 0xffffff);

		if (this.modal.isVisible())
		{
			this.open.area.draw(0x88000000);
			this.modal.area.draw(0x88000000);
		}
	}
}