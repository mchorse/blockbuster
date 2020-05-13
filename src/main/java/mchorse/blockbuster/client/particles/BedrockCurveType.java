package mchorse.blockbuster.client.particles;

public enum BedrockCurveType
{
	LINEAR("linear"), HERMITE("catmull_rom");

	public final String id;

	public static BedrockCurveType fromString(String type)
	{
		for (BedrockCurveType t : values())
		{
			if (t.id.equals(type))
			{
				return t;
			}
		}

		return LINEAR;
	}

	private BedrockCurveType(String id)
	{
		this.id = id;
	}
}