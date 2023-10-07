package net.alerex.novacoins;

import net.alerex.novacoins.block.ModBlocks;
import net.alerex.novacoins.block.entity.ModBlockEntities;
import net.alerex.novacoins.recipe.ModRecipes;
import net.alerex.novacoins.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NovaCoins implements ModInitializer {
	public static final String MOD_ID = "novacoins";
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("novacoins");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Nova Coins initializing!");

		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
		ModScreenHandlers.registerAllScreenHandlers();
		ModRecipes.registerRecipes();
	}
}