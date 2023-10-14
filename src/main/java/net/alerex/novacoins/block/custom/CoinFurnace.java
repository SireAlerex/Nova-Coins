package net.alerex.novacoins.block.custom;

import net.alerex.novacoins.NovaCoins;
import net.alerex.novacoins.block.entity.CoinFurnaceEntity;
import net.alerex.novacoins.block.entity.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


public class CoinFurnace extends BlockWithEntity implements BlockEntityProvider {
	public static final DirectionProperty FACING;
	public static final BooleanProperty LIT;

	public CoinFurnace(Settings settings) {
		super(settings);
		this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(LIT, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, LIT);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}

	/* BLOCK ENTITY */

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		NovaCoins.LOGGER.info("CoinFurnace: onStateReplaced state="+state+" pos="+pos+" newState="+newState+" moved="+moved);
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof CoinFurnaceEntity coinFurnace) {
				ItemScatterer.spawn(world, pos, coinFurnace);
				coinFurnace.dropExperience((ServerWorld) world, Vec3d.ofCenter(pos));
				coinFurnace.resetCraftCount();
				world.updateComparators(pos, this);
			}
			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult result) {
		NovaCoins.LOGGER.info("CoinFurnace: onUse state="+state+" pos="+pos+" blockHitResult="+result);
		if (!world.isClient) {
			NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

			if (screenHandlerFactory != null) {
				player.openHandledScreen(screenHandlerFactory);
			}
		}

		return ActionResult.SUCCESS;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new CoinFurnaceEntity(pos, state);
	}

	@Nullable
	@Override
	public  <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return checkType(type, ModBlockEntities.COIN_FURNACE, CoinFurnaceEntity::tick);
	}

	static {
		FACING = HorizontalFacingBlock.FACING;
		LIT = Properties.LIT;
	}
}

