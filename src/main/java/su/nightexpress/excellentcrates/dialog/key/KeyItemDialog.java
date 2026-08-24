package su.nightexpress.excellentcrates.dialog.key;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.dialog.generic.GenericItemDialog;
import su.nightexpress.excellentcrates.key.CrateKey;
import com.notauraaa.folianightcore.bridge.item.AdaptedItem;
import com.notauraaa.folianightcore.locale.LangEntry;
import com.notauraaa.folianightcore.locale.entry.TextLocale;

import java.util.List;

public class KeyItemDialog extends GenericItemDialog<CrateKey> {

    private static final TextLocale TITLE = LangEntry.builder("Dialog.Key.Item.Title").text(title("Key", "Item"));

    @Override
    @NotNull
    protected TextLocale title() {
        return TITLE;
    }

    @Override
    protected void setName(@NotNull CrateKey source, @NotNull String name) {
        source.setName(name);
    }

    @Override
    protected void setDescription(@NotNull CrateKey source, @NotNull List<String> description) {
        // TODO
    }

    @Override
    protected void setItem(@NotNull CrateKey source, @NotNull AdaptedItem item) {
        source.setItem(item);
        source.markDirty();
    }
}
