package net.alerex.novacoins.block.entity;

import net.alerex.novacoins.NovaCoins;
import net.alerex.novacoins.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
	public static BlockEntityType<CoinFurnaceEntity> COIN_FURNACE;

	public static void registerBlockEntities() {
		NovaCoins.LOGGER.info("Registering Mod Block Entities for " + NovaCoins.MOD_ID);
		COIN_FURNACE = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(NovaCoins.MOD_ID, "coin_block"),
				FabricBlockEntityTypeBuilder.create(CoinFurnaceEntity::new, ModBlocks.COIN_FURNACE).build());
	}
}

