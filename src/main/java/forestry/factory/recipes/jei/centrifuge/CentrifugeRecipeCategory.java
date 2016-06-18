package forestry.factory.recipes.jei.centrifuge;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.core.recipes.jei.ForestryTooltipCallback;
import forestry.core.render.ForestryResource;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeWrapper;

public class CentrifugeRecipeCategory extends ForestryRecipeCategory {

	private static final int[][] OUTPUTS = new int[][]{{0, 0}, {1, 0}, {2, 0}, {0, 1}, {1, 1}, {2, 1}, {0, 2}, {1, 2}, {2, 2}};
	
	private static final Comparator<Entry<ItemStack, Float>> highestChanceComparator = new Comparator<Entry<ItemStack, Float>>() {
		@Override
		public int compare(Entry<ItemStack, Float> o1, Entry<ItemStack, Float> o2) {
			return o2.getValue().compareTo(o1.getValue());
		}
	};
	
	private static final int inputSlot = 0;
	private static final int outputSlot = 1;
	
	private final static ResourceLocation guiTexture = new ForestryResource("textures/gui/centrifugesocket.png");
	@Nonnull
	private final IDrawableAnimated arrow;

	public CentrifugeRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createDrawable(guiTexture, 25, 18, 126, 54), "tile.for.centrifuge.name");
		
		IDrawableStatic arrowDrawable = guiHelper.createDrawable(guiTexture, 176, 0, 4, 17);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 80, IDrawableAnimated.StartDirection.BOTTOM, false);
	}
	
	@Nonnull
	@Override
	public String getUid() {
		return ForestryRecipeCategoryUid.CENTRIFUGE;
	}

	@Override
	public void drawAnimations(@Nonnull Minecraft minecraft) {
		arrow.draw(minecraft, 33, 18);
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		
		guiItemStacks.init(inputSlot, true, 4, 18);
		guiItemStacks.setFromRecipe(inputSlot, recipeWrapper.getInputs());
		CentrifugeRecipeWrapper centrifugeWrapper = (CentrifugeRecipeWrapper) recipeWrapper;
		ForestryTooltipCallback tooltip = new ForestryTooltipCallback();
		setResults(tooltip, centrifugeWrapper.getRecipe().getAllProducts(), guiItemStacks);
		guiItemStacks.addTooltipCallback(tooltip);
	}
	
	private static void setResults(ForestryTooltipCallback tooltip, Map<ItemStack, Float> outputs, IGuiItemStackGroup guiItemStacks) {
		Set<Entry<ItemStack, Float>> entrySet = outputs.entrySet();
		if (entrySet.isEmpty()) {
			return;
		}
		Queue<Entry<ItemStack, Float>> sortByChance = new PriorityQueue<>(entrySet.size(), highestChanceComparator);
		sortByChance.addAll(entrySet);

		int i = 0;
		while (!sortByChance.isEmpty()) {
			Entry<ItemStack, Float> stack = sortByChance.poll();
			if (i >= OUTPUTS.length) {
				return;
			}
			int x = 72 + OUTPUTS[i][0] * 18;
			int y = OUTPUTS[i][1] * 18;
			int slotIndex = outputSlot + i;
			guiItemStacks.init(slotIndex, false, x, y);
			guiItemStacks.set(slotIndex, stack.getKey());
			tooltip.addChanceTooltip(slotIndex, stack.getValue());
			i++;
		}
	}

}
