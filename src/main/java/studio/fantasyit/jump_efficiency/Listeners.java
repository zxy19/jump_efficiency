package studio.fantasyit.jump_efficiency;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

@EventBusSubscriber(modid = JumpEfficiency.MODID, bus = EventBusSubscriber.Bus.GAME)
public class Listeners {

    private static final int[][] offsets = {
            {0, 0, 0},
            {0, -1, 0},
            {1, -1, 0},
            {-1, -1, 0},
            {0, -1, 1},
            {0, -1, -1},
            {1, -1, 1},
            {-1, -1, -1},
            {1, -1, -1},
            {-1, -1, 1},
    };

    @SubscribeEvent
    public static void jump(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.getType().is(JumpEfficiency.allowEntity)) return;
        BlockPos entityPos = entity.blockPosition();
        applyForAllPos(entity, entityPos);
    }

    private static void applyForAllPos(Entity entity, BlockPos entityPos) {
        for (int[] offset : offsets) {
            BlockPos pos = entityPos.offset(offset[0], offset[1], offset[2]);
            if (isPositionAvailable(entity, pos)) {
                tryApplyTickOn(entity, pos);
            }
        }
    }

    private static boolean isPositionAvailable(Entity entity, BlockPos pos) {
        BlockState state = entity.level().getBlockState(pos);
        if (Config.useBlockBlacklist) {
            if (state.is(JumpEfficiency.denyBlock)) return false;
        } else {
            if (!state.is(JumpEfficiency.allowBlock)) return false;
        }
        BlockEntity be = entity.level().getBlockEntity(pos);
        if (be == null || be.isRemoved()) return false;
        return true;
    }

    private static void tryApplyTickOn(Entity entity, BlockPos blockPos) {
        BlockState state = entity.level().getBlockState(blockPos);
        BlockEntity be = entity.level().getBlockEntity(blockPos);
        if (be != null && state.getBlock() instanceof EntityBlock eb) {
            @SuppressWarnings("unchecked")
            BlockEntityTicker<BlockEntity> ticker = (BlockEntityTicker<BlockEntity>) eb.getTicker(
                    entity.level(),
                    state,
                    be.getType()
            );
            BeforeApplyTickEvent beforeEvent = new BeforeApplyTickEvent(entity, state, Config.possibility);
            NeoForge.EVENT_BUS.post(beforeEvent);
            if (beforeEvent.isCanceled()) return;
            double points = beforeEvent.getPossibilityPoint();
            int count = 0;
            RandomSource random = entity.getRandom();
            points -= random.nextDouble();
            while (points > 0) {
                count++;
                if (ticker != null && !be.isRemoved())
                    ticker.tick(entity.level(), blockPos, state, be);
                points -= random.nextDouble();
            }
            if (be.getLevel() instanceof ServerLevel sl)
                sl.sendParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        blockPos.getCenter().x,
                        blockPos.getCenter().y,
                        blockPos.getCenter().z,
                        Math.min(count * 3, 100),
                        0.5,
                        0.5,
                        0.5,
                        0.01
                );
        }
    }
}
