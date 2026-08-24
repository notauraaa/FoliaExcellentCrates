package su.nightexpress.excellentcrates.opening.selectable;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.api.crate.Reward;
import com.notauraaa.folianightcore.bridge.wrap.FoliaSound;
import com.notauraaa.folianightcore.config.ConfigValue;
import com.notauraaa.folianightcore.config.FileConfig;
import com.notauraaa.folianightcore.ui.menu.MenuViewer;
import com.notauraaa.folianightcore.ui.menu.data.ConfigBased;
import com.notauraaa.folianightcore.ui.menu.data.Filled;
import com.notauraaa.folianightcore.ui.menu.data.MenuFiller;
import com.notauraaa.folianightcore.ui.menu.data.MenuLoader;
import com.notauraaa.folianightcore.ui.menu.item.ItemHandler;
import com.notauraaa.folianightcore.ui.menu.item.ItemOptions;
import com.notauraaa.folianightcore.ui.menu.item.MenuItem;
import com.notauraaa.folianightcore.ui.menu.type.LinkedMenu;
import com.notauraaa.folianightcore.util.Lists;
import com.notauraaa.folianightcore.util.bukkit.FoliaItem;
import com.notauraaa.folianightcore.util.placeholder.Replacer;
import com.notauraaa.folianightcore.util.sound.VanillaSound;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static su.nightexpress.excellentcrates.Placeholders.*;
import static com.notauraaa.folianightcore.util.text.night.wrapper.TagWrappers.*;

public class SelectableMenu extends LinkedMenu<CratesPlugin, SelectableOpening> implements ConfigBased, Filled<Reward> {

    private int[]        rewardSlots;
    private String       rewardName;
    private List<String> rewardLore;

    private String rewardEntry;

    private FoliaItem  selectedIcon;

    private FoliaSound selectSound;
    private FoliaSound unselectSound;
    private FoliaSound limitSound;
    private FoliaSound confirmSound;

    public SelectableMenu(@NotNull CratesPlugin plugin) {
        super(plugin, MenuType.GENERIC_9X6, BLACK.wrap("Select " + GENERIC_AMOUNT + " reward(s)!"));
    }

    @Override
    @NotNull
    public MenuFiller<Reward> createFiller(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();
        SelectableOpening opening = this.getLink(player);
        //Crate crate = opening.getCrate();

        return MenuFiller.builder(this)
            .setSlots(this.rewardSlots)
            .setItems(opening.getCrateRewards())
            .setItemCreator(reward -> {
                FoliaItem item = opening.isSelectedReward(reward) ? this.selectedIcon.copy() : FoliaItem.fromItemStack(reward.getPreviewItem())
                    .setDisplayName(this.rewardName)
                    .setLore(this.rewardLore);

                return item.replacement(replacer -> replacer.replace(reward.replacePlaceholders()));
            })
            .setItemClick(reward -> (viewer1, event) -> {
                if (opening.isSelectedReward(reward)) {
                    opening.removeSelectedReward(reward);
                    this.unselectSound.play(player);
                }
                else {
                    if (opening.isAllRewardsSelected()) {
                        this.limitSound.play(player);
                        return;
                    }

                    opening.addSelectedReward(reward);
                    this.selectSound.play(player);
                }
                this.runNextTick(() -> this.flush(viewer));
            })
            .build();
    }

    @Override
    @NotNull
    protected String getTitle(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();
        SelectableOpening opening = this.getLink(player);

        return Replacer.create()
            .replace(GENERIC_AMOUNT, () -> String.valueOf(opening.getRequiredAmount()))
            .replace(GENERIC_CURRENT, () -> String.valueOf(opening.getSelectedAmount()))
            .apply(super.getTitle(viewer));
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    protected void onItemPrepare(@NotNull MenuViewer viewer, @NotNull MenuItem menuItem, @NotNull FoliaItem item) {
        super.onItemPrepare(viewer, menuItem, item);

        if (!viewer.hasItem(menuItem)) {
            SelectableOpening opening = this.getLink(viewer);

            item.replacement(replacer -> replacer
                .replace(GENERIC_AMOUNT, () -> String.valueOf(opening.getRequiredAmount()))
                .replace(GENERIC_CURRENT, () -> String.valueOf(opening.getSelectedAmount()))
                .replace(GENERIC_REWARDS, () -> opening.getSelectedRewards().stream()
                    .sorted(Comparator.comparing(Reward::getId))
                    .map(reward -> Replacer.create().replace(reward.replacePlaceholders()).apply(this.rewardEntry))
                    .collect(Collectors.joining(BR))
                )
            );
        }
    }

    @Override
    protected void onClose(@NotNull MenuViewer viewer) {
        SelectableOpening opening = this.getLink(viewer);
        boolean hasAnchor = this.cache.hasAnchor(viewer.getPlayer());

        super.onClose(viewer);

        if (!hasAnchor && !opening.isCompleted()) {
            opening.stop();
        }
    }

    private void handleConfirm(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();
        SelectableOpening opening = this.getLink(player);

        opening.confirm();
        this.confirmSound.play(player);
    }

    @Override
    public void loadConfiguration(@NotNull FileConfig config, @NotNull MenuLoader loader) {
        this.rewardSlots = ConfigValue.create("Reward.Slots",
            new int[] {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34}
        ).read(config);

        this.rewardName = ConfigValue.create("Reward.Name",
            REWARD_NAME
        ).read(config);

        this.rewardLore = ConfigValue.create("Reward.Lore", Lists.newList(
            REWARD_DESCRIPTION,
            EMPTY_IF_ABOVE,
            YELLOW.wrap("→ " + UNDERLINED.wrap("Click to select"))
        )).read(config);

        this.rewardEntry = ConfigValue.create("Reward.EntryName",
            GRAY.wrap("- " + REWARD_NAME)
        ).read(config);

        this.selectedIcon = ConfigValue.create("Selection.Icon",
            FoliaItem.fromType(Material.LIME_STAINED_GLASS_PANE)
                .setDisplayName(GREEN.wrap(BOLD.wrap("Selected: ")) + WHITE.wrap(REWARD_NAME))
                .setLore(Lists.newList(
                    GRAY.wrap("You'll get this reward."),
                    "",
                    GREEN.wrap("→ " + UNDERLINED.wrap("Click to unselect"))
                ))
                .hideAllComponents()
        ).read(config);

        this.selectSound = ConfigValue.create("Selection.Sound-V", VanillaSound.of(Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE)).read(config);
        this.unselectSound = ConfigValue.create("Selection.Sound-X", VanillaSound.of(Sound.BLOCK_GLASS_BREAK)).read(config);
        this.limitSound = ConfigValue.create("Selection.Sound-Limit", VanillaSound.of(Sound.ENTITY_VILLAGER_NO)).read(config);
        this.confirmSound = ConfigValue.create("Selection.Sound-Confirm", VanillaSound.of(Sound.BLOCK_VAULT_OPEN_SHUTTER)).read(config);

        loader.addDefaultItem(new FoliaItem(Material.BLACK_STAINED_GLASS_PANE)
            .setHideTooltip(true)
            .toMenuItem()
            .setSlots(IntStream.range(45, 54).toArray())
        );

        loader.addDefaultItem(MenuItem.buildNextPage(this, 53).setPriority(10));
        loader.addDefaultItem(MenuItem.buildPreviousPage(this, 45).setPriority(10));

        loader.addDefaultItem(FoliaItem.fromType(Material.LIME_DYE)
            .setDisplayName(GREEN.wrap(BOLD.wrap("Confirm")))
            .setLore(Lists.newList(
                GRAY.wrap("You'll get the following rewards:"),
                GENERIC_REWARDS,
                EMPTY_IF_ABOVE,
                GREEN.wrap("→ " + UNDERLINED.wrap("Click to confirm"))
            ))
            .hideAllComponents()
            .toMenuItem()
            .setSlots(49)
            .setPriority(10)
            .setHandler(new ItemHandler("confirm", (viewer, event) -> this.handleConfirm(viewer),
                ItemOptions.builder()
                    .setVisibilityPolicy(viewer -> this.getLink(viewer).isAllRewardsSelected())
                    .build()
            ))
        );

        loader.addDefaultItem(FoliaItem.fromType(Material.GRAY_DYE)
            .setDisplayName(WHITE.wrap(BOLD.wrap("Confirm")) + " " + GRAY.wrap("(Not Enough)"))
            .setLore(Lists.newList(
                GRAY.wrap("You selected " + WHITE.wrap(GENERIC_CURRENT) + "/" + WHITE.wrap(GENERIC_AMOUNT) + " rewards."),
                "",
                WHITE.wrap("→ " + UNDERLINED.wrap("Click to exit"))
            ))
            .hideAllComponents()
            .toMenuItem()
            .setSlots(49)
            .setPriority(1)
            .setHandler(ItemHandler.forClose(this))
        );
    }
}
