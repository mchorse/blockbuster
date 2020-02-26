package mchorse.blockbuster.api.formats.vox.data;

import mchorse.blockbuster.api.formats.vox.VoxReader;

import java.io.InputStream;
import java.util.Map;

public class VoxGroup
{
	public final int id;
	public final Map<String, String> attrs;
	public final int frames;
	public final int[] ids;

	public VoxGroup(InputStream stream, VoxReader reader) throws Exception 
	{
		this.id = reader.readInt(stream);
		this.attrs = reader.readDictionary(stream);
		this.frames = reader.readInt(stream);
		this.ids = new int[this.frames];

		for (int i = 0; i < frames; i ++)
		{
			this.ids[i] = reader.readInt(stream);
		}
	}
}