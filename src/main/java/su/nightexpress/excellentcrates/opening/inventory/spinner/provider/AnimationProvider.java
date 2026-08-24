package su.nightexpress.excellentcrates.opening.inventory.spinner.provider;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.opening.inventory.InventoryOpening;
import su.nightexpress.excellentcrates.opening.inventory.spinner.SpinnerProvider;
import su.nightexpress.excellentcrates.opening.inventory.spinner.SpinnerData;
import su.nightexpress.excellentcrates.opening.inventory.spinner.impl.AnimationSpinner;
import com.notauraaa.folianightcore.config.FileConfig;
import com.notauraaa.folianightcore.config.Writeable;
import com.notauraaa.folianightcore.util.bukkit.FoliaItem;
import com.notauraaa.folianightcore.util.random.WeightedItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnimationProvider implements SpinnerProvider, Writeable {

    private final Map<String, WeightedItem<FoliaItem>> itemMap;

    public AnimationProvider(@NotNull Map<String, WeightedItem<FoliaItem>> itemMap) {
        this.itemMap = new HashMap<>(itemMap);
    }

    @NotNull
    public static AnimationProvider read(@NotNull FileConfig config, @NotNull String path) {
        Map<String, WeightedItem<FoliaItem>> itemsMap = new HashMap<>();
        config.getSection(path + ".Items").forEach(sId -> {
            double weight = config.getDouble(path + ".Items." + sId + ".Chance", 100D);
            FoliaItem item = config.getCosmeticItem(path + ".Items." + sId);
            itemsMap.put(sId.toLowerCase(), new WeightedItem<>(item, weight));
        });

        return new AnimationProvider(itemsMap);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.remove(path + ".Items");

        this.itemMap.forEach((id, witem) -> {
            config.set(path + ".Items." + id + ".Chance", witem.getWeight());
            config.set(path + ".Items." + id, witem.getItem());
        });
    }

    @Override
    @NotNull
    public AnimationSpinner createSpinner(@NotNull CratesPlugin plugin, @NotNull SpinnerData data, @NotNull InventoryOpening opening) {
        return new AnimationSpinner(data, opening, new ArrayList<>(this.itemMap.values()));
    }

    @NotNull
    public Map<String, WeightedItem<FoliaItem>> getItemMap() {
        return this.itemMap;
    }
}
