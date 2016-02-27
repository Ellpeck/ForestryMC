/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.lepidopterology;

import javax.annotation.Nonnull;

import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IGenome;

public interface IButterflyGenome extends IGenome<ButterflyChromosome> {

	@Nonnull
	@Override
	IAlleleButterflySpecies getPrimary();

	@Nonnull
	@Override
	IAlleleButterflySpecies getSecondary();

	float getSize();

	int getLifespan();

	int getMetabolism();
	
	int getFertility();

	float getSpeed();

	EnumTolerance getToleranceTemp();

	EnumTolerance getToleranceHumid();

	boolean getNeverSleeps();

	boolean getTolerantFlyer();

	boolean getFireResist();

	IFlowerProvider getFlowerProvider();

	IAlleleButterflyEffect getEffect();

	@Nonnull
	@Override
	IButterflyRoot getSpeciesRoot();
}