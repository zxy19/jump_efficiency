package studio.fantasyit.jump_efficiency;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class BeforeApplyTickEvent extends Event implements ICancellableEvent {
    private final Entity entity;
    private final BlockState blockState;
    private double possibilityPoint;

    public BeforeApplyTickEvent(Entity entity, BlockState blockState, double possibilityPoint) {
        this.entity = entity;
        this.blockState = blockState;
        this.possibilityPoint = possibilityPoint;
    }

    public Entity getEntity() {
        return entity;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public double getPossibilityPoint() {
        return possibilityPoint;
    }

    public void setPossibilityPoint(double possibilityPoint) {
        this.possibilityPoint = possibilityPoint;
    }
}
