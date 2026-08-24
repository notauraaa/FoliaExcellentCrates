package su.nightexpress.excellentcrates.dialog.crate;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.config.Config;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.dialog.Dialog;
import com.notauraaa.folianightcore.bridge.dialog.wrap.WrappedDialog;
import com.notauraaa.folianightcore.bridge.dialog.wrap.input.single.WrappedSingleOptionEntry;
import com.notauraaa.folianightcore.config.FileConfig;
import com.notauraaa.folianightcore.locale.LangEntry;
import com.notauraaa.folianightcore.locale.entry.DialogElementLocale;
import com.notauraaa.folianightcore.locale.entry.TextLocale;
import com.notauraaa.folianightcore.ui.dialog.Dialogs;
import com.notauraaa.folianightcore.ui.dialog.build.*;

import java.util.ArrayList;
import java.util.List;

import static com.notauraaa.folianightcore.util.text.night.wrapper.TagWrappers.*;

public class CratePreviewDialog extends Dialog<Crate> {

    private static final TextLocale TITLE = LangEntry.builder("Dialog.Crate.Preview.Title").text(title("Crate", "Preview Template"));

    private static final DialogElementLocale BODY = LangEntry.builder("Dialog.Crate.Preview.Body").dialogElement(400,
        "Select a preview GUI template for the crate.",
        "You can create and edit previews in the " + SOFT_YELLOW.wrap(Config.DIR_PREVIEWS) + " directory.",
        "",
        SOFT_YELLOW.wrap("→ ") + "To disable crate preview, uncheck the " + SOFT_YELLOW.wrap("Enabled") + " box."
    );

    private static final TextLocale INPUT_ENABLED = LangEntry.builder("Dialog.Crate.Preview.Input.Enabled").text("Enabled");
    private static final TextLocale INPUT_PREVIEW = LangEntry.builder("Dialog.Crate.Preview.Input.Preview").text(SOFT_YELLOW.wrap("Preview"));

    private static final String JSON_ENABLED = "enabled";
    private static final String JSON_ID      = "id";

    private final CratesPlugin plugin;

    public CratePreviewDialog(@NotNull CratesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public WrappedDialog create(@NotNull Player player, @NotNull Crate crate) {
        List<WrappedSingleOptionEntry> entries = new ArrayList<>();

        this.plugin.getCrateManager().getPreviewNames().stream().sorted(String::compareTo).forEach(id -> {
            entries.add(new WrappedSingleOptionEntry(id, FileConfig.withExtension(id), crate.getPreviewId().equalsIgnoreCase(id)));
        });

        return Dialogs.create(builder -> {
            builder.base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage(BODY))
                .inputs(
                    DialogInputs.bool(JSON_ENABLED, INPUT_ENABLED).initial(crate.isPreviewEnabled()).build(),
                    DialogInputs.singleOption(JSON_ID, INPUT_PREVIEW, entries).build()
                )
                .build()
            );

            builder.type(DialogTypes.multiAction(DialogButtons.ok()).exitAction(DialogButtons.back()).build());

            builder.handleResponse(DialogActions.OK, (viewer, identifier, nbtHolder) -> {
                if (nbtHolder == null) return;

                boolean enabled = nbtHolder.getBoolean(JSON_ENABLED, false);
                String id = nbtHolder.getText(JSON_ID, crate.getPreviewId());

                crate.setPreviewEnabled(enabled);
                crate.setPreviewId(id);
                crate.markDirty();
                viewer.callback();
            });
        });
    }
}
