package net.alerex.novacoins.compat;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.alerex.novacoins.recipe.CoinRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CoinFurnaceDisplay extends BasicDisplay {
	public CoinFurnaceDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
		super(inputs, outputs);
	}

	public CoinFurnaceDisplay(CoinRecipe recipe) {
		super(getInputList(recipe), List.of(EntryIngredient.of(EntryIngredients.ofIngredient(Ingredient.ofStacks(recipe.getRawOutput())))));
	}

	private static List<EntryIngredient> getInputList(CoinRecipe recipe) {
		if(recipe == null) return Collections.emptyList();
		List<EntryIngredient> list = new ArrayList<>();
		list.add(EntryIngredients.ofIngredient(recipe.getIngredients().get(0)));
		return list;
	}

	@Override
	public CategoryIdentifier<?> getCategoryIdentifier() {
		return CoinFurnaceCategory.COIN_FURNACE;
	}
}
