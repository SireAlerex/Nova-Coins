package net.alerex.novacoins.recipe;

import com.google.gson.*;
import net.alerex.novacoins.NovaCoins;
import net.alerex.novacoins.util.*;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Arrays;

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
			if (json.toString().isEmpty()) {
				throw new JsonSyntaxException("Empty JSON object");
			}
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
			NovaCoins.LOGGER.info("CoinRecipe: reading packet for id="+id);
			DefaultedList<IngredientStack> inputs = DefaultedList.ofSize(buf.readInt(), new IngredientStack(Ingredient.EMPTY, 0));

			inputs.replaceAll(ignored -> new IngredientStack(Ingredient.fromPacket(buf), buf.readInt()));

			ItemStack output = buf.readItemStack();
			return new CoinRecipe(id, output, inputs);
		}

		@Override
		public void write(PacketByteBuf buf, CoinRecipe recipe) {
			NovaCoins.LOGGER.info("CoinRecipe: writing packet for recipe="+recipe+" id="+recipe.id+" output="+recipe.output + " ingredients="+recipe.recipeItems);
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

	@Override
	public DefaultedList<Ingredient> getIngredients() {
		DefaultedList<Ingredient> list = DefaultedList.of();
		for (IngredientStack ing : this.getIngredientStacks()) {
			list.add(ing.getIngredient());
		}

		return list;
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
