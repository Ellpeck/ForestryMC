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
package forestry.factory.tiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import forestry.api.core.IErrorLogic;
import forestry.api.recipes.IStillRecipe;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.render.TankRenderInfo;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.factory.gui.ContainerStill;
import forestry.factory.gui.GuiStill;
import forestry.factory.inventory.InventoryStill;
import forestry.factory.recipes.StillRecipeManager;

public class TileStill extends TilePowered implements ISidedInventory, ILiquidTankTile {
	private static final int ENERGY_PER_RECIPE_TIME = 200;

	private final FilteredTank resourceTank;
	private final FilteredTank productTank;
	private final TankManager tankManager;

	private IStillRecipe currentRecipe;
	private FluidStack bufferedLiquid;

	public TileStill() {
		super("still", 1100, 8000);
		setInternalInventory(new InventoryStill(this));
		resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, true, false);
		resourceTank.setFilters(StillRecipeManager.recipeFluidInputs);

		productTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, false, true);
		productTank.setFilters(StillRecipeManager.recipeFluidOutputs);

		tankManager = new TankManager(this, resourceTank, productTank);
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);
		tankManager.writeToNBT(nbttagcompound);

		if (bufferedLiquid != null) {
			NBTTagCompound buffer = new NBTTagCompound();
			bufferedLiquid.writeToNBT(buffer);
			nbttagcompound.setTag("Buffer", buffer);
		}
		return nbttagcompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		tankManager.readFromNBT(nbttagcompound);

		if (nbttagcompound.hasKey("Buffer")) {
			NBTTagCompound buffer = nbttagcompound.getCompoundTag("Buffer");
			bufferedLiquid = FluidStack.loadFluidStackFromNBT(buffer);
		}
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		tankManager.writeData(data);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		tankManager.readData(data);
	}

	@Override
	public void updateServerSide() {
		super.updateServerSide();

		if (updateOnInterval(20)) {
			FluidHelper.drainContainers(tankManager, this, InventoryStill.SLOT_CAN);

			FluidStack fluidStack = productTank.getFluid();
			if (fluidStack != null) {
				FluidHelper.fillContainers(tankManager, this, InventoryStill.SLOT_RESOURCE, InventoryStill.SLOT_PRODUCT, fluidStack.getFluid(), true);
			}
		}
	}

	@Override
	public boolean workCycle() {

		int cycles = currentRecipe.getCyclesPerUnit();
		FluidStack output = currentRecipe.getOutput();

		FluidStack product = new FluidStack(output, output.amount * cycles);
		productTank.fillInternal(product, true);

		bufferedLiquid = null;

		return true;
	}

	private void checkRecipe() {
		FluidStack recipeLiquid = bufferedLiquid != null ? bufferedLiquid : resourceTank.getFluid();

		if (!StillRecipeManager.matches(currentRecipe, recipeLiquid)) {
			currentRecipe = StillRecipeManager.findMatchingRecipe(recipeLiquid);

			int recipeTime = currentRecipe == null ? 0 : currentRecipe.getCyclesPerUnit();
			setEnergyPerWorkCycle(ENERGY_PER_RECIPE_TIME * recipeTime);
			setTicksPerWorkCycle(recipeTime);
		}
	}

	@Override
	public boolean hasWork() {
		checkRecipe();

		boolean hasRecipe = currentRecipe != null;
		boolean hasTankSpace = true;
		boolean hasLiquidResource = true;

		if (hasRecipe) {
			FluidStack fluidStack = currentRecipe.getOutput();
			hasTankSpace = productTank.fillInternal(fluidStack, false) == fluidStack.amount;
			if (bufferedLiquid == null) {
				int cycles = currentRecipe.getCyclesPerUnit();
				FluidStack input = currentRecipe.getInput();
				int drainAmount = cycles * input.amount;
				FluidStack drained = resourceTank.drain(drainAmount, false);
				hasLiquidResource = drained != null && drained.amount == drainAmount;
				if (hasLiquidResource) {
					bufferedLiquid = new FluidStack(input, drainAmount);
					resourceTank.drain(drainAmount, true);
				}
			}
		}

		IErrorLogic errorLogic = getErrorLogic();
		errorLogic.setCondition(!hasRecipe, EnumErrorCode.NO_RECIPE);
		errorLogic.setCondition(!hasTankSpace, EnumErrorCode.NO_SPACE_TANK);
		errorLogic.setCondition(!hasLiquidResource, EnumErrorCode.NO_RESOURCE_LIQUID);

		return hasRecipe && hasLiquidResource && hasTankSpace;
	}

	@Override
	public TankRenderInfo getResourceTankInfo() {
		return new TankRenderInfo(resourceTank);
	}

	@Override
	public TankRenderInfo getProductTankInfo() {
		return new TankRenderInfo(productTank);
	}

	@Nonnull
	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Nonnull
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tankManager);
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return new GuiStill(player.inventory, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerStill(player.inventory, this);
	}
}
