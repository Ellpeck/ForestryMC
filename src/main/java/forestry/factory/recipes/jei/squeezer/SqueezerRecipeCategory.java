package forestry.factory.recipes.jei.squeezer;

import javax.annotation.Nonnull;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.core.recipes.jei.ForestryTooltipCallback;
import forestry.core.render.ForestryResource;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeWrapper;

public class SqueezerRecipeCategory extends ForestryRecipeCategory {
	
	private static final int[][] INPUTS = new int[][]{{0, 0}, {1, 0}, {2, 0}, {0, 1}, {1, 1}, {2, 1}, {0, 2}, {1, 2}, {2, 2}};
	
	private static final int craftOutputSlot = 0;
	private static final int craftInputSlot = 1;
	
	private static final int outputTank = 0;
	
	private final static ResourceLocation guiTexture = new ForestryResource("textures/gui/squeezersocket.png");
	@Nonnull
	private final IDrawableAnimated arrow;
	@Nonnull
	private final IDrawable tankOverlay;

	public SqueezerRecipeCategory(@Nonnull IGuiHelper guiHelper) {
		super(guiHelper.createDrawable(guiTexture, 9, 16, 158, 62), "tile.for.squeezer.name");
		
		IDrawableStatic arrowDrawable = guiHelper.createDrawable(guiTexture, 176, 60, 43, 18);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
		this.tankOverlay = guiHelper.createDrawable(guiTexture, 176, 0, 16, 58);
	}
	
	@Nonnull
	@Override
	public String getUid() {
		return ForestryRecipeCategoryUid.SQUEEZER;
	}

	@Override
	public void drawAnimations(@Nonnull Minecraft minecraft) {
		arrow.draw(minecraft, 67, 25);
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
		
		AbstractSqueezerRecipeWrapper<?> wrapper = (AbstractSqueezerRecipeWrapper) recipeWrapper;
		
		float chance = wrapper.getRemnantsChance();

		guiFluidStacks.init(outputTank, false, 113, 2, 16, 58, 10000, false, tankOverlay);
		guiFluidStacks.set(outputTank, wrapper.getFluidOutputs());

		guiItemStacks.init(craftOutputSlot, false, 87, 43);
		guiItemStacks.set(craftOutputSlot, wrapper.getOutputs());
		ForestryTooltipCallback tooltip = new ForestryTooltipCallback();
		tooltip.addChanceTooltip(craftOutputSlot, chance);
		setIngredients(guiItemStacks, wrapper.getInputs());

		guiItemStacks.addTooltipCallback(tooltip);
	}
	
	private static void setIngredients(@Nonnull IGuiItemStackGroup guiItemStacks, @Nonnull List<ItemStack> inputs) {
		int i = 0;
		for (ItemStack stack : inputs) {
			guiItemStacks.init(craftInputSlot + i, true, 7 + INPUTS[i][0] * 18, 4 + INPUTS[i][1] * 18);
			guiItemStacks.set(craftInputSlot + i, stack);
			i++;
		}
	}

}
