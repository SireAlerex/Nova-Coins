
package net.alerex.novacoins.screen;

import net.alerex.novacoins.NovaCoins;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
	public static final ScreenHandlerType<CoinFurnaceScreenHandler> COIN_FURNACE_SCREEN_HANDLER =
			Registry.register(Registries.SCREEN_HANDLER, new Identifier(NovaCoins.MOD_ID, "coin_furnace"),
					new ExtendedScreenHandlerType<>(CoinFurnaceScreenHandler::new));

	public static void registerAllScreenHandlers() {
		NovaCoins.LOGGER.info("Registering Mod Screen Handlers for " + NovaCoins.MOD_ID);
	}
}
