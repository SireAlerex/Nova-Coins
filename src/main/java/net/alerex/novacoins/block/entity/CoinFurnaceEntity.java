package alerex.novacoins.block.entity;

import alerex.novacoins.NovaCoins;
import alerex.novacoins.recipe.CoinRecipe;
import alerex.novacoins.screen.CoinFurnaceScreenHandler;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;

public class CoinFurnaceEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
	private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
	protected final PropertyDelegate propertyDelegate;
	private int progress = 0;
	private int maxProgress = 100;
	private int fuelTicks = 0;

	public CoinFurnaceEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.COIN_FURNACE, pos, state);
		this.propertyDelegate = new PropertyDelegate() {
			@Override
			public int get(int index) {
				return switch (index) {
					case 0 -> CoinFurnaceEntity.this.progress;
					case 1 -> CoinFurnaceEntity.this.maxProgress;
					case 2 -> CoinFurnaceEntity.this.fuelTicks;
					default -> 0;
				};
			}

			@Override
			public void set(int index, int value) {
				switch (index) {
					case 0 -> CoinFurnaceEntity.this.progress = value;
					case 1 -> CoinFurnaceEntity.this.maxProgress = value;
					case 2 -> CoinFurnaceEntity.this.fuelTicks = value;
				}
			}

			@Override
			public int size() {
				return 3;
			}
		};
	}

	@Override
	public DefaultedList<ItemStack> getItems() {
		return this.inventory;
	}

	@Override
	public Text getDisplayName() {
		return Text.literal("Coin Furnace");
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return new CoinFurnaceScreenHandler(syncId, inv, this, this.propertyDelegate);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		Inventories.writeNbt(nbt, inventory);
		nbt.putInt("coin_block.progress", progress);
		nbt.putInt("coin_block.fuelTicks", fuelTicks);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		Inventories.readNbt(nbt, inventory);
		super.readNbt(nbt);
		progress = nbt.getInt("coin_block.progress");
		fuelTicks = nbt.getInt("coin_block.fuelTicks");
	}

	private void resetProgress() {
		this.progress = 0;
	}

	public static void tick(World world, BlockPos blockPos, BlockState blockState, CoinFurnaceEntity entity) {
		if (world.isClient()) {
			return;
		}
		entity.fuelTicks = entity.fuelTicks == 0? 0 : entity.fuelTicks-1;

		if (hasRecipe(entity)) {
			if (entity.fuelTicks == 0) {
				Item fuel = entity.getItems().get(2).getItem();
				Integer ticks = FuelRegistry.INSTANCE.get(fuel);
				if (ticks != null) {
					entity.fuelTicks = ticks/2;
					entity.removeStack(2, 1);
				}
			}
			if (entity.fuelTicks > 0) {
				entity.progress++;
				markDirty(world, blockPos, blockState);
				if (entity.progress >= entity.maxProgress) craftItem(entity);
			}
		} else {
			entity.resetProgress();
			markDirty(world, blockPos, blockState);
		}
	}

	private static void craftItem(CoinFurnaceEntity entity) {
		SimpleInventory inventory = getInventory(entity);
		World world = entity.getWorld();

		if (world == null) {
			return;
		}

		Optional<CoinRecipe> firstMatch = world.getRecipeManager()
				.getFirstMatch(CoinRecipe.Type.INSTANCE, inventory, entity.getWorld());

		if (firstMatch.isEmpty()) {
			return;
		}

		if (entity.fuelTicks > 0) {
			CoinRecipe recipe = firstMatch.get();
			entity.removeStack(0, recipe.getIngredientStacks().get(0).getCount());
			entity.setStack(1, new ItemStack(recipe.getRawOutput().getItem(),
					entity.getStack(1).getCount() + recipe.getRawOutput().getCount()));
			entity.resetProgress();
		}
	}

	private static boolean hasRecipe(CoinFurnaceEntity entity) {
		World world = entity.getWorld();
		if (world == null) {
			return false;
		}

		SimpleInventory inventory = getInventory(entity);

		Optional<CoinRecipe> match = world.getRecipeManager()
				.getFirstMatch(CoinRecipe.Type.INSTANCE, inventory, entity.getWorld());

		return match.isPresent()
				&& canInsertAmountInOutput(inventory, match.get().getRawOutput().getCount())
				&& canInsertItemInOutput(inventory, match.get().getRawOutput().getItem());
	}

	@NotNull
	private static SimpleInventory getInventory(CoinFurnaceEntity entity) {
		SimpleInventory inventory = new SimpleInventory(entity.size());
		for (int i = 0; i < entity.size(); i++) {
			inventory.setStack(i, entity.getStack(i));
		}
		return inventory;
	}

	private static boolean canInsertItemInOutput(SimpleInventory inventory, Item item) {
		return inventory.getStack(1).isEmpty() || inventory.getStack(1).getItem() == item;
	}

	private static boolean canInsertAmountInOutput(SimpleInventory inventory, int amount) {
		return inventory.getStack(1).getMaxCount() >= inventory.getStack(1).getCount() + amount;
	}
}


