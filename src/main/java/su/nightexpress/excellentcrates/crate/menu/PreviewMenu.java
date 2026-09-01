package su.nightexpress.excellentcrates.crate.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.api.crate.Reward;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.crate.impl.CrateSource;
import su.nightexpress.excellentcrates.util.InteractType;
import su.nightexpress.excellentcrates.util.TextHelper;
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
import com.notauraaa.folianightcore.util.ItemUtil;
import com.notauraaa.folianightcore.util.Lists;
import su.nightexpress.excellentcrates.util.SafeFoliaItem;
import com.notauraaa.folianightcore.util.placeholder.Replacer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static su.nightexpress.excellentcrates.Placeholders.*;
import static com.notauraaa.folianightcore.util.text.night.wrapper.TagWrappers.*;

public class PreviewMenu extends LinkedMenu<CratesPlugin, CrateSource> implements Filled<Reward>, ConfigBased {

    private static final String NO_PERMISSION = "%no_permission%";

    private int[]        rewardSlots;
    private String       rewardName;
    private List<String> rewardLore;
    private List<String> noPermissionLore;
    private List<String> limitsLore;
    private boolean      hideUnavailable;

    public PreviewMenu(@NotNull CratesPlugin plugin, @NotNull FileConfig config) {
        super(plugin, MenuType.GENERIC_9X5, BLACK.wrap(CRATE_NAME));
        this.setApplyPlaceholderAPI(true);
        this.load(config);
    }

    @Override
    @NotNull
    protected String getTitle(@NotNull MenuViewer viewer) {
        CrateSource source = this.getLink(viewer);

        return source.getCrate().replacePlaceholders().apply(this.title);
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    @NotNull
    public MenuFiller<Reward> createFiller(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();
        Crate crate = this.getLink(player).getCrate();

        var autoFill = MenuFiller.builder(this);

        autoFill.setSlots(this.rewardSlots);
        autoFill.setItems((this.hideUnavailable ? crate.getRewards(player) : crate.getRewards()).stream().filter(Reward::isRollable).toList());
        autoFill.setItemCreator(reward -> {
            List<String> restrictions = new ArrayList<>();
            List<String> limits = new ArrayList<>();

            if (reward.fitRequirements(player)) {
                if (reward.getLimits().isEnabled() && reward.getLimits().isAmountLimited()) {
                    limits.addAll(Replacer.create()
                        .replace(GENERIC_AMOUNT, () -> String.valueOf(reward.getAvailableRolls(player)))
                        .apply(this.limitsLore)
                    );
                }
            }
            else {
                restrictions.addAll(this.noPermissionLore);
            }

            Replacer replacer = Replacer.create()
                .replace(GENERIC_LIMITS, limits)
                .replace(NO_PERMISSION, restrictions)
                .replace("%win_limit_amount%", limits)
                .replace("%win_limit_cooldown%", Collections.emptyList())
                .replace("%win_limit_drained%", Collections.emptyList())
                .replace("%win_limit_no_permission%", restrictions)
                .replace(reward.replacePlaceholders())
                .replace(crate.replacePlaceholders());
            if (this.applyPlaceholderAPI) {
                replacer.replacePlaceholderAPI(player);
            }

            org.bukkit.inventory.ItemStack item = SafeFoliaItem.fromItemStack(reward.getPreviewItem()).ignoreNameAndLore().getItemStack();
            ItemUtil.editMeta(item, meta -> {
                TextHelper.setDisplayName(meta, replacer.apply(this.rewardName));
                TextHelper.setLore(meta, replacer.apply(this.rewardLore));
            });
            return SafeFoliaItem.fromItemStack(item);
        });

        return autoFill.build();
    }

    @Override
    public void loadConfiguration(@NotNull FileConfig config, @NotNull MenuLoader loader) {
        this.hideUnavailable = ConfigValue.create("Reward.Hide_Unavailable",
            true,
            "When enabled, displays only rewards that can be rolled out for a player."
        ).read(config);

        this.rewardSlots = ConfigValue.create("Reward.Slots",
            new int[] {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34}
        ).read(config);

        this.rewardName = ConfigValue.create("Reward.Name",
            REWARD_NAME
        ).read(config);

        this.rewardLore = ConfigValue.create("Reward.Lore.Default", Lists.newList(
            NO_PERMISSION,
            EMPTY_IF_ABOVE,
            DARK_GRAY.wrap("»") + GRAY.wrap( " Rarity: " + WHITE.wrap(REWARD_RARITY_NAME) + " → " + GREEN.wrap(REWARD_ROLL_CHANCE + "%")),
            GENERIC_LIMITS,
            EMPTY_IF_BELOW,
            REWARD_DESCRIPTION
        )).read(config);

        this.noPermissionLore = ConfigValue.create("Reward.Lore.No_Permission", Lists.newList(
            GRAY.wrap(RED.wrap("✘") + " You don't have access to this reward.")
        )).read(config);

        this.limitsLore = ConfigValue.create("Reward.Lore.LimitInfo", Lists.newList(
            DARK_GRAY.wrap("»") + GRAY.wrap(" Rolls Available: ") + YELLOW.wrap(GENERIC_AMOUNT)
        )).read(config);

        loader.addHandler(new ItemHandler("open", (viewer, event) -> {
            CrateSource source = this.getLink(viewer);
            if (!source.hasItem() || !source.hasBlock()) return;

            Player player = viewer.getPlayer();

            this.runNextTick(() -> {
                player.closeInventory();
                plugin.getCrateManager().interactCrate(player, source.getCrate(), InteractType.CRATE_OPEN, source.getItem(), source.getBlock());
            });
        }, ItemOptions.builder().setVisibilityPolicy(viewer -> {
            CrateSource source = this.getLink(viewer);
            return source.hasItem() || source.hasBlock();
        }).build()));

        loader.addDefaultItem(new SafeFoliaItem(Material.BLACK_STAINED_GLASS_PANE).setHideTooltip(true).toMenuItem()
            .setSlots(1,2,3,5,6,7,9,18,27,17,26,35,37,38,39,40,41,42,43));

        loader.addDefaultItem(new SafeFoliaItem(Material.GRAY_STAINED_GLASS_PANE).setHideTooltip(true).toMenuItem()
            .setSlots(0,4,8,36,44));

        {
            org.bukkit.inventory.ItemStack milestonesHead = SafeFoliaItem.asCustomHead("1daf09284530ce92ed2df2a62e1b05a11f1871f85ae559042844206d66c0b5b0").getItemStack();
            ItemUtil.editMeta(milestonesHead, meta -> TextHelper.setDisplayName(meta, GOLD.wrap(BOLD.wrap("Milestones"))));
            loader.addDefaultItem(SafeFoliaItem.fromItemStack(milestonesHead).toMenuItem()
                .setPriority(10)
                .setSlots(4)
                .setHandler(new ItemHandler("milestones", (viewer, event) -> {
                    this.runNextTick(() -> plugin.getCrateManager().openMilestones(viewer.getPlayer(), this.getLink(viewer)));
                }, ItemOptions.builder().setVisibilityPolicy(viewer -> this.getLink(viewer).getCrate().hasMilestones()).build()))
            );
        }

        loader.addDefaultItem(MenuItem.buildExit(this, 40).setPriority(10));
        loader.addDefaultItem(MenuItem.buildNextPage(this, 26).setPriority(10));
        loader.addDefaultItem(MenuItem.buildPreviousPage(this, 18).setPriority(10));
    }
}
