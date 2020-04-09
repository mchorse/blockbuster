package mchorse.blockbuster_pack;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelPack;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.structure.PacketStructureListRequest;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.blockbuster_pack.morphs.ImageMorph;
import mchorse.blockbuster_pack.morphs.ParticleMorph;
import mchorse.blockbuster_pack.morphs.RecordMorph;
import mchorse.blockbuster_pack.morphs.SequencerMorph;
import mchorse.mclib.utils.resources.RLUtils;
import mchorse.metamorph.api.creative.categories.MorphCategory;
import mchorse.metamorph.api.creative.sections.MorphSection;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.io.FilenameUtils;

import java.util.HashMap;
import java.util.Map;

public class BlockbusterSection extends MorphSection
{
	public MorphCategory extra;
	public MorphCategory structures;
	public Map<String, MorphCategory> models = new HashMap<String, MorphCategory>();

	public BlockbusterSection(String title)
	{
		super(title);

		this.extra = new MorphCategory(this, "Blockbuster extra");
		this.structures = new MorphCategory(this, "Blockbuster structures");

		/* Adding some default morphs which don't need to get reloaded */
		ImageMorph image = new ImageMorph();

		image.texture = RLUtils.create("blockbuster", "textures/gui/icon.png");

		this.extra.add(image);
		this.extra.add(new ParticleMorph());
		this.extra.add(new SequencerMorph());
		this.extra.add(new RecordMorph());
	}

	public void add(String key, Model model)
	{
		String path = this.getCategoryId(key);
		CustomMorph morph = new CustomMorph();

		morph.name = "blockbuster." + key;
		morph.model = model;

		MorphCategory category = this.models.get(path);

		if (category == null)
		{
			category = new MorphCategory(this, path.isEmpty() ? "Blockbuster models" : "Blockbuster models (" + path + ")");
			this.models.put(path, category);
			this.categories.add(category);
		}

		category.add(morph);
	}

	public void remove(String key)
	{
		String path = this.getCategoryId(key);
		String name = "blockbuster." + key;
		MorphCategory category = this.models.get(path);
		AbstractMorph morph = null;

		for (AbstractMorph m : category.getMorphs())
		{
			if (m.name.equals(name))
			{
				morph = m;

				break;
			}
		}

		if (morph != null)
		{
			category.remove(morph);
		}
	}

	private String getCategoryId(String key)
	{
		if (key.contains("/"))
		{
			key = FilenameUtils.getPath(key);

			return key.substring(0, key.length() - 1);
		}

		return "";
	}

	@Override
	public void update(World world)
	{
		this.reloadModels();

		this.categories.clear();
		this.add(this.extra);
		this.add(this.structures);

		/* Add models categories */
		for (MorphCategory category : this.models.values())
		{
			this.add(category);
		}
	}

	private void reloadModels()
	{
		/* Reload models and skin */
		ModelPack pack = Blockbuster.proxy.models.pack;

		if (pack == null)
		{
			pack = Blockbuster.proxy.getPack();

			if (Minecraft.getMinecraft().isSingleplayer())
			{
				pack.addFolder(DimensionManager.getCurrentSaveRootDirectory() + "/blockbuster/models");
			}
		}

		ClientProxy.actorPack.pack.reload();
		Blockbuster.proxy.loadModels(pack, false);

		Blockbuster.proxy.particles.reload();
		Dispatcher.sendToServer(new PacketStructureListRequest());
	}
}