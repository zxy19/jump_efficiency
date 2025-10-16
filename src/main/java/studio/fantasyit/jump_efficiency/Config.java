package studio.fantasyit.jump_efficiency;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = JumpEfficiency.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue SHOW_PARTICLE = BUILDER
            .comment("Show particle when apply an extra tick")
            .define("show_particle", true);
    private static final ModConfigSpec.BooleanValue USE_BLOCK_BLACKLIST = BUILDER
            .comment("Use blacklist to check if block can be apply extra ticks")
            .define("use_block_blacklist", false);

    private static final ModConfigSpec.DoubleValue POSSIBILITY_POINTS = BUILDER
            .comment("The possibility to apply an extra tick. Every time when the entity jumps, then pick a number between 0 and 1 and remove this number from possibility point value. If the remain point is greater than 0, then apply an extra tick and repeat the steps before.")
            .defineInRange("possibility_point", 6.0f, 0, 1000000.f);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean showParticle;
    public static boolean useBlockBlacklist;
    public static double possibility;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        showParticle = SHOW_PARTICLE.get();
        useBlockBlacklist = USE_BLOCK_BLACKLIST.get();
        possibility = POSSIBILITY_POINTS.get();
    }
}
