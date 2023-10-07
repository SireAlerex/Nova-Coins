package net.alerex.novacoins.screen;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class CoinFurnaceScreenHandler extends ScreenHandler {
	private final Inventory inventory;
	private final PropertyDelegate propertyDelegate;

	public CoinFurnaceScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, new SimpleInventory(3), new ArrayPropertyDelegate(3));
	}

	public CoinFurnaceScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(ModScreenHandlers.COIN_BLOCK_SCREEN_HANDLER, syncId);
		checkSize(inventory, 3);
		this.inventory = inventory;
		inventory.onOpen(playerInventory.player);
		this.propertyDelegate = propertyDelegate;

		this.addSlot(new Slot(inventory, 0, 56, 17));  // input
		this.addSlot(new Slot(inventory, 1, 116, 35));  // output
		this.addSlot(new Slot(inventory, 2, 56, 53)); // fuel

		addPlayerInventory(playerInventory);
		addPlayerHotbar(playerInventory);

		addProperties(propertyDelegate);
	}

	public boolean isCrafting() {
		return propertyDelegate.get(0) > 0;
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int invSlot) {
		ItemStack newStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(invSlot);
		if (slot.hasStack()) {
			ItemStack originalStack = slot.getStack();
			newStack = originalStack.copy();
			// from furnace to player inventory
			if (invSlot < this.inventory.size()) {
				if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
				// from player inventory to furnace
			} else {
				if (FuelRegistry.INSTANCE.get(slot.getStack().getItem()) != null) {
					if (!this.insertItem(originalStack, 2, 3, false)) {
						return ItemStack.EMPTY;
					}
				}
				if (!this.insertItem(originalStack, 0, 1, false)) {
					return ItemStack.EMPTY;
				}
			}

			if (originalStack.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}
		}

		return newStack;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUse(player);
	}

	private void addPlayerInventory(PlayerInventory playerInventory) {
		for (int i = 0; i < 3; i++) {
			for (int l = 0; l < 9; l++) {
				this.addSlot(new Slot(playerInventory, l + i*9 +9, 8 + l*18, 84 + i*18));
			}
		}
	}

	private void addPlayerHotbar(PlayerInventory playerInventory) {
		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(playerInventory, i, 8 + i*18, 142));
		}
	}
}
