package su.nightexpress.excellentcrates.util;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.config.Config;
import su.nightexpress.excellentcrates.config.Keys;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import com.notauraaa.folianightcore.util.*;
import com.notauraaa.folianightcore.util.bukkit.FoliaItem;
import com.notauraaa.folianightcore.util.text.night.FoliaMessage;
import com.notauraaa.folianightcore.util.text.night.wrapper.TagWrappers;
import com.notauraaa.folianightcore.util.wrapper.UniParticle;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class CrateUtils {

    public static final int REWARD_ITEMS_LIMIT = 27;

    @NotNull
    public static Set<Player> getPlayersForEffects(@NotNull Location location) {
        Set<Player> players = new HashSet<>(Bukkit.getServer().getOnlinePlayers());
        players.removeIf(player -> !isInEffectRange(player, location));

        return players;
    }

    public static boolean isInEffectRange(@NotNull Player player, @NotNull Location location) {
        World world = location.getWorld();
        int distance = Config.CRATE_EFFECTS_VISIBILITY_DISTANCE.get();

        return player.getWorld() == world && player.getLocation().distance(location) <= distance;
    }

    @NotNull
    public static ItemStack removeCrateTags(@NotNull ItemStack itemStack) {
        ItemUtil.editMeta(itemStack, meta -> {
            PDCUtil.remove(meta, Keys.crateId);
            PDCUtil.remove(meta, Keys.keyId);
        });
        return itemStack;
    }

    @NotNull
    public static ItemStack getQuestionStack() {
        return FoliaItem.asCustomHead("2705fd94a0c431927fb4e639b0fcfb49717e412285a02b439e0112da22b2e2ec").hideAllComponents().getItemStack();
    }

    @NotNull
    public static FoliaItem getDefaultLinkTool() {
        ItemStack item = FoliaItem.fromType(Material.BLAZE_ROD)
            .hideAllComponents()
            .getItemStack();
        ItemUtil.editMeta(item, meta -> {
            TextHelper.setDisplayName(meta, TagWrappers.GOLD.and(TagWrappers.BOLD).wrap("Link Tool"));
            TextHelper.setLore(meta, Lists.newList(
                TagWrappers.GRAY.wrap("Click a block to link it"),
                TagWrappers.GRAY.wrap("with the crate!")
            ));
        });
        return FoliaItem.fromItemStack(item);
    }

    @NotNull
    public static ItemStack getDefaultItem(@NotNull Crate crate) {
        ItemStack item = FoliaItem.fromType(Material.CHEST)
            .hideAllComponents()
            .getItemStack();
        ItemUtil.editMeta(item, meta -> {
            TextHelper.setDisplayName(meta, crate.getName());
            TextHelper.setLore(meta, crate.getDescription());
        });
        return item;
    }

    @NotNull
    @Deprecated
    public static String createID(@NotNull String name) {
        String id = StringUtil.transformForID(name);
        if (id.isBlank()) id = UUID.randomUUID().toString().substring(0, 8);

        return id;
    }

    @NotNull
    public static String generateRewardID(@NotNull Crate crate, @NotNull ItemStack itemStack) {
        String itemName = Optional.ofNullable(ItemUtil.getDisplayNameSerialized(itemStack))
            .map(FoliaMessage::stripTags)
            .orElse(BukkitThing.getValue(itemStack.getType()));

        String name = Strings.varStyle(itemName).orElse(UUID.randomUUID().toString());

        int count = 0;
        while (crate.getReward(addCount(name, count)) != null) {
            count++;
        }

        return addCount(name, count);
    }

    public static boolean isValidCommand(@NotNull String command) {
        String firstPart = command.split(" ")[0];

        int index = firstPart.indexOf(':');
        String name = index >= 0 ? firstPart.substring(index + 1) : firstPart;

        return CommandUtil.getCommand(name).isPresent();
    }

    private static String addCount(@NotNull String str, int count) {
        return count <= 0 ? str : str + "_" + count;
    }

    public static boolean isSupportedParticle(@NotNull Particle particle) {
        return particle != Particle.VIBRATION && particle != Particle.DUST_COLOR_TRANSITION && particle != Particle.TRAIL;
    }

    public static boolean isSupportedParticleData(@NotNull UniParticle particle) {
        return particle.getParticle() != null && isSupportedParticleData(particle.getParticle().getDataType());
    }

    public static boolean isSupportedParticleData(@NotNull Class<?> clazz) {
        return clazz != Void.class && clazz != Vibration.class && clazz != Particle.DustTransition.class && clazz != Particle.Trail.class;
    }
}
