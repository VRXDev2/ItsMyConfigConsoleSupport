package me.vrxdev.itsMyConfigConsoleSupport;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.placeholder.Placeholder;
import to.itsme.itsmyconfig.placeholder.PlaceholderDependancy;
import to.itsme.itsmyconfig.placeholder.PlaceholderManager;

import java.util.HashMap;
import java.util.Map;

public final class ItsMyConfigConsoleSupport extends JavaPlugin {
    public static Map<String, String> cachedPlaceholders = new HashMap<>();

    @Override
    public void onEnable() {
        PlaceholderManager placeholderManager = ItsMyConfig.getInstance().getPlaceholderManager();
        IMCFilterInstaller.install(placeholderManager);

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            cachedPlaceholders.clear();
            for (var entry : placeholderManager.getPlaceholdersMap().entrySet()) {
                Placeholder ph = entry.getValue();
                if (!ph.hasDependency(PlaceholderDependancy.NONE)) continue;
                cachedPlaceholders.put("<p:" + entry.getKey() + ">", ph.getResult((OfflinePlayer)null, new String[0]));
            }
        }, 0, 1200);
    }

    @Override
    public void onDisable() {
        // this is done only to add proper reload safety
        IMCFilterInstaller.uninstall();
    }
}
