package mchorse.blockbuster.api.formats.vox.data;

import mchorse.blockbuster.api.formats.vox.VoxReader;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VoxTransform extends VoxBaseNode
{
	public int childId;
	public int unusedId;
	public int layerId;
	public List<Matrix4f> transforms;

	public VoxTransform(InputStream stream, VoxReader reader) throws Exception
	{
		this.id = reader.readInt(stream);
		this.attrs = reader.readDictionary(stream);
		this.childId = reader.readInt(stream);
		this.unusedId = reader.readInt(stream);
		this.layerId = reader.readInt(stream);
		this.num = reader.readInt(stream);
		this.transforms = new ArrayList<Matrix4f>();

		for (int i = 0; i < this.num; i ++)
		{
			Map<String, String> dict = reader.readDictionary(stream);
			Matrix3f rotation = new Matrix3f();
			Vector3f translate = new Vector3f(0, 0, 0);

			rotation.setIdentity();

			if (dict.containsKey("_r"))
			{
				rotation = reader.readRotation(Integer.parseInt(dict.get("_r")));
			}

			if (dict.containsKey("_t"))
			{
				String[] splits = dict.get("_t").split(" ");

				if (splits.length == 3)
				{
					/* Stupid coordinate systems... */
					translate.set(-Integer.parseInt(splits[0]), Integer.parseInt(splits[1]), Integer.parseInt(splits[2]));
				}
			}

			/* Assemble the main result */
			Matrix4f transform = new Matrix4f();

			transform.set(rotation);
			transform.setTranslation(translate);

			this.transforms.add(transform);
		}
	}
}