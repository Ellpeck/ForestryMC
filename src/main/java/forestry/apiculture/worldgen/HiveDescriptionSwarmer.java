/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.apiculture.worldgen;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import forestry.api.apiculture.hives.IHiveDescription;
import forestry.api.apiculture.hives.IHiveGen;
import forestry.api.apiculture.hives.IHiveRegistry;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.apiculture.PluginApiculture;
import forestry.apiculture.tiles.TileHive;

public class HiveDescriptionSwarmer implements IHiveDescription {

	private final List<ItemStack> bees;

	public HiveDescriptionSwarmer(ItemStack... bees) {
		this.bees = Arrays.asList(bees);
	}

	@Override
	public IHiveGen getHiveGen() {
		return new HiveGenGround(Blocks.DIRT, Blocks.GRASS);
	}

	@Override
	public IBlockState getBlockState() {
		return PluginApiculture.blocks.beehives.getStateForType(IHiveRegistry.HiveType.SWARM);
	}

	@Override
	public boolean isGoodBiome(Biome biome) {
		return true;
	}

	@Override
	public boolean isGoodHumidity(EnumHumidity humidity) {
		return true;
	}

	@Override
	public boolean isGoodTemperature(EnumTemperature temperature) {
		return true;
	}

	@Override
	public float getGenChance() {
		return 128.0f;
	}

	@Override
	public void postGen(World world, Random rand, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileHive) {
			((TileHive) tile).setContained(bees);
		}
	}
}
