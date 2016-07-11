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
package forestry.apiculture.tiles;

import javax.annotation.Nonnull;
import java.util.Collections;

import net.minecraft.entity.player.EntityPlayer;

import forestry.api.apiculture.DefaultBeeListener;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.apiculture.BeehouseBeeModifier;
import forestry.apiculture.InventoryBeeHousing;
import forestry.apiculture.gui.ContainerBeeHousing;
import forestry.apiculture.gui.GuiBeeHousing;

public class TileBeeHouse extends TileBeeHousingBase {
	@Nonnull
	private static final IBeeModifier beeModifier = new BeehouseBeeModifier();

	@Nonnull
	private final IBeeListener beeListener;
	@Nonnull
	private final InventoryBeeHousing beeInventory;

	public TileBeeHouse() {
		super("bee.house");
		this.beeListener = new DefaultBeeListener();

		beeInventory = new InventoryBeeHousing(12);
		beeInventory.disableAutomation();
		setInternalInventory(beeInventory);
	}

	@Nonnull
	@Override
	public IBeeHousingInventory getBeeInventory() {
		return beeInventory;
	}

	@Override
	public Iterable<IBeeModifier> getBeeModifiers() {
		return Collections.singleton(beeModifier);
	}

	@Override
	public Iterable<IBeeListener> getBeeListeners() {
		return Collections.singleton(beeListener);
	}

	@Override
	public Object getGui(EntityPlayer player, int data) {
		ContainerBeeHousing container = new ContainerBeeHousing(player.inventory, this, false);
		return new GuiBeeHousing<>(this, container, GuiBeeHousing.Icon.BEE_HOUSE);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerBeeHousing(player.inventory, this, false);
	}
}
