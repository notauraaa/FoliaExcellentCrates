package su.nightexpress.excellentcrates.dialog.cost;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.crate.cost.Cost;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.dialog.generic.GenericCreationDialog;
import su.nightexpress.excellentcrates.util.ItemHelper;
import com.notauraaa.folianightcore.locale.LangEntry;
import com.notauraaa.folianightcore.locale.entry.TextLocale;
import com.notauraaa.folianightcore.util.StringUtil;

import java.util.ArrayList;

public class CostCreationDialog extends GenericCreationDialog<Crate> {

    private static final TextLocale TITLE = LangEntry.builder("Dialog.CostOption.Creation.Title").text(title("Cost Option", "Creation"));

    @Override
    @NotNull
    protected TextLocale title() {
        return TITLE;
    }

    @Override
    protected boolean canCreate(@NotNull Crate crate, @NotNull String id) {
        return !crate.hasCost(id);
    }

    @Override
    protected void create(@NotNull Crate crate, @NotNull String id) {
        Cost cost = new Cost(id, true, StringUtil.capitalizeUnderscored(id), ItemHelper.vanilla(new ItemStack(Material.TRIAL_KEY)), new ArrayList<>());
        crate.addCost(cost);
        crate.markDirty();
    }
}
