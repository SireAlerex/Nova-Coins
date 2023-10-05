package alerex.novacoins.recipe;

import com.google.gson.*;
import alerex.novacoins.util.*;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class CoinRecipe implements Recipe<SimpleInventory> {
	private final Identifier id;
	private final ItemStack output;
	private final DefaultedList<IngredientStack> recipeItems;

	public CoinRecipe(Identifier id, ItemStack output, DefaultedList<IngredientStack> recipeItems) {
		this.id = id;
		this.output = output;
		this.recipeItems = recipeItems;
	}

	@Override
	public boolean matches(SimpleInventory inventory, World world) {
		if (world.isClient()) {
			return false;
		}

		ItemStack inputStack = inventory.getStack(0);
		IngredientStack recipeStack = recipeItems.get(0);

		return recipeStack.getIngredient().test(inputStack) && recipeStack.getCount() <= inputStack.getCount();
	}

	@Override
	public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
		return output;
	}

	@Override
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getOutput(DynamicRegistryManager registryManager) {
		return output.copy();
	}

	@Override
	public Identifier getId() {
		return id;
	}

	public static class Serializer implements RecipeSerializer<CoinRecipe> {
		private Serializer() {}

		public static final Serializer INSTANCE = new Serializer();

		public static final String ID = "coin_recipe";

		@Override
		public CoinRecipe read(Identifier id, JsonObject json) {
			Gson gson = new Gson();

			ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));

			JsonArray ingredientStacks = JsonHelper.getArray(json, "ingredients");
			DefaultedList<IngredientStack> inputs = DefaultedList.ofSize(1, new IngredientStack(Ingredient.EMPTY, 0));

			for (int i = 0; i < inputs.size(); i++) {
				JsonObject object = ingredientStacks.get(i).getAsJsonObject();

				JsonObject ingredientObject = new JsonObject();
				if (object.get("item") != null) {
					ingredientObject.add("item", object.get("item"));
				} else if (object.get("tag") != null) {
					ingredientObject.add("tag", object.get("tag"));
				} else {
					throw new JsonSyntaxException("Missing 'item' or 'tag' filed for ingredient");
				}
				Ingredient ing = Ingredient.fromJson(ingredientObject);

				JsonElement raw_count = object.get("count");
				Integer count = raw_count != null ? gson.fromJson(raw_count, Integer.class) : 1;
				IngredientStack ingredientStack = new IngredientStack(ing, count);
				inputs.set(i, ingredientStack);
			}

			return new CoinRecipe(id, output, inputs);
		}

		@Override
		public CoinRecipe read(Identifier id, PacketByteBuf buf) {
			DefaultedList<IngredientStack> inputs = DefaultedList.ofSize(buf.readInt(), new IngredientStack(Ingredient.EMPTY, 0));

			for (int i = 0; i < inputs.size(); i++) {
				Ingredient ing = Ingredient.fromPacket(buf);
				Integer count = buf.readInt();
				inputs.set(1, new IngredientStack(ing, count));
			}

			ItemStack output = buf.readItemStack();
			return new CoinRecipe(id, output, inputs);
		}

		@Override
		public void write(PacketByteBuf buf, CoinRecipe recipe) {
			buf.writeInt(recipe.getIngredientStacks().size());
			for (IngredientStack ing : recipe.getIngredientStacks()) {
				ing.getIngredient().write(buf);
				buf.writeInt(ing.getCount());
			}
			buf.writeItemStack(recipe.getRawOutput());
		}
	}

	public DefaultedList<IngredientStack> getIngredientStacks() {
		return this.recipeItems;
	}

	public ItemStack getRawOutput() {
		return output.copy();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return Serializer.INSTANCE;
	}

	public static class Type implements RecipeType<CoinRecipe> {
		private Type() {}
		public static final Type INSTANCE = new Type();

		public static final String ID = "coin_recipe";
	}

	@Override
	public RecipeType<?> getType() {
		return Type.INSTANCE;
	}
}
