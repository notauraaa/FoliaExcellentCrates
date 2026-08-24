package su.nightexpress.excellentcrates.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.api.cost.CostType;
import su.nightexpress.excellentcrates.crate.effect.CrateEffect;
import su.nightexpress.excellentcrates.crate.effect.impl.*;
import com.notauraaa.folianightcore.util.registry.SimpleRegistry;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class CratesRegistries {

    public static final SimpleRegistry<String, CrateEffect> EFFECT = new SimpleRegistry<>();
    public static final SimpleRegistry<String, CostType> COST_TYPE = new SimpleRegistry<>();

    public static void load(@NotNull CratesPlugin plugin) {
        registerDefaultEffects();

    }

    private static void registerDefaultEffects() {
        registerEffect(new BeaconEffect());
        registerEffect(new HeartEffect());
        registerEffect(new HelixEffect());
        registerEffect(new PulsarEffect());
        registerEffect(new SimpleEffect());
        registerEffect(new SphereEffect());
        registerEffect(new SpiralEffect());
        registerEffect(new TornadoEffect());
        registerEffect(new VortexEffect());
    }

    public static void clear() {
        COST_TYPE.clear();
        EFFECT.clear();
    }

    public static void registerEffect(@NotNull CrateEffect effect) {
        EFFECT.register(effect.getId(), effect);
    }

    public static void registerCostType(@NotNull CostType type) {
        COST_TYPE.register(type.getId(), type);
    }

    @NotNull
    public static CrateEffect effectOrDummy(@NotNull String id) {
        return EFFECT.byKey(id).orElse(DummyEffect.INSTANCE);
    }

    @NotNull
    public static Collection<CrateEffect> getEffects() {
        return EFFECT.values();
    }

    @Nullable
    public static CostType getCostType(@NotNull String id) {
        return COST_TYPE.getByKey(id);
    }

    @NotNull
    public static Set<CostType> getAvailableCostTypes() {
        return COST_TYPE.map().values().stream().filter(CostType::isAvailable).collect(Collectors.toSet());
    }
}
