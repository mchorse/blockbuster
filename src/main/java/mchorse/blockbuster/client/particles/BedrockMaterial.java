package mchorse.blockbuster.client.particles;

public enum  BedrockMaterial
{
	OPAQUE, ALPHA, BLEND;

	public static BedrockMaterial fromString(String material)
	{
		if ("particles_alpha".equals(material))
		{
			return ALPHA;
		}
		else if ("particles_blend".equals(material))
		{
			return BLEND;
		}

		return OPAQUE;
	}
}