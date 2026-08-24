package su.nightexpress.excellentcrates.dialog.reward;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.api.crate.Reward;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.dialog.Dialog;
import com.notauraaa.folianightcore.bridge.common.FoliaNbtHolder;
import com.notauraaa.folianightcore.bridge.dialog.wrap.WrappedDialog;
import com.notauraaa.folianightcore.bridge.dialog.wrap.button.WrappedActionButton;
import com.notauraaa.folianightcore.locale.LangEntry;
import com.notauraaa.folianightcore.locale.entry.ButtonLocale;
import com.notauraaa.folianightcore.locale.entry.DialogElementLocale;
import com.notauraaa.folianightcore.locale.entry.EnumLocale;
import com.notauraaa.folianightcore.locale.entry.TextLocale;
import com.notauraaa.folianightcore.ui.dialog.Dialogs;
import com.notauraaa.folianightcore.ui.dialog.build.*;
import com.notauraaa.folianightcore.util.BukkitThing;
import com.notauraaa.folianightcore.util.Enums;
import com.notauraaa.folianightcore.util.LowerCase;
import com.notauraaa.folianightcore.util.text.night.FoliaMessage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.notauraaa.folianightcore.util.text.night.wrapper.TagWrappers.*;

public class RewardSortingDialog extends Dialog<Crate> {

    private static final EnumLocale<SortMode> SORT_MODE_LOCALE = LangEntry.builder("Dialog.Rewards.Sorting.Mode").enumeration(SortMode.class);

    private static final TextLocale TITLE = LangEntry.builder("Dialog.Rewards.Sorting.Title").text(title("Rewards", "Sorting"));

    private static final DialogElementLocale BODY = LangEntry.builder("Dialog.Rewards.Sorting.Body").dialogElement(400,
        "Please select a sorting mode."
    );

    public static final TextLocale INPUT_REVERSED = LangEntry.builder("Dialog.Rewards.Sorting.Input.Reversed").text("Reversed");

    private static final ButtonLocale BUTTON_MODE = LangEntry.builder("Dialog.Rewards.Sorting.Button.Mode")
        .button(SOFT_YELLOW.wrap("→") + " Mode: " + SOFT_YELLOW.wrap("%s"));

    private static final String JSON_REVERSED = "reversed";
    private static final String JSON_MODE     = "mode";

    @Override
    @NotNull
    public WrappedDialog create(@NotNull Player player, @NotNull Crate crate) {
        List<WrappedActionButton> buttons = new ArrayList<>();

        for (SortMode mode : SortMode.values()) {
            buttons.add(
                DialogButtons.action(BUTTON_MODE.replace(str -> str.formatted(SORT_MODE_LOCALE.getLocalized(mode))))
                    .action(DialogActions.customClick(DialogActions.OK, FoliaNbtHolder.builder().put(JSON_MODE, LowerCase.INTERNAL.apply(mode.name())).build()))
                    .build()
            );
        }

        return Dialogs.create(builder -> {
            builder.base(DialogBases.builder(TITLE)
                .inputs(DialogInputs.bool(JSON_REVERSED, INPUT_REVERSED).initial(false).build())
                .body(DialogBodies.plainMessage(BODY))
                .build()
            );

            builder.type(DialogTypes.multiAction(buttons).exitAction(DialogButtons.back()).build());

            builder.handleResponse(DialogActions.OK, (viewer, identifier, nbtHolder) -> {
                if (nbtHolder == null) return;

                String raw = nbtHolder.getText(JSON_MODE).orElse(null);
                if (raw == null) return;

                SortMode mode = Enums.get(raw, SortMode.class);
                if (mode == null) return;

                boolean reversed = nbtHolder.getBoolean(JSON_REVERSED, false);
                Comparator<Reward> comparator = mode.getComparator(reversed);

                crate.setRewards(crate.getRewards().stream().sorted(comparator).toList());
                crate.markDirty();
                viewer.closeFully();
            });
        });
    }

    private enum SortMode {

        WEIGHT(Comparator.comparingDouble(Reward::getWeight)),
        RARITY(Comparator.comparingDouble((Reward reward) -> reward.getRarity().getWeight())),
        CHANCE(Comparator.comparingDouble(Reward::getRollChance)),
        NAME(Comparator.comparing(reward -> FoliaMessage.stripTags(reward.getName()))),
        ITEM(Comparator.comparing(reward -> BukkitThing.getValue(reward.getPreviewItem().getType())));

        private final Comparator<Reward> comparator;

        SortMode(@NotNull Comparator<Reward> comparator) {
            this.comparator = comparator;
        }

        @NotNull
        public Comparator<Reward> getComparator(boolean reversed) {
            return reversed ? this.comparator.reversed() : this.comparator;
        }
    }
}
