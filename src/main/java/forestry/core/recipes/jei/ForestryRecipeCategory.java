package forestry.core.recipes.jei;

import javax.annotation.Nonnull;

import forestry.core.utils.Translator;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;

public abstract class ForestryRecipeCategory<T extends IRecipeWrapper> extends BlankRecipeCategory<T> {
	@Nonnull
	private final IDrawable background;
	@Nonnull
	private final String localizedName;
	
	public ForestryRecipeCategory(@Nonnull IDrawable background, String unlocalizedName) {
		this.background = background;
		this.localizedName = Translator.translateToLocal(unlocalizedName);
	}

	@Nonnull
	@Override
	public String getTitle() {
		return localizedName;
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}
}
