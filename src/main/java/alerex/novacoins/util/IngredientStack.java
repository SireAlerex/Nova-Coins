package alerex.novacoins.util;

import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Pair;

public class IngredientStack {
	private final Pair<Ingredient, Integer> pair;

	public IngredientStack(Ingredient ingredient, Integer integer) {
		this.pair = new Pair<>(ingredient, integer);
	}

	public Ingredient getIngredient() {
		return this.pair.getLeft();
	}

	public Integer getCount() {
		return this.pair.getRight();
	}

	@Override
	public String toString() {
		return "IngredientStack{" +
				"ingredient=" + pair.getLeft() +
				", count=" + pair.getRight() +
				'}';
	}
}
