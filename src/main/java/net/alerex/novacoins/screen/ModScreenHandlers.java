
package net.alerex.novacoins.screen;

import net.alerex.novacoins.NovaCoins;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreenHandlers {
	public static ScreenHandlerType<CoinFurnaceScreenHandler> COIN_FURNACE_SCREEN_HANDLER;

	public static void registerAllScreenHandlers() {
		NovaCoins.LOGGER.info("Registering Mod Screen Handlers for " + NovaCoins.MOD_ID);
		COIN_FURNACE_SCREEN_HANDLER = new ScreenHandlerType<>(CoinFurnaceScreenHandler::new, FeatureSet.empty());
	}
}
