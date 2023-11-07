package net.alerex.novacoins.compat;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.alerex.novacoins.recipe.CoinRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CoinFurnaceDisplay extends BasicDisplay {
	public CoinFurnaceDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<Identifier> location) {
		super(inputs, outputs, location);
	}

	public CoinFurnaceDisplay(CoinRecipe recipe) {
		this(EntryIngredients.ofIngredients(recipe.getIngredients()),
				Collections.singletonList(EntryIngredients.of(recipe.getOutput(BasicDisplay.registryAccess()))),
				Optional.ofNullable(recipe.getId()));
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
