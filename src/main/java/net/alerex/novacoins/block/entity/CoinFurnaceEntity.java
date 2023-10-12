package net.alerex.novacoins.block.entity;

import net.alerex.novacoins.block.custom.CoinFurnace;
import net.alerex.novacoins.recipe.CoinRecipe;
import net.alerex.novacoins.screen.CoinFurnaceScreenHandler;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;

public class CoinFurnaceEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory, SidedInventory {
	private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
	protected final PropertyDelegate propertyDelegate;
	private int progress = 0;
	private int maxProgress = 100;
	private int fuelTicks = 0;
	private int craftCount = 0;
	private int lastFuelMax = 0;

	@Override
	public int[] getAvailableSlots(Direction side) {
		int[] result = new int[1];
		result[0] = switch (side) {
			case UP -> 0;
			case DOWN -> 1;
			default -> 2;
		};
		return result;
	}

	public CoinFurnaceEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.COIN_FURNACE, pos, state);
		this.propertyDelegate = new PropertyDelegate() {
			@Override
			public int get(int index) {
				return switch (index) {
					case 0 -> CoinFurnaceEntity.this.progress;
					case 1 -> CoinFurnaceEntity.this.maxProgress;
					case 2 -> CoinFurnaceEntity.this.fuelTicks;
					case 3 -> CoinFurnaceEntity.this.craftCount;
					case 4 -> CoinFurnaceEntity.this.lastFuelMax;
					default -> 0;
				};
			}

			@Override
			public void set(int index, int value) {
				switch (index) {
					case 0 -> CoinFurnaceEntity.this.progress = value;
					case 1 -> CoinFurnaceEntity.this.maxProgress = value;
					case 2 -> CoinFurnaceEntity.this.fuelTicks = value;
					case 3 -> CoinFurnaceEntity.this.craftCount = value;
					case 4 -> CoinFurnaceEntity.this.lastFuelMax = value;
				}
			}

			@Override
			public int size() {
				return 5;
			}
		};
	}

	@Override
	public DefaultedList<ItemStack> getItems() {
		return this.inventory;
	}

	@Override
	public Text getDisplayName() {
		return Text.translatable("block.novacoins.coin_furnace");
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
		nbt.putInt("coin_block.craftCount", craftCount);
		nbt.putInt("coin_block.lastFuelMax", lastFuelMax);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		Inventories.readNbt(nbt, inventory);
		super.readNbt(nbt);
		progress = nbt.getInt("coin_block.progress");
		fuelTicks = nbt.getInt("coin_block.fuelTicks");
		craftCount = nbt.getInt("coin_block.craftCount");
		lastFuelMax = nbt.getInt("coin_block.lastFuelMax");
	}

	private void resetProgress() {
		this.progress = 0;
	}

	private boolean isBurning() {
		return this.fuelTicks > 0;
	}

	public static void tick(World world, BlockPos blockPos, BlockState blockState, CoinFurnaceEntity entity) {
		if (world.isClient()) {
			return;
		}
		boolean burningAtStart = entity.isBurning();
		entity.fuelTicks = Integer.max(0, entity.fuelTicks-1);

		if (hasRecipe(entity)) {
			if (entity.fuelTicks == 0) {
				Item fuel = entity.getItems().get(2).getItem();
				Integer ticks = FuelRegistry.INSTANCE.get(fuel);
				if (ticks != null) {
					entity.fuelTicks = ticks/2;
					entity.lastFuelMax = ticks/2;
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

		if (burningAtStart != entity.isBurning()) {
			blockState = blockState.with(CoinFurnace.LIT, entity.isBurning());
			world.setBlockState(blockPos, blockState, 3);
			markDirty(world, blockPos, blockState);
		}
	}

	private static void craftItem(CoinFurnaceEntity entity) {
		World world = entity.getWorld();
		if (world == null) {
			return;
		}

		SimpleInventory inventory = getInventory(entity);
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
			entity.craftCount += 1;
		}
	}

	private static boolean hasRecipe(CoinFurnaceEntity entity) {
		World world = entity.getWorld();
		if (world == null) {
			return false;
		}

		SimpleInventory inventory = getInventory(entity);

		Optional<CoinRecipe> match = world.getRecipeManager()
				.getFirstMatch(CoinRecipe.Type.INSTANCE, inventory, world);

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

	public static int getExperienceFromCraftCount(int amount) {
		float xp = (float) (0.7 * amount);
		int wholeXp = MathHelper.floor(xp);
		float f = MathHelper.fractionalPart(xp);
		if (f != 0.0F && Math.random() < (double)f) {
			++wholeXp;
		}
		return Integer.max(wholeXp, 0); // be sure no negative xp
	}

	public void dropExperience(ServerWorld world, Vec3d pos) {
		ExperienceOrbEntity.spawn(world, pos, getExperienceFromCraftCount(this.craftCount));
		this.craftCount = 0;
	}

	public void resetCraftCount() {
		this.craftCount = 0;
	}
}
