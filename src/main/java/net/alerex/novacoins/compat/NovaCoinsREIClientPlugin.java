package net.alerex.novacoins.compat;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.alerex.novacoins.block.ModBlocks;
import net.alerex.novacoins.recipe.CoinRecipe;
import net.alerex.novacoins.screen.CoinFurnaceScreen;

public class NovaCoinsREIClientPlugin implements REIClientPlugin {
	@Override
	public void registerCategories(CategoryRegistry registry) {
		registry.add(new CoinFurnaceCategory());

		registry.addWorkstations(CoinFurnaceCategory.COIN_FURNACE, EntryStacks.of(ModBlocks.COIN_FURNACE));
	}

	@Override
	public void registerDisplays(DisplayRegistry registry) {
		registry.registerRecipeFiller(CoinRecipe.class, CoinRecipe.Type.INSTANCE,
				CoinFurnaceDisplay::new);
	}

	@Override
	public void registerScreens(ScreenRegistry registry) {
		registry.registerClickArea(screen -> new Rectangle(75, 30, 20, 30), CoinFurnaceScreen.class,
				CoinFurnaceCategory.COIN_FURNACE);
	}
}
