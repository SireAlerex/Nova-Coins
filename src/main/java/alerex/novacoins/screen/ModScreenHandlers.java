package alerex.novacoins.screen;

import alerex.novacoins.NovaCoins;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreenHandlers {
	public static ScreenHandlerType<CoinFurnaceScreenHandler> COIN_BLOCK_SCREEN_HANDLER;

	public static void registerAllScreenHandlers() {
		NovaCoins.LOGGER.info("Registering Mod Screen Handlers for " + NovaCoins.MOD_ID);
		COIN_BLOCK_SCREEN_HANDLER = new ScreenHandlerType<>(CoinFurnaceScreenHandler::new, FeatureSet.empty());
	}
}
