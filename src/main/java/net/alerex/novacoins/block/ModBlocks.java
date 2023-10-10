package net.alerex.novacoins.block;

import net.alerex.novacoins.NovaCoins;
import net.alerex.novacoins.block.custom.CoinFurnace;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
	public static final Block COIN_FURNACE = registerBlock("coin_furnace",
			new CoinFurnace(FabricBlockSettings.create().requiresTool().strength(4.0f)));

	private static Block registerBlock(String name, Block block) {
		registerBlockItem(name, block);
		return Registry.register(Registries.BLOCK, new Identifier(NovaCoins.MOD_ID, name), block);
	}

	private static Item registerBlockItem(String name, Block block) {
		return Registry.register(Registries.ITEM, new Identifier(NovaCoins.MOD_ID, name), new BlockItem(block, new FabricItemSettings()));
	}

	private static void addFunctionalTab(FabricItemGroupEntries entries) {
		entries.add(COIN_FURNACE);
	}

	public static void registerModBlocks() {
		NovaCoins.LOGGER.info("Registering Mod Blocks for " + NovaCoins.MOD_ID);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(ModBlocks::addFunctionalTab);
	}
}

