package su.nightexpress.excellentcrates.dialog.cost;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.crate.cost.Cost;
import su.nightexpress.excellentcrates.dialog.Dialog;
import com.notauraaa.folianightcore.bridge.dialog.wrap.WrappedDialog;
import com.notauraaa.folianightcore.locale.LangEntry;
import com.notauraaa.folianightcore.locale.entry.DialogElementLocale;
import com.notauraaa.folianightcore.locale.entry.TextLocale;
import com.notauraaa.folianightcore.ui.dialog.Dialogs;
import com.notauraaa.folianightcore.ui.dialog.build.*;

import static com.notauraaa.folianightcore.util.text.night.wrapper.TagWrappers.*;

public class CostNameDialog extends Dialog<Cost> {

    private static final String INPUT_NAME = "name";

    private static final TextLocale TITLE = LangEntry.builder("Dialog.CostOption.Name.Title").text(title("Cost Option", "Name"));

    private static final DialogElementLocale BODY = LangEntry.builder("Dialog.CostOption.Name.Body").dialogElement(400,
        "Sets the " + SOFT_YELLOW.wrap("display name") + " for the selected cost option.",
        "It's best to choose a collective name, such as " + SOFT_YELLOW.wrap("\"Keys\"") + " or " + SOFT_YELLOW.wrap("\"Coins\"") + "."
    );

    @Override
    @NotNull
    public WrappedDialog create(@NotNull Player player, @NotNull Cost cost) {
        return Dialogs.create(builder -> {
            builder.base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage(BODY))
                .inputs(DialogInputs.text(INPUT_NAME, "").initial(cost.getName()).labelVisible(false).build())
                .build()
            );

            builder.type(DialogTypes.multiAction(DialogButtons.ok())
                .exitAction(DialogButtons.back())
                .build()
            );

            builder.handleResponse(DialogActions.OK, (user, identifier, nbtHolder) -> {
                if (nbtHolder == null) return;

                String name = nbtHolder.getText(INPUT_NAME, cost.getName());
                cost.setName(name);
                user.callback();
            });
        });
    }
}
