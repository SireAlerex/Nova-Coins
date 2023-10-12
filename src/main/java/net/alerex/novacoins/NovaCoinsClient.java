package net.alerex.novacoins;

import net.alerex.novacoins.screen.CoinFurnaceScreen;
import net.alerex.novacoins.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

@Environment(EnvType.CLIENT)
public class NovaCoinsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(ModScreenHandlers.COIN_FURNACE_SCREEN_HANDLER, CoinFurnaceScreen::new);
	}
}

