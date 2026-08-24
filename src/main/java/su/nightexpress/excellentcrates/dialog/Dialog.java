package su.nightexpress.excellentcrates.dialog;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.notauraaa.folianightcore.bridge.dialog.wrap.WrappedDialog;
import com.notauraaa.folianightcore.locale.LangContainer;
import com.notauraaa.folianightcore.ui.dialog.Dialogs;
import com.notauraaa.folianightcore.util.text.night.wrapper.TagWrappers;

public abstract class Dialog<T> implements LangContainer {

    @NotNull
    public abstract WrappedDialog create(@NotNull Player player, @NotNull T source);

    public void show(@NotNull Player player, @NotNull T source, @Nullable Runnable callback) {
        Dialogs.showDialog(player, this.create(player, source), callback);
    }

    @NotNull
    protected static String title(@NotNull String prefix, @NotNull String title) {
        return TagWrappers.YELLOW.and(TagWrappers.BOLD).wrap(prefix.toUpperCase()) + TagWrappers.DARK_GRAY.wrap( " » ") + TagWrappers.WHITE.wrap(title);
    }
}
