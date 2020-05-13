package mchorse.blockbuster.client.particles;

public enum  BedrockMaterial
{
	OPAQUE("particles_opaque"), ALPHA("particles_alpha"), BLEND("particles_blend");

	public final String id;

	public static BedrockMaterial fromString(String material)
	{
		for (BedrockMaterial mat : values())
		{
			if (mat.id.equals(material))
			{
				return mat;
			}
		}

		return OPAQUE;
	}

	private BedrockMaterial(String id)
	{
		this.id = id;
	}
}