package mchorse.blockbuster.api.formats.vox;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelTransform;
import mchorse.blockbuster.api.formats.IMeshes;
import mchorse.blockbuster.api.formats.Mesh;
import mchorse.blockbuster.api.formats.vox.data.Vox;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.model.ModelVoxRenderer;
import net.minecraft.client.model.ModelBase;

public class MeshesVOX implements IMeshes
{
	public Mesh mesh;
	public VoxDocument document;
	public Vox vox;

	public MeshesVOX(VoxDocument document, VoxDocument.LimbNode node)
	{
		this.document = document;
		this.vox = node.chunk;

		this.mesh = new VoxBuilder(node.rotation).build(vox);
	}

	@Override
	public ModelCustomRenderer createRenderer(Model data, ModelBase model, ModelLimb limb, ModelTransform transform)
	{
		return new ModelVoxRenderer(model, limb, transform, this);
	}
}