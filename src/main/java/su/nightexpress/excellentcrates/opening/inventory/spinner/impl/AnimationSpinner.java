package su.nightexpress.excellentcrates.opening.inventory.spinner.impl;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.opening.inventory.InventoryOpening;
import su.nightexpress.excellentcrates.opening.inventory.spinner.AbstractSpinner;
import su.nightexpress.excellentcrates.opening.inventory.spinner.SpinnerData;
import com.notauraaa.folianightcore.util.bukkit.FoliaItem;
import com.notauraaa.folianightcore.util.random.Rnd;
import com.notauraaa.folianightcore.util.random.WeightedItem;

import java.util.ArrayList;
import java.util.List;

public class AnimationSpinner extends AbstractSpinner {

    private final List<WeightedItem<FoliaItem>> items;

    public AnimationSpinner(@NotNull SpinnerData data, @NotNull InventoryOpening opening, @NotNull List<WeightedItem<FoliaItem>> items) {
        super(data, opening);
        this.items = items;
    }

    @Override
    protected void onStop() {

    }

    @Override
    @NotNull
    public ItemStack createItem(int slot) {
        return this.items.isEmpty() ? new ItemStack(Material.AIR) : Rnd.getByWeight(new ArrayList<>(this.items)).getItemStack();
    }
}
