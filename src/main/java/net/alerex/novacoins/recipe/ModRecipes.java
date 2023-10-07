package net.alerex.novacoins.recipe;

import net.alerex.novacoins.NovaCoins;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
	public static void registerRecipes() {
		NovaCoins.LOGGER.info("Registering Mod Recipes for " + NovaCoins.MOD_ID);
		Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(NovaCoins.MOD_ID, CoinRecipe.Serializer.ID),
				CoinRecipe.Serializer.INSTANCE);
		Registry.register(Registries.RECIPE_TYPE, new Identifier(NovaCoins.MOD_ID, CoinRecipe.Type.ID),
				CoinRecipe.Type.INSTANCE);
	}
}
