package su.nightexpress.excellentcrates.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class FoliaScheduler {

    private static final boolean IS_FOLIA = isFoliaServer();

    private FoliaScheduler() {}

    private static boolean isFoliaServer() {
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.FoliaScheduler");
            return Bukkit.getServer().getClass().getSimpleName().equals("FoliaServer") 
                || Bukkit.getScheduler().getClass().getSimpleName().equals("FoliaScheduler");
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    public static void runTask(@NotNull Plugin plugin, @NotNull Runnable task) {
        if (IS_FOLIA) {
            Bukkit.getGlobalRegionScheduler().run(plugin, task);
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public static void runTaskLater(@NotNull Plugin plugin, @NotNull Runnable task, long delayTicks) {
        if (IS_FOLIA) {
            Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task, delayTicks);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    public static void runTaskTimer(@NotNull Plugin plugin, @NotNull Runnable task, long delayTicks, long periodTicks) {
        if (IS_FOLIA) {
            Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, task, delayTicks, periodTicks);
        } else {
            Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
        }
    }

    public static void runTaskAsync(@NotNull Plugin plugin, @NotNull Runnable task) {
        if (IS_FOLIA) {
            Bukkit.getAsyncScheduler().runNow(plugin, task);
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }

    public static void runTaskLaterAsync(@NotNull Plugin plugin, @NotNull Runnable task, long delayTicks) {
        if (IS_FOLIA) {
            Bukkit.getAsyncScheduler().runDelayed(plugin, task, delayTicks);
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delayTicks);
        }
    }

    public static void runAtLocation(@NotNull Plugin plugin, @NotNull Location location, @NotNull Runnable task) {
        if (IS_FOLIA) {
            Bukkit.getRegionScheduler().run(plugin, location, task);
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public static void runDelayedAtLocation(@NotNull Plugin plugin, @NotNull Location location, @NotNull Runnable task, long delayTicks) {
        if (IS_FOLIA) {
            Bukkit.getRegionScheduler().runDelayed(plugin, location, task, delayTicks);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    public static void runAtFixedRateAtLocation(@NotNull Plugin plugin, @NotNull Location location, @NotNull Runnable task, long delayTicks, long periodTicks) {
        if (IS_FOLIA) {
            Bukkit.getRegionScheduler().runAtFixedRate(plugin, location, task, delayTicks, periodTicks);
        } else {
            Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
        }
    }

    public static void runAtEntity(@NotNull Plugin plugin, @NotNull Player player, @NotNull Runnable task) {
        if (IS_FOLIA) {
            Bukkit.getRegionScheduler().run(plugin, player, task);
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public static boolean isFolia() {
        return IS_FOLIA;
    }
}