/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.lepidopterology;

import javax.annotation.Nullable;

import forestry.api.genetics.IIndividualLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public interface IButterfly extends IIndividualLiving {

	@Override
	IButterflyGenome getGenome();

	/**
	 * @return Genetic information of the mate, null if unmated.
	 */
	@Override
	IButterflyGenome getMate();

	/**
	 * @return Physical size of the butterfly.
	 */
	float getSize();

	/**
	 * @return true if the butterfly can naturally spawn at the given location at this time. (Used to auto-spawn butterflies from tree leaves.)
	 */
	boolean canSpawn(World world, double x, double y, double z);

	/**
	 * @return true if the butterfly can take flight at the given location at this time. (Used to auto-spawn butterflies from dropped items.)
	 */
	boolean canTakeFlight(World world, double x, double y, double z);

	/**
	 * @return true if the environment (temperature, humidity) is valid for the butterfly at the given location.
	 */
	boolean isAcceptedEnvironment(World world, double x, double y, double z);

	/**
	 * @return create a caterpillar with the two genome's from the nursery.
	 */
	@Nullable
	IButterfly spawnCaterpillar(World world, IButterflyNursery nursery);

	/**
	 * @param playerKill Whether or not the butterfly was killed by a player.
	 * @param lootLevel  Loot level according to the weapon used to kill the butterfly.
	 * @return Array of itemstacks to drop on death of the given entity.
	 */
	NonNullList<ItemStack> getLootDrop(IEntityButterfly entity, boolean playerKill, int lootLevel);

	/**
	 * @param playerKill Whether or not the nursery was broken by a player.
	 * @param lootLevel  Fortune level.
	 * @return Array of itemstacks to drop on breaking of the nursery.
	 */
	NonNullList<ItemStack> getCaterpillarDrop(IButterflyNursery nursery, boolean playerKill, int lootLevel);

	/**
	 * @return itemstacks to drop on breaking of the cocoon.
	 */
	NonNullList<ItemStack> getCocoonDrop(IButterflyCocoon cocoon);

	/**
	 * Create an exact copy of this butterfly.
	 */
	@Override
	IButterfly copy();

}
