package mchorse.blockbuster_pack.morphs;

import mchorse.metamorph.api.morphs.AbstractMorph;

public interface ISyncableMorph
{
	public void pauseMorph(AbstractMorph previous, int offset);

	public boolean isPaused();
}