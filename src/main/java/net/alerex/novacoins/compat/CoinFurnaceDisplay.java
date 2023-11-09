package net.alerex.novacoins.compat;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.alerex.novacoins.recipe.CoinRecipe;
import net.alerex.novacoins.util.IngredientStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CoinFurnaceDisplay extends BasicDisplay {
	public CoinFurnaceDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<Identifier> location) {
		super(inputs, outputs, location);
	}

	public CoinFurnaceDisplay(CoinRecipe recipe) {
		this(EntryIngredients.ofIngredients(recipe.getIngredients()),
				getInputAndOutputList(recipe),
				Optional.ofNullable(recipe.getId()));
	}

	private static List<EntryIngredient> getInputAndOutputList(CoinRecipe recipe) {
		ArrayList<EntryIngredient> list = new ArrayList<>();

		IngredientStack ing_stack = recipe.getIngredientStacks().get(0);
		ItemStack x = ing_stack.getIngredient().getMatchingStacks()[0];
		x.setCount(ing_stack.getCount());
		list.add(EntryIngredients.of(x));

		list.add(EntryIngredients.of(recipe.getRawOutput()));

		return list;
	}

	@Override
	public CategoryIdentifier<?> getCategoryIdentifier() {
		return CoinFurnaceCategory.COIN_FURNACE;
	}


}
