package mchorse.blockbuster.api.formats.vox.data;

import mchorse.blockbuster.api.formats.vox.VoxReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class VoxTransform
{
	public final int id;
	public final Map<String, String> attrs;
	public final int childId;
	public final int unusedId;
	public final int layerId;
	public final int frames;
	public final ArrayList<Map<String, String>> nodes;

	public VoxTransform(InputStream stream, VoxReader reader) throws Exception
	{
		this.id = reader.readInt(stream);
		this.attrs = reader.readDictionary(stream);
		this.childId = reader.readInt(stream);
		this.unusedId = reader.readInt(stream);
		this.layerId = reader.readInt(stream);
		this.frames = reader.readInt(stream);
		this.nodes = new ArrayList<Map<String, String>>();

		for (int i = 0; i < this.frames; i ++)
		{
			this.nodes.add(reader.readDictionary(stream));
		}
	}
}