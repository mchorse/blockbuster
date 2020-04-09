package mchorse.blockbuster_pack;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelPack;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.structure.PacketStructureListRequest;
import mchorse.blockbuster_pack.morphs.ImageMorph;
import mchorse.blockbuster_pack.morphs.ParticleMorph;
import mchorse.blockbuster_pack.morphs.RecordMorph;
import mchorse.blockbuster_pack.morphs.SequencerMorph;
import mchorse.mclib.utils.resources.RLUtils;
import mchorse.metamorph.api.creative.categories.MorphCategory;
import mchorse.metamorph.api.creative.sections.MorphSection;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

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

		this.extra = new MorphCategory(this, "extra");
		this.structures = new MorphCategory(this, "structures");

		/* Adding some default morphs which don't need to get reloaded */
		ImageMorph image = new ImageMorph();

		image.texture = RLUtils.create("blockbuster", "textures/gui/icon.png");

		this.extra.add(new ImageMorph());
		this.extra.add(new ParticleMorph());
		this.extra.add(new SequencerMorph());
		this.extra.add(new RecordMorph());
	}

	public void add(String id, Model model)
	{
		System.out.println("Add: " + id + " " + model.name);
	}

	public void remove(String id)
	{
		System.out.println("Remove: " + id);
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