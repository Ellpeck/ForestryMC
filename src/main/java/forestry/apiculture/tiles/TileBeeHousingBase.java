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
import java.io.IOException;

import com.mojang.authlib.GameProfile;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.apiculture.gui.IGuiBeeHousingInventory;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamableGui;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.owner.OwnerHandler;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.IClimatised;
import forestry.core.tiles.TileBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public abstract class TileBeeHousingBase extends TileBase implements IBeeHousing, IOwnedTile, IClimatised, IGuiBeeHousingInventory, IStreamableGui {
	private final OwnerHandler ownerHandler = new OwnerHandler();
	private final IBeekeepingLogic beeLogic;
	private Biome cachedBiome;

	// CLIENT
	private int breedingProgressPercent = 0;

	protected TileBeeHousingBase(String hintKey) {
		super(hintKey);
		this.beeLogic = BeeManager.beeRoot.createBeekeepingLogic(this);
	}

	@Override
	public IBeekeepingLogic getBeekeepingLogic() {
		return beeLogic;
	}

	/* LOADING & SAVING */
	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);
		beeLogic.writeToNBT(nbttagcompound);
		ownerHandler.writeToNBT(nbttagcompound);
		return nbttagcompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		beeLogic.readFromNBT(nbttagcompound);
		ownerHandler.readFromNBT(nbttagcompound);
	}

	@Nonnull
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound updateTag = super.getUpdateTag();
		beeLogic.writeToNBT(updateTag);
		ownerHandler.writeToNBT(updateTag);
		return updateTag;
	}

	@Override
	public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		beeLogic.readFromNBT(tag);
		ownerHandler.readFromNBT(tag);
	}

	@Override
	public IOwnerHandler getOwnerHandler() {
		return ownerHandler;
	}

	/* ICLIMATISED */
	@Override
	public EnumTemperature getTemperature() {
		return EnumTemperature.getFromBiome(getBiome(), worldObj, getPos());
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getExactHumidity());
	}

	@Override
	public float getExactTemperature() {
		return ForestryAPI.climateManager.getTemperature(worldObj, getPos());
	}

	@Override
	public float getExactHumidity() {
		return ForestryAPI.climateManager.getHumidity(worldObj, getPos());
	}

	/* UPDATING */
	@Override
	public void updateClientSide() {
		if (beeLogic.canDoBeeFX() && updateOnInterval(4)) {
			beeLogic.doBeeFX();

			if (updateOnInterval(50)) {
				doPollenFX(worldObj, getPos().getX(), getPos().getY(), getPos().getZ());
			}
		}
	}

	public static void doPollenFX(World world, double xCoord, double yCoord, double zCoord) {
		double fxX = xCoord + 0.5F;
		double fxY = yCoord + 0.25F;
		double fxZ = zCoord + 0.5F;
		float distanceFromCenter = 0.6F;
		float leftRightSpreadFromCenter = distanceFromCenter * (world.rand.nextFloat() - 0.5F);
		float upSpread = world.rand.nextFloat() * 6F / 16F;
		fxY += upSpread;

		Proxies.render.addEntityHoneyDustFX(world, fxX - distanceFromCenter, fxY, fxZ + leftRightSpreadFromCenter);
		Proxies.render.addEntityHoneyDustFX(world, fxX + distanceFromCenter, fxY, fxZ + leftRightSpreadFromCenter);
		Proxies.render.addEntityHoneyDustFX(world, fxX + leftRightSpreadFromCenter, fxY, fxZ - distanceFromCenter);
		Proxies.render.addEntityHoneyDustFX(world, fxX + leftRightSpreadFromCenter, fxY, fxZ + distanceFromCenter);
	}

	@Override
	public void updateServerSide() {
		if (beeLogic.canWork()) {
			beeLogic.doWork();
		}
	}

	@Override
	public int getHealthScaled(int i) {
		return breedingProgressPercent * i / 100;
	}

	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		data.writeVarInt(beeLogic.getBeeProgressPercent());
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		breedingProgressPercent = data.readVarInt();
	}

	// / IBEEHOUSING
	@Override
	public Biome getBiome() {
		if (cachedBiome == null) {
			cachedBiome = worldObj.getBiome(getPos());
		}
		return cachedBiome;
	}

	@Override
	public int getBlockLightValue() {
		return worldObj.getLightFromNeighbors(getPos().up());
	}

	@Override
	public boolean canBlockSeeTheSky() {
		return worldObj.canBlockSeeSky(getPos().up());
	}

	@Override
	public GameProfile getOwner() {
		return getOwnerHandler().getOwner();
	}

	@Override
	public Vec3d getBeeFXCoordinates() {
		return new Vec3d(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5);
	}

}
