package mchorse.blockbuster.api.formats.vox.data;

import mchorse.blockbuster.api.formats.vox.VoxReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class VoxShape
{
	private final int id;
	private final Map<String, String> attrs;
	private final int frames;
	private final ArrayList<Attribute> modelAttrs;

	public VoxShape(InputStream stream, VoxReader reader) throws Exception
	{
		this.id = reader.readInt(stream);
		this.attrs = reader.readDictionary(stream);
		this.frames = reader.readInt(stream);
		this.modelAttrs = new ArrayList<Attribute>();

		for (int i = 0; i < this.frames; i ++)
		{
			this.modelAttrs.add(new Attribute(reader.readInt(stream), reader.readDictionary(stream)));
		}
	}

	public static class Attribute
	{
		public final int id;
		public final Map<String, String> attrs;

		public Attribute(int id, Map<String, String> attrs)
		{
			this.id = id;
			this.attrs = attrs;
		}
	}
}