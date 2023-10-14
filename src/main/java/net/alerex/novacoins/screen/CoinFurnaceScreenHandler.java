package net.alerex.novacoins.screen;

import net.alerex.novacoins.block.entity.CoinFurnaceEntity;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.MathHelper;

public class CoinFurnaceScreenHandler extends ScreenHandler {
	private final Inventory inventory;
	private final PropertyDelegate propertyDelegate;

	public CoinFurnaceScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
		this(syncId, playerInventory, playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()), new ArrayPropertyDelegate(5));
	}

	public CoinFurnaceScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity, ArrayPropertyDelegate propertyDelegate) {
		super(ModScreenHandlers.COIN_FURNACE_SCREEN_HANDLER, syncId);
		checkSize((Inventory) blockEntity, 3);
		this.inventory = (Inventory) blockEntity;
		inventory.onOpen(playerInventory.player);
		this.propertyDelegate = propertyDelegate;

		this.addSlot(new Slot(inventory, 0, 56, 17));  // input
		this.addSlot(new Slot(inventory, 1, 116, 35));  // output
		this.addSlot(new Slot(inventory, 2, 56, 53)); // fuel

		addPlayerInventory(playerInventory);
		addPlayerHotbar(playerInventory);

		addProperties(propertyDelegate);
	}

	public CoinFurnaceScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, new SimpleInventory(3), new ArrayPropertyDelegate(5));
	}

	public CoinFurnaceScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(ModScreenHandlers.COIN_FURNACE_SCREEN_HANDLER, syncId);
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

	public boolean isBurning() {
		return propertyDelegate.get(2) > 0;
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
				int originalCount = originalStack.getCount();
				if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
				int diff = originalCount - slot.getStack().getCount();
				if (invSlot == 1 && diff > 0) {
					if (propertyDelegate.get(3) != originalCount) {
						dropExperienceHopper(player, diff, originalCount);
					} else {
						dropExperience(player, diff);
					}
				}
			// from player inventory to furnace
			} else {
				if (FuelRegistry.INSTANCE.get(slot.getStack().getItem()) != null) {
					if (!this.insertItem(originalStack, 2, 3, false)) {
						return ItemStack.EMPTY;
					}
				}
				else if (!this.insertItem(originalStack, 0, 1, false)) {
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

	@Override
	public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
		if (slotIndex == 1 && actionType != SlotActionType.QUICK_MOVE) {
			if (!this.getCursorStack().isOf(Items.AIR)) {
				ItemStack cursorItem = this.getCursorStack();
				ItemStack slotItem = this.slots.get(slotIndex).getStack();
				if (cursorItem.isOf(slotItem.getItem())) {
					int amount;
					if (cursorItem.getCount() + slotItem.getCount() <= cursorItem.getMaxCount()) {
						amount = slotItem.getCount();
					} else {
						amount = cursorItem.getMaxCount() - cursorItem.getCount();
					}
					cursorItem.increment(amount);
					this.slots.get(slotIndex).takeStack(amount);
					dropExperience(player, amount);
				}
			} else {
				Slot slot = this.slots.get(slotIndex);
				int originalCount = slot.getStack().getCount();
				super.onSlotClick(slotIndex, button, actionType, player);
				int diff = originalCount - slot.getStack().getCount();
				if (diff > 0) {
					dropExperience(player, diff);
				}
			}
			return;
		}
		super.onSlotClick(slotIndex, button, actionType, player);
	}

	private void dropExperience(PlayerEntity player, int amount) {
		int craftCount = propertyDelegate.get(3);
		propertyDelegate.set(3, craftCount - amount);
		player.addExperience(CoinFurnaceEntity.getExperienceFromCraftCount(amount));
	}

	private void dropExperienceHopper(PlayerEntity player, int amount, int currentAmount) {
		int craftCount = propertyDelegate.get(3);
		int hoppers = craftCount - currentAmount;
		int total = amount + hoppers;
		propertyDelegate.set(3, craftCount - total);
		player.addExperience(CoinFurnaceEntity.getExperienceFromCraftCount(total));
	}

	public int getScaledProgress() {
		float progress = (float)this.propertyDelegate.get(0);
		float maxProgress = (float)this.propertyDelegate.get(1);
		float progressArrowSize = 23; // width of arrow

		return maxProgress != 0 && progress != 0 ? MathHelper.ceil(progress * progressArrowSize / maxProgress) : 0;
	}

	public int getScaledBurning() {
		float fuelTicks = (float)this.propertyDelegate.get(2);
		float maxFuelTicks = (float)this.propertyDelegate.get(4);
		float progressArrowSize = 13; // height of flames

		return maxFuelTicks != 0 && fuelTicks != 0 ? MathHelper.ceil(fuelTicks * progressArrowSize / maxFuelTicks) : 0;
	}
}
