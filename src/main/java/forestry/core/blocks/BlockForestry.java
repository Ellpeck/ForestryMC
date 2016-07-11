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
package forestry.core.blocks;

import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.core.IItemModelRegister;
import forestry.core.CreativeTabForestry;
import forestry.core.tiles.TileForestry;
import forestry.core.utils.Log;

public abstract class BlockForestry extends Block implements IItemModelRegister, ITileEntityProvider {

	protected BlockForestry(Material material) {
		super(material);
		setHardness(1.5f);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (world.isRemote) {
			return;
		}

		if (placer instanceof EntityPlayer) {
			TileEntity tile = world.getTileEntity(pos);

			if (tile instanceof IOwnedTile) {
				IOwnerHandler ownerHandler = ((IOwnedTile) tile).getOwnerHandler();
				EntityPlayer player = (EntityPlayer) placer;
				GameProfile gameProfile = player.getGameProfile();
				ownerHandler.setOwner(gameProfile);
			}
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		try {
			TileEntity tile = worldIn.getTileEntity(pos);
			if (tile instanceof TileForestry) {
				((TileForestry) tile).onNeighborBlockChange(state.getBlock());
			}
		} catch (StackOverflowError error) {
			Log.error("Stack Overflow Error in BlockMachine.onNeighborBlockChange()", error);
			throw error;
		}
	}

}
