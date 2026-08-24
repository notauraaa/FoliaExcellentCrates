package su.nightexpress.excellentcrates.editor.crate;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.config.Lang;
import su.nightexpress.excellentcrates.crate.CrateManager;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.crate.CrateDialogs;
import su.nightexpress.excellentcrates.dialog.DialogRegistry;
import com.notauraaa.folianightcore.locale.LangContainer;
import com.notauraaa.folianightcore.locale.LangEntry;
import com.notauraaa.folianightcore.locale.entry.IconLocale;
import com.notauraaa.folianightcore.ui.menu.MenuViewer;
import com.notauraaa.folianightcore.ui.menu.data.Filled;
import com.notauraaa.folianightcore.ui.menu.data.MenuFiller;
import com.notauraaa.folianightcore.ui.menu.item.MenuItem;
import com.notauraaa.folianightcore.ui.menu.type.LinkedMenu;
import com.notauraaa.folianightcore.util.bukkit.FoliaItem;

import java.util.Comparator;
import java.util.stream.IntStream;

import static su.nightexpress.excellentcrates.Placeholders.*;
import static com.notauraaa.folianightcore.util.text.night.wrapper.TagWrappers.GREEN;

public class CrateListMenu extends LinkedMenu<CratesPlugin, CrateManager> implements Filled<Crate>, LangContainer {

    private static final IconLocale LOCALE_CRATE = LangEntry.iconBuilder("Editor.Button.Crates.Crate")
        .rawName(CRATE_NAME)
        .appendCurrent("Status", GENERIC_INSPECTION)
        .appendCurrent("ID", CRATE_ID).br()
        .appendClick("Click to open")
        .build();

    private static final IconLocale LOCALE_CREATION = LangEntry.iconBuilder("Editor.Button.Crates.Create")
        .accentColor(GREEN)
        .name("New Crate")
        .appendInfo("Use this button to create", "brand new crates!").br()
        .appendClick("Click to create")
        .build();

    private final DialogRegistry dialogs;

    public CrateListMenu(@NotNull CratesPlugin plugin, @NotNull DialogRegistry dialogs) {
        super(plugin, MenuType.GENERIC_9X5, Lang.EDITOR_TITLE_CRATE_LIST.text());
        this.dialogs = dialogs;
        this.plugin.injectLang(this);

        this.addItem(MenuItem.buildReturn(this, 40, (viewer, event) -> {
            this.runNextTick(() -> this.plugin.getEditorManager().openEditor(viewer.getPlayer()));
        }));

        this.addItem(MenuItem.buildNextPage(this, 44));
        this.addItem(MenuItem.buildPreviousPage(this, 36));
        this.addItem(MenuItem.background(Material.BLACK_STAINED_GLASS_PANE, IntStream.range(36, 45).toArray()));
        this.addItem(MenuItem.background(Material.GRAY_STAINED_GLASS_PANE, IntStream.range(0, 36).toArray()));

        this.addItem(Material.ANVIL, LOCALE_CREATION, 42, (viewer, event, manager) -> {
            Player player = viewer.getPlayer();
            this.dialogs.show(player, CrateDialogs.CRATE_CREATION, manager, () -> this.flush(player));
        });
    }

    @Override
    @NotNull
    public MenuFiller<Crate> createFiller(@NotNull MenuViewer viewer) {
        var autoFill = MenuFiller.builder(this);

        autoFill.setSlots(IntStream.range(0, 36).toArray());
        autoFill.setItems(this.getLink(viewer).getCrates().stream().sorted(Comparator.comparing(Crate::getId)).toList());
        autoFill.setItemCreator(crate -> {
            return FoliaItem.fromItemStack(crate.getRawItemStack())
                .localized(LOCALE_CRATE)
                .replacement(replacer -> replacer
                    .replace(GENERIC_INSPECTION, () -> Lang.inspection(Lang.INSPECTIONS_GENERIC_OVERVIEW, !crate.hasProblems()))
                    .replace(crate.replacePlaceholders())
                );
        });
        autoFill.setItemClick(crate -> (viewer1, event) -> {
            this.runNextTick(() -> this.plugin.getEditorManager().openOptionsMenu(viewer1.getPlayer(), crate));
        });

        return autoFill.build();
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }
}
