package su.nightexpress.excellentcrates.util;

import com.notauraaa.folianightcore.util.ItemUtil;
import com.notauraaa.folianightcore.util.bukkit.FoliaItem;
import com.notauraaa.folianightcore.util.bukkit.FoliaMeta;
import com.notauraaa.folianightcore.util.placeholder.PlaceholderContext;
import com.notauraaa.folianightcore.util.placeholder.Replacer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SafeFoliaItem extends FoliaItem {

    public SafeFoliaItem(@NotNull Material material) {
        super(material);
    }

    public SafeFoliaItem(@NotNull Material material, int amount) {
        super(material, amount);
    }

    public SafeFoliaItem(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    @NotNull
    public static SafeFoliaItem fromType(@NotNull Material material) {
        return new SafeFoliaItem(material);
    }

    @NotNull
    public static SafeFoliaItem fromItemStack(@NotNull ItemStack itemStack) {
        return new SafeFoliaItem(itemStack);
    }

    @NotNull
    public static SafeFoliaItem asCustomHead(@NotNull String skinURL) {
        SafeFoliaItem item = new SafeFoliaItem(Material.PLAYER_HEAD);
        item.setSkinURL(skinURL);
        return item;
    }

    @NotNull
    @Override
    public ItemStack getItemStack() {
        FoliaMeta meta = getMeta();

        String rawName = meta.getDisplayName();
        List<String> rawLore = meta.getLore();
        Replacer replacer = meta.getReplacer();
        PlaceholderContext ctx = meta.getPlaceholderContext();

        meta.setDisplayName(null);
        meta.setLore(null);

        ItemStack stack = super.getItemStack();

        meta.setDisplayName(rawName);
        meta.setLore(rawLore);

        if (rawName != null) {
            String resolved = resolveString(rawName, replacer, ctx);
            ItemUtil.editMeta(stack, itemMeta -> TextHelper.setDisplayName(itemMeta, resolved));
        }

        if (rawLore != null) {
            List<String> resolved = resolveList(rawLore, replacer, ctx);
            ItemUtil.editMeta(stack, itemMeta -> TextHelper.setLore(itemMeta, addEmptyLines(resolved)));
        }

        return stack;
    }

    @NotNull
    private String resolveString(@NotNull String text, @Nullable Replacer replacer, @Nullable PlaceholderContext ctx) {
        if (ctx != null) return ctx.apply(text);
        if (replacer != null) return replacer.apply(text);
        return text;
    }

    @NotNull
    private List<String> resolveList(@NotNull List<String> list, @Nullable Replacer replacer, @Nullable PlaceholderContext ctx) {
        if (ctx != null) return ctx.apply(list);
        if (replacer != null) return replacer.apply(list);
        return new ArrayList<>(list);
    }

    @NotNull
    private List<String> addEmptyLines(@NotNull List<String> lore) {
        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            if (line.equalsIgnoreCase("%empty-if-above%")) {
                if (i == 0 || isEmpty(lore.get(i - 1))) {
                    lore.remove(i);
                } else {
                    lore.set(i, "");
                }
                return addEmptyLines(lore);
            }
            if (line.equalsIgnoreCase("%empty-if-below%")) {
                if (i == lore.size() - 1 || isEmpty(lore.get(i + 1))) {
                    lore.remove(i);
                } else {
                    lore.set(i, "");
                }
                return addEmptyLines(lore);
            }
        }
        return lore;
    }

    private boolean isEmpty(@NotNull String line) {
        return line.isBlank() || line.equalsIgnoreCase("%empty-if-above%") || line.equalsIgnoreCase("%empty-if-below%");
    }
}
