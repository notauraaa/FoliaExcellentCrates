package su.nightexpress.excellentcrates.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.notauraaa.folianightcore.util.text.night.FoliaMessage;

import java.util.List;

public class TextHelper {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    @NotNull
    public static Component parseComponent(@NotNull String text) {
        return LEGACY.deserialize(FoliaMessage.asLegacy(text));
    }

    public static void setDisplayName(@NotNull ItemMeta meta, @Nullable String text) {
        if (text == null) {
            meta.displayName(null);
            return;
        }
        meta.displayName(parseComponent(text));
    }

    public static void setLore(@NotNull ItemMeta meta, @Nullable List<String> lines) {
        if (lines == null) {
            meta.lore(null);
            return;
        }
        meta.lore(lines.stream().map(TextHelper::parseComponent).toList());
    }

    public static void setCustomName(@NotNull Entity entity, @Nullable String text) {
        if (text == null) {
            entity.customName(null);
            return;
        }
        entity.customName(parseComponent(text));
        entity.setCustomNameVisible(true);
    }
}
