package forestry.factory.recipes.jei.moistener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import forestry.api.fuels.MoistenerFuel;
import forestry.api.recipes.IMoistenerRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;

public class MoistenerRecipeWrapper extends ForestryRecipeWrapper<IMoistenerRecipe> {
	
	@Nonnull
	private final MoistenerFuel fuel;
	
	public MoistenerRecipeWrapper(@Nonnull IMoistenerRecipe recipe, @Nonnull MoistenerFuel fuel) {
		super(recipe);
		this.fuel = fuel;
	}

	@Nonnull
	@Override
	public List<ItemStack> getInputs() {
		List<ItemStack> inputs = new ArrayList<>();
		inputs.add(getRecipe().getResource());
		if (fuel.getItem() != null) {
			inputs.add(fuel.getItem());
		}
		return inputs;
	}

	@Nonnull
	@Override
	public List<ItemStack> getOutputs() {
		List<ItemStack> outputs = new ArrayList<>();
		outputs.add(getRecipe().getProduct());
		if (fuel.getProduct() != null) {
			outputs.add(fuel.getProduct());
		}
		return outputs;
	}

	@Nonnull
	public MoistenerFuel getFuel() {
		return fuel;
	}

}
