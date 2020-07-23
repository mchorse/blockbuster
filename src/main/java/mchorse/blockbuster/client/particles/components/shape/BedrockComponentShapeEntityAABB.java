package mchorse.blockbuster.client.particles.components.shape;

import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;

public class BedrockComponentShapeEntityAABB extends BedrockComponentShapeBase
{
	@Override
	public void apply(BedrockEmitter emitter, BedrockParticle particle)
	{
		float centerX = (float) this.offset[0].get();
		float centerY = (float) this.offset[1].get();
		float centerZ = (float) this.offset[2].get();

		float w = 0;
		float h = 0;
		float d = 0;

		if (emitter.target != null)
		{
			w = emitter.target.width;
			h = emitter.target.height;
			d = emitter.target.width;
		}

		particle.position.x = centerX + ((float) Math.random() - 0.5F) * w;
		particle.position.y = centerY + ((float) Math.random() - 0.5F) * h;
		particle.position.z = centerZ + ((float) Math.random() - 0.5F) * d;

		if (this.surface)
		{
			int roll = (int) (Math.random() * 6 * 100) % 6;

			if (roll == 0) particle.position.x = centerX + w / 2F;
			else if (roll == 1) particle.position.x = centerX - w / 2F;
			else if (roll == 2) particle.position.y = centerY + h / 2F;
			else if (roll == 3) particle.position.y = centerY - h / 2F;
			else if (roll == 4) particle.position.z = centerZ + d / 2F;
			else if (roll == 5) particle.position.z = centerZ - d / 2F;
		}

		this.direction.applyDirection(particle, centerX, centerY, centerZ);
	}
}
